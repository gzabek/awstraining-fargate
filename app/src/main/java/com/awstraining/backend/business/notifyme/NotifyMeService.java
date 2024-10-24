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
    private final Translator translator;
    // TODO lab3
    //  1. Inject sentiment detector

    @Autowired
    public NotifyMeService(MessageSender messageSender, Translator translator) {
        this.messageSender = messageSender;
        this.translator = translator;

    }

    public String notifyMe(NotifyMeDO notifyMe) {

        // TODO: lab1
        //  1. Send text using sender.
        //  2. Return sent message.
        // TODO: lab2
        //  1. Translate text from using translator.
        //  2. Change sending of text to "translated text" and return it.
        String translatedText = translator.translate(notifyMe);
        // TODO: lab3
        //  1. Detect sentiment of translated message.
        //  2. Change sending of text to "setiment: translated text" and return it.
//        String detected = sentiment.detectSentiment(notifyMe.targetLc(), notifyMe.text());
//        String result = detected + ": " + translatedText;
        messageSender.send(translatedText);

        return translatedText;
    }

}
