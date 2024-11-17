package data.mirzapurdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import Utility.MySharedPreferences;

public class BlockList extends AppCompatActivity {
	Connection C;
	Global g;
	boolean netwoekAvailable=false;
	Location currentLocation; 
	double currentLatitude,currentLongitude; 

	Location currentLocationNet; 
	double currentLatitudeNet,currentLongitudeNet;

	ProgressDialog progDailog;
	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	SimpleAdapter mSchedule;
	
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exit, menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) { 
	    	case R.id.mnuUpdateRound:
	    		try
	    		{
	    		//Check for Internet connectivity
	    	        //*******************************************************************************
	    	    	if (Connection.haveNetworkConnection(this)) {    		 
	    	    	    netwoekAvailable=true;
	    	    	 
	    	    	} else {     	 
	    	    	    netwoekAvailable=false;
	    	    	} 
	    			
	    	    	if(netwoekAvailable==false)
	    	    	{
	    	    	    Connection.MessageBox(BlockList.this, "রাঊন্ড নাম্বার আপডেট করার  জন্য ইন্টারনেট কানেকশান পাওয়া যাচ্ছে না।");
	    	    	}
	    	    	else
	    	    	{
			    		String RD = C.ReturnResult("ReturnSingleValue","Select Rnd from Round where CurrentRound='1'");
			    		if(RD==null | RD.length()==0)
			    		{
			    			Connection.MessageBox(BlockList.this, "রাঊন্ড নাম্বার আপডেট করা সম্ভব হচ্ছেনা।");
			    		}
			    		else
			    		{
				            if(!C.Existence("Select * from Round where Rnd='"+ RD +"'"))
				            {
				            	C.Save("Insert into Round(Rnd,StartDate,EndDate,CurrentRound)Values('"+ RD +"','','','1')");
				                Spinner txtRound=(Spinner)findViewById(R.id.txtRound);
				                txtRound.setAdapter(C.getArrayAdapter("SELECT Rnd from Round where rnd is not null order by rnd desc limit 10"));
				            }
			    		}
	    	    	}
	    		}
	    		catch(Exception ex)
	    		{
	    			//Connection.MessageBox(BlockList.this, ex.getStackTrace());
	    		}
	    		return true;
    		
    		case R.id.exitmenu:
    		        turnGPSOff();
    			finish();
            	        System.exit(0);   			   			
    			return true;	    			
    	}    
    	return false;
    }
 
	Spinner spnCluster;
	private static String CLUSTER = "";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocklist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        C = new Connection(this);
        g = Global.getInstance();

        spnCluster = (Spinner)findViewById(R.id.spnCluster);
        spnCluster.setAdapter(C.getArrayAdapter("Select Cluster from currentcluster"));
		CLUSTER = spnCluster.getSelectedItem().toString();

        final Spinner txtRound = (Spinner)findViewById(R.id.txtRound);
        txtRound.setAdapter(C.getArrayAdapter("Select Rnd from Round where rnd is not null and rnd not in('null') order by rnd desc limit 15"));
        
        
        final Spinner txtBlock = (Spinner)findViewById(R.id.txtBlock);
        txtBlock.setAdapter(C.getArrayAdapter("Select distinct block from baris order by cast(block as int)"));
        //txtBlock.setSelection(Global.SpinnerItemPosition(txtBlock, 2, g.getBlockCode()));
        txtBlock.setSelection(Global.SpinnerItemPosition(txtBlock, 2, MySharedPreferences.getValue(BlockList.this,"block")));

        //Bari Number Update: 18 Sep 2014
        String NewVillNo = "";
    	String NewBariNo = "";
    	String OldVillBari = "";

        Button cmdBlock = (Button)findViewById(R.id.cmdBlock);
        cmdBlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try
            	{
	         	  	final ProgressDialog progDailog = ProgressDialog.show(BlockList.this, "", "অপেক্ষা করুন ...", true); 
					finish();
			        new Thread() { 
			            public void run() { 
			                try { 	     
			                	
			                	g.setClusterCode(spnCluster.getSelectedItem().toString());
				            	g.setBlockCode(txtBlock.getSelectedItem().toString());
				            	g.setRoundNumber(txtRound.getSelectedItem().toString());
				            	
				            	String CL = g.getClusterCode();
				            	String B  = g.getBlockCode();
				            	
				            	String[] V = Connection.split(C.ReturnSingleValue("Select distinct (b.vill ||','||v.vname) from Baris b,mdssvill v where b.vill=v.vill and v.cluster='"+ CL +"' and b.block='"+ B +"'"), ',');            	
				            	g.setVillageCode(V[0]);
				            	String VL = V[0];
				            	g.setVillageCode(VL);
				            	g.setVillageCode(V[0].toString());
				            	g.setVillageName(V[1].toString());

				            	Bundle IDbundle = new Bundle();
								IDbundle.putString("cluster", CL);
								IDbundle.putString("block", B);
								IDbundle.putString("vcode", VL);
								IDbundle.putString("village", V[1].toString());
				         	  	
					         	//finish();
				     	    	Intent f11 = new Intent(getApplicationContext(),HouseholdIndex.class);
				     	    	f11.putExtras(IDbundle);
				     	    	startActivity(f11);   
				     	    	
			                    } catch (Exception e) { 
			                } 
			                progDailog.dismiss(); 
			            } 
			        }.start(); 		    	    	
            	}
            	catch(Exception ex)
            	{
            		Connection.MessageBox(BlockList.this, ex.getMessage());
            		return;
            	}
            }
        }); 
        
        
        
        //Enable GPS options in device
        //Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        //intent.putExtra("enabled", true);
        //sendBroadcast(intent);
        
        turnGPSOn();
        
        //GPS Location
        FindLocation();
        FindLocationNet();
        
        //Check for Internet connectivity
        //*******************************************************************************
    	if (Connection.haveNetworkConnection(this)) {    		 
    		netwoekAvailable=true;    	 
    	} else {     	 
    		netwoekAvailable=false;
    	} 
    	
        //Main Menu
        //*******************************************************************************      
        GridView g1=(GridView) findViewById(R.id.gridMenu);
		g1.setAdapter(new GridAdapter(this));
		g1.setOnItemClickListener(new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {           
        	 try
        	 {
        	 	g.setClusterCode(spnCluster.getSelectedItem().toString());

            	//Upload data to central server
            	//*******************************************************************************
            	if(position==0)
            	{
            		AlertDialog.Builder builder = new AlertDialog.Builder(BlockList.this);
        	    	builder
        			    	.setTitle("Message")	    			    	
        			    	.setMessage("আপনি কি নতুন তথ্য ডাটা বেজ সার্ভারে আপলোড করতে চান[হ্যাঁ/না]?")
        			    	.setIcon(android.R.drawable.ic_dialog_alert)
        			    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		    	    	   public void onClick(DialogInterface dialog, int which) {		
    		    	    	    	switch (which){
    		    	    	        case DialogInterface.BUTTON_POSITIVE:
    		    	    	        	
    		    	    	        	
    		    	    	        	if(netwoekAvailable==false)
    		    	                	{
    		    	                		Connection.MessageBox(BlockList.this, "Internet connection is not available.");
    		    	                		return;
    		    	                	}
    		    	            		
    		    	            		try
    		    	            		{    		    	            			
    		    	            			String ResponseString="Upload Status:";
 
	    		    				        /*final ProgressDialog progDailog = ProgressDialog.show(
	    		    				        		BlockList.this, "", "অপেক্ষা করুন ...", true);*/
											progDailog = new ProgressDialog(BlockList.this);
											progDailog.setTitle("Data Sync");
											progDailog.setMessage("Data Sync in Progress, Please wait ...");
											progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
											progDailog.setCancelable(false);
											progDailog.setIcon(R.drawable.upload);
											progDailog.show();


	    		    				        new Thread() { 
	    		    				            public void run() { 
	    		    				            	String ResponseString="Upload Status:";
	    		    				            	String response;
	    		    				            	
	    		    				                try {    		    				        
	    		    		    	    	        	String VariableList;
	    		    		    	    	        	String TableName;	
	    		    		    	    	        	String SQL = "";
	    		    		    	    	        	String UniqueField = "";

														Common.Connection CJSon = new Common.Connection(BlockList.this);
														
	    		    		    	    	        	//Upload/Download Status: 02 Apr 2014
	    		    		    	    	        	String r = C.ExecuteCommandOnServer("Insert into UploadMonitor(Cluster)Values('"+ g.getClusterCode() +"')");
	    		    		    	    	        	
	    		    		    	    	        	/*
	    		    		    	    	        	String[] UpdateControl = Connection.split(C.ReturnResult("ReturnSingleValue","select (Member+','+Event+','+PNo+','+EvDel)UC from UpdateControl where Cluster='"+ g.getClusterCode() +"'"), ',');
	    		    		    	    	        	String MemberUpdate = UpdateControl[0].toString();
	    		    		    	    	        	String EventUpdate  = UpdateControl[1].toString();
	    		    		    	    	        	String PNoUpdate    = UpdateControl[2].toString();
	    		    		    	    	        	String EventDel     = UpdateControl[3].toString();
	    		    		    	    	        	*/

														//30 Jun 2015
														//PNO Missing in Pregnancy History
														SQL = "select h.Vill||h.Bari||h.HH||h.Sno hh,h.Pno from Member h,PregHis p where h.Vill||h.Bari||h.HH||h.Sno=p.Vill||p.Bari||p.Hh||p.Sno and LENgth(p.pno)=0";
														Cursor PH = C.ReadData(SQL);
														PH.moveToFirst();
														while(!PH.isAfterLast())
														{
															try
															{
																SQL = "Update PregHis set pno='"+ PH.getString(1).toString() +"' where vill||bari||hh||sno='"+ PH.getString(0).toString() +"'";
																C.Save(SQL);
															}
															catch(Exception e)
															{

															}

															PH.moveToNext();
														}
														PH.close();

														//***************************
	    		        		    	            	//  Update Local Database
	    		    		    	    	        	//***************************

														//Delete Data From the Local Device: 20 Oct 2017
														String UniqueID_Column = "";
														String UniqueID = "";
														String[] UniqueID_List;

														CJSon.Sync_Download("DeleteID_List",g.getClusterCode(),"");

														Cursor cur_H = C.ReadData("Select TableName,ID from DeleteID_List Where DeleteStatus='N'");
														cur_H.moveToFirst();
														while(!cur_H.isAfterLast())
														{
															UniqueID_Column = "";
															UniqueID = C.ReturnSingleValue("select UniqueID from DatabaseTab where TableName='"+ cur_H.getString(cur_H.getColumnIndex("TableName")) +"'");
															UniqueID_List = UniqueID.split(",");
															for(int i=0;i<UniqueID_List.length;i++){
																UniqueID_Column += UniqueID_Column.length()==0 ? "Cast("+ UniqueID_List[i] +" as varchar(50))" : "||Cast("+ UniqueID_List[i] +" as varchar(50))";
															}

															try {
																C.Save("Delete from " + cur_H.getString(cur_H.getColumnIndex("TableName")) + " where " + UniqueID_Column + "='" + cur_H.getString(cur_H.getColumnIndex("ID")) + "'");
																C.Save("Update DeleteID_List set DeleteStatus='Y',Upload='2' where ID='" + cur_H.getString(cur_H.getColumnIndex("ID")) + "'");
															}catch (Exception ex){

															}
															cur_H.moveToNext();
														}
														cur_H.close();
														TableName = "DeleteID_List";
														VariableList = "TableName, ID, DeleteStatus, Upload, modifyDate";
														response = CJSon.UploadJSON(TableName , VariableList , "TableName,ID");

														SQL  = "select a.Vill,a.Bari,a.HH,a.SNo,a.PNo,a.GAge,a.StartTime,a.EndTime,a.DeviceID,a.EntryUser,a.Lat,a.Lon,convert(varchar(20),a.EnDt,120)modifyDate,'1' Upload,convert(varchar(20),a.modifyDate,120)modifyDate\n" +
																" from data_GAge a inner join MDSSVill v on a.Vill=v.Vill where v.Cluster='"+ g.getClusterCode()+"' and a.Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL, "data_GAge", "Vill,Bari,HH,SNo,PNo,GAge,StartTime,EndTime,DeviceID,EntryUser,Lat,Lon,EnDt,Upload,modifyDate", "Vill,Bari,HH,SNo");


														//Baris Table: 03 Jan 2017
														SQL  = "Select b.Vill, Bari,v.Cluster, Block, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec, b.Status, EnDt, Upload from Baris b,MDSSVill v where b.Vill=v.Vill and v.Cluster='"+ g.getClusterCode() +"' and Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL, "Baris", "Vill, Bari,Cluster, Block, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec, Status, EnDt, Upload", "Vill, Bari");

														//Household Table: 16 May 2016
														SQL  = "Select h.Vill, h.Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, h.Block, Rnd, h.Upload, ContactNo,h.EnDt";
														SQL += " from Household h,Baris b,MDSSVill v where h.Vill+h.Bari=b.Vill+b.Bari and b.Vill=v.Vill and h.Upload='3' and v.Cluster='"+ g.getClusterCode() +"'";
														CJSon.DownloadJSON_UpdateServer(SQL, "Household", "Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, Rnd, Upload, ContactNo,EnDt", "Vill, Bari, Hh");

														//Visits: 20 Oct 2017
														SQL = "select v.Vill, v.Bari, v.Hh, Rsno, Dma, EnterDt, Vdate, v.Rnd, Lat, Lon, LatNet, LonNet, '1' Upload, v.Note \n" +
																"from Visits v inner join Household h on v.Vill+v.Bari+v.Hh=h.Vill+h.Bari+h.Hh\n" +
																"where h.Clust='"+ g.getClusterCode() +"' and v.Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL, "Visits", "Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon, LatNet, LonNet, Upload, Note", "Vill, Bari, Hh, Rnd");

														//Member Table
														SQL = "Select m.Vill, m.Bari, m.Hh, m.Sno, m.Pno, m.Name, m.Rth, m.Sex, m.BDate, m.Age, m.Mono, m.Fano, m.Edu, m.Ms, m.Pstat, m.LmpDt, m.Sp1, m.Sp2, m.Sp3, m.Sp4, m.Ocp, m.EnType, m.EnDate, m.ExType, m.ExDate, m.PageNo, m.Status, m.Upload, m.PosMig, m.PosMigDate from Member m,Household h";
														SQL += " where m.vill+m.bari+m.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and m.Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL, "Member", "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate", "Vill, Bari, Hh, Sno");

														//Events Table
														SQL = "Select e.Vill, e.Bari, e.Hh, e.Pno, e.Sno, e.EvType, e.EvDate, e.Info1, e.Info2, e.Info3, e.Info4,e.Info5, e.Vdate, e.Rnd, e.Upload";
														SQL += " from Events e,Household h where e.vill+e.bari+e.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and e.Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL,"Events", "Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Info5, Vdate, Rnd, Upload", "Vill, Bari, Hh, Sno, EvDate, Rnd");

														//PregHis: 20 Oct 2017
														SQL = "Select p.Vill, p.Bari, p.Hh, p.Sno, p.Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, p.Rnd, PageNo, Status, p.Upload, Lat, Lon\n" +
																"from PregHis p inner join Household h on p.Vill+p.Bari+p.Hh=h.Vill+h.Bari+h.Hh \n" +
																"where h.Clust='"+ g.getClusterCode() +"' and p.Upload='3'";
														CJSon.DownloadJSON_UpdateServer(SQL,"PregHis", "Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon", "Vill, Bari, Hh, Pno");

														//SES download: 02 Dec 2018
														SQL = "select s.Vill, s.Bari, s.Hh, s.SESNo, s.Visit, s.Q015a, s.Q015b, s.Q015c, s.Q016a, s.Q016b, s.Q016c, s.Q017, s.Q018, s.Q019a, s.Q019b, s.Q019c, s.Q019d, s.Q019e, s.Q019f, \n" +
																"s.Q019g, s.Q019h, s.Q019i, s.Q019j, s.Q019k, s.Q019l, s.Q019m, s.Q019n, s.Q019o, s.Q019p, s.Q019q, s.Q019r, s.Q019s, s.Q019t, s.Q019u, s.Q019v, s.Q019w, s.Q019x, s.Q019y, \n" +
																"s.Q019z, s.Q020a, s.Q020b, s.Q020c, s.Q020d, s.Q020e, s.Q020f, s.Q020g, s.Q020h, s.Q021, s.Q022a, s.Q022b, s.Q022c, s.Q023a, s.Q023b, s.Q024a, s.Q024b, s.Q025a, s.Q025b, \n" +
																"s.Q026, s.Q027a, s.Q027b, s.Q027c, s.Q027d, s.Q027e, s.Q027f, s.Q027g, s.Q027h, s.Q027i, s.Q027j, s.Q027y, s.Q027z, s.Q028a, s.Q028b, s.Q028c, s.Q028d, s.Q028e, s.Q028y, \n" +
																"s.Q029, s.Q030a, s.Q030b, s.Q030c, s.Q030d, s.Q030e, s.Q030f, s.Q030g, s.Q030h, s.Q030z, s.Q031, s.Vdate, s.Rnd, s.PageNo, s.Status, s.Upload, s.Lat, s.Lon from ses s\n" +
																" inner join Baris b on s.vill=b.vill and s.bari=b.bari\n" +
																" inner join MDSSVill c on b.vill=c.Vill \n" +
																" where s.sesno=2 and c.cluster='"+ g.getClusterCode() +"' and s.Upload=3";
														CJSon.DownloadJSON_UpdateServer(SQL,"SES", "Vill, Bari, Hh, SESNo, Visit, Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d, Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q, Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d, Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a, Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y, Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e, Q030f, Q030g, Q030h, Q030z, Q031, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon", "Vill, Bari, Hh, SESNo");


														//Immunization download: 13 Nov 2024
														SQL = "select i.Vill, i.Bari, i.HH, i.Sno, i.PNO, i.Status, i.BCG, i.BCGDT, i.BCGSource, i.Penta1, i.Penta1DT, i.Penta1Source, i.Penta2, i.Penta2DT, i.Penta2Source, i.Penta3, i.Penta3DT, i.Penta3Source, i.PCV1, i.PCV1DT, i.PCV1Source, i.PCV2, i.PCV2DT, i.PCV2Source, i.PCV3, i.PCV3DT, i.PCV3Source, i.\n" +
																" OPV0, i.OPV0DT, i.OPV0Source, i.OPV1, i.OPV1DT, i.OPV1Source, i.OPV2, i.OPV2DT, i.OPV2Source, i.OPV3, i.OPV3DT, i.OPV3Source, i.OPV4, i.OPV4DT, i.OPV4Source, i. Measles, i.MeaslesDT, i.MeaslesSource, i.MR, i.MRDT, i.MRSource, i.Rota, i.RotaDT, i.RotaSource, i.MMR, i.MMRDT, i.MMRSource, i.\n" +
																" Typhoid, i.TyphoidDT, i.TyphoidSource, i.Influ, i.InfluDT, i.InfluSource, i.HepaA, i.HepaADT, i.HepaASource, i.ChickenPox, i.ChickenPoxDT, i.ChickenPoxSource, i.Rabies, i.RabiesDT, i.RabiesSource, i.IPV, i.IPVDT, i.IPVSource, i.VitaminA, i.VitaminADT, i.VitaminASource, i.\n" +
																" fIPV1, i.fIPVDT1, i.fIPVSource1, i.fIPV2, i.fIPVDT2, i.fIPVSource2, i.BCGDTDk, i.Penta1DTDK, i.Penta2DTDK, i.Penta3DTDK, i.PCV1DTDK, i.PCV2DTDK, i.PCV3DTDK, i.OPV0DTDK, i.OPV1DTDK, i.OPV2DTDK, i.OPV3DTDK, i.OPV4DTDK, i.MeaslesDTDK, i.MRDTDK, i.RotaDTDK, i.MMRDTDK, i.TyphoidDTDK, i.\n" +
																" InfluDTDK, i.HepaADTDk, i.ChickenPoxDTDk, i.RabiesDTDk, i.IPVDTDk, i.Fipvdt1dk, i.Fipvdt2Dk, i.VitaminADTDk, i.EnDt, i.Upload from Immunization s\n" +
																" inner join Baris b on i.vill=b.vill and i.bari=b.bari\n" +
																" inner join MDSSVill c on b.vill=c.Vill \n" +
																" where c.cluster='"+ g.getClusterCode() +"' and i.Upload=3";
														CJSon.DownloadJSON_UpdateServer(SQL,"Immunization", "Vill,Bari,HH,Sno,PNO,Status,BCG,BCGDT,BCGSource,Penta1,Penta1DT,Penta1Source,Penta2,Penta2DT,Penta2Source,Penta3,Penta3DT,Penta3Source,PCV1,PCV1DT,PCV1Source,PCV2,PCV2DT,PCV2Source,PCV3,PCV3DT,PCV3Source,OPV0,OPV0DT,OPV0Source,OPV1,OPV1DT,OPV1Source,OPV2,OPV2DT,OPV2Source,OPV3,OPV3DT,OPV3Source,OPV4,OPV4DT,OPV4Source,Measles,MeaslesDT,MeaslesSource,MR,MRDT,MRSource,Rota,RotaDT,RotaSource,MMR,MMRDT,MMRSource,Typhoid,TyphoidDT,TyphoidSource,Influ,InfluDT,InfluSource,HepaA,HepaADT,HepaASource,ChickenPox,ChickenPoxDT,ChickenPoxSource,Rabies,RabiesDT,RabiesSource,IPV,IPVDT,IPVSource,VitaminA,VitaminADT,VitaminASource,fIPV1,fIPVDT1,fIPVSource1,fIPV2,fIPVDT2,fIPVSource2,BCGDTDk,Penta1DTDK,Penta2DTDK,Penta3DTDK,PCV1DTDK,PCV2DTDK,PCV3DTDK,OPV0DTDK,OPV1DTDK,OPV2DTDK,OPV3DTDK,OPV4DTDK,MeaslesDTDK,MRDTDK,RotaDTDK,MMRDTDK,TyphoidDTDK,InfluDTDK,HepaADTDk,ChickenPoxDTDk,RabiesDTDk,IPVDTDk,Fipvdt1dk,Fipvdt2Dk,VitaminADTDk,EnDt,Upload", "Vill, Bari, HH, Sno");



														//PNO Update: Update needed 3 for pno update
														SQL = "Select m.Vill, m.Bari, m.Hh, m.Sno, m.Pno from Member m,Household h";
														SQL += " where m.vill+m.bari+m.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and UpdateNeeded='3'";
														CJSon.DownloadJSON_UpdatePNO(SQL, "Vill, Bari, Hh, Sno, Pno", "Vill, Bari, Hh, Sno");

														//Delete Events: 02 Apr 2014
														SQL = "Select d.Vill, d.Bari, d.Hh, d.Sno, d.EvType, d.EvDate,d.Rnd";
														SQL += " from Household h, EventsDelete d where h.Vill+h.Bari+h.Hh=d.Vill+d.Bari+d.Hh";
														SQL += " and h.Clust='"+ g.getClusterCode() +"' and d.DeleteNeeded='1'";
														CJSon.DownloadJSON_EventDelete(SQL, "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd", "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd");


														//***************************
														//  Upload data to server
														//***************************

														//Table: data_GAge
														TableName = "data_GAge";
														VariableList = "Vill,Bari,HH,SNo,PNo,GAge,StartTime,EndTime,DeviceID,EntryUser,Lat,Lon,EnDt,Upload,modifyDate";
														response = CJSon.UploadJSON(TableName , VariableList , "Vill,Bari,HH,SNo");

														//Table: Baris
														TableName = "Baris";
														VariableList = "Vill, Bari, Block, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec, Status, EnDt, Upload";
														response = CJSon.UploadJSON(TableName , VariableList , "Vill, Bari");

														//Household
														TableName = "Household";
														VariableList = "Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, EnDt, Rnd, Upload, ContactNo,Note";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh");

														//Table: Visits
														TableName = "Visits";
														VariableList = "Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon, LatNet, LonNet, Upload, Note";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh, Rnd");

														//Member
														TableName = "Member";
														VariableList = "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh, Sno");

														//SES
														TableName = "SES";
														VariableList = "Vill, Bari, Hh, SESNo, Visit, Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d, Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q, Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d, Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a, Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y, Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e, Q030f, Q030g, Q030h, Q030z, Q031, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh, SESNo");


														//Preg. History
														TableName = "PregHis";
														VariableList = "Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh, Pno");

														//Delete events data from server and local temp table (UpdateEvents): 02 Dec 2013
														String SQ = "";
														Cursor m = C.ReadData("Select (vill||bari||hh) as hh, sno as sno, pno as pno, evtype as evtype, evdate as evdate from UpdateEvents");
														m.moveToFirst();
														while(!m.isAfterLast())
														{
															SQ = "Delete from Events where ";
															SQ += " Vill+Bari+HH = '"+  m.getString(0).toString() +"' and ";
															SQ += " PNo = '"+  m.getString(2).toString() +"' and ";
															SQ += " SNo = '"+  m.getString(1).toString() +"'  and EvType='"+  m.getString(3).toString() +"' and EvDate='"+  m.getString(4).toString() +"'";
															C.ExecuteCommandOnServer(SQ);

															SQ = "Delete from UpdateEvents where ";
															SQ += " Vill||Bari||HH = '"+  m.getString(0).toString() +"' and ";
															SQ += " PNo = '"+  m.getString(2).toString() +"' and ";
															SQ += " SNo = '"+  m.getString(1).toString() +"'  and EvType='"+  m.getString(3).toString() +"' and EvDate='"+  m.getString(4).toString() +"'";
															C.Save(SQ);

															m.moveToNext();
														}
														m.close();

														//Events
														TableName = "Events";
														VariableList = "Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Info5, Vdate, Rnd, Upload";
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd");

														//Immunization
														TableName = "Immunization";
														VariableList = "Vill,Bari,HH,Sno,PNO,Status,BCG,BCGDT,BCGSource,Penta1,Penta1DT,Penta1Source,Penta2,Penta2DT,Penta2Source,Penta3,Penta3DT,Penta3Source,PCV1,PCV1DT,PCV1Source,PCV2,PCV2DT,PCV2Source,PCV3,PCV3DT,PCV3Source,OPV0,OPV0DT,OPV0Source,OPV1,OPV1DT,OPV1Source,OPV2,OPV2DT,OPV2Source,OPV3,OPV3DT,OPV3Source,OPV4,OPV4DT,OPV4Source,Measles,MeaslesDT,MeaslesSource,MR,MRDT,MRSource,Rota,RotaDT,RotaSource,MMR,MMRDT,MMRSource,Typhoid,TyphoidDT,TyphoidSource,Influ,InfluDT,InfluSource,HepaA,HepaADT,HepaASource,ChickenPox,ChickenPoxDT,ChickenPoxSource,Rabies,RabiesDT,RabiesSource,IPV,IPVDT,IPVSource,VitaminA,VitaminADT,VitaminASource,fIPV1,fIPVDT1,fIPVSource1,fIPV2,fIPVDT2,fIPVSource2,BCGDTDk,Penta1DTDK,Penta2DTDK,Penta3DTDK,PCV1DTDK,PCV2DTDK,PCV3DTDK,OPV0DTDK,OPV1DTDK,OPV2DTDK,OPV3DTDK,OPV4DTDK,MeaslesDTDK,MRDTDK,RotaDTDK,MMRDTDK,TyphoidDTDK,InfluDTDK,HepaADTDk,ChickenPoxDTDk,RabiesDTDk,IPVDTDk,Fipvdt1dk,Fipvdt2Dk,VitaminADTDk,EnDt,Upload";
//														VariableList = "Vill, Bari, HH, Sno, PNo, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource,IPV, IPVDT, IPVSource,fIPV1,fIPVDT1,fIPVSource1,fIPV2,fIPVDT2,fIPVSource2,VitaminA, VitaminADT, VitaminASource, EnDt, Upload";
														String[] I = C.GenerateArrayList(VariableList, TableName);
														response = CJSon.UploadJSON(TableName, VariableList, "Vill, Bari, HH, Sno");

														//Death
														TableName = "Death";
														VariableList = "Vill, Bari, HH, SNo, PNo, Status, DthPlace, FacName, FacOther, Treatment, WhenTreat, Facility, Disp, WhoDisp, Type, Time, EverPreg, PregonDeath, LastPregTime, DMY, CauseofDeath, StartTime, EndTime, Lat, Lon, UserId, EnDt, Upload";
														String[] D = C.GenerateArrayList(VariableList, TableName);
														response = CJSon.UploadJSON(TableName , VariableList , "Vill, Bari, HH, Sno");

														//Table: GPSData
														TableName = "GPSData";
														VariableList = "IDNO, Lat, Lon, Type, Landmark, Landmark_Name, Established, ContactNo, Note, Accuracy, TimeStart, TimeEnd, Sat_Connected, EnDt, UserId, Upload";
														response = CJSon.UploadJSON(TableName , VariableList , "IDNO");


														//19 May 2016
														//Status: Total Bari, Household, Member
														String CurrRound = txtRound.getSelectedItem().toString();
														String PrevRound = String.valueOf(Integer.parseInt(CurrRound)-1);

														if(!C.Existence("Select count(*)Total from ClusterBlock_Status Where Cluster='"+ g.getClusterCode() +"' and Rnd='"+ PrevRound +"'")) {
															TableName = "ClusterBlock_Status";
															SQL = "select Cluster,Block,TotalBari,TotalHH,TotalMem,Rnd from ClusterBlock_Status where Cluster='" + g.getClusterCode() + "' and Rnd='"+ PrevRound +"'";
															VariableList = "Cluster,Block,TotalBari,TotalHH,TotalMem,Rnd";
															response = CJSon.DownloadJSON(SQL, TableName, VariableList, "Cluster,Block,TotalBari,TotalHH,TotalMem,Rnd");
														}


	    		    				                } catch (Exception e) { 
	    		    				                } 
	    		    				                progDailog.dismiss();

													Intent syncService = new Intent(BlockList.this, DatabaseFileSync_Service.class);
													startService(syncService);
	    		    				                
	    		    				            } 
	    		    				        }.start(); 	
    		    				        
    		    				        
    		    				        
    		    	            		}
    		    	            		catch(Exception ex)
    		    	            		{
    		    	            			Connection.MessageBox(BlockList.this, ex.getMessage());
    		    	            		}
    		    	    	        	
    		    	    	            break;
    		
    		    	    	        case DialogInterface.BUTTON_NEGATIVE:
    		    	    	            //No button clicked
    		    	    	            break;
    		    	    	        }
    		    	    	    }
        			    })
        	    	.setNegativeButton("No", null)	//Do nothing on no
        	    	.show();                		
            		   			            		
            	}
            	
            	//GPS Reading
            	//*******************************************************************************            	
            	/*else if(position==1)
            	{
                    //GPS Location
                    FindLocation();
            		Connection.MessageBox(BlockList.this, "GPS from Satellite\n   Latitude:     "+Double.toString(currentLatitude)+"\n   Longitude: "+ Double.toString(currentLongitude)
            										  + "\n\nGPS from Network\n   Latitude:     "+Double.toString(currentLatitudeNet)+"\n   Longitude: "+ Double.toString(currentLongitudeNet));
            		return;
            	}*/

            	//Migration Process
            	//*******************************************************************************            	
            	else if(position==1)
            	{            		                            
                    if(!netwoekAvailable)
                    {
                        Connection.MessageBox(BlockList.this, "Migration Process এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

            		//final ProgressDialog progDailog = ProgressDialog.show( BlockList.this, "", "মাইগ্রেশন প্রসেস হচ্ছে, অপেক্ষা করুন ...", true);

					progDailog = new ProgressDialog(BlockList.this);
					progDailog.setTitle("মাইগ্রেশন প্রসেস");
					progDailog.setMessage("মাইগ্রেশন প্রসেস হচ্ছে, অপেক্ষা করুন ...");
					progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progDailog.setCancelable(false);
					progDailog.setIcon(R.drawable.upload);
					progDailog.show();

			        new Thread() { 
			            public void run() { 
			                try {
								String TableName = "";
								String VariableList = "";
								String SQLStr = "";
								String Res = "";

								Common.Connection CJson = new Common.Connection(BlockList.this);
			            		String SQL = "";
			            		String S  = "";

								//CJson.Sync_Download_Batch("DatabaseTab",CLUSTER,"");
								SQLStr  = "Select TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate from DatabaseTab as t\n" +
										"where not exists(select * from Sync_Management where\n" +
										"lower(TableName)  = lower('DatabaseTab') and\n" +
										"UniqueID   = 'DatabaseTab' and\n" +
										"convert(varchar(19),modifydate,120) = convert(varchar(19),t.modifydate,120) and\n" +
										"UserId   ='"+ CLUSTER +"')";
								VariableList = "TableName, TableScript, ColumnList, UniqueID, Sync_Upload, Sync_Download, BatchSize, modifyDate";
								Res = CJson.DownloadJSON(SQLStr,"DatabaseTab",VariableList,"TableName");

								C.CreateTable("t22","create table t22(pno varchar(10),endate varchar(10))");
								C.CreateTable("t23","create table t23(pno varchar(10),endate varchar(10))");
								C.Save("delete from t22");
								C.Save("delete from t23");
								C.Save("Insert into t22 select pno,endate from member where entype in('22')");
								C.Save("Insert into t23 select pno,endate from member where entype in('22')");

			            		C.Save("Delete from MigDatabase");

			            		//Data from with-in cluster
				    	    	SQL =  "Insert into migdatabase(extype, hh, Sno,Pno,Name,ExDate) select extype, (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where ExType='52'";
				    	    	SQL += " and not exists(select sno from t22 where t22.pno=m.pno and t22.endate=m.exdate)order by Name asc";
				    	    	C.Save(SQL);

				    	    	SQL =  "Insert into migdatabase(extype, hh, Sno,Pno,Name,ExDate) select extype, (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where ExType='53'";
				    	    	SQL += " and not exists(select sno from t23 where t23.pno=m.pno and t23.endate=m.exdate)order by Name asc";
				    	    	C.Save(SQL);


				    	    	//Download data from server
								if(netwoekAvailable)
								{
									//C.Save("Delete from MigDatabase");
								    CJson.ExecuteCommandOnServer("Delete from sync_management where TableName='MigDatabase' and UserId='"+ CLUSTER +"'");
                                    CJson.Sync_Download_Batch("MigDatabase",CLUSTER,"");

                                    C.Save("Delete from Mig_PregHis");
									CJson.ExecuteCommandOnServer("Delete from sync_management where TableName='Mig_PregHis' and UserId='"+ CLUSTER +"'");
									CJson.Sync_Download_Batch("Mig_PregHis",CLUSTER,"");

                                    C.Save("Delete from Mig_Immunization");
									CJson.ExecuteCommandOnServer("Delete from sync_management where TableName='Mig_Immunization' and UserId='"+ CLUSTER +"'");
									CJson.Sync_Download_Batch("Mig_Immunization",CLUSTER,"");

								}
				    	    	
			                } catch (Exception e) { 
			                } 
			                progDailog.dismiss(); 
			            } 
			        }.start(); 	            		

            	}
            	
            	//Bari Update
            	//*******************************************************************************
            	/*else if(position==3)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList.this, "Bari Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

	    	    	if(netwoekAvailable==true)
	    	    	{
		        		String SQ  = "select Cluster, Block, Vill, Bari, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec from vwBariName where Cluster in('"+ g.getClusterCode() +"')";				
		        		C.BariNameUpdate(SQ,"vwBariName", "Cluster, Block, Vill, Bari, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec");
	    	    	}
            	}*/
            	
            	//Block Update
            	//*******************************************************************************
            	/*else if(position==4)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList.this, "Block Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

	    	    	if(netwoekAvailable==true)
	    	    	{
		        		String SQ  = "select Cluster, Block, Vill, Bari, NewCluster, NewBlock from BlockUpdateMobile where Cluster in('"+ g.getClusterCode() +"') and Status='2'";				
		        		C.BlockUpdate(SQ,"BlockUpdateMobile", "Cluster, Block, Vill, Bari, NewCluster, NewBlock");
		        		
		        		//04 Jan 2015
		        		//Bari Transfer from one block to another block
		        		
	    	    	}
            	}*/


            	//Exit from the system
            	//*******************************************************************************
            	else if(position==2)
            	{    				
    				AlertDialog.Builder builder = new AlertDialog.Builder(BlockList.this);
        	    	builder
        			    	.setTitle("Message")	    			    	
        			    	.setMessage("আপনি কি সিস্টেম থেকে বের হতে চান[হ্যাঁ/না]?")
        			    	.setIcon(android.R.drawable.ic_dialog_alert)
        			    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    		    	    	   public void onClick(DialogInterface dialog, int which) {		
    		    	    	    	switch (which){
    		    	    	        case DialogInterface.BUTTON_POSITIVE:
    		    	    	            
    		    	    	            //Disabled GPS options in device
    		    	    	            //Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
    		    	    	            //intent.putExtra("enabled", false);
    		    	    	            //sendBroadcast(intent);
    		    	    	        
    		    	    	            turnGPSOff();
    		    	    	        	
	    		    	    	    finish();
	    		    	    	    System.exit(0);
    		    	    	            break;
    		
    		    	    	        case DialogInterface.BUTTON_NEGATIVE:
    		    	    	            //No button clicked
    		    	    	            break;
    		    	    	        }
    		    	    	    }
        			    })
        	    	.setNegativeButton("No", null)	//Do nothing on no
        	    	.show();     				
            	}
            	
            	//Contact No and Note update
            	//*******************************************************************************            	
            	/*else if(position==6)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList.this, "Contact No and Note Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

            		//02 Jan 2015
            		//Contact no and Note update for Cluster 07,16
            		if(g.getClusterCode().equalsIgnoreCase("07") | g.getClusterCode().equalsIgnoreCase("16") | g.getClusterCode().equalsIgnoreCase("03")){
		    	    	C.UpdateData("Select Vill,Bari,HH,ContactNo from Household where Clust='"+ g.getClusterCode() +"' and len(ContactNo)<>0", "Household", "Vill,Bari,HH,ContactNo", "Vill,Bari,HH");
		    	    	C.UpdateData("Select h.Vill,h.Bari,h.HH,h.Rnd,Note from Visits v, Household h where v.vill+v.bari+v.hh=h.vill+h.bari+h.hh and h.clust='"+ g.getClusterCode() +"' and LEN(Note)<>0", "Visits", "Vill,Bari,HH,Rnd,Note", "Vill,Bari,HH,Rnd");
            		}
            	}*/
             }
             catch(Exception ex)
             {
            	 Connection.MessageBox(BlockList.this, ex.getMessage());
             }            	

            } 

        }); 
        
	}

	private void BariTransfer(String SourceCluster, String DestinationCluster)
	{
		if (g.getClusterCode().equals(SourceCluster))
		{
		    //C.Save("Update Baris");
		}
	}

	public class GridAdapter extends BaseAdapter {
		private Context mContext;
	
		public GridAdapter(Context c) {
			mContext = c;
		}
	
		public int getCount() {
			return mThumbIds.length;
		}
	
		public Object getItem(int position) {
			return null;
		}
	
		public long getItemId(int position) {
			return 0;
		}
	
		//create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			View MyView = convertView;
	        
		if (convertView == null) {
    	            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
    	            MyView = li.inflate(R.layout.single_grid_item, null);
    	
    	            // Add The Image!!!
    	            ImageView iv = (ImageView)MyView.findViewById(R.id.album_image);
    	            iv.setImageResource(mThumbIds[position]);
    	            
    	            // Add The Text!!!
    	            TextView tv = (TextView)MyView.findViewById(R.id.image_name);
    	            tv.setTextSize(20);
    	            tv.setText(desc[position]);
    	            
	            }
    	            return MyView;
		}

		//20 Oct 2017
		private String[] desc={
				"আপলোড",
				"মাইগ্রেশন",
				"বাহির"};

		//references to our images
		private Integer[] mThumbIds = {
				R.drawable.upload,
				R.drawable.sync,
				R.drawable.exit
		};
	}

    //GPS Reading
    //.....................................................................................................
    public void FindLocation() { 
        LocationManager locationManager = (LocationManager) this 
                .getSystemService(Context.LOCATION_SERVICE); 
 
        LocationListener locationListener = new LocationListener() { 
            public void onLocationChanged(Location location) {
                updateLocation(location);                  
            } 
 
            public void onStatusChanged(String provider, int status, Bundle extras) { 
            } 
 
            public void onProviderEnabled(String provider) { 
            } 
 
            public void onProviderDisabled(String provider) { 
            } 
        }; 
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    
    void updateLocation(Location location) { 
        currentLocation  = location; 
        currentLatitude  = currentLocation.getLatitude(); 
        currentLongitude = currentLocation.getLongitude(); 
    } 
    
    //Location from network provider	            
    public void FindLocationNet() { 
        LocationManager locationManager = (LocationManager) this 
                .getSystemService(Context.LOCATION_SERVICE); 
 
        LocationListener locationListener = new LocationListener() { 
            public void onLocationChanged(Location location) { 
                updateLocationNet(location);                  
            } 
 
            public void onStatusChanged(String provider, int status, Bundle extras) { 
            } 
 
            public void onProviderEnabled(String provider) { 
            } 
 
            public void onProviderDisabled(String provider) { 
            } 
        }; 
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    
    void updateLocationNet(Location location1) { 
        currentLocationNet  = location1; 
        currentLatitudeNet  = currentLocationNet.getLatitude(); 
        currentLongitudeNet = currentLocationNet.getLongitude(); 
    }
    
    //Method to turn on GPS
    public void turnGPSOn(){
        try
        {
                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        
                if(!provider.contains("gps")){ //if gps is disabled
                    final Intent poke = new Intent();
                    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                    poke.setData(Uri.parse("3"));
                    sendBroadcast(poke);
                }
        }
        catch (Exception e) {

        }
    }
    
    //Method to turn off the GPS
    public void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    //turning off the GPS if its in on state. to avoid the battery drain.
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        turnGPSOff();
    }
}