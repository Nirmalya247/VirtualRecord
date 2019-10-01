/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.util.Base64;
/**
 *
 * @author Administrator
 */
public class vdCode {
    private final String characterEncoding = "UTF-8";
    private final String cipherTransformation = "AES/CBC/PKCS5Padding";
    private final String aesEncryptionAlgorithm = "AES";

    public byte[] decrypt(byte[] cipherText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpecy = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }
    public byte[] encrypt(byte[] plainText, byte[] key, byte [] initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }
    private byte[] getKeyBytes(String key) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[16];
        byte[] parameterKeyBytes= key.getBytes(characterEncoding);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, Math.min(parameterKeyBytes.length, keyBytes.length));
        return keyBytes;
    }
    public String encrypt(String plainText, String key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
        byte[] plainTextbytes = plainText.getBytes(characterEncoding);
        byte[] keyBytes = getKeyBytes(key);
        String tEncryp = Base64.getEncoder().encodeToString(encrypt(plainTextbytes,keyBytes, keyBytes));
        return tEncryp;
    }
    public String decrypt(String encryptedText, String key) throws KeyException, GeneralSecurityException, GeneralSecurityException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] cipheredBytes = Base64.getDecoder().decode(encryptedText);
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
    }

    public void EncryptFile(String path, String plainText, String key) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException {
        String encryptedText = "";
        if (plainText != "")
        {
            byte[] plainTextbytes = plainText.getBytes(characterEncoding);
            byte[] keyBytes = getKeyBytes(key);
            encryptedText = Base64.getEncoder().encodeToString(encrypt(plainTextbytes,keyBytes, keyBytes));
        }

        try {
            File myFile = new File(path);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(encryptedText);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public String DecryptFile(String path, String key) throws UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String encryptedText = "";
        File file = new File(path);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            if((line = br.readLine()) != null){
                text.append(line);
            }
            while ((line = br.readLine()) != null) {
                text.append('\n');
                text.append(line);
            }
            br.close();
            encryptedText = text.toString();
        }
        catch (IOException e) {
        }

        if (encryptedText.equals("")) return "";
        byte[] cipheredBytes = Base64.getDecoder().decode(encryptedText);
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(cipheredBytes, keyBytes, keyBytes), characterEncoding);
    }
    public String DecryptByte(byte[] encryptedBytes, String key) throws UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] keyBytes = getKeyBytes(key);
        return new String(decrypt(encryptedBytes, keyBytes, keyBytes), characterEncoding);
    }

    public static int expectedEncodLength(String realStr) throws UnsupportedEncodingException {
        int realLength = URLEncoder.encode(realStr, "UTF-8").length();
        int n = (realLength / 16);
        int t = ((n + 1) * 24);
        if (n != 0)
        {
            t -= ((n % 3) == 1 || (n % 3) == 2) ? ((n / 3) * 2 + (n % 3)) * 4 : ((n / 3) * 2) * 4;
        }
        return t;
    }

    public static String encryptMy(String line, String pass){
        byte[] passBytes = pass.getBytes();
        byte[] lineBytes = line.getBytes();
        byte[] encryptedBytes = new byte[line.length()];

        for(int i = 0; i < lineBytes.length; i++){
            encryptedBytes[i] = (byte) ((lineBytes[i] + passBytes[i % passBytes.length]) % 256);
        }

        return encryptedBytes.toString();
    }
    public static String decryptMy(String line, String pass){
        byte[] passBytes = pass.getBytes();
        byte[] lineBytes = line.getBytes();
        byte[] decryptedBytes = new byte[line.length()];
        int t = 0;

        for(int i = 0; i < lineBytes.length; i++){
            t = lineBytes[i] - passBytes[i % passBytes.length];
            if(t < 0){
                t = 256 - t;
            }
            decryptedBytes[i] = (byte)t;
        }

        return decryptedBytes.toString();
    }
}
