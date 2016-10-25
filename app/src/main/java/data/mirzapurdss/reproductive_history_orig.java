package data.mirzapurdss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.text.TextWatcher;
import android.text.Editable;

public class reproductive_history_orig extends Activity {
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backmenu, menu);
        return true;
    }

	public boolean onOptionsItemSelected(MenuItem item) {    
    	switch (item.getItemId()) {    
    		case R.id.mnuBack:
	    		  AlertDialog.Builder adb = new AlertDialog.Builder(reproductive_history_orig.this);
	    		  adb.setTitle("Pregnancy History");
		          adb.setMessage("আপনি কি এ ফরম থেকে বের হতে চান[Yes/No]?");
		          
		          adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
		              public void onClick(DialogInterface dialog1, int which) {		  
 	        		            		  	        		            		  
		              }});

		          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
		              public void onClick(DialogInterface dialog1, int which) {
		            	  finish();  			              		        		            	  
		              }});
		          
		          adb.show();			
    	}    
    	return false;
    }	
	 Connection C;
	 Location currentLocation; 
	 double currentLatitude,currentLongitude; 
	 Global g;


	String Status="",EntryStatus="",form="";

	//Master Variable
	//--------------------------------------------------------
	public String CallinForm;
	private String Rnd;
	private String VillageCode;
	private String BariCode;
	private String HHCode;
	private String Household;
	private String SNo;
	private String PNo;
	
	Bundle IDbundle;

	/** Called when the activity is first created. */
	    
		TextView VlblQ102;
        Spinner txtQ102SL;
        TextView VlblQ102A;
        EditText txtQ102Name;
        TextView VlblQ103;
        Spinner spnQ103;     
        TextView VlblQ104MM;
        EditText txtQ104MM;
        TextView VlblQ104YY;
        EditText txtQ104YY;
        TextView VlblQ105;
        RadioGroup rdogrpQ105;
        RadioButton rdoYQ105;
        RadioButton rdoNQ105;
        TextView VlblQ106;
        RadioGroup rdogrpQ106;
        RadioButton rdoYQ106;
        RadioButton rdoNQ106;
        TextView VlblQ107;
        TextView VlblQ107A;
        EditText txtQ107A;
        TextView VlblQ107B;
        EditText txtQ107B;
        TextView VlblQ108;
        RadioGroup rdogrpQ108;
        RadioButton rdoYQ108;
        RadioButton rdoNQ108;
        TextView VlblQ109;
        TextView VlblQ109A;
        EditText txtQ109A;
        TextView VlblQ109B;
        EditText txtQ109B;
        TextView VlblQ110;
        RadioGroup rdogrpQ110;
        RadioButton rdoYQ110;
        RadioButton rdoNQ110;
        TextView VlblQ111;
        TextView VlblQ111A;
        EditText txtQ111A;
        TextView VlblQ111B;
        EditText txtQ111B;
        TextView VlblQ112;
        RadioGroup rdogrpQ112;
        RadioButton rdoYQ112;
        RadioButton rdoNQ112;
        TextView VlblQ113;
        EditText txtQ113;
        TextView VlblQ114;
        EditText txtQ114;
        TextView VlblQ115A;
        TextView VlblQ115B;
        TextView VlblQ115C;
        TextView VlblQ115;
        RadioGroup rdogrpQ115;
        RadioButton rdoYQ115;
        RadioButton rdoNQ115;
        TextView lblQ115A;
        TextView lblQ115B;
        TextView lblQ115C;

	
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
        setContentView(R.layout.reproductive_history);
        C=new Connection(this);
        g = Global.getInstance();
        EntryStatus="Entry";
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        IDbundle  = getIntent().getExtras();        
        VillageCode = IDbundle.getString("vill");
        BariCode  = IDbundle.getString("bari");
        HHCode    = IDbundle.getString("hh");
        Household = IDbundle.getString("household");
        Rnd       = IDbundle.getString("rnd");


	    VlblQ102=(TextView) findViewById(R.id.VlblQ102);
	    txtQ102SL=(Spinner) findViewById(R.id.txtQ102SL);
	    //txtQ102SL.setAdapter(C.getArrayAdapter("select (Sno||'-'||Name)Name from tTrans where status='m' and Vill||Bari||Hh='"+ (VillageCode+BariCode+HHCode) +"' and Sex='2' and ms<>'30' and ((julianday(date('now'))-julianday(bdate))/365.25)<50 and length(extype)=0 order by Name asc"));
		txtQ102SL.setAdapter(C.getArrayAdapter("select (Sno||'-'||Name)Name from tTrans where status='m' and Vill||Bari||Hh='"+ (VillageCode+BariCode+HHCode) +"' and Sex='2' and ms<>'30' and ((julianday(date('now'))-julianday(bdate))/365.25)<50 order by Name asc"));
	    
        VlblQ103=(TextView) findViewById(R.id.VlblQ103);
        spnQ103=(Spinner) findViewById(R.id.spnQ103);        
        VlblQ104MM=(TextView) findViewById(R.id.VlblQ104MM);
        txtQ104MM=(EditText) findViewById(R.id.txtQ104MM);
        VlblQ104YY=(TextView) findViewById(R.id.VlblQ104YY);
        txtQ104YY=(EditText) findViewById(R.id.txtQ104YY);
        VlblQ105=(TextView) findViewById(R.id.VlblQ105);
        rdogrpQ105=(RadioGroup) findViewById(R.id.rdogrpQ105);
        rdoYQ105=(RadioButton) findViewById(R.id.rdoYQ105);
        rdoNQ105=(RadioButton) findViewById(R.id.rdoNQ105);
        VlblQ106=(TextView) findViewById(R.id.VlblQ106);
        rdogrpQ106=(RadioGroup) findViewById(R.id.rdogrpQ106);
        rdoYQ106=(RadioButton) findViewById(R.id.rdoYQ106);
        rdoNQ106=(RadioButton) findViewById(R.id.rdoNQ106);
        VlblQ107=(TextView) findViewById(R.id.lblHQ107);
        VlblQ107A=(TextView) findViewById(R.id.VlblQ107A);
        txtQ107A=(EditText) findViewById(R.id.txtQ107A);
        VlblQ107B=(TextView) findViewById(R.id.VlblQ107B);
        txtQ107B=(EditText) findViewById(R.id.txtQ107B);
        VlblQ108=(TextView) findViewById(R.id.VlblQ108);
        rdogrpQ108=(RadioGroup) findViewById(R.id.rdogrpQ108);
        rdoYQ108=(RadioButton) findViewById(R.id.rdoYQ108);
        rdoNQ108=(RadioButton) findViewById(R.id.rdoNQ108);
        VlblQ109=(TextView) findViewById(R.id.lblHQ109);
        VlblQ109A=(TextView) findViewById(R.id.VlblQ109A);
        txtQ109A=(EditText) findViewById(R.id.txtQ109A);
        VlblQ109B=(TextView) findViewById(R.id.VlblQ109B);
        txtQ109B=(EditText) findViewById(R.id.txtQ109B);
        VlblQ110=(TextView) findViewById(R.id.VlblQ110);
        rdogrpQ110=(RadioGroup) findViewById(R.id.rdogrpQ110);
        rdoYQ110=(RadioButton) findViewById(R.id.rdoYQ110);
        rdoNQ110=(RadioButton) findViewById(R.id.rdoNQ110);
        VlblQ111=(TextView) findViewById(R.id.lblHQ111);
        VlblQ111A=(TextView) findViewById(R.id.VlblQ111A);
        txtQ111A=(EditText) findViewById(R.id.txtQ111A);
        VlblQ111B=(TextView) findViewById(R.id.VlblQ111B);
        txtQ111B=(EditText) findViewById(R.id.txtQ111B);
        VlblQ112=(TextView) findViewById(R.id.VlblQ112);
        rdogrpQ112=(RadioGroup) findViewById(R.id.rdogrpQ112);
        rdoYQ112=(RadioButton) findViewById(R.id.rdoYQ112);
        rdoNQ112=(RadioButton) findViewById(R.id.rdoNQ112);
        VlblQ113=(TextView) findViewById(R.id.VlblQ113);
        txtQ113=(EditText) findViewById(R.id.txtQ113);
        VlblQ114=(TextView) findViewById(R.id.VlblQ114);
        txtQ114=(EditText) findViewById(R.id.txtQ114);
        VlblQ115A=(TextView) findViewById(R.id.lblQ115A);
        VlblQ115B=(TextView) findViewById(R.id.lblQ115B);
        VlblQ115C=(TextView) findViewById(R.id.lblQ115C);
        lblQ115A=(TextView) findViewById(R.id.lblQ115A);
        lblQ115B=(TextView) findViewById(R.id.lblQ115B);
        lblQ115C=(TextView) findViewById(R.id.lblQ115C);
        VlblQ115=(TextView) findViewById(R.id.VlblQ115);
        rdogrpQ115=(RadioGroup) findViewById(R.id.rdogrpQ115);
        rdoYQ115=(RadioButton) findViewById(R.id.rdoYQ115);
        rdoNQ115=(RadioButton) findViewById(R.id.rdoNQ115);
	    
	    
          ArrayAdapter adptrQ103=ArrayAdapter.createFromResource(this, R.array.RHQlistQ103, android.R.layout.simple_spinner_item);
          adptrQ103.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          spnQ103.setAdapter(adptrQ103);
      	
          
        spnQ103.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    	    @Override
    	    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    	    	if(spnQ103.getSelectedItemPosition()>=2 & spnQ103.getSelectedItemPosition()<=4)
    	    	{
			    	txtQ104MM.setText(null);
			    	txtQ104YY.setText(null);
			    	rdogrpQ105.clearCheck();
			    	rdogrpQ106.clearCheck();
			    	txtQ107A.setText(null);
			    	txtQ107B.setText(null);
			    	rdogrpQ108.clearCheck();
			    	txtQ109A.setText(null);
			    	txtQ109B.setText(null);
			    	rdogrpQ110.clearCheck();
			    	txtQ111A.setText(null);
			    	txtQ111B.setText(null);
			    	rdogrpQ112.clearCheck();
			    	txtQ113.setText(null);
			    	txtQ114.setText(null); 	
			    	
    	    		txtQ104MM.setEnabled(false);
    	    		txtQ104MM.setText("");
    	    		txtQ104YY.setEnabled(false);
    	    		txtQ104YY.setText("");
    	    		rdoYQ105.setEnabled(false);
    	    		rdoNQ105.setEnabled(false);
    	    		rdoYQ106.setEnabled(false);
      				rdoNQ106.setEnabled(false);  				
      				txtQ107A.setEnabled(false);
      				txtQ107A.setText("");
      				txtQ107B.setEnabled(false);
      				txtQ107B.setText("");
      				rdoYQ108.setEnabled(false);
      				rdoNQ108.setEnabled(false);  			
      				txtQ109A.setEnabled(false);  	
      				txtQ109A.setText("");
      				txtQ109B.setEnabled(false);
      				txtQ109B.setText("");
      				rdoYQ110.setEnabled(false);
      				rdoNQ110.setEnabled(false);
      				txtQ111A.setEnabled(false);
      				txtQ111A.setText("");
      				txtQ111B.setEnabled(false);
      				txtQ111B.setText("");
      				rdoYQ112.setEnabled(false);
      				rdoNQ112.setEnabled(false);
      				txtQ113.setEnabled(false);
      				txtQ113.setText("");
      				txtQ114.setEnabled(false);
      				txtQ114.setText("");
    	    	}
    	    	else
    	    	{
    	    		txtQ104MM.setEnabled(true);
    	    		txtQ104YY.setEnabled(true);
    	    		rdoYQ105.setEnabled(true);
    	    		rdoNQ105.setEnabled(true);
    	    		rdoYQ106.setEnabled(true);
      				rdoNQ106.setEnabled(true);  				
      				txtQ107A.setEnabled(true);
      				txtQ107B.setEnabled(true);
      				rdoYQ108.setEnabled(true);
      				rdoNQ108.setEnabled(true);  			
      				txtQ109A.setEnabled(true);  	
      				txtQ109B.setEnabled(true);
      				rdoYQ110.setEnabled(true);
      				rdoNQ110.setEnabled(true);
      				txtQ111A.setEnabled(true);
      				txtQ111B.setEnabled(true);
      				rdoYQ112.setEnabled(true);
      				rdoNQ112.setEnabled(true);
      				txtQ113.setEnabled(true);
      				txtQ114.setEnabled(true);  	    		
    	    	}
    	    }
    	    @Override
    	    public void onNothingSelected(AdapterView<?> parentView) {
    	        // your code here
    	    }  
          });

      	
      	rdogrpQ105.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
  			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
  			if(R.id.rdoYQ105 == checkedId)
  			{  				
  				rdoYQ106.setEnabled(true);
  				rdoNQ106.setEnabled(true);  				
  				txtQ107A.setEnabled(true);  				
  				txtQ107B.setEnabled(true);
  				rdoYQ108.setEnabled(true);
  				rdoNQ108.setEnabled(true);  			
  				txtQ109A.setEnabled(true);  				
  				txtQ109B.setEnabled(true);
  				rdoYQ110.setEnabled(true);
  				rdoNQ110.setEnabled(true);
  				txtQ111A.setEnabled(true);
  				txtQ111B.setEnabled(true);
  				rdoYQ112.setEnabled(true);
  				rdoNQ112.setEnabled(true);
  				txtQ113.setEnabled(true);
  				txtQ114.setEnabled(true);

  				VlblQ106.setTextColor(Color.BLACK);
  				VlblQ107.setTextColor(Color.BLACK);
  				VlblQ107A.setTextColor(Color.BLACK);
  				VlblQ107B.setTextColor(Color.BLACK);
  				VlblQ108.setTextColor(Color.BLACK);
  				VlblQ109.setTextColor(Color.BLACK);
  				VlblQ109A.setTextColor(Color.BLACK);
  				VlblQ109B.setTextColor(Color.BLACK);
  				VlblQ110.setTextColor(Color.BLACK);
  				VlblQ111.setTextColor(Color.BLACK);
  				VlblQ111A.setTextColor(Color.BLACK);
  				VlblQ111B.setTextColor(Color.BLACK);
  				VlblQ112.setTextColor(Color.BLACK);
  				VlblQ113.setTextColor(Color.BLACK);
  				VlblQ114.setTextColor(Color.BLACK);
  			} 
  			else if(R.id.rdoNQ105 == checkedId) 
  			{ 
  				
  				rdoYQ106.setChecked(false);
  				rdoNQ106.setChecked(false);  
  				rdoYQ108.setChecked(false);
  				rdoNQ108.setChecked(false);  				
  				//rdoYQ110.setChecked(false);
  				//rdoNQ110.setChecked(false);
  				//rdoYQ112.setChecked(false);
  				//rdoNQ112.setChecked(false);
  				
  				rdoYQ106.setEnabled(false);
  				rdoNQ106.setEnabled(false);   				
  				rdoYQ108.setEnabled(false);
  				rdoNQ108.setEnabled(false);
  				//rdoYQ110.setEnabled(false);
  				//rdoNQ110.setEnabled(false);
  				//rdoYQ112.setEnabled(false);
  				//rdoNQ112.setEnabled(false);
  				
  				txtQ107A.setEnabled(false);  				
  				txtQ107B.setEnabled(false);  								
  				txtQ109A.setEnabled(false);  				
  				txtQ109B.setEnabled(false);
  				//txtQ111A.setEnabled(false);  				
  				//txtQ111B.setEnabled(false);
  				//txtQ113.setEnabled(false);
  				//txtQ114.setEnabled(false);
  				
  				txtQ107A.setText(null);
  				txtQ107B.setText(null);
  				txtQ109A.setText(null);
  				txtQ109B.setText(null);
  				//txtQ111A.setText(null);
  				//txtQ111B.setText(null);
  				//txtQ113.setText(null);
  				//txtQ114.setText(null);
  				
  				VlblQ106.setTextColor(Color.GRAY);
  				VlblQ107.setTextColor(Color.GRAY);
  				VlblQ107A.setTextColor(Color.GRAY);
  				VlblQ107B.setTextColor(Color.GRAY);
  				VlblQ108.setTextColor(Color.GRAY);
  				VlblQ109.setTextColor(Color.GRAY);	
  				VlblQ109A.setTextColor(Color.GRAY);		
  				VlblQ109B.setTextColor(Color.GRAY);		
  				//VlblQ110.setTextColor(Color.GRAY);
  				//VlblQ111.setTextColor(Color.GRAY);	
  				//VlblQ111A.setTextColor(Color.GRAY);		
  				//VlblQ111B.setTextColor(Color.GRAY);
  				//VlblQ112.setTextColor(Color.GRAY);
  				//VlblQ113.setTextColor(Color.GRAY);
  				//VlblQ114.setTextColor(Color.GRAY);
  			} 

  		}});
  		
          	
          	
          	rdogrpQ106.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
      			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
      			if(R.id.rdoYQ106 == checkedId)
      			{  	
      				txtQ107A.setEnabled(true);      				
      				txtQ107B.setEnabled(true);     				
      				
      				VlblQ107.setTextColor(Color.BLACK);
      				VlblQ107A.setTextColor(Color.BLACK);
      				VlblQ107B.setTextColor(Color.BLACK);
      			
      			} 
      			else if(R.id.rdoNQ106 == checkedId) 
      			{       				
      				
      				txtQ107A.setEnabled(false);      				
      				txtQ107B.setEnabled(false); 
      				txtQ107A.setText(null);
      				txtQ107B.setText(null);
      				
      				VlblQ107.setTextColor(Color.GRAY);
      				VlblQ107A.setTextColor(Color.GRAY);
      				VlblQ107B.setTextColor(Color.GRAY);
      				
      			} 
      		}});
  	      
          	rdogrpQ108.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
      			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
      			if(R.id.rdoYQ108 == checkedId)
      			{  	
      				txtQ109A.setEnabled(true);      				
      				txtQ109B.setEnabled(true);     				
      				
      				VlblQ109.setTextColor(Color.BLACK);
      				VlblQ109A.setTextColor(Color.BLACK);
      				VlblQ109B.setTextColor(Color.BLACK);
      			
      			} 
      			else if(R.id.rdoNQ108 == checkedId) 
      			{         				
      				txtQ109A.setEnabled(false);      				
      				txtQ109B.setEnabled(false);      				
      				txtQ109A.setText(null);
      				txtQ109B.setText(null);
      				
      				VlblQ109.setTextColor(Color.GRAY);
      				VlblQ109A.setTextColor(Color.GRAY);
      				VlblQ109B.setTextColor(Color.GRAY);
      				
      			} 
      		}});
  	      
          	
          	rdogrpQ110.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
      			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
      			if(R.id.rdoYQ110 == checkedId)
      			{  	
      				txtQ111A.setEnabled(true);      				
      				txtQ111B.setEnabled(true);     				
      				
      				VlblQ111.setTextColor(Color.BLACK);
      				VlblQ111A.setTextColor(Color.BLACK);
      				VlblQ111B.setTextColor(Color.BLACK);
      			
      			} 
      			else if(R.id.rdoNQ110 == checkedId) 
      			{         				
      				txtQ111A.setEnabled(false);      				
      				txtQ111B.setEnabled(false);      				
      				txtQ111A.setText(null);
      				txtQ111B.setText(null);
      				
      				VlblQ111.setTextColor(Color.GRAY);
      				VlblQ111A.setTextColor(Color.GRAY);
      				VlblQ111B.setTextColor(Color.GRAY);
      				
      			} 
      		}});
          	
          	
          	rdogrpQ112.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
      			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
      			if(R.id.rdoYQ112 == checkedId)
      			{  	
      				txtQ113.setEnabled(true);     				
      				VlblQ113.setTextColor(Color.BLACK);      			
      			} 
      			else if(R.id.rdoNQ112 == checkedId) 
      			{         				
      				txtQ113.setEnabled(false); 
      				txtQ113.setText(null);
      				VlblQ113.setTextColor(Color.GRAY);
      			} 
      		}});
          	
          	
          	txtQ113.addTextChangedListener(new TextWatcher() {	    		 
	    		   public void afterTextChanged(Editable s)
	    		   {
	    			   lblQ115C.setText(txtQ113.getText());    	
	    		   }	    		 
	    		   public void beforeTextChanged(CharSequence s, int start,
	    		     int count, int after) {
	    		   }	    		 
	    		   public void onTextChanged(CharSequence s, int start,
	    		     int before, int count) 
	    		   {
	    			   //MainDataRetrieve();			   
	    		   }
	    		  });





			Button btnRHQSaveContinue=(Button)findViewById(R.id.cmdRHQSaveandContinue);
			btnRHQSaveContinue.setOnClickListener(new View.OnClickListener() {
	  			public void onClick(View view)
	  			{


	  				PNo = C.ReturnSingleValue("Select PNo from tTrans where status='m' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'");
					String SQL="";
					int i=0;

						String  VQ103="", VQ105="",VQ106="", VQ108="",VQ110="",VQ109A="",VQ109B="",VQ111A="",VQ111B="";
                    Integer Q107A=0,Q107B=0,Q109A=0,Q109B=0,Q111A=0,Q111B=0,Q113=0;
						if(spnQ103.getSelectedItemPosition()==0)
						{
							Connection.MessageBox(reproductive_history_orig.this, "সাক্ষাৎ এর অবস্থা কি তথ্য খালি রাখা যাবে না");
	  						return;
						}
						if(spnQ103.getSelectedItemPosition()==1 & txtQ104MM.getText().length()==0)
						{
							Connection.MessageBox(reproductive_history_orig.this, "গর্ভবতী মায়ের কোন মাসে বিয়ে হয়েছিল তথ্য খালি রাখা যাবে না");
	  						return;
						}
						if(spnQ103.getSelectedItemPosition()==1 & txtQ104YY.getText().length()==0)
						{
							Connection.MessageBox(reproductive_history_orig.this, "গর্ভবতী মায়ের কোন বছরে বিয়ে হয়েছিল এ তথ্য খালি রাখা যাবে না");
	  						return;
						}

						  try
	  	  					{
	  		  					Calendar c = Calendar.getInstance();
	  		  					//SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
	  		  					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	  		  					String formattedDate = sdf.format(c.getTime());
	  			  		        Date date1 = sdf.parse(formattedDate);
	  			  		        Date date2 = sdf.parse(txtQ104YY.getText()+"-"+txtQ104MM.getText()+"-"+"1");
	  			  		        if(date2.after(date1))
	  					        {
	  					        	Connection.MessageBox(reproductive_history_orig.this, "বিয়ের তারিখ বর্তমান তারিখের থেকে বড় হবে না");
	  					        	return;
	  					        }
	  			  		       /*
	  			  		        Integer yy; 
	  			  		        if(date1.getMonth()<date2.getMonth())
	  			  		        {
	  			  		        	yy=date1.getYear()-date2.getYear()-1;
	  			  		        }
	  			  		        else
	  			  		        {
	  			  		        	yy=date1.getYear()-date2.getYear();
	  			  		        }
	  			  		        if(yy<12 ||yy>50)
	  			  		        {
		  			  		        //Connection.MessageBox(reproductive_history_orig.this, "বিয়ের বয়স ১২-৫০ বছরের মধ্যে হতে হবে");
	  					        	//return;
	  			  		        }
	  			  		        
	  			  		        int age = Global.DateDifference(Global.DateNowDMY(), StartDateDDMMYYYY)
	  			  		       */
	  	  					}
						  catch(ParseException ex)
	  	  					{
	  	  			    		ex.printStackTrace();
	  	  			    	}



						if(spnQ103.getSelectedItemPosition()==4)
						{
							VQ103="7";
						}
						else
						{
							VQ103=String.valueOf(spnQ103.getSelectedItemPosition());
						}
	  					boolean available=false;

	  					try
	  					{
			  		  	available=C.Existence("Select * from tTrans Where status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'");

	  						if(!available)
	  						{
	  							if(spnQ103.getSelectedItemPosition()==1)
	  							{
	  								SQL="Insert into tTrans";
			  	  		   	  		SQL+="(Status,Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY,VDate,Rnd) values(";
			  	  		   	  		SQL+="'p',";
			  	  		   	  	  	SQL+="'" + VillageCode + "',";
			  	  			  		SQL+="'" + BariCode + "',";
			  	  			  		SQL+="'" + HHCode + "',";
			  	  			  		SQL+="'" + Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"',";
			  	  			  		SQL+="'" + PNo +"',";
			  	  					SQL+="'" + VQ103 +"',";
			  						SQL+="'" + txtQ104MM.getText() + "','" +  txtQ104YY.getText() + "',";
			  						SQL+="'"+ Global.DateNow() +"',";
			  						SQL+="'"+ g.getRoundNumber() +"')";

			  						C.Save(SQL);

	  							}
	  							else
	  							{
	  								SQL="Insert into tTrans";
			  	  		   	  		SQL+="(Status,Vill, Bari, Hh, Sno, Pno, Visit,";
			  	  		   	  		SQL+="MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd) values(";
			  	  		   	  		SQL+="'p',";
			  	  		   	  	  	SQL+="'" + VillageCode + "',";
			  	  			  		SQL+="'" + BariCode + "',";
			  	  			  		SQL+="'" + HHCode + "',";
			  	  			  		SQL+="'" + Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"',";
			  	  			  		SQL+="'" + PNo +"',";
			  	  					SQL+="'" + VQ103 +"',";
			  						SQL+="'','','','','','','','','','','','','','','','"+ Global.DateNow() +"','"+ g.getRoundNumber()  +"')";
			  						C.Save(SQL);
			  						Connection.MessageBox(reproductive_history_orig.this, "Saved successfully.");

			  						return;
	  							}

	  						}
	  						else
	  						{
	  							if(spnQ103.getSelectedItemPosition()==1)
	  							{
	  								SQL="Update tTrans set  Visit='" + VQ103 + "',MarM='" + txtQ104MM.getText() + "',MarY='" + txtQ104YY.getText() + "' ";
			  						SQL+=" where status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
					  		  		C.Save(SQL);

	  							}
	  							else
	  							{
	  								SQL="Update tTrans set Visit='" + VQ103 + "',MarM='',MarY='', Births='', LiveHh='', SLiveHh='', DLiveHh='', LiveOut='', SLiveOut='', DLiveOut='', Died='', SDied='', DDied='', Abor='', TAbor='', TotPreg='' ";
	  								SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
	  								C.Save(SQL);
						  		  	Connection.MessageBox(reproductive_history_orig.this, "Updated successfully.");
			  						return;
	  							}

	  						}


						if(!rdoYQ105.isChecked() && !rdoNQ105.isChecked())
						{
							Connection.MessageBox(reproductive_history_orig.this, "মা কখনও কোন সন্তান জন্ম দিয়েছেন কি না তথ্য খালি রাখা যাবে না");
							return;
						}


						if(rdoYQ105.isChecked())
						{
							VQ105="1";
							if(!rdoYQ106.isChecked() && !rdoNQ106.isChecked())
							{
								Connection.MessageBox(reproductive_history_orig.this, "বর্তমানে সন্তানরা মায়ের কাছে থাকে কি না তথ্য খালি রাখা যাবে না");
								return;
							}
							else if(rdoYQ106.isChecked())
							{
								if(txtQ107A.getText().toString().length()==0)
								{

								}
								else if(txtQ107B.getText().toString().length()==0)
								{

								}
							}
						}
						else
						{
							VQ105="2";
						}
						if(rdoYQ106.isChecked())
						{
							VQ106="1";
						}
						else
						{
							VQ106="2";
						}
						if(rdoYQ105.isChecked())
						{
							if(rdoYQ106.isChecked())
							{
								if(txtQ107A.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন ছেলে আপনার সাথে থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								if(txtQ107B.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন মেয়ে আপনার সাথে থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								SQL="Update tTrans set Births='" + VQ105 + "', LiveHh='" + VQ106 + "', SLiveHh='" + txtQ107A.getText() + "', DLiveHh='" + txtQ107B.getText() + "' ";
								SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
				  		  		C.Save(SQL);

							}
							else
							{
								SQL="Update tTrans set Births='" + VQ105 + "', LiveHh='" + VQ106 + "', SLiveHh='', DLiveHh='' ";
								SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
				  		  		C.Save(SQL);

							}

						}
						else
						{
							SQL="Update tTrans set Births='" + VQ105 + "', LiveHh='', SLiveHh='', DLiveHh='',LiveOut='', SLiveOut='', DLiveOut='' ";
							SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
			  		  		C.Save(SQL);


						}

						if(rdoYQ105.isChecked())
						{

							//return;
						}
						else
						{

							//return;
						}



						if(rdoYQ105.isChecked())
						{
							if(!rdoYQ108.isChecked() && !rdoNQ108.isChecked())
							{
								Connection.MessageBox(reproductive_history_orig.this, "মা সন্তান জন্ম দিয়েছেন কিন্তু তার কাছে থাকে না এই তথ্য খালি রাখা যাবে না");
								return;
							}

							if(rdoYQ108.isChecked())
							{
								VQ108="1";
								if(txtQ109A.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন ছেলে অন্য কোথাও থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								if(txtQ109B.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন মেয়েঅন্য কোথাও  থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								VQ109A=txtQ109A.getText().toString();
								VQ109B=txtQ109B.getText().toString();
							}
							else
							{
								VQ108="2";
								VQ109A="";
								VQ109B="";
							}
						}
						else
						{
							VQ108="";
							VQ109A="";
							VQ109B="";
						}
						if(rdoYQ105.isChecked())
						{
							if(!rdoYQ110.isChecked() && !rdoNQ110.isChecked())
							{
								Connection.MessageBox(reproductive_history_orig.this, "মা সন্তান জন্ম দিয়েছেন কিন্তু পরে মারা গিয়েছেন এই তথ্য খালি রাখা যাবে না");
								return;
							}

							if(rdoYQ110.isChecked())
							{
								VQ110="1";
								if(txtQ111A.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন ছেলে অন্য কোথাও থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								if(txtQ111B.getText().toString().length()==0)
								{
									Connection.MessageBox(reproductive_history_orig.this, "কতজন মেয়ে অন্য কোথাও  থাকে তথ্য ফাকা রাখা যাবে না");
									return;
								}
								VQ111A=txtQ111A.getText().toString();
								VQ111B=txtQ111B.getText().toString();
							}
							else
							{
								VQ110="2";
								VQ111A="";
								VQ111B="";
							}
						}




						Integer V107A=0,V107B=0;
						if(rdoYQ106.isChecked())
						{
							V107A=Integer.valueOf(txtQ107A.getText().toString());
							V107B=Integer.valueOf(txtQ107B.getText().toString());
						}
						lblQ115A.setText(String.valueOf(V107A+V107B+Integer.valueOf((VQ109A.length()==0?"0":VQ109A))+Integer.valueOf(VQ109B.length()==0?"0":VQ109B)));
						lblQ115B.setText(String.valueOf(Integer.valueOf(VQ111A.length()==0?"0":VQ111A)+Integer.valueOf(VQ111B.length()==0?"0":VQ111B)));

						SQL="Update tTrans set LiveOut='" + VQ108 +"', SLiveOut='" + VQ109A + "', DLiveOut='" + VQ109B + "', Died='" + VQ110 + "', SDied='" + VQ111A + "', DDied='" + VQ111B + "'";
						SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
		  		  		C.Save(SQL);


						try
						{
						String VQ112="",VQ113="",VQ115="";
						if(rdoYQ105.isChecked())
						{
							if(!rdoYQ112.isChecked() && !rdoNQ112.isChecked())
							{
								Connection.MessageBox(reproductive_history_orig.this, "মায়ের কোন গর্ভপাত/ গর্ভনষ্ট বা মৃত জন্ম দিয়েছিল কি না এই তথ্য ফাকা রাখা যাবে না");
								return;
							}
						}

						if(rdoYQ112.isChecked())
						{
							VQ112="1";
							if(txtQ113.getText().toString().length()==0)
							{
								Connection.MessageBox(reproductive_history_orig.this, "কতজন গর্ভপাত/ গর্ভনষ্ট বা মৃত জন্ম হয়েছে তথ্য ফাকা রাখা যাবে না");
								return;
							}
							VQ113=txtQ113.getText().toString();
						}
						else
						{
							VQ112="2";
							VQ113="";
						}
						if(txtQ114.getText().toString().length()==0)
						{
							Connection.MessageBox(reproductive_history_orig.this, "উত্তরগুলোর যোগফল ফাকা রাখা যাবে না");
							return;
						}
						//02 Jan 2015
						/*if(rdoYQ115.isChecked()==false && rdoNQ115.isChecked()==false)
						{
							Connection.MessageBox(reproductive_history_orig.this, "উত্তরদাতার উত্তর গুলো সঠিক হয়েছে কি না তথ্য ফাকা রাখা যাবে না");
							return;
						}*/
						if(rdoYQ115.isChecked())
						{
							VQ115="1";
						}
						else
						{

							VQ115="2";
						}


						if(rdoYQ105.isChecked())
						{
							if(rdoYQ106.isChecked())
							{
								Q107A=Integer.valueOf(txtQ107A.getText().toString());
								Q107B=Integer.valueOf(txtQ107B.getText().toString());
							}
							else
							{
								Q107A=0;
								Q107B=0;
							}
						}
						else
						{
							Q107A=0;
							Q107B=0;
							Q109A=0;
							Q109B=0;
						}
						if(rdoYQ105.isChecked())
						{
							if(rdoYQ108.isChecked())
							{
								Q109A=Integer.valueOf(txtQ109A.getText().toString());
								Q109B=Integer.valueOf(txtQ109B.getText().toString());
							}
							else
							{
								Q109A=0;
								Q109B=0;
							}
						}
						if(rdoYQ105.isChecked())
						{
							if(rdoYQ110.isChecked())
							{
								Q111A=Integer.valueOf(txtQ111A.getText().toString());
								Q111B=Integer.valueOf(txtQ111B.getText().toString());
							}
							else
							{
								Q111A=0;
								Q111B=0;
							}
						}
						if(rdoYQ105.isChecked())
						{
							if(rdoYQ112.isChecked())
							{
								Q113=Integer.valueOf(txtQ113.getText().toString());

							}
							else
							{
								Q113=0;

							}
						}

						if(rdoYQ105.isChecked())
						{
							if(Q107A+Q107B+Q109A+Q109B+Q111A+Q111B+Q113 !=Integer.valueOf(txtQ114.getText().toString()))
							{
								Connection.MessageBox(reproductive_history_orig.this, "উত্তরদাতার উত্তর গুলো সাথে যোগফলের মিল নাই");

								return;
							}
						}



						SQL="Update tTrans set Abor='" + VQ112 + "', TAbor='" + VQ113 + "', TotPreg='" + txtQ114.getText().toString() + "'";
						SQL+=" where  status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
		  		  		C.Save(SQL);
		  		  		Connection.MessageBox(reproductive_history_orig.this, "মায়ের সকল তথ্য সঠিক ভাবে সেভ হয়েছে");

						return;
						}
						catch(Exception ex)
						{
							Connection.MessageBox(reproductive_history_orig.this, "error info");
							return;
						}


	  			}

                catch(Exception ex)
                {
                    Connection.MessageBox(reproductive_history_orig.this, "error");
                    return;
                }

            }
	    	});
        

			txtQ102SL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			    	Clear();
			    	//PNo = C.ReturnSingleValue("Select PNo from PregHis where vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'");
			    	PNo = C.ReturnSingleValue("Select PNo from tTrans where status='m' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'");
			    	MainDataRetrieve(Household,Global.Left(txtQ102SL.getSelectedItem().toString(),2), PNo);
			    }

			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    }

			});

	MainDataRetrieve(Household,Global.Left(txtQ102SL.getSelectedItem().toString(),2), PNo);
			
    }
    public void Clear()
    {
    	spnQ103.setSelection(0);
    	txtQ104MM.setText(null);
    	txtQ104YY.setText(null);
    	rdogrpQ105.clearCheck();
    	rdogrpQ106.clearCheck();
    	txtQ107A.setText(null);
    	txtQ107B.setText(null);
    	rdogrpQ108.clearCheck();
    	txtQ109A.setText(null);
    	txtQ109B.setText(null);
    	rdogrpQ110.clearCheck();
    	txtQ111A.setText(null);
    	txtQ111B.setText(null);
    	rdogrpQ112.clearCheck();
    	txtQ113.setText(null);
    	txtQ114.setText(null); 	    	
    }
    
   
   
    public void MainDataRetrieve(String Household, String SNo, String PNo)
    {
    	String SQLStr="";
    	try
    	{
    		if(C.Existence("select vill from tTrans where status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'"))
    		{
		  		SQLStr="Select Visit,";  		
		  		SQLStr+="(case when VDate is null then '' else VDate end) VDate ,";
		  		SQLStr+="(case when MarM is null then '' else MarM end) MarM ,";
		  		SQLStr+="(case when MarY is null then '' else MarY end) MarY ,";
		  		SQLStr+="(case when Births is null then '' else Births end) Births ,";
		  		SQLStr+="(case when LiveHh is null then '' else LiveHh end) LiveHh ,";
		  		SQLStr+="(case when SLiveHh  is null then '' else SLiveHh  end) SLiveHh ,";
		  		SQLStr+="(case when DLiveHh  is null then '' else DLiveHh  end) DLiveHh ,";
		  		SQLStr+="(case when LiveOut  is null then '' else LiveOut  end) LiveOut ,";
		  		SQLStr+="(case when SLiveOut is null then '' else SLiveOut end) SLiveOut ,";
		  		SQLStr+="(case when DLiveOut is null then '' else DLiveOut end) DLiveOut ,";
		  		SQLStr+="(case when Died is null then '' else Died end) Died ,";
		  		SQLStr+="(case when SDied is null then '' else SDied end) SDied ,";
		  		SQLStr+="(case when DDied is null then '' else DDied end) DDied ,";
		  		SQLStr+="(case when Abor is null then '' else Abor end) Abor ,";
		  		SQLStr+="(case when TAbor is null then '' else TAbor end) TAbor ,";
		  		SQLStr+="(case when TotPreg is null then '' else TotPreg end) TotPreg";  		
		  			
		  		SQLStr+=" from tTrans where status='p' and vill||bari||hh='"+ Household +"' and SNo='"+ Global.Left(txtQ102SL.getSelectedItem().toString(),2) +"'";
    		}
    		else
    		{
		  		SQLStr = "Select Visit,";
		  		SQLStr+="(case when VDate is null then '' else VDate end) VDate ,";
		  		SQLStr+="(case when MarM is null then '' else MarM end) MarM ,";
		  		SQLStr+="(case when MarY is null then '' else MarY end) MarY ,";
		  		SQLStr+="(case when Births is null then '' else Births end) Births ,";
		  		SQLStr+="(case when LiveHh is null then '' else LiveHh end) LiveHh ,";
		  		SQLStr+="(case when SLiveHh  is null then '' else SLiveHh  end) SLiveHh ,";
		  		SQLStr+="(case when DLiveHh  is null then '' else DLiveHh  end) DLiveHh ,";
		  		SQLStr+="(case when LiveOut  is null then '' else LiveOut  end) LiveOut ,";
		  		SQLStr+="(case when SLiveOut is null then '' else SLiveOut end) SLiveOut ,";
		  		SQLStr+="(case when DLiveOut is null then '' else DLiveOut end) DLiveOut ,";
		  		SQLStr+="(case when Died is null then '' else Died end) Died ,";
		  		SQLStr+="(case when SDied is null then '' else SDied end) SDied ,";
		  		SQLStr+="(case when DDied is null then '' else DDied end) DDied ,";
		  		SQLStr+="(case when Abor is null then '' else Abor end) Abor ,";
		  		SQLStr+="(case when TAbor is null then '' else TAbor end) TAbor,";
		  		SQLStr+="(case when TotPreg is null then '' else TotPreg end) TotPreg";
		  		SQLStr+=" from PregHis where PNo='"+ PNo +"'";
    		}
    		
    		
	  		Cursor cur=C.ReadData(SQLStr);
  		
	        if(cur.getCount()==0)
	        {
				EntryStatus="Entry";
				Status="IC";	        	
	        	return;
	        }
	        else
	        {
	        	EntryStatus="Update";
	        }
	        
	        cur.moveToFirst();

	        while(!cur.isAfterLast())
	        {        	
	        		Integer i=0;
	        		//Integer page=Integer.valueOf(cur.getString(cur.getColumnIndex("PageNo")));
	        		//Status=cur.getString(cur.getColumnIndex("Status"));
	  				
	        		spnQ103.setSelection(Global.SpinnerItemPosition(spnQ103, 1, cur.getString(cur.getColumnIndex("Visit"))));
	        		/*if(Integer.valueOf(cur.getString(cur.getColumnIndex("Visit")))>3)
	        		{
	        			spnQ103.setSelection(4);
	        		}
	        		else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Visit")))<4)
	        		{
	        			spnQ103.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Visit"))));
	        		}
	        		*/
	        		txtQ104MM.setText(cur.getString(cur.getColumnIndex("MarM")));
	        		txtQ104YY.setText(cur.getString(cur.getColumnIndex("MarY")));
	        		if(cur.getString(cur.getColumnIndex("Births")).equals("1"))
	        		{
	        			rdoYQ105.setChecked(true);
	        			if(cur.getString(cur.getColumnIndex("LiveHh")).equals("1"))
		        		{
	        				rdoYQ106.setChecked(true);
		        			txtQ107A.setText(cur.getString(cur.getColumnIndex("SLiveHh")));
		        			txtQ107B.setText(cur.getString(cur.getColumnIndex("DLiveHh")));
		        		}
	        			else if(cur.getString(cur.getColumnIndex("LiveHh")).equals("2"))
	        			{
	        				rdoNQ106.setChecked(true);
	        			}
	        			if(cur.getString(cur.getColumnIndex("LiveOut")).equals("1"))
		        		{
	        				rdoYQ108.setChecked(true);
		        			txtQ109A.setText(cur.getString(cur.getColumnIndex("SLiveOut")));
		        			txtQ109B.setText(cur.getString(cur.getColumnIndex("DLiveOut")));
		        		}
	        			else if(cur.getString(cur.getColumnIndex("LiveOut")).equals("2"))
	        			{
	        				rdoNQ108.setChecked(true);
	        			}
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Births")).equals("2"))
	        		{
	        			rdoNQ105.setChecked(true);
	        		}
	        			
	        		
	        		if(cur.getString(cur.getColumnIndex("Died")).equals("1"))
	        		{
	        			rdoYQ110.setChecked(true);
	        			txtQ111A.setText(cur.getString(cur.getColumnIndex("SDied")));
	        			txtQ111B.setText(cur.getString(cur.getColumnIndex("DDied")));
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Died")).equals("2"))
	        		{
	        			rdoNQ110.setChecked(true);
	        		}
	        		if(cur.getString(cur.getColumnIndex("Abor")).equals("1"))
	        		{
	        			rdoYQ112.setChecked(true);
	        			txtQ113.setText(cur.getString(cur.getColumnIndex("TAbor")));
	        			
	        		}          	
	        		else if(cur.getString(cur.getColumnIndex("Abor")).equals("2"))
	        		{
	        			rdoNQ112.setChecked(true);
	        		}
	        		txtQ114.setText(cur.getString(cur.getColumnIndex("TotPreg")));        		
	        		cur.moveToNext();  	
	        		if(Status.equals("IC"))
	          		{

	      				return;
	          		}
					else
	          		{

	          			return;
	          		}

	        
	        }		
	                
	        cur.close();
	    
    	}
    	catch(Exception ex)
    	{
    		Connection.MessageBox(reproductive_history_orig.this, "error cur");
    		return;
    	}
    }

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

}