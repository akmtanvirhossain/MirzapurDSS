package data.mirzapurdss;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BlockList_Backup extends Activity{
	Connection C;
	Global g;
	boolean netwoekAvailable=false;
	Location currentLocation; 
	double currentLatitude,currentLongitude; 

	Location currentLocationNet; 
	double currentLatitudeNet,currentLongitudeNet; 
	
	
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
	    	    	    Connection.MessageBox(BlockList_Backup.this, "রাঊন্ড নাম্বার আপডেট করার  জন্য ইন্টারনেট কানেকশান পাওয়া যাচ্ছে না।");
	    	    	}
	    	    	else
	    	    	{
			    		String RD = C.ReturnResult("ReturnSingleValue","Select Rnd from Round where CurrentRound='1'");
			    		if(RD==null | RD.length()==0)
			    		{
			    			Connection.MessageBox(BlockList_Backup.this, "রাঊন্ড নাম্বার আপডেট করা সম্ভব হচ্ছেনা।");
			    		}
			    		else
			    		{
				            if(!C.Existence("Select * from Round where Rnd='"+ RD +"'"))
				            {
				            	C.Save("Insert into Round(Rnd,StartDate,EndDate,CurrentRound)Values('"+ RD +"','','','1')");
				                Spinner txtRound=(Spinner)findViewById(R.id.txtRound);
				                txtRound.setAdapter(C.getArrayAdapter("SELECT Rnd from Round where rnd is not null order by rnd desc limit 4"));	            	
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocklist);
        
        C = new Connection(this);
        g = Global.getInstance();

        spnCluster = (Spinner)findViewById(R.id.spnCluster);
        spnCluster.setAdapter(C.getArrayAdapter("Select Cluster from currentcluster"));
        //txtCluster.setText(C.ReturnSingleValue("Select Cluster from CurrentCluster"));
        
        final Spinner txtRound = (Spinner)findViewById(R.id.txtRound);
        txtRound.setAdapter(C.getArrayAdapter("SELECT Rnd from Round where rnd is not null and rnd not in('null') order by rnd desc limit 4"));
        
        
        final Spinner txtBlock = (Spinner)findViewById(R.id.txtBlock);
        txtBlock.setAdapter(C.getArrayAdapter("Select distinct block from baris order by cast(block as int)"));
        txtBlock.setSelection(Global.SpinnerItemPosition(txtBlock, 2, g.getBlockCode()));

        //Bari Number Update: 18 Sep 2014
        String NewVillNo = "";
    	String NewBariNo = "";
    	String OldVillBari = "";
    	
    	//01/12/2015 only for death report sync
    	/*if(g.getClusterCode().equals("06") & Global.DateNowDMY().equals("01/12/2015"))
    	{
    	        String R = C.ReturnSingleValue( "Select count(*)Total from Death");
    	        if(Integer.valueOf( R )<100)
    	        {
                	String S = "select Vill, Bari, HH, SNo, PNo, Status, DthPlace, FacName, FacOther, Treatment, WhenTreat, Facility, Disp, WhoDisp, Type, Time, EverPreg, PregonDeath, LastPregTime, DMY, CauseofDeath, StartTime, EndTime, Lat, Lon, UserId, EnDt, Upload  from Death where Vill+Bari+HH in(select Vill+Bari+HH from Household where Clust='06') and Status='01'";
                	C.DownloadData(S,"Death","Vill, Bari, HH, SNo, PNo, Status, DthPlace, FacName, FacOther, Treatment, WhenTreat, Facility, Disp, WhoDisp, Type, Time, EverPreg, PregonDeath, LastPregTime, DMY, CauseofDeath, StartTime, EndTime, Lat, Lon, UserId, EnDt, Upload","Vill, Bari, HH, SNo");
    	        }
    	}*/
    	
    	/*
    	if(g.getClusterCode().equals("01"))
        {
    	    C.Save( "Delete from Baris where Vill='102' and Bari='0516'");
    	    C.Save( "Delete from Baris where Vill='102' and Bari='0496'");
        }
    	else if(g.getClusterCode().equals("03"))
        {
            C.Save( "Delete from Baris where Vill='231' and Bari='0141'");
            C.Save( "Delete from Baris where Vill='321' and Bari='0110'");
            C.Save( "Delete from Baris where Vill='114' and Bari='0170'");
        }
    	else if(g.getClusterCode().equals("04"))
        {
            C.Save( "Delete from Baris where Vill='228' and Bari='N298'");
            C.Save( "Delete from Baris where Vill='228' and Bari='0228'");
        }
        else if(g.getClusterCode().equals("05"))
        {
            C.Save( "Delete from Baris where Vill='326' and Bari='Q338'");
        }
        else if(g.getClusterCode().equals("07"))
        {
            C.Save( "Delete from Baris where Vill='307' and Bari='0641'");
        }
        else if(g.getClusterCode().equals("08"))
        {
            C.Save( "Delete from Baris where Vill='153' and Bari='0096'");
            C.Save( "Delete from Baris where Vill='153' and Bari='0097'");
        }
        else if(g.getClusterCode().equals("14"))
        {
            C.Save( "Delete from Baris where Vill='207' and Bari='0208'");
            C.Save( "Delete from Baris where Vill='207' and Bari='0214'");
        }
        else if(g.getClusterCode().equals("15"))
        {
            C.Save( "Delete from Baris where Vill='175' and Bari='0068'");
        }
        else if(g.getClusterCode().equals("16"))
        {
            C.Save( "Delete from Baris where Vill='243' and Bari='0391'");
        }
    	*/
    	
    	//30 Jun 2015
    	//PNO Missing in Pregnancy History
    	String SQL = "";
    	SQL = "select h.Vill||h.Bari||h.HH||h.Sno hh,h.Pno from Member h,PregHis p where h.Vill||h.Bari||h.HH||h.Sno=p.Vill||p.Bari||p.Hh||p.Sno and LENgth(p.pno)=0";    	
        Cursor m = C.ReadData(SQL);
        m.moveToFirst();
        while(!m.isAfterLast())
        {       
            try
            {
                SQL = "Update PregHis set pno='"+ m.getString(1).toString() +"' where vill||bari||hh||sno='"+ m.getString(0).toString() +"'";   
                C.Save(SQL);
            }
            catch(Exception e)
            {
                
            }
                
            m.moveToNext();
        }                       
        m.close();     	
    	
    	
    	
    	/*
        if(g.getClusterCode().equals("08"))
        {        	
        	NewBariNo  = "0064";
        	OldVillBai = "1440131";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        	NewBariNo  = "0065";
        	OldVillBai = "1440132";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	
        	NewBariNo  = "0066";
        	OldVillBai = "1440133";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        	NewBariNo  = "0067";
        	OldVillBai = "1440134";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        	NewBariNo  = "0068";
        	OldVillBai = "1440135";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        }
        else if(g.getClusterCode().equals("10"))
        {        	
        	NewBariNo  = "0107";
        	OldVillBai = "3180161";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        
        	NewBariNo  = "0108";
        	OldVillBai = "3180162";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        
        	NewBariNo  = "0109";
        	OldVillBai = "3180163";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        	NewBariNo  = "0110";
        	OldVillBai = "3180164";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");

        	NewBariNo  = "0111";
        	OldVillBai = "3180165";
        	
        	C.Save("Update Events		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update preghis		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update immunization Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update member		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update ses			Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update visits		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update household	Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        	C.Save("Update baris		Set Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBai +"'");
        }
        */
        
    	/*try
    	{
           if(g.getClusterCode().equals("07"))
            {               
        	    NewVillNo   = "307";
                    NewBariNo   = "0652";
                    OldVillBari = "3090537";
                    if(!C.Existence("Select Vill from Household where Vill||Bari='"+ (NewVillNo+NewBariNo) +"' " ))
                    {                
                        C.Save("Update Events           Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update preghis          Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update immunization     Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update member           Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update ses              Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update visits           Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update household        Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("Update baris            Set Vill='"+ NewVillNo +"',Bari='"+ NewBariNo +"' where Vill||Bari='"+ OldVillBari +"'");
                        C.Save("delete from Baris where vill||bari in('3090504') and not exists(select * from household where vill||bari=Baris.vill||Baris.bari)");
                    }
            }
    	}
    	catch(Exception ex)
    	{
    	    Connection.MessageBox( BlockList.this, ex.getMessage() );
    	    return;
    	}
    	*/
        Button cmdBlock = (Button)findViewById(R.id.cmdBlock);
        cmdBlock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try
            	{
	         	  	final ProgressDialog progDailog = ProgressDialog.show(BlockList_Backup.this, "", "অপেক্ষা করুন ...", true); 

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
				         	  	
				         	finish();				         	  	
				     	    	Intent f11 = new Intent(getApplicationContext(),HouseholdIndex.class);
				     	    	//Intent f11 = new Intent(getApplicationContext(),SESPregHisMissingList.class);
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
            		Connection.MessageBox(BlockList_Backup.this, ex.getMessage());
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
            	//Upload data to central server
            	//*******************************************************************************
            	if(position==0)
            	{   	
            		
            		AlertDialog.Builder builder = new AlertDialog.Builder(BlockList_Backup.this);
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
    		    	                		Connection.MessageBox(BlockList_Backup.this, "Internet connection is not avialable.");
    		    	                		return;
    		    	                	}
    		    	            		
    		    	            		try
    		    	            		{    		    	            			
    		    	            			String ResponseString="Upload Status:";
 
	    		    				        final ProgressDialog progDailog = ProgressDialog.show( 
	    		    				        		BlockList_Backup.this, "", "অপেক্ষা করুন ...", true); 

	    		    				        new Thread() { 
	    		    				            public void run() { 
	    		    				            	String ResponseString="Upload Status:";
	    		    				            	String response;
	    		    				            	
	    		    				                try {    		    				        
	    		    		    	    	        	String VariableList;
	    		    		    	    	        	String TableName;	
	    		    		    	    	        	String SQL = "";
	    		    		    	    	        	
	    		    		    	    	        	
	    		    		    	    	        	//Upload/Download Status: 02 Apr 2014
	    		    		    	    	        	String r = C.ExecuteCommandOnServer("Insert into UploadMonitor(Cluster)Values('"+ g.getClusterCode() +"')");
	    		    		    	    	        	
	    		    		    	    	        	/*
	    		    		    	    	        	String[] UpdateControl = Connection.split(C.ReturnResult("ReturnSingleValue","select (Member+','+Event+','+PNo+','+EvDel)UC from UpdateControl where Cluster='"+ g.getClusterCode() +"'"), ',');
	    		    		    	    	        	String MemberUpdate = UpdateControl[0].toString();
	    		    		    	    	        	String EventUpdate  = UpdateControl[1].toString();
	    		    		    	    	        	String PNoUpdate    = UpdateControl[2].toString();
	    		    		    	    	        	String EventDel     = UpdateControl[3].toString();
	    		    		    	    	        	*/
	    		    		    	    	        	
	    		    		    	    	        	//***************************
	    		        		    	            	//  Update Local Database
	    		    		    	    	        	//***************************
	    		    		    	    	        	
	    		        		    	            	//Member Table: 01 Apr 2014
	    		    					        SQL = "Select m.Vill, m.Bari, m.Hh, m.Sno, m.Pno, m.Name, m.Rth, m.Sex, m.BDate, m.Age, m.Mono, m.Fano, m.Edu, m.Ms, m.Pstat, m.LmpDt, m.Sp1, m.Sp2, m.Sp3, m.Sp4, m.Ocp, m.EnType, m.EnDate, m.ExType, m.ExDate, m.PageNo, m.Status, m.Upload, m.PosMig, m.PosMigDate from Member m,Household h";
	    		    					        SQL += " where m.vill+m.bari+m.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and UpdateNeeded='1'";				
	    		    					        C.UpdateData(SQL,"Member", "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate", "Vill, Bari, Hh, Sno");
	    		        		    	            	
	    		    					        //Events Table: 01 Apr 2014
	    		    					        SQL = "Select e.Vill, e.Bari, e.Hh, e.Pno, e.Sno, e.EvType, e.EvDate, e.Info1, e.Info2, e.Info3, e.Info4, e.Vdate, e.Rnd, e.Upload";
	    		    					        SQL += " from Events e,Household h where e.vill+e.bari+e.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and UpdateNeeded='1'";				
	    		    					        C.UpdateData(SQL,"Events", "Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload", "Vill, Bari, Hh, Sno, EvDate, Rnd");
	    		        		    	            		    		        		    	            	
	    		        		    	            	//PNO Update: Update needed 3 for pno update
	    		    					        SQL = "Select m.Vill, m.Bari, m.Hh, m.Sno, m.Pno from Member m,Household h";
	    		    					        SQL += " where m.vill+m.bari+m.Hh=h.Vill+h.Bari+h.HH and h.Clust='"+ g.getClusterCode() +"' and UpdateNeeded='3'";				
	    		    					        C.PNOUpdate(SQL,"Vill, Bari, Hh, Sno, Pno", "Vill, Bari, Hh, Sno");														
	    		        		    	            	
	    		    					        //Delete Events: 02 Apr 2014
	    		    					        SQL = "Select d.Vill, d.Bari, d.Hh, d.Sno, d.EvType, d.EvDate,d.Rnd";
	    		    					        SQL += " from Household h, EventsDelete d where h.Vill+h.Bari+h.Hh=d.Vill+d.Bari+d.Hh";
	    		    					        SQL += " and h.Clust='"+ g.getClusterCode() +"' and d.DeleteNeeded='1'";
	    		    					        C.EventDelete(SQL, "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd", "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd");														
	    		    					        		

	    		    		    	    	        	//***************************
	    		        		    	            	//  Upload data to server
	    		    		    	    	        	//***************************
	    		    					        
	    		    		    	            		//Table: Baris
	    		    		    	            		TableName = "Baris";
	    		        		    	    	        VariableList = "Vill, Bari, Block, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec, Status, EnDt, Upload";
	    		        		    	    	        String[] B = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(B, TableName , VariableList , "Vill, Bari");
	    		        		    	            	
	    		        		    	            	
	    		    		    	            		//Table: Visits
	    		    		    	            		TableName = "Visits";
	    		        		    	    	        VariableList = "Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon, LatNet, LonNet, Upload, Note";
	    		        		    	    	        String[] V = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(V, TableName , VariableList , "Vill, Bari, Hh, Rnd");
	    		        		    	            	
	    		        		    	            	
	    		        		    	            	//Household
	    		    		    	            		TableName = "Household";
	    		        		    	    	        VariableList = "Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, EnDt, Rnd, Upload, ContactNo";
	    		        		    	    	        String[] H = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(H, TableName , VariableList , "Vill, Bari, Hh");
	    		        		    	            	
	    		        		    	            	
	    		        		    	            	//SES
	    		    		    	            		TableName = "SES";
	    		        		    	    	        VariableList = "Vill, Bari, Hh, SESNo, Visit, Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d, Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q, Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d, Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a, Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y, Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e, Q030f, Q030g, Q030h, Q030z, Q031, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon";
	    		        		    	    	        String[] S = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(S, TableName , VariableList , "Vill, Bari, Hh, SESNo");

	    		        		    	            	    		        		    	            	
	    		        		    	            	//Member
	    		    		    	            		TableName = "Member";
	    		        		    	    	        VariableList = "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate";
	    		        		    	    	        String[] M = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(M, TableName , VariableList , "Vill, Bari, Hh, Sno");
	    		        		    	            	
	    		        		    	            	
	    		        		    	            	//Preg. History
	    		    		    	            		TableName = "PregHis";
	    		        		    	    	        VariableList = "Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon";
	    		        		    	    	        String[] P = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(P, TableName , VariableList , "Vill, Bari, Hh, Pno");
	    		        		    	            	
	    		        		    	            	
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
	    		        		    	    	        VariableList = "Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload";
	    		        		    	    	        String[] E = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(E, TableName , VariableList , "Vill, Bari, Hh, Sno, EvType, EvDate, Rnd");
	    		        		    	            		    		        		    	            	
	    		        		    	            	//Immunization
	    		    		    	            		TableName = "Immunization";
	    		        		    	    	        VariableList = "Vill, Bari, HH, Sno, PNo, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource,IPV, IPVDT, IPVSource,VitaminA, VitaminADT, VitaminASource, EnDt, Upload";
	    		        		    	    	        String[] I = C.GenerateArrayList(VariableList, TableName);
	    		        		    	            	response = C.UploadData(I, TableName , VariableList , "Vill, Bari, HH, Sno");
	    		        		    	            		    		        		    	            		    		        		    	            	
                                                                        //Death
                                                                        TableName = "Death";
                                                                        VariableList = "Vill, Bari, HH, SNo, PNo, Status, DthPlace, FacName, FacOther, Treatment, WhenTreat, Facility, Disp, WhoDisp, Type, Time, EverPreg, PregonDeath, LastPregTime, DMY, CauseofDeath, StartTime, EndTime, Lat, Lon, UserId, EnDt, Upload";
                                                                        String[] D = C.GenerateArrayList(VariableList, TableName);
                                                                        response = C.UploadData(D, TableName , VariableList , "Vill, Bari, HH, Sno");
	    		    					        		
                                                                        
                                                                        //30 Jun 2015
                                                                        //Execute command from server on local device
	    		    					        		
	    		    				                } catch (Exception e) { 
	    		    				                } 
	    		    				                progDailog.dismiss(); 
	    		    				                
	    		    				            } 
	    		    				        }.start(); 	
    		    				        
    		    				        
    		    				        
    		    	            		}
    		    	            		catch(Exception ex)
    		    	            		{
    		    	            			Connection.MessageBox(BlockList_Backup.this, ex.getMessage());
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
            	else if(position==1)
            	{
                    //GPS Location
                    FindLocation();
            		Connection.MessageBox(BlockList_Backup.this, "GPS from Satellite\n   Latitude:     "+Double.toString(currentLatitude)+"\n   Longitude: "+ Double.toString(currentLongitude)
            										  + "\n\nGPS from Network\n   Latitude:     "+Double.toString(currentLatitudeNet)+"\n   Longitude: "+ Double.toString(currentLongitudeNet));
            		return;
            	}

            	//Migration Process
            	//*******************************************************************************            	
            	else if(position==2)
            	{            		                            
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList_Backup.this, "Migration Process এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

            		final ProgressDialog progDailog = ProgressDialog.show( 
			                BlockList_Backup.this, "", "অপেক্ষা করুন ...", true); 

			        new Thread() { 
			            public void run() { 
			                try {
			            		String SQL = "";
			            		String S  = "";
			            		
			            		C.Save("Delete from MigDatabase");

			            		//Data from with-in cluster
				    	    	SQL =  "Insert into migdatabase(extype, hh, Sno,Pno,Name,ExDate) select extype, (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where ExType='52'";
				    	    	SQL += "and not exists(select sno from Member where pno=m.pno and entype='22' and endate=m.exdate)order by Name asc";  
				    	    	C.Save(SQL);

				    	    	SQL =  "Insert into migdatabase(extype, hh, Sno,Pno,Name,ExDate) select extype, (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where ExType='53'";
				    	    	SQL += "and not exists(select sno from Member where pno=m.pno and entype='23' and endate=m.exdate)order by Name asc";  
				    	    	C.Save(SQL);
				    	    					    	    
				    	    
				    	    	if(netwoekAvailable==true)
				    	    	{
					    	    //Data from outside cluster
				    	    	    S  = "select extype, hh, Sno,Pno,Name,ExDate from MigDatabase m,mdssvill v where left(m.hh,3)=v.vill and v.Cluster not in('"+ g.getClusterCode() +"')";				
				    	    	    C.DownloadMigData(S,"MigDatabase", "extype, hh, Sno, Pno, Name, ExDate");
				    	    	    
				    	    	    //Pregnancy History(Migration)
				    	    	    C.Save("Delete from Mig_PregHis");
                                                    S  = "select Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon from PregHis p where exists(select * from MigDatabase where Pno=p.Pno)";                             
                                                    C.DownloadMigData(S,"Mig_PregHis", "Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon");

                                                    //Immunization(Migration)
                                                    C.Save("Delete from Mig_Immunization");
                                                    S  = "select Vill, Bari, HH, Sno, PNO, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource, IPV, IPVDT, IPVSource, VitaminA, VitaminADT, VitaminASource, EnDt, Upload from Immunization i where exists(select * from MigDatabase where Pno=i.Pno)";                             
                                                    C.DownloadMigData(S,"Mig_Immunization", "Vill, Bari, HH, Sno, PNO, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource, IPV, IPVDT, IPVSource, VitaminA, VitaminADT, VitaminASource, EnDt, Upload");

				    	    	}
				    	    	
			                } catch (Exception e) { 
			                } 
			                progDailog.dismiss(); 
			            } 
			        }.start(); 	            		

            	}
            	
            	//Bari Update
            	//*******************************************************************************
            	else if(position==3)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList_Backup.this, "Bari Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

	    	    	if(netwoekAvailable==true)
	    	    	{
		        		String SQ  = "select Cluster, Block, Vill, Bari, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec from vwBariName where Cluster in('"+ g.getClusterCode() +"')";				
		        		C.BariNameUpdate(SQ,"vwBariName", "Cluster, Block, Vill, Bari, BariName, BariLoc, Xdec, Xmin, Xsec, Ydec, Ymin, Ysec");
	    	    	}
            	}
            	
            	//Block Update
            	//*******************************************************************************
            	else if(position==4)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList_Backup.this, "Block Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

	    	    	if(netwoekAvailable==true)
	    	    	{
		        		String SQ  = "select Cluster, Block, Vill, Bari, NewCluster, NewBlock from BlockUpdateMobile where Cluster in('"+ g.getClusterCode() +"') and Status='2'";				
		        		C.BlockUpdate(SQ,"BlockUpdateMobile", "Cluster, Block, Vill, Bari, NewCluster, NewBlock");
		        		
		        		//04 Jan 2015
		        		//Bari Transfer from one block to another block
		        		
	    	    	}
            	}

            	
            	//Exit from the system
            	//*******************************************************************************
            	else if(position==5)
            	{    				
    				AlertDialog.Builder builder = new AlertDialog.Builder(BlockList_Backup.this);
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
            	else if(position==6)
            	{
                    if(netwoekAvailable==false)
                    {
                        Connection.MessageBox(BlockList_Backup.this, "Contact No and Note Update এর জন্য ইন্টারনেট থাকা আবশ্যক।");
                    }

            		//02 Jan 2015
            		//Contact no and Note update for Cluster 07,16
            		if(g.getClusterCode().equalsIgnoreCase("07") | g.getClusterCode().equalsIgnoreCase("16")){	    	    	
		    	    	C.UpdateData("Select Vill,Bari,HH,ContactNo from Household where Clust='"+ g.getClusterCode() +"' and len(ContactNo)<>0", "Household", "Vill,Bari,HH,ContactNo", "Vill,Bari,HH");
		    	    	C.UpdateData("Select h.Vill,h.Bari,h.HH,h.Rnd,Note from Visits v, Household h where v.vill+v.bari+v.hh=h.vill+h.bari+h.hh and h.clust='"+ g.getClusterCode() +"' and LEN(Note)<>0", "Visits", "Vill,Bari,HH,Rnd,Note", "Vill,Bari,HH,Rnd");
            		}
            	}
             }
             catch(Exception ex)
             {
            	 Connection.MessageBox(BlockList_Backup.this, ex.getMessage());
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
	
		private String[] desc={		
				"আপলোড",			
				"জি পি এস",
				"Process Migration",
				"Bari Update",
				"Block Update",
				"বাহির",
				"Contact/Note Update"};
		
		//references to our images
		private Integer[] mThumbIds = {
				R.drawable.upload, 
				R.drawable.gps, 
				R.drawable.sync, 
				R.drawable.sync,
				R.drawable.sync, 
				R.drawable.exit,
				R.drawable.sync
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