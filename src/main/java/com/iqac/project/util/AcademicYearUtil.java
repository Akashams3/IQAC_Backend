package com.iqac.project.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Canonical academic year format in this project: {@code YYYY-YY} (e.g. {@code 2024-25}).
 * Stored values may still use {@code YYYY-YYYY}; queries accept both.
 */
public final class AcademicYearUtil {

    private static final Pattern FOUR_FOUR = Pattern.compile("^(\\d{4})-(\\d{4})$");
    private static final Pattern FOUR_TWO = Pattern.compile("^(\\d{4})-(\\d{2})$");

    private AcademicYearUtil() {
    }

    /** Null-safe trim; blank → null. */
    public static String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Normalizes common inputs to {@code YYYY-YY}.
     * {@code 2024-2025} → {@code 2024-25}; already-short values pass through.
     */
    public static String normalizeToShort(String raw) {
        String s = blankToNull(raw);
        if (s == null) return null;
        Matcher m4 = FOUR_FOUR.matcher(s);
        if (m4.matches()) {
            String y1 = m4.group(1);
            String y2full = m4.group(2);
            return y1 + "-" + y2full.substring(2);
        }
        Matcher m2 = FOUR_TWO.matcher(s);
        if (m2.matches()) return s;
        return s;
    }

    /**
     * Expands {@code YYYY-YY} to spanning form {@code YYYY-YYYY} (e.g. {@code 2024-25} → {@code 2024-2025}).
     * Returns the input unchanged if it is not {@code YYYY-YY}.
     */
    public static String toLongForm(String shortOrAny) {
        String s = blankToNull(shortOrAny);
        if (s == null) return null;
        Matcher m2 = FOUR_TWO.matcher(s);
        if (!m2.matches()) return s;
        int y1 = Integer.parseInt(m2.group(1));
        int yy = Integer.parseInt(m2.group(2));
        int century = (y1 / 100) * 100;
        int y2 = century + yy;
        if (y2 <= y1) {
            y2 += 100;
        }
        return y1 + "-" + y2;
    }

    /**
     * Pair for repository filters: canonical short form and long spanning form.
     * Blank input yields {@code {null, null}} (no year filter).
     */
    public static String[] filterPair(String raw) {
        String shortForm = normalizeToShort(blankToNull(raw));
        if (shortForm == null) {
            return new String[]{null, null};
        }
        return new String[]{shortForm, toLongForm(shortForm)};
    }
}
