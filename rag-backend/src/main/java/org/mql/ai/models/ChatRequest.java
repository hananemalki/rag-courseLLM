package org.mql.ai.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatRequest {
    
    private String question;
    
    @JsonProperty("include_sources")
    private boolean includeSources = true;
    
    private String language; 
    
    public ChatRequest() {
    }
    
    public ChatRequest(String question, boolean includeSources) {
        this.question = question;
        this.includeSources = includeSources;
    }
    
    public ChatRequest(String question, boolean includeSources, String language) {
        this.question = question;
        this.includeSources = includeSources;
        this.language = language;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public boolean isIncludeSources() {
        return includeSources;
    }
    
    public void setIncludeSources(boolean includeSources) {
        this.includeSources = includeSources;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    @Override
    public String toString() {
        return "ChatRequest{" +
                "question='" + question + '\'' +
                ", includeSources=" + includeSources +
                ", language='" + language + '\'' +
                '}';
    }
}