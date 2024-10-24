package com.awstraining.backend.business.notifyme;

public interface Sentiment {
    default String detectSentiment(String language, String text) {
        return "";
    }
}
