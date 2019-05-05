/* 
 * File:    Security.java
 * Date:    05/03/2019
 * Authors: Raysean Jones-Dent, Tonye Andre Martial, Matt Mitchell, Kristine Dudley, Woo Choi, Justin Kim
 * Project: VirtualAtm
 * Course:  UMUC CMSC 495-7982
 */
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

/**
 * Security api used to perform cryptographic functions within the application.
 */
public class Security {

   /**
    * Constant for the default salt length used to generate hashes created by this api
    */
   private static final int DEFAULT_SALT_LENGTH = 32;
   
   /**
    * Constant for the default number of iterations used to generate hashes in the api
    */
   private static final int DEFAULT_HASH_ITERATIONS = 10000;
   
   /**
    * Constant for the field separator for hashes consumed and returned by the api
    */
   private static final String FIELD_SEPERATOR = "^";

   /**
    * Constants for the hash algorithm types supported by this api
    */
   private enum AlgorithmTypes {
      PBKDF2WithHmacSHA1, PBKDF2WithHmacSHA256, PBKDF2WithHmacSHA512
   }

   /**
    * Private default constructor to restrict usage of this class to static methods only
    */
   private Security() {
   }

   /**
    * Creates a hash from the provided value
    * @param value The value to hash
    * @return A string representing the hash algorithm used, hash iterations, and hash separated by a field separator
    * @throws Exception 
    */
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

   /**
    * Generates a hash from the plain text value provided and compares it to the hash provided.
    * @param hash  The existing hash
    * @param value The plain text value to hash
    * @return true/false depending upon whether the hash generated from value matches the existing hash
    * @throws Exception 
    */
   public static boolean compareHash(String hash, String value) throws Exception {
      String[] parts = hash.split(Pattern.quote(FIELD_SEPERATOR));
      if (parts.length < 3) {
         throw new Exception("Invalid hash provided!");
      }

      AlgorithmTypes alg = AlgorithmTypes.values()[Integer.parseInt(parts[0])];
      int iterations = Integer.parseInt(parts[1]);
      byte[] blob = Base64.getUrlDecoder().decode(parts[2]);
      byte[] salt = Arrays.copyOfRange(blob, 0, DEFAULT_SALT_LENGTH);
      byte[] hashedValue = deriveKey(alg, value.toCharArray(), salt, iterations);

      // although less efficient - comparisons should be on the entire hash to ensure every comparision performs takes constant time
      int accumulator = 0;
      for (int i = 0; i < hashedValue.length; i++) {
         accumulator |= blob[salt.length + i] ^ hashedValue[i];
      }
      return (accumulator == 0);
   }

   /**
    * Utility method used to derive a PBKF2 based key (hash) using the java SecretKeyFactory
    * @param alg The algorithm to use
    * @param value The value to hash
    * @param salt The salt used to create the hash
    * @param iterations The number of iterations used to create the hash
    * @return Byte array containing the key (hash)
    * @throws InvalidKeySpecException
    * @throws NoSuchAlgorithmException 
    */
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
