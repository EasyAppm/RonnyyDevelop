package com.easyapp.ronnyy.rpeasyapp;

import com.easyapp.cipher.AESCipher;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherApi extends AESCipher {

    public CipherApi(String sha1, String sha2) {
        super(sha2.getBytes(), sha1.getBytes());
    }

    @Override
    protected SecretKey factorySecretKey(byte[] password, String algorithm) throws NoSuchAlgorithmException {
        return new SecretKeySpec(password, algorithm);
    }

    @Override
    protected IvParameterSpec factoryIvParameterSpec(byte[] initialVector, int blockSize) throws NoSuchAlgorithmException {
        return new IvParameterSpec(initialVector);
    }
   
    public byte[] decrypt(String text) throws Exception {
        int length = text.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(text.charAt(i), 16) << 4) + Character.digit(text.charAt(i + 1), 16));
        }
        return newCipherDecryptMode().doFinal(data);
    }
    
    
}
