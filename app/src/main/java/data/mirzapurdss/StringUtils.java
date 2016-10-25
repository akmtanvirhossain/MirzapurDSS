package data.mirzapurdss;

import java.io.*;
import java.util.*;

public class StringUtils
    {

    public static String replaceString(String s, String sMatch, String sReplace)
    {
        if (sReplace == null)
            sReplace = "";

        if (sMatch == null || "".equals(sMatch) || sMatch.equals(sReplace))
            return s;

        if (s == null || s.equals(""))
        {
            return "";
        }

        int i = 0;
        int j = s.indexOf(sMatch);

        if (j < 0)
        {
            return s;
        }

        StringBuffer sb = new StringBuffer(s.length());

        while (true)
        {
            sb.append(s.substring(i, j));
            sb.append(sReplace);

            i = j + sMatch.length();
            j = s.indexOf(sMatch, i);

            if (j < 0)
            {
                sb.append(s.substring(i));
                break;
            }
        }

        return sb.toString();
    }


    public static String escapeDelimiter(String s, String sEscaper, String sDelimiter, String sBackendDelimiter)
    {
        if (s == null || "".equals(s))
            return "";

        StringBuffer sbResult = new StringBuffer();
        for (int i = 0; i < s.length(); i++)
        {
            if (s.startsWith("" + sEscaper + sEscaper, i))
            {
                sbResult.append(sEscaper);
                i++;
            }
            else if (s.startsWith("" + sEscaper + sDelimiter, i))
            {
                sbResult.append(sDelimiter);
                i++;
            }
            else if (s.startsWith("" + sDelimiter, i))
            {
                sbResult.append(sBackendDelimiter);
            }
            else
            {
                sbResult.append(s.charAt(i));
            }
        }

        return sbResult.toString();
    }


    public static String escapeForJS(String txt)
    {
        return escapeForJS(txt, false);
    }



    public static String escapeForJS(String txt, boolean useDosCRLF)
    {
        txt = StringUtils.replaceString(txt, "\\", "\\\\");

        if (useDosCRLF)
        {
            txt = StringUtils.replaceString(txt, "\r", "");
            txt = StringUtils.replaceString(txt, "\n", "\\r\\n");
        }
        else
        {
            txt = StringUtils.replaceString(txt, "\r", "");
        }

        txt = StringUtils.replaceString(txt, "\n", "\\n");
        txt = StringUtils.replaceString(txt, "'", "\\'");
        txt = StringUtils.replaceString(txt, "\"", "\\\"");
        return txt;
    }




    public static String escapeForCSV(String sArg)
    {
        StringBuffer sb = new StringBuffer();
        if (sArg != null)
        {
            if ((sArg.indexOf(",") >= 0) || (sArg.indexOf("\n") >= 0) || (sArg.indexOf("\r") >= 0)
                || (sArg.indexOf("\"") >= 0))
            {
                sb.append("\"");
                sb.append(StringUtils.replaceString(sArg, "\"", "\"\""));
                sb.append("\"");
            }
            else
            {
                sb.append(sArg);
            }
        }

        return sb.toString();
    }

    public static String escapeForHTML(String sArg, boolean isAscii)
    {
        String s;
        if(isAscii)
        {
            s = replaceString(sArg, "&", "&");
            s = replaceString(s, "<", "<");
            s = replaceString(s, ">", ">");
            s = replaceString(s, "\"", "\"");
            s = replaceString(s, "'", "'");
        }
        else
        {
            s = replaceString(sArg,"&","&");
            s = replaceString(s, "<", "<");
            s = replaceString(s, ">", ">");
            s = replaceString(s, "\"", "\"");
            s = replaceString(s, "'", "'");
        }
        return preserveSpaceRuns(s);
    }

    public static String preserveSpaceRuns(String s)
    {
        if (s.indexOf("  ") < 0)        
// Quick check for no runs of spaces
            return s;
        else
        {
            int imax = s.length();
            StringBuffer sb = new StringBuffer(imax + imax);
            for (int i = 0; i < imax; i++)
            {
                char c = s.charAt(i);
                sb.append(c);
                if (c == ' ')
                {
                    for (int j = i + 1; j < imax && s.charAt(j) == c; j++)
                    {
                        sb.append(" ");
                        i = j;
                    }
                }
            }
            return sb.toString();
        }
    }



    public static String escapeWithHTMLEntities(String s, int beg, int end)
    {
        StringBuffer sbResult = new StringBuffer();

        for (int i = 0; i < s.length(); i++)
        {
            int ch = (int)s.charAt(i);
            if (ch < beg || ch > end)
                sbResult.append("&#" + ch + ";");
            else
                sbResult.append(s.charAt(i));
        }

        return sbResult.toString();
    }

}