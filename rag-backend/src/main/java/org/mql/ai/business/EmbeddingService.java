package org.mql.ai.business;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public interface EmbeddingService {
	Embedding embedText(String text);
	List<Embedding> embedSegments(List<TextSegment> segments);
	void storeEmbeddings(List<TextSegment> segments);
}
