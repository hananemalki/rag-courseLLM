package org.mql.ai.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(LangChainConfig.class);
    
    @Value("${ollama.base.url}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.model}")
    private String ollamaModel;
    
    @Value("${ollama.embedding.model}")
    private String ollamaEmbeddingModel;
    
    @Value("${chroma.db.url}")
    private String chromaDbUrl;
    
    @Value("${chroma.db.collection}")
    private String chromaDbCollection;
    
    @Value("${rag.timeout-seconds:120}")
    private int timeoutSeconds;
    
    @Bean
    public ChatLanguageModel chatModel() {
        logger.info(" Configuration du ChatLanguageModel...");
        logger.info(" Ollama URL: {}", ollamaBaseUrl);
        logger.info(" Modèle: {}", ollamaModel);
        
        return OllamaChatModel.builder()
            .baseUrl(ollamaBaseUrl)
            .modelName(ollamaModel)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .build();
    }
    
    @Bean
    public EmbeddingModel embeddingModel() {
        logger.info(" Configuration de l'EmbeddingModel...");
        logger.info(" Ollama URL: {}", ollamaBaseUrl);
        logger.info(" Modèle d'embedding: {}", ollamaEmbeddingModel);
        
        return OllamaEmbeddingModel.builder()
            .baseUrl(ollamaBaseUrl)
            .modelName(ollamaEmbeddingModel)
            .timeout(Duration.ofSeconds(timeoutSeconds))
            .build();
    }
    
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        logger.info(" Configuration de ChromaDB EmbeddingStore...");
        logger.info(" ChromaDB URL: {}", chromaDbUrl);
        logger.info(" Collection: {}", chromaDbCollection);
        
        try {
            logger.info(" Vérification de la connexion à ChromaDB...");
            
            ChromaEmbeddingStore store = ChromaEmbeddingStore.builder()
                .baseUrl(chromaDbUrl)
                .collectionName(chromaDbCollection)
                .timeout(Duration.ofSeconds(30))
                .logRequests(true)  
                .logResponses(true) 
                .build();
            
            logger.info(" ChromaDB connecté avec succès (API v2) !");
            return store;
            
        } catch (Exception e) {
            logger.error(" ERREUR: Impossible de se connecter à ChromaDB", e);
            logger.error(" ChromaDB nécessite l'API v2");
            logger.error(" Vérifiez que ChromaDB est démarré: docker ps | grep chromadb");
            
            if (e.getMessage() != null && e.getMessage().contains("405")) {
                logger.error(" SOLUTION: Utilisez une version compatible de LangChain4j");
                logger.error(" Ou redémarrez ChromaDB avec: docker restart chromadb");
            }
            
            throw new RuntimeException(
                "Impossible de se connecter à ChromaDB sur " + chromaDbUrl + 
                ". ChromaDB utilise l'API v2. Vérifiez la compatibilité de LangChain4j.", 
                e
            );
        }
    }
}