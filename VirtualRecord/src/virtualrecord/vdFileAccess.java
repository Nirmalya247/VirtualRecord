/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Administrator
 */
public class vdFileAccess {
    vdCode vdCode = new vdCode();
    public String path = "";
    public String data = "";
    public String password = "";
    public int charAt = -1;
    public int startFrom = 0;
    public int lengthRecent = 0;

    public vdFileAccess()
    {
        path = "";
        data = "";
        password = "";
        charAt = -1;
        startFrom = 0;
    }

    /**
     *
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public void loadAll() throws InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        data = vdCode.DecryptFile(path, password);
        charAt = -1;
        startFrom = 0;
    }
    public void loadSub(int start, int length) throws GeneralSecurityException {
        File file = new File(path);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            char line[] = new char[length];
            br.skip(start);
            br.read(line, 0, length);
            br.close();
            data = String.valueOf(line);
            data = vdCode.decrypt(data, password);
        }
        catch (IOException e) {
            data = "";
        }
        charAt = -1;
        startFrom = 0;
    }
    public void loadAllFrom(int start) throws GeneralSecurityException, IOException {
        File file = new File(path);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            br.skip(start);
            String line;
            if((line = br.readLine()) != null){
                text.append(line);
            }
            while ((line = br.readLine()) != null){
                text.append('\n');
                text.append(line);
            }
            data = text.toString();
            data = vdCode.decrypt(data, password);
        }
        catch (IOException e) {
            data = "";
        }
        charAt = -1;
        startFrom = 0;
    }

    public String decryptSub(int start, int length) throws GeneralSecurityException, IOException {
        return vdCode.decrypt(data.substring(start, start+length), password);
    }
    public int decryptIntSub(int start, int length) throws UnsupportedEncodingException {
        return getIntFromStrng(data.substring(start, start+length));
    }
    public String decryptPropertySub(int start, int length) throws UnsupportedEncodingException {
        return getBinaryFromInt(getIntFromStrng(data.substring(start, start+length)),8);
    }

    public String decryptSub(int length) throws GeneralSecurityException, IOException {
        lengthRecent = length;
        return vdCode.decrypt(data.substring(startFrom, startFrom+length), password);
    }
    public int decryptIntSub(int length) throws UnsupportedEncodingException {
        lengthRecent = length;
        return getIntFromStrng(data.substring(startFrom, startFrom+length));
    }
    public String decryptPropertySub(int length) throws UnsupportedEncodingException {
        lengthRecent = length;
        return getBinaryFromInt(getIntFromStrng(data.substring(startFrom, startFrom+length)), 8);
    }

    public void goNext()
    {
        startFrom += lengthRecent;
    }

    public void save(String data, String fileName)
    {
        try {
            File myFile = new File(new File(path).getParent() + "\\" + fileName + ".vdvs");

            myFile.createNewFile();


            FileWriter writer = new FileWriter(myFile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
        }
    }
    public void append(String data, String fileName)
    {
        try {
            File myFile = new File(new File(path).getParent() + "\\" + fileName + ".vdvs");

            FileWriter writer = new FileWriter(myFile, true);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
        }
    }



    public String getStringFromInt(int value, int length) throws UnsupportedEncodingException
    {
        byte bytes[];
        if(length == 1)
        {
            bytes = new byte[]{(byte)value};
        }
        else
        {
            bytes = new byte[length];
            ByteBuffer buffer = ByteBuffer.allocate(4); // in java, int takes 4 bytes.
            buffer.order(LITTLE_ENDIAN);
            bytes = buffer.putInt(value).array();
        }

        return new String(bytes, "ISO-8859-1");
    }
    public int getIntFromStrng(String value) throws UnsupportedEncodingException
    {
        if(vdDataTypes.isNull(value))return 0;
        byte bytes[] = value.getBytes("ISO-8859-1");
        if(bytes.length == 1)
        {
            return bytes[0] & 0xFF;
        }
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    public byte[] getBytesFromInt(int value, int length)
    {
        byte bytes[];
        if(length == 1)
        {
            bytes = new byte[]{(byte)value};
        }
        else
        {
            bytes = new byte[length];
            ByteBuffer buffer = ByteBuffer.allocate(4); // in java, int takes 4 bytes.
            buffer.order(LITTLE_ENDIAN);
            bytes = buffer.putInt(value).array();
        }

        return bytes;
    }
    public int getIntFromBytes(byte[] bytes)
    {
        if(bytes == null)return 0;
        if(bytes.length == 1)
        {
            return bytes[0] & 0xFF;
        }
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(LITTLE_ENDIAN);
        return wrapped.getInt();
    }

    public int getIntFromBinary(String binary)
    {
        int tInt = 0;
        int n = binary.length();
        for (int i = 0; i < binary.length(); i++)
        {
            n--;
            if (binary.charAt(i) == '1') tInt += (int)Math.pow(2, n);
        }
        return tInt;
    }
    public String getBinaryFromInt(int value, int length)
    {
        StringBuilder result = new StringBuilder();
        for(int i = length; i >= 0 ; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        result.replace(0, 1, "");
        return result.toString();
    }

    public String generateName()
    {
        String tReturn = "";
        do
        {
            for (int i = 0; i < 31; i++)
            {
                tReturn += Integer.toString((int)(Math.random()*10));
            }
        } while (new File(new File(path).getParent() + "\\" + tReturn + ".vdvs").exists());

        return tReturn;
    }
    public String generatePass()
    {
        String tReturn = "";
        String tNameCode = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        for (int i = 0; i < 31; i++)
        {
            tReturn += tNameCode.charAt((int)(Math.random()*61));
        }
        return tReturn;
    }
}
