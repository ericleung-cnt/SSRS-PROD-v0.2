package org.mardep.ssrs.util;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.Md5Crypt;

public class PasswordUtil {

	public static final String SALT = "$1$m";

	public static String encrypt(String rawPassword){
		if(rawPassword==null) return null;

		String result = Md5Crypt.md5Crypt(rawPassword.getBytes(), SALT);
		return result;
	}

	private static final String AES = "AES";
    private static final byte[] keyValue = "SSRS2019SSRS2019".getBytes();

    public static void main(String args[]) throws Exception {
        System.out.println(encryptAES(args.length > 0 ? args[0] : "password"));
//        System.out.println(decryptAES(encriptValue));
    }

    /**
     * @param args
     * @throws Exception
     */

    public static String encryptAES(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        byte[] encryptedByteValue = Base64.getEncoder().encode(encValue);
        String encryptedValue = new String(encryptedByteValue);

        return encryptedValue;
    }

    public static String decryptAES(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedValue.getBytes());
        byte[] enctVal = c.doFinal(decoded);
        return new String(enctVal);
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, AES);
        return key;
    }

}
