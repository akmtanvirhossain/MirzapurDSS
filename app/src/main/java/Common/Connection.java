package Common;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

//--------------------------------------------------------------------------------------------------
// Created by TanvirHossain on 18/04/2016.
//--------------------------------------------------------------------------------------------------

public class Connection extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DB_NAME = Global.DatabaseName;
    private static final String DBLocation= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Global.DatabaseFolder + File.separator +  DB_NAME;

    // Todo table name
    private static final String TABLE_TODO = "todo_items";

    private Context dbContext;

    public Connection(Context context) {
        super(context, DBLocation, null, DATABASE_VERSION);
        dbContext=context;

        CreateTable("LastLogin","Create Table LastLogin(BlockNo varchar(2))");
    }

    // Creating our initial tables
    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("Create Table abc(sid varchar(10))");

    }

    // Upgrading the database between versions
    // This method is called when database is upgraded like modifying the table structure,
    // adding constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 1) {
            // Wipe older tables if existed
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            // Create tables again
            onCreate(db);
        }
    }

    //Check the existence of database table
    //----------------------------------------------------------------------------------------------
    public boolean TableExists(String TableName)
    {
        Cursor c = null;
        boolean tableExists = false;
        SQLiteDatabase db = this.getReadableDatabase();
        try
        {
            c = db.rawQuery("Select * from "+TableName,null);
            tableExists = true;
            c.close();
            db.close();
        }
        catch (Exception e) {
        }
        return tableExists;
    }

    //Create database tables
    //----------------------------------------------------------------------------------------------
    public void CreateTable(String TableName,String SQL)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(!TableExists(TableName))
        {
            db.execSQL(SQL);
        }
    }

    //Read data from database and return to Cursor variable
    //----------------------------------------------------------------------------------------------
    public Cursor ReadData(String SQL)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur=db.rawQuery(SQL, null);
        return cur;
    }

    //Check existence of data in database
    //----------------------------------------------------------------------------------------------
    public boolean Existence(String SQL)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur=db.rawQuery(SQL, null);
        if(cur.getCount()==0)
        {
            cur.close();
            db.close();
            return false;
        }
        else
        {
            cur.close();
            db.close();
            return true;
        }
    }

    //Return single result based on the SQL query
    //----------------------------------------------------------------------------------------------
    public String ReturnSingleValue(String SQL)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur=db.rawQuery(SQL, null);
        String retValue="";
        cur.moveToFirst();
        while(!cur.isAfterLast())
        {
            retValue=cur.getString(0);
            cur.moveToNext();
        }
        cur.close();
        db.close();
        return retValue;
    }

    //Split function
    //----------------------------------------------------------------------------------------------
    public static String[] split(String s, char separator)
    {
        ArrayList<String> d = new ArrayList<String>();
        for (int ini = 0, end = 0; ini <= s.length(); ini = end + 1)
        {
            end = s.indexOf(separator, ini);
            if (end == -1) {
                end = s.length();
            }

            String st = s.substring(ini, end).trim();


            if (st.length() > 0) {
                d.add(st);
            }
            else {
                d.add("");
            }
        }

        String[] temp = new String[d.size()];
        temp=d.toArray(temp);
        return temp;
    }

    //Save/Update/Delete data in to database
    //----------------------------------------------------------------------------------------------
    public void Save(String SQL)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL);
        db.close();
    }


    //Message Box
    //----------------------------------------------------------------------------------------------
    public static void MessageBox(Context ClassName,String Msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassName);
        builder.setMessage(Msg)
                .setTitle("Message")
                .setCancelable(true)
                        //.setIcon(R.drawable.logo)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Ok", null);
        builder.show();
    }

    //Generate data list
    //----------------------------------------------------------------------------------------------
    public List<String> getDataList(String SQL){
        List<String> data = new ArrayList<String>();
        Cursor cursor = ReadData(SQL);
        if (cursor.moveToFirst()) {
            do {
                data.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

    //Array adapter for spinner item
    //----------------------------------------------------------------------------------------------
    public ArrayAdapter<String> getArrayAdapter(String SQL){
        List<String> dataList = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            do {
                dataList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.dbContext,
                android.R.layout.simple_spinner_item, dataList);

        return dataAdapter;
    }


    //Check whether internet connectivity available or not
    //----------------------------------------------------------------------------------------------
    public static boolean haveNetworkConnection(Context con) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try
        {
            ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
        }
        catch(Exception e)
        {

        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    //Execute command on Database Server
    //----------------------------------------------------------------------------------------------
    public String ExecuteCommandOnServer(String SQLStr)
    {
        String response="";
        String result="";
        ExecuteCommand e=new ExecuteCommand();

        try {
            response = e.execute(SQLStr).get();
            if(response.equals("done"))
            {
                result = "done";
            }
            else
            {
                result = "not";
            }
        }
        catch (Exception e1){
            result = "not";
        }

        return result;
    }


    //Find the variable positions in an array list
    //----------------------------------------------------------------------------------------------
    private int VarPosition(String VariableName, String[] ColumnList)
    {
        int pos=0;
        for(int i=0; i< ColumnList.length; i++)
        {
            if(VariableName.trim().equalsIgnoreCase(ColumnList[i].toString().trim()))
            {
                pos=i;
                i=ColumnList.length;
            }
        }
        return pos;
    }


    // Getting array list for Upload with ^ separator from Cursor
    //----------------------------------------------------------------------------------------------
    public String[] GenerateArrayList(String VariableList,String TableName)
    {
        Cursor cur_H;
        cur_H = ReadData("Select "+ VariableList +" from "+ TableName +" where Upload='2'");

        cur_H.moveToFirst();
        String[] Data    = new String[cur_H.getCount()];
        String DataList = "";
        String[] Count=VariableList.toString().split(",");
        int RecordCount=0;

        while(!cur_H.isAfterLast())
        {
            for(int c=0; c<Count.length; c++)
            {
                if(c==0)
                {
                    if (cur_H.getString(c) == null)
                        DataList = "";
                    else if(cur_H.getString(c).equals("null"))
                        DataList = "";
                    else
                        DataList = cur_H.getString(c).toString();

                }
                else
                {
                    if (cur_H.getString(c) == null)
                        DataList+="^"+"";
                    else if(cur_H.getString(c).equals("null"))
                        DataList+="^"+"";
                    else
                        DataList+="^"+cur_H.getString(c).toString();
                }
            }
            Data[RecordCount]=DataList;
            RecordCount+=1;
            cur_H.moveToNext();
        }
        cur_H.close();

        return Data;
    }

    public List<DataClassProperty> GetDataListJSON(String VariableList,String TableName,String UniqueField)
    {
        Cursor cur_H = ReadData("Select "+ VariableList +" from "+ TableName +" where Upload='2'");
        cur_H.moveToFirst();
        List<DataClassProperty> data = new ArrayList<DataClassProperty>();
        DataClassProperty d;

        String DataList = "";
        String[] Count=VariableList.toString().split(",");
        String[] UField = UniqueField.toString().split(",");
        int RecordCount=0;

        String WhereClause="";
        String VarData[];
        int varPos=0;
        while(!cur_H.isAfterLast())
        {
            //Prepare Data List
            for(int c=0; c < Count.length; c++)
            {
                if(c==0)
                {
                    if (cur_H.getString(c) == null)
                        DataList = "";
                    else if(cur_H.getString(c).equals("null"))
                        DataList = "";
                    else
                        DataList = cur_H.getString(c).toString();

                }
                else
                {
                    if (cur_H.getString(c) == null)
                        DataList+="^"+"";
                    else if(cur_H.getString(c).equals("null"))
                        DataList+="^"+"";
                    else
                        DataList+="^"+cur_H.getString(c).toString();
                }
            }

            //Prepare Where Clause
            VarData = DataList.split("\\^");
            varPos=0;


            for(int j=0; j< UField.length; j++)
            {
                varPos=VarPosition(UField[j].toString(),Count);
                if(j==0)
                {
                    WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                }
                else
                {
                    WhereClause += " and "+UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                }
            }

            d = new DataClassProperty();
            d.setdatalist(DataList);
            d.setuniquefieldwithdata(WhereClause);
            data.add(d);

            RecordCount+=1;
            cur_H.moveToNext();
        }
        cur_H.close();

        return data;
    }

    public  String DownloadJSON(String SQL,String TableName,String ColumnList, String UniqueField)
    {
        String WhereClause="";
        int varPos=0;

        String response = "";
        String resp = "";

        try{

            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select "+ VarList[0] +" from "+ TableName +" Where "+ WhereClause))
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Update "+ TableName +" Set ";
                            SQL+= VarList[r] + " = '"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            if(r == VarData.length-1)
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                                SQL += " Where "+ WhereClause;
                            }
                            else
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                            }
                        }
                    }

                    Save(SQL);
                }
                //Insert command
                else
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Insert into "+ TableName +"("+ ColumnList +")Values(";
                            SQL+= "'"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            SQL+= ",'"+ VarData[r].toString() +"'";
                        }
                    }
                    SQL += ")";

                    Save(SQL);
                }

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {

            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    public  String DownloadJSON_UpdateServer(String SQL,String TableName,String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select "+ VarList[0] +" from "+ TableName +" Where "+ WhereClause))
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Update "+ TableName +" Set ";
                            SQL+= VarList[r] + " = '"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            if(r == VarData.length-1)
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                                SQL += " Where "+ WhereClause;
                            }
                            else
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                            }
                        }
                    }

                    Save(SQL);
                }
                //Insert command
                else
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Insert into "+ TableName +"("+ ColumnList +")Values(";
                            SQL+= "'"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            SQL+= ",'"+ VarData[r].toString() +"'";
                        }
                    }
                    SQL += ")";

                    Save(SQL);
                }

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    try {
                        //sqlString.add("Update " + TableName + " Set UpdateNeeded='2' Where " + data);
                        sqlString.add("Update " + TableName + " Set Upload='1' Where " + data);
                    }catch (Exception ex)
                    {

                    }
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }

    public  String DownloadJSON_UpdatePNO(String SQL, String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            String PNO="";
            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select Vill from Member Where "+ WhereClause))
                {
                    PNO = VarData[4].toString();

                    Save("Update Member  Set PNo='"+ PNO +"' Where "+ WhereClause);
                    Save("Update Events  Set PNo='"+ PNO +"' Where "+ WhereClause);
                    Save("Update PregHis Set PNo='"+ PNO +"' Where "+ WhereClause);

                    //update download status on server
                    //String rep = ExecuteCommandOnServer("Update Member set UpdateNeeded='4' Where "+ WhereClause);
                }

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update Member Set UpdateNeeded='4' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }

    public  String DownloadJSON_EventDelete(String SQL, String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            String PNO="";
            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select Vill from Events Where "+ WhereClause))
                {
                    Save("Delete from Events Where " + WhereClause);

                    //update download status on server
                    //rep = ExecuteCommandOnServer("Update EventsDelete set DeleteNeeded='2' Where "+ WhereClause);
                }


                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update EventsDelete set DeleteNeeded='2' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    public  String DownloadJSON_DataUpdate(String SQL,String TableName,String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select "+ VarList[0] +" from "+ TableName +" Where "+ WhereClause))
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Update "+ TableName +" Set ";
                            SQL+= VarList[r] + " = '"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            if(r == VarData.length-1)
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                                SQL += " Where "+ WhereClause;
                            }
                            else
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString() +"'";
                            }
                        }
                    }

                    Save(SQL);
                }
                //Insert command
                else
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Insert into "+ TableName +"("+ ColumnList +")Values(";
                            SQL+= "'"+ VarData[r].toString() +"'";
                        }
                        else
                        {
                            SQL+= ",'"+ VarData[r].toString() +"'";
                        }
                    }
                    SQL += ")";

                    Save(SQL);
                }

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update "+ TableName +" Set Upload='2' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }

    public  String DownloadJSON_PNO_Update(String SQL,String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();
            String PNO = "";
            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select Vill from Member Where "+ WhereClause))
                {
                    PNO = VarData[4].toString();

                    Save("Update Member  Set PNo='" + PNO + "' Where " + WhereClause);
                    Save("Update Events  Set PNo='" + PNO + "' Where " + WhereClause);
                    Save("Update PregHis Set PNo='" + PNO + "' Where " + WhereClause);

                    dataStatus.add(WhereClause);
                }
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update Member Set UpdateNeeded='4' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    public  String DownloadJSON_Event_Delete(String SQL,String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();
            String PNO = "";
            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Update command
                if(Existence("Select Vill from Events Where "+ WhereClause))
                {
                    Save("Delete from Events Where " + WhereClause);

                    dataStatus.add(WhereClause);
                }

            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update EventsDelete Set DeleteNeeded='2' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    //Remove data on local device
    public  String DownloadJSON_Delete_UpdateServer(String SQL,String LocalTableName, String ServerTableName, String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Delete command
                SQL = "Delete from "+ LocalTableName +" Where "+ WhereClause;

                Save(SQL);

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update "+ ServerTableName +" Set Upload='2' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    public  String DownloadJSON_BlockUpdate_UpdateServer(String SQL,String LocalTableName,String ColumnList, String UniqueField)
    {
        String WhereClause = "";
        int varPos         = 0;

        String response = "";
        String resp     = "";

        try{
            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<String>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
                    }
                }

                //Delete command
                SQL = "Delete from "+ LocalTableName +" Where "+ WhereClause;

                Save(SQL);

                dataStatus.add(WhereClause);
            }


            //Status back to server
            if(dataStatus.size()>0)
            {
                //Generate SQL String
                List<String> sqlString = new ArrayList<String>();
                for(String data : dataStatus){
                    sqlString.add("Update BariRemove Set Upload='2' Where "+ data);
                }

                DataClass_SQL_Update dt = new DataClass_SQL_Update();
                dt.setSQLString(sqlString);

                String json = gson.toJson(dt);
                UploadDataSQLJSON u = new UploadDataSQLJSON();
                try
                {
                    response=u.execute(json).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }



    //Download list item from server based on SQl query
    public  List<String> DownloadJSONList(String SQL)
    {
        String response = "";
        String resp = "";

        List<String> dataStatus = new ArrayList<String>();
        try{

            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);
            dataStatus = responseData.getdata();

        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return dataStatus;
    }


    public String UploadJSON(String TableName,String ColumnList,String UniqueFields)
    {
        DataClass dt = new DataClass();
        dt.settablename(TableName);
        dt.setcolumnlist(ColumnList);
        List<DataClassProperty> data = GetDataListJSON(ColumnList, TableName, UniqueFields);
        dt.setdata(data);

        Gson gson = new Gson();
        String json = gson.toJson(dt);
        String response="";
        UploadDataJSON u = new UploadDataJSON();
        try{
            response=u.execute(json).get();

            //Process Response
            downloadClass d = new downloadClass();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            //upload all records as successfull upload then update status of upload=2 for unsuccessfull
            //Save("Update " + TableName + " Set Upload='1'");
            for(int i=0; i<responseData.getdata().size(); i++)
            {
                Save("Update " + TableName + " Set Upload='1' where " + responseData.getdata().get(i).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    // Getting result from database server based on SQL
    //----------------------------------------------------------------------------------------------
    public String ReturnResult(String MethodName, String SQL)
    {
        ReturnResult r=new ReturnResult();
        String response="";
        r.Method_Name = MethodName;
        r.SQLStr=SQL;
        try {
            response=r.execute("").get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
        return response;
    }


    //Generate table in local database
    public void DatabaseTableSync(String TableName) {
        String SQLStr = "";
        DownloadData d = new DownloadData();
        d.Method_Name = "DownloadData";
        d.SQLStr = "Select TableName, TableScript from DatabaseTab where TableName='"+ TableName +"'";

        String DataArray[] = null;

        try {
            DataArray = d.execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < DataArray.length; i++) {
            String VarData[] = split(DataArray[i], '^');
            CreateTable(VarData[0], VarData[1]);
        }
    }


    //Sync data between local and server
    public void DatabaseTableDataSync(String[] TableList) {
        String response = "";
        String TableName = "";
        String VariableList ="";
        String UniqueField = "";
        String[] V;

        for(int i=0; i< TableList.length;i++) {
            if(TableList[i].toLowerCase().equals("household"))
                UniqueField = "Div, Dist, Upz, UN, Mouza, Vill,ProvType,ProvCode, HHNo";
            else if(TableList[i].toLowerCase().equals("member"))
                UniqueField = "Dist, Upz, UN, Mouza, Vill, ProvType,ProvCode, HHNo, SNo";
            else if(TableList[i].toLowerCase().equals("visits"))
                UniqueField = "Dist, Upz, UN, Mouza, Vill,ProvType,ProvCode, HHNo, VDate";

            VariableList = GetColumnList(TableName);
            V = GenerateArrayList(VariableList, TableName);
            //response = UploadData(V, TableName, VariableList, UniqueField);
        }
    }

    //Rebuild Local Database from Server
    //----------------------------------------------------------------------------------------------
    public void RebuildDatabase(String Cluster, String VHWCode, String VHWName) {
        List<String> listItem = new ArrayList<String>();
        listItem = DownloadJSONList("Select TableName+'^'+TableScript from DatabaseTab");

        for (int i = 0; i < listItem.size(); i++) {
            String VarData[] = split(listItem.get(i),'^');
            CreateTable(VarData[0], VarData[1]);
        }

        //Login Table
        Save("Insert into Login(UserId, UserName, Pass)Values('"+ VHWCode +"','"+ VHWCode +"','"+ VHWName +"')");

        //------------------------------------------------------------------------------------------
        //Data Sync: Download data from server
        //------------------------------------------------------------------------------------------
        String Res = "";
        String TableName;
        String VariableList;
        String UniqueField;
        String SQLStr;

        try {

            //MDSSVill
            //--------------------------------------------------------------------------------------
            Res = DownloadJSON("Select Vill, Vname, UCode, UName, Cluster, Status, OldUnion from MDSSVill","MDSSVill","Vill, Vname, UCode, UName, Cluster, Status, OldUnion","Vill");

            //Cluster
            //--------------------------------------------------------------------------------------
            Res = DownloadJSON("Select Cluster, UpdateDT, DeviceSetting from Cluster Where Cluster='"+ Cluster +"'","Cluster","Cluster, UpdateDT, DeviceSetting","Cluster");

            //Weekly Visit Schedule
            //--------------------------------------------------------------------------------------
            SQLStr = "Select top 5 Week, (cast(YEAR(StDate) as varchar(4))+'-'+right('0'+ cast(MONTH(StDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(StDate) as varchar(2)),2))StDate," +
                    "(cast(YEAR(EnDate) as varchar(4))+'-'+right('0'+ cast(MONTH(EnDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(EnDate) as varchar(2)),2))EnDate" +
                    " from WeeklyVstDt where week <=(select week from WeeklyVstDt where GETDATE() between cast(stdate as datetime) and cast(endate as datetime)) order by Week desc";
            Res = DownloadJSON(SQLStr,"WeeklyVstDt","Week, StDate, EnDate","Week");

            //Code List
            //--------------------------------------------------------------------------------------
            Res = DownloadJSON("Select FName, VarName, VarCode, VarDes from CodeList","CodeList","FName, VarName, VarCode, VarDes","FName, VarName, VarCode");

            //VHWs
            //--------------------------------------------------------------------------------------
            Res = DownloadJSON("Select Cluster, VHW, VHWNAME, Active, JoinDt, ExitDt, SystemUpdateDT,BariChar from VHWs Where Active='1' and Cluster='"+ Cluster +"' and VHW='"+ VHWCode +"'","VHWs","Cluster, VHW, VHWNAME, Active, JoinDt, ExitDt, SystemUpdateDT","VHW,Active");

            //Bari
            //--------------------------------------------------------------------------------------
            TableName = "Bari";
            VariableList = "Vill, Bari, BariName, BariLoc, Cluster, Block, Lat, Lon, EnDt, UserId, Upload, UploadDT";
            SQLStr = "Select Vill, Bari, BariName, BariLoc, Cluster, Block, Lat, Lon, EnDt, UserId, Upload, UploadDT" +
                    " from Bari where Cluster = '"+ Cluster +"'";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"Vill, Bari");

            //DSS Bari
            //--------------------------------------------------------------------------------------
            TableName = "DSSBari";
            VariableList = "Vill, Bari, BariName, BariLoc, Cluster, Block, Lat, Lon, EnDt, UserId";
            SQLStr  = "Select d.Vill, d.Bari, d.BariName, d.BariLoc, d.Cluster, d.Block, d.Lat, d.Lon, d.EnDt, d.UserId";
            SQLStr += " from DSSBari d,Bari b where d.Vill+d.Bari=b.vill+b.bari and b.Cluster='"+ Cluster +"'";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"Vill, Bari");

            //Visits
            //--------------------------------------------------------------------------------------
            TableName = "Visits";
            VariableList = "ChildId, PID, CID, Week, VDate, VStat, SickStatus, ExDate, Lat, Lon, EnDt, UserId, Upload, UploadDT";
            SQLStr  = " select ChildId, PID, CID, Week, VDate, VStat, SickStatus, ExDate, Lat, Lon, EnDt, UserId, Upload, UploadDT from";
            SQLStr += " (Select ChildId, PID, CID, Week, (cast(YEAR(VDate) as varchar(4))+'-'+right('0'+ cast(MONTH(VDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(VDate) as varchar(2)),2)) VDate,";
            SQLStr += " VStat, SickStatus, (cast(YEAR(ExDate) as varchar(4))+'-'+right('0'+ cast(MONTH(ExDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(ExDate) as varchar(2)),2)) ExDate,";
            SQLStr += " v.Lat, v.Lon, v.EnDt, v.UserId, v.Upload, v.UploadDT,rank() over (partition by childid order by week desc)total";
            SQLStr += " from Visits v, Bari b where left(v.ChildId,7)=b.Vill+b.Bari and b.Cluster='"+ Cluster +"')a";
            SQLStr += " where total  between 1 and 5";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"ChildId,Week");

            //Child
            //--------------------------------------------------------------------------------------
            SQLStr = "Select ChildId, c.Vill, c.bari, HH, SNo, PID, CID, Name, Sex, (cast(YEAR(BDate) as varchar(4))+'-'+right('0'+ cast(MONTH(BDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(BDate) as varchar(2)),2))BDate,";
            SQLStr += "AgeM, MoNo, MoPNO, MoName, FaNo, FaPNO, FaName, EnType, (cast(YEAR(EnDate) as varchar(4))+'-'+right('0'+ cast(MONTH(EnDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(EnDate) as varchar(2)),2))EnDate,";
            SQLStr += "ExType, (cast(YEAR(ExDate) as varchar(4))+'-'+right('0'+ cast(MONTH(ExDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(ExDate) as varchar(2)),2))ExDate,";
            SQLStr += "(cast(YEAR(VStDate) as varchar(4))+'-'+right('0'+ cast(MONTH(VStDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(VStDate) as varchar(2)),2))VStDate,VHW, VHWCluster, VHWBlock, Referral, c.EnDt, c.UserId, c.Upload, c.UploadDt";
            SQLStr += " from Child c,Bari b where c.Vill=b.Vill and c.Bari=b.Bari and b.Cluster='"+ Cluster +"'";

            TableName = "Child";
            VariableList = "ChildId, Vill, bari, HH, SNo, PID, CID, Name, Sex, BDate, AgeM, MoNo, MoPNO, MoName, FaNo, FaPNO, FaName, EnType, EnDate, ExType, ExDate, VStDate, VHW, VHWCluster, VHWBlock, Referral, EnDt, UserId, Upload, UploadDt";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"ChildId");

            //MWRA
            //--------------------------------------------------------------------------------------
            SQLStr = "Select mwraId, c.Vill, c.bari, HH, SNo, PID, CID, Name, Sex, (cast(YEAR(BDate) as varchar(4))+'-'+right('0'+ cast(MONTH(BDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(BDate) as varchar(2)),2))BDate,";
            SQLStr += "AgeM, MoNo, MoPNO, MoName, FaNo, FaPNO, FaName, EnType, (cast(YEAR(EnDate) as varchar(4))+'-'+right('0'+ cast(MONTH(EnDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(EnDate) as varchar(2)),2))EnDate,";
            SQLStr += "ExType, (cast(YEAR(ExDate) as varchar(4))+'-'+right('0'+ cast(MONTH(ExDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(ExDate) as varchar(2)),2))ExDate,c.PStat,c.LMPDt,c.EnDt";
            SQLStr += " from MWRA c,Bari b where c.Vill=b.Vill and c.Bari=b.Bari and b.Cluster='"+ Cluster +"'";

            TableName = "MWRA";
            VariableList = "MwraId, Vill, bari, HH, SNo, PID, CID, Name, Sex, BDate, AgeM, MoNo, MoPNO, MoName, FaNo, FaPNO, FaName, EnType, EnDate, ExType, ExDate,PStat,LMPDt, EnDt";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"MwraId");

            //AssNewBorn
            //--------------------------------------------------------------------------------------
            SQLStr = "select a.ChildId, a.CID, a.PID, Temp, Week, VType, Visit, (cast(YEAR(VDate) as varchar(4))+'-'+right('0'+ cast(MONTH(VDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(VDate) as varchar(2)),2))VDate, Oth1, Oth2, Oth3, HNoCry, HNoBrea, HConv, HUncon, HDBrea, HJaund, HHFever, HLFever, HSkin, HFedp, HPus, HVomit, HWeak, HLeth, Asses, RR1, RR2, NoCry, Gasp, SBrea, BirthAs, Conv, RBrea, CInd, HFever, Hypo, UCon, Pus, UmbR, Weak, Leth, NoFed, Vsd, ConvH, Fonta, Vomit, H1Fever, LFever, NJaun, Pvsd, Jaund, SJaun, EyeP, Gono, Sick, Ref, RSlip, Comp, Reason, TPlace, TPlaceC, TAbsIn, TAbsDur, Hos, StartTime, EndTime, a.EnDt, a.UserId, a.Upload";
            SQLStr += " from AssNewBorn a";
            SQLStr += " inner join Child c on a.ChildId=c.ChildId";
            SQLStr += " inner join Bari b on c.Vill=b.Vill and c.bari=b.Bari";
            SQLStr += " where b.Cluster='"+ Cluster +"'";

            TableName = "AssNewBorn";
            VariableList = "ChildId, CID, PID, Temp, Week, VType, Visit, VDate, Oth1, Oth2, Oth3, HNoCry, HNoBrea, HConv, HUncon, HDBrea, HJaund, HHFever, HLFever, HSkin, HFedp, HPus, HVomit, HWeak, HLeth, Asses, RR1, RR2, NoCry, Gasp, SBrea, BirthAs, Conv, RBrea, CInd, HFever, Hypo, UCon, Pus, UmbR, Weak, Leth, NoFed, Vsd, ConvH, Fonta, Vomit, H1Fever, LFever, NJaun, Pvsd, Jaund, SJaun, EyeP, Gono, Sick, Ref, RSlip, Comp, Reason, TPlace, TPlaceC, TAbsIn, TAbsDur, Hos, StartTime, EndTime, EnDt, UserId, Upload";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"ChildId, Week, Visit");

            //AssPneu
            //--------------------------------------------------------------------------------------
            SQLStr = "select a.ChildId, a.PID, a.CID, Week, (cast(YEAR(VDate) as varchar(4))+'-'+right('0'+ cast(MONTH(VDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(VDate) as varchar(2)),2))VDate, VType, Visit, temp, Cough, ";
            SQLStr += " (cast(YEAR(CoughDt) as varchar(4))+'-'+right('0'+ cast(MONTH(CoughDt) as varchar(2)),2)+'-'+right('0'+cast(DAY(CoughDt) as varchar(2)),2))CoughDt, DBrea, ";
            SQLStr += " (cast(YEAR(DBreaDt) as varchar(4))+'-'+right('0'+ cast(MONTH(DBreaDt) as varchar(2)),2)+'-'+right('0'+cast(DAY(DBreaDt) as varchar(2)),2))DBreaDt, Fever, ";
            SQLStr += " (cast(YEAR(FeverDt) as varchar(4))+'-'+right('0'+ cast(MONTH(FeverDt) as varchar(2)),2)+'-'+right('0'+cast(DAY(FeverDt) as varchar(2)),2))FeverDt, OthCom1, OthCom2, OthCom3, Asses, RR1, RR2, Conv, FBrea, CInd, Leth, UCon, Drink, Vomit, None, LFever, MFever, HFever, Neck, Fonta, Conv2, Leth2, Ucon2, Drink2, Vomit2, CSPne, CPPne, CNPne, CLFever, CMFever, CHFever, CMenin, TSPne, TPPne, TNPne, TLFever, TMFever, THFever, TMenin, Ref, RSlip, Comp, Reason, TPlace, TPlaceC, TAbsIn, TAbsDur, Hos, a.EnDt, a.UserId, a.Upload";
            SQLStr += " from AssPneu a";
            SQLStr += " inner join Child c on a.ChildId=c.ChildId";
            SQLStr += " inner join Bari b on c.Vill=b.Vill and c.bari=b.Bari";
            SQLStr += " where b.Cluster='"+ Cluster +"'";

            TableName = "AssPneu";
            VariableList = "ChildId, PID, CID, Week, VDate, VType, Visit, temp, Cough, CoughDt, DBrea, DBreaDt, Fever, FeverDt, OthCom1, OthCom2, OthCom3, Asses, RR1, RR2, Conv, FBrea, CInd, Leth, UCon, Drink, Vomit, None, LFever, MFever, HFever, Neck, Fonta, Conv2, Leth2, Ucon2, Drink2, Vomit2, CSPne, CPPne, CNPne, CLFever, CMFever, CHFever, CMenin, TSPne, TPPne, TNPne, TLFever, TMFever, THFever, TMenin, Ref, RSlip, Comp, Reason, TPlace, TPlaceC, TAbsIn, TAbsDur, Hos, EnDt, UserId, Upload";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"ChildId, Week, Visit");

            //NonComp
            //--------------------------------------------------------------------------------------
            SQLStr = " select a.ChildId, a.CID, a.PID, Week, VType, Visit, (cast(YEAR(VDate) as varchar(4))+'-'+right('0'+ cast(MONTH(VDate) as varchar(2)),2)+'-'+right('0'+cast(DAY(VDate) as varchar(2)),2)) VDate, RefResult, Q1a, Q1b, Q1c, Q1d, CausOth, VisitOthYN, Provider1, Provider2, Provider3, Provider4, ProviderOth1, Prescrip, RefA, RefB, RefC, RefD, RefE, RefF, RefG, RefH, RefI, RefX, RefOth, ServiceA, ServiceB, ServiceC, ServiceD, ServiceE, ServiceF, ServiceG, ServiceH, ServiceX, ServiceOth, StartTime, EndTime, a.UserId, a.EnDt, a.Upload";
            SQLStr += " from NonComp a";
            SQLStr += " inner join Child c on a.ChildId=c.ChildId";
            SQLStr += " inner join Bari b on c.Vill=b.Vill and c.bari=b.Bari";
            SQLStr += " where b.Cluster='"+ Cluster +"'";

            TableName = "NonComp";
            VariableList = "ChildId, CID, PID, Week, VType, Visit, VDate, RefResult, Q1a, Q1b, Q1c, Q1d, CausOth, VisitOthYN, Provider1, Provider2, Provider3, Provider4, ProviderOth1, Prescrip, RefA, RefB, RefC, RefD, RefE, RefF, RefG, RefH, RefI, RefX, RefOth, ServiceA, ServiceB, ServiceC, ServiceD, ServiceE, ServiceF, ServiceG, ServiceH, ServiceX, ServiceOth, StartTime, EndTime, UserId, EnDt, Upload";
            Res = DownloadJSON(SQLStr,TableName,VariableList,"ChildId, Week, Visit");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //To get the list of columns(string) in table
    //----------------------------------------------------------------------------------------------
    public String GetColumnList(String TableName)
    {
        String CList = "";
        Cursor cur_H;
        cur_H = ReadData("pragma table_info('"+ TableName +"')");

        cur_H.moveToFirst();
        int RecordCount=0;

        while(!cur_H.isAfterLast())
        {
            if(RecordCount==0)
                CList +=  cur_H.getString(cur_H.getColumnIndex("name"));
            else
                CList +=  ","+ cur_H.getString(cur_H.getColumnIndex("name"));

            RecordCount += 1;
            cur_H.moveToNext();
        }
        cur_H.close();

        return CList;
    }

    public int GetTotalColumn(String TableName)
    {
        int totalCol = 0;
        Cursor cur_H;
        cur_H = ReadData("pragma table_info('"+ TableName +"')");
        totalCol = cur_H.getColumnCount();
        cur_H.close();

        return totalCol;
    }

    //To get the list of columns(string array) in table
    //----------------------------------------------------------------------------------------------
    public String[] GetColumnListArray(String TableName)
    {
        Cursor cur = ReadData("SELECT * FROM "+ TableName +" WHERE 0");
        String[] columnNames;
        try {
            columnNames = cur.getColumnNames();
        } finally {
            cur.close();
        }
        return columnNames;
    }

    //Download Table List from server
    private String[] TableListServer()
    {
        String SQLStr= "";
        DownloadData d = new DownloadData();
        d.Method_Name = "DownloadData";
        d.SQLStr = "Select TableName from DatabaseTab";

        String DataArray[] = null;

        try {
            DataArray = d.execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return DataArray;
    }

    public void TableStructureSync()
    {
        String TableList[] = TableListServer();
        String SQL = "";
        String TableName = "";
        for (int t = 0; t < TableList.length; t++) {
            TableName = TableList[t];
            SQL = "select (c.name+'^'+cast(c.length as varchar(10)))ColwithLength from sysobjects t,syscolumns c";
            SQL += " where t.id=c.id and t.name='"+ TableName +"' and lower(t.xtype)='u' order by colid";

            //Local database
            String[] local = GetColumnListArray(TableName);

            //Server database
            String[] server = DownloadArrayList(SQL);

            String[] C;
            Boolean matched = false;

            //matched database columns(local and server)
            for (int i=0;i<server.length;i++)
            {
                matched = false;
                C = Connection.split(server[i], '^');
                for(int j=0;j<local.length;j++)
                {
                    if(C[0].toString().toLowerCase().equals(local[j].toString().toLowerCase()))
                    {
                        matched = true;
                        j = local.length;
                    }
                }
                if(matched==false)
                {
                    Save("Alter table "+ TableName +" add column "+ C[0].toString() +" varchar("+ C[1].toString() +") default ''");
                }
            }
        }


/*
        String SQL = "";
        SQL = "select (c.name+'^'+cast(c.length as varchar(10)))ColwithLength from sysobjects t,syscolumns c";
        SQL += " where t.id=c.id and t.name='"+ TableName +"' and lower(t.xtype)='u' order by colid";

        //Local database
        String[] local = GetColumnListArray(TableName);

        //Server database
        String[] server = DownloadArrayList(SQL);

        String[] C;
        Boolean matched = false;

        //matched database columns(local and server)
        for (int i=0;i<server.length;i++)
        {
            matched = false;
            C = Connection.split(server[i], '^');
            for(int j=0;j<local.length;j++)
            {
                if(C[0].toString().toLowerCase().equals(local[j].toString().toLowerCase()))
                {
                    matched = true;
                    j = local.length;
                }
            }
            if(matched==false)
            {
                Save("Alter table "+ TableName +" add column "+ C[0].toString() +" varchar("+ C[1].toString() +") default ''");
            }
        }
*/
    }

    private String[] DownloadArrayList(String SQL)
    {
        DownloadData d = new DownloadData();
        d.Method_Name="DownloadData";
        d.SQLStr=SQL;

        String DataArray[] = new String[0];
        try {
            DataArray = d.execute("").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return DataArray;
    }

    /*
    // Insert record into the database
    public void addTodoItem(TodoItem item) {
        // Open database connection
        SQLiteDatabase db = this.getWritableDatabase();
        // Define values for each field
        ContentValues values = new ContentValues();
        values.put(KEY_BODY, item.getBody());
        values.put(KEY_PRIORITY, item.getPriority());
        // Insert Row
        db.insertOrThrow(TABLE_TODO, null, values);
        db.close(); // Closing database connection
    }


    // Returns a single todo item by id
    public TodoItem getTodoItem(int id) {
        // Open database for reading
        SQLiteDatabase db = this.getReadableDatabase();
        // Construct and execute query
        Cursor cursor = db.query(TABLE_TODO,  // TABLE
                new String[] { KEY_ID, KEY_BODY, KEY_PRIORITY }, // SELECT
                KEY_ID + "= ?", new String[] { String.valueOf(id) },  // WHERE, ARGS
                null, null, "id ASC", "100"); // GROUP BY, HAVING, ORDER BY, LIMIT
        if (cursor != null)
            cursor.moveToFirst();
        // Load result into model object
        TodoItem item = new TodoItem(cursor.getString(1), cursor.getInt(2));
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)));
        // return todo item
        return item;
    }

    public List<TodoItem> getAllTodoItems() {
        List<TodoItem> todoItems = new ArrayList<TodoItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TodoItem item = new TodoItem(cursor.getString(1), cursor.getInt(2));
                item.setId(cursor.getInt(0));
                // Adding todo item to list
                todoItems.add(item);
            } while (cursor.moveToNext());
        }

        // return todo list
        return todoItems;
    }

    public int getTodoItemCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TODO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public int updateTodoItem(TodoItem item) {
        // Open database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        // Setup fields to update
        ContentValues values = new ContentValues();
        values.put(KEY_BODY, item.getBody());
        values.put(KEY_PRIORITY, item.getPriority());
        // Updating row
        int result = db.update(TABLE_TODO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        // Close the database
        db.close();
        return result;
    }

    public void deleteTodoItem(TodoItem item) {
        // Open database for writing
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete the record with the specified id
        db.delete(TABLE_TODO, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        // Close the database
        db.close();
    }
    */

    //New Update: 20 Oct 2017
    public void Sync_Download(String TableName, String UserId, String WhereClause)
    {
        //Retrieve sync parameter
        //------------------------------------------------------------------------------------------
        String[] SyncParam = Sync_Parameter(TableName);

        String SQLStr       = SyncParam[0];
        String VariableList = SyncParam[1];
        String UniqueField  = SyncParam[2];
        String SQL_VariableList  = SyncParam[3];
        String Res = "";
        String SQL = "";

        //Generate Unique ID field
        //------------------------------------------------------------------------------------------
        String[] U = UniqueField.split(",");
        String UID = "";
        //String UID_Sync = "";
        for(int i=0; i<U.length; i++){
            if(i==0)
                UID = "cast(t."+ U[i] +" as varchar(50))";
            else
                UID += "+cast(t."+ U[i] +" as varchar(50))";
        }

        //calculate total records
        //------------------------------------------------------------------------------------------
        Integer totalRecords = 0;
        SQL  = "Select Count(*)totalRec from "+ TableName +" as t";
        SQL += " where not exists(select * from Sync_Management where";
        SQL += " lower(TableName)  = lower('"+ TableName +"') and";
        SQL += " UniqueID   = "+ UID +" and";
        SQL += " convert(varchar(19),modifydate,120) = convert(varchar(19),t.modifydate,120) and";

        SQL += " UserId   ='"+ UserId +"')";
        if(WhereClause.length()>0)
        {
            SQL += " and "+ WhereClause;
        }

        String totalRec = ReturnResult("ReturnSingleValue",SQL);
        if(totalRec==null)
            totalRecords =0;
        else
            totalRecords = Integer.valueOf(totalRec);

        //Calculate batch size
        //------------------------------------------------------------------------------------------
        //0(zero) means all selected data
        Integer batchSize = Integer.valueOf(ReturnSingleValue("select ifnull(batchsize,0)batchsize from DatabaseTab where TableName='"+ TableName +"'"));
        Integer totalBatch   = 1;

        if(batchSize==0) {
            totalBatch = 1;
            batchSize = totalRecords;
        }
        else if(batchSize > 0) {
            totalBatch = totalRecords/batchSize;
            if(totalRecords%batchSize>0)
                totalBatch += 1;
        }

        //Execute batch download
        //------------------------------------------------------------------------------------------
        for(int i = 0; i < totalBatch; i++) {
            SQL  = "Select top "+ batchSize +" "+ SQL_VariableList +" from "+ TableName +" as t";
            SQL += " where not exists(select * from Sync_Management where";
            SQL += " lower(TableName)  = lower('"+ TableName +"') and";
            SQL += " UniqueID   = "+ UID +" and";
            SQL += " convert(varchar(19),modifydate,120) = convert(varchar(19),t.modifydate,120) and";
            SQL += " UserId   ='"+ UserId +"')";
            if(WhereClause.length()>0)
            {
                SQL += " and "+ WhereClause;
            }

            Res = DownloadJSON_Update_Sync_Management(SQL, TableName, VariableList, UniqueField, UserId);
        }
    }

    private String DownloadJSON_Update_Sync_Management(String SQL, String TableName,String ColumnList, String UniqueField, String UserId)
    {
        String WhereClause="";
        int varPos=0;

        String response = "";
        String resp = "";

        try{

            DownloadDataJSON dload = new DownloadDataJSON();
            response=dload.execute(SQL).get();

            //Process Response
            downloadClass d = new downloadClass();
            Gson gson = new Gson();
            Type collType = new TypeToken<downloadClass>(){}.getType();
            downloadClass responseData = (downloadClass) gson.fromJson(response,collType);

            String UField[]  = UniqueField.split(",");
            String VarList[] = ColumnList.split(",");

            List<String> dataStatus = new ArrayList<>();
            String modifyDate = "";
            String UID        = "";
            String USID       = "";
            String DataList = "";
            DataClassProperty dd;
            List<DataClassProperty> data = new ArrayList<DataClassProperty>();

            for(int i=0; i<responseData.getdata().size(); i++)
            {
                String VarData[] = split(responseData.getdata().get(i).toString(),'^');

                //Generate where clause
                SQL="";
                WhereClause="";
                varPos=0;
                for(int j=0; j< UField.length; j++)
                {
                    varPos = VarPosition(UField[j].toString(),VarList);
                    if(j==0)
                    {
                        WhereClause = UField[j].toString()+"="+ "'"+ VarData[varPos].toString().replace("'","") +"'";
                        UID = VarData[varPos].toString();
                    }
                    else
                    {
                        WhereClause += " and "+ UField[j].toString()+"="+ "'"+ VarData[varPos].toString().replace("'","") +"'";
                        UID += VarData[varPos].toString();
                    }
                }

                //Update command
                if(Existence("Select "+ VarList[0] +" from "+ TableName +" Where "+ WhereClause))
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Update "+ TableName +" Set ";
                            SQL+= VarList[r] + " = '"+ VarData[r].toString().replace("'","") +"'";
                        }
                        else
                        {
                            if(r == VarData.length-1)
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString().replace("'","") +"'";
                                SQL += " Where "+ WhereClause;
                            }
                            else
                            {
                                SQL+= ","+ VarList[r] + " = '"+ VarData[r].toString().replace("'","") +"'";
                            }
                        }

                        if(VarList[r].toString().toLowerCase().equals("modifydate"))
                            modifyDate = VarData[r].toString();
                    }

                    Save(SQL);
                }
                //Insert command
                else
                {
                    for(int r=0;r<VarList.length;r++)
                    {
                        if(r==0)
                        {
                            SQL = "Insert into "+ TableName +"("+ ColumnList +")Values(";
                            SQL+= "'"+ VarData[r].toString().replace("'","") +"'";
                        }
                        else
                        {
                            SQL+= ",'"+ VarData[r].toString().replace("'","") +"'";
                        }

                        if(VarList[r].toString().toLowerCase().equals("modifydate"))
                            modifyDate = VarData[r].toString();

                    }
                    SQL += ")";

                    Save(SQL);
                }

                DataList = TableName + "^" + UID + "^"+ UserId + "^" + modifyDate;
                dd = new DataClassProperty();
                dd.setdatalist(DataList);
                dd.setuniquefieldwithdata("" +
                        "TableName='"+ TableName +"' and " +
                        "UniqueID='"+ UID +"' and " +
                        "UserId='"+ UserId +"' and " +
                        "modifyDate='"+ modifyDate +"'");
                data.add(dd);
            }

            DataClass dt = new DataClass();
            dt.settablename("Sync_Management");
            dt.setcolumnlist("TableName, UniqueID, UserId, modifyDate");
            dt.setdata(data);

            Gson gson1   = new Gson();
            String json1 = gson1.toJson(dt);
            String resp1 = "";

            UploadDataJSON u = new UploadDataJSON();

            try{
                resp1=u.execute(json1).get();
            } catch (Exception e) {
                e.printStackTrace();
            }



        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        return resp;
    }


    public String[] Sync_Parameter(String TableName)
    {
        String VariableList = "";
        String UniqueField  = "";
        String SQLStr       = "";
        String SQL_VariableList = "";
        String BatchSize = "0";

        Cursor cur_H = ReadData("Select ColumnList as columnlist, UniqueID as uniqueid,ifnull(BatchSize,'0') as batchsize from DatabaseTab where tablename='"+ TableName +"'");
        cur_H.moveToFirst();

        while(!cur_H.isAfterLast())
        {
            SQLStr       = "Select "+ cur_H.getString(cur_H.getColumnIndex("columnlist")) +" from "+ TableName +" Where Upload='2'";
            VariableList = cur_H.getString(cur_H.getColumnIndex("columnlist"));
            SQL_VariableList = Convert_VariableList(TableName,VariableList);
            UniqueField  = cur_H.getString(cur_H.getColumnIndex("uniqueid"));
            BatchSize  = cur_H.getString(cur_H.getColumnIndex("batchsize"));

            cur_H.moveToNext();
        }
        cur_H.close();
        String[] ParaList = new String[]{
                SQLStr,
                VariableList,
                UniqueField,
                SQL_VariableList,
                BatchSize
        };

        return  ParaList;
    }

    private String Convert_VariableList(String TableName, String VariableList)
    {
        String finalVariableList = "";
        String[] tempList = VariableList.split(",");
        String tempVar = "";
        String temp="";
        String[] DateVarList = DateVariableList(TableName).split(",");
        int matched = 2;
        for(int i=0;i<tempList.length;i++)
        {
            temp = tempList[i];
            matched = 2;

            for(int j=0;j<DateVarList.length;j++)
            {
                if(temp.equalsIgnoreCase(DateVarList[j]))
                    matched = 1;
            }

            if(matched==1) {
                if(temp.equalsIgnoreCase("endt") | temp.equalsIgnoreCase("modifydate") | temp.equalsIgnoreCase("uploaddt"))
                    finalVariableList += finalVariableList.length() == 0 ? "Convert(varchar(19)," + tempList[i] + ",120)" : ", Convert(varchar(19)," + tempList[i] + ",120)";
                else
                    finalVariableList += finalVariableList.length() == 0 ? "Convert(varchar(10)," + tempList[i] + ",120)" : ", Convert(varchar(10)," + tempList[i] + ",120)";
            }
            else {
                if(temp.equalsIgnoreCase("upload"))
                    finalVariableList += finalVariableList.length()==0 ? "'1'" : ", '1'";
                else
                    finalVariableList += finalVariableList.length() == 0 ? tempList[i] : ", " + tempList[i];
            }
        }
        return finalVariableList;
    }

    private String DateVariableList(String TableName)
    {
        Cursor cur_H = ReadData("PRAGMA table_info('"+ TableName +"')");
        cur_H.moveToFirst();
        String temp = "";
        String type = "";
        String name = "";
        String dateVariable = "";
        while(!cur_H.isAfterLast())
        {
            type = cur_H.getString(cur_H.getColumnIndex("type"));
            name = cur_H.getString(cur_H.getColumnIndex("name")).toLowerCase();
            if((type.equalsIgnoreCase("date") | type.equalsIgnoreCase("datetime")) & !name.equalsIgnoreCase("endt") & !name.equalsIgnoreCase("modifydate"))
            {
                dateVariable += dateVariable.length()==0? cur_H.getString(cur_H.getColumnIndex("name")) : ","+  cur_H.getString(cur_H.getColumnIndex("name"));
            }

            cur_H.moveToNext();
        }
        cur_H.close();

        return dateVariable;
    }


    public void HouseholdHead_Update()
    {
        String SQL1 = "select h.vill,h.bari,h.hh,m.name from Household h inner join (select vill,bari,hh,sno,name from Member where rth='01' group by vill,bari,hh order by date(endate) desc) m on h.vill=m.vill and h.bari=m.bari and h.hh=m.hh where length(h.hhhead)=0";

        try {
            Cursor curH = ReadData(SQL1);
            curH.moveToFirst();
            while (!curH.isAfterLast()) {
                SQL1 = "Update Household set upload='2',HHHead='" + curH.getString(curH.getColumnIndex("Name")) + "'";
                SQL1 += " where vill='" + curH.getString(curH.getColumnIndex("Vill")) + "'";
                SQL1 += " and bari='" + curH.getString(curH.getColumnIndex("Bari")) + "'";
                SQL1 += " and hh='" + curH.getString(curH.getColumnIndex("HH")) + "'";
                Save(SQL1);
                curH.moveToNext();
            }
            curH.close();
        }catch (Exception ex){

        }
    }


}