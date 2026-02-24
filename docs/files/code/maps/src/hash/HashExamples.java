package hash;

/**
 * Standalone demonstrations of hash-code and compression functions
 * (Chapter 10.2).
 *
 * All methods are static utilities — no instances needed.
 */
public class HashExamples {

    // ==================== Hash Codes ====================

    /**
     * Polynomial hash code using Horner's method.
     *   h = s[0]*a^(n-1) + s[1]*a^(n-2) + ... + s[n-1]
     * Common choices: a = 33, 37, 39, 41.
     */
    public static int polynomialHash(String s, int a) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = h * a + s.charAt(i);
        }
        return h;
    }

    /**
     * Cyclic-shift hash code.
     * Replaces multiplication by a bit rotation of 'shift' positions.
     */
    public static int cyclicShiftHash(String s, int shift) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = (h << shift) | (h >>> (32 - shift));  // rotate left
            h += s.charAt(i);
        }
        return h;
    }

    // ==================== Compression ====================

    /** Division method: h mod N. */
    public static int compressDivision(int hashCode, int N) {
        return Math.abs(hashCode) % N;
    }

    /** MAD (Multiply-Add-and-Divide): ((a*h + b) mod p) mod N. */
    public static int compressMAD(int hashCode, int a, int b, int p, int N) {
        return (int) (((long) a * Math.abs(hashCode) + b) % p) % N;
    }

    // ==================== Demo ====================

    /** Print hash-code and compression examples for a set of sample strings. */
    public static void demo() {
        String[] samples = {"hello", "world", "abc", "ABC", "Java"};

        System.out.println("=== Hash Code Examples ===");
        System.out.println();

        System.out.println("Polynomial hash (a=33):");
        for (String s : samples) {
            System.out.println("  \"" + s + "\" -> " + polynomialHash(s, 33));
        }
        System.out.println();

        System.out.println("Cyclic-shift hash (shift=5):");
        for (String s : samples) {
            System.out.println("  \"" + s + "\" -> " + cyclicShiftHash(s, 5));
        }
        System.out.println();

        System.out.println("=== Compression Examples ===");
        System.out.println();

        int h = polynomialHash("hello", 33);
        int N = 17;
        System.out.println("hashCode of \"hello\" (poly, a=33) = " + h);
        System.out.println("  Division (N=" + N + ")          : " + compressDivision(h, N));
        System.out.println("  MAD (a=3, b=5, p=19, N=" + N + "): " + compressMAD(h, 3, 5, 19, N));
    }
}
