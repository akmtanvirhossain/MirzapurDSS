package data.mirzapurdss;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
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
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class EventList extends AppCompatActivity {
	SimpleAdapter eList;
	ArrayList<HashMap<String, String>> mylist   = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> evmylist = new ArrayList<HashMap<String, String>>();
	Connection C;

	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backmenu, menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) {    
    		case R.id.mnuBack:
    			finish();    			
    	}    
    	return false;
    }
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventlist);
        C = new Connection(this);
        
        	try
        	{ 
      		Cursor cur1 = C.ReadData("select sno,evtype,evdate,info1,info2,info3,ifnull(info4,'')info4 from Events where Vill||Bari||HH='"+ (Global.VillageCode+Global.BariCode+Global.HouseholdCode) +"' order by sno,evtype");
      		cur1.moveToFirst();
      		evmylist.clear();
    		
    		ListView evlist = (ListView)findViewById(R.id.lstEvent);  	
      		int i=0;
    	        while(!cur1.isAfterLast())
    	        {       
    	        	HashMap<String, String> map = new HashMap<String, String>();
    	        	//ListView evlist = (ListView)dialog.findViewById(R.id.lstEvent);
    	        	if(i==0)
    	        	{
    	        		View header = getLayoutInflater().inflate(R.layout.eventlistheading, null);
    	        		evlist.addHeaderView(header);	        		
    	        	}

    	        		map.put("sno", cur1.getString(cur1.getColumnIndex("Sno")));
    	        		map.put("evtype", cur1.getString(cur1.getColumnIndex("EvType")));
    	  				map.put("evdate", cur1.getString(cur1.getColumnIndex("EvDate")));
    	  				map.put("info1", cur1.getString(cur1.getColumnIndex("Info1")));
    					map.put("info2", cur1.getString(cur1.getColumnIndex("Info2")));
    					map.put("info3", cur1.getString(cur1.getColumnIndex("Info3")));
    					map.put("info4", cur1.getString(cur1.getColumnIndex("info4")));

    					evmylist.add(map);		  					
    					
    					
    					eList = new SimpleAdapter(EventList.this, evmylist, R.layout.eventlistrow,
    							new String[] {"sno"},
    							new int[] {R.id.e_sno});  
    					evlist.setAdapter(new EventListAdapter(this));
    	        		
    					i+=1;
    	        	cur1.moveToNext();
    	        }  		        
    	        cur1.close();

        	}
    	    catch(Exception  e)
    		{
    			Connection.MessageBox(EventList.this, e.getMessage());
    			return;
    		}	
        }        

    
    
    public class EventListAdapter extends BaseAdapter 
    {
        private Context context;

        public EventListAdapter(Context c){
            context = c;
        }
 
        public int getCount() {
            return eList.getCount();
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
				convertView = inflater.inflate(R.layout.eventlistrow, null); 
			}
			
		
			final HashMap<String, String> o = (HashMap<String, String>) eList.getItem(position);
			
			TextView e_sno=(TextView)convertView.findViewById(R.id.e_sno);
			TextView e_evtype=(TextView)convertView.findViewById(R.id.e_evtype);
			TextView e_evdate=(TextView)convertView.findViewById(R.id.e_evdate);
			TextView e_info1=(TextView)convertView.findViewById(R.id.e_info1);
			TextView e_info2=(TextView)convertView.findViewById(R.id.e_info2);
			TextView e_info3=(TextView)convertView.findViewById(R.id.e_info3);
			TextView e_info4=(TextView)convertView.findViewById(R.id.e_info4);
			
			e_sno.setText(o.get("sno").toString());
			e_evtype.setText(o.get("evtype").toString());
			e_evdate.setText(o.get("evdate").toString());
			e_info1.setText(o.get("info1").toString());
			e_info2.setText(o.get("info2").toString());
			e_info3.setText(o.get("info3").toString());
			e_info4.setText(o.get("info4").toString());
			
			
			//final AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
			
			/*			memtab.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	//ShowEventForm(o.get("vill"),o.get("bari"),o.get("hh"),o.get("sno"),o.get("pno"),o.get("name"));
         	
	            	}
	        });	     	
			*/
			
	        return convertView;
		}
				
	}
}