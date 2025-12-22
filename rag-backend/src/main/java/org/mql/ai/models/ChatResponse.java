package org.mql.ai.models;

import java.util.List;

public class ChatResponse {
	private String answer;
    private List<SourceInfo> sources;
    private long processingTimeMs;
    private boolean success;
    private String errorMessage;

    public ChatResponse() {}

    public ChatResponse(String answer) {
        this.answer = answer;
        this.success = true;
    }

    public static ChatResponse success(String answer, List<SourceInfo> sources, long processingTime) {
        ChatResponse response = new ChatResponse();
        response.setAnswer(answer);
        response.setSources(sources);
        response.setProcessingTimeMs(processingTime);
        response.setSuccess(true);
        return response;
    }

    public static ChatResponse error(String errorMessage) {
        ChatResponse response = new ChatResponse();
        response.setErrorMessage(errorMessage);
        response.setSuccess(false);
        return response;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<SourceInfo> getSources() {
        return sources;
    }

    public void setSources(List<SourceInfo> sources) {
        this.sources = sources;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
