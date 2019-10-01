/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import virtualrecord.myMultyData;
/**
 *
 * @author Administrator
 */
public class vdConnection {
    vdCode vdCode = new vdCode();
    public vdTableData[] tableData = null;
    vdParser parser = null;
    vdFileAccess files = new vdFileAccess();
    public String dataPath = "";
    String mainPass = vdCode.decrypt("RBqQ9a/VUX9AJ8A1FWE1YG0ZnlJTNKxwdmxidZisDu0=", "Nirmalya 18 147");

    public int version = 0;
    public String pass = "";
    public vdTableInfo[] tables = null;
    public int tableCount = 0;

    public boolean dataBaseExist = false;

    public vdConnection(String path, String pass) throws GeneralSecurityException, IOException {
        mainPass = pass;
        openConnection(path);
    }

    public vdConnection(String path) throws GeneralSecurityException, IOException {
        openConnection(path);
    }

    public void openConnection(String path) throws GeneralSecurityException, IOException {
        if(!new File(path + "0000000000000000000000000000000.vdvs").exists())
        {
            dataBaseExist = false;
            dataPath = path;
            files.path = path + "0000000000000000000000000000000.vdvs";
            version = 1;
            tableCount = 0;
            pass = "";
            return;
        }
        dataBaseExist = true;

        dataPath = path;
        files.path = path + "0000000000000000000000000000000.vdvs";
        //varsion
        files.password = mainPass;
        files.loadSub(0, 24);
        version = files.getIntFromStrng(files.data.substring(0, 4));
        //pass
        files.password = mainPass;
        files.loadSub(24, 44);
        pass = files.data;
        //load rest
        files.password = pass;
        files.loadAllFrom(68);
        //decrypt table info
        files.password = mainPass;

        tableCount = files.decryptIntSub(4);
        files.goNext();

        tables = new vdTableInfo[tableCount];

        for (int iTable = 0; iTable < tableCount; iTable++) {
            addTable(iTable);
            for (int iColumn = 0; iColumn < tables[iTable].columnCount; iColumn++) {
                addColumn(iTable, iColumn);
            }
        }
    }
    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    void saveVDInfo() throws UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String tempData = "";
        String recentData = "";
        //varsion
        recentData = vdStringCompare.getStringFromInt(1, 4);
        tempData += vdCode.encrypt(recentData, mainPass);
        //pass
        recentData = pass;
        tempData += vdCode.encrypt(recentData, mainPass);
        //table info
        recentData = vdStringCompare.getStringFromInt(tableCount, 4);
        for (int iTable = 0; iTable < tableCount; iTable++) {
            recentData += vdCode.encrypt(tables[iTable].encription, mainPass);
            recentData += vdCode.encrypt(vdDataTypes.padRight(tables[iTable].name, 31, ' '), mainPass);
            recentData += vdCode.encrypt(tables[iTable].fileName, mainPass);
            recentData += vdStringCompare.getStringFromInt(tables[iTable].columnCount, 4);
            recentData += vdStringCompare.getStringFromInt(tables[iTable].tableType, 1);
            if(tables[iTable].tableType == 2){
                recentData += vdStringCompare.getStringFromInt(tables[iTable].minID, 4);
                recentData += vdStringCompare.getStringFromInt(tables[iTable].maxID, 4);
                recentData += vdCode.encrypt(tables[iTable].dataFileFakeName, mainPass);
                recentData += vdCode.encrypt(tables[iTable].dataFilePass, mainPass);
            }

            for (int iColumn = 0; iColumn < tables[iTable].columnCount; iColumn++) {
                recentData += vdCode.encrypt(vdDataTypes.padRight(tables[iTable].columns[iColumn].name, 31, ' '), mainPass);
                recentData += vdStringCompare.getStringFromInt(tables[iTable].columns[iColumn].type, 1);
                recentData += vdStringCompare.getStringFromInt(tables[iTable].columns[iColumn].length, 4);
                recentData += vdStringCompare.getStringFromInt(vdStringCompare.getIntFromBinary(tables[iTable].columns[iColumn].getPopertyString()), 1);
            }
        }
        tempData += vdCode.encrypt(recentData, pass);
        files.save(tempData, "0000000000000000000000000000000");
    }

    void addTable(int tableNo) throws GeneralSecurityException, IOException {
        String encription = files.decryptSub(44);
        files.goNext();
        String name = vdDataTypes.trimEnd(files.decryptSub(44), ' ');
        files.goNext();
        String fileName = files.decryptSub(44);
        files.goNext();
        int columnCount = files.decryptIntSub(4);
        files.goNext();
        int tableType = files.decryptIntSub(1);
        files.goNext();
        int tableMinID = 0;
        int tableMaxID = 0;
        String dataFileFakeName = "";
        String dataFilePass = "";
        if(tableType == 2){
            tableMinID = files.decryptIntSub(4);
            files.goNext();
            tableMaxID = files.decryptIntSub(4);
            files.goNext();
            dataFileFakeName = files.decryptSub(44);
            files.goNext();
            dataFilePass = files.decryptSub(44);
            files.goNext();
        }

        tables[tableNo] = new vdTableInfo(encription, name, fileName, columnCount, tableNo, tableType, tableMinID, tableMaxID, dataFileFakeName, dataFilePass, false);
    }

    void addColumn(int tableNo, int colNo) throws GeneralSecurityException, IOException {
        String name = vdDataTypes.trimEnd(files.decryptSub(44), ' ');
        files.goNext();
        int type = files.decryptIntSub(1);
        files.goNext();
        int length = files.decryptIntSub(4);
        files.goNext();
        String property = files.decryptPropertySub(1);
        files.goNext();
        int columNo = colNo;

        vdColumnInfo tColumnInfo = new vdColumnInfo(tables[tableNo].name, name, type, length, property, columNo);
        tables[tableNo].addColumnInfo(tColumnInfo);
    }

    void addTableData(vdTableInfo value) {
        if (value != null) {
            int len;
            if (tableData == null) len = 0; else len = tableData.length;
            if(len == 0) tableData = new vdTableData[1]; else tableData = Arrays.copyOf(tableData, len + 1);
            vdTableData temp = new vdTableData(value);
            tableData[len] = temp;
        }
    }


    public void loadDataFromFile(int tableNo) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        tableData[tableNo].loadDataFromFile(dataPath);
    }

    public void loadDataFromFile(String tableName) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        int tableNo = -1;
        int tableDataNo = -1;
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].name.equals(tableName)) {
                tableNo = i;
                break;
            }
        }
        if (tableData != null) for (int i = 0; i < tableData.length; i++) {
            if (tableData[i].name.equals(tableName)) {
                tableDataNo = i;
                break;
            }
        }
        if (tableNo > -1) {
            if (tableDataNo > -1) {
                tableData[tableDataNo].loadDataFromFile(dataPath);
            } else {
                addTableData(tables[tableNo]);
                tableData[tableData.length - 1].loadDataFromFile(dataPath);
            }
        }
        tableData[tableNo].loadDataFromFile(dataPath);
    }

    public int tableInfoNo(String tableName) {
        for (int i = 0; i < tables.length; i++) {
            if (tables[i].name.equals(tableName)) {
                return i;
            }
        }
        return -1;
    }

    public vdTableData parse(String query) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException, GeneralSecurityException, IOException {
        parser = new vdParser(query);
        parser.getNextExec();
        vdTableData returnTable = new vdTableData();

        if (parser.nextCommands.data.get(0).equals("select")) returnTable = selectQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("update")) returnTable = updateTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("insert")) returnTable = insertIntoTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("delete")) returnTable = deleteFromTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("create") && parser.nextCommands.data.get(3).equals("table")) returnTable = createTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("alter")) returnTable = alterTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("drop")) returnTable = dropTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("create") && parser.nextCommands.data.get(3).equals("database")) returnTable = createDatebase(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("retrieve")) returnTable = retrieveQuery(parser.nextCommands);

        return returnTable;
    }
    public myMultyData parseMyMultyData(String query) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
        parser = new vdParser(query);
        parser.getNextExec();
        vdTableData returnTable = new vdTableData();

        if (parser.nextCommands.data.get(0).equals("select")) returnTable = selectQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("update")) returnTable = updateTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("insert")) returnTable = insertIntoTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("delete")) returnTable = deleteFromTableQuery(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("create") && parser.nextCommands.data.get(3).equals("table")) returnTable = createTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("alter")) returnTable = alterTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("drop")) returnTable = dropTable(parser.nextCommands);
        if (parser.nextCommands.data.get(0).equals("create") && parser.nextCommands.data.get(3).equals("database")) returnTable = createDatebase(parser.nextCommands);

        return returnTable.getDataFormatted();
    }

    vdTableInfo getTableInfo(String name) {
        if (tables == null) return null;
        if (tables.length > 0) {
            for (int i = 0; i < tables.length; i++) {
                if (tables[i].name.equals(name)) return tables[i];
            }
        }
        return null;
    }

    public boolean[] whereQue(myMultyData query, vdColumnInfo[] tempColumns, String tableName, vdTableData tTable) throws UnsupportedEncodingException, ParseException {
        boolean[] tReturn = new boolean[tTable.rowCount];
        for (int row = 0; row < tTable.rowCount; row++) {
            myMultyData results = new myMultyData();
            int dataType = 0;
            int colNoFirstOpp = 0;
            String recentFirstOpp = "";
            String compareStr = "";
            String recentSecOpp = "";
            parser.parseWhere(query);

            while (parser.isWhereSub) {
                parser.getNextWhere();
                for (int i = 0; i < parser.nextWhere.length(); ) {
                    if (vdDataTypes.isRawData(parser.nextWhere.data.get(i)) == 3) {
                        results.add(parser.nextWhere.data.get(i));
                        i += 3;
                    } else {
                        recentFirstOpp = parser.nextWhere.data.get(i);
                        compareStr = parser.nextWhere.data.get(i + 3);
                        recentSecOpp = parser.nextWhere.data.get(i + 6);
                        colNoFirstOpp = tTable.getColumnNoByName(recentFirstOpp);
                        dataType = tempColumns[colNoFirstOpp].type;

                        if(vdDataTypes.isSpecialFunc(recentSecOpp)>0)recentSecOpp = vdDataTypes.getSpecialFuncData(recentSecOpp);
                        else if (vdDataTypes.isRawData(recentSecOpp) == 0) {
                            recentSecOpp = tTable.getData(row, recentSecOpp);
                            recentSecOpp = vdDataTypes.getDecryptedRawData(recentSecOpp, dataType);
                        }
                        results.add(Boolean.toString(tTable.compareData(row, colNoFirstOpp, recentSecOpp, compareStr)).toLowerCase());
                        i += 12;
                    }

                    if (parser.whereSeperator == 1 && results.data.get(results.length() - 1).equals("false"))
                        break;
                    if (parser.whereSeperator == 2 && results.data.get(results.length() - 1).equals("true"))
                        break;
                }
                if (parser.whereSeperator == 0) parser.setNextWhere(results.data.get(0));
                if (parser.whereSeperator == 1) {
                    if (results.isIn("false")) parser.setNextWhere("false");
                    else parser.setNextWhere("true");
                }
                if (parser.whereSeperator == 2) {
                    if (results.isIn("true")) parser.setNextWhere("true");
                    else parser.setNextWhere("false");
                }
            }
            parser.getNextWhere();
            tReturn[row] = parser.nextWhere.data.get(0).equals("true");
        }

        return tReturn;
    }

    public vdTableData selectQuery(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        int tFromLoc = query.find("from");
        int tWhereLoc = query.find("where");
        int tAsLoc = (query.length() > tFromLoc + 6 && query.data.get(tFromLoc + 6).equals("as")) ? tFromLoc + 6 : -1;


        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;

        myMultyData columnName = query.getSub(3, tFromLoc - 1);
        for (int i = 0; i < columnName.length(); i += 3) {
            if (columnName.data.get(i).equals(",")) {
                columnName.remove(i);
                columnName.remove(i);
                columnName.remove(i);
            }
        }
        String tableName = query.data.get(tFromLoc + 3);
        String tableVirtualName = (tAsLoc > -1) ? query.data.get(tAsLoc + 3) : query.data.get(tFromLoc + 3);
        myMultyData wheres = tWhereLoc > 0 ? query.getRest(tWhereLoc + 3) : new myMultyData();

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableVirtualName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        if (wheres.length() > 0) {
            boolean[] tempWhereData = whereQue(wheres, returnTable.columns, tableVirtualName, returnTable);
            int j = 0;
            for (int i = 0; i < tempWhereData.length; i++) {
                if (!tempWhereData[i]) {
                    returnTable.removeRow(j);
                    j--;
                }
                j++;
            }
        }
        if (!columnName.data.get(0).equals("*"))
            returnTable = selectQue(columnName, returnTable.columns, tableVirtualName, returnTable);

        return returnTable;
    }

    public vdTableData selectQue(myMultyData query, vdColumnInfo[] tempColumns, String tableName, vdTableData tTable) throws UnsupportedEncodingException, ParseException {
        vdTableData returnTable = new vdTableData();
        vdTableInfo tempTableInfo = new vdTableInfo("", tableName, "", 0, 0, tTable.tableType, 0, 0, tTable.dataFileFakeName, tTable.dataFilePass, true);
        myMultyData columnNames = new myMultyData();
        int colCount = 0;
        for (int i = 0; i < query.length(); i += 3) {
            String colTabName = "";
            String columnName = query.data.get(i);
            String columnVirtualName = columnName;
            int type = 0, length = 0;

            if (query.length() > i + 3 && query.data.get(i + 3).equals("as")) {
                columnVirtualName = query.data.get(i + 6);
                i += 6;
            }
            if (vdDataTypes.isRawData(columnName) > 0) {
                colTabName = tableName;
                columnNames.add(columnName);
                type = vdDataTypes.isRawData(columnName);
                length = vdDataTypes.lengthRawData(columnName, type);
            } else {
                columnNames.add(columnName);
                for (int j = 0; j < tempColumns.length; j++) {
                    if (tempColumns[j].name.equals(columnName)) {
                        colTabName = tempColumns[j].tableName;
                        type = tempColumns[j].type;
                        length = tempColumns[j].length;
                        // break;
                    }
                }
            }
            tempTableInfo.addColumnInfo(new vdColumnInfo(colTabName, columnVirtualName, type, length, "00000000", colCount));
            colCount++;
        }
        tempTableInfo.columnCount = colCount;
        returnTable = new vdTableData(tempTableInfo);

        for (int row = 0; row < tTable.rowCount; row++) {
            for (int column = 0; column < returnTable.columns.length; column++) {
                if(vdDataTypes.isSpecialFunc(columnNames.data.get(column))>0)returnTable.data += vdDataTypes.getEncryptedRawData(vdDataTypes.getSpecialFuncData(columnNames.data.get(column)), tempTableInfo.columns[column].type);
                else if(vdDataTypes.isRawData(columnNames.data.get(column)) == 0) {
                    returnTable.data += tTable.getData(row, columnNames.data.get(column));
                } else {
                    returnTable.data += vdDataTypes.getEncryptedRawData(columnNames.data.get(column), tempTableInfo.columns[column].type);
                }
            }
        }
        returnTable.rowCount = returnTable.data.length() / returnTable.rowLength;
        return returnTable;
    }

    public vdTableData updateTableQuery(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        int tWhereLoc = query.find("where");

        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;

        myMultyData columnName = new myMultyData();
        if(tWhereLoc < 0)columnName = query.getRest(6); else columnName = query.getSub(6, tWhereLoc - 1);
        for (int i = 0; i < columnName.length(); i += 3) {
            if (columnName.data.get(i).equals(",") || columnName.data.get(i).equals("=")) {
                columnName.remove(i);
                columnName.remove(i);
                columnName.remove(i);
            }
        }
        String tableName = query.data.get(3);
        myMultyData wheres = tWhereLoc > 0 ? query.getRest(tWhereLoc + 3) : new myMultyData();

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }

        if (wheres.length() > 0) {
            boolean[] tempWhereData = whereQue(wheres, returnTable.columns, tableName, returnTable);
            for (int i = 0; i < tempWhereData.length; i++) {
                if (tempWhereData[i]) {
                    for (int j = 0; j < columnName.length(); j += 6) {
                        returnTable.updateCell(columnName.data.get(j + 3), i, columnName.data.get(j));
                    }
                }
            }
        } else {
            for (int i = 0; i < returnTable.rowCount; i++) {
                for (int j = 0; j < columnName.length(); j += 6) {
                    returnTable.updateCell(columnName.data.get(j + 3), i, columnName.data.get(j));
                }
            }
        }
        returnTable.saveTable(dataPath);
        return returnTable;
    }

    public vdTableData insertIntoTableQuery(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        int tIntoLoc = query.find("into");
        int tValuesLoc = query.find("values");

        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;

        myMultyData columnName = query.data.get(tIntoLoc + 6).equals("(") ? query.getSub(tIntoLoc + 9, tValuesLoc - 4).setOnly(0, 6) : new myMultyData();
        myMultyData values = query.getSub(tValuesLoc + 6, query.length() - 4).setOnly(0, 6);
        boolean isAllInsert = columnName.length() == 0;

        String tableName = query.data.get(tIntoLoc + 3);
        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        String tempRow = "";
        String recent = "";
        int x = -1;
        int tempDataLength = 0;
        int tempDataType = 0;
        
        if(tempTableInfo.tableType == 1){
            for (int column = 0; column < tempTableInfo.columnCount; column++) {
                tempDataLength = tempTableInfo.columns[column].length;
                tempDataType = tempTableInfo.columns[column].type;
                if (isAllInsert) {
                    recent = values.data.get(column);
                } else {
                    x = -1;
                    for (int i = 0; i < columnName.length(); i++) {
                        if (columnName.data.get(i).equals(tempTableInfo.columns[column].name)) {
                            x = i;
                            break;
                        }
                    }
                    if (x == -1) recent = "";
                    else recent = values.data.get(x);
                }
                if(vdDataTypes.isSpecialFunc(recent) > 0)recent=vdDataTypes.getSpecialFuncData(recent);
                if(recent == null || recent.isEmpty())recent = vdDataTypes.padRight("", tempDataLength, '\0');
                else {
                    if(tempDataType == 2)
                    {
                        recent = vdDataTypes.getEncryptedRawData(recent, tempDataType, tempDataLength);
                    }
                    else recent = vdDataTypes.getEncryptedRawData(recent, tempDataType, tempDataLength);
                }
                tempRow += recent;
            }
            returnTable.addRow(tempRow);
            returnTable.saveTable(dataPath);
        }
        else{
            for (int i = 0; i < values.length(); i += tempTableInfo.columnCount){
                for (int column = 0; column < tempTableInfo.columnCount - 1; column++) {
                    tempDataLength = tempTableInfo.columns[column].length;
                    tempDataType = tempTableInfo.columns[column].type;
                    recent = values.data.get((i * tempTableInfo.columnCount) + column);
                    if(vdDataTypes.isSpecialFunc(recent) > 0)recent=vdDataTypes.getSpecialFuncData(recent);

                    if(recent == null || recent.isEmpty())recent = vdDataTypes.padRight("", tempDataLength, '\0');
                    else recent = vdDataTypes.getEncryptedRawData(recent, tempDataType, tempDataLength);
                    tempRow += recent;
                }
                tempDataType = tempTableInfo.columns[tempTableInfo.columnCount - 1].type;
                recent = values.data.get(((i + 1) * tempTableInfo.columnCount) - 1);
                recent = recent.substring(1, recent.length() - 1);
                if (recent.length() > 65535) recent = recent.substring(0, 65535);
                tempRow += recent;

                int tIDValue = vdStringCompare.getIntFromStrng(values.data.get(i * tempTableInfo.columnCount));
                if(tempTableInfo.maxID < tIDValue){
                    tables[tTeableInfoNo].maxID = tIDValue;
                    saveVDInfo();
                }
                if(tempTableInfo.minID > tIDValue){
                    tables[tTeableInfoNo].minID = tIDValue;
                    saveVDInfo();
                }
                returnTable.addRow(tempRow, dataPath);
            }
        }
        
        return returnTable;
    }

    public vdTableData deleteFromTableQuery(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        int tFromLoc = query.find("from");
        int tWhereLoc = query.find("where");

        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;

        String tableName = query.data.get(tFromLoc + 3);
        myMultyData wheres = tWhereLoc > 0 ? query.getRest(tWhereLoc + 3) : new myMultyData();

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }

        if (wheres.length() > 0 && !wheres.data.get(0).equals("*")) {
            boolean[] tempWhereData = whereQue(wheres, returnTable.columns, tableName, returnTable);
            int j = 0;
            for (int i = 0; i < tempWhereData.length; i++) {
                if (tempWhereData[i]) {
                    returnTable.removeRow(j);
                    j--;
                }
                j++;
            }
        } else if (wheres.length() > 0 && wheres.data.get(0).equals("*")) {
            returnTable.data = "";
            returnTable.rowCount = 0;
        }

        returnTable.saveTable(dataPath);
        return returnTable;
    }

    public vdTableData createTable(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        vdTableData returnTable = new vdTableData("", 0, true);
        vdTableInfo tempTableInfo = null;
        int tTableLoc = query.find("table");
        int t23Loc = query.find("(");
        int t24Loc = query.find(")");

        int tableType = Integer.parseInt(query.data.get(tTableLoc + 3));
        String tableName = query.data.get(tTableLoc + 6);
        myMultyData columnData = query.getSub(t23Loc + 3, t24Loc - 1);
        columnData.setOnly(0, 3);
        columnData.remove(",");
        int columnCount = columnData.length() / 4;
        String tTableFileName = files.generateName();
        String tTablePass = files.generatePass();
        
        String dataFileFakeName = "";
        String dataFilePass = "";
        if(tableType == 2){
            dataFileFakeName = files.generateName();
            dataFilePass = files.generatePass();
        }

        tempTableInfo = new vdTableInfo(tTablePass, tableName, tTableFileName, columnCount, tableCount + 1, tableType, 0, 0, dataFileFakeName, dataFilePass, false);
        for (int iColumn = 0; iColumn < columnData.length(); iColumn += 4) {
            tempTableInfo.addColumnInfo(new vdColumnInfo(tableName, columnData.data.get(iColumn), vdDataTypes.getDataTypeNo(columnData.data.get(iColumn + 1)), Integer.parseInt(columnData.data.get(iColumn + 2)), columnData.data.get(iColumn + 3), iColumn / 4));
        }
        int len = 0;
        if(tables == null)len = 0; else len = tables.length;
        if(len == 0) tables = new vdTableInfo[1]; else tables = Arrays.copyOf(tables, len + 1);
        tables[tableCount] = tempTableInfo;
        tableCount++;

        saveVDInfo();
        files.save("", tTableFileName);
        if(tableType == 2){
            files.save("", dataFileFakeName);
        }
        return returnTable;
    }

    public vdTableData alterTable(myMultyData query) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        query = query.getSub(6, query.length() - 1);
        String tableName = query.data.get(0);
        String columnName = query.data.get(6);

        int tCommand = query.find("add");
        if (tCommand > -1)
            return alterTableAdd(query.getSub(6, query.length() - 1), tableName, columnName);
        tCommand = query.find("drop");
        if (tCommand > -1)
            return alterTableDrop(query.getSub(6, query.length() - 1), tableName, columnName);
        tCommand = query.find("alter");
        return alterTableAlter(query.getSub(6, query.length() - 1), tableName, columnName);
    }

    public vdTableData alterTableAdd(myMultyData query, String tableName, String columnName) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;
        int dataType = vdDataTypes.getDataTypeNo(query.data.get(3));
        int dataLength = Integer.parseInt(query.data.get(6));
        String dataProperty = query.data.get(9);

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        vdColumnInfo tColumnInfo = new vdColumnInfo(tableName, columnName, dataType, dataLength, dataProperty, tempTableInfo.columnCount);
        //add column to table info
        tempTableInfo.addColumnInfo(tColumnInfo);
        tempTableInfo.columnCount++;
        //add column to table data
        if(returnTable.columnCount == 0) returnTable.columns = new vdColumnInfo[1]; else returnTable.columns = Arrays.copyOf(returnTable.columns, returnTable.columnCount + 1);
        returnTable.columns[returnTable.columnCount] = tColumnInfo;
        returnTable.columns[returnTable.columnCount].columStart = returnTable.rowLength;
        returnTable.rowLength += dataLength;

        String tempData = "";
        tempData = vdDataTypes.padRight(tempData, dataLength, '\0');
        for (int row = 0; row < returnTable.rowCount; row++) {
            StringBuffer text = new StringBuffer(returnTable.data);
            text.insert(row * returnTable.rowLength + returnTable.columns[returnTable.columnCount].columStart, tempData);
            returnTable.data = text.toString();
        }
        returnTable.columnCount++;

        //save
        returnTable.saveTable(dataPath);
        saveVDInfo();
        return returnTable;
    }

    public vdTableData alterTableDrop(myMultyData query, String tableName, String columnName) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        //remove column from table info
        vdColumnInfo removedColumnInfo = tempTableInfo.removeColumnInfo(columnName);
        //remove column from table data
        returnTable.columns = tempTableInfo.columns;
        returnTable.rowLength = tempTableInfo.rowLength;
        returnTable.columnCount = tempTableInfo.columnCount;

        String tempData = "";
        int tStart = 0;
        for (int row = 0; row < returnTable.rowCount; row++) {
            StringBuffer text = new StringBuffer(returnTable.data);
            tStart = row * returnTable.rowLength + removedColumnInfo.columStart;
            text.replace(tStart, tStart+removedColumnInfo.length, "");
            returnTable.data = text.toString();
        }

        //save
        returnTable.saveTable(dataPath);
        saveVDInfo();
        return returnTable;
    }

    public vdTableData alterTableAlter(myMultyData query, String tableName, String columnName) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;
        int dataType = vdDataTypes.getDataTypeNo(query.data.get(3));
        int dataLength = Integer.parseInt(query.data.get(6));
        String dataProperty = query.data.get(9);

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            tempTableInfo.name = tableName;
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        int removeColumnNo = tempTableInfo.getColumnNo(columnName);
        vdColumnInfo tColumnInfo = new vdColumnInfo(tableName, columnName, dataType, dataLength, dataProperty, removeColumnNo);
        //add column to table info
        vdColumnInfo removedColumnInfo = tempTableInfo.columns[removeColumnNo];
        tColumnInfo.columStart = removedColumnInfo.columStart;
        tempTableInfo.columns[removeColumnNo] = tColumnInfo;

        for (int i = removeColumnNo + 1; i < tempTableInfo.columnCount; i++) {
            tempTableInfo.columns[i].columStart = tempTableInfo.columns[i - 1].columStart + tempTableInfo.columns[i - 1].length;
        }
        tempTableInfo.rowLength = tempTableInfo.columns[tempTableInfo.columnCount - 1].columStart + tempTableInfo.columns[tempTableInfo.columnCount - 1].length;
        //add column to table data
        returnTable.columns = tempTableInfo.columns;
        returnTable.rowLength = tempTableInfo.rowLength;

        String tempData = "";
        tempData = vdDataTypes.padRight(tempData, dataLength, '\0');
        int tReplaceStart = 0;
        for (int row = 0; row < returnTable.rowCount; row++) {
            tReplaceStart = row * returnTable.rowLength + returnTable.columns[removeColumnNo].columStart;

            StringBuffer text = new StringBuffer(returnTable.data);
            text.replace(tReplaceStart, tReplaceStart + removedColumnInfo.length, tempData);
            returnTable.data = text.toString();
        }

        //save
        returnTable.saveTable(dataPath);
        saveVDInfo();
        return returnTable;
    }

    public vdTableData dropTable(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        vdTableData returnTable = new vdTableData("", 0, true);
        returnTable.rowCount = 0;
        int tTableLoc = query.find("table");
        String tableName = query.data.get(tTableLoc + 3);

        int tTableInfoNo = tableInfoNo(tableName);
        if (tTableInfoNo == -1) return returnTable;
        vdTableInfo tampTableInfo = tables[tTableInfoNo];

        if (tTableInfoNo < tables.length - 1) {
            for (int i = tTableInfoNo + 1; i < tables.length; i++) {
                tables[i - 1] = tables[i];
            }
        }
        tables = Arrays.copyOf(tables, tables.length - 1);
        tableCount--;

        saveVDInfo();
        File file = new File(dataPath + tampTableInfo.fileName + ".vdvs");
        boolean deleted = file.delete();
        if(tampTableInfo.tableType == 2){
            file = new File(dataPath + tampTableInfo.dataFileFakeName + ".vdvs");
            deleted = file.delete();
        }
        return returnTable;
    }

    public vdTableData createDatebase(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        vdTableData returnTable = new vdTableData("", 0, true);
        version = 0;
        pass = files.generatePass();
        tables = null;
        tableCount = 0;
        saveVDInfo();
        return returnTable;
    }

    public vdTableData retrieveQuery(myMultyData query) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException, GeneralSecurityException, IOException {
        int tFromLoc = query.find("from");
        
        int tByLoc = query.find("by");
        int tByUpLoc = query.find("byup");
        int tByDownLoc = query.find("bydown");
        int tByIDLoc = query.find("byid");
        
        int tToLoc = query.find("to");
        int tUpToLoc = query.find("upto");
        int tDownToLoc = query.find("downto");
        int tIDToLoc = query.find("idto");
        
        int tRetrieveStart = 0, tRetrieveEnd = 0;
        
        if(tByLoc > -1) tRetrieveStart = Integer.parseInt(query.data.get(tByLoc + 3));
        else if(tByUpLoc > -1) tRetrieveStart = Integer.parseInt(query.data.get(tByUpLoc + 3));
        else if(tByDownLoc > -1) tRetrieveStart = Integer.parseInt(query.data.get(tByDownLoc + 3));
        else if(tByIDLoc > -1) tRetrieveStart = Integer.parseInt(query.data.get(tByIDLoc + 3));
        
        if(tToLoc > -1) tRetrieveEnd = Integer.parseInt(query.data.get(tToLoc + 3));
        else if(tUpToLoc > -1) tRetrieveEnd = Integer.parseInt(query.data.get(tUpToLoc + 3));
        else if(tDownToLoc > -1) tRetrieveEnd = Integer.parseInt(query.data.get(tDownToLoc + 3));
        else if(tIDToLoc > -1) tRetrieveEnd = Integer.parseInt(query.data.get(tIDToLoc + 3));

        vdTableData returnTable = null;
        vdTableInfo tempTableInfo = null;
        
        String tableName = query.data.get(tFromLoc + 3);

        int tTeableInfoNo = tableInfoNo(tableName);
        if (tTeableInfoNo > -1) {
            tempTableInfo = tables[tTeableInfoNo];
            returnTable = new vdTableData(tempTableInfo);
            returnTable.loadDataFromFile(dataPath);
        }
        
        if(tByLoc > -1) tRetrieveStart = tRetrieveStart;
        else if(tByUpLoc > -1) tRetrieveStart = returnTable.rowCount - tRetrieveStart + 1;
        else if(tByDownLoc > -1) tRetrieveStart = tRetrieveStart;
        else if(tByIDLoc > -1) tRetrieveStart = returnTable.getMyDataIDLoc(tRetrieveStart);
        
        if(tToLoc > -1) tRetrieveEnd = tRetrieveEnd;
        else if(tUpToLoc > -1) tRetrieveEnd = tRetrieveStart + (tRetrieveEnd - 1);
        else if(tDownToLoc > -1) tRetrieveEnd = tRetrieveStart - (tRetrieveEnd - 1);
        else if(tIDToLoc > -1) tRetrieveEnd = returnTable.getMyDataIDLoc(tRetrieveEnd);
        
        returnTable.getMyData(tRetrieveStart, tRetrieveEnd, dataPath);

        return returnTable;
    }
}
