package org.mql.ai.controllers;

import org.mql.ai.business.PDFLoaderService;
import org.mql.ai.business.RagService;
import org.mql.ai.models.ChatRequest;
import org.mql.ai.models.ChatResponse;
import org.mql.ai.models.DocumentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    
     // POST /api/chat/ask

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> askQuestion(@RequestBody ChatRequest request) {
        logger.info(" Question reçue: {}", request.getQuestion());
        
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ChatResponse.error("La question ne peut pas être vide"));
        }
        
        ChatResponse response = ragService.processQuestion(request);
        
        if (response.isSuccess()) {
            logger.info(" Réponse générée avec succès");
            return ResponseEntity.ok(response);
        } else {
            logger.error(" Erreur: {}", response.getErrorMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    
    // Health check
     
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "RAG Chat API"
        ));
    }

    
    // Obtenir le nombre de documents indexés
     
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        int count = ragService.getIndexedDocumentsCount();
        return ResponseEntity.ok(Map.of(
            "indexedDocuments", count,
            "status", "ready"
        ));
    }

}