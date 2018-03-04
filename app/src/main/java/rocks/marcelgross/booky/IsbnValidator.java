package rocks.marcelgross.booky;

public class IsbnValidator {
    public static boolean isValid(String isbn) {
        if (isbn == null) {
            return false;
        }

        isbn = isbn.replaceAll("-", "").trim();

        return validateIsbn13(isbn) || validateIsbn10(isbn);
    }

    private static boolean validateIsbn13(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }

        try {
            int tot = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                tot += i % 2 == 0 ? digit : digit * 3;
            }

            int checksum = (10 - (tot % 10)) % 10;
            return checksum == Integer.parseInt(isbn.substring(12));
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static boolean validateIsbn10(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }

        try {
            int tot = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Integer.parseInt(isbn.substring(i, i + 1));
                tot += ((10 - i) * digit);
            }

            String checksum = Integer.toString((11 - (tot % 11)) % 11);
            if ("10".equals(checksum)) {
                checksum = "X";
            }

            return checksum.equals(isbn.substring(9));
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
