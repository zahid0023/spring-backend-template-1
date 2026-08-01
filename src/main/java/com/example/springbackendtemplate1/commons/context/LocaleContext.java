package com.example.springbackendtemplate1.commons.context;

public final class LocaleContext {

    private static final ThreadLocal<Long> CURRENT_LOCALE_ID = new ThreadLocal<>();

    private LocaleContext() {
    }

    public static void setLocaleId(Long localeId) {
        CURRENT_LOCALE_ID.set(localeId);
    }

    public static Long getLocaleId() {
        return CURRENT_LOCALE_ID.get();
    }

    public static void clear() {
        CURRENT_LOCALE_ID.remove();
    }
}
