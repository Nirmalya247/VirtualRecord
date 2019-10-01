/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualrecord;

import java.util.Arrays;
import java.util.*;

/**
 *
 * @author Administrator
 */
public class myMultyData {
    //public String data[];
    ArrayList<String> data;
    public myMultyData()
    {
        data = new ArrayList<String>(0);
    }
    public myMultyData(String value)
    {
        data = new ArrayList<String>(0);
        data.add(value);
    }
    public myMultyData(String[] value)
    {
        data = new ArrayList<String>(0);
        data.addAll(Arrays.asList(value));
    }
    public myMultyData(ArrayList<String> value)
    {
        data = new ArrayList<String>(0);
        data.addAll(value);
    }
    public myMultyData(List<String> value)
    {
        data = new ArrayList<String>(0);
        data.addAll(value);
    }
    public myMultyData(myMultyData value)
    {
        data = new ArrayList<String>(0);
        data.addAll(value.data);
    }
    
    
    
    public void add(String value)
    {
        data.add(value);
    }
    public void add(String value, int at)
    {
        data.add(at, value);
    }
    public void add(myMultyData value)
    {
        data.addAll(value.data);
    }
    public void add(ArrayList<String> value)
    {
        data.addAll(value);
    }
    public void add(String[] value)
    {
        data.addAll(Arrays.asList(value));
    }
    public boolean isIn(String value)
    {
        if (data == null) return false;
        return data.contains(value);
    }
    public boolean isIn(String value, int stp, int pos)
    {
        int len;
        if (data == null) len = 0; else len = data.size();
        for (int i = 0; i < len; i += stp)
        {
            if (data.get(i + pos).equals(value))
                return true;
        }
        return false;
    }
    public boolean isAll(String value)
    {
        int len;
        if (data == null) len = 0; else len = data.size();
        for (int i = 0; i < len; i ++)
        {
            if (!data.get(i).equals(value))
                return false;
        }
        return true;
    }
    public boolean isNull()
    {
        if (data == null) return true;
        else
        {
            return data.isEmpty();
        }
    }
    public int length()
    {
        if (data == null) return 0; else return data.size();
    }
    public ArrayList<String> remove(String value)
    {
        if (!isNull())
        {
            data.removeAll(Collections.singleton(value));
            return data;
        }
        return null;
    }
    public ArrayList<String> remove(int value)
    {
        if (!isNull())
        {
            data.remove(value);
            return data;
        }
        return null;
    }
    public int find(String value)
    {
        if (!isNull()) {
            return data.indexOf(value);
        }
        return -1;
    }
    public myMultyData getSub(int start, int end)
    {
        return new myMultyData(data.subList(start, end));
    }
    public myMultyData getSub(String start, String end)
    {
        return new myMultyData(data.subList(find(start), find(end)));
    }
    public myMultyData getRest(int start)
    {
        return new myMultyData(data.subList(start, data.size()));
    }
    public myMultyData getRest(String start)
    {
        return new myMultyData(data.subList(find(start), data.size()));
    }
    public myMultyData setOnly(int stp, int step)
    {
        myMultyData t = new myMultyData();
        for (int i = 0; i < length(); i += step)
        {
            t.add(data.get(i + stp));
        }
        data = t.data;
        return t;
    }
}