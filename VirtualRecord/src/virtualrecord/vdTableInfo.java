package virtualrecord;

import java.util.Arrays;

public class vdTableInfo {
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
    public int rowLength = 0;
    public String encription;

    public vdTableInfo(String encrip, String nam, String fNam, int colCount, int tablNo, int tableTyp, int minid, int maxid, String dataFileFakeN, String dataFileP, boolean isN)
    {
        encription = encrip;
        name = nam;
        fileName = fNam;
        columnCount = colCount;
        tableNo = tablNo;
        tableType = tableTyp;
        minID = minid;
        maxID = maxid;
        dataFileFakeName = dataFileFakeN;
        dataFilePass = dataFileP;
        columns = null;
        isNew = isN;
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
    public int getColumnNo(String columnName)
    {
        if (columnCount == 0) return -1;
        for (int i = 0; i < columnCount; i++)
        {
            if (columns[i].name.equals(columnName)) { return i;  }
        }
        return -1;
    }
    public vdColumnInfo removeColumnInfo(String columnName)
    {
        vdColumnInfo temp = null;
        int n = -1;
        if (columnCount == 0) return temp;
        for(int i = 0; i < columnCount; i++)
        {
            if(columns[i].name.equals(columnName)) { n = i; break; }
        }
        if (n == -1) return temp;
        temp = columns[n];

        if(n < columns.length - 1)
        {
            for(int i = n + 1; i < columns.length; i++)
            {
                columns[i-1] = columns[i];
            }
        }
        columns = Arrays.copyOf(columns, columns.length - 1);

        columnCount--;

        for (int i = n; i < columnCount; i++)
        {
            columns[i].columStart = i == 0 ? 0 : columns[i - 1].columStart + columns[i - 1].length;
        }
        rowLength = columnCount == 0 ? 0 : columns[columnCount - 1].columStart + columns[columnCount - 1].length;
        return temp;
    }
}
