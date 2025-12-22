package org.mql.ai.business;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.mql.ai.exceptions.RAGException;
import org.mql.ai.models.ChatRequest;
import org.mql.ai.models.ChatResponse;
import org.mql.ai.models.SourceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RAGServiceDefault implements RagService {
    
    private static final Logger logger = LoggerFactory.getLogger(RAGServiceDefault.class);
    
    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final PDFLoaderService pdfLoaderService;
    
    private final int maxResults;
    private final double minScore;
    
    private final String promptTemplateFr;
    private final String promptTemplateEn;
    private final String noResultsMessageFr;
    private final String noResultsMessageEn;
    private final String errorMessageFr;
    private final String errorMessageEn;

    public RAGServiceDefault(ChatLanguageModel chatModel, 
    		EmbeddingModel embeddingModel, 
    		EmbeddingStore<TextSegment> embeddingStore,
    		PDFLoaderService pdfLoaderService,
            @Value("${rag.retrieval.max-results}") int maxResults,
            @Value("${rag.retrieval.min-score}") double minScore,
            @Value("${rag.prompt.template.fr:Default FR}") String promptTemplateFr,
            @Value("${rag.prompt.template.en:Default EN}") String promptTemplateEn,
            @Value("${rag.message.no-results.fr:Aucune information trouvee}") String noResultsMessageFr,
            @Value("${rag.message.no-results.en:No information found}") String noResultsMessageEn,
            @Value("${rag.message.error.fr:Erreur}") String errorMessageFr,
            @Value("${rag.message.error.en:Error}") String errorMessageEn) {
        
    	this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.pdfLoaderService = pdfLoaderService;
        this.maxResults = maxResults;
        this.minScore = minScore;
        this.promptTemplateFr = promptTemplateFr;
        this.promptTemplateEn = promptTemplateEn;
        this.noResultsMessageFr = noResultsMessageFr;
        this.noResultsMessageEn = noResultsMessageEn;
        this.errorMessageFr = errorMessageFr;
        this.errorMessageEn = errorMessageEn;
        
        logger.info(" RAG Service initialisé");
        logger.info(" Paramètres: maxResults={}, minScore={}", maxResults, minScore);
        logger.info(" Support multilingue: FR, EN");
    }

    @Override
    public ChatResponse processQuestion(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        String language = determineLanguage(request);
        logger.info(" Traitement de la question (langue: {}): {}", language, request.getQuestion());
        
        try {
            // 1. Créer l'embedding de la question
            logger.debug(" Création de l'embedding de la question...");
            Embedding questionEmbedding = embeddingModel.embed(request.getQuestion()).content();
            
            // 2. Rechercher les documents similaires
            logger.debug(" Recherche de similarité dans ChromaDB...");
            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
            
            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
            List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();
            
            logger.info(" {} documents pertinents trouvés", matches.size());
            
            if (matches.isEmpty()) {
                logger.warn(" Aucun document pertinent trouvé");
                String message = language.equals("en") ? noResultsMessageEn : noResultsMessageFr;
                return ChatResponse.error(message);
            }
            
            // 3. Construire le contexte
            String context = buildContext(matches);
            logger.debug(" Contexte construit: {} caractères", context.length());
            
            // 4. Construire le prompt selon la langue
            String prompt = buildPrompt(request.getQuestion(), context, language);
            
            // 5. Générer la réponse avec le LLM
            logger.debug(" Génération de la réponse avec Ollama...");
            String answer = chatModel.generate(prompt);
            
            // 6. Créer les sources DÉDOUBLONNÉES si demandé
            List<SourceInfo> sources = null;
            if (request.isIncludeSources()) {
                sources = buildSourcesDeduplicated(matches);
                logger.debug(" {} sources uniques extraites", sources.size());
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            logger.info(" Réponse générée en {}ms", processingTime);
            
            return ChatResponse.success(answer, sources, processingTime);
            
        } catch (Exception e) {
            logger.error(" Erreur lors du traitement de la question", e);
            String message = language.equals("en") ? errorMessageEn : errorMessageFr;
            return ChatResponse.error(message + " " + e.getMessage());
        }
    }

    @Override
    public int indexAllDocuments() {
        logger.info(" Indexation de tous les documents...");
        try {
            var documents = pdfLoaderService.loadAllPDFs();
            logger.info(" {} documents indexés", documents.size());
            return documents.size();
            
        } catch (Exception e) {
            logger.error(" Erreur lors de l'indexation", e);
            throw new RAGException("Erreur lors de l'indexation des documents", e);
        }
    }

    @Override
    public boolean indexDocument(String fileName) {
        logger.info(" Indexation du document: {}", fileName);
        
        try {
            var documents = pdfLoaderService.loadSpecificPDFs(List.of(fileName));
            return !documents.isEmpty();
            
        } catch (Exception e) {
            logger.error(" Erreur lors de l'indexation de {}", fileName, e);
            return false;
        }
    }

    @Override
    public int getIndexedDocumentsCount() {
    	return pdfLoaderService.getIndexedFilesCount();
    }

    @Override
    public void clearIndex() {
        logger.warn(" Suppression de l'index non implémentée");
        throw new UnsupportedOperationException(
            "La suppression de l'index n'est pas encore implémentée"
        );
    }


    /**
     * Détermine la langue de la question
     */
    private String determineLanguage(ChatRequest request) {
        if (request.getLanguage() != null) {
            return request.getLanguage().toLowerCase();
        }

        String question = request.getQuestion().toLowerCase();
        String[] frenchWords = {"le", "la", "les", "un", "une", "des", "et", "est", "dans", "pour", "que", "qui", "avec", "comment", "pourquoi", "où"};
        String[] englishWords = {"the", "is", "are", "and", "or", "in", "on", "at", "what", "how", "why", "where", "when", "who"};
        
        long frenchCount = 0;
        long englishCount = 0;
        
        for (String word : frenchWords) {
            if (question.contains(" " + word + " ") || question.startsWith(word + " ")) {
                frenchCount++;
            }
        }
        
        for (String word : englishWords) {
            if (question.contains(" " + word + " ") || question.startsWith(word + " ")) {
                englishCount++;
            }
        }
        
        return englishCount > frenchCount ? "en" : "fr";
    }

    /**
     * Construit le contexte à partir des segments trouvés
     */
    private String buildContext(List<EmbeddingMatch<TextSegment>> matches) {
        return matches.stream()
            .map(match -> match.embedded().text())
            .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * Construit le prompt pour le LLM selon la langue
     */
    private String buildPrompt(String question, String context, String language) {
        String template = language.equals("en") ? promptTemplateEn : promptTemplateFr;
        return String.format(template, context, question);
    }

    /**
     * Garde le meilleur score par document
     */
    private List<SourceInfo> buildSourcesDeduplicated(List<EmbeddingMatch<TextSegment>> matches) {
        // Map pour stocker le meilleur match par fichier
        Map<String, EmbeddingMatch<TextSegment>> bestMatchByFile = new LinkedHashMap<>();
        
        for (EmbeddingMatch<TextSegment> match : matches) {
            TextSegment segment = match.embedded();
            String fileName = segment.metadata().getString("file_name");
            
            if (fileName == null) {
                fileName = "Document inconnu";
            }
            
            // Garder le match avec le meilleur score pour ce fichier
            EmbeddingMatch<TextSegment> existingMatch = bestMatchByFile.get(fileName);
            if (existingMatch == null || match.score() > existingMatch.score()) {
                bestMatchByFile.put(fileName, match);
            }
        }
        
        // Convertir en SourceInfo
        List<SourceInfo> sources = new ArrayList<>();
        for (Map.Entry<String, EmbeddingMatch<TextSegment>> entry : bestMatchByFile.entrySet()) {
            String fileName = entry.getKey();
            EmbeddingMatch<TextSegment> match = entry.getValue();
            
            String text = match.embedded().text();
            String excerpt = text.length() > 200 
                ? text.substring(0, 200) + "..." 
                : text;
            
            SourceInfo source = new SourceInfo(fileName, match.score(), excerpt);
            sources.add(source);
        }
        
        // Trier par score décroissant
        sources.sort((s1, s2) -> Double.compare(s2.getRelevanceScore(), s1.getRelevanceScore()));
        
        return sources;
    }
}