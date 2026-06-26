package com.nhynhi.store;

import java.util.Locale;

/** Tiện ích định dạng tiền tệ kiểu Việt Nam: 29.500.000 ₫ */
public final class StoreFormat {
    private StoreFormat() { }

    public static String money(double value) {
        long n = Math.round(value);
        return String.format(Locale.US, "%,d", n).replace(',', '.') + " ₫";
    }

    /** Rút gọn số lớn: 1.2 tỷ / 35.9 tr / 650 K */
    public static String moneyShort(double value) {
        if (value >= 1_000_000_000d) return trim(value / 1_000_000_000d) + " tỷ";
        if (value >= 1_000_000d)     return trim(value / 1_000_000d) + " tr";
        if (value >= 1_000d)         return trim(value / 1_000d) + " K";
        return String.valueOf(Math.round(value));
    }

    private static String trim(double v) {
        return String.format(Locale.US, "%.1f", v).replace(".0", "");
    }
}
