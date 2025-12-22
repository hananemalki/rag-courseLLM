package org.mql.ai.business;

import org.mql.ai.models.ChatRequest;
import org.mql.ai.models.ChatResponse;

public interface RagService {
    ChatResponse processQuestion(ChatRequest request);
    int indexAllDocuments();
    boolean indexDocument(String fileName);
    int getIndexedDocumentsCount();
    void clearIndex();
}