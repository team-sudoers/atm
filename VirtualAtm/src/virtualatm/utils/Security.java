package virtualatm.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Security {

   private static final int DEFAULT_SALT_LENGTH = 32;
   private static final int DEFAULT_HASH_ITERATIONS = 10000;
   private static final String FIELD_SEPERATOR = "^";

   private enum AlgorithmTypes {
      PBKDF2WithHmacSHA1, PBKDF2WithHmacSHA256, PBKDF2WithHmacSHA512
   }

   private Security() {
   }

   public static String createHash(String value) throws Exception {
      byte[] salt = new byte[DEFAULT_SALT_LENGTH];
      SecureRandom random = new SecureRandom();
      random.nextBytes(salt);

      byte[] key = deriveKey(AlgorithmTypes.PBKDF2WithHmacSHA256, value.toCharArray(), salt, DEFAULT_HASH_ITERATIONS);
      byte[] blob = new byte[salt.length + key.length];
      System.arraycopy(salt, 0, blob, 0, salt.length);
      System.arraycopy(key, 0, blob, salt.length, key.length);

      Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
      String ret = AlgorithmTypes.PBKDF2WithHmacSHA256.ordinal() + FIELD_SEPERATOR
              + DEFAULT_HASH_ITERATIONS + FIELD_SEPERATOR
              + enc.encodeToString(blob);

      return ret;
   }

   public static boolean compareHash(String hash, String value) throws Exception {
      String[] parts = hash.split(Pattern.quote(FIELD_SEPERATOR));
      if (parts.length < 3) {
         throw new Exception("Invalid hash provided!");
      }

      AlgorithmTypes alg = AlgorithmTypes.values()[Integer.parseInt(parts[0])];
      int iterations = Integer.parseInt(parts[1]);
      byte[] blob = Base64.getUrlDecoder().decode(parts[2]);
      byte[] salt = Arrays.copyOfRange(blob, 0, DEFAULT_SALT_LENGTH);
      byte[] check = deriveKey(alg, value.toCharArray(), salt, iterations);

      int zero = 0;
      for (int idx = 0; idx < check.length; ++idx) {
         zero |= blob[salt.length + idx] ^ check[idx];
      }
      return zero == 0;
   }

   private static byte[] deriveKey(AlgorithmTypes alg, char[] value, byte[] salt, int iterations) throws InvalidKeySpecException, NoSuchAlgorithmException {
      SecretKeyFactory skf = SecretKeyFactory.getInstance(alg.name());

      int keyLength = 0;
      switch (alg) {
         case PBKDF2WithHmacSHA1:
            keyLength = 128;
            break;
         case PBKDF2WithHmacSHA256:
            keyLength = 256;
            break;
         case PBKDF2WithHmacSHA512:
            keyLength = 512;
            break;
         default:
            keyLength = 0;
      }
      
      PBEKeySpec spec = new PBEKeySpec(value, salt, iterations, keyLength);
      SecretKey key = skf.generateSecret(spec);
      return key.getEncoded();
   }
}
