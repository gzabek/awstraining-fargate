package com.awstraining.backend.business.notifyme.adapter;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.awstraining.backend.business.notifyme.MessageSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageSnsAWSSender implements MessageSender {

    private static final Logger LOGGER = LogManager.getLogger(MessageSnsAWSSender.class);

    private final AmazonSNS amazonSNS;
    private final String snsTopic;
    // TODO: lab1
    //  1. Inject AWS AmazonsSNS from configuration SNSConfig.
    //  2. Make sure that you created new value in parameter store with arn of sns topic.
    //  3. Inject parameter with @Value annotation through constructor.
    @Autowired
    public MessageSnsAWSSender(AmazonSNS amazonSNS, @Value("${notification.topicarn}") String snsTopic) {
        this.amazonSNS = amazonSNS;
        this.snsTopic = snsTopic;
    }

    @Override
    public void send(String text) {
        // TODO: lab1
        //  1. Create publish request.
        PublishRequest request = new PublishRequest(snsTopic, text);
        //  2. Publish request with sns
        amazonSNS.publish(request);
        //  3. Log information about successful sent message to topic with topic arn and message id.
        LOGGER.info("Message was sent on topic {} with id {}", snsTopic, request.getMessageGroupId());
    }
}
