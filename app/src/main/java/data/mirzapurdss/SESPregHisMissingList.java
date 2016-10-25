package data.mirzapurdss;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SESPregHisMissingList extends Activity{
	Connection C;
	Global g;
	
	ArrayList<HashMap<String, String>> mylist;
	SimpleAdapter mSchedule;
	Bundle IDbundle;

	private static String CurrentVillage;
	private static String CurrentVCode;
	
	Location currentLocation; 
	double currentLatitude,currentLongitude; 

	Location currentLocationNet; 
	double currentLatitudeNet,currentLongitudeNet; 
	
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backbari, menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) {
	    	case R.id.mnuRefresh:
		        C.Save("delete from visits_temp");
	     	  	C.Save("insert into visits_temp(vill,bari,hh,rnd,rsno,vdate) select vill,bari,hh,rnd,rsno,vdate from visits where Rnd='"+ g.getRoundNumber() +"'");
	    		
	    		Spinner BariList = (Spinner)findViewById(R.id.BariList);
	    		//BariList.setSelection(2);
	    		BlockList(false, Global.Left(BariList.getSelectedItem().toString(),4));
	    		
	    		//Total household visited : 26 Nov 2013
		    	TextView lblTotalHH=(TextView)findViewById(R.id.lblTotalHH);
		    	TextView lblHHVisited=(TextView)findViewById(R.id.lblHHVisited);
		    		    	
		    	Cursor cur = C.ReadData("select count(h.vill)thh,count(v.vill)tvisit from Household h left outer join visits_temp v on h.vill||h.bari||h.hh=v.vill||v.bari||v.hh and v.rnd='"+ g.getRoundNumber() +"' where h.Clust='"+ g.getClusterCode() +"' and h.block='"+ g.getBlockCode() +"' and (length(h.Extype)=0 or h.extype not in('51','52','53','55'))");
		    	cur.moveToFirst();
		        while(!cur.isAfterLast())
		        {
			    	lblTotalHH.setText("Total HH: "+cur.getString(cur.getColumnIndex("thh")));
			    	lblHHVisited.setText("Visited HH: "+cur.getString(cur.getColumnIndex("tvisit")));
			    	cur.moveToNext();
		        }
		        cur.close();
		        
	    		return true;
    		case R.id.mnuNewBari:
    			try
    			{
	    			String CurrentBariNo="";
	        		String LastBariNo = C.ReturnSingleValue("select Bari from Baris where vill=='"+ (g.getVillageCode()) +"' order by Bari desc limit 1");
	        		if(!Global.Left(LastBariNo, 1).matches("[a-zA-z]{1}"))
	        		{
	        			CurrentBariNo = Global.Right("000" + String.valueOf((Integer.parseInt(LastBariNo)+1)),4);
	        		}
	        		else if(Global.Left(LastBariNo, 1).matches("[a-zA-z]{1}"))
	        		{
	        			CurrentBariNo = Global.Left(LastBariNo, 1) + Global.Right("000" + String.valueOf((Integer.parseInt(Global.Right(LastBariNo,3))+1)),3);
	        		}
	        		
	        		ShowBariForm(g.getVillageCode(),CurrentBariNo,"s");
	    			}
    			catch(Exception ex)
    			{
    				Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
    			}
        		return true;
    		case R.id.mnuBack:
    			finish();	
    			Intent f11 = new Intent(getApplicationContext(),BlockList.class);
     	    	startActivity(f11); 
    			return true;	    			
    	}    
    	return false;
    }
	
	
	//Disabled Back/Home key
	//--------------------------------------------------------------------------------------------------	
	@Override 
	public boolean onKeyDown(int iKeyCode, KeyEvent event)
	{
	    if(iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME) 
	         { return false; }
	    else { return true;  }
	}
	
	
	ListView list;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.householdindex);
        try
        {
	        C=new Connection(this);
	        g=Global.getInstance();
	        
	        FindLocation();
	        FindLocationNet();
	        
	        C.Save("delete from visits_temp");
     	  	C.Save("insert into visits_temp(vill,bari,hh,rnd,rsno,vdate) select distinct vill,bari,hh,rnd,rsno,vdate from visits where Rnd='"+ g.getRoundNumber() +"'");
     	  	
	        IDbundle	= getIntent().getExtras();
	        CurrentVillage = IDbundle.getString("village");
	        CurrentVCode = IDbundle.getString("vcode");
	        
	        TextView lblBlock=(TextView)findViewById(R.id.lblBlock);
	        lblBlock.setText(" Block: " + g.getBlockCode());        
	        TextView lblVillage=(TextView)findViewById(R.id.lblVillageName);
	    	lblVillage.setText("Village: "+ CurrentVCode + "," + CurrentVillage);        
	    	
	    	list = (ListView) findViewById(R.id.listHHIndex);
	    	
	    	//Total household visited : 26 Nov 2013
	    	TextView lblTotalHH=(TextView)findViewById(R.id.lblTotalHH);
	    	TextView lblHHVisited=(TextView)findViewById(R.id.lblHHVisited);
	    		    	
	    	Cursor cur = C.ReadData("select count(h.vill)thh,count(v.vill)tvisit from Household h left outer join visits_temp v on h.vill||h.bari||h.hh=v.vill||v.bari||v.hh and v.rnd='"+ g.getRoundNumber() +"' where h.Clust='"+ g.getClusterCode() +"' and h.block='"+ g.getBlockCode() +"' and (length(h.Extype)=0 or h.extype not in('51','52','53','55'))");
	    	cur.moveToFirst();
	        while(!cur.isAfterLast())
	        {
		    	lblTotalHH.setText("Total HH: "+cur.getString(cur.getColumnIndex("thh")));
		    	lblHHVisited.setText("Visited HH: "+cur.getString(cur.getColumnIndex("tvisit")));
		    	cur.moveToNext();
	        }
	        cur.close();
	        
	    	/*cur.moveToFirst();
	        while(!cur.isAfterLast())
	        {
	        	if(!C.Existence("select vill from tTrans where vill||bari||hh='"+ Household +"' and sno='"+ curphis.getString(curphis.getColumnIndex("sno")) +"' and status='p'") | !C.Existence("select vill from PregHis where pno='"+ curphis.getString(curphis.getColumnIndex("pno")) +"'"))
	        	{
	        		ErrMsg += "-> RHQ হয় নাই (সিরিয়াল নাম্বার= "+  cur.getString(cur.getColumnIndex("sno")) +" এবং নাম= "+ curphis.getString(curphis.getColumnIndex("name")) +" ).\n";
	        	}
	        	cur.moveToNext();
	        }
	        cur.close();*/
	    	
	    	final Spinner BariList = (Spinner)findViewById(R.id.BariList);	
	    	String SQL = "Select ' ' union Select ' All Bari' union select b.bari||', '||b.BariName from SES s inner join Baris b on s.vill||s.bari=b.vill||b.bari"; 
	    	SQL += " inner join mdssvill v on b.vill=v.vill where Visit<>'1' and v.cluster='"+ g.getClusterCode() +"' and b.block='"+ g.getBlockCode() +"'";
	    	BariList.setAdapter(C.getArrayAdapter(SQL));
	    	BariList.setSelection(2);

	    	Button cmdBariUpdate = (Button)findViewById(R.id.cmdBariUpdate);
	    	cmdBariUpdate.setOnClickListener(new View.OnClickListener() {
	    	    public void onClick(View arg0) {
	    	    	if(BariList.getSelectedItemPosition()==0) return;
	    	    	if(BariList.getSelectedItem().toString().trim().equalsIgnoreCase("all bari")) return;
	    	    	
	    	    	String CurrentBariNo = Global.Left(BariList.getSelectedItem().toString(),4);
	    	    	ShowBariForm(g.getVillageCode(),CurrentBariNo,"u");
	    	    }
	    	});
	    	
	    	BariList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    	    @Override
	    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	    	    	
	    	    	if(BariList.getSelectedItemPosition()==0)
	    	    	{
	    	    		
	    	    	}
	    	    	else if(BariList.getSelectedItem().toString().trim().equalsIgnoreCase("all bari"))
	    	    	{
	    	    		g.setBariCode("");
	    	    		BlockList(false, "");	    	    		
	    	    	}
	    	    	else
	    	    	{
	    	    		g.setBariCode(BariList.getSelectedItem().toString());
	    	    		BlockList(false, Global.Left(BariList.getSelectedItem().toString(),4));
	    	    	}
	    	    }

	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	    	
	    	    }

	    	});	   
        }
        catch(Exception ex)
        {
        	Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
        	return;
        }
        
	}
	
	public void BlockListUpdate()
	{
		BlockList(false,g.getBariCode());
	}

	public void BlockList(Boolean heading, String BariCode)
	{
	    final ListView list = (ListView) findViewById(R.id.listHHIndex);		 	
			mylist = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map;
			
			
			try
			{	
		        C.Save("delete from visits_temp");
		        C.Save("insert into visits_temp(vill,bari,hh,rnd,rsno,vdate) select distinct vill,bari,hh,rnd,rsno,vdate from visits where vill='"+ g.getVillageCode() +"' and (case when rsno='77' then '"+ g.getRoundNumber() +"' else Rnd end)='"+ g.getRoundNumber() +"'");
		        
				String BCode = ""; //BariCode.length()==0?"%":BariCode;
				String SQL = ""; 
				//BariCode = g.getBariCode().length()==0?BariCode:g.getBariCode();
				if(BariCode.length()!=0)
				{
					SQL +="select b.bari,ifnull(h.hh,'')as hh,ifnull(h.hhhead,'')hhhead,count(m.vill)totalMem,vl.vill,b.bariname,(case when v.rnd is null then '2' else '1' end)RoundVisit,";
					SQL +=" ifnull(h.rel,'')rel,ifnull(v.rsno,'')rsno,ifnull(v.vdate,'')vdate,count(case when m.posmig='54' then '1' else null end)posmig from ";
					SQL +=" Baris b";
					SQL +=" left outer join Household h on b.vill||b.bari=h.vill||h.bari";
					SQL +=" left outer join Member m on h.vill||h.bari||h.hh=m.Vill||m.Bari||m.hh and length(m.extype)=0";
					SQL +=" left outer join visits_temp v on h.vill||h.bari||h.hh=v.Vill||v.Bari||v.hh";
					SQL +=" left outer join SES s on h.vill||h.bari||h.hh=s.Vill||s.Bari||s.hh";
					SQL +=" inner join mdssvill vl on (b.vill=vl.vill)";
					SQL +=" where vl.Cluster='"+ g.getClusterCode() +"' and";
					SQL +=" b.block='"+ g.getBlockCode() +"' and b.bari ='"+ BariCode +"' and s.visit<>'1'";
					SQL +=" group by h.vill,h.bari,h.hh";
					SQL +=" order by h.vill, h.Bari, h.HH";					
				}
				else
				{
					SQL +="select b.bari,ifnull(h.hh,'')as hh,ifnull(h.hhhead,'')hhhead,count(m.vill)totalMem,vl.vill,b.bariname,(case when v.rnd is null then '2' else '1' end)RoundVisit,";
					SQL +=" ifnull(h.rel,'')rel,ifnull(v.rsno,'')rsno,ifnull(v.vdate,'')vdate,count(case when m.posmig='54' then '1' else null end)posmig from ";
					SQL +=" Baris b";
					SQL +=" left outer join Household h on b.vill||b.bari=h.vill||h.bari";
					SQL +=" left outer join Member m on h.vill||h.bari||h.hh=m.Vill||m.Bari||m.hh and length(m.extype)=0";
					SQL +=" left outer join visits_temp v on h.vill||h.bari||h.hh=v.Vill||v.Bari||v.hh";
					SQL +=" left outer join SES s on h.vill||h.bari||h.hh=s.Vill||s.Bari||s.hh";
					SQL +=" inner join mdssvill vl on (b.vill=vl.vill)";
					SQL +=" where vl.Cluster='"+ g.getClusterCode() +"' and";
					SQL +=" b.block='"+ g.getBlockCode() +"' and s.visit<>'1'";
					SQL +=" group by h.vill,h.bari,h.hh";
					SQL +=" order by h.vill, h.Bari, h.HH";

				}
				Cursor cur=C.ReadData(SQL);
				
				cur.moveToFirst();
				if(heading==true)
				{
					View header = getLayoutInflater().inflate(R.layout.householdindexheading, null);
					list.addHeaderView(header);
				}
				
				while(!cur.isAfterLast())
				{				
					map = new HashMap<String, String>();
					map.put("bari", cur.getString(0));
					map.put("hhno",cur.getString(1));
					map.put("hhhead", cur.getString(2));
					map.put("totalmem", cur.getString(3));
					map.put("vcode", cur.getString(4));
					map.put("bariname", cur.getString(5));
					map.put("visit", cur.getString(6));
					map.put("rel", cur.getString(7));
					map.put("rsno", cur.getString(8));
					map.put("vdate", cur.getString(9));
					map.put("posmig", cur.getString(10));
					
					mylist.add(map);

					cur.moveToNext();
				}
				cur.close();
				mSchedule = new SimpleAdapter(this, mylist, R.layout.householdindexrow,
				            new String[] {"bari","hhno", "hhhead"}, 
				            new int[] {R.id.Bari, R.id.HHNo, R.id.HHHead});
				
				list.setAdapter(new HHListAdapter(this));	

			}
			catch(Exception e)
			{
			     AlertDialog.Builder adb=new AlertDialog.Builder(SESPregHisMissingList.this);
		         adb.setTitle("Message");
		         adb.setMessage(e.getMessage());
		         adb.setPositiveButton("Ok", null);
		         adb.show();
			}
				
	}
	
	
	public class HHListAdapter extends BaseAdapter 
    {
        private Context context;

        public HHListAdapter(Context c){
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
				convertView = inflater.inflate(R.layout.householdindexrow, null); 
			}
			
			
			TextView Bari= (TextView) convertView.findViewById(R.id.Bari);
			TextView HHNo= (TextView) convertView.findViewById(R.id.HHNo);
			TextView HHHead= (TextView) convertView.findViewById(R.id.HHHead);
			TextView lblVillage= (TextView) convertView.findViewById(R.id.lblVillageName);
			TextView BariN= (TextView) convertView.findViewById(R.id.BariN);
			
			
			final HashMap<String, String> o = (HashMap<String, String>) mSchedule.getItem(position);
			
			Bari.setText(o.get("bari"));
			HHNo.setText(o.get("hhno"));
			HHHead.setText(o.get("hhhead"));
			BariN.setText(o.get("bariname"));
			Global.VillageCode = o.get("vcode").toString();
			final String VD = Global.DateConvertDMY(o.get("vdate").toString());
			
			if(o.get("visit").equals("1") & o.get("rsno").equals("99"))
			{
				Bari.setTextColor(Color.RED);
				BariN.setTextColor(Color.BLACK);
				HHNo.setTextColor(Color.BLACK);
				HHHead.setTextColor(Color.BLACK);								
			}
			//else if(o.get("visit").equals("1") & o.get("rsno").equals("77"))
			else if(Integer.valueOf(o.get("totalmem"))==0)
			{
				Bari.setTextColor(Color.LTGRAY);				
				BariN.setTextColor(Color.LTGRAY);
				HHNo.setTextColor(Color.LTGRAY);
				HHHead.setTextColor(Color.LTGRAY);
			}			
			else if(o.get("posmig").equals("1"))
			{
				Bari.setTextColor(Color.BLUE);				
				BariN.setTextColor(Color.BLUE);
				HHNo.setTextColor(Color.BLUE);
				HHHead.setTextColor(Color.BLUE);				
			}
			else if(o.get("visit").equals("2"))
			{				
				Bari.setTextColor(Color.BLACK);
				BariN.setTextColor(Color.BLACK);
				HHNo.setTextColor(Color.BLACK);
				HHHead.setTextColor(Color.BLACK);				
			}						
			else if(o.get("visit").equals("1"))
			{
				Bari.setTextColor(Color.GREEN);
				BariN.setTextColor(Color.BLACK);
				HHNo.setTextColor(Color.BLACK);
				HHHead.setTextColor(Color.BLACK);				
			}			

						
			Bari.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	            		g.setVillageCode(o.get("vcode").toString());
	            		g.setBariCode(o.get("bari").toString());
	            		if(o.get("hhno").length()==0)
	            		{
	            			Connection.MessageBox(SESPregHisMissingList.this, "এ বাড়ীতে কোন খানা নেই।");
	            			return;
	            		}
	            		g.setHouseholdNo(o.get("hhno").toString());
	            		ShowVisitForm(o.get("vcode"),o.get("bari"),o.get("hhno"),g.getRoundNumber(),o.get("bariname"),o.get("hhhead"),"o",o.get("rel"),VD,o.get("rsno"));
	            	}
	        });	        	        
			BariN.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            		g.setVillageCode(o.get("vcode").toString());
	            		g.setBariCode(o.get("bari").toString());
	            		if(o.get("hhno").length()==0)
	            		{
	            			Connection.MessageBox(SESPregHisMissingList.this, "এ বাড়ীতে কোন খানা নেই।");
	            			return;
	            		}
	            		
	            		g.setHouseholdNo(o.get("hhno").toString());	            		
	            		ShowVisitForm(o.get("vcode"),o.get("bari"),o.get("hhno"),g.getRoundNumber(),o.get("bariname"),o.get("hhhead"),"o",o.get("rel"),VD,o.get("rsno"));
	            	}
	        });	
			HHNo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            		g.setVillageCode(o.get("vcode").toString());
	            		g.setBariCode(o.get("bari").toString());
	            		if(o.get("hhno").length()==0)
	            		{
	            			Connection.MessageBox(SESPregHisMissingList.this, "এ বাড়ীতে কোন খানা নেই।");
	            			return;
	            		}
	            		
	            		g.setHouseholdNo(o.get("hhno").toString());	            		
	            		ShowVisitForm(o.get("vcode"),o.get("bari"),o.get("hhno"),g.getRoundNumber(),o.get("bariname"),o.get("hhhead"),"o",o.get("rel"),VD,o.get("rsno"));
	            	}
	        });	
			HHHead.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            		g.setVillageCode(o.get("vcode").toString());
	            		g.setBariCode(o.get("bari").toString());
	            		if(o.get("hhno").length()==0)
	            		{
	            			Connection.MessageBox(SESPregHisMissingList.this, "এ বাড়ীতে কোন খানা নেই।");
	            			return;
	            		}
	            		
	            		g.setHouseholdNo(o.get("hhno").toString());	            		
	            		ShowVisitForm(o.get("vcode"),o.get("bari"),o.get("hhno"),g.getRoundNumber(),o.get("bariname"),o.get("hhhead"),"o",o.get("rel"),VD,o.get("rsno"));
	            	}
	        });	
			Button cmdNewHH = (Button)convertView.findViewById(R.id.cmdNewHH);
			cmdNewHH.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            		g.setVillageCode(o.get("vcode").toString());
	            		g.setBariCode(o.get("bari").toString());
	            		String TotalHH = C.ReturnSingleValue("select ifnull(max(cast(hh as int)),0) from Member where vill||bari='"+ o.get("vcode")+o.get("bari") +"'");
	            		if(TotalHH.equals("99"))
	            		{
	            			Connection.MessageBox(SESPregHisMissingList.this, "এ বাড়ীতে 99 এর বেশী খানা হবে না।");
	            			return;
	            		}
	            		String HNo = Global.Right("000"+C.ReturnSingleValue("select (ifnull(max(cast(hh as int)),0)+1)MaxSl from Member where vill||Bari='"+ o.get("vcode")+o.get("bari") +"'"),2);
	            		ShowVisitForm(o.get("vcode"),o.get("bari"),HNo,g.getRoundNumber(),o.get("bariname"),o.get("hhhead"),"n","0","","");
	            	}
	        });	
			

	        return convertView;
		}				
	}
	
	
	private void ShowVisitForm(final String Vill, final String Bari,final String HH, final String Rnd, final String BName, final String HHead,final String OldNewHH, final String Religion,final String LastVDate,final String RsNo)
    {
    	final Dialog dialog = new Dialog(SESPregHisMissingList.this);    	
    	String SQL="";
    	g.setRsNo("");
    	
    	if(OldNewHH.equalsIgnoreCase("o"))
    	{
    		dialog.setTitle("Visit Form [ Old Household ]");
    		
    		String R = RsNo.length() == 0?"0":RsNo;
    		if(Integer.valueOf(R)>=1 & Integer.valueOf(R)<=76)
    		{
            	SQL  =" Select ' ' union";
            	SQL +=" Select '77-Entire household migrated-out' union";
            	SQL +=" Select (SNo||'-'||Name)  from Member where Vill||Bari||Hh='"+ (Vill+Bari+HH) +"' and ((julianday(date('now'))-julianday(bdate))/365.25)>10 and (extype is null or length(extype)=0)";        		    			
    		}
    		else
    		{
	        	SQL  =" Select ' ' union";
	        	SQL +=" Select '00-No Visit due to unavoidable situation' union";
	        	SQL +=" Select '77-Entire household migrated-out' union";
	        	SQL +=" Select '88-Refused to interview' union";
	        	SQL +=" Select '99-All adult members absent' union";
	        	SQL +=" Select (SNo||'-'||Name)  from Member where Vill||Bari||Hh='"+ (Vill+Bari+HH) +"' and ((julianday(date('now'))-julianday(bdate))/365.25)>10 and (extype is null or length(extype)=0)";
    		}
    	}
    	else if(OldNewHH.equalsIgnoreCase("n"))
    	{
    		dialog.setTitle("Visit Form [ New Household ]");
            //respondent for the new household
        	SQL  =" Select ' ' union";
        	SQL +=" Select '01-Member 1' union";
        	SQL +=" Select '02-Member 2' union";
        	SQL +=" Select '03-Member 3' union";
        	SQL +=" Select '04-Member 4' union";
        	SQL +=" Select '05-Member 5' union";
        	SQL +=" Select '06-Member 6'";        	        		
    	}
		//dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialog.setContentView(R.layout.visit);
    	dialog.setCancelable(true);
    	dialog.setCanceledOnTouchOutside(false);
    	
    	final EditText VisitNote = (EditText)dialog.findViewById(R.id.VisitNote);
    	VisitNote.setText(C.ReturnSingleValue("Select Note from Visits where vill||bari||hh='"+ (Vill+Bari+HH) +"' and Rnd='"+ Rnd +"'"));
    	
    	final Spinner BariList = (Spinner)findViewById(R.id.BariList);
    	final EditText txtHH = (EditText)dialog.findViewById(R.id.txtHH);
    	txtHH.setText(Vill+"-"+Bari+"-"+HH);	
    	
    	final EditText txtRnd = (EditText)dialog.findViewById(R.id.txtRnd);
    		txtRnd.setText(Rnd);
    	final EditText VisitDate = (EditText)dialog.findViewById(R.id.VisitDate);
    	if(LastVDate.length()==0)
    		VisitDate.setText(g.CurrentDateDMY);
    	else
    		VisitDate.setText(LastVDate);
    	
    	final Spinner spnResp = (Spinner)dialog.findViewById(R.id.spnResp);
    	final Spinner spnRel  = (Spinner)dialog.findViewById(R.id.spnRel);
        ArrayAdapter adptrRel = ArrayAdapter.createFromResource(this, R.array.listReligion, android.R.layout.simple_spinner_item);
        adptrRel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRel.setAdapter(adptrRel);

        
    	spnResp.setAdapter(C.getArrayAdapter(SQL));
    	
        for(int i=1;i<spnResp.getCount();i++)
        {
        	if(RsNo.equals(Global.Left(spnResp.getItemAtPosition(i).toString(),2)))
        	{
        		spnResp.setSelection(i);
        		i=spnResp.getCount();
        	}
        }
    	
        
        spnRel.setSelection(Integer.parseInt(Religion.length()==0?"0":Religion));

    	
    	
    	Button cmdVisitSave = (Button)dialog.findViewById(R.id.cmdVisitSave);
    	cmdVisitSave.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View arg0) {
    	    	try
    	    	{
        		AlertDialog.Builder adb = new AlertDialog.Builder(SESPregHisMissingList.this);
        				
        				
        		
		            	if(txtRnd.getText().length()==0)
		            	{
		            		Connection.MessageBox(SESPregHisMissingList.this, "সঠিক রউন্ড নাম্বার সিলেক্ট করুন।");
		            		return;
		            	}
		            	else if(spnRel.getSelectedItemPosition()==0)
		            	{
		            		Connection.MessageBox(SESPregHisMissingList.this, "সঠিক ধর্ম সিলেক্ট করুন।");
		            		return;
		            	}
		            	
		            	//visit date
		            	String VD = Global.DateValidate(VisitDate.getText().toString());		            	
		            	if(VD.length()!=0)
		            	{
		            		Connection.MessageBox(SESPregHisMissingList.this, VD);
		            		return;
		            	}
		            	
		            	else if(spnResp.getSelectedItemPosition()==0)
		            	{
		            		Connection.MessageBox(SESPregHisMissingList.this, "তালিকা থেকে সঠিক  respondent সিলেক্ট করুন।");
		            		return;
		            	}
		            			            			            	
		            	
		            	//For old household
		            	else if(OldNewHH.equalsIgnoreCase("o"))
		            	{
		            		final int Resp = Integer.parseInt(Global.Left(spnResp.getSelectedItem().toString(),2));
		            		g.setRsNo(Global.Left(spnResp.getSelectedItem().toString(),2));
		            		
		            		if(Resp >= 1 & Resp <= 76)
		            		{
		                		  adb.setTitle("Household Visit");
		        		          adb.setMessage("এই খানায় কি কোন ধরনের ইভেন্ট পরিবর্তন হয়েছে[Yes/No]?");
		        		          
		        		          //have no events
		        		          //-----------------------------------------------------------------
		        		          adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
		        		              public void onClick(DialogInterface dialog1, int which) {		  
			        		            	   //save visit then close
			        		            	   String SQL = "";
			        		            	   try
			        		            	   {
				        			            	if(!C.Existence("Select * from Visits where vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'"))
				        			            	{
				        				            	SQL = "Insert into Visits(Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon,LatNet, LonNet,Upload,Note)Values(";
				        				            	SQL += "'"+ Vill +"',";
				        				            	SQL += "'"+ Bari +"',";
				        				            	SQL += "'"+ HH +"',";
				        				            	SQL += "'"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
				        				            	SQL += "'"+ g.getUserId() +"',"; //DC code
				        				            	SQL += "'"+ Global.DateTimeNow() +"',"; //Enter Date
				        				            	SQL += "'"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
				        				            	SQL += "'"+ Rnd +"',"; //round
				        				            	SQL += "'"+ Double.toString(currentLatitude) +"',";    //lat
				        				            	SQL += "'"+ Double.toString(currentLongitude) +"',";   //lon
				        						  		SQL += "'"+ Double.toString(currentLatitudeNet) +"',"; //latnet
				        						  		SQL += "'"+ Double.toString(currentLongitudeNet) +"','2',";//lonnet
				        						  		SQL += "'"+ VisitNote.getText().toString() +"')";
				        			            	}
				        			            	else
				        			            	{
				        				            	SQL = "Update Visits set upload='2',";
				        				            	SQL += " Rsno='"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";				        				            	
				        				            	SQL += " VDate='"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
				        				            	SQL += " Note='"+ VisitNote.getText().toString() +"',";
				        				            	SQL += " Dma='"+ g.getUserId() +"'"; //DC code
				        				            	SQL += " where vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'";
				        			            	}
				        			            	C.Save(SQL);
				        			            	C.Save("Update Household set upload='2', Rel='"+ Global.Left(spnRel.getSelectedItem().toString(),1) +"' where vill||bari||hh='"+ (Vill+Bari+HH) +"'");
				        			            	
				        			            	BlockList(false, Global.Left(BariList.getSelectedItem().toString(),4));
			        		            	   }
			        		            	   catch(Exception ex)
			        		            	   {
			        		            		   Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
			        		            		   return;
			        		            	   }
			        			            	dialog1.cancel();
			        			            	dialog.cancel();		        		            		  	        		            		  
		        		              }});

		        		          //have events
		        		          //-----------------------------------------------------------------		        		          
		        		          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
		        		              public void onClick(DialogInterface dialog1, int which) {
		        			            	//transfer data for events
		        			            	DataForEvents(Vill+Bari+HH,Rnd,OldNewHH);

		        		            	    String SQL = "";
		        			            	//save visit then continue
		        			            	if(!C.Existence("Select * from tTrans where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'"))
		        			            	{
		        				            	SQL = "Insert into tTrans(Status,Vill, Bari, Hh, Resp, Dma, EnterDt, Vdate, Rnd,Lat,Lon,Latnet,Lonnet,upload,Note)Values(";
		        				            	SQL += "'v',";
		        				            	SQL += "'"+ Vill +"',";
		        				            	SQL += "'"+ Bari +"',";
		        				            	SQL += "'"+ HH +"',";
		        				            	SQL += "'"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
		        				            	SQL += "'"+ g.getUserId() +"',"; //DC code
		        				            	SQL += "'"+ Global.DateTimeNow() +"',"; //Enter date
		        				            	SQL += "'"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
		        				            	SQL += "'"+ Rnd +"',"; //round	
		        				            	SQL += "'"+ Double.toString(currentLatitude) +"',";    //lat
		        				            	SQL += "'"+ Double.toString(currentLongitude) +"',";   //lon
		        						  		SQL += "'"+ Double.toString(currentLatitudeNet) +"',"; //latnet
		        						  		SQL += "'"+ Double.toString(currentLongitudeNet) +"','2',";//lonnet		
		        						  		SQL += "'"+ VisitNote.getText().toString() +"')";
		        				            	
		        			            	}
		        			            	else
		        			            	{
		        				            	SQL = "Update tTrans set upload='2',";
		        				            	SQL += " Resp='"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
		        				            	SQL += " VDate='"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
		        				            	SQL += " Note='"+ VisitNote.getText().toString() +"',";
		        				            	SQL += " Dma='"+ g.getUserId() +"'"; //DC code
		        				            	SQL += " where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'";
		        			            	}
		        			            	C.Save(SQL);
		        			            	C.Save("Update tTrans set upload='2', Rel='"+ Global.Left(spnRel.getSelectedItem().toString(),1) +"' where status='h' and vill||bari||hh='"+ (Vill+Bari+HH) +"'");
		        			            	
		        			            	//finish();
		        			            	dialog1.cancel();
		        			            	dialog.cancel();
		        			            	
		        			            	//transfer data for events
		        			            	//DataForEvents(Vill+Bari+HH,Rnd,OldNewHH);
		        			        	  	
		        			            	//call member event form
		        			            	Bundle IDbundle = new Bundle();
		        			        	  	IDbundle.putString("vill", Vill);
		        			        	  	IDbundle.putString("villname", CurrentVillage);
		        			        	  	IDbundle.putString("bari", Bari);	
		        			        	  	IDbundle.putString("bariname", BName);	
		        			        	  	IDbundle.putString("hhno", HH);
		        			        	  	IDbundle.putString("hhhead", HHead);	
		        			        	  	IDbundle.putString("totalmember", "0");
		        			        	  	IDbundle.putString("vdate", VisitDate.getText().toString());
		        			            	Intent f11 = new Intent(getApplicationContext(),MemberEvents.class);
		        			            	f11.putExtras(IDbundle);
		        			            	startActivity(f11); 
		        			              		        		            	  
		        		              }});
		        		          
		        		          adb.show();		            			
		            		}
		            		else if(Resp == 77)
		            		{
      			            	//transfer data for events
      			            	DataForEvents(Vill+Bari+HH,Rnd,OldNewHH);
      			            	
	      		            	  String SQL = "";
	      			            	//save visit then continue
	      			            	if(!C.Existence("Select * from tTrans where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'"))
	      			            	{
	      				            	SQL = "Insert into tTrans(Status,Vill, Bari, Hh, Resp, Dma, EnterDt, Vdate, Rnd,Lat,Lon,LatNet,LonNet,Upload,Note)Values(";
	      				            	SQL += "'v',";
	      				            	SQL += "'"+ Vill +"',";
	      				            	SQL += "'"+ Bari +"',";
	      				            	SQL += "'"+ HH +"',";
	      				            	SQL += "'"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
	      				            	SQL += "'"+ g.getUserId() +"',"; //DC code
	      				            	SQL += "'"+ Global.DateTimeNow() +"',"; //Enter date
	      				            	SQL += "'"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
	      				            	SQL += "'"+ Rnd +"',"; //round	
        				            	SQL += "'"+ Double.toString(currentLatitude) +"',";    //lat
        				            	SQL += "'"+ Double.toString(currentLongitude) +"',";   //lon
        						  		SQL += "'"+ Double.toString(currentLatitudeNet) +"',"; //latnet
        						  		SQL += "'"+ Double.toString(currentLongitudeNet) +"','2',";//lonnet		
        						  		SQL += "'"+ VisitNote.getText().toString() +"')";
	      				            	
	      			            	}
	      			            	else
	      			            	{
	      				            	SQL = "Update tTrans set upload='2',";
	      				            	SQL += " Resp='"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
	      				            	SQL += " VDate='"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
	      				            	SQL += " Note='"+ VisitNote.getText().toString() +"',";
	      				            	SQL += " Dma='"+ g.getUserId() +"'"; //DC code
	      				            	SQL += " where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'";
	      			            	}
	      			            	C.Save(SQL);
	      			            	C.Save("Update tTrans set upload='2',Rel='"+ Global.Left(spnRel.getSelectedItem().toString(),1) +"' where status='h' and vill||bari||hh='"+ (Vill+Bari+HH) +"'");
	      			            	
	      			            	dialog.cancel();
	      			            	
	      			            	//transfer data for events
	      			            	//DataForEvents(Vill+Bari+HH,Rnd,OldNewHH);
	      			        	  	
	      			            	//call member event form
	      			            	Bundle IDbundle = new Bundle();
	      			        	  	IDbundle.putString("vill", Vill);
	      			        	  	IDbundle.putString("villname", CurrentVillage);
	      			        	  	IDbundle.putString("bari", Bari);	
	      			        	  	IDbundle.putString("bariname", BName);	
	      			        	  	IDbundle.putString("hhno", HH);
	      			        	  	IDbundle.putString("hhhead", HHead);	
	      			        	  	IDbundle.putString("totalmember", "0");
	      			        	  	IDbundle.putString("vdate", VisitDate.getText().toString());
	      			            	Intent f11 = new Intent(getApplicationContext(),MemberEvents.class);
	      			            	f11.putExtras(IDbundle);
	      			            	startActivity(f11); 
		            			
		            		}
		            		else if(Resp == 0 | Resp == 88 | Resp == 99)
		            		{
	     		            	   //save visit then cancel
	     		            	   String SQL = "";
	     		            	   try
	     		            	   {
		        			            	if(!C.Existence("Select * from Visits where vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'"))
		        			            	{
		        				            	SQL = "Insert into Visits(Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon,LatNet,LonNet,upload,Note)Values(";
		        				            	SQL += "'"+ Vill +"',";
		        				            	SQL += "'"+ Bari +"',";
		        				            	SQL += "'"+ HH +"',";
		        				            	SQL += "'"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
		        				            	SQL += "'"+ g.getUserId() +"',"; //DC code
		        				            	SQL += "'"+ Global.DateTimeNow() +"',"; //Enter Date
		        				            	SQL += "'"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
		        				            	SQL += "'"+ Rnd +"',"; //round
		        				            	SQL += "'"+ Double.toString(currentLatitude) +"',";    //lat
		        				            	SQL += "'"+ Double.toString(currentLongitude) +"',";   //lon
		        						  		SQL += "'"+ Double.toString(currentLatitudeNet) +"',"; //latnet
		        						  		SQL += "'"+ Double.toString(currentLongitudeNet) +"','2',";//lonnet	
		        						  		SQL += "'"+ VisitNote.getText().toString() +"')";
		        			            	}
		        			            	else
		        			            	{
		        				            	SQL = "Update Visits set upload='2',";
		        				            	SQL += " Rsno='"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";				        				            	
		        				            	SQL += " VDate='"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
		        				            	SQL += " Note='"+ VisitNote.getText().toString() +"',";
		        				            	SQL += " Dma='"+ g.getUserId() +"'"; //DC code
		        				            	SQL += " where vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'";
		        			            	}
		        			            	C.Save(SQL);
		        			            	C.Save("Update Household set upload='2',Rel='"+ Global.Left(spnRel.getSelectedItem().toString(),1) +"' where vill||bari||hh='"+ (Vill+Bari+HH) +"'");
		        			            	
		        			            	BlockList(false, Global.Left(BariList.getSelectedItem().toString(),4));  	
	     		            	   }
	     		            	   catch(Exception ex)
	     		            	   {
	     		            		   Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
	     		            		   return;
	     		            	   }
	     			            	dialog.cancel();		            			
		            		}
		            		
		            		
	            		
		            	}

		            	
		            	//For new household
		            	else if(OldNewHH.equalsIgnoreCase("n"))
		            	{
		            		final int Resp = Integer.parseInt(Global.Left(spnResp.getSelectedItem().toString(),2));
		            		
		            		if(Resp >= 1 & Resp <= 76)
		            		{

    		            	  String SQL = "";
    		            	  try
    		            	  {
    		            		C.Save("delete from tTrans where Vill||Bari||HH = '"+ Vill+Bari+HH +"'");
    		            		  
    			            	//save visit then continue
    			            	if(!C.Existence("Select * from tTrans where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'"))
    			            	{
    				            	SQL = "Insert into tTrans(Status,Vill, Bari, Hh, resp, Dma, EnterDt, Vdate, Rnd,Lat,Lon,LatNet,LonNet,upload,Note)Values(";
    				            	SQL += "'v',";
    				            	SQL += "'"+ Vill +"',";
    				            	SQL += "'"+ Bari +"',";
    				            	SQL += "'"+ HH +"',";
    				            	SQL += "'"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
    				            	SQL += "'"+ g.getUserId() +"',"; //DC code
    				            	SQL += "'"+ Global.DateTimeNow() +"',"; //enter date
    				            	SQL += "'"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
    				            	SQL += "'"+ Rnd +"',"; //round
    				            	SQL += "'"+ Double.toString(currentLatitude) +"',";    //lat
    				            	SQL += "'"+ Double.toString(currentLongitude) +"',";   //lon
    						  		SQL += "'"+ Double.toString(currentLatitudeNet) +"',"; //latnet
    						  		SQL += "'"+ Double.toString(currentLongitudeNet) +"','2',";//lonnet		
    						  		SQL += "'"+ VisitNote.getText().toString() +"')";
    			            	}
    			            	else
    			            	{
    				            	SQL = "Update tTrans set upload='2',";
    				            	SQL += " Resp='"+ Global.Left(spnResp.getSelectedItem().toString(),2) +"',";
    				            	SQL += " VDate='"+ Global.DateConvertYMD(VisitDate.getText().toString()) +"',"; //date of visit
    				            	SQL += " Note='"+ VisitNote.getText().toString() +"',";
    				            	SQL += " Dma='"+ g.getUserId() +"'"; //DC code
    				            	SQL += " where status='v' and vill||bari||hh='"+ Vill+Bari+HH +"' and Rnd='"+ Rnd +"'";
    			            	}
    			            	C.Save(SQL);
    			            	
    			                String SQLSTR="";
    			                //------------------------------------------------------------------
    			                SQLSTR = "Insert into tTrans";
    			                SQLSTR += "(Status,Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, EnterDt,Rnd,upload,ContactNo)Values(";
    			                SQLSTR += "'h',";
    			                SQLSTR += "'"+ Vill +"',";
    			                SQLSTR += "'"+ Bari +"',";
    			                SQLSTR += "'"+ HH +"',";
    			                SQLSTR += "'',";
    			                SQLSTR += "'',"; //EnType
    			                SQLSTR += "'',";
    			                SQLSTR += "'',"; //ExType
    			                SQLSTR += "'',";
    			                SQLSTR += "'"+ Global.Left(spnRel.getSelectedItem().toString(),1) +"',"; //Religion
    			                SQLSTR += "'',"; //HH Head
    			                SQLSTR += "'"+ g.getClusterCode() +"',";   //Cluster
    			                SQLSTR += "'"+ g.getBlockCode() +"',";     //Block
    			                SQLSTR += "'"+ Global.DateTimeNow() +"',"; //Enter Date
    			                SQLSTR += "'"+ g.getRoundNumber() +"','2','')";   //Round number

    			                	C.Save(SQLSTR);
    			                //------------------------------------------------------------------   
    			                	
    			                g.setHouseholdNo(HH);
    		            	  }
    		            	   catch(Exception ex)
    		            	   {
    		            		   Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
    		            		   return;
    		            	   }
    		            	  
    		            	  //finish();
    		            	  dialog.dismiss();	
    		            	  
    		            	//transfer data for events
  			            	DataForEvents(Vill+Bari+HH,Rnd,OldNewHH);
  			        	  	
  			            	//call member event form
  			            	Bundle IDbundle = new Bundle();
  			        	  	IDbundle.putString("vill", Vill);
  			        	  	IDbundle.putString("villname", CurrentVillage);
  			        	  	IDbundle.putString("bari", Bari);	
  			        	  	IDbundle.putString("bariname", BName);	
  			        	  	IDbundle.putString("hhno", HH);
  			        	  	//IDbundle.putString("hhhead", HHead);
  			        	  	IDbundle.putString("hhhead", HHead);
  			        	  	IDbundle.putString("totalmember", "0");
  			        	  	IDbundle.putString("vdate", VisitDate.getText().toString());
  			        	  	
  			        	  	g.setMigVillage("");
  			        	  
  			            	Intent f11 = new Intent(getApplicationContext(),MemberEvents.class);
  			            	f11.putExtras(IDbundle);
  			            	startActivity(f11);     		            	  
		            		}
		            	}

	    	    	
    	    }
    	    catch(Exception ex)
    	    {
    	    	Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
    	    	return;
    	    }
    	    }
    	});    	
    	
    	Button cmdVisitClose = (Button)dialog.findViewById(R.id.cmdVisitClose);
    	cmdVisitClose.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View arg0) {
    	        // TODO Auto-generated method stub
    	        dialog.cancel();
    	    }
    	});
		
    	dialog.show();
    }
    
	
	
	private void DataForEvents(String VillBariHH,String Rnd,String OldNewHH)
	{
				String SQL="";
				
				if(OldNewHH.equals("o"))
					C.Save("delete from tTrans where Vill||Bari||HH = '"+ VillBariHH +"'");

				
				//-- -Household Information-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				SQL = "Insert into tTrans";
				SQL += " (Status,Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, Note, ContactNo)";
				SQL += " Select 'h',Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, Note, ContactNo";
				SQL += " from Household where Vill||Bari||HH = '"+ VillBariHH +"'";
				
				C.Save(SQL);
				
				//-- -Member Information-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				SQL = " Insert into tTrans";
				SQL += " (Status,Vill,bari,Hh, Pno,Sno,Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4,"; 
				SQL += " Ocp,EnType,EnDate, ExType,ExDate,PosMig,PosMigDate) Select 'm',Vill,bari,Hh, Pno,Sno,Name, Rth, Sex, BDate, Age, Mono,"; 
				SQL += " Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp,EnType,EnDate,ExType,ExDate,PosMig,PosMigDate";
				SQL += " from Member where Vill||Bari||HH = '"+ VillBariHH +"'";
				C.Save(SQL);
				
				//-- -SES Information-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				SQL = " Insert into tTrans";
				SQL += " (Status,Vill,bari,Hh, Sno,SesNo,Visit,Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d,"; 
				SQL += " Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q,"; 
				SQL += " Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d,"; 
				SQL += " Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a,"; 
				SQL += " Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y,"; 
				SQL += " Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e,"; 
				SQL += " Q030f, Q030g, Q030h, Q030z, Q031,vDate,Rnd)";
				SQL += " Select 's',Vill,bari,Hh, 0,SESNo,Visit,Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d,"; 
				SQL += " Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q,"; 
				SQL += " Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d,"; 
				SQL += " Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a,"; 
				SQL += " Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y,"; 
				SQL += " Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e,"; 
				SQL += " Q030f, Q030g, Q030h, Q030z, Q031,VDate,Rnd from SES where Vill||Bari||HH = '"+ VillBariHH +"'";
				C.Save(SQL);
				
				//-- -Preg. History-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				SQL = " Insert into tTrans";
				SQL += " (Status,Vill,bari,Hh,Pno,Sno,Visit,MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut,"; 
				SQL += " Died, SDied, DDied, Abor, TAbor, TotPreg,VDate,Rnd)";
				SQL += " Select 'p',Vill,bari,Hh, Pno,sno,Visit,MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut,"; 
				SQL += " Died, SDied, DDied, Abor, TAbor, TotPreg,VDate,Rnd From PregHis where Vill||Bari||HH = '"+ VillBariHH +"'";
				//SQL += " Where PNo in(Select PNO from Member Where Vill||Bari||HH = '"+ VillBariHH +"')";
				C.Save(SQL);
				
				//-- -Visit Info.-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				SQL = " Insert into tTrans(Status,Vill,bari,HH,SNo,Dma,VDate,Rnd,Lat,Lon)";
				SQL += " Select 'v',Vill,bari,HH,RSNo,Dma,VDate,Rnd,Lat,Lon from Visits where Vill||Bari||HH = '"+ VillBariHH +"' and Rnd = '"+ Rnd +"'";
				C.Save(SQL);
				
				//-- -Immunization-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
				C.Save("delete from ImmunizationTemp where Vill||Bari||HH = '"+ VillBariHH +"'");
				
				//SQL = "Insert into ImmunizationTemp Select * from Immunization where Vill||Bari||HH = '"+ VillBariHH +"'";
				//18 Sep 2014
				SQL = "Insert into ImmunizationTemp Select * from Immunization where PNo in(Select PNO from Member Where Vill||Bari||HH = '"+ VillBariHH +"')";
				C.Save(SQL);
	}


	private void ShowBariForm(final String Vill,final String BariNo, final String Status)
    {
		//Status: u-update, s-save (new bari)
		
    	final Dialog dialog = new Dialog(SESPregHisMissingList.this);
    	dialog.setTitle("Bari Form");
		//dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialog.setContentView(R.layout.bari);
    	dialog.setCancelable(true);
    	dialog.setCanceledOnTouchOutside(false);
    	final Spinner BariList = (Spinner)findViewById(R.id.BariList);
    	final TextView txtCluster = (TextView)dialog.findViewById(R.id.txtCluster);
    	final TextView txtBlock = (TextView)dialog.findViewById(R.id.txtBlock);
    	final TextView txtVill = (TextView)dialog.findViewById(R.id.txtVill);
    	final EditText txtBari = (EditText)dialog.findViewById(R.id.txtBari);
    	final EditText txtBName = (EditText)dialog.findViewById(R.id.txtBName);
    	final EditText txtBLoc = (EditText)dialog.findViewById(R.id.txtBLoc);
    	
    	txtCluster.setText(g.getClusterCode());
    	txtBlock.setText(g.getBlockCode());
    	txtVill.setText(g.getVillageCode()+", "+CurrentVillage);
    	txtBari.setText(BariNo);
    	
    	if(Status.equalsIgnoreCase("u"))
    	{
    		txtBName.setText(BariList.getSelectedItem().toString().substring(6,BariList.getSelectedItem().toString().length()));
    		txtBLoc.setText(C.ReturnSingleValue("Select BariLoc from Baris where Vill||Bari='"+ (Vill+BariNo) +"'"));
    	}
    	
    	Button cmdBariSave = (Button)dialog.findViewById(R.id.cmdBariSave);
    	cmdBariSave.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View arg0) {
	    	    if(txtBName.getText().length()==0)
	    	    {
	    	    	Connection.MessageBox(SESPregHisMissingList.this, "বাড়ীর নাম খালি রাখা যাবে না।");
	    	    	return;
	    	    }
	    	    else
	    	    {
	    	    	try
	    	    	{
	    	    		//------------------------------------------------------------------
	    	    		int latdegrees = (int)currentLatitudeNet;
	    	    		currentLatitudeNet -= latdegrees;
	    	    		currentLatitudeNet *= 60.;
	    				if(currentLatitudeNet < 0)
	    					currentLatitudeNet *= -1;
	    				int latminutes = (int)currentLatitudeNet;
	    				currentLatitudeNet -= latminutes;
	    				currentLatitudeNet *= 60.;
	    				double latseconds = currentLatitudeNet;
	    				//------------------------------------------------------------------
	    	    		int londegrees = (int)currentLongitudeNet;
	    	    		currentLongitudeNet -= londegrees;
	    	    		currentLongitudeNet *= 60.;
	    				if(currentLongitudeNet < 0)
	    					currentLongitudeNet *= -1;
	    				int lonminutes = (int)currentLongitudeNet;
	    				currentLongitudeNet -= lonminutes;
	    				currentLongitudeNet *= 60.;
	    				double lonseconds = currentLongitudeNet;	    				
	    				//------------------------------------------------------------------
	    				
		    	    	String SQL="";
		    	    	int selBari = BariList.getSelectedItemPosition();
		    	    	if(Status.equalsIgnoreCase("s"))
		    	    	{
			    	    	SQL  = "Insert into Baris (Vill, Bari, Block, BariName,BariLoc,Xdec, Xmin, Xsec, Ydec, Ymin, Ysec,Status, EnDt,Upload)Values(";
			    	    	SQL += "'"+ Vill +"','"+ txtBari.getText() +"','"+ txtBlock.getText() +"','"+ txtBName.getText() +"','"+ txtBLoc.getText() +"',";
			    	    	SQL += "'"+ String.format("%d",latdegrees) +"',";
			    	    	SQL += "'"+ String.format("%d",latminutes) +"',";
			    	    	SQL += "'"+ String.format("%.3f",latseconds) +"',";
			    	    	
			    	    	SQL += "'"+ String.format("%d",londegrees) +"',";
			    	    	SQL += "'"+ String.format("%d",lonminutes) +"',";
			    	    	SQL += "'"+ String.format("%.3f",lonseconds) +"',";
			    	    	SQL += "'1','"+ g.CurrentDateYYYYMMDD +"','2')";
			    	    	C.Save(SQL);
		    	    	}
		    	    	else if(Status.equalsIgnoreCase("u"))
		    	    	{
			    	    	SQL  = "Update Baris Set ";
			    	    	SQL += " BariName='"+ txtBName.getText() +"',BariLoc='"+ txtBLoc.getText() +"',Upload='2'";
			    	    	SQL += " Where Vill='"+ Vill +"' and Bari='"+ txtBari.getText() +"'";
			    	    	C.Save(SQL);		    	    		
		    	    	}
		    	    	dialog.dismiss();
		    	    	final Spinner BariList = (Spinner)findViewById(R.id.BariList);	    	
		    	    	BariList.setAdapter(C.getArrayAdapter("Select ' ' union Select ' All Bari' union select Bari||', '||BariName from baris b,mdssvill v where b.vill=v.vill and v.cluster='"+ g.getClusterCode() +"' and b.block='"+ g.getBlockCode() +"'"));
		    	    	
		    	    	BariList.setSelection(selBari);
		    	    	//BlockList(false, Global.Left(BariList.getSelectedItem().toString(),4));
	    	    	}
	    	    	catch(Exception ex)
	    	    	{
	    	    		Connection.MessageBox(SESPregHisMissingList.this, ex.getMessage());
	    	    		return;
	    	    	}
	    	    	
	    	    }
    	    }
    	});    	
    	
    	Button cmdBariClose = (Button)dialog.findViewById(R.id.cmdBariClose);
    	cmdBariClose.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View arg0) {
    	        // TODO Auto-generated method stub
    	        dialog.cancel();
    	    }
    	});
		
    	dialog.show();
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
 
            public void onStatusChanged(String provider, int status, 
                    Bundle extras) { 
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
}
