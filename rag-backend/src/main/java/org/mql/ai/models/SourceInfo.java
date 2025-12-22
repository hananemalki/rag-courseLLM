package org.mql.ai.models;

public class SourceInfo {
	private String fileName;
    private double relevanceScore;
    private String excerpt;
    private int pageNumber;

    public SourceInfo() {}

    public SourceInfo(String fileName, double relevanceScore, String excerpt) {
        this.fileName = fileName;
        this.relevanceScore = relevanceScore;
        this.excerpt = excerpt;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
