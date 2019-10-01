/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class vdStringCompare {
    public static int isInArray(String str, String[] arr, int stp, int pos)
    {
        for(int i = 0; i < arr.length; i += stp)
        {
            if (arr[i + pos].equals(str)) return (i / stp) + 1;
        }
        return 0;
    }
    public static int isInString(String str,int strStart,String[] arr,int stp,int pos)
    {
        for(int i =0;i<arr.length; i+=stp)
        {
            try
            {
                if (str.substring(strStart, strStart+arr[i + pos].length()).equals(arr[i + pos])) return (i / stp) + 1;
            }
            catch(Exception e) {}
        }
        return 0;
    }
    public static String[] takeStepSeparator(String command,int seperatorNo)
    {
        String stepT = vdParser.seperator[((seperatorNo - 1) * 2) + 1];
        if (stepT.equals("1"))
        {
            command = command.substring(vdParser.seperator[(seperatorNo - 1) * 2].length());
            return new String[]{ command,null};
        }
        if (stepT.equals("2"))
        {
            command = command.substring(vdParser.seperator[(seperatorNo - 1) * 2].length());
            return new String[] { command, vdParser.seperator[(seperatorNo - 1) * 2] };
        }
        if (stepT.equals("3"))
        {
            String temp = "'";
            command = command.substring(1);
            while (command != null && !command.isEmpty())
            {
                if(command.charAt(0)=='\''&& temp.charAt(temp.length()-1) != '\\')
                {
                    temp += "'";
                    command = command.substring(1);
                    return new String[] { command, temp };
                }
                else
                {
                    temp += command.charAt(0);
                    command = command.substring(1);
                }
            }

            return new String[] { command, temp };
        }
        return new String[] { null, null };
    }
    public static String[] getUntilSeparator(String command)
    {
        String temp = "";

        while(command != null && !command.isEmpty())
        {
            if(isInString(command,0,vdParser.seperator,2,0)!=0)
            {
                return new String[] { command, temp };
            }
            else
            {
                temp += command.charAt(0);
                command = command.substring(1);
            }
        }
        return new String[] { command, temp };
    }




    public static String getStringFromInt(int value, int length) throws UnsupportedEncodingException
    {
        byte bytes[];
        if(length == 1)
        {
            bytes = new byte[]{(byte)value};
        }
        else
        {
            bytes = new byte[length];
            ByteBuffer buffer = ByteBuffer.allocate(length); // in java, int takes 4 bytes.
            buffer.order(LITTLE_ENDIAN);
            bytes = buffer.putInt(value).array();
        }

        return new String(bytes, "ISO-8859-1");
    }
    public static int getIntFromStrng(String value) throws UnsupportedEncodingException
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
    public static String getStringFromInt(double value, int length) throws UnsupportedEncodingException
    {
        byte bytes[];
        bytes = new byte[length];
        ByteBuffer buffer = ByteBuffer.allocate(length); // in java, int takes 4 bytes.
        buffer.order(LITTLE_ENDIAN);
        bytes = buffer.putDouble(value).array();

        return new String(bytes, "ISO-8859-1");
    }
    public static double getDoubleFromStrng(String value) throws UnsupportedEncodingException
    {
        if(vdDataTypes.isNull(value))return 0;
        byte bytes[] = value.getBytes("ISO-8859-1");
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(LITTLE_ENDIAN);
        return wrapped.getDouble();
    }
    public static String getStringFromDate(String value) throws UnsupportedEncodingException {
        String tDate = "";
        tDate += getStringFromInt(Integer.parseInt(value.substring(0,4)), 4);
        tDate += getStringFromInt(Integer.parseInt(value.substring(5,7)), 1);
        tDate += getStringFromInt(Integer.parseInt(value.substring(8,10)), 1);
        tDate += getStringFromInt(Integer.parseInt(value.substring(11,13)), 1);
        tDate += getStringFromInt(Integer.parseInt(value.substring(14,16)), 1);
        tDate += getStringFromInt(Integer.parseInt(value.substring(17,19)), 1);
        return tDate;
    }
    public static Date getDateFromStrng(String value) throws UnsupportedEncodingException, ParseException {
        String tDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (vdDataTypes.isNull(value)) tDate = "0001-01-01 00:00:00";
        else
        {
            String tYear = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(0, 4))), 4, '0');
            String tMonth = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(4, 5))), 2, '0');
            String tDay = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(5, 6))), 2, '0');
            String tHour = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(6, 7))), 2, '0');
            String tMin = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(7, 8))), 2, '0');
            String tSec = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(8, 9))), 2, '0');
            tDate = tYear + "-" + tMonth + "-" + tDay + " " + tHour + ":" + tMin + ":" + tSec;
        }
        return sdf.parse(tDate);
    }
    public static String getDateStringFromStrng(String value) throws UnsupportedEncodingException, ParseException {
        String tDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (vdDataTypes.isNull(value)) tDate = "0001-01-01 00:00:00";
        else
        {
            String tYear = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(0, 4))), 4, '0');
            String tMonth = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(4, 5))), 2, '0');
            String tDay = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(5, 6))), 2, '0');
            String tHour = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(6, 7))), 2, '0');
            String tMin = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(7, 8))), 2, '0');
            String tSec = vdDataTypes.padLeft(Integer.toString(getIntFromStrng(value.substring(8, 9))), 2, '0');
            tDate = tYear + "-" + tMonth + "-" + tDay + " " + tHour + ":" + tMin + ":" + tSec;
        }
        return tDate;
    }





    public static Date getDateTimeFromDouble(double OADate)
    {
        long num = (long) ((OADate * 86400000.0) + ((OADate >= 0.0) ? 0.5 : -0.5));
        if (num < 0L) {
            num -= (num % 0x5265c00L) * 2L;
        }
        num += 0x3680b5e1fc00L;
        num -=  62135596800000L;

        return new Date(num);
    }
    public static int getIntFromBinary(String binary)
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
    public static String getBinaryFromInt(int value, int length)
    {
        StringBuilder result = new StringBuilder();
        for(int i = length; i >= 0 ; i--) {
            int mask = 1 << i;
            result.append((value & mask) != 0 ? "1" : "0");
        }
        result.replace(0, 1, "");
        return result.toString();
    }

    public static String generateName(String path,String extension)
    {
        String tReturn = "";
        do
        {
            for (int i = 0; i < 31; i++)
            {
                tReturn += Integer.toString((int)(Math.random()*10));
            }
        } while (new File(path + tReturn + "." + extension).exists());

        return tReturn;
    }
    public static String generatePass()
    {
        String tReturn = "";
        String tNameCode = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        for (int i = 0; i < 31; i++)
        {
            tReturn += tNameCode.charAt((int)(Math.random()*61));
        }
        return tReturn;
    }
    public static void save(String path,String data)
    {
        try {
            File myFile = new File(path);

            myFile.createNewFile();


            FileWriter writer = new FileWriter(myFile);
            writer.append(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
        }
    }
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
    public static void deleteRecursive(String path) {
        File fileOrDirectory = new File(path);
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
    public static void deleteFile(File myFile) {
        if (myFile.isFile()) {
            myFile.delete();
        }
    }
    public static void deleteFile(String path) {
        File myFile = new File(path);
        if (myFile.isFile()) {
            myFile.delete();
        }
    }
}
