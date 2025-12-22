package org.mql.ai.business;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingServiceDefault implements EmbeddingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingServiceDefault.class);
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingServiceDefault(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    @Override
    public Embedding embedText(String text) {
        logger.debug(" Création d'embedding pour le texte (longueur: {})", text.length());
        
        try {
            Response<Embedding> response = embeddingModel.embed(text);
            Embedding embedding = response.content();
            
            logger.debug(" Embedding créé: dimension {}", embedding.dimension());
            return embedding;
            
        } catch (Exception e) {
            logger.error(" Erreur lors de la création de l'embedding", e);
            throw new RuntimeException("Erreur lors de la création de l'embedding", e);
        }
    }

    @Override
    public List<Embedding> embedSegments(List<TextSegment> segments) {
        logger.info(" Création d'embeddings pour {} segments", segments.size());
        
        try {
            List<Embedding> embeddings = new ArrayList<>();
            
            int batchSize = 10;
            for (int i = 0; i < segments.size(); i += batchSize) {
                int end = Math.min(i + batchSize, segments.size());
                List<TextSegment> batch = segments.subList(i, end);
                Response<List<Embedding>> response = embeddingModel.embedAll(batch);
                embeddings.addAll(response.content());
             
                logger.debug(" Batch {}/{} traité", (i / batchSize) + 1, (segments.size() + batchSize - 1) / batchSize);
            }
            
            logger.info(" {} embeddings créés avec succès", embeddings.size());
            return embeddings;
            
        } catch (Exception e) {
            logger.error(" Erreur lors de la création des embeddings", e);
            throw new RuntimeException("Erreur lors de la création des embeddings", e);
        }
    }

    @Override
    public void storeEmbeddings(List<TextSegment> segments) {
        logger.info(" Stockage de {} segments dans ChromaDB", segments.size());
        
        try {
            List<Embedding> embeddings = embedSegments(segments);
            List<String> ids = embeddingStore.addAll(embeddings, segments);
            logger.info(" {} embeddings stockés avec succès", ids.size());
            
        } catch (Exception e) {
            logger.error(" Erreur lors du stockage des embeddings", e);
            throw new RuntimeException("Erreur lors du stockage des embeddings", e);
        }
    }
}