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
public class vdParser {
    public static String vdCommands[] =
    {       "retrieve","1","1",
            "select"  ,"1","1",
            "update"  ,"1","1",
            "insert"  ,"1","1",
            "delete"  ,"1","1",
            "create"  ,"1","1",
            "alter"   ,"1","1",
            "drop"    ,"1","1",

            "from"    ,"1","2",
            "where"   ,"1","2",
            "into"    ,"1","2",
            "values"  ,"1","2",
            "by"      ,"1","2",
            "byup"    ,"1","2",
            "bydown"  ,"1","2",
            "byid"    ,"1","2",
            "to"      ,"1","2",
            "upto"    ,"1","2",
            "downto"  ,"1","2",
            "idto"    ,"1","2",

            "not"     ,"2","2",
            "null"    ,"2","2",

            "int"     ,"2","1",
            "varchar" ,"2","1",
            "in"      ,"2","1",
            "and"     ,"2","1",
            "or"      ,"2","1"
    };
    public static String seperator[] = {
                " ",                                 "1",
                ",",                                 "2",
                "(",                                 "2",
                ")",                                 "2",
                System.getProperty("line.separator"),"1",
                "'",                                 "3",
                "<",                                 "2",
                ">",                                 "2",
                "!=",                                "2",
                "=",                                 "2",
                "==",                                "2",
                "&&",                                "2",
                "||",                                "2"
    };

    public myMultyData parsed = new myMultyData();
    public myMultyData nextCommands = new myMultyData();
    public boolean isNextSub = false;
    int nextCommandsStart = 0;
    int nextCommandsEnd = 0;

    public myMultyData parsedWhere = new myMultyData();
    public myMultyData nextWhere = new myMultyData();
    public boolean isWhereSub = false;
    public int whereSeperator = 0; // && 1 ; || 2 ; null 0

    public vdParser()
    {
        parsed = new myMultyData();
    }
    public vdParser(String value)
    {
        parsed = StringSeparator(value);
    }

    public void getNextExec()
    {
        myMultyData brac23 = new myMultyData();
        int brac23got = 0;
        nextCommands = new myMultyData();
        int i = 0;
        for (; i < parsed.length(); i += 3)
        {
            if (parsed.data.get(i).equals("(") && parsed.data.get(i+4).equals("1") && parsed.data.get(i + 5).equals("1")) brac23.add(Integer.toString(i));
            if (parsed.data.get(i).equals("(")) brac23got++;
            if (parsed.data.get(i).equals(")"))
            {
                if(brac23.length() < brac23got)
                {
                    brac23got--;
                }
                else
                {
                    break;
                }
            }
        }

        nextCommandsStart = Integer.parseInt(brac23.data.get(brac23.length() - 1)) + 3;
        nextCommandsEnd = i;
        for (int j = nextCommandsStart; j < nextCommandsEnd; j++)
        {
            nextCommands.add(parsed.data.get(j));
        }
        if (brac23.length() > 1) isNextSub = true; else isNextSub = false;
    }
    public void setNextSub(String value)
    {
        nextCommandsEnd = nextCommandsEnd + 3;

        parsed.data.set(nextCommandsStart - 3, value);
        parsed.data.set(nextCommandsStart - 2, "3");
        parsed.data.set(nextCommandsStart - 1, "0");
        for (int j = nextCommandsStart; j < nextCommandsEnd; j++)
        {
            parsed.remove(nextCommandsStart);
        }
    }

    public void parseWhere(myMultyData value)
    {
        if (value.data.get(0) != "(") { value.add("3", 0); value.add("2", 0); value.add("(", 0); value.add(")"); value.add("2"); value.add("4"); }
        parsedWhere = new myMultyData(value);
        nextWhere = new myMultyData();
        isWhereSub = true;
    }
    public void getNextWhere()
    {
        if (!isWhereSub) { nextWhere = parsedWhere; return; }
        myMultyData brac23 = new myMultyData();
        nextWhere = new myMultyData();
        int i = 0;
        for (; i < parsedWhere.length(); i += 3)
        {
            if (parsedWhere.data.get(i).equals("(")) brac23.add(Integer.toString(i));
            if (parsedWhere.data.get(i).equals(")")) break;
        }
        for (int j = Integer.parseInt(brac23.data.get(brac23.length() - 1)) + 3; j < i; j++)
        {
            nextWhere.add(parsedWhere.data.get(j));
        }
        if (nextWhere.isIn("&&") || nextWhere.isIn("and")) whereSeperator = 1;
        else if (nextWhere.isIn("||") || nextWhere.isIn("or")) whereSeperator = 2;
        else whereSeperator = 0;
    }
    public void setNextWhere(String value)
    {
        myMultyData brac23 = new myMultyData();
        int i = 0;
        for (; i < parsedWhere.length(); i += 3)
        {
            if (parsedWhere.data.get(i).equals("(")) brac23.add(Integer.toString(i));
            if (parsedWhere.data.get(i).equals(")")) break;
        }
        int n = Integer.parseInt(brac23.data.get(brac23.length() - 1));
        parsedWhere.data.set(n, value);
        parsedWhere.data.set(n + 1, "3");
        parsedWhere.data.set(n + 2, "0");
        for (int j = n + 3; j < i + 3; j++)
        {
            parsedWhere.remove(n + 3);
        }
        if (brac23.length() > 1) isWhereSub = true; else isWhereSub = false;
    }


    public myMultyData StringSeparator(String command)
    {
        myMultyData temp = new myMultyData();
        String lastCmd = "";
        String cmdT = command;
        String[] cmdRetT;
        int tCmd;
        while (cmdT.length() != 0)
        {
            //seperator or String
            tCmd = vdStringCompare.isInString(cmdT, 0, seperator, 2, 0);
            if (tCmd != 0)
            {
                cmdRetT = vdStringCompare.takeStepSeparator(cmdT, tCmd);
                cmdT = cmdRetT[0];
                lastCmd = cmdRetT[1];
                if (lastCmd != null && !lastCmd.isEmpty())
                {
                    temp.add(new String[] { lastCmd ,"2", Integer.toString(tCmd)});

                }
                continue;
            }
            //commands
            /*
            tCmd = vdStringCompare.isInString(cmdT, 0, vdCommands, 3, 0);
            if (tCmd != 0)
            {
                cmdT = cmdT.substring(vdCommands[(tCmd - 1) * 3].length());
                lastCmd = vdCommands[(tCmd - 1) * 3];
                temp.add(new String[] { lastCmd, "1", vdCommands[tCmd*3-1] });
                continue;
            }
            */
            //other
            cmdRetT = vdStringCompare.getUntilSeparator(cmdT);
            cmdT = cmdRetT[0];
            lastCmd = cmdRetT[1];
            if (lastCmd != null && !lastCmd.isEmpty())
            {
                temp.add(new String[] { lastCmd, "3", "0" });
            }
        }
        for (int i = 0; i < temp.length(); i += 3)
        {
            tCmd = vdStringCompare.isInArray(temp.data.get(i), vdCommands, 3, 0);
            if(tCmd > 0)
            {
                temp.data.set(i + 1, vdCommands[(tCmd * 3) - 2]);
                temp.data.set(i + 2, vdCommands[(tCmd * 3) - 1]);
            }
        }
        return temp;
    }
}