package com.digisoft.mma.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.net.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.digisoft.mma.util.AppConstant.EXCEPTION;

public class CryptographyUtils {
    public static boolean encryptFile(FileInputStream inputStream, String path) {

        try {
            FileOutputStream fos = new FileOutputStream(path.concat(".crypt"));
            byte[] key = (AppConstant.salt).getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            int b;
            byte[] d = new byte[1024 * 1024];
            while ((b = inputStream.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            cos.flush();
            cos.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(EXCEPTION, "FileNotFoundException" + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(EXCEPTION, "IOException" + e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.e(EXCEPTION, "NoSuchAlgorithmException" + e.getLocalizedMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(EXCEPTION, "NoSuchPaddingException" + e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            Log.e(EXCEPTION, "InvalidKeyException: " + e.getLocalizedMessage());
        } finally {
            Log.i(EXCEPTION, "FINALLY - encryptFile");
        }

        File encryptedFile = new File(path.concat(".crypt"));
        return (encryptedFile.exists());
    }

    public static String getEncryptedString(byte[] array, String pid) {
        String encryptedString = "";
        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] key = (AppConstant.salt + pid).getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            encryptedString = Base64.encodeBase64String(cipher.doFinal(array));
        } catch (NoSuchAlgorithmException e) {
            Log.e(EXCEPTION, "NoSuchAlgorithmException" + e.getLocalizedMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(EXCEPTION, "NoSuchPaddingException" + e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            Log.e(EXCEPTION, "InvalidKeyException" + e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            Log.e(EXCEPTION, "BadPaddingException" + e.getLocalizedMessage());
        } catch (IllegalBlockSizeException e) {
            Log.e(EXCEPTION, "IllegalBlockSizeException" + e.getLocalizedMessage());
        } finally {
            Log.i(EXCEPTION, "FINALLY - EncryptedString");
        }
        return encryptedString;
    }

    public static byte[] getDecodedByte(String strToDecrypt, String pid) {
        byte[] bytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES");
            byte[] key = (AppConstant.salt + pid).getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec sks = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            bytes = cipher.doFinal(Base64.decodeBase64(strToDecrypt));
        } catch (NoSuchAlgorithmException e) {
            Log.e(EXCEPTION, "NoSuchAlgorithmException" + e.getLocalizedMessage());
        } catch (NoSuchPaddingException e) {
            Log.e(EXCEPTION, "NoSuchPaddingException" + e.getLocalizedMessage());
        } catch (InvalidKeyException e) {
            Log.e(EXCEPTION, "InvalidKeyException" + e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            Log.e(EXCEPTION, "BadPaddingException" + e.getLocalizedMessage());
        } catch (IllegalBlockSizeException e) {
            Log.e(EXCEPTION, "IllegalBlockSizeException" + e.getLocalizedMessage());
        } finally {
            Log.i(EXCEPTION, "FINALLY - DecryptedString");
        }
        return bytes;
    }

    public static String getDecodedString(String strToDecrypt, String pid) {
        return new String(getDecodedByte(strToDecrypt, pid));
    }

    public static Bitmap getDecodedBitmap(String strToDecrypt, String pid) {
        byte[] bytes = getDecodedByte(strToDecrypt, pid);
        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return img;
    }

}
