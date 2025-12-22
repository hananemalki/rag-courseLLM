package org.mql.ai.business;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.mql.ai.models.DocumentMetadata;

public interface PDFLoaderService {
	List<DocumentMetadata> loadAllPDFs();
	DocumentMetadata loadPDF(Path pdfPath);
	List<DocumentMetadata> loadSpecificPDFs(List<String> pdfNames);
	DocumentMetadata uploadAndIndexPDF(byte[] fileContent, String fileName);
	boolean isPDFIndexed(String fileName);
	int getIndexedFilesCount();
    Set<String> getIndexedFileNames();
    List<DocumentMetadata> getIndexedDocumentsMetadata();
    
    List<DocumentMetadata> forceReindexAll();
}
