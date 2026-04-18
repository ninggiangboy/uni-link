package dev.ngb.util;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public final class HashUtils {

    private static final int BUFFER_SIZE = 8192;

    private static final String ALG_SHA256 = "SHA-256";
    private static final String ALG_SHA512 = "SHA-512";
    private static final String ALG_MD5 = "MD5";
    private static final String ALG_HMAC_SHA256 = "HmacSHA256";

    public static byte[] sha256(byte[] input) {
        return digest(ALG_SHA256, input);
    }

    public static String sha256Hex(String input) {
        return toHex(sha256(input.getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] sha512(byte[] input) {
        return digest(ALG_SHA512, input);
    }

    public static String sha512Hex(String input) {
        return toHex(sha512(input.getBytes(StandardCharsets.UTF_8)));
    }

    public static byte[] md5(byte[] input) {
        return digest(ALG_MD5, input);
    }

    public static String md5Hex(String input) {
        return toHex(md5(input.getBytes(StandardCharsets.UTF_8)));
    }

    private static byte[] digest(String algorithm, byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static byte[] hmacSha256(byte[] key, byte[] data) {
        return hmac(ALG_HMAC_SHA256, key, data);
    }

    public static String hmacSha256Hex(String key, String data) {
        return toHex(
                hmacSha256(
                        key.getBytes(StandardCharsets.UTF_8),
                        data.getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    private static byte[] hmac(String algorithm, byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit((b & 0xF), 16));
        }
        return sb.toString();
    }

    public static byte[] fromHex(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) (
                    (Character.digit(hex.charAt(i), 16) << 4)
                            + Character.digit(hex.charAt(i + 1), 16)
            );
        }
        return result;
    }

    public static boolean secureEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) return false;

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    public static String sha256Hex(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            return toHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw ExceptionUtils.wrap(e);
        }
    }
}
