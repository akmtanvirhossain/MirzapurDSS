package data.mirzapurdss;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.core.BuildConfig;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import Common.Connection;
import Common.Global;
import Common.ProjectSetting;
import Utility.MySharedPreferences;

public class LoginActivity_New extends AppCompatActivity {
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    Connection C;
    Global g;
    boolean networkAvailable = false;
    int count = 0;
    TextView lblStaffType;
    String SystemUpdateDT="";
    private ProgressDialog dialog;
    private String Password="";
    MySharedPreferences sp;
    TextView Country;
    TextView Facility;
    EditText pass;

    TextView lblSystemDate;
    String CLUSTER = "";

    public static final String SECURITY_TAG = "Security Permission";
    private static final int REQUEST_Code = 0;
    private static String[] PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};

    private void checkPermission()
    {
        Log.e(SECURITY_TAG,"Checking Permission.");
        if (
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) &
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) &
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)&
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)&
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                ) {
            Log.e(SECURITY_TAG,"Calling Requesting Permission!!!");
            requestPermission();
        } else {
            Log.e(SECURITY_TAG,"Your permission has already been granted.");

            Activity_Load();
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Log.e(SECURITY_TAG,"Requesting Permission to User.");
            ActivityCompat.requestPermissions(this,PERMISSIONS_LIST,REQUEST_Code);
        } else {
            Log.e(SECURITY_TAG,"Requesting Permission Directly.");
            ActivityCompat.requestPermissions(this,PERMISSIONS_LIST,REQUEST_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length == PERMISSIONS_LIST.length && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //********* Granted ********
            Activity_Load();
        } else {
            //********* Not Granted ********
            ActivityCompat.requestPermissions(this,PERMISSIONS_LIST,REQUEST_Code);
        }
    }

    @Override
    public boolean onKeyDown(int iKeyCode, KeyEvent event) {
        if (iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME) {
            AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity_New.this);
            adb.setTitle("Close");
            adb.setMessage("Do you want to exist from the system?");
            adb.setNegativeButton("No", null);
            adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            adb.show();
            return false;
        } else {
            return true;
        }
    }

    private void Activity_Load(){
        try
        {
            C = new Connection(this);
            g = Global.getInstance();
            sp = new MySharedPreferences();
            sp.save(this,"deviceid","");
            sp.save(this,"userid","");
            sp.save(this,"cluster","");
            sp.save(this,"block","");

            //final TextView UniqueUserId      = (TextView)findViewById(R.id.UniqueUserId);
            final Spinner uid      = (Spinner)findViewById(R.id.userId);
            final EditText pass    = (EditText)findViewById(R.id.pass);
            TextView lblSystemDate = (TextView)findViewById(R.id.lblSystemDate);



            //Check for Internet connectivity
            networkAvailable = Connection.haveNetworkConnection(LoginActivity_New.this);

            CLUSTER = C.ReturnSingleValue("Select Cluster from CurrentCluster");
            g.setClusterCode(CLUSTER);
            sp.save(this,"cluster",CLUSTER);

            lblSystemDate = (TextView)findViewById(R.id.lblSystemDate);

            //Need to update date every time whenever shared updated system
            //Format: DDMMYYYY
            //*********************************************************************
            SystemUpdateDT = "26022025"; //old version date: "09112021"

            lblSystemDate.setText("Version: 1.0, Built on: "+ SystemUpdateDT);

            lblSystemDate.setText(data.mirzapurdss.Global.Left(SystemUpdateDT, 2)+" - "+SystemUpdateDT.substring(2,4)+" - "+ data.mirzapurdss.Global.Right(SystemUpdateDT,4));
            //*********************************************************************

            //Check for Internet connectivity
            networkAvailable = Common.Connection.haveNetworkConnection(LoginActivity_New.this);

            final Spinner txtBlock = (Spinner)findViewById(R.id.blockNumber);
            txtBlock.setAdapter(C.getArrayAdapter("Select distinct block from baris where length(Block)>0 order by cast(block as int)"));
            txtBlock.setSelection(data.mirzapurdss.Global.SpinnerItemPosition(txtBlock, 2, C.ReturnSingleValue("Select BlockNo from LastLogin")));

            Spinner s = (Spinner) findViewById(R.id.userId);
            s.setAdapter(C.getArrayAdapter("select (UserId||', '||UserName)User from Login"));

            Button btnClose=(Button)findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    finish();
                    System.exit(0);
                }
            });


            //07 Jan 2018
            //Intent syncService = new Intent(this, Data_Process_Service.class);
            //startService(syncService);


            //Execute Specific Task
            try {
                String resp1 = "";
                C.CreateTable("process_tab", "Create table process_tab(process_id int)");

                //15 11 2018
                /*if (!C.Existence("Select * from process_tab where process_id=2")) {
                    Common.Connection CJson = new Common.Connection(LoginActivity_New.this);
                    String resp3 = CJson.SaveData("Drop table DatabaseTab");
                    String resp2 = CJson.SaveData("Create table DatabaseTab(TableName varchar (50),TableScript varchar(500),ColumnList varchar(500),UniqueID varchar (500),Sync_Upload char (1),Sync_Download char (1),BatchSize int,modifyDate datetime,Constraint pk_DatabaseTab Primary Key(TableName))");
                    String SQLStr  = "Select TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate from DatabaseTab as t\n" +
                            "where not exists(select * from Sync_Management where\n" +
                            "lower(TableName)  = lower('DatabaseTab') and\n" +
                            "UniqueID   = 'DatabaseTab' and\n" +
                            "convert(varchar(19),modifydate,120) = convert(varchar(19),t.modifydate,120) and\n" +
                            "UserId   ='"+ CLUSTER +"')";
                    String VariableList = "TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate";
                    String Res = CJson.DownloadJSON(SQLStr,"DatabaseTab",VariableList,"TableName");

                    String resp0 = CJson.SaveData("Drop table MigDatabase");
                    resp1 = CJson.SaveData("Create table MigDatabase(ExType varchar (2),hh varchar (9),Sno varchar (2),Pno varchar (8),Name varchar (100),ExDate varchar (10),modifydate datetime,Constraint pk_MigDatabase Primary Key(hh,Sno))");

                    if (resp1.length() == 0) C.Save("Insert into process_tab(process_id)values(2)");

                    CJson.Sync_Download_Batch("MigDatabase",CLUSTER,"");
                }
                //03-10-2019
                else if (!C.Existence("Select * from process_tab where process_id=4")) {
                    String SQL = "CREATE TABLE tTrans ( Status VARCHAR( 1 ),Vill   VARCHAR( 3 ),Bari   VARCHAR( 4 ),Hh VARCHAR( 2 ),Pno VARCHAR( 8 ),Sno VARCHAR( 2 ),VDate  DATETIME,SESNo  VARCHAR( 1 ),Visit  VARCHAR( 1 ),Rnd VARCHAR( 2 ),Dma VARCHAR( 8 ),\n" +
                            "EnterDt DATETIME,EnType VARCHAR( 2 ),EnDate DATETIME,ExType VARCHAR( 2 ),ExDate DATETIME,Rel VARCHAR( 1 ),UCode  VARCHAR( 2 ),HHHead VARCHAR( 60 )   ,Clust  VARCHAR( 2 ),Block  VARCHAR( 2 ),PvRnd  VARCHAR( 2 ),PvDate DATETIME,\n" +
                            "Name   VARCHAR( 50 )   ,Rth VARCHAR( 2 ),Sex VARCHAR( 1 ),BDate  DATETIME,Age VARCHAR( 3 ),Mono   VARCHAR( 2 ),Fano   VARCHAR( 2 ),Edu VARCHAR( 2 ),Ms VARCHAR( 2 ),Pstat  VARCHAR( 2 ),LmpDt  DATETIME,Sp1 VARCHAR( 2 ),\n" +
                            "Sp2 VARCHAR( 2 ),Sp3 VARCHAR( 2 ),Sp4 VARCHAR( 2 ),Ocp VARCHAR( 2 ),EvType VARCHAR( 8 ),EvDate DATETIME,Info1  VARCHAR( 20 )   ,Info2  VARCHAR( 8 ),Info3  VARCHAR( 8 ),Info4  DATETIME,Q015a  INTEGER,Q015b  INTEGER,\n" +
                            "Q015c  INTEGER,Q016a  INTEGER,Q016b  INTEGER,Q016c  INTEGER,Q017   INTEGER,Q018   INTEGER,Q019a  INTEGER,Q019b  INTEGER,Q019c  INTEGER,Q019d  INTEGER,Q019e  INTEGER,Q019f  INTEGER,Q019g  INTEGER,Q019h  INTEGER,Q019i  INTEGER,\n" +
                            "Q019j  INTEGER,Q019k  INTEGER,Q019l  INTEGER,Q019m  INTEGER,Q019n  INTEGER,Q019o  INTEGER,Q019p  INTEGER,Q019q  INTEGER,Q019r  INTEGER,Q019s  INTEGER,Q019t  INTEGER,Q019u  INTEGER,Q019v  INTEGER,Q019w  INTEGER,Q019x  INTEGER,\n" +
                            "Q019y  INTEGER,Q019z  INTEGER,Q020a  INTEGER,Q020b  INTEGER,Q020c  INTEGER,Q020d  INTEGER,Q020e  INTEGER,Q020f  INTEGER,Q020g  INTEGER,Q020h  INTEGER,Q021   INTEGER,Q022a  INTEGER,Q022b  INTEGER,Q022c  INTEGER,Q023a  INTEGER,\n" +
                            "Q023b  INTEGER,Q024a  INTEGER,Q024b  INTEGER,Q025a  INTEGER,Q025b  INTEGER,Q026   INTEGER,Q027a  INTEGER,Q027b  INTEGER,Q027c  INTEGER,Q027d  INTEGER,Q027e  INTEGER,Q027f  INTEGER,Q027g  INTEGER,Q027h  INTEGER,Q027i  INTEGER,\n" +
                            "Q027j  INTEGER,Q027y  INTEGER,Q027z  INTEGER,Q028a  INTEGER,Q028b  INTEGER,Q028c  INTEGER,Q028d  INTEGER,Q028e  INTEGER,Q028y  INTEGER,Q029   INTEGER,Q030a  INTEGER,Q030b  INTEGER,Q030c  INTEGER,Q030d  INTEGER,Q030e  INTEGER,\n" +
                            "Q030f  INTEGER,Q030g  INTEGER,Q030h  INTEGER,Q030z  INTEGER,Q031   INTEGER,MarM   VARCHAR( 2 ),MarY   VARCHAR( 4 ),Births INTEGER,LiveHh INTEGER,SLiveHh INTEGER,DLiveHh INTEGER,LiveOut INTEGER,SLiveOut   INTEGER,\n" +
                            "DLiveOut   INTEGER,Died   INTEGER,SDied  INTEGER,DDied  INTEGER,Abor   INTEGER,TAbor  INTEGER,TotPreg INTEGER,PageNo VARCHAR( 2 ),Upload VARCHAR( 1 ),Lat VARCHAR( 20 )   ,Lon VARCHAR( 20 )   ,LatNet VARCHAR( 20 )   ,\n" +
                            "LonNet VARCHAR( 20 )   ,Resp   VARCHAR( 2 ),PosMig VARCHAR( 2 ),PosMigDate VARCHAR( 10 )   ,Note   VARCHAR( 150 )  NOT NULL  DEFAULT '',ContactNo  VARCHAR( 50 )   NOT NULL   DEFAULT '' )";
                    C.Save("Drop table tTrans");
                    C.CreateTable("tTrans",SQL);
                    if (resp1.length() == 0) C.Save("Insert into process_tab(process_id)values(4)");
                }*/


                //08112021
                if (!C.Existence("Select * from process_tab where process_id=5")) {
                    C.CreateTable("DataCorrectionNote", "Create table DataCorrectionNote(Vill varchar (3),Bari varchar (4),HH varchar (2),Sno varchar (2),Serial int,Note nvarchar(500),Status varchar (1),ClearDate varchar (20),Cluster varchar (2),Block varchar (2),Upload varchar (1),modifyDate datetime,Constraint pk_DataCorrectionNote Primary Key(Vill,Bari,HH,Sno,Serial))");
                    C.Save("Insert into process_tab(process_id)values(5)");
                }

                //13012023
                if (!C.Existence("Select * from process_tab where process_id=6")) {
                    C.CreateTable("data_GAge", "Create table data_GAge(Vill varchar (3),Bari varchar (4),HH varchar (2),SNo varchar (2),PNo varchar (10),GAge varchar (2),StartTime varchar (5),EndTime varchar (5),DeviceID varchar (10),EntryUser varchar (10),Lat varchar (20),Lon varchar (20),EnDt datetime,Upload int,modifyDate datetime,Constraint pk_data_GAge Primary Key(Vill,Bari,HH,SNo))");
                    C.Save("Insert into process_tab(process_id)values(6)");
                }

                if (!C.Existence("Select * from process_tab where process_id=6")) {
                    C.CreateTable("data_GAge", "Create table data_GAge(Vill varchar (3),Bari varchar (4),HH varchar (2),SNo varchar (2),PNo varchar (10),GAge varchar (2),StartTime varchar (5),EndTime varchar (5),DeviceID varchar (10),EntryUser varchar (10),Lat varchar (20),Lon varchar (20),EnDt datetime,Upload int,modifyDate datetime,Constraint pk_data_GAge Primary Key(Vill,Bari,HH,SNo))");
                    C.Save("Insert into process_tab(process_id)values(6)");
                }

                if (!C.Existence("Select * from process_tab where process_id=9")) {
                C.SaveData("Drop Table Mig_Immunization");

                   String SQL = "Create table Mig_Immunization(Vill varchar (3),Bari varchar (4),HH varchar (2),Sno varchar (2),PNO varchar (8),Status varchar (2),BCG varchar (1),BCGDT varchar (10),BCGSource varchar (2),BCGDTDk varchar (1),Penta1 varchar (1),Penta1DT varchar (10),Penta1Source varchar (2),Penta1DTDK varchar (1),Penta2 varchar (1),Penta2DT varchar (10),Penta2Source varchar (2),Penta2DTDK varchar (1),Penta3 varchar (1),Penta3DT varchar (10),Penta3Source varchar (2),Penta3DTDK varchar (1),PCV1 varchar (1),PCV1DT varchar (10),PCV1Source varchar (2),PCV1DTDK varchar (1),PCV2 varchar (1),PCV2DT varchar (10),PCV2Source varchar (2),PCV2DTDK varchar (1),PCV3 varchar (1),PCV3DT varchar (10),PCV3Source varchar (2),PCV3DTDK varchar (1),OPV0 varchar (1),OPV0DT varchar (10),OPV0Source varchar (2),OPV0DTDK varchar (1),OPV1 varchar (1),OPV1DT varchar (10),OPV1Source varchar (2),OPV1DTDK varchar (1),OPV2 varchar (1),OPV2DT varchar (10),OPV2Source varchar (2),OPV2DTDK varchar (1),OPV3 varchar (1),OPV3DT varchar (10),OPV3Source varchar (2),OPV3DTDK varchar (1),OPV4 varchar (1),OPV4DT varchar (10),OPV4Source varchar (2),OPV4DTDK varchar (1),Measles varchar (1),MeaslesDT varchar (10),MeaslesSource varchar (2),MeaslesDTDK varchar (1),MR varchar (1),MRDT varchar (10),MRSource varchar (2),MRDTDK varchar (1),Rota varchar (1),RotaDT varchar (10),RotaSource varchar (2),RotaDTDK varchar (1),MMR varchar (1),MMRDT varchar (10),MMRSource varchar (2),MMRDTDK varchar (1),Typhoid varchar (1),TyphoidDT varchar (10),TyphoidSource varchar (2),TyphoidDTDK varchar (1),Influ varchar (1),InfluDT varchar (10),InfluSource varchar (2),InfluDTDK varchar (1),HepaA varchar (1),HepaADT varchar (10),HepaASource varchar (2),HepaADTDk varchar (1),ChickenPox varchar (1),ChickenPoxDT varchar (10),ChickenPoxSource varchar (2),ChickenPoxDTDk varchar (1),Rabies varchar (1),RabiesDT varchar (10),RabiesSource varchar (2),RabiesDTDk varchar (1),IPV varchar (1),IPVDT varchar (10),IPVDTDk varchar (1),IPVSource varchar (2),fIPV1 varchar (1),fIPVDT1 varchar (10),Fipvdt1dk varchar (1),fIPVSource1 varchar (2),fIPV2 varchar (1),fIPVDT2 varchar (10),Fipvdt2Dk varchar (1),fIPVSource2 varchar (2),VitaminA varchar (1),VitaminADT varchar (10),VitaminADTDk varchar (1),VitaminASource varchar (2),EnDt varchar (20),Upload varchar (1),modifydate datetime,Constraint pk_Mig_Immunization Primary Key(Vill,Bari,HH,Sno))";

                    C.CreateTable("Mig_Immunization",SQL);

                    C.SaveData("Update DatabaseTab set \n" +
                            " TableScript='Create table Mig_Immunization(Vill varchar (3),Bari varchar (4),HH varchar (2),Sno varchar (2),PNO varchar (8),Status varchar (2),BCG varchar (1),BCGDT varchar (10),BCGSource varchar (2),BCGDTDk varchar (1),Penta1 varchar (1),Penta1DT varchar (10),Penta1Source varchar (2),Penta1DTDK varchar (1),Penta2 varchar (1),Penta2DT varchar (10),Penta2Source varchar (2),Penta2DTDK varchar (1),Penta3 varchar (1),Penta3DT varchar (10),Penta3Source varchar (2),Penta3DTDK varchar (1),PCV1 varchar (1),PCV1DT varchar (10),PCV1Source varchar (2),PCV1DTDK varchar (1),PCV2 varchar (1),PCV2DT varchar (10),PCV2Source varchar (2),PCV2DTDK varchar (1),PCV3 varchar (1),PCV3DT varchar (10),PCV3Source varchar (2),PCV3DTDK varchar (1),OPV0 varchar (1),OPV0DT varchar (10),OPV0Source varchar (2),OPV0DTDK varchar (1),OPV1 varchar (1),OPV1DT varchar (10),OPV1Source varchar (2),OPV1DTDK varchar (1),OPV2 varchar (1),OPV2DT varchar (10),OPV2Source varchar (2),OPV2DTDK varchar (1),OPV3 varchar (1),OPV3DT varchar (10),OPV3Source varchar (2),OPV3DTDK varchar (1),OPV4 varchar (1),OPV4DT varchar (10),OPV4Source varchar (2),OPV4DTDK varchar (1),Measles varchar (1),MeaslesDT varchar (10),MeaslesSource varchar (2),MeaslesDTDK varchar (1),MR varchar (1),MRDT varchar (10),MRSource varchar (2),MRDTDK varchar (1),Rota varchar (1),RotaDT varchar (10),RotaSource varchar (2),RotaDTDK varchar (1),MMR varchar (1),MMRDT varchar (10),MMRSource varchar (2),MMRDTDK varchar (1),Typhoid varchar (1),TyphoidDT varchar (10),TyphoidSource varchar (2),TyphoidDTDK varchar (1),Influ varchar (1),InfluDT varchar (10),InfluSource varchar (2),InfluDTDK varchar (1),HepaA varchar (1),HepaADT varchar (10),HepaASource varchar (2),HepaADTDk varchar (1),ChickenPox varchar (1),ChickenPoxDT varchar (10),ChickenPoxSource varchar (2),ChickenPoxDTDk varchar (1),Rabies varchar (1),RabiesDT varchar (10),RabiesSource varchar (2),RabiesDTDk varchar (1),IPV varchar (1),IPVDT varchar (10),IPVDTDk varchar (1),IPVSource varchar (2),fIPV1 varchar (1),fIPVDT1 varchar (10),Fipvdt1dk varchar (1),fIPVSource1 varchar (2),fIPV2 varchar (1),fIPVDT2 varchar (10),Fipvdt2Dk varchar (1),fIPVSource2 varchar (2),VitaminA varchar (1),VitaminADT varchar (10),VitaminADTDk varchar (1),VitaminASource varchar (2),EnDt varchar (20),Upload varchar (1),modifydate datetime,Constraint pk_Mig_Immunization Primary Key(Vill,Bari,HH,Sno))',\n" +
                            " ColumnList='Vill,Bari,HH,Sno,PNO,Status,BCG,BCGDT,BCGSource,BCGDTDk,Penta1,Penta1DT,Penta1Source,Penta1DTDK,Penta2,Penta2DT,Penta2Source,Penta2DTDK,Penta3,Penta3DT,Penta3Source,Penta3DTDK,PCV1,PCV1DT,PCV1Source,PCV1DTDK,PCV2,PCV2DT,PCV2Source,PCV2DTDK,PCV3,PCV3DT,PCV3Source,PCV3DTDK,OPV0,OPV0DT,OPV0Source,OPV0DTDK,OPV1,OPV1DT,OPV1Source,OPV1DTDK,OPV2,OPV2DT,OPV2Source,OPV2DTDK,OPV3,OPV3DT,OPV3Source,OPV3DTDK,OPV4,OPV4DT,OPV4Source,OPV4DTDK,Measles,MeaslesDT,MeaslesSource,MeaslesDTDK,MR,MRDT,MRSource,MRDTDK,Rota,RotaDT,RotaSource,RotaDTDK,MMR,MMRDT,MMRSource,MMRDTDK,Typhoid,TyphoidDT,TyphoidSource,TyphoidDTDK,Influ,InfluDT,InfluSource,InfluDTDK,HepaA,HepaADT,HepaASource,HepaADTDk,ChickenPox,ChickenPoxDT,ChickenPoxSource,ChickenPoxDTDk,Rabies,RabiesDT,RabiesSource,RabiesDTDk,IPV,IPVDT,IPVDTDk,IPVSource,fIPV1,fIPVDT1,Fipvdt1dk,fIPVSource1,fIPV2,fIPVDT2,Fipvdt2Dk,fIPVSource2,VitaminA,VitaminADT,VitaminADTDk,VitaminASource,EnDt,Upload,modifydate'\n" +
                            " UniqueID='Vill,Bari,HH,Sno'\n" +
                            " where TableName='Mig_Immunization'");
//                    C.CreateTable("data_GAge", "Create table data_GAge(Vill varchar (3),Bari varchar (4),HH varchar (2),SNo varchar (2),PNo varchar (10),GAge varchar (2),StartTime varchar (5),EndTime varchar (5),DeviceID varchar (10),EntryUser varchar (10),Lat varchar (20),Lon varchar (20),EnDt datetime,Upload int,modifyDate datetime,Constraint pk_data_GAge Primary Key(Vill,Bari,HH,SNo))");
                    C.Save("Insert into process_tab(process_id)values(9)");
                }


            }catch (Exception ex){

            }

            if (networkAvailable)
            {
                if(!isMyServiceRunning(Sync_Service.class)) {
                    String CLUSTER = C.ReturnSingleValue("Select Cluster from currentcluster limit 1");
                    String RND = C.ReturnSingleValue("Select max(cast(Rnd as int))Rnd from Round");
                    sp.save(this,"cluster",CLUSTER);
                    sp.save(this,"rnd",RND);
                    Intent syncService = new Intent(this, Sync_Service.class);
                    startService(syncService);
                }
            }


            //Login -----------------------------------------------------------------------
            Button loginButton = (Button) findViewById(R.id.btnLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try
                    {
                        g.setUserId(data.mirzapurdss.Global.Left(uid.getSelectedItem().toString(),2));

                        //Store Last Login information
                        C.Save("Delete from LastLogin");
                        C.Save("Insert into LastLogin(BlockNo)Values('"+ data.mirzapurdss.Global.Left(txtBlock.getSelectedItem().toString(),2) +"')");
                        g.setBlockCode(data.mirzapurdss.Global.Left(txtBlock.getSelectedItem().toString(), 2));

                        sp.save(LoginActivity_New.this,"block",data.mirzapurdss.Global.Left(txtBlock.getSelectedItem().toString(), 2));

                        //netwoekAvailable = false;

                        //Download Updated System
                        //...................................................................................
                        if(networkAvailable==true)
                        {
                            //Retrieve data from server for checking local device
                            String[] ServerVal  = data.mirzapurdss.Connection.split(C.ReturnResult("ReturnSingleValue","sp_ServerCheck '"+ g.getClusterCode() +"'"), ',');
                            String SystemUpdate = ServerVal[0].toString(); //System Update Status
                            String ServerDate   = ServerVal[1].toString(); //System Date
                            String UpdateDT     = ServerVal[2].toString(); //Updated System Date

                            //Check Internet connectivity & Update system
                            if (!UpdateDT.equals(SystemUpdateDT) & data.mirzapurdss.Connection.haveNetworkConnection(LoginActivity_New.this))
                            {
                                C.ExecuteCommandOnServer("Update SystemUpdate set UpdateNeeded='2' where Cluster = '"+ g.getClusterCode() +"' and UpdateNeeded='1'");

                                LoginActivity_New.SystemDownload d = new LoginActivity_New.SystemDownload();
                                d.setContext(getApplicationContext());
                                d.execute(Common.Global.UpdatedSystem);
                            }
                            else
                            {
                                if(ServerDate.equals(data.mirzapurdss.Global.TodaysDateforCheck())==false)
                                {
                                    data.mirzapurdss.Connection.MessageBox(LoginActivity_New.this, "আপনার ট্যাব এর তারিখ সঠিক নয় ["+ g.CurrentDateDDMMYYYY +"]।");
                                    startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                                    return;
                                }

                                //Call main Menu Form
                                //...................................................................................
                                finish();
                                Intent f11 = new Intent(getApplicationContext(),BlockList.class);
                                startActivity(f11);

                            }

                        }
                        else
                        {
                            //Call main Menu Form
                            //...................................................................................
                            finish();
                            Intent f11 = new Intent(getApplicationContext(),BlockList.class);
                            startActivity(f11);
                        }
                    }
                    catch(Exception ex)
                    {
                        data.mirzapurdss.Connection.MessageBox(LoginActivity_New.this, ex.getMessage());
                        return;
                    }
                }
            });
        }
        catch(Exception ex)
        {
            Connection.MessageBox(LoginActivity_New.this, ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.login_new);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            checkPermission();


        } catch (Exception ex) {
            Connection.MessageBox(LoginActivity_New.this, ex.getMessage());
        }
    }

    //Install application
    private void InstallApplication()
    {
        File apkfile = new File(Environment.getExternalStorageDirectory() + File.separator + Global.NewVersionName +".apk");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkuri = FileProvider.getUriForFile(LoginActivity_New.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    apkfile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(apkuri, "application/vnd.android.package-archive");

            startActivity(intent);
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");

            startActivity(intent);
        }
    }

    //Downloading updated system from the central server
    class SystemDownload extends AsyncTask<String, String, Void> {
        private Context context;

        public void setContext(Context contextf){
            context = contextf;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity_New.this);
            dialog.setMessage("Downloading Updated System...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.show();
        }


        protected void onProgressUpdate(String... progress) {
            dialog.setProgress(Integer.parseInt(progress[0]));
            //publishProgress(progress);

        }

        //@Override
        protected void onPostExecute(String unused) {
            dialog.dismiss();
        }


        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();
                int lenghtOfFile = c.getContentLength();

                File file=Environment.getExternalStorageDirectory();

                file.mkdirs();
                File outputFile = new File(file.getAbsolutePath()+ File.separator + Global.NewVersionName+".apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }
                else
                {
                    outputFile.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();


                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    count++;
                }
                fos.close();
                is.close();


                InstallApplication();

                dialog.dismiss();

            } catch (IOException e) {
                //Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}