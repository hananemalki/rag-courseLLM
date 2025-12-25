package org.mql.ai.controllers;

import java.util.List;
import java.util.Map;

import org.mql.ai.business.PDFLoaderService;
import org.mql.ai.business.RagService;
import org.mql.ai.models.DocumentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
class PDFController {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);
    
    private final PDFLoaderService pdfLoaderService;
    private final RagService ragService;

    public PDFController(PDFLoaderService pdfLoaderService, RagService ragService) {
        this.pdfLoaderService = pdfLoaderService;
        this.ragService = ragService;
    }

    /**
     * Upload et indexation d'un nouveau PDF
     *  /api/documents/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPDF(@RequestParam("file") MultipartFile file) {
        logger.info(" Upload de fichier: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Le fichier est vide"));
        }
        
        if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Seuls les fichiers PDF sont acceptés"));
        }
        
        try {
            byte[] fileContent = file.getBytes();
            DocumentMetadata metadata = pdfLoaderService.uploadAndIndexPDF(
                fileContent, 
                file.getOriginalFilename()
            );
            
            logger.info(" Document indexé: {}", file.getOriginalFilename());
            return ResponseEntity.ok(metadata);
            
        } catch (Exception e) {
            logger.error(" Erreur lors de l'upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Indexer tous les PDFs du répertoire
     *  /api/documents/index-all
     */
    
    @PostMapping("/index-all")
    public ResponseEntity<Map<String, Object>> indexAll() {
        logger.info(" Indexation de tous les documents...");
        
        try {
            List<DocumentMetadata> docs = pdfLoaderService.forceReindexAll();
            return ResponseEntity.ok(Map.of( "message", "Indexation terminée", "documentsIndexed", docs.size()
            ));
        } catch (Exception e) {
            logger.error(" Erreur lors de l'indexation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Vérifier si un document est indexé
     *  /api/documents/check/{filename}
     */
    @GetMapping("/check/{filename}")
    public ResponseEntity<Map<String, Boolean>> checkIndexed(@PathVariable String filename) {
        boolean indexed = pdfLoaderService.isPDFIndexed(filename);
        return ResponseEntity.ok(Map.of("indexed", indexed));
    }

    /**
     * Supprimer l'index complet (avec confirmation)
     * /api/documents/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearIndex(
            @RequestParam(defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ajoutez ?confirm=true pour confirmer"));
        }
        
        logger.warn(" Suppression de l'index demandée");
        ragService.clearIndex();
        
        return ResponseEntity.ok(Map.of(
            "message", "Index supprimé avec succès"
        ));
    }

    /**
     * Lister tous les documents
     *  /api/documents/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<DocumentMetadata>> listDocuments() {
        List<DocumentMetadata> documents = pdfLoaderService.loadAllPDFs();
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/indexed")
    public ResponseEntity<List<DocumentMetadata>> getIndexedDocuments() {
        logger.info(" Liste des documents indexés");
        List<DocumentMetadata> documents = pdfLoaderService.getIndexedDocumentsMetadata();
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Force la ré-indexation même si déjà indexé
     *  /api/documents/force-reindex
     */
    @PostMapping("/force-reindex")
    public ResponseEntity<Map<String, Object>> forceReindex() {
        logger.info(" Force ré-indexation demandée");
        
        try {
            List<DocumentMetadata> docs = pdfLoaderService.forceReindexAll();
            return ResponseEntity.ok(Map.of(
                "message", "Ré-indexation forcée terminée",
                "documentsIndexed", docs.size()
            ));
        } catch (Exception e) {
            logger.error(" Erreur lors de la ré-indexation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    
}