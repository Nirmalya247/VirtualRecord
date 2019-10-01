/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

/**
 *
 * @author Administrator
 */
public class vdColumnInfo {
    public String tableName;
    public String name;
    public int type;
    public int length;
    public int columNo;
    public int columStart;

    public boolean isPrimeryKey;
    public boolean isNotNull;
    public boolean isUniqueIndex;
    public boolean isBinaryColumn;
    public boolean isUnsigned;
    public boolean isFillWithZero;
    public boolean isAutoIncrement;
    public boolean isGeneratedColumn;

    public vdColumnInfo(String tabNam,String nam, int typ, int len, String prop, int colNo)
    {
        tableName = tabNam;
        name = nam;
        type = typ;
        length = len;
        columNo = colNo;

        isPrimeryKey = prop.charAt(0) == '1';
        isNotNull = prop.charAt(1) == '1';
        isUniqueIndex = prop.charAt(2) == '1';
        isBinaryColumn = prop.charAt(3) == '1';
        isUnsigned = prop.charAt(4) == '1';
        isFillWithZero = prop.charAt(5) == '1';
        isAutoIncrement = prop.charAt(6) == '1';
        isGeneratedColumn = prop.charAt(7) == '1';
    }
    public String getPopertyString()
    {
        String temp = "";
        temp += isPrimeryKey ? "1" : "0";
        temp += isNotNull ? "1" : "0";
        temp += isUniqueIndex ? "1" : "0";
        temp += isBinaryColumn ? "1" : "0";
        temp += isUnsigned ? "1" : "0";
        temp += isFillWithZero ? "1" : "0";
        temp += isAutoIncrement ? "1" : "0";
        temp += isGeneratedColumn ? "1" : "0";
        return temp;
    }
}
