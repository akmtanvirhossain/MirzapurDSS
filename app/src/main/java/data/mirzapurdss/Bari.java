package data.mirzapurdss;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.text.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.text.TextWatcher;
import android.text.Editable;
//import android.provider.Settings.Global;


public class Bari extends AppCompatActivity {
	 Connection C;
	 Location currentLocation; 
	 double currentLatitude,currentLongitude; 
	 Global g;
	 ViewFlipper flipper;	
	 SimpleAdapter mSchedule;
	
	
	String Status="",EntryStatus="",form="",PatientID="123";
	String DCode="10",UpCode="10",Cluster="10",Block="10",VCode="10",Bari="10",HH="10",Round="10",Pno="10789";
	Integer SLNO=0;

	ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

	
		//Disabled Back/Home key
		//--------------------------------------------------------------------------------------------------	
		@Override 
		public boolean onKeyDown(int iKeyCode, KeyEvent event)
		{
		    if(iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME) 
		         { return false; }
		    else { return true;  }
		}

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bari);
        C=new Connection(this);
        g=new Global();
        EntryStatus="Entry";
    }
    
  
   
    
   
    
    
  /*  
    public void DataRetrieve()
    {
    	try
    	{   		
    		
    	
    
	  
  		Integer Count=Integer.valueOf(C.ReturnSingleValue("Select count(*) from VitalSign where  PatientID='" + PatientID + "' and  VDate='" + g.CurrentDateYYYYMMDD + "'"));
  		//cur.moveToFirst();
  		rdoAY.setEnabled(true);
  		rdoAN.setEnabled(true);
  		rdoGY.setEnabled(true);
  		rdoGN.setEnabled(true);
  		rdoHY.setEnabled(true);
  		rdoHN.setEnabled(true);  		
  		txtB.setEnabled(true);
  		txtC.setEnabled(true);
  		txtD.setEnabled(true);
  		txtE.setEnabled(true);
  		txtF.setEnabled(true);
  		
  		if(Count==0)
  		{
  			rdo1st.setEnabled(true);
  			rdo2nd.setEnabled(false);
  		}
  		else if(Count==1)
  		{
  			rdo1st.setEnabled(false);
  			rdo2nd.setEnabled(true);
  		}
  		else if(Count==2)
  		{
  			rdo1st.setEnabled(false);
  			rdo2nd.setEnabled(false);
  			rdoAY.setEnabled(false);
  	  		rdoAN.setEnabled(false);
  	  		rdoGY.setEnabled(false);
  	  		rdoGN.setEnabled(false);
  	  		rdoHY.setEnabled(false);
  	  		rdoHN.setEnabled(false);  
  	  		txtB.setEnabled(false);
  	  		txtC.setEnabled(false);
  	  		txtD.setEnabled(false);
  	  		txtE.setEnabled(false);
  	  		txtF.setEnabled(false);
  		}
  		
  		ListView list1 = (ListView) findViewById(R.id.lstVitalSignInfo);
	  	list1.setAdapter(null);	
	  
  		Cursor cur=C.ReadData("Select VDate,(case when QA  is null then '' else QA    end) QA,(case when QB  is null then '' else QB  end) QB,(case when QC  is null then 0 else QC  end) QC,(case when QD  is null then '' else QD  end) QD,(case when QE  is null then '' else QE  end) QE,(case when QF  is null then '' else QF  end) QF,(case when QG  is null then '' else QG  end) QG,(case when QH  is null then '' else QH  end) QH,(case when Round  is null then '' else Round  end) Round from VitalSign where  PatientID='" + PatientID + "' and  VDate='" + g.CurrentDateYYYYMMDD + "' order by VDate , Round asc");
  		cur.moveToFirst();
  		mylist.clear();
	        if(cur.getCount()==0)
	        { 
	        	
	        	SLNO=1;
	        	return;
	        }
	        else
	        {
	        	//MemberEntryEdit="Edit";
	        	
	        }

	      
	        
	        while(!cur.isAfterLast())
	        {       
	        		HashMap<String, String> map = new HashMap<String, String>();
	  				ListView list = (ListView) findViewById(R.id.lstVitalSignInfo);  
	  				
	  				map.put("VDate", cur.getString(cur.getColumnIndex("VDate")));
	  				if(cur.getString(cur.getColumnIndex("QA")).equals("1"))
	  				{
	  					map.put("Cons", "Yes");
	  				}
	  				else
	  				{
	  					map.put("Cons", "NO");
	  				}
					map.put("Temp", cur.getString(cur.getColumnIndex("QB")));
					map.put("Res", cur.getString(cur.getColumnIndex("QC")));
					map.put("Pulse", cur.getString(cur.getColumnIndex("QD")));
					map.put("Satu", cur.getString(cur.getColumnIndex("QE")));
					map.put("BPress", cur.getString(cur.getColumnIndex("QF")));
					
					if(cur.getString(cur.getColumnIndex("QG")).equals("1"))
	  				{
	  					map.put("Dehy", "Yes");
	  				}
	  				else
	  				{
	  					map.put("Dehy", "NO");
	  				}
					
					if(cur.getString(cur.getColumnIndex("QH")).equals("1"))
	  				{
	  					map.put("Umb", "Yes");
	  				}
	  				else
	  				{
	  					map.put("Umb", "NO");
	  				}
					
					if(cur.getString(cur.getColumnIndex("Round")).equals("1"))
	  				{
	  					map.put("Rnd", "1");
	  				}
	  				else
	  				{
	  					map.put("Rnd", "2");
	  				}
					map.put("edit","edit");
	  				map.put("del","del");
					mylist.add(map);
		  					


					mSchedule = new SimpleAdapter(Blood.this, mylist, R.layout.rowvitalsign,new String[] {"V Date","Cons","Temp", "Res", "Pulse","Satu","B Press","Dehy","Umb","Rnd","edit","del"},
								new int[] {R.id.VDate, R.id.Cons,R.id.Temp, R.id.Res, R.id.Pulse, R.id.Satu, R.id.BPress, R.id.Dehy, R.id.Umb, R.id.Rnd,R.id.cmdB1,R.id.cmdB2});
					//mSchedule.notifyDataSetChanged();
					//list.setAdapter(mSchedule);	  
					list.setAdapter(new MemberListAdapter(this));
	        	
	        	cur.moveToNext();
	        }  		        
	        cur.close();
  		
  		
  		
  		
  		
  		
  		
  		
    	}
	    catch(Exception  e)
		{
			Connection.MessageBox(Blood.this, e.getMessage());
		}	        
    }
   
  
    public class MemberListAdapter extends BaseAdapter 
    {
        private Context context;

        public MemberListAdapter(Context c){
            context = c;
        }
 
        public int getCount() {
            return mSchedule.getCount();
        }
 
        public Object getItem(int position) {
            return position;
        }
 
        public long getItemId(int position) {
            return position;
        }
        
            
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.rowvitalsign, null); 
			}
			

			TextView VDate= (TextView) convertView.findViewById(R.id.VDate);
			TextView Cons= (TextView) convertView.findViewById(R.id.Cons);
			TextView Temp= (TextView) convertView.findViewById(R.id.Temp);
			TextView Res= (TextView) convertView.findViewById(R.id.Res);
			TextView Pulse= (TextView) convertView.findViewById(R.id.Pulse);
			TextView Satu= (TextView) convertView.findViewById(R.id.Satu);
			TextView BPress= (TextView) convertView.findViewById(R.id.BPress);
			TextView Dehy= (TextView) convertView.findViewById(R.id.Dehy);
			TextView Umb= (TextView) convertView.findViewById(R.id.Umb);
			TextView Rnd= (TextView) convertView.findViewById(R.id.Rnd);
			Button   cmdB1 = (Button)convertView.findViewById(R.id.cmdB1);
			Button   cmdB2 = (Button)convertView.findViewById(R.id.cmdB2);

			
			final HashMap<String, String> o = (HashMap<String, String>) mSchedule.getItem(position);
			
			VDate.setText(o.get("VDate"));
			Cons.setText(o.get("Cons"));
			Temp.setText(o.get("Temp"));
			Res.setText(o.get("Res"));
			Pulse.setText(o.get("Pulse"));
			Satu.setText(o.get("Satu"));
			BPress.setText(o.get("BPress"));
			Dehy.setText(o.get("Dehy"));
			Umb.setText(o.get("Umb"));
			Rnd.setText(o.get("Rnd"));
			
	        final AlertDialog.Builder adb = new AlertDialog.Builder(Blood.this);
	        
	        	cmdB1.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
				          adb.setTitle("Member Edit:");
				          adb.setMessage("à¦†à¦ªà¦¨à¦¿ à¦¸à¦¦à¦¸à§�à¦¯à§‡à¦° à¦¤à¦¥à§�à¦¯ à¦�à¦¡à¦¿à¦Ÿ à¦•à¦°à¦¤à§‡ à¦šà¦¾à¦¨?[Yes/No]?");
				          adb.setNegativeButton("No", null);
				          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
				              public void onClick(DialogInterface dialog, int which) {
				                 
				            	  
				            	  
				            	  rdoAY.setEnabled(true);
				            	  rdoAN.setEnabled(true);
				            	  rdoGY.setEnabled(true);
				            	  rdoGN.setEnabled(true);
				            	  rdoHY.setEnabled(true);
				            	  rdoHN.setEnabled(true);  		
				            	  txtB.setEnabled(true);
				            	  txtC.setEnabled(true);
				            	  txtD.setEnabled(true);
				            	  txtE.setEnabled(true);
				            	  txtF.setEnabled(true);
				            	  
				            	  EntryStatus="Edit";
				                  SLNO=Integer.valueOf(o.get("Rnd"));
				                 
				                  
				                 // MemberEntryEdit="Edit";
				                  
				                  
				                 	Cursor cur=C.ReadData("Select " +
				                			" (case when QA  is null then '' else QA    end) QA," +
				                			" (case when QB  is null then '' else QB  end) QB," +
				                			" (case when QC  is null then 0 else QC  end) QC," +
				                			" (case when QD  is null then '' else QD  end) QD," +
				                			" (case when QE  is null then '' else QE  end) QE," +
				                			" (case when QF  is null then '' else QF  end) QF," +
				                			" (case when QG  is null then '' else QG  end) QG," +				                						                			
				                			" (case when QH  is null then '' else QH  end) QH," +
				                			" (case when Round  is null then '' else Round  end) Round" +
				                			" from VitalSign where PatientID='" + PatientID + "' and  VDate='" + g.CurrentDateYYYYMMDD + "' and Round='"+ SLNO +"'");
				        	        
				        	        cur.moveToFirst();
				        	        while(!cur.isAfterLast())
				        	        {      
				        	        	if(cur.getString(cur.getColumnIndex("QA")).equals("1"))
				        	        	{
				        	        		rdoAY.setChecked(true);
				        	        	}
				        	        	else
				        	        	{
				        	        		rdoAN.setChecked(true);
				        	        		
				        	        	}
				        	        	
				        	        	txtB.setText(cur.getString(cur.getColumnIndex("QB")));
				        	        	txtC.setText(cur.getString(cur.getColumnIndex("QC")));
				        	        	txtD.setText(cur.getString(cur.getColumnIndex("QD")));
				        	        	txtE.setText(cur.getString(cur.getColumnIndex("QE")));
				        	        	txtF.setText(cur.getString(cur.getColumnIndex("QF")));
				        	        	if(cur.getString(cur.getColumnIndex("QG")).equals("1"))
				        	        	{
				        	        		rdoGY.setChecked(true);
				        	        	}
				        	        	else
				        	        	{
				        	        		rdoGN.setChecked(true);
				        	        		
				        	        	}
				        	        	if(cur.getString(cur.getColumnIndex("QH")).equals("1"))
				        	        	{
				        	        		rdoHY.setChecked(true);
				        	        	}
				        	        	else
				        	        	{
				        	        		rdoHN.setChecked(true);
				        	        		
				        	        	}
				        	        	if(cur.getString(cur.getColumnIndex("Round")).equals("1"))
				        	        	{
				        	        		rdo1st.setChecked(true);
				        	        	}
				        	        	else
				        	        	{
				        	        		rdo2nd.setChecked(true);
				        	        		
				        	        	}
				        	        	cur.moveToNext();
				        	        }  		        
				        	        cur.close();
				        	        
     	
				    				
				        	       // flipper.setDisplayedChild(0);
				              }});
				          adb.show();
		            }
		        });
	        	        
	        	cmdB2.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
				          adb.setTitle("Member Delete:");
				          adb.setMessage("à¦†à¦ªà¦¨à¦¿ à¦¸à¦¦à¦¸à§�à¦¯à§‡à¦° à¦¤à¦¥à§�à¦¯ à¦¡à¦¿à¦²à¦¿à¦Ÿ à¦•à¦°à¦¤à§‡ à¦šà¦¾à¦¨?[Yes/No]?");
				          adb.setNegativeButton("No", null);
				          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
				              public void onClick(DialogInterface dialog, int which) {
				            	  
				            	SLNO=Integer.valueOf(o.get("Rnd"));				            	  
				              	String SQL="";            	         	
				            	SQL ="Delete from VitalSign where ";				            	
				 			  	SQL+="PatientID='"+ PatientID +"'";
				 			  	SQL+="and VDate='"+ g.CurrentDateYYYYMMDD +"'";	
				 			  	SQL+="and Round='"+ SLNO +"'";		            	
				  		  		C.Save(SQL);  		  		
				   		  		DataRetrieve();				        	        				        	        
				              }});
				          adb.show();
		            }
		        });
	       
	        return convertView;
				
		}

    } 
  
    */
    public void FindLocation() { 
        LocationManager locationManager = (LocationManager) this 
                .getSystemService(Context.LOCATION_SERVICE); 
 
        LocationListener locationListener = new LocationListener() { 
            public void onLocationChanged(Location location) { 
                updateLocation(location);                  
                } 
 
            public void onStatusChanged(String provider, int status, 
                    Bundle extras) { 
            } 
 
            public void onProviderEnabled(String provider) { 
            } 
 
            public void onProviderDisabled(String provider) { 
            } 
        }; 
        locationManager.requestLocationUpdates( 
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    
    void updateLocation(Location location) { 
        currentLocation = location; 
        currentLatitude = currentLocation.getLatitude(); 
        currentLongitude = currentLocation.getLongitude(); 
    }
    /*
    @Override
   
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.actions , menu);
    }  
        */
}