package org.mql.ai.exceptions;

@org.springframework.web.bind.annotation.RestControllerAdvice
class GlobalExceptionHandler {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(PDFLoadException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> 
            handlePDFLoadException(PDFLoadException e) {
        logger.error(" Erreur de chargement PDF", e);
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                    "error", "PDF Load Error",
                    "message", e.getMessage()
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RAGException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> 
            handleRAGException(RAGException e) {
        logger.error(" Erreur RAG", e);
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                    "error", "RAG Processing Error",
                    "message", e.getMessage()
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> 
            handleGenericException(Exception e) {
        logger.error(" Erreur inattendue", e);
        return org.springframework.http.ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                    "error", "Internal Server Error",
                    "message", e.getMessage()
                ));
    }
}
