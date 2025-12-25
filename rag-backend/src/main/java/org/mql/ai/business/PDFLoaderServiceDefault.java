package org.mql.ai.business;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.mql.ai.exceptions.PDFLoadException;
import org.mql.ai.models.DocumentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PDFLoaderServiceDefault implements PDFLoaderService {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFLoaderServiceDefault.class);
    
    private final String coursesDirectory;
    private final int chunkSize;
    private final int chunkOverlap;
    private final EmbeddingService embeddingService;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    private final Set<String> indexedFiles = ConcurrentHashMap.newKeySet();
    private final Map<String, DocumentMetadata> documentsCache = new ConcurrentHashMap<>();
    
    public PDFLoaderServiceDefault(
            @Value("${rag.courses-directory}") String coursesDirectory,
            @Value("${rag.chunk-size}") int chunkSize,
            @Value("${rag.chunk-overlap}") int chunkOverlap,
            EmbeddingService embeddingService,
            EmbeddingStore<TextSegment> embeddingStore) {
        this.coursesDirectory = coursesDirectory;
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
        this.embeddingService = embeddingService;
        this.embeddingStore = embeddingStore;
        
        logger.info(" Répertoire des cours: {}", coursesDirectory);
        logger.info(" Taille des chunks: {} (overlap: {})", chunkSize, chunkOverlap);
    }
    
    
    // Détecte les PDFs et indexe ceux qui ne sont pas dans ChromaDB
    @PostConstruct
    public void initializeIndex() {
        logger.info(" Initialisation de l'index au démarrage...");
        
        try {
            Path coursesPath = Paths.get(coursesDirectory);
            
            if (!Files.exists(coursesPath)) {
                logger.warn(" Répertoire non trouvé, création: {}", coursesDirectory);
                Files.createDirectories(coursesPath);
                logger.info(" Index initialisé (aucun document à traiter)");
                return;
            }
            
            List<Path> pdfFiles = Files.list(coursesPath)
                .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                .collect(Collectors.toList());
            
            logger.info(" {} fichiers PDF détectés dans le répertoire", pdfFiles.size());
            
            if (pdfFiles.isEmpty()) {
                logger.info(" Index initialisé (aucun document à traiter)");
                return;
            }
            
            int alreadyIndexed = 0;
            int newlyIndexed = 0;
            
            for (Path pdfPath : pdfFiles) {
                String fileName = pdfPath.getFileName().toString();
                
                if (isReallyIndexedInChroma(fileName)) {
                    alreadyIndexed++;
                    indexedFiles.add(fileName);
                    
                    try {
                        long fileSize = Files.size(pdfPath);
                        int chunkCount = getChunkCountFromChroma(fileName);
                        
                        DocumentMetadata metadata = new DocumentMetadata(fileName, pdfPath.toString());
                        metadata.setId(UUID.randomUUID().toString());
                        metadata.setFileSize(fileSize);
                        metadata.setNumberOfChunks(chunkCount);
                        metadata.setIndexed(true);
                        metadata.setIndexDate(LocalDateTime.now());
                        documentsCache.put(fileName, metadata);
                        
                        logger.debug(" {} déjà indexé ({} chunks)", fileName, chunkCount);
                    } catch (IOException e) {
                        logger.warn(" Impossible de lire les métadonnées de: {}", fileName);
                    }
                } else {
                    logger.info(" Indexation de: {}", fileName);
                    DocumentMetadata metadata = loadPDF(pdfPath);
                    if (metadata != null) {
                        newlyIndexed++;
                    }
                }
            }
            
            logger.info(" Initialisation terminée:");
            logger.info("   - {} documents déjà indexés", alreadyIndexed);
            logger.info("   - {} documents nouvellement indexés", newlyIndexed);
            logger.info("   - {} documents au total", indexedFiles.size());
            
        } catch (IOException e) {
            logger.error(" Erreur lors de l'initialisation de l'index", e);
        }
    }
    
    
    // vérifie si un document est vraiment dans ChromaDB
    private boolean isReallyIndexedInChroma(String fileName) {
        try {
            Embedding testEmbedding = embeddingService.embedText("test");
            
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(testEmbedding)
                .maxResults(50) 
                .minScore(0.0) 
                .build();
            
            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
            
            
            boolean found = result.matches().stream()
                .anyMatch(match -> {
                    String segmentFileName = match.embedded().metadata().getString("file_name");
                    return fileName.equals(segmentFileName);
                });
            
            return found;
            
        } catch (Exception e) {
            logger.debug(" Erreur vérification ChromaDB pour {}: {}", fileName, e.getMessage());
            return false;
        }
    }
    
    
    // Compte le nombre de chunks d'un fichier dans ChromaDB
    private int getChunkCountFromChroma(String fileName) {
        try {
            Embedding testEmbedding = embeddingService.embedText("test");
            
            EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(testEmbedding)
                .maxResults(1000) 
                .minScore(0.0)
                .build();
            
            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);
            
            long count = result.matches().stream()
                .filter(match -> {
                    String segmentFileName = match.embedded().metadata().getString("file_name");
                    return fileName.equals(segmentFileName);
                })
                .count();
            
            return (int) count;
            
        } catch (Exception e) {
            logger.debug(" Impossible de compter les chunks de {}", fileName);
            return 0;
        }
    }

    @Override
    public List<DocumentMetadata> loadAllPDFs() {
        logger.info(" Chargement de tous les PDFs...");
        
        try {
            Path coursesPath = Paths.get(coursesDirectory);
            
            if (!Files.exists(coursesPath)) {
                logger.warn(" Répertoire non trouvé, création: {}", coursesDirectory);
                Files.createDirectories(coursesPath);
                return new ArrayList<>();
            }
            
            List<DocumentMetadata> documents = Files.list(coursesPath)
                .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                .filter(path -> !indexedFiles.contains(path.getFileName().toString()))
                .map(this::loadPDF)
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
            
            logger.info(" {} PDFs chargés avec succès", documents.size());
            return documents;
            
        } catch (IOException e) {
            logger.error(" Erreur lors du chargement des PDFs", e);
            throw new PDFLoadException("Impossible de charger les PDFs", e);
        }
    }

    @Override
    public DocumentMetadata loadPDF(Path pdfPath) {
        String fileName = pdfPath.getFileName().toString();
        
        if (indexedFiles.contains(fileName)) {
            logger.info(" PDF déjà indexé: {}", fileName);
            return documentsCache.get(fileName);
        }
        
        logger.info(" Chargement du PDF: {}", fileName);
        
        try {
            byte[] fileContent = Files.readAllBytes(pdfPath);
            logger.debug(" Taille du fichier: {} octets", fileContent.length);
            
            ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
            Document document = parser.parse(new ByteArrayInputStream(fileContent));
            logger.debug(" Document parsé avec succès");
           
            DocumentSplitter splitter = DocumentSplitters.recursive(
                chunkSize, 
                chunkOverlap
            );
            List<TextSegment> segments = splitter.split(document);
            logger.debug(" Document découpé en {} segments", segments.size());
            
            for (int i = 0; i < segments.size(); i++) {
                TextSegment segment = segments.get(i);
                segment.metadata().put("file_name", fileName);
                segment.metadata().put("source", pdfPath.toString());
                segment.metadata().put("chunk_index", String.valueOf(i));
            }
            logger.debug(" Métadonnées ajoutées à {} segments", segments.size());
            
            logger.debug(" Stockage des embeddings dans ChromaDB...");
            embeddingService.storeEmbeddings(segments);
            logger.debug(" Embeddings stockés avec succès");
            
            DocumentMetadata metadata = new DocumentMetadata(
                fileName,
                pdfPath.toString()
            );
            metadata.setId(UUID.randomUUID().toString());
            metadata.setFileSize(fileContent.length);
            metadata.setNumberOfChunks(segments.size());
            metadata.setIndexed(true);
            metadata.setIndexDate(LocalDateTime.now());
            
            indexedFiles.add(fileName);
            documentsCache.put(fileName, metadata);
            
            logger.info(" PDF indexé: {} ({} chunks)", fileName, segments.size());
            
            return metadata;
            
        } catch (Exception e) {
            logger.error(" Erreur lors du chargement de {}: {}", 
                        fileName, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DocumentMetadata> loadSpecificPDFs(List<String> pdfNames) {
        logger.info(" Chargement de {} PDFs spécifiques", pdfNames.size());
        
        List<DocumentMetadata> documents = new ArrayList<>();
        Path coursesPath = Paths.get(coursesDirectory);
        
        for (String pdfName : pdfNames) {
            try {
                Path pdfPath = coursesPath.resolve(pdfName);
                if (Files.exists(pdfPath)) {
                    DocumentMetadata doc = loadPDF(pdfPath);
                    if (doc != null) {
                        documents.add(doc);
                    }
                } else {
                    logger.warn(" PDF non trouvé: {}", pdfName);
                }
            } catch (Exception e) {
                logger.error(" Erreur avec {}: {}", pdfName, e.getMessage());
            }
        }
        
        logger.info(" {} PDFs chargés sur {} demandés", documents.size(), pdfNames.size());
        return documents;
    }

    @Override
    public DocumentMetadata uploadAndIndexPDF(byte[] fileContent, String fileName) {
        logger.info(" Upload et indexation de: {}", fileName);
        
        if (indexedFiles.contains(fileName)) {
            logger.warn(" Le fichier {} est déjà indexé", fileName);
            throw new PDFLoadException("Le document '" + fileName + "' est déjà indexé dans la base de données");
        }
        
        try {
            Path coursesPath = Paths.get(coursesDirectory);
            if (!Files.exists(coursesPath)) {
                Files.createDirectories(coursesPath);
            }
            
            Path filePath = coursesPath.resolve(fileName);
            
            if (Files.exists(filePath)) {
                logger.warn(" Le fichier {} existe déjà sur disque", fileName);
                throw new PDFLoadException("Le fichier '" + fileName + "' existe déjà");
            }
            
            Files.write(filePath, fileContent, 
                       StandardOpenOption.CREATE, 
                       StandardOpenOption.TRUNCATE_EXISTING);
            
            logger.info(" Fichier sauvegardé: {}", filePath);
            
            return loadPDF(filePath);
            
        } catch (IOException e) {
            logger.error(" Erreur lors de l'upload de {}: {}", fileName, e.getMessage(), e);
            throw new PDFLoadException("Impossible d'uploader le PDF: " + fileName, e);
        }
    }

    @Override
    public boolean isPDFIndexed(String fileName) {
        return indexedFiles.contains(fileName);
    }
    
    @Override
    public int getIndexedFilesCount() {
        return indexedFiles.size();
    }
    
    @Override
    public Set<String> getIndexedFileNames() {
        return new HashSet<>(indexedFiles);
    }
    
    @Override
    public List<DocumentMetadata> getIndexedDocumentsMetadata() {
        return new ArrayList<>(documentsCache.values());
    }
    
    @Override
    public List<DocumentMetadata> forceReindexAll() {
        logger.info(" Force ré-indexation de tous les fichiers");
        indexedFiles.clear();
        documentsCache.clear();
        
        return loadAllPDFs();
    }
}