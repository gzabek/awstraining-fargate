package com.awstraining.backend.business.notifyme;

import com.amazonaws.services.comprehend.AmazonComprehend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotifyMeService {

    // TODO: lab1
    //  1. Inject MessageSender.
    private final MessageSender messageSender;
    // TODO lab2

    @Autowired
    public NotifyMeService(MessageSender messageSender) {
        this.messageSender = messageSender;

    }

    public String notifyMe(NotifyMeDO notifyMe) {

        // TODO: lab1
        //  1. Send text using sender.
        //  2. Return sent message.
        messageSender.send(notifyMe.text());
        // TODO: lab2
        //  1. Translate text from using translator.
        //  2. Change sending of text to "translated text" and return it.
//        String translatedText = translator.translate(notifyMe);
//        // TODO: lab3
//        //  1. Detect sentiment of translated message.
//        //  2. Change sending of text to "setiment: translated text" and return it.
//        String detected = sentiment.detectSentiment(notifyMe.targetLc(), notifyMe.text());
//        String result = detected + ": " + translatedText;
//        messageSender.send(result);

        return "ok";
    }

}
