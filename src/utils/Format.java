package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Alat bantu format tampilan dan format nilai untuk database.
 *
 * @author ruben
 */
public final class Format {

    private static final Locale INDONESIA = Locale.forLanguageTag("id-ID");

    private Format() {
    }

    /**
     * Mengubah angka menjadi teks desimal dengan titik (untuk SQL),
     * contoh: 150000.5 -> "150000.50".
     *
     * Dipakai agar nilai decimal selalu memakai titik, bukan koma,
     * walaupun komputer memakai locale Indonesia.
     */
    public static String decimal(double nilai) {
        return BigDecimal.valueOf(nilai)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * Mengubah angka menjadi format rupiah untuk ditampilkan,
     * contoh: 150000.5 -> "Rp150.000,50".
     */
    public static String rupiah(double nilai) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(INDONESIA);
        return nf.format(nilai);
    }
}
