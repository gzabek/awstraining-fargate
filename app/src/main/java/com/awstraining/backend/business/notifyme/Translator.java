package com.awstraining.backend.business.notifyme;

public interface Translator {
    default String translate(NotifyMeDO notifyMeDO) {
        return "";
    }
}
