/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Administrator
 */
public class vdTableData {
    vdCode vdCode = new vdCode();
    public String encription;
    public String name;
    public String fileName;
    public int columnCount;
    public int tableNo;
    public int tableType;
    public int minID;
    public int maxID;
    public String dataFileFakeName;
    public String dataFilePass;

    public vdColumnInfo[] columns;
    public boolean isNew = false;
    public String data = "";
    public int rowLength = 0;
    public int rowCount = 0;
    
    public myMultyData dataMy = new myMultyData();

    public vdTableData()
    {
        name = "";
        columnCount = 0;
        columns = null;
        isNew = true;
    }
    public vdTableData(String encrip, String nam, String fNam, int colCount, int tablNo, int tableTyp, int minid, int maxid, String dataFileFakeN, String dataFileP, boolean isN)
    {
        encription = encrip;
        name = nam;
        fileName = fNam;
        columnCount = colCount;
        tableNo = tablNo;
        columns = null;
        tableType = tableTyp;
        minID = minid;
        maxID = maxid;
        dataFileFakeName = dataFileFakeN;
        dataFilePass = dataFileP;
        isNew = isN;
    }
    public vdTableData(String nam, int colCount, boolean isN)
    {
        name = nam;
        columnCount = colCount;
        columns = null;
        isNew = isN;
    }
    public vdTableData(vdTableInfo value)
    {
        encription = value.encription;
        name = value.name;
        fileName = value.fileName;
        columnCount = value.columnCount;
        tableNo = value.tableNo;
        tableType = value.tableType;
        minID = value.minID;
        maxID = value.maxID;
        dataFileFakeName = value.dataFileFakeName;
        dataFilePass = value.dataFilePass;
        columns = value.columns;
        isNew = value.isNew;
        rowLength = value.rowLength;
    }

    public void addColumnInfo(vdColumnInfo value)
    {
        int len;
        if (columns == null) len = 0; else len = columns.length;
        if(len == 0) columns = new vdColumnInfo[1]; else columns = Arrays.copyOf(columns, len + 1);
        columns[len] = value;
        columns[len].columStart = rowLength;
        rowLength += columns[len].length;
    }
    public void loadDataFromFile(String path) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        vdFileAccess files = new vdFileAccess();
        if(tableType == 1) {
            files.path = path + fileName + ".vdvs";
            files.password = encription;
        }
        else {
            files.path = path + dataFileFakeName + ".vdvs";
            files.password = dataFilePass;
        }
        files.loadAll();
        data = files.data;
        if(tableType == 1) {
            rowCount = data.length() / rowLength;
        }
        else {
            rowCount = data.length() / 12;
        }
    }

    public int getColumnNoByName(String columnName)
    {
        for (int i = 0; i < columns.length; i++) { if (columns[i].name.equals(columnName) || (columns[i].tableName + "." + columns[i].name).equals(columnName)) { return i; } }
        return 0;
    }
    public String getData(int row, int column)
    {
        return data.substring(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart+columns[column].length);
    }
    public String getData(int row, String columnName)
    {
        int column = getColumnNoByName(columnName);
        return data.substring(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart+columns[column].length);
    }

    public String getDataDecrypted(int row, int column) throws UnsupportedEncodingException, ParseException {
        String temp = data.substring(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart+columns[column].length);
        return vdDataTypes.getDecryptedRawData(temp, columns[column].type);
    }
    public String getDataDecrypted(int row, String columnName) throws UnsupportedEncodingException, ParseException {
        int column = getColumnNoByName(columnName);
        String temp = data.substring(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart+columns[column].length);
        return vdDataTypes.getDecryptedRawData(temp, columns[column].type);
    }
    public String getDataDecryptedForShow(int row, int column) throws UnsupportedEncodingException, ParseException {
        String temp = data.substring(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart+columns[column].length);
        return vdDataTypes.getDecryptedRawDataForShow(temp, columns[column].type);
    }
    public void removeRow(int rowNo)
    {
        rowCount--;
        StringBuffer text = new StringBuffer(data);
        if(tableType == 1){
            text.replace( rowNo * rowLength, rowNo * rowLength + rowLength, "");
        }
        else{
            text.replace(rowNo * 12, (rowNo * 12) + 12, "");
        }
        data = text.toString();
    }
    public void updateCell(String value, int row, String columnName) throws UnsupportedEncodingException, ParseException {
        int column = getColumnNoByName(columnName);
        StringBuffer text = new StringBuffer(data);
        if(vdDataTypes.isSpecialFunc(value)>0)value = vdDataTypes.getSpecialFuncData(value);
        text.replace(rowLength * row + columns[column].columStart, rowLength * row + columns[column].columStart + columns[column].length, vdDataTypes.getEncryptedRawData(value, columns[column].type, columns[column].length));
        data = text.toString();
    }
    public void addRow(String value) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if(tableType == 1){
            data += value;
        }
        rowCount++;
    }
    public void addRow(String value, String path) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if(tableType == 2){
            try {
                String tEncrypted = vdCode.encrypt(value, encription);
                File myFile = new File(path + fileName + ".vdvs");
                int tStartPos = (int)myFile.length(); int tEndPos = tStartPos + tEncrypted.length();

                FileWriter writer = new FileWriter(myFile, true);
                writer.append(tEncrypted);
                writer.flush();
                writer.close();
                
                data += value.substring(0, 4) + vdStringCompare.getStringFromInt(tStartPos, 4) + vdStringCompare.getStringFromInt(tEndPos, 4);
                vdCode.EncryptFile(path + dataFileFakeName + ".vdvs", data, dataFilePass);
            } catch (Exception e) {}
            rowCount++;
        }
    }
    public myMultyData getDataFormatted() throws UnsupportedEncodingException, ParseException {
        myMultyData temp = new myMultyData();
        for(int row = 0; row < rowCount; row++)
        {
            for (int column = 0; column < columnCount; column++)
            {
                temp.add(getDataDecryptedForShow(row, column));
            }
        }
        return temp;
    }
    public void saveTable(String path) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        if(tableType == 1){
            vdCode.EncryptFile(path + fileName + ".vdvs", data, encription);
        }
    }



    //data to type (can compare -> int < > <= >= != = ; String != = == < >; datetime = < > <= >= ; boolean = )
    public boolean compareData(int row, String columnName, String value2, String comapeType) throws UnsupportedEncodingException, ParseException {
        int column = getColumnNoByName(columnName);
        return compareData(row, column, value2, comapeType);
    }
    public boolean compareData(int row, int column, String value2, String comapeType) throws UnsupportedEncodingException, ParseException {
        String value1 = getData(row, column);
        if (vdDataTypes.isNull(value1) && comapeType.equals("is") && value2.equals("null")) return true;//check for is null

        if (!vdDataTypes.isType(value2, columns[column].type, columns[column].length)) return false;//if values dont match
        int type = columns[column].type;

        if (value2 == null || value2.equals("")) return false;//if one value null
        if (vdDataTypes.isNull(value1) && type != 1 && type != 3) return false;//if one value null
        if (type == 1)
        {
            int v1 = vdStringCompare.getIntFromStrng(value1);
            int v2 = Integer.parseInt(value2);
            if (comapeType.equals("=")) return v1 == v2;
            else if (comapeType.equals("<")) return v1 < v2;
            else if (comapeType.equals(">")) return v1 > v2;
            else if (comapeType.equals("<=")) return v1 <= v2;
            else if (comapeType.equals(">=")) return v1 >= v2;
            else if (comapeType.equals("!=")) return v1 != v2;
        }
        else if (type == 2)
        {
            String v1 = vdDataTypes.trimEnd(value1, '\0');
            String v2 = vdDataTypes.trimEnd(value2, '\0');
            if (v2.charAt(0) == '\'' && v2.charAt(v2.length() - 1) == '\'') { v2 = v2.substring(1, v2.length() - 1); v2.replace("\\'", "'"); }

            if (comapeType.equals("==")) return v1.equals(v2);
            else if (comapeType.equals("<")) return v1.compareToIgnoreCase(v2) < 0;
            else if (comapeType .equals(">")) return v1.compareToIgnoreCase(v2) > 0;
            else if (comapeType.equals("=")) return v1.equalsIgnoreCase(v2);
            else if (comapeType.equals("!=")) return !v1.equalsIgnoreCase(v2);
        }
        else if (type == 3)
        {
            int v1 = vdStringCompare.getIntFromStrng(value1);
            int v2 = Integer.parseInt(value2);
            if (comapeType.equals("=")) return v1 == v2;
            else if (comapeType.equals("<")) return v1 < v2;
            else if (comapeType.equals(">")) return v1 > v2;
            else if (comapeType.equals("<=")) return v1 <= v2;
            else if (comapeType.equals(">=")) return v1 >= v2;
            else if (comapeType.equals("!=")) return v1 != v2;
        }
        else if (type == 5)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date v1 = vdStringCompare.getDateFromStrng(value1);
            String tV1S= vdStringCompare.getDateStringFromStrng(value1);
            if (value2.charAt(0) == '\'' && value2.charAt(value2.length() - 1) == '\'') { value2 = value2.substring(1, value2.length() - 1);}
            Date v2 = sdf.parse(value2);

            if (comapeType.equals("=")) return v1.compareTo(v2) == 0;
            else if (comapeType.equals("<")) return v1.compareTo(v2) < 0;
            else if (comapeType.equals(">")) return v1.compareTo(v2) > 0;
            else if (comapeType.equals("<=")) return v1.compareTo(v2) <= 0;
            else if (comapeType.equals(">=")) return v1.compareTo(v2) >= 0;
            else if (comapeType.equals("!=")) return v1.compareTo(v2) != 0;
        }
        return false;
    }

    public int getMyDataIDLoc(int idToSearch){
        for(int i = 0; i < rowCount; i++){
            try {
                if(vdStringCompare.getIntFromStrng(data.substring(i * 11, (i * 11) + 4)) == idToSearch) return i;
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(vdTableData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }
    public void getMyData(int row1, int row2, String path) throws UnsupportedEncodingException, GeneralSecurityException, KeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, ParseException{
        int tLen = row2 - row1 + 1;
        int tStart = 0;
        int[] starts = new int[tLen], ends = new int[tLen];
        String[] datas = new String[columnCount * tLen];
        
        tStart = vdStringCompare.getIntFromStrng(data.substring((row1 * 12) + 4, (row1 * 12) + 8));
        for(int i = row1; i <= row2; i++){
            starts[i - row1] = vdStringCompare.getIntFromStrng(data.substring((i * 12) + 4, (i * 12) + 8)) - tStart;
            ends[i - row1] = vdStringCompare.getIntFromStrng(data.substring((i * 12) + 8, (i * 12) + 12)) - tStart;
        }
        String tData = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path + fileName + ".vdvs"));
            br.skip(tStart);
            
            char[] nChar = new char[ends[tLen - 1]];
            br.read(nChar, 0, ends[tLen - 1]);
            tData = String.valueOf(nChar);
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(vdTableData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(vdTableData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(int row = 0; row < tLen; row++){
            String tDataRow = vdCode.decrypt(tData.substring(starts[row], ends[row]), encription);
            String temp = "";
            int column = 0;
            for(column = 0; column < columnCount - 1; column++){
                temp = tDataRow.substring(columns[column].columStart, columns[column].columStart+columns[column].length);
                datas[(row * columnCount) + column] = vdDataTypes.getDecryptedRawData(temp, columns[column].type);
            }
            column = columnCount - 1;
            datas[(row * columnCount) + column] = tDataRow.substring(columns[column].columStart);
        }
        rowCount = tLen;
        dataMy = new myMultyData(datas);
    }
}
