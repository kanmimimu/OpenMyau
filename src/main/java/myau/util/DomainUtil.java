package myau.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DomainUtil {
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "\\b(?:[a-zA-Z0-9](?:[a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}\\b"
    );

    private static String replacementDomain = "";
    private static boolean enabled = false;

    public static boolean isEnabled() {
        return enabled && !replacementDomain.isEmpty();
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static String getReplacementDomain() {
        return replacementDomain;
    }

    public static void setReplacementDomain(String domain) {
        replacementDomain = domain;
        enabled = !domain.isEmpty();
    }

    public static boolean containsDomain(String text) {
        if (text == null || text.isEmpty()) return false;
        String stripped = stripColorCodes(text);
        return DOMAIN_PATTERN.matcher(stripped).find();
    }

    public static String replaceDomain(String original) {
        if (original == null || original.isEmpty() || replacementDomain.isEmpty()) {
            return original;
        }

        String stripped = stripColorCodes(original);
        if (!DOMAIN_PATTERN.matcher(stripped).find()) {
            return original;
        }

        String colorCode = original.length() >= 2 && original.charAt(0) == '\u00a7'
                ? original.substring(0, 2)
                : "\u00a7f";
        return colorCode + replacementDomain;
    }

    private static String stripColorCodes(String text) {
        return text.replaceAll("[\u00a7&][0-9a-fk-or]", "").trim();
    }
}
