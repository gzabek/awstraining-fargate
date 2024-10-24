package com.awstraining.backend.business.notifyme.adapter;

import com.awstraining.backend.business.notifyme.Sentiment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SentimentDetectorImpl implements Sentiment {

    private static final Logger LOGGER = LogManager.getLogger(SentimentDetectorImpl.class);
    

    
    // TODO: lab3
    //  1. Inject AWS AmazonComprehend from configuration ComprehendSentimentConfig.
//    @Autowired
    public SentimentDetectorImpl() {
        
    }

    // TODO: lab3
    //  1. Create detect sentiment text request.
    //  2. Call detection.
    //  3. Log information about sentiment.
    //  4. Return sentiment.
    @Override
    public String detectSentiment(String language, String text) {
      return "";
    }
}
