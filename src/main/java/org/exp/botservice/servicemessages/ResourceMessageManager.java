package org.exp.botservice.servicemessages;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceMessageManager {
    public static ResourceBundle bundle;

    public static void loadBundle(String baseName, Locale locale) {
        bundle = ResourceBundle.getBundle(baseName, locale);
    }

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }
}