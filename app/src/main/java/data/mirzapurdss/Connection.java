package data.mirzapurdss;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Common.DataClass;
import Common.DataClassProperty;
import Common.DownloadDataJSON;
import Common.FileUpload;
import Common.ProjectSetting;
import Common.UploadDataJSON;
import Common.downloadClass;
import Utility.CompressZip;

public class Connection extends SQLiteOpenHelper {
    private static final String DB_NAME = "DSSDatabase.db";
    private static final int DB_VERSION = 1;    
    
	
	private final Context dbContext;
	SQLiteDatabase db;

	private static Context ud_context;

	public Connection(Context context) {
       super(context, DB_NAME, null, DB_VERSION);		
		dbContext=context;
		ud_context = context;

		CreateOpenDatabase();

		String SQL = "";

		//19 May 2016
		CreateTable("ClusterBlock_Status","Create table ClusterBlock_Status(Cluster varchar(2),Block varchar(2),TotalBari int,TotalHH int,TotalMem int,Rnd varchar(5))");

		//17 May 2016
		SQL  = "Create table GPSData";
		SQL += "(IDNO varchar(20), Lat varchar(20), Lon varchar(20), Type int,Landmark varchar(3),Landmark_Name varchar(60),Established int,ContactNo varchar(30),";
		SQL += "Note varchar(100),Accuracy int,TimeStart varchar(10),TimeEnd varchar(10),Sat_Connected int,EnDt varchar(20),UserId varchar(10),Upload varchar(1),UploadDT datetime)";
		CreateTable("GPSData",SQL);

		CreateTable("LastLogin","Create Table LastLogin(BlockNo varchar(2))");

		SQL = "CREATE TABLE Mig_PregHis(Vill varchar(3),Bari varchar(4),Hh varchar(2),Sno varchar(2),Pno varchar(8),Visit varchar(1),MarM varchar(2),MarY varchar(4),";
		SQL += "Births varchar(2),LiveHh varchar(2),SLiveHh varchar(2),DLiveHh varchar(2),LiveOut varchar(2),SLiveOut varchar(2),DLiveOut varchar(2),Died varchar(2),";
		SQL += "SDied varchar(2),DDied varchar(2),Abor varchar(2),TAbor varchar(2),TotPreg varchar(2),Vdate varchar(10),Rnd varchar(2),PageNo varchar(2),Status varchar(1),";
		SQL += "Upload varchar(1),Lat varchar(20),Lon varchar(20))";
		CreateTable("Mig_PregHis",SQL);
		if(!isFieldExist("Mig_PregHis","modifydate")){
			Save("Alter table Mig_PregHis add column modifydate datetime");
		}

		/*SQL = "CREATE TABLE Mig_Immunization(";
		SQL += " Vill varchar(3),Bari varchar(4),HH varchar(2),Sno varchar(2),PNO varchar(8),Status varchar(2),BCG varchar(1),BCGDT varchar(10),BCGSource varchar(2),Penta1 varchar(1),Penta1DT varchar(10),Penta1Source varchar(2),";
		SQL += "Penta2 varchar(1),Penta2DT varchar(10),Penta2Source varchar(2),Penta3 varchar(1),Penta3DT varchar(10),Penta3Source varchar(2),PCV1 varchar(1),PCV1DT varchar(10),PCV1Source varchar(2),PCV2 varchar(1),PCV2DT varchar(10),";
		SQL += "PCV2Source varchar(2),PCV3 varchar(1),PCV3DT varchar(10),PCV3Source varchar(2),OPV0 varchar(1),OPV0DT varchar(10),OPV0Source varchar(2),OPV1 varchar(1),OPV1DT varchar(10),OPV1Source varchar(2),OPV2 varchar(1),";
		SQL += "OPV2DT varchar(10),OPV2Source varchar(2),OPV3 varchar(1),OPV3DT varchar(10),OPV3Source varchar(2),OPV4 varchar(1),OPV4DT varchar(10),OPV4Source varchar(2),Measles varchar(1),MeaslesDT varchar(10),MeaslesSource varchar(2),";
		SQL += "MR varchar(1),MRDT varchar(10),MRSource varchar(2),Rota varchar(1),RotaDT varchar(10),RotaSource varchar(2),MMR varchar(1),MMRDT varchar(10),MMRSource varchar(2),Typhoid varchar(1),TyphoidDT varchar(10),TyphoidSource varchar(2),";
		SQL += "Influ varchar(1),InfluDT varchar(10),InfluSource varchar(2),HepaA varchar(1),HepaADT varchar(10),HepaASource varchar(2),ChickenPox varchar(1),ChickenPoxDT varchar(10),ChickenPoxSource varchar(2),Rabies varchar(1),";
		SQL += "RabiesDT varchar(10),RabiesSource varchar(2),IPV varchar(1),IPVDT varchar(10),IPVSource varchar(2),VitaminA varchar(1),VitaminADT varchar(10),VitaminASource varchar(2),EnDt varchar(20),Upload varchar(1))";
		*/

		SQL = "CREATE TABLE Mig_Immunization(";
		SQL += " Vill varchar(3),Bari varchar(4),HH varchar(2),Sno varchar(2),PNO varchar(8),Status varchar(2),BCG varchar(1),BCGDT varchar(10), BCGDTDk varchar(1),BCGSource varchar(2),Penta1 varchar(1),Penta1DT varchar(10), Penta1DTDK varchar(1),Penta1Source varchar(2),";
		SQL += "Penta2 varchar(1),Penta2DT varchar(10), Penta2DTDK varchar(1),Penta2Source varchar(2),Penta3 varchar(1),Penta3DT varchar(10), Penta3DTDK varchar(1),Penta3Source varchar(2),PCV1 varchar(1),PCV1DT varchar(10), PCV1DTDK varchar(1),PCV1Source varchar(2),PCV2 varchar(1),PCV2DT varchar(10), PCV2DTDK varchar(1),";
		SQL += "PCV2Source varchar(2),PCV3 varchar(1),PCV3DT varchar(10), PCV3DTDK varchar(1),PCV3Source varchar(2),OPV0 varchar(1),OPV0DT varchar(10), OPV0DTDK varchar(1),OPV0Source varchar(2),OPV1 varchar(1),OPV1DT varchar(10), OPV1DTDK varchar(1),OPV1Source varchar(2),OPV2 varchar(1),";
		SQL += "OPV2DT varchar(10), OPV2DTDK varchar(1),OPV2Source varchar(2),OPV3 varchar(1),OPV3DT varchar(10), OPV3DTDK varchar(1),OPV3Source varchar(2),OPV4 varchar(1),OPV4DT varchar(10),OPV4DTDK varchar(1),OPV4Source varchar(2),Measles varchar(1),MeaslesDT varchar(10),MeaslesDTDK varchar(1),MeaslesSource varchar(2),";
		SQL += "MR varchar(1),MRDT varchar(10),MRDTDK varchar(1),MRSource varchar(2),Rota varchar(1),RotaDT varchar(10), RotaDTDK varchar(1),RotaSource varchar(2),MMR varchar(1),MMRDT varchar(10), MMRDTDK varchar(1),MMRSource varchar(2),Typhoid varchar(1),TyphoidDT varchar(10),TyphoidDTDK varchar(1),TyphoidSource varchar(2),";
		SQL += "Influ varchar(1),InfluDT varchar(10), InfluDTDK varchar(1),InfluSource varchar(2),HepaA varchar(1),HepaADT varchar(10), HepaADTDk varchar(1),HepaASource varchar(2),ChickenPox varchar(1),ChickenPoxDT varchar(10), ChickenPoxDTDk varchar(1),ChickenPoxSource varchar(2),Rabies varchar(1),";
		SQL += "RabiesDT varchar(10), RabiesDTDk varchar(1),RabiesSource varchar(2),IPV varchar(1),IPVDT varchar(10),IPVDTDk varchar(1),IPVSource varchar(2), fIPV1 varchar (1),fIPVDT1 varchar (10), Fipvdt1dk varchar(1),fIPVSource1 varchar (2),fIPV2 varchar (1),fIPVDT2 varchar (10),fIPVSource2 varchar (2),";
		SQL += "VitaminA varchar(1),VitaminADT varchar(10), VitaminADTDk varchar(1),VitaminASource varchar(2),EnDt varchar(20),Upload varchar(1))";

		CreateTable("Mig_Immunization",SQL);
		if(!isFieldExist("Mig_Immunization","modifydate")){
			Save("Alter table Mig_Immunization add column modifydate datetime");
		}

		CreateTable("Death"     ,"CREATE TABLE Death(Vill varchar(3) ,Bari varchar(4) ,HH varchar(2) ,SNo varchar(2) ,PNo varchar(8) ,Status varchar(2) ,DthPlace varchar(2) ,FacName varchar(2) ,FacOther varchar(50) ,Treatment varchar(1) ,WhenTreat varchar(3) ,Facility varchar(2) ,Disp varchar(2) ,WhoDisp varchar(50) ,Type varchar(2) ,Time varchar(2) , EverPreg varchar(1) ,PregonDeath varchar(1) ,LastPregTime varchar(2) ,DMY varchar(1) ,CauseofDeath varchar(2) , StartTime varchar(10) , EndTime varchar(10) ,   Lat varchar(50) ,       Lon varchar(50) ,       UserId varchar(10) ,    EnDt varchar(20) ,      Upload varchar(1) )");
		CreateTable("Death_Temp","CREATE TABLE Death_Temp(Vill varchar(3) ,Bari varchar(4) ,HH varchar(2) ,SNo varchar(2) ,PNo varchar(8) ,Status varchar(2) ,DthPlace varchar(2) ,FacName varchar(2) ,FacOther varchar(50) ,Treatment varchar(1) ,WhenTreat varchar(3) ,Facility varchar(2) ,Disp varchar(2) ,WhoDisp varchar(50) ,Type varchar(2) ,Time varchar(2) ,    EverPreg varchar(1) ,PregonDeath varchar(1) ,LastPregTime varchar(2) ,DMY varchar(1) ,CauseofDeath varchar(2) , StartTime varchar(10) , EndTime varchar(10) ,   Lat varchar(50) ,       Lon varchar(50) ,       UserId varchar(10) ,    EnDt varchar(20) ,      Upload varchar(1) )");

		//16 May 2016
		//important for performance issue
		Cursor b=db.rawQuery("Select * from Baris limit 1",null);
		if(b.getColumnCount()==14)
		{
			Save("Alter table Baris add column Cluster varchar(2) default ''");
			Save("update baris set cluster=(select cluster from mdssvill where vill=baris.vill)");
		}
		b.close();

		Save("update baris set cluster=(select cluster from mdssvill where vill=baris.vill) where length(cluster)=0 or cluster is null");


		//25 Oct 2015
		Cursor c1=db.rawQuery("Select * from Events limit 1",null);
		if(c1.getColumnCount()==14)
		{
			Save("Alter table Events add column info5 varchar(10)");
			Save("Alter table tTrans add column info5 varchar(10)");
		}
		c1.close();

		/*
		Cursor d=db.rawQuery("Select * from Death limit 1",null);
		if(d.getColumnCount()!=27)
		 {
			Save("Drop table Death");
			Save("Drop table Death_Temp");
		 }
		d.close();
		*/

		//25 Oct 2015
		Cursor d1=db.rawQuery("Select * from Death limit 1",null);
		if(d1.getColumnCount()==27)
		 {
			Save("Alter table Death add      column Status varchar(2)");
			Save("Alter table Death_Temp add column Status varchar(2)");
		 }
		d1.close();

		Cursor d2=db.rawQuery("Select * from Immunization limit 1",null);
		if(d2.getColumnCount()==71)
		 {
				Save("Alter table Immunization add column IPV varchar(1)");
				Save("Alter table Immunization add column IPVDT varchar(10)");
				Save("Alter table Immunization add column IPVSource varchar(2)");

				Save("Alter table Immunization add column VitaminA varchar(1)");
				Save("Alter table Immunization add column VitaminADT varchar(10)");
				Save("Alter table Immunization add column VitaminASource varchar(2)");

				 Save("Alter table Immunization add column BCGDTDk varchar(1)");
				 Save("Alter table Immunization add column Penta1DTDK varchar(1)");
				 Save("Alter table Immunization add column Penta2DTDK varchar(1)");
				 Save("Alter table Immunization add column Penta3DTDK varchar(1)");
				 Save("Alter table Immunization add column PCV1DTDK varchar(1)");
				 Save("Alter table Immunization add column PCV2DTDK varchar(1)");
				 Save("Alter table Immunization add column PCV3DTDK varchar(1)");
				 Save("Alter table Immunization add column OPV0DTDK varchar(1)");
				 Save("Alter table Immunization add column OPV1DTDK varchar(1)");
				 Save("Alter table Immunization add column OPV2DTDK varchar(1)");
				 Save("Alter table Immunization add column OPV3DTDK varchar(1)");
				 Save("Alter table Immunization add column OPV4DTDK varchar(1)");
				 Save("Alter table Immunization add column MeaslesDTDK varchar(1)");
				 Save("Alter table Immunization add column MRDTDK varchar(1)");
				 Save("Alter table Immunization add column RotaDTDK varchar(1)");
				 Save("Alter table Immunization add column MMRDTDK varchar(1)");
				 Save("Alter table Immunization add column TyphoidDTDK varchar(1)");
				 Save("Alter table Immunization add column InfluDTDK varchar(1)");
				 Save("Alter table Immunization add column HepaADTDk varchar(1)");
				 Save("Alter table Immunization add column ChickenPoxDTDk varchar(1)");
				 Save("Alter table Immunization add column RabiesDTDk varchar(1)");
				 Save("Alter table Immunization add column IPVDTDk varchar(1)");
				 Save("Alter table Immunization add column Fipvdt1dk varchar(1)");
				 Save("Alter table Immunization add column Fipvdt2Dk varchar(1)");
				 Save("Alter table Immunization add column VitaminADTDk varchar(1)");


				Save("Alter table ImmunizationTemp add column IPV varchar(1)");
				Save("Alter table ImmunizationTemp add column IPVDT varchar(10)");
				Save("Alter table ImmunizationTemp add column IPVSource varchar(2)");

				Save("Alter table ImmunizationTemp add column VitaminA varchar(1)");
				Save("Alter table ImmunizationTemp add column VitaminADT varchar(10)");
				Save("Alter table ImmunizationTemp add column VitaminASource varchar(2)");

				 Save("Alter table ImmunizationTemp add column BCGDTDk varchar(1)");
				 Save("Alter table ImmunizationTemp add column Penta1DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column Penta2DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column Penta3DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column PCV1DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column PCV2DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column PCV3DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column OPV0DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column OPV1DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column OPV2DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column OPV3DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column OPV4DTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column MeaslesDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column MRDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column RotaDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column MMRDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column TyphoidDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column InfluDTDK varchar(1)");
				 Save("Alter table ImmunizationTemp add column HepaADTDk varchar(1)");
				 Save("Alter table ImmunizationTemp add column ChickenPoxDTDk varchar(1)");
				 Save("Alter table ImmunizationTemp add column RabiesDTDk varchar(1)");
				 Save("Alter table ImmunizationTemp add column IPVDTDk varchar(1)");
				 Save("Alter table ImmunizationTemp add column Fipvdt1dk varchar(1)");
				 Save("Alter table ImmunizationTemp add column Fipvdt2Dk varchar(1)");
				 Save("Alter table ImmunizationTemp add column VitaminADTDk varchar(1)");
		 }
		d2.close();

		//Date: 12 Apr 2015
        //if(!Existence("select * from POP where Code='06'"))
        //  {
        //    Save("Insert into POP(Code,Place)Values('06','06 - মির্জাপুরের ভিতরে এন.জি.ও/ক্লিনিক/সরকারী মাতৃসদন')");
        //  }
                
                
		//Update data into Tab Database: may be run any time
		//-------------------------------------------------------------------------------------------
	  	Save("update Member set Pstat='', LmpDt='' where EnType='25' and length(pstat)<>0");
		Save("update Member set Pstat='', LmpDt='' where Sex='1' and length(pstat)<>0");

	  	Save("update Member set Pstat='',LmpDt='' where ms='30'");
        //db.execSQL("Update Immunization Set PNo=ifnull((Select ifnull(PNo,'') from Member where Vill||Bari||HH||SNo=Immunization.vill||Immunization.bari||Immunization.hh||Immunization.sno),'') where length(pno)=0 or pno is null");
	    //db.execSQL("Delete from Visits where cast(Rnd as int)<20");
	        
	    //10 11 2015
	    //db.execSQL("update death set status='01' where status is null or length(status)=0");
    }
	
	@Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Login");
        onCreate(db);
    }
    
	public void CreateOpenDatabase()
	{		
		File sdcard = Environment.getExternalStorageDirectory();
		String dbfile = sdcard.getAbsolutePath() + File.separator + "DSSDatabase" + File.separator +  DB_NAME;
        db=dbContext.openOrCreateDatabase(dbfile, SQLiteDatabase.CREATE_IF_NECESSARY,null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
          
	}
	
	public SQLiteDatabase getReadableDatabase()
	{
	    /*db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
	            + File.separator + DATABASE_NAME, null,
	            SQLiteDatabase.OPEN_READONLY);*/
		
		SQLiteDatabase db1;
		db1=SQLiteDatabase.openDatabase(DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
	    return db1;
	}
	
	public SQLiteDatabase getWritableDatabase()
	{
	    /*db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH
	            + File.separator + DATABASE_NAME, null,
	            SQLiteDatabase.OPEN_READWRITE);*/
		
		SQLiteDatabase db1;
		db1=SQLiteDatabase.openDatabase(DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
	    return db1;
	}
	
	public boolean TableExists(String TableName)
	{        
        Cursor c = null;
        boolean tableExists = false;
        try
        {
            c = db.rawQuery("Select * from "+TableName,null);
            tableExists = true;
            c.close();
        }
        catch (Exception e) {
        }
        return tableExists;
	}
	
	public void CreateTable(String TableName,String SQL)
	{
		if(!TableExists(TableName))
		{
			db.execSQL(SQL);
		}
	}
	
	public Cursor ReadData(String SQL)
	{
		Cursor cur=db.rawQuery(SQL, null);
		return cur;
	}	
	
	public boolean Existence(String SQL)
	{
		Cursor cur=db.rawQuery(SQL, null);
		if(cur.getCount()==0)
		{
			cur.close();
			return false;
		}
		else
		{
			cur.close();
			return true;
		}
	}
	
	public String ReturnSingleValue(String SQL)
	{
			Cursor cur=db.rawQuery(SQL, null);
			String retValue="";
			cur.moveToFirst();
		        while(!cur.isAfterLast())
		        {
		        	retValue=cur.getString(0);
		        	cur.moveToNext();
		        }  	
		        cur.close();  			
		  return retValue;
	}
	
	public void Save(String SQL)
	{
		db.execSQL(SQL);
	}

	public String SaveData(String SQL) {
		String response = "";
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL(SQL);
		}catch(Exception ex){
			response = ex.getMessage();
		}finally {
			db.close();
		}
		return response;
	}

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

	
	//Message Box
	//......................................................................................	
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
	//......................................................................................
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
	//......................................................................................
	public ArrayAdapter<String> getArrayAdapter(String SQL){
		List<String> dataList = new ArrayList<String>();
        Cursor cursor = ReadData(SQL);
        if (cursor.moveToFirst()) {
            do {
            	dataList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
 
        cursor.close();
 
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.dbContext,
                android.R.layout.simple_spinner_item, dataList);
 
        return dataAdapter;        
	}

	public ArrayAdapter<String> getArrayAdapterMultiline(String SQL){
		List<String> dataList = new ArrayList<String>();
		Cursor cursor = ReadData(SQL);
		if (cursor.moveToFirst()) {
			do {
				dataList.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		cursor.close();

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.dbContext,
				R.layout.multiline_spinner_dropdown_item, dataList);

		return dataAdapter;
	}


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
	
	
	//..........................................................................................................
	// Download data from Database Server
	//..........................................................................................................
	public String DownloadData(String SQLStr, String TableName,String ColumnList, String UniqueField)
	{
		String rep = "";
    	String SQL = "";
    	
    	int totalDownload=0;
    	String DownloadStatus="";
    	String WhereClause="";
    	int varPos=0;
    	
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
    		String UField[]  = UniqueField.split(",");
    		String VarList[] = ColumnList.split(",");
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	        	//String VarData[] = DataArray[i].split("\\^");
	        	String VarData[] = split(DataArray[i],'^');
	        	
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
			        
			        if(!TableName.equalsIgnoreCase("events"))
			        {
				        db.execSQL(SQL);
			        	totalDownload+=1;
			        }
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
			        
			        db.execSQL(SQL);
		        	totalDownload+=1;
	        	}
		        
	        	//update download status on server
	        	rep = ExecuteCommandOnServer("Update "+ TableName +" set Download='1', DownloadDt='"+ Global.DateTimeNow() +"' Where "+ WhereClause);
	    	}
	    	
	    	DownloadStatus="Total download completed: "+ totalDownload +" of "+ DataArray.length;
	    	
    	return DownloadStatus;
    	}
    	catch(Exception e)
    	{
    		return "Download Error:"+ e.getMessage();
    	}
	}	
	
	
	public String UpdateData(String SQLStr, String TableName,String ColumnList, String UniqueField)
	{
		String rep = "";
    	String SQL = "";
    	
    	int totalDownload=0;
    	String DownloadStatus="";
    	String WhereClause="";
    	int varPos=0;
    	
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
    		String UField[]  = UniqueField.split(",");
    		String VarList[] = ColumnList.split(",");
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	        	//String VarData[] = DataArray[i].split("\\^");
	        	String VarData[] = split(DataArray[i],'^');
	        	
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
			        
			        if(!TableName.equalsIgnoreCase("events"))
			        {
				        db.execSQL(SQL);
			        	totalDownload+=1;
			        }
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
			        
			        db.execSQL(SQL);
		        	totalDownload+=1;
	        	}
		        
	        	//update download status on server
	        	rep = ExecuteCommandOnServer("Update "+ TableName +" set UpdateNeeded='2' Where "+ WhereClause);
	    	}
	    	
	    	DownloadStatus="Total update completed: "+ totalDownload +" of "+ DataArray.length;
	    	
    	return DownloadStatus;
    	}
    	catch(Exception e)
    	{
    		return "Update Error:"+ e.getMessage();
    	}
	}	
	
	public String PNOUpdate(String SQLStr, String ColumnList, String UniqueField)
	{
	String rep = "";
    	String SQL = "";
    	
    	int totalDownload=0;
    	String DownloadStatus="";
    	String WhereClause="";
    	int varPos=0;
    	
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
    		String UField[]  = UniqueField.split(",");
    		String VarList[] = ColumnList.split(",");
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	        	String VarData[] = split(DataArray[i],'^');
	        	
	        	//Generate where clause
	        	SQL="";
	        	WhereClause="";
	        	varPos=0;
	        	String PNO="";
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
	        		
	        		db.execSQL("Update Member  Set PNo='"+ PNO +"' Where "+ WhereClause);
	        		db.execSQL("Update Events  Set PNo='"+ PNO +"' Where "+ WhereClause);
	        		db.execSQL("Update PregHis Set PNo='"+ PNO +"' Where "+ WhereClause);

		        	//update download status on server
	        		rep = ExecuteCommandOnServer("Update Member set UpdateNeeded='4' Where "+ WhereClause);
	        	}
	    	}
	    	
	    	DownloadStatus="Total update completed: "+ totalDownload +" of "+ DataArray.length;
	    	
    	return DownloadStatus;
    	}
    	catch(Exception e)
    	{
    		return "Update Error:"+ e.getMessage();
    	}
	}	
	
	//02 Apr 2014
	public String EventDelete(String SQLStr, String ColumnList, String UniqueField)
	{
		String rep = "";
    	String SQL = "";
    	
    	int totalDownload=0;
    	String DownloadStatus="";
    	String WhereClause="";
    	int varPos=0;
    	
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
    		String UField[]  = UniqueField.split(",");
    		String VarList[] = ColumnList.split(",");
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	        	String VarData[] = split(DataArray[i],'^');
	        	
	        	//Generate where clause
	        	SQL="";
	        	WhereClause="";
	        	varPos=0;
	        	String PNO="";
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
	        		db.execSQL("Delete from Events Where "+ WhereClause);

		        	//update download status on server
	        		rep = ExecuteCommandOnServer("Update EventsDelete set DeleteNeeded='2' Where "+ WhereClause);
	        	}
	    	}
	    	
	    	DownloadStatus="Total update completed: "+ totalDownload +" of "+ DataArray.length;
	    	
    	return DownloadStatus;
    	}
    	catch(Exception e)
    	{
    		return "Update Error:"+ e.getMessage();
    	}
	}
	
	//Downlaod SQL Command from server and execute on local device
	public void ExecuteSQLQuery(String ClusterCode)
        {
	    String rep = "";
            try
            {
                    String SQLStr = "Select cast(SL as varchar(10))+'^'+SQL from SQLQueryForTab where Cluster='"+ ClusterCode +"' and Status='no'";
                    DownloadData d=new DownloadData();
                    d.Method_Name="DownloadData";
                    d.SQLStr=SQLStr;
                    
                    String DataArray[]=null;
                    DataArray=d.execute("").get();
                    
                    for(int i=0;i<DataArray.length;i++)
                    {
                        String VarData[] = split(DataArray[i],'^');
                        db.execSQL(VarData[1].toString());
                        
                        //update download status on server
                        rep = ExecuteCommandOnServer("Update SQLQueryForTab set Status='yes' Where SL='"+ VarData[0].toString() +"'");
                    }
            }
            catch(Exception e)
            {                
            }
        }       
	
	
	public void DownloadMigData(String SQLStr, String TableName,String ColumnList)
	{	
    	String[] col;
    	String SQL="";
    	String SQL1="";
    	int totalCol=0;

    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	    		SQL="";
	    		SQL1="";
	    		
	    		//Calculate total number of column
	    		totalCol= Connection.split(DataArray[i], Global.VariableSeperator).length;
	    		col=new String[totalCol];
	    		
	    		//Separate column value to an array
	    		col=Connection.split(DataArray[i],  Global.VariableSeperator);
	    		
		        for(int r=0;r<col.length;r++)
		        {      
		        	if(r==0)
		        	{
		        		SQL+="'"+ col[r].toString() +"'";
		        	}
		        	else
		        	{
		        		SQL+=",'"+ col[r].toString() +"'";
		        	}
		        }
		        
		        
		        //save data into databse table
		        SQL1 =  "Insert into "+ TableName +"("+ColumnList+")Values(";
	        	SQL1 += SQL+")";

	        	db.execSQL(SQL1);
	    	}	    	
    	}
    	catch(Exception e)
    	{
    		return;
    	}
	}
	
	public void DownloadID(String SQLStr, String TableName)
	{	
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();    	
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{
	        	db.execSQL("Insert into ServerID Values('"+ DataArray[i] +"')");
	    	}	 
	    	String WhereCondition="";
	    	if (TableName.equalsIgnoreCase("household"))
	    		WhereCondition = "Vill||Bari||HH not in(select IDField from ServerID)";
	    	
	    	Save("Update "+ TableName +" Set Upload='2' where "+ WhereCondition);
    	}
    	catch(Exception e)
    	{
    		return;
    	}
	}
	
	
	//Bari Name Update
	//..........................................................................................................
	public void BariNameUpdate(String SQLStr, String TableName,String ColumnList)
	{	
    	String[] col;
    	String SQL1="";
    	int totalCol=0;

    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{	    		
	    		//Calculate total number of column
	    		totalCol= Connection.split(DataArray[i], Global.VariableSeperator).length;
	    		col=new String[totalCol];
	    		
	    		//Separate column value to an array
	    		col=Connection.split(DataArray[i],  Global.VariableSeperator);
	    		
	        	SQL1 = "Update Baris Set ";
       				    		
		        for(int r=0;r<col.length;r++)
		        {      
		        	if(r==4)
		        		SQL1 += " BariName='"+ col[r].toString() +"',";
		        	else if(r==5)
		               	SQL1 += " BariLoc='"+ col[r].toString() +"',";
		        	else if(r==6)
		               	SQL1 += " Xdec='"+ col[r].toString() +"',";
		        	else if(r==7)
		               	SQL1 += " Xmin='"+ col[r].toString() +"',";
		        	else if(r==8)
		               	SQL1 += " Xsec='"+ col[r].toString() +"',";
		        	else if(r==9)
		               	SQL1 += " Ydec='"+ col[r].toString() +"',";
		        	else if(r==10)
		               	SQL1 += " Ymin='"+ col[r].toString() +"',";
		        	else if(r==11)
		               	SQL1 += " Ysec='"+ col[r].toString() +"'";
		        }
		        SQL1 += " where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and length(trim(bariname))=0";
		        
		        
	        	db.execSQL(SQL1);
	        	
	        	String r = ExecuteCommandOnServer("Update Baris set BNameUpdate='1'  where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and BNameUpdate='2'");
	    	}	    	
    	}
    	catch(Exception e)
    	{
    		return;
    	}
	}
	
	//Block Update
	//..........................................................................................................	
	public void BlockUpdate(String SQLStr, String TableName,String ColumnList)
	{	
    	String[] col;
    	int totalCol=0;
    	try
    	{
	    	DownloadData d=new DownloadData();
	    	d.Method_Name="DownloadData";
	    	d.SQLStr=SQLStr;
	    	
	    	String DataArray[]=null;
	    	DataArray=d.execute("").get();
	    	
	    	
	    	for(int i=0;i<DataArray.length;i++)
	    	{	    		
	    		//Calculate total number of column
	    		totalCol= Connection.split(DataArray[i], Global.VariableSeperator).length;
	    		col=new String[totalCol];
	    		
	    		//Separate column value to an array
	    		col=Connection.split(DataArray[i],  Global.VariableSeperator);
	    		
	        	db.execSQL("Update Baris set Block='"+ col[5].toString() +"' where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and Block <> '"+ col[5].toString() +"'");
	        	db.execSQL("Update Household set Block='"+ col[5].toString() +"' where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and Block <> '"+ col[5].toString() +"'");
	        	
	        	String r = "";
	        	r = ExecuteCommandOnServer("Update BlockUpdateMobile set Status='1',UpdateDT='"+ Global.DateTimeNow() +"' where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"'");
	        	r = ExecuteCommandOnServer("Update Baris set Block='"+ col[5].toString() +"' where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and Block<>'"+ col[5].toString() +"'");
	        	r = ExecuteCommandOnServer("Update Household set Block='"+ col[5].toString() +"' where Vill='"+ col[2].toString() +"' and Bari='"+ col[3].toString() +"' and Block<>'"+ col[5].toString() +"'");
	        	
	    	}	    	
    	}
    	catch(Exception e)
    	{
    		return;
    	}
	}
	
	
	//Execute command on Database Server
	//..........................................................................................................
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
	
	// Data Upload to Database Server
	//..........................................................................................................	
	public String UploadData(String[] DataArray,String TableName,String ColumnList,String UniqueFields)
	{
		String[] D=new String[DataArray.length];
		String[] Col=ColumnList.split(",");
		
		String response="";
		int totalRec=0;
        for(int i=0;i<DataArray.length;i++)
        {
        	//Generate Where Clause
        	String VarName[]=UniqueFields.split(",");
        	String VarData[]=DataArray[i].toString().split("\\^");
        	int varPos=0;
        	
        	String WhereClause="";
        	for(int j=0; j< VarName.length; j++)
        	{
        		varPos=VarPosition(VarName[j].toString(),Col);
        		if(j==0)
        		{
        			WhereClause = VarName[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
        		}
        		else
        		{
        			WhereClause += " and "+VarName[j].toString()+"="+ "'"+ VarData[varPos].toString() +"'";
        		}
        	}        	        	
        	
        	//Calling web service through class: UploadData
    		UploadData u = new UploadData();
        	u.TableName			  = TableName;     	
        	u.ColumnList		  = ColumnList;
        	u.UniqueFieldWithData = WhereClause;
				try {					
			    	response=u.execute(DataArray[i]).get(); 
			    	if(response.equals("done"))
			    	{
			    		db.execSQL("Update "+ TableName +" Set Upload='1' where "+ WhereClause);
			    		totalRec+=1;
			    	}
			    	else
			    	{
			    	    String a=response;
			    	}
			    	
				} catch (Exception e) {
					e.printStackTrace();
				}
        }
        return Integer.toString(totalRec);
	}

	//Find the variable positions in an array list
	//..........................................................................................................	
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
	//..........................................................................................................
	public String[] GenerateArrayList(String VariableList,String TableName)
	{
		Cursor cur_H;
		String SQL = "";
		
		//Original
		cur_H = ReadData("Select "+ VariableList +" from "+ TableName +" where Upload in('2')");
		//cur_H = ReadData("Select "+ VariableList +" from "+ TableName +" where visit='1' and Upload in('1','2')");
		
		//31 Dec 2014
		/*if(TableName.equalsIgnoreCase("household"))
			SQL = "Select "+ VariableList +" from "+ TableName +" where length(contactno)<>0";
		else if(TableName.equalsIgnoreCase("visits"))
			SQL = "Select "+ VariableList +" from "+ TableName +" where length(note)<>0";
		cur_H = ReadData(SQL);
		*/
		
		
		
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
					DataList=cur_H.getString(c).toString();
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
	
	
	
	// Getting result from database server based on SQL
	//..........................................................................................................
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
	
	// Getting the devise setting information from database server
	//..........................................................................................................
	public String GetCluster()
	{
		String settingDcode="";
		String settingUPcode="";
		String settingAreaCode="";
		
		Cursor cur=ReadData("Select * from Setting");
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			settingDcode=cur.getString(0);
			settingUPcode=cur.getString(1);
			settingAreaCode=cur.getString(2);	            				
			cur.moveToNext();
		}
		cur.close();
		
		return settingDcode+","+settingUPcode+","+settingAreaCode;
	}

		
	//Copy database to SD card (need to check)*********** 29 dec 2012
	//..........................................................................................................
	/*
	public void copyDataBaseToSdcard() throws IOException{
    	
        final Calendar c = Calendar.getInstance();
        int dateYear = c.get(Calendar.YEAR);
        int dateMonth = c.get(Calendar.MONTH)+1;
        int dateDay = c.get(Calendar.DAY_OF_MONTH);
        long mil = System.currentTimeMillis();
        	
        	InputStream databaseInput = null;
            
        	String inFileName = DB_PATH + DB_NAME;
            
            String dDirName = dateDay+"-"+dateMonth+"-"+dateYear; 

            String outFileName = Environment.getExternalStorageDirectory()
            + "/icddrbDB/"+dDirName+"/"+DB_NAME+"_("+dateDay+"-"+dateMonth+"-"+dateYear+")_"+mil;
            
            String outDir = Environment.getExternalStorageDirectory()
            + "/icddrbDB/";
            
            String outDatedDir = Environment.getExternalStorageDirectory()
            + "/icddrbDB/"+dDirName;
            
            File dir = new File(outDir);
            File dDir = new File(outDatedDir);
            if(dir.exists()){
            	if(dDir.exists()){
            		File file = new File(outFileName);
            		if(file.exists()){    			
            			if(file.delete()){
            				Log.e("deleting","file");
            			}
            		}	
            	}else{
            		if(dDir.mkdirs()){
        				Log.e("making",outDatedDir+"dir");
        			}
            	}
            }else{
    			if(dir.mkdirs()){
    				Log.e("making","dir");
            		if(dDir.mkdirs()){
        				Log.e("making",outDatedDir+"dir");
        			}
    			}
            }

            OutputStream databaseOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            File f = new File(inFileName);
            if(f.exists()){
                databaseInput = new FileInputStream(f);
                while((length = databaseInput.read(buffer)) > 0) 
                {
                    databaseOutput.write(buffer, 0, length);
                    databaseOutput.flush();
                }
                databaseInput.close();

                databaseOutput.flush();
                databaseOutput.close();
            }
            
            
        	databaseInput = null;
        	
            inFileName = DB_PATH + DB_NAME;
            
            outFileName = Environment.getExternalStorageDirectory()+"/external_sd"
            + "/icddrbDB/"+dDirName+"/"+DB_NAME+"_("+dateDay+"-"+dateMonth+"-"+dateYear+")_"+mil;
            
            outDir = Environment.getExternalStorageDirectory()+"/external_sd"
            + "/icddrbDB/";
            
            outDatedDir = Environment.getExternalStorageDirectory()+"/external_sd"
            + "/icddrbDB/"+dDirName;
            
            dir = new File(outDir);
            dDir = new File(outDatedDir);
            if(dir.exists()){
            	if(dDir.exists()){
            		File file = new File(outFileName);
            		if(file.exists()){    			
            			if(file.delete()){
            				Log.e("deleting","file");
            			}
            		}	
            	}else{
            		if(dDir.mkdirs()){
        				Log.e("making",outDatedDir+"dir");
        			}
            	}
            }else{
    			if(dir.mkdirs()){
    				Log.e("making","dir");
            		if(dDir.mkdirs()){
        				Log.e("making",outDatedDir+"dir");
        			}
    			}
            }
            databaseOutput = new FileOutputStream(outFileName);

            buffer = new byte[1024];
            f = new File(inFileName);
            if(f.exists()){
                databaseInput = new FileInputStream(f);
                while((length = databaseInput.read(buffer)) > 0) 
                {
                    databaseOutput.write(buffer, 0, length);
                    databaseOutput.flush();
                }
                databaseInput.close();

                databaseOutput.flush();
                databaseOutput.close();
            }
    }
*/
	
	public static String DateFormatDDMMYYYY(String date)
    {	
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(df.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		date = df.format(c.getTime());
		return date;
    }
	
	public static String DateFormatYYYYMMDD(String date)
    {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(df.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		date = df.format(c.getTime());
		return date;
    }
	
	/*public static String addDaysYYYYMMDD(String date, int days)
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return sdf.format(cal.getTime());
    }	

	public static String addDaysDDMMYYYY(String date, int days)
    {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return sdf.format(cal.getTime());
    }
	*/

	
	public void imageUploading(String pathToOurFile)

	{
			pathToOurFile="/mnt/sdcard/abc1.jpg";

	       //Log.d("Savepoint 1", "Done");

	       //String urlServer = "http://169.254.245.231:806/imageServer.aspx?mn=mob";
			String urlServer = "http://chu.icddrb.org/mob/databaseUpload.aspx?mn=mob";
	       int bytesRead, bytesAvailable, bufferSize;
	           byte[] buffer;
	           int maxBufferSize = 1*1024*1024;
	           FileInputStream fileInputStream;

	        //DataInputStream DinputStream = null;
	        DataOutputStream DoutputStream = null ;

	         try
	           {

	           fileInputStream = new FileInputStream(pathToOurFile);

	           URL url = new URL(urlServer);
	           HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	           // Allow Inputs & Outputs
	           connection.setDoInput(true);
	           connection.setDoOutput(true);
	           connection.setChunkedStreamingMode(0);
	           connection.setUseCaches(false);

	           // Enable POST method
	           connection.setRequestMethod("POST");

	           connection.setRequestProperty("Connection", "Keep-Alive");
	           connection.setRequestProperty("Content-Type",  "multipart/form-data");
	           connection.setRequestProperty("SD-FileName", "abc3.jpg");//This will be the file name

	           //Log.d("Savepoint 2", "Done");

	           OutputStream out = new BufferedOutputStream( connection.getOutputStream());
	           DoutputStream = new DataOutputStream(out);

	           bytesAvailable = fileInputStream.available();
	           bufferSize = Math.min(bytesAvailable, maxBufferSize);
	           buffer = new byte[bufferSize];

	           // Read file
	           bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	           while (bytesRead > 0)
	           {
	               DoutputStream.write(buffer, 0, bufferSize);
	               bytesAvailable = fileInputStream.available();
	               bufferSize = Math.min(bytesAvailable, maxBufferSize);
	               bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	           }

	            int serverResponseCode = connection.getResponseCode();
	            String serverResponseMessage = connection.getResponseMessage();
	            //Log.d("ServerCode",""+serverResponseCode);
	            //Log.d("serverResponseMessage",""+serverResponseMessage);
	            //serverResponseString =serverResponseMessage.toString();

	           fileInputStream.close(); 
	           DoutputStream.flush();
	           DoutputStream.close();


	           //reading response from server.........
	           String line = "";               
	           InputStreamReader isr = new InputStreamReader(connection.getInputStream());
	           BufferedReader reader = new BufferedReader(isr);
	           StringBuilder sb = new StringBuilder();
	           while ((line = reader.readLine()) != null)
	           {
	               sb.append(line + "\n");
	           }


	           // Response from server after login process will be stored in response variable.                
	           String response = sb.toString();

	           //Log.d("Response", response);
	           //serverResponseString = serverResponseString + " ,and the response is: " + response;
	           
	           isr.close();
	           reader.close();


	           }
	           catch (Exception ex)
	           {
	               Log.e("Error: ", ex.toString());
	           }
	}


	//batch wise data sync : based on the value of Column BatchSize in DatabaseTab table
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

	//done
	//download data from server and include those id's into Table: Sync_Management
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

	//done
	private String[] Sync_Parameter(String TableName)
	{
		String VariableList = "";
		String UniqueField  = "";
		String SQLStr       = "";
		String SQL_VariableList = "";

		Cursor cur_H = ReadData("Select ColumnList as columnlist, UniqueID as uniqueid from DatabaseTab where tablename='"+ TableName +"'");
		cur_H.moveToFirst();

		while(!cur_H.isAfterLast())
		{
			SQLStr       = "Select "+ cur_H.getString(cur_H.getColumnIndex("columnlist")) +" from "+ TableName +" Where Upload='2'";
			VariableList = cur_H.getString(cur_H.getColumnIndex("columnlist"));
			SQL_VariableList = Convert_VariableList(TableName,VariableList);
			UniqueField  = cur_H.getString(cur_H.getColumnIndex("uniqueid"));

			cur_H.moveToNext();
		}
		cur_H.close();
		String[] ParaList = new String[]{
				SQLStr,
				VariableList,
				UniqueField,
				SQL_VariableList
		};

		return  ParaList;
	}

	//done
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

	//done
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

	//done
	//Upload data to server
	public void Sync_Upload(List<String> tableList)
	{
		for(int i=0; i< tableList.size(); i++)
		{
			Sync_Upload_Process(tableList.get(i).toString());
		}
	}

	//done
	private void Sync_Upload_Process(String TableName)
	{
		String VariableList = "";
		String UniqueField  = "";
		String SQLStr       = "";
		String Res          = "";

		Cursor cur_H = ReadData("Select ColumnList as columnlist, UniqueID as uniqueid from DatabaseTab where tablename='"+ TableName +"'");
		cur_H.moveToFirst();

		while(!cur_H.isAfterLast())
		{
			SQLStr       = "Select "+ cur_H.getString(cur_H.getColumnIndex("columnlist")) +" from "+ TableName +" Where Upload='2'";
			VariableList = cur_H.getString(cur_H.getColumnIndex("columnlist"));
			UniqueField  = cur_H.getString(cur_H.getColumnIndex("uniqueid"));
			cur_H.moveToNext();
		}
		cur_H.close();

		Res = UploadJSON(TableName,VariableList,UniqueField);
	}


	private String Discard_UploadDT_modifyDate(String VariableList)
	{
		String finalVarList = "";
		String[] VList = VariableList.split(",");
		for(int i=0;i<VList.length;i++)
		{
			if(!VList[i].equalsIgnoreCase("uploaddt") & !VList[i].equalsIgnoreCase("modifydate"))
				finalVarList += finalVarList.length()==0 ? VList[i] : ","+ VList[i];
		}
		return finalVarList;
	}

	public String UploadJSON(String TableName,String ColumnList,String UniqueFields)
	{
		String response = "";
		List<DataClassProperty> data = GetDataListJSON(ColumnList, TableName, UniqueFields);

		if(data.size()>0) {
			DataClass dt = new DataClass();
			dt.settablename(TableName);
			dt.setcolumnlist(ColumnList);

			dt.setdata(data);
			Gson gson = new Gson();
			String json = gson.toJson(dt);
			UploadDataJSON u = new UploadDataJSON();
			try {
				response = u.execute(json).get();

				//Process Response
				if(response!=null) {
					downloadClass d = new downloadClass();
					Type collType = new TypeToken<downloadClass>() {
					}.getType();
					downloadClass responseData = (downloadClass) gson.fromJson(response, collType);

					//upload all records as successfull upload then update status of upload=2 for unsuccessfull
					for (int i = 0; i < responseData.getdata().size(); i++) {
						Save("Update " + TableName + " Set Upload='1' where " + responseData.getdata().get(i).toString());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
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
						DataList = cur_H.getString(c).toString().trim();

				}
				else
				{
					if (cur_H.getString(c) == null)
						DataList+="^"+"";
					else if(cur_H.getString(c).equals("null"))
						DataList+="^"+"";
					else
						DataList+="^"+cur_H.getString(c).toString().trim();
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

	public List<DataClassProperty> GetDataListJSON_Bangla(String VariableList,String TableName,String UniqueField)
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
						DataList = "N"+cur_H.getString(c).toString().trim();

				}
				else
				{
					if (cur_H.getString(c) == null)
						DataList+="^N"+"";
					else if(cur_H.getString(c).equals("null"))
						DataList+="^N"+"";
					else
						DataList+="^N"+cur_H.getString(c).toString().trim();
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

	//To get the list of columns(string array) in table
	//----------------------------------------------------------------------------------------------
	public String[] GetColumnListArray(String TableName) {
		Cursor cur = ReadData("Select * From " + TableName + " Where 0");
		String[] columnNames;
		try {
			columnNames = cur.getColumnNames();
		} finally {
			cur.close();
		}
		return columnNames;
	}

	public void AddColumnIfNotExists(String TableName, String ColumnName) {
		//Server database
		String[] ColumnList = GetColumnListArray(TableName);
		//ReturnSingleValue("select ColumnList from databaseSetting where TableName='"+ TableName +"'").toString().split(",");
		Boolean matched = false;
		for (int i = 0; i < ColumnList.length; i++) {
			if (ColumnList[i].toString().toLowerCase().equals(ColumnName.toLowerCase())) {
				matched = true;
				i = ColumnList.length;
			}
		}
		if (matched == false) {
			Save("Alter table " + TableName + " add column " + ColumnName + " varchar(50) default ''");
		}
	}

	// This method will check if column exists in your table
	public boolean isFieldExist(String tableName, String fieldName)
	{
		boolean isExist = false;
		Cursor res = db.rawQuery("PRAGMA table_info("+ tableName +")",null);
		res.moveToFirst();
		do {
			String currentColumn = res.getString(1);
			if (currentColumn.equals(fieldName)) {
				isExist = true;
			}
		} while (res.moveToNext());
		return isExist;
	}

	private static void zipDatabase(String DeviceID)
	{
		CompressZip compressZip = new CompressZip();
		String[] dbFile = new String[1];
		dbFile[0] = Environment.getExternalStorageDirectory() + File.separator + Global.DatabaseFolder + File.separator + Global.DatabaseName;
		String dbFolder = Environment.getExternalStorageDirectory() + File.separator + Global.DatabaseFolder;
		String output   = Global.zipDatabaseName;
		compressZip.zip(dbFile, dbFolder, output);
	}

	public static void DatabaseUploadZip(String DeviceID) {

		//Compress database
		zipDatabase(DeviceID);

		//Upload File from Specific Folder
		String[] FilePathStrings;
		String[] FileNameStrings;
		File[] listFile;

		//
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + Global.DatabaseFolder);
		file.mkdirs();
		if (file.isDirectory()) {
			listFile = file.listFiles();
			FilePathStrings = new String[listFile.length];
			FileNameStrings = new String[listFile.length];

			for (int i = 0; i < listFile.length; i++) {
				FilePathStrings[i] = listFile[i].getAbsolutePath();
				FileNameStrings[i] = listFile[i].getName();

				//Upload file to server
				FileUpload myTask = new FileUpload();
				String[] params = new String[2];

				if (listFile[i].getName().equalsIgnoreCase(Global.zipDatabaseName)) {
					params[0] = listFile[i].getName();
					params[1] = DeviceID + "_" + Common.Global.CurrentDMY() + "_" + listFile[i].getName();
					myTask.execute(params);
				}
			}
		}
	}

	/*public static void SyncDataService(String CLUSTER, String RND)
	{
			Common.Connection C = new Common.Connection();
			String SQL = "";
			SQL  = "Select TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate from DatabaseTab as t";
			SQL += " where not exists(select * from Sync_Management where";
			SQL += " (TableName)  = 'DatabaseTab' and";
			SQL += " UniqueID   = t.TableName and";
			SQL += " convert(varchar(19),modifydate,120) = convert(varchar(19),t.modifydate,120) and";
			SQL += " UserId   ='"+ CLUSTER +"')";

			String Res = C.DownloadJSON_Update_Sync_Management(SQL, "DatabaseTab", "TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate", "TableName", CLUSTER);

			C.Sync_Download("DataCorrectionNote", CLUSTER, "Cluster='"+ CLUSTER +"' and Rnd='"+ RND +"'");

	}*/


}

