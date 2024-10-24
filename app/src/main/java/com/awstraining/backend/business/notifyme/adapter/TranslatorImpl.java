package com.awstraining.backend.business.notifyme.adapter;

import com.awstraining.backend.business.notifyme.NotifyMeDO;
import com.awstraining.backend.business.notifyme.Translator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class TranslatorImpl implements Translator {

    private static final Logger LOGGER = LogManager.getLogger(TranslatorImpl.class);
    

    
    // TODO: lab2
    //  1. Inject AWS AmazonTranslate from configuration TranslatorConfig.
//    @Autowired
    public TranslatorImpl() {
        
    }
    
    @Override
    public String translate(NotifyMeDO notifyMeDO) {
        // TODO: lab2
        //  1. Create translate text request.
        //  2. Call translate.
        //  3. Log information about successful translated message.
        //  4. Return translated message
        return "";
    }
}
