package emu.grasscutter.utils;

import emu.grasscutter.Grasscutter;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class RSADecryptionUtil {
    private static PrivateKey sdkPrivateKey;
    private static PrivateKey authPrivateKey;

    static {
        sdkPrivateKey = loadKey("/keys/private_key.der", "private_key.der");
        authPrivateKey = loadKey("/keys/auth_private-key.der", "auth_private-key.der");
    }

    private static PrivateKey loadKey(String resourcePath, String name) {
        try {
            byte[] keyBytes = FileUtils.readResource(resourcePath);
            if (keyBytes == null || keyBytes.length == 0) {
                Grasscutter.getLogger().warn("Key not found: " + name);
                return null;
            }
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (Exception e) {
            Grasscutter.getLogger().warn("Failed to load key " + name + ": " + e.getMessage());
            return null;
        }
    }

    public static String decrypt(String encryptedBase64) throws Exception {
        if (encryptedBase64 == null || encryptedBase64.isEmpty()) {
            throw new IllegalArgumentException("Encrypted data is null or empty");
        }

        byte[] encryptedBytes;
        try {
            encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        } catch (Exception e) {
            throw new Exception("Failed to base64-decode input", e);
        }

        Grasscutter.getLogger().debug("RSA decrypt attempt: input base64 len=" + encryptedBase64.length() + " bytes=" + encryptedBytes.length);

        if (sdkPrivateKey != null) {
            String result = tryDecrypt(encryptedBytes, sdkPrivateKey, "RSA/ECB/PKCS1Padding", "sdk+PKCS1");
            if (result != null) return result;
        }

        if (sdkPrivateKey != null) {
            String result = tryDecrypt(encryptedBytes, sdkPrivateKey, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding", "sdk+OAEP");
            if (result != null) return result;
        }

        if (authPrivateKey != null) {
            String result = tryDecrypt(encryptedBytes, authPrivateKey, "RSA/ECB/PKCS1Padding", "auth+PKCS1");
            if (result != null) return result;
        }

        if (authPrivateKey != null) {
            String result = tryDecrypt(encryptedBytes, authPrivateKey, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding", "auth+OAEP");
            if (result != null) return result;
        }

        String plaintext = new String(encryptedBytes, StandardCharsets.UTF_8);
        if (isPrintable(plaintext)) {
            Grasscutter.getLogger().info("RSA decrypt: using plaintext fallback, value=" + plaintext);
            return plaintext;
        }

        Grasscutter.getLogger().error("RSA decrypt: all methods failed for input (base64 len=" + encryptedBase64.length() + ")");
        throw new Exception("RSA decryption failed: no matching key or padding");
    }

    private static String tryDecrypt(byte[] data, PrivateKey key, String transformation, String label) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(data);
            String result = new String(decrypted, StandardCharsets.UTF_8);
            Grasscutter.getLogger().info("RSA decrypt succeeded with " + label + " -> [" + result + "]");
            return result;
        } catch (Exception e) {
            Grasscutter.getLogger().debug("RSA decrypt failed with " + label + ": " + e.getMessage());
            return null;
        }
    }

    private static boolean isPrintable(String s) {
        if (s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (c < 32 || c > 126) return false;
        }
        return true;
    }
}
