/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import static virtualrecord.vdStringCompare.getIntFromStrng;

/**
 *
 * @author Administrator
 */
public class vdDataTypes {
    //1 = int 2=varchar 3=tinyint 4=null 5=datetime
    public static String padRight(String value, int length, char charToPad){
        while(value.length() < length)
        {
            value += charToPad;
        }
        return value;
    }
    public static String padLeft(String value, int length, char charToPad){
        while(value.length() < length)
        {
            value = charToPad + value;
        }
        return value;
    }
    public static String trimEnd(String value, char charToPad){
        if(value.charAt(0)=='\0') return "";
        if(charToPad == '\0'){
            return value.replaceAll("\0", "");
        }
        else{
            int n = 0;
            int len = value.length() - 1;
            while(value.charAt(len) == charToPad)
            {
                n++;
                len--;
            }
            return value.substring(0, value.length() - n);
        }
    }
    static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    static boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    static boolean tryParseDateTime(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static int isSpecialFunc(String value)
    {
        if(value.equals("time_now"))return 5;
        if(value.equals("time_now_utc"))return 5;
        return 0;
    }
    public static String getSpecialFuncData(String value)
    {
        if(value.equals("time_now"))return "'"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())+"'";
        if(value.equals("time_now_utc"))
        {
            SimpleDateFormat tTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            tTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            return "'"+ tTime.format(new java.util.Date())+"'";
        }
        return "";
    }

    public static int getDataTypeNo(String value){
        if (value.equals("int")) return 1;
        else if (value.equals("varchar")) return 2;
        else if (value.equals("tinyint")) return 3;
        else if (value.equals("null")) return 4;
        else if (value.equals("datetime")) return 5;
        else return 0;
    }
    public static String  getDataTypeString(int value){
        if (value == 1) return "int";
        else if (value == 2) return "varchar";
        else if (value == 3) return "tinyint";
        else if (value == 4) return "null";
        else if (value == 5) return "datetime";
        else return "";
    }
    public static int isRawData(String value){
        int tempN = 0;
        if (isSpecialFunc(value) > 0) return isSpecialFunc(value);
        if (tryParseInt(value))
        {
            if(Integer.parseInt(value) < 256) return 3;
            return 1;
        }
        else if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'')
        {
            if(tryParseDateTime(value))return 5;
            return 2;
        }
        else if (value.equals("null")) return 4;

        return 0;
    }
    public static int lengthRawData(String value, int type){
        if (isSpecialFunc(value) > 0) value = getSpecialFuncData(value);
        if (type == 1) return 4;
        else if (type == 2) return value.length() - 2;
        else if (type == 3) return 1;
        else if (type == 5) return 9;
        return 0;
    }

    public static String getEncryptedRawData(String value, int type) throws UnsupportedEncodingException, ParseException {
        if (type == 1) return vdStringCompare.getStringFromInt(Integer.parseInt(value), 4);
        else if (type == 2) return value.substring(1, value.length() - 1);
        else if (type == 3) return vdStringCompare.getStringFromInt(Integer.parseInt(value), 1);
        else if (type == 5)
        {
            return vdStringCompare.getStringFromDate(value.substring(1, value.length() - 1));
        }
        return "";
    }
    public static String getEncryptedRawData(String value, int type, int length) throws UnsupportedEncodingException, ParseException {
        if (type == 1) return vdStringCompare.getStringFromInt(Integer.parseInt(value), 4);
        else if (type == 2)
        {
            value = value.substring(1, value.length() - 1);
            if (value.length() > length) value = value.substring(0, length);
            else if(value.length() < length) value = value + new String(new char[length - value.length()]);
            return value;
        }
        else if (type == 3) return vdStringCompare.getStringFromInt(Integer.parseInt(value), 1);
        else if (type == 4)
        {
            value = "";
            value = new String(new char[length]);
            return value;
        }
        else if (type == 5)
        {
            value = value.substring(1, value.length() - 1);
            return vdStringCompare.getStringFromDate(value);
        }
        return "";
    }
    public static String getDecryptedRawData(String value, int type) throws UnsupportedEncodingException, ParseException {
        if (type == 1) return Integer.toString(getIntFromStrng(value));
        else if (type == 2)return "'" + value + "'";
        else if (type == 3) return Integer.toString(getIntFromStrng(value));
        else if (type == 4) return "null";
        else if (type == 5) return "'" + vdStringCompare.getDateStringFromStrng(value) + "'";
        return "";
    }
    public static String getDecryptedRawDataForShow(String value, int type) throws UnsupportedEncodingException, ParseException {
        if (type == 1) return Integer.toString(getIntFromStrng(value));
        else if (type == 2) return trimEnd(value, '\0');
        else if (type == 3) return Integer.toString(getIntFromStrng(value));
        else if (type == 4) return "null";
        else if (type == 5) return vdStringCompare.getDateStringFromStrng(value);
        return "";
    }

    public static boolean isType(String value, int type, int length){
        int tempN = 0;
        //data to type
        if (type == 1 && tryParseInt(value)) return true;
        else if (type == 2 && value.length() <= length + 2) return true;
        else if (type == 3 && tryParseInt(value) && Integer.parseInt(value) < 256) return true;
        else if (type == 4 && value == "null") return true;
        else if (type == 5 && tryParseDateTime(value.substring(1, value.length() - 1))) return true;
        return false;
    }
    public static boolean isNull(String value){
        for(int i = 0; i < value.length(); i++)
        {
            if(value.charAt(i) != '\0')return false;
        }
        return true;
    }
}
