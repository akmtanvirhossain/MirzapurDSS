package data.mirzapurdss;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class ses_orig extends Activity {
		public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.backmenu, menu);
	        return true;
	    }
	
		public boolean onOptionsItemSelected(MenuItem item) {    
	    	switch (item.getItemId()) {    
	    		case R.id.mnuBack:
	    		  AlertDialog.Builder adb = new AlertDialog.Builder(ses_orig.this);
	    		  adb.setTitle("SES");
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
	private String Rnd;
	private String VillageCode;
	private String BariCode;
	private String HHCode;
	private String Household;
	private String SESNo;
	
	Bundle IDbundle;
	
	
		   Spinner spnSESNo;
		   Spinner spnVisit;
		   
	 	   TextView VlblQ015a;
	 	   Spinner spnQ015a;
	       TextView VlblQ015b;
 	       Spinner spnQ015b;
 	       TextView VlblQ015c;
 	       Spinner spnQ015c;
	
 	       TextView VlblQ016a;
 	       RadioGroup rdogrpQ016a;
 	       RadioButton rdoYQ016a;
 	       RadioButton rdoNQ016a;
	       RadioButton rdoDKQ016a;
 	       TextView VlblQ016b;
 	       RadioGroup rdogrpQ016b;
 	       RadioButton rdoYQ016b;
 	       RadioButton rdoNQ016b;
 	       RadioButton rdoDKQ016b;
 	       TextView VlblQ016c;
 	       RadioGroup rdogrpQ016c;
 	       RadioButton rdoYQ016c;
 	       RadioButton rdoNQ016c;
 	       RadioButton rdoDKQ016c;
 	       Spinner spnQ017;
 	       TextView VlblQ018;
 	       RadioGroup rdogrpQ018;
 	       RadioButton rdoYQ018;
 	       RadioButton rdoNQ018;
 	       TextView VlblQ019a;
 	       EditText txtQ019a;
 	       TextView VlblQ019b;
 	       EditText txtQ019b;
 	       TextView VlblQ019c;
 	       EditText txtQ019c;
 	       TextView VlblQ019d;
 	       EditText txtQ019d;
 	       TextView VlblQ019e;
 	       EditText txtQ019e;
 	       TextView VlblQ019f;
 	       EditText txtQ019f;
 	       TextView VlblQ019g;
 	       EditText txtQ019g;
 	       TextView VlblQ019h;
 	       EditText txtQ019h;
 	       TextView VlblQ019i;
 	       EditText txtQ019i;
 	       TextView VlblQ019j;
 	       EditText txtQ019j;
 	       TextView VlblQ019k;
 	       EditText txtQ019k;
 	       TextView VlblQ019l;
 	       EditText txtQ019l;
 	       TextView VlblQ019m;
 	       EditText txtQ019m;
 	       TextView VlblQ019n;
 	       EditText txtQ019n;
 	       TextView VlblQ019o;
 	       EditText txtQ019o;
 	       TextView VlblQ019p;
 	       EditText txtQ019p;
 	       TextView VlblQ019q;
 	       EditText txtQ019q;
 	       TextView VlblQ019r;
 	       EditText txtQ019r;
 	       TextView VlblQ019s;
 	       EditText txtQ019s;
 	       TextView VlblQ019t;
 	       EditText txtQ019t;
 	       TextView VlblQ019u;
 	       EditText txtQ019u;
 	       TextView VlblQ019v;
 	       EditText txtQ019v;
 	       TextView VlblQ019w;
 	       EditText txtQ019w;
 	       TextView VlblQ019x;
 	       EditText txtQ019x;
 	       TextView VlblQ019y;
 	       EditText txtQ019y;
 	       TextView VlblQ019z;
 	       EditText txtQ019z;
 	       TextView VlblQ020a;
 	       EditText txtQ020a;
 	       TextView VlblQ020b;
 	       EditText txtQ020b;
 	       TextView VlblQ020c;
 	       EditText txtQ020c;
 	       TextView VlblQ020d;
 	       EditText txtQ020d;
 	       TextView VlblQ020g;
 	       EditText txtQ020g;
 	       TextView VlblQ020e;
 	       EditText txtQ020e;
 	       TextView VlblQ020f;
 	       EditText txtQ020f;
 	       TextView VlblQ020h;
 	       EditText txtQ020h;
 	      
 	       EditText txtQ021;
 	       TextView VlblQ022a;
 	       Spinner spnQ022a;
 	       TextView VlblQ022b;
	       Spinner spnQ022b;
	       TextView VlblQ022c;
 	       Spinner spnQ022c;

 	       TextView VlblQ023a;
	       EditText txtQ023a;
	       TextView VlblQ023b;
	       EditText txtQ023b;
 	       TextView VlblQ024a;
 	       EditText txtQ024a;
 	       TextView VlblQ024b;
 	       EditText txtQ024b;
 	       TextView VlblQ025a;
 	       EditText txtQ025a;
 	       TextView VlblQ025b;
 	       EditText txtQ025b;
 	       TextView VlblQ026;
 	       RadioGroup rdogrpQ026;
 	       RadioButton rdoYQ026;
 	       RadioButton rdoNQ026;
 	       TextView VlblQ027a;
 	       CheckBox chkQ027a;
 	       TextView VlblQ027b;
 	       CheckBox chkQ027b;
 	       TextView VlblQ027c;
 	       CheckBox chkQ027c;
 	       TextView VlblQ027d;
 	       CheckBox chkQ027d;
 	       TextView VlblQ027e;
 	       CheckBox chkQ027e;
 	       TextView VlblQ027f;
 	       CheckBox chkQ027f;
 	       TextView VlblQ027g;
 	       CheckBox chkQ027g;
 	       TextView VlblQ027h;
 	       CheckBox chkQ027h;
 	       TextView VlblQ027i;
 	       CheckBox chkQ027i;
 	       TextView VlblQ027j;
 	       CheckBox chkQ027j;
 	       TextView VlblQ027y;
 	       CheckBox chkQ027y;
 	       TextView VlblQ027z;
 	       CheckBox chkQ027z;
 	       TextView VlblQ028a;
 	       EditText txtQ028a;
 	       TextView VlblQ028b;
 	       EditText txtQ028b;
 	       TextView VlblQ028c;
 	       EditText txtQ028c;
 	       TextView VlblQ028d;
 	       EditText txtQ028d;
 	       TextView VlblQ028e;
 	       EditText txtQ028e;
 	       TextView VlblQ028y;
 	       EditText txtQ028y;
 	       TextView VlblQ029;
 	       RadioGroup rdogrpQ029;
 	       RadioButton rdoYQ029;
 	       RadioButton rdoNQ029;
 	       RadioButton rdoDKQ029;
 	      
 	       TextView VlblQ030a;
 	       CheckBox chkQ030a;
 	       TextView VlblQ030b;
 	       CheckBox chkQ030b;
 	       TextView VlblQ030c;
 	       CheckBox chkQ030c;
 	       TextView VlblQ030d;
 	       CheckBox chkQ030d;
 	       TextView VlblQ030e;
 	       CheckBox chkQ030e;
 	       TextView VlblQ030f;
 	       CheckBox chkQ030f;
 	       TextView VlblQ030g;
 	       CheckBox chkQ030g;
 	       TextView VlblQ030h;
 	       CheckBox chkQ030h;
 	       TextView VlblQ030z;
 	       CheckBox chkQ030z;
 	       TextView VlblQ031;
 	       Spinner spnQ031;
			
	
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
        setContentView(R.layout.ses_orig);
        C=new Connection(this);
        g=Global.getInstance();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        EntryStatus="Entry";

        IDbundle  = getIntent().getExtras();        
        VillageCode = IDbundle.getString("vill");
        BariCode  = IDbundle.getString("bari");
        HHCode    = IDbundle.getString("hh");
        Household = IDbundle.getString("household");
        Rnd       = IDbundle.getString("rnd");
        
        
         spnSESNo = (Spinner)findViewById(R.id.spnSESNo);
         String SQL="";
         String NewSESNo = "";
         
         if(C.Existence("select vill from tTrans where status='s' and vill||bari||hh='" + Household + "'"))
         {
        	 NewSESNo = C.ReturnSingleValue("select (ifnull(max(sesno),0)+1)SESNo from ttrans where status='s' and vill||bari||hh='"+ Household +"' and cast((julianday(date('now'))-julianday(vdate))/365.25 as int)>=3 order by sesno desc limit 1");
        	 if(NewSESNo.equals("1"))
        		 SQL = "select distinct ifnull(SESNo,'')SESNo from ttrans where status='s' and vill||bari||hh='"+ Household +"' order by SESNo desc";
        	 else
        		 SQL = "Select '"+ NewSESNo +"' SESNo union select distinct ifnull(SESNo,'')SESNo from ttrans where status='s' and vill||bari||hh='"+ Household +"' order by SESNo desc";
         }
         else
         {
        	 SQL = "Select '1'";
         }
         
         //SQL = "Select '1'";
         
         spnSESNo.setAdapter(C.getArrayAdapter(SQL));
         //spnSESNo.setAdapter(C.getArrayAdapter("select ifnull(max(sesno),1)SESNo from ttrans where status='s' and vill||bari||hh='"+ Household +"' order by sesno desc"));

         spnVisit = (Spinner)findViewById(R.id.spnVisit);
         ArrayAdapter adptrVisit = ArrayAdapter.createFromResource(this, R.array.listVisitStatus, android.R.layout.simple_spinner_item);
         adptrVisit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spnVisit.setAdapter(adptrVisit);
           	  
           	  
	 	 VlblQ015a=(TextView) findViewById(R.id.VlblQ015a);
	 	 spnQ015a=(Spinner) findViewById(R.id.spnQ015a);
	     VlblQ015b=(TextView) findViewById(R.id.VlblQ015b);
	     spnQ015b=(Spinner) findViewById(R.id.spnQ015b);
	     VlblQ015c=(TextView) findViewById(R.id.VlblQ015c);
	     spnQ015c=(Spinner) findViewById(R.id.spnQ015c);
	
	     VlblQ016a=(TextView) findViewById(R.id.VlblQ016a);
	     rdogrpQ016a=(RadioGroup) findViewById(R.id.rdogrpQ016a);
	     rdoYQ016a=(RadioButton) findViewById(R.id.rdoYQ016a);
	     rdoNQ016a=(RadioButton) findViewById(R.id.rdoNQ016a);
	     rdoDKQ016a=(RadioButton) findViewById(R.id.rdoDKQ016a);
	     VlblQ016b=(TextView) findViewById(R.id.VlblQ016b);
	        rdogrpQ016b=(RadioGroup) findViewById(R.id.rdogrpQ016b);
	        rdoYQ016b=(RadioButton) findViewById(R.id.rdoYQ016b);
	        rdoNQ016b=(RadioButton) findViewById(R.id.rdoNQ016b);
	        rdoDKQ016b=(RadioButton) findViewById(R.id.rdoDKQ016b);
	        VlblQ016c=(TextView) findViewById(R.id.VlblQ016c);
	        rdogrpQ016c=(RadioGroup) findViewById(R.id.rdogrpQ016c);
	        rdoYQ016c=(RadioButton) findViewById(R.id.rdoYQ016c);
	        rdoNQ016c=(RadioButton) findViewById(R.id.rdoNQ016c);
	        rdoDKQ016c=(RadioButton) findViewById(R.id.rdoDKQ016c);
	        spnQ017=(Spinner) findViewById(R.id.spnQ017);
	        VlblQ018=(TextView) findViewById(R.id.VlblQ018);
	        rdogrpQ018=(RadioGroup) findViewById(R.id.rdogrpQ018);
	        rdoYQ018=(RadioButton) findViewById(R.id.rdoYQ018);
	        rdoNQ018=(RadioButton) findViewById(R.id.rdoNQ018);
	        VlblQ019a=(TextView) findViewById(R.id.VlblQ019a);
	        txtQ019a=(EditText) findViewById(R.id.txtQ019a);
	        VlblQ019b=(TextView) findViewById(R.id.VlblQ019b);
	        txtQ019b=(EditText) findViewById(R.id.txtQ019b);
	        VlblQ019c=(TextView) findViewById(R.id.VlblQ019c);
	        txtQ019c=(EditText) findViewById(R.id.txtQ019c);
	        VlblQ019d=(TextView) findViewById(R.id.VlblQ019d);
	        txtQ019d=(EditText) findViewById(R.id.txtQ019d);
	        VlblQ019e=(TextView) findViewById(R.id.VlblQ019e);
	        txtQ019e=(EditText) findViewById(R.id.txtQ019e);
	        VlblQ019f=(TextView) findViewById(R.id.VlblQ019f);
	        txtQ019f=(EditText) findViewById(R.id.txtQ019f);
	        VlblQ019g=(TextView) findViewById(R.id.VlblQ019g);
	        txtQ019g=(EditText) findViewById(R.id.txtQ019g);
	        VlblQ019h=(TextView) findViewById(R.id.VlblQ019h);
	        txtQ019h=(EditText) findViewById(R.id.txtQ019h);
	        VlblQ019i=(TextView) findViewById(R.id.VlblQ019i);
	        txtQ019i=(EditText) findViewById(R.id.txtQ019i);
	        VlblQ019j=(TextView) findViewById(R.id.VlblQ019j);
	        txtQ019j=(EditText) findViewById(R.id.txtQ019j);
	        VlblQ019k=(TextView) findViewById(R.id.VlblQ019k);
	        txtQ019k=(EditText) findViewById(R.id.txtQ019k);
	        VlblQ019l=(TextView) findViewById(R.id.VlblQ019l);
	        txtQ019l=(EditText) findViewById(R.id.txtQ019l);
	        VlblQ019m=(TextView) findViewById(R.id.VlblQ019m);
	        txtQ019m=(EditText) findViewById(R.id.txtQ019m);
	        VlblQ019n=(TextView) findViewById(R.id.VlblQ019n);
	        txtQ019n=(EditText) findViewById(R.id.txtQ019n);
	        VlblQ019o=(TextView) findViewById(R.id.VlblQ019o);
	        txtQ019o=(EditText) findViewById(R.id.txtQ019o);
	        VlblQ019p=(TextView) findViewById(R.id.VlblQ019p);
	        txtQ019p=(EditText) findViewById(R.id.txtQ019p);
	        VlblQ019q=(TextView) findViewById(R.id.VlblQ019q);
	        txtQ019q=(EditText) findViewById(R.id.txtQ019q);
	        VlblQ019r=(TextView) findViewById(R.id.VlblQ019r);
	        txtQ019r=(EditText) findViewById(R.id.txtQ019r);
	        VlblQ019s=(TextView) findViewById(R.id.VlblQ019s);
	        txtQ019s=(EditText) findViewById(R.id.txtQ019s);
	        VlblQ019t=(TextView) findViewById(R.id.VlblQ019t);
	        txtQ019t=(EditText) findViewById(R.id.txtQ019t);
	        VlblQ019u=(TextView) findViewById(R.id.VlblQ019u);
	        txtQ019u=(EditText) findViewById(R.id.txtQ019u);
	        VlblQ019v=(TextView) findViewById(R.id.VlblQ019v);
	        txtQ019v=(EditText) findViewById(R.id.txtQ019v);
	        VlblQ019w=(TextView) findViewById(R.id.VlblQ019w);
	        txtQ019w=(EditText) findViewById(R.id.txtQ019w);
	        VlblQ019x=(TextView) findViewById(R.id.VlblQ019x);
	        txtQ019x=(EditText) findViewById(R.id.txtQ019x);
	        VlblQ019y=(TextView) findViewById(R.id.VlblQ019y);
	        txtQ019y=(EditText) findViewById(R.id.txtQ019y);
	        VlblQ019z=(TextView) findViewById(R.id.VlblQ019z);
	        txtQ019z=(EditText) findViewById(R.id.txtQ019z);
	        VlblQ020a=(TextView) findViewById(R.id.VlblQ020a);
	        txtQ020a=(EditText) findViewById(R.id.txtQ020a);
	        VlblQ020b=(TextView) findViewById(R.id.VlblQ020b);
	        txtQ020b=(EditText) findViewById(R.id.txtQ020b);
	        VlblQ020c=(TextView) findViewById(R.id.VlblQ020c);
	        txtQ020c=(EditText) findViewById(R.id.txtQ020c);
	        VlblQ020d=(TextView) findViewById(R.id.VlblQ020d);
	        txtQ020d=(EditText) findViewById(R.id.txtQ020d);
	        VlblQ020g=(TextView) findViewById(R.id.VlblQ020g);
	        txtQ020g=(EditText) findViewById(R.id.txtQ020g);
	        VlblQ020e=(TextView) findViewById(R.id.VlblQ020e);
	        txtQ020e=(EditText) findViewById(R.id.txtQ020e);
	        VlblQ020f=(TextView) findViewById(R.id.VlblQ020f);
	        txtQ020f=(EditText) findViewById(R.id.txtQ020f);
	        VlblQ020h=(TextView) findViewById(R.id.VlblQ020h);
	        txtQ020h=(EditText) findViewById(R.id.txtQ020h);
	      
	        txtQ021=(EditText) findViewById(R.id.txtQ021);
	        VlblQ022a=(TextView) findViewById(R.id.VlblQ022a);
	        spnQ022a=(Spinner) findViewById(R.id.spnQ022a);
	        VlblQ022b=(TextView) findViewById(R.id.VlblQ022b);
	        spnQ022b=(Spinner) findViewById(R.id.spnQ022b);
	        VlblQ022c=(TextView) findViewById(R.id.VlblQ022c);
	        spnQ022c=(Spinner) findViewById(R.id.spnQ022c);

	        VlblQ023a=(TextView) findViewById(R.id.VlblQ023a);
	        txtQ023a=(EditText) findViewById(R.id.txtQ023a);
	        VlblQ023b=(TextView) findViewById(R.id.VlblQ023b);
	        txtQ023b=(EditText) findViewById(R.id.txtQ023b);
	        VlblQ024a=(TextView) findViewById(R.id.VlblQ024a);
	        txtQ024a=(EditText) findViewById(R.id.txtQ024a);
	        VlblQ024b=(TextView) findViewById(R.id.VlblQ024b);
	        txtQ024b=(EditText) findViewById(R.id.txtQ024b);
	        VlblQ025a=(TextView) findViewById(R.id.VlblQ025a);
	        txtQ025a=(EditText) findViewById(R.id.txtQ025a);
	        VlblQ025b=(TextView) findViewById(R.id.VlblQ025b);
	        txtQ025b=(EditText) findViewById(R.id.txtQ025b);
	        VlblQ026=(TextView) findViewById(R.id.VlblQ026);
	        rdogrpQ026=(RadioGroup) findViewById(R.id.rdogrpQ026);
	       rdoYQ026=(RadioButton) findViewById(R.id.rdoYQ026);
	        rdoNQ026=(RadioButton) findViewById(R.id.rdoNQ026);
	        VlblQ027a=(TextView) findViewById(R.id.VlblQ027a);
	        chkQ027a=(CheckBox) findViewById(R.id.chkQ027a);
	        VlblQ027b=(TextView) findViewById(R.id.VlblQ027b);
	        chkQ027b=(CheckBox) findViewById(R.id.chkQ027b);
	        VlblQ027c=(TextView) findViewById(R.id.VlblQ027c);
	        chkQ027c=(CheckBox) findViewById(R.id.chkQ027c);
	        VlblQ027d=(TextView) findViewById(R.id.VlblQ027d);
	        chkQ027d=(CheckBox) findViewById(R.id.chkQ027d);
	        VlblQ027e=(TextView) findViewById(R.id.VlblQ027e);
	        chkQ027e=(CheckBox) findViewById(R.id.chkQ027e);
	        VlblQ027f=(TextView) findViewById(R.id.VlblQ027f);
	        chkQ027f=(CheckBox) findViewById(R.id.chkQ027f);
	        VlblQ027g=(TextView) findViewById(R.id.VlblQ027g);
	        chkQ027g=(CheckBox) findViewById(R.id.chkQ027g);
	        VlblQ027h=(TextView) findViewById(R.id.VlblQ027h);
	        chkQ027h=(CheckBox) findViewById(R.id.chkQ027h);
	        VlblQ027i=(TextView) findViewById(R.id.VlblQ027i);
	        chkQ027i=(CheckBox) findViewById(R.id.chkQ027i);
	        VlblQ027j=(TextView) findViewById(R.id.VlblQ027j);
	        chkQ027j=(CheckBox) findViewById(R.id.chkQ027j);
	        VlblQ027y=(TextView) findViewById(R.id.VlblQ027y);
	        chkQ027y=(CheckBox) findViewById(R.id.chkQ027y);
	        VlblQ027z=(TextView) findViewById(R.id.VlblQ027z);
	        chkQ027z=(CheckBox) findViewById(R.id.chkQ027z);
	        VlblQ028a=(TextView) findViewById(R.id.VlblQ028a);
	        txtQ028a=(EditText) findViewById(R.id.txtQ028a);
	        VlblQ028b=(TextView) findViewById(R.id.VlblQ028b);
	        txtQ028b=(EditText) findViewById(R.id.txtQ028b);
	        VlblQ028c=(TextView) findViewById(R.id.VlblQ028c);
	        txtQ028c=(EditText) findViewById(R.id.txtQ028c);
	        VlblQ028d=(TextView) findViewById(R.id.VlblQ028d);
	        txtQ028d=(EditText) findViewById(R.id.txtQ028d);
	        VlblQ028e=(TextView) findViewById(R.id.VlblQ028e);
	        txtQ028e=(EditText) findViewById(R.id.txtQ028e);
	        VlblQ028y=(TextView) findViewById(R.id.VlblQ028y);
	        txtQ028y=(EditText) findViewById(R.id.txtQ028y);
	        VlblQ029=(TextView) findViewById(R.id.VlblQ029);
	        rdogrpQ029=(RadioGroup) findViewById(R.id.rdogrpQ029);
	        rdoYQ029=(RadioButton) findViewById(R.id.rdoYQ029);
	        rdoNQ029=(RadioButton) findViewById(R.id.rdoNQ029);
	        rdoDKQ029=(RadioButton) findViewById(R.id.rdoDKQ029);
	      
	        VlblQ030a=(TextView) findViewById(R.id.VlblQ030a);
	        chkQ030a=(CheckBox) findViewById(R.id.chkQ030a);
	        VlblQ030b=(TextView) findViewById(R.id.VlblQ030b);
	        chkQ030b=(CheckBox) findViewById(R.id.chkQ030b);
	        VlblQ030c=(TextView) findViewById(R.id.VlblQ030c);
	        chkQ030c=(CheckBox) findViewById(R.id.chkQ030c);
	        VlblQ030d=(TextView) findViewById(R.id.VlblQ030d);
	        chkQ030d=(CheckBox) findViewById(R.id.chkQ030d);
	        VlblQ030e=(TextView) findViewById(R.id.VlblQ030e);
	        chkQ030e=(CheckBox) findViewById(R.id.chkQ030e);
	        VlblQ030f=(TextView) findViewById(R.id.VlblQ030f);
	        chkQ030f=(CheckBox) findViewById(R.id.chkQ030f);
	        VlblQ030g=(TextView) findViewById(R.id.VlblQ030g);
	        chkQ030g=(CheckBox) findViewById(R.id.chkQ030g);
	        VlblQ030h=(TextView) findViewById(R.id.VlblQ030h);
	        chkQ030h=(CheckBox) findViewById(R.id.chkQ030h);
	        VlblQ030z=(TextView) findViewById(R.id.VlblQ030z);
	        chkQ030z=(CheckBox) findViewById(R.id.chkQ030z);
	        VlblQ031=(TextView) findViewById(R.id.VlblQ031);
	        spnQ031=(Spinner) findViewById(R.id.spnQ031);

      
          ArrayAdapter adptrQ015a=ArrayAdapter.createFromResource(
    	  this, R.array.listQ015a, android.R.layout.simple_spinner_item);
    	  adptrQ015a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	  spnQ015a.setAdapter(adptrQ015a);

  	      ArrayAdapter adptrQ015b=ArrayAdapter.createFromResource(this, R.array.listQ015a, android.R.layout.simple_spinner_item);
  	      adptrQ015b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	      spnQ015b.setAdapter(adptrQ015b);

  	      ArrayAdapter adptrQ015c=ArrayAdapter.createFromResource(this, R.array.listQ015a, android.R.layout.simple_spinner_item);
  	      adptrQ015c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	      spnQ015c.setAdapter(adptrQ015c);

  	      ArrayAdapter adptrQ017=ArrayAdapter.createFromResource(this, R.array.listQ017, android.R.layout.simple_spinner_item);
	      adptrQ017.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      spnQ017.setAdapter(adptrQ017);

  	      ArrayAdapter adptrQ022a=ArrayAdapter.createFromResource(this, R.array.listQ022a, android.R.layout.simple_spinner_item);
  	      adptrQ022a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	      spnQ022a.setAdapter(adptrQ022a);

  	      ArrayAdapter adptrQ022b=ArrayAdapter.createFromResource(this, R.array.listQ022a, android.R.layout.simple_spinner_item);
  	      adptrQ022b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	      spnQ022b.setAdapter(adptrQ022b);

  	      ArrayAdapter adptrQ022c=ArrayAdapter.createFromResource(this, R.array.listQ022a, android.R.layout.simple_spinner_item);
  	      adptrQ022c.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	      spnQ022c.setAdapter(adptrQ022c);
  	      
  	    ArrayAdapter adptrQ031=ArrayAdapter.createFromResource(this, R.array.listQ031, android.R.layout.simple_spinner_item);
	      adptrQ031.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      spnQ031.setAdapter(adptrQ031);

  	     rdogrpQ026.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { 
  			public void onCheckedChanged(RadioGroup rg, int checkedId) { 
  			if(R.id.rdoYQ026== checkedId)
  			{

  				chkQ027a.setEnabled(true);
  				chkQ027b.setEnabled(true);
  				chkQ027c.setEnabled(true);
  				chkQ027d.setEnabled(true);
  				chkQ027e.setEnabled(true);
  				chkQ027f.setEnabled(true);
  				chkQ027g.setEnabled(true);
  				chkQ027h.setEnabled(true);
  				chkQ027i.setEnabled(true);
  				chkQ027j.setEnabled(true);
  				chkQ027y.setEnabled(true);
  				chkQ027z.setEnabled(true);
  				txtQ028a.setEnabled(true);
  				txtQ028b.setEnabled(true);
  				txtQ028c.setEnabled(true);
  				txtQ028d.setEnabled(true);
  				txtQ028e.setEnabled(true);
  				txtQ028y.setEnabled(true);
  				rdoYQ029.setEnabled(true);
  				rdoNQ029.setEnabled(true);
  				rdoDKQ029.setEnabled(true);
  				VlblQ027a.setTextColor(Color.BLACK);
  				VlblQ027b.setTextColor(Color.BLACK);
  				VlblQ027c.setTextColor(Color.BLACK);
  				VlblQ027d.setTextColor(Color.BLACK);
  				VlblQ027e.setTextColor(Color.BLACK);
  				VlblQ027f.setTextColor(Color.BLACK);
  				VlblQ027g.setTextColor(Color.BLACK);
  				VlblQ027h.setTextColor(Color.BLACK);
  				VlblQ027i.setTextColor(Color.BLACK);
  				VlblQ027j.setTextColor(Color.BLACK);
  				VlblQ027y.setTextColor(Color.BLACK);
  				VlblQ027z.setTextColor(Color.BLACK);
  				VlblQ028a.setTextColor(Color.BLACK);
  				VlblQ028b.setTextColor(Color.BLACK);
  				VlblQ028c.setTextColor(Color.BLACK);
  				VlblQ028d.setTextColor(Color.BLACK);
  				VlblQ028e.setTextColor(Color.BLACK);
  				VlblQ028y.setTextColor(Color.BLACK);
  				VlblQ029.setTextColor(Color.BLACK);
  			} 
  			else if(R.id.rdoNQ026 == checkedId) 
  			{ 
  				chkQ027a.setChecked(false);
  				chkQ027b.setChecked(false);
  				chkQ027c.setChecked(false);
  				chkQ027d.setChecked(false);
  				chkQ027e.setChecked(false);
  				chkQ027f.setChecked(false);
  				chkQ027g.setChecked(false);
  				chkQ027h.setChecked(false);
  				chkQ027i.setChecked(false);
  				chkQ027j.setChecked(false);
  				chkQ027y.setChecked(false);
  				chkQ027z.setChecked(false);
  				txtQ028a.setText(null);
  				txtQ028b.setText(null);
  				txtQ028c.setText(null);
  				txtQ028d.setText(null);
  				txtQ028e.setText(null);
  				txtQ028y.setText(null);
  				rdoYQ029.setChecked(false);
  				rdoNQ029.setChecked(false);
  				rdoDKQ029.setChecked(false);
  				
  				chkQ027a.setEnabled(false);
  				chkQ027b.setEnabled(false);
  				chkQ027c.setEnabled(false);
  				chkQ027d.setEnabled(false);
  				chkQ027e.setEnabled(false);
  				chkQ027f.setEnabled(false);
  				chkQ027g.setEnabled(false);
  				chkQ027h.setEnabled(false);
  				chkQ027i.setEnabled(false);
  				chkQ027j.setEnabled(false);
  				chkQ027y.setEnabled(false);
  				chkQ027z.setEnabled(false);
  				txtQ028a.setEnabled(false);
  				txtQ028b.setEnabled(false);
  				txtQ028c.setEnabled(false);
  				txtQ028d.setEnabled(false);
  				txtQ028e.setEnabled(false);
  				txtQ028y.setEnabled(false);
  				rdoYQ029.setEnabled(false);
  				rdoNQ029.setEnabled(false);
  				rdoDKQ029.setEnabled(false);
  				VlblQ027a.setTextColor(Color.GRAY);
  				VlblQ027b.setTextColor(Color.GRAY);
  				VlblQ027c.setTextColor(Color.GRAY);
  				VlblQ027d.setTextColor(Color.GRAY);
  				VlblQ027e.setTextColor(Color.GRAY);
  				VlblQ027f.setTextColor(Color.GRAY);
  				VlblQ027g.setTextColor(Color.GRAY);
  				VlblQ027h.setTextColor(Color.GRAY);
  				VlblQ027i.setTextColor(Color.GRAY);
  				VlblQ027j.setTextColor(Color.GRAY);
  				VlblQ027y.setTextColor(Color.GRAY);
  				VlblQ027z.setTextColor(Color.GRAY);
  				VlblQ028a.setTextColor(Color.GRAY);
  				VlblQ028b.setTextColor(Color.GRAY);
  				VlblQ028c.setTextColor(Color.GRAY);
  				VlblQ028d.setTextColor(Color.GRAY);
  				VlblQ028e.setTextColor(Color.GRAY);
  				VlblQ028y.setTextColor(Color.GRAY);
  				VlblQ029.setTextColor(Color.GRAY);
  			} 
  		}});
			

			
			Button btnsesSaveContinue=(Button)findViewById(R.id.cmdSESSaveandContinue);
			btnsesSaveContinue.setOnClickListener(new View.OnClickListener() {
	  			public void onClick(View view) {

					String SQL = "";
					int i = 0;

					boolean available = false;

					try {
						//3-Absent, 7-Incomplete
						if (Global.Left(spnVisit.getSelectedItem().toString(), 1).equals("3") | Global.Left(spnVisit.getSelectedItem().toString(), 1).equals("7")) {
							SQL = "Select Vill from tTrans Where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							available = C.Existence(SQL);
							try {
								if (!available) {
									SQL = "Insert into tTrans";
									SQL += "(Status,Vill, Bari, HH,SESNo,Visit,VDate,Rnd,PageNo,Upload,Lat,Lon) values(";
									SQL += "'s',";
									SQL += "'" + VillageCode + "',";
									SQL += "'" + BariCode + "',";
									SQL += "'" + HHCode + "',";
									SQL += "'" + spnSESNo.getSelectedItem().toString() + "',";
									SQL += "'" + Global.Left(spnVisit.getSelectedItem().toString(), 1) + "',";
									SQL += "'" + g.CurrentDateYYYYMMDD + "',";
									SQL += "'" + g.getRoundNumber() + "',";
									SQL += "'0','2','','')";

									C.Save(SQL);
								} else {
									SQL = "Update tTrans set Visit='" + Global.Left(spnVisit.getSelectedItem().toString(), 1) + "' where ";
									SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
									SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
									C.Save(SQL);
								}
							} catch (Exception ex) {
								Connection.MessageBox(ses_orig.this, ex.getMessage());
								return;
							}

							finish();
						}



						available = false;

						//for 1-completed visit
						String VQ015A = "", VQ015B = "", VQ015C = "", VQ016A = "", VQ016B = "", VQ016C = "";

						if (spnQ015a.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় ধোয়া-মোছার জন্য কোথাকার পানি ব্যবহার করা হয় তথ্য খালি রাখা যাবে না ");
							return;
						} else if (spnQ015a.getSelectedItemPosition() == 1) {
							VQ015A = "11";
						} else if (spnQ015a.getSelectedItemPosition() == 2) {
							VQ015A = "12";
						} else if (spnQ015a.getSelectedItemPosition() == 3) {
							VQ015A = "21";
						} else if (spnQ015a.getSelectedItemPosition() == 4) {
							VQ015A = "22";
						} else if (spnQ015a.getSelectedItemPosition() == 5) {
							VQ015A = "23";
						} else if (spnQ015a.getSelectedItemPosition() == 6) {
							VQ015A = "31";
						} else if (spnQ015a.getSelectedItemPosition() == 7) {
							VQ015A = "32";
						} else if (spnQ015a.getSelectedItemPosition() == 8) {
							VQ015A = "41";
						} else if (spnQ015a.getSelectedItemPosition() == 9) {
							VQ015A = "51";
						} else if (spnQ015a.getSelectedItemPosition() == 10) {
							VQ015A = "61";
						} else if (spnQ015a.getSelectedItemPosition() == 11) {
							VQ015A = "77";
						}
						if (spnQ015b.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় গোসলের জন্য কোথাকার পানি ব্যবহার করা হয় তথ্য খালি রাখা যাবে না");
							return;
						} else if (spnQ015b.getSelectedItemPosition() == 1) {
							VQ015B = "11";
						} else if (spnQ015b.getSelectedItemPosition() == 2) {
							VQ015B = "12";
						} else if (spnQ015b.getSelectedItemPosition() == 3) {
							VQ015B = "21";
						} else if (spnQ015b.getSelectedItemPosition() == 4) {
							VQ015B = "22";
						} else if (spnQ015b.getSelectedItemPosition() == 5) {
							VQ015B = "23";
						} else if (spnQ015b.getSelectedItemPosition() == 6) {
							VQ015B = "31";
						} else if (spnQ015b.getSelectedItemPosition() == 7) {
							VQ015B = "32";
						} else if (spnQ015b.getSelectedItemPosition() == 8) {
							VQ015B = "41";
						} else if (spnQ015b.getSelectedItemPosition() == 9) {
							VQ015B = "51";
						} else if (spnQ015b.getSelectedItemPosition() == 10) {
							VQ015B = "61";
						} else if (spnQ015b.getSelectedItemPosition() == 11) {
							VQ016B = "77";
						}
						if (spnQ015c.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় খাবার জন্য কোথাকার পানি ব্যবহার করা হয় তথ্য খালি রাখা যাবে না ");
							return;
						} else if (spnQ015c.getSelectedItemPosition() == 1) {
							VQ015C = "11";
						} else if (spnQ015c.getSelectedItemPosition() == 2) {
							VQ015C = "12";
						} else if (spnQ015c.getSelectedItemPosition() == 3) {
							VQ015C = "21";
						} else if (spnQ015c.getSelectedItemPosition() == 4) {
							VQ015C = "22";
						} else if (spnQ015c.getSelectedItemPosition() == 5) {
							VQ015C = "23";
						} else if (spnQ015c.getSelectedItemPosition() == 6) {
							VQ015C = "31";
						} else if (spnQ015c.getSelectedItemPosition() == 7) {
							VQ015C = "32";
						} else if (spnQ015c.getSelectedItemPosition() == 8) {
							VQ015C = "41";
						} else if (spnQ015c.getSelectedItemPosition() == 9) {
							VQ015C = "51";
						} else if (spnQ015c.getSelectedItemPosition() == 10) {
							VQ015C = "61";
						} else if (spnQ015c.getSelectedItemPosition() == 11) {
							VQ015C = "77";
						}

						if (!rdoYQ016a.isChecked() && !rdoNQ016a.isChecked() && !rdoDKQ016a.isChecked()) {
							Connection.MessageBox(ses_orig.this, "অন্য কোন খানার সদস্য এই পানি  ধোয়া-মোছার জন্য ব্যবহার  করে কি না তথ্য খালি রাখা যাবে না ");
							return;
						}

						if (!rdoYQ016b.isChecked() && !rdoNQ016b.isChecked() && !rdoDKQ016b.isChecked()) {
							Connection.MessageBox(ses_orig.this, "অন্য কোন খানার সদস্য এই পানি  গোসলের জন্য ব্যবহার  করে কি না তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (!rdoYQ016c.isChecked() && !rdoNQ016c.isChecked() && !rdoDKQ016c.isChecked()) {
							Connection.MessageBox(ses_orig.this, "অন্য কোন খানার সদস্য এই পানি  খাবার  জন্য ব্যবহার  করে কি না তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (rdoYQ016a.isChecked()) {
							VQ016A = "1";
						} else if (rdoNQ016a.isChecked()) {
							VQ016A = "2";
						} else if (rdoDKQ016a.isChecked()) {
							VQ016A = "9";
						}
						if (rdoYQ016b.isChecked()) {
							VQ016B = "1";
						} else if (rdoNQ016b.isChecked()) {
							VQ016B = "2";
						} else if (rdoDKQ016b.isChecked()) {
							VQ016B = "9";
						}
						if (rdoYQ016c.isChecked()) {
							VQ016C = "1";
						} else if (rdoNQ016c.isChecked()) {
							VQ016C = "2";
						} else if (rdoDKQ016c.isChecked()) {
							VQ016C = "9";
						}


						SQL = "Select Vill from tTrans Where ";
						SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
						SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

						available = C.Existence(SQL);
						try {
							if (!available) {
								SQL = "Insert into tTrans";
								SQL += "(Status,Vill, Bari, HH,SESNo,Visit,Q015a, Q015b, Q015c, Q016a, Q016b, Q016c,VDate,Rnd,PageNo,Upload,Lat,Lon) values(";
								SQL += "'s',";
								SQL += "'" + VillageCode + "',";
								SQL += "'" + BariCode + "',";
								SQL += "'" + HHCode + "',";
								SQL += "'" + spnSESNo.getSelectedItem().toString() + "',";
								SQL += "'" + Global.Left(spnVisit.getSelectedItem().toString(), 1) + "',";
								SQL += "'" + VQ015A + "','" + VQ015B + "','" + VQ015C + "','" + VQ016A + "','" + VQ016B + "','" + VQ016C + "',";
								SQL += "'" + g.CurrentDateYYYYMMDD + "',";
								SQL += "'" + g.getRoundNumber() + "',";
								SQL += "'0','2','','')";

								C.Save(SQL);
							} else {
								SQL = "Update tTrans set Visit='" + Global.Left(spnVisit.getSelectedItem().toString(), 1) + "', Q015a='" + VQ015A + "',Q015b='" + VQ015B + "',Q015c='" + VQ015C + "',Q016a='" + VQ016A + "',Q016b='" + VQ016B + "',Q016c='" + VQ016C + "' where ";
								SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
								SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
								C.Save(SQL);
							}
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}


						String VQ017 = "", VQ018 = "";
						if (spnQ017.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় পায়খানা করার কি বাবস্থা আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (spnQ017.getSelectedItemPosition() == 1) {
							VQ017 = "11";
						} else if (spnQ017.getSelectedItemPosition() == 2) {
							VQ017 = "21";
						} else if (spnQ017.getSelectedItemPosition() == 3) {
							VQ017 = "22";
						} else if (spnQ017.getSelectedItemPosition() == 4) {
							VQ017 = "23";
						} else if (spnQ017.getSelectedItemPosition() == 5) {
							VQ017 = "24";
						} else if (spnQ017.getSelectedItemPosition() == 6) {
							VQ017 = "31";
						} else if (spnQ017.getSelectedItemPosition() == 7) {
							VQ017 = "77";
						}
						if (!rdoYQ018.isChecked() && !rdoNQ018.isChecked()) {
							Connection.MessageBox(ses_orig.this, "আপনার খানায় বিদ্যুৎ আছে কি না তথ্য খালি রাখা যাবে না");
							return;
						}
						if (rdoYQ018.isChecked()) {
							VQ018 = "1";
						} else {
							VQ018 = "2";
						}

						try {
							SQL = "Update tTrans set Q017='" + VQ017 + "',Q018='" + VQ018 + "',PageNo='1' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ019a.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা আলমারি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019b.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা টেবিল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019c.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ব্রেঞ্ছ/চেয়ার আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019d.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ডাইনিং টেবিল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019e.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা সোফা আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						try {
							SQL = "Update tTrans set Q019a='" + txtQ019a.getText() + "', Q019b='" + txtQ019b.getText() + "', Q019c='" + txtQ019c.getText() + "', Q019d='" + txtQ019d.getText() + "', Q019e='" + txtQ019e.getText() + "',PageNo='2' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ019f.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা খাট/চৌকি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019g.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা আলনা আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019h.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা লেপ/কম্বল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019i.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা তোষক/জাযিম/ফোম আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019j.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা সেলাই মেশিন আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						try {
							SQL = "Update tTrans set Q019f='" + txtQ019f.getText() + "', Q019g='" + txtQ019g.getText() + "', Q019h='" + txtQ019h.getText() + "', Q019i='" + txtQ019i.getText() + "', Q019j='" + txtQ019j.getText() + "',PageNo='3' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ019k.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা হাত ঘড়ি/দেয়াল ঘড়ি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019l.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা রেডিও আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019m.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা  টেলিভিশন আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019n.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ফ্রিজ আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019o.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা  বৈদ্যুতিক ফ্যান আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						try {
							SQL = "Update tTrans set Q019k='" + txtQ019k.getText() + "', Q019l='" + txtQ019l.getText() + "', Q019m='" + txtQ019m.getText() + "', Q019n='" + txtQ019n.getText() + "', Q019o='" + txtQ019o.getText() + "',PageNo='4' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						//stop: 06 mar 2014
						//if(txtQ019p.getText().length()==0)
						//{
						//	Connection.MessageBox(ses_orig.this, "খানায় কয়টা বৈদ্যুতিক লাইট আছে তথ্য খালি রাখা যাবে না ");
						//	return;
						//}
						if (txtQ019q.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা কুপি/হারিকেন আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019r.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা মোটর সাইকেল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019s.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা টেলিফোন আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019t.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা মোবাইল ফোন আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						try {
							SQL = "Update tTrans set Q019p='', Q019q='" + txtQ019q.getText() + "', Q019r='" + txtQ019r.getText() + "', Q019s='" + txtQ019s.getText() + "', Q019t='" + txtQ019t.getText() + "',PageNo='5' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ019u.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা সাইকেল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019v.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা গাড়ী/মাইক্রোবাস/ভ্যান আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019w.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা রিকশা/রিকশা ভ্যান আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019x.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা নৌকা আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019y.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা যন্ত্রচালিত নৌকা আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ019z.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা কলের লাঙ্গল আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						try {
							SQL = "Update tTrans set Q019u='" + txtQ019u.getText() + "', Q019v='" + txtQ019v.getText() + "', Q019w='" + txtQ019w.getText() + "', Q019x='" + txtQ019x.getText() + "', Q019y='" + txtQ019y.getText() + "', Q019z='" + txtQ019z.getText() + "',PageNo='6' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ020a.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা গরু আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020a.getText().toString()) > 88 & Integer.parseInt(txtQ020a.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  গরুর বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020b.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা মহিষ আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020b.getText().toString()) > 88 & Integer.parseInt(txtQ020b.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  মহিষের বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020c.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ছাগল আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020c.getText().toString()) > 88 & Integer.parseInt(txtQ020c.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  ছাগলের বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020d.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ভেড়া আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020d.getText().toString()) > 88 & Integer.parseInt(txtQ020d.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  ভেড়ার বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020e.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা মুরগী আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020e.getText().toString()) > 88 & Integer.parseInt(txtQ020e.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  মুরগীর বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020f.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা হাঁস আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020f.getText().toString()) > 88 & Integer.parseInt(txtQ020f.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  হাঁসের বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020g.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা কবুতর আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020g.getText().toString()) > 88 & Integer.parseInt(txtQ020g.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  কবুতরের বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						if (txtQ020h.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা ঘোড়া আছে তথ্য খালি রাখা যাবে না ");
							return;
						} else if (Integer.parseInt(txtQ020h.getText().toString()) > 88 & Integer.parseInt(txtQ020h.getText().toString()) < 99) {
							Connection.MessageBox(ses_orig.this, "খানায় ৮৮ টি  ঘোড়ার বেশী হলে ৮৮ রেকর্ড করুন");
							return;
						}
						try {
							SQL = "Update tTrans set Q020a='" + txtQ020a.getText() + "', Q020b='" + txtQ020b.getText() + "', Q020c='" + txtQ020c.getText() + "', Q020d='" + txtQ020d.getText() + "', Q020e='" + txtQ020e.getText() + "', Q020f='" + txtQ020f.getText() + "', Q020g='" + txtQ020g.getText() + "', Q020h='" + txtQ020h.getText() + "',PageNo='7' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ021.getText().length() == 0 || Integer.parseInt(txtQ021.getText().toString()) > 30) {
							Connection.MessageBox(ses_orig.this, "খানায় কয়টা রুম আছে তথ্য খালি রাখা  বা ৩০ এর বেশী হতে পারে না ");
							return;
						}
						if (spnQ022a.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "প্রধান বসত ঘরের চালের নির্মাণ সামগ্রী কোনটা তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (spnQ022b.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "প্রধান বসত ঘরের দেওয়ালের নির্মাণ সামগ্রী কোনটা তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (spnQ022c.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "প্রধান বসত ঘরের মেঝের নির্মাণ সামগ্রী কোনটা তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (spnQ022a.getSelectedItemPosition() == 6 && spnQ022b.getSelectedItemPosition() != 6) {
							Connection.MessageBox(ses_orig.this, "প্রধান বসত ঘরের চালের নির্মাণ সামগ্রী সিমেন্ট/ইট/ বালি তাই  ঘরের মেঝের নির্মাণ সামগ্রী সিমেন্ট/ইট দিতে হবে");
							return;
						}
						try {
							SQL = "Update tTrans set Q021='" + txtQ021.getText() + "', Q022a='" + String.valueOf(spnQ022a.getSelectedItemPosition()) + "', Q022b='" + String.valueOf(spnQ022b.getSelectedItemPosition()) + "', Q022c='" + String.valueOf(spnQ022c.getSelectedItemPosition()) + "',PageNo='8' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (txtQ023a.getText().length() == 0 || txtQ023b.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় বসত ভিটার জন্য কতটুক  জমি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ024a.getText().length() == 0 || txtQ024b.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় চাষের জন্য কতটুক  জমি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}
						if (txtQ025a.getText().length() == 0 || txtQ025b.getText().length() == 0) {
							Connection.MessageBox(ses_orig.this, "খানায় বসত ভিটা ও চাষের জমি ছাড়া কতটুক  জমি আছে তথ্য খালি রাখা যাবে না ");
							return;
						}

						try {
							SQL = "Update tTrans set Q023a='" + txtQ023a.getText() + "', Q023b='" + txtQ023b.getText() + "', Q024a='" + txtQ024a.getText() + "', Q024b='" + txtQ024b.getText() + "',Q025a='" + txtQ025a.getText() + "', Q025b='" + txtQ025b.getText() + "',PageNo='9' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (!rdoYQ026.isChecked() && !rdoNQ026.isChecked()) {
							Connection.MessageBox(ses_orig.this, "সদস্যের কোন আত্মীয়স্বজন বিদেশে থাকে/ কাজ করে কি না তথ্য খালি রাখা যাবে না ");
							return;
						}
						String VQ026 = "";
						if (rdoYQ026.isChecked()) {
							VQ026 = "1";
						} else {
							VQ026 = "2";
						}

						try {
							if (rdoYQ026.isChecked()) {
								String V27A = "", V27B = "", V27C = "", V27D = "", V27E = "";
								if (chkQ027a.isChecked()) {
									V27A = "1";
								} else {
									V27A = "2";
								}
								if (chkQ027b.isChecked()) {
									V27B = "1";
								} else {
									V27B = "2";
								}
								if (chkQ027c.isChecked()) {
									V27C = "1";
								} else {
									V27C = "2";
								}
								if (chkQ027d.isChecked()) {
									V27D = "1";
								} else {
									V27D = "2";
								}
								if (chkQ027e.isChecked()) {
									V27E = "1";
								} else {
									V27E = "2";
								}

								SQL = "Update tTrans set Q026='" + VQ026 + "', Q027a='" + V27A + "', Q027b='" + V27B + "', Q027c='" + V27C + "', Q027d='" + V27D + "', Q027e='" + V27E + "',PageNo='10' where ";
								SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
								SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";

								C.Save(SQL);

							} else {
								SQL = "Update tTrans set Q026='" + VQ026 + "', Q027a='', Q027b='', Q027c='', Q027d='', Q027e='', Q027f='', Q027g='', Q027h='', Q027i='', Q027j='', Q027y='', Q027z='', Q028a='', Q028b='', Q028c='', Q028d='', Q028e='', Q028y='', Q029='',PageNo='10' where ";
								SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
								SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
								C.Save(SQL);

							}
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if (rdoYQ026.isChecked()) {
							if ((chkQ027z.isChecked()) && (chkQ027a.isChecked() || chkQ027b.isChecked() || chkQ027c.isChecked() || chkQ027d.isChecked() || chkQ027e.isChecked() || chkQ027f.isChecked() || chkQ027g.isChecked() || chkQ027h.isChecked() || chkQ027i.isChecked() || chkQ027j.isChecked() || chkQ027y.isChecked())) {
								Connection.MessageBox(ses_orig.this, "খানা প্রধানের সাথে বিদেশ অবস্থান কারী আত্মীয়ের সম্পক আছে তাই জানা নাই টিক হবে না");
								return;
							} else if (!chkQ027z.isChecked() && !chkQ027a.isChecked() && !chkQ027b.isChecked() && !chkQ027c.isChecked() && !chkQ027d.isChecked() && !chkQ027e.isChecked() && !chkQ027f.isChecked() && !chkQ027g.isChecked() && !chkQ027h.isChecked() && !chkQ027i.isChecked() && !chkQ027j.isChecked() && !chkQ027y.isChecked()) {
								Connection.MessageBox(ses_orig.this, "খানা প্রধানের সাথে বিদেশ অবস্থান কারী আত্মীয়ের সম্পক কি তা জানা নাই, তাই জানা নাই টিক হবে ");
								return;
							}
							String V27F = "", V27G = "", V27H = "", V27I = "", V27J = "", V27Y = "", V27Z = "";
							if (chkQ027f.isChecked()) {
								V27F = "1";
							} else {
								V27F = "2";
							}
							if (chkQ027g.isChecked()) {
								V27G = "1";
							} else {
								V27G = "2";
							}
							if (chkQ027h.isChecked()) {
								V27H = "1";
							} else {
								V27H = "2";
							}
							if (chkQ027i.isChecked()) {
								V27I = "1";
							} else {
								V27I = "2";
							}
							if (chkQ027j.isChecked()) {
								V27J = "1";
							} else {
								V27J = "2";
							}
							if (chkQ027y.isChecked()) {
								V27Y = "1";
							} else {
								V27Y = "2";
							}
							if (chkQ027z.isChecked()) {
								V27Z = "1";
							} else {
								V27Z = "2";
							}

							try {
								SQL = "Update tTrans set  Q027f='" + V27F + "', Q027g='" + V27G + "', Q027h='" + V27H + "', Q027i='" + V27I + "', Q027j='" + V27J + "', Q027y='" + V27Y + "', Q027z='" + V27Z + "',PageNo='11' where ";
								SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
								SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
								C.Save(SQL);
							} catch (Exception ex) {
								Connection.MessageBox(ses_orig.this, ex.getMessage());
								return;
							}

						}


						if (rdoYQ026.isChecked()) {
							if (txtQ028a.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয় যুক্তরাজ্য/লন্ডন/ইউরপে থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (txtQ028b.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয় উত্তর আমেরিকায় থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (txtQ028c.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয় অস্ট্রেলিয়া/নিউজিল্যান্ড থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (txtQ028d.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয় মধ্যপ্রাচ্য থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (txtQ028e.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয়  মালয়েশিয়া/সিঙ্গাপুর/ব্রুনাই থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (txtQ028y.getText().length() == 0) {
								Connection.MessageBox(ses_orig.this, "সদস্যের কতজন আত্মীয় অন্যান্য জায়গাই থাকে তথ্য খালি রাখা যাবে না ");
								return;
							}
							String V29 = "";
							if (!rdoYQ029.isChecked() & !rdoNQ029.isChecked() && !rdoDKQ029.isChecked()) {
								Connection.MessageBox(ses_orig.this, "যে সকল আত্মীয়স্বজন দেশের বাইরে থাকে তারা টাকা পাঠাই কি  না তথ্য খালি রাখা যাবে না ");
								return;
							}
							if (rdoYQ029.isChecked()) {
								V29 = "1";
							} else if (rdoNQ029.isChecked()) {
								V29 = "2";
							} else {
								V29 = "9";
							}

							try {
								SQL = "Update tTrans set  Q028a='" + txtQ028a.getText() + "',Q028b='" + txtQ028b.getText() + "',Q028c='" + txtQ028c.getText() + "',Q028d='" + txtQ028d.getText() + "',Q028e='" + txtQ028e.getText() + "',Q028y='" + txtQ028y.getText() + "',Q029='" + V29 + "',PageNo='12' where ";
								SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
								SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
								C.Save(SQL);
							} catch (Exception ex) {
								Connection.MessageBox(ses_orig.this, ex.getMessage());
								return;
							}

						}


						String V30A = "", V30B = "", V30C = "", V30D = "", V30E = "", V30F = "";
						if (chkQ030a.isChecked()) {
							V30A = "1";
						} else {
							V30A = "2";
						}
						if (chkQ030b.isChecked()) {
							V30B = "1";
						} else {
							V30B = "2";
						}
						if (chkQ030c.isChecked()) {
							V30C = "1";
						} else {
							V30C = "2";
						}
						if (chkQ030d.isChecked()) {
							V30D = "1";
						} else {
							V30D = "2";
						}
						if (chkQ030e.isChecked()) {
							V30E = "1";
						} else {
							V30E = "2";
						}
						if (chkQ030f.isChecked()) {
							V30F = "1";
						} else {
							V30F = "2";
						}

						try {
							SQL = "Update tTrans set Q030a='" + V30A + "',Q030b='" + V30B + "',Q030c='" + V30C + "',Q030d='" + V30D + "',Q030e='" + V30E + "',Q030f='" + V30F + "',PageNo='13' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}

						if ((chkQ030z.isChecked()) && (chkQ030a.isChecked() || chkQ030b.isChecked() || chkQ030c.isChecked() || chkQ030d.isChecked() || chkQ030e.isChecked() || chkQ030f.isChecked() || chkQ030g.isChecked() || chkQ030h.isChecked())) {
							Connection.MessageBox(ses_orig.this, "খানার সদস্য নিম্নের সংস্থার সদস্য, তাই কোন সংস্থার সদস্য নয় টিক হবে না");
							return;
						} else if (!chkQ030z.isChecked() && !chkQ030a.isChecked() && !chkQ030b.isChecked() && !chkQ030c.isChecked() && !chkQ030d.isChecked() && !chkQ030e.isChecked() && !chkQ030f.isChecked() && !chkQ030g.isChecked() && !chkQ030h.isChecked()) {
							Connection.MessageBox(ses_orig.this, "খানার সদস্য নিম্নের সংস্থার সদস্য নয়, তাই কোন সংস্থার সদস্য নয় টিক হবে ");
							return;
						}

						String V30G = "", V30H = "", V30Z = "", V31 = "";
						if (chkQ030g.isChecked()) {
							V30G = "1";
						} else {
							V30G = "2";
						}
						if (chkQ030h.isChecked()) {
							V30H = "1";
						} else {
							V30H = "2";
						}
						if (chkQ030z.isChecked()) {
							V30Z = "1";
						} else {
							V30Z = "2";
						}
						if (spnQ031.getSelectedItemPosition() == 0) {
							Connection.MessageBox(ses_orig.this, "আপনার পরিবারে কুমুদিনি হাসপাতালের শোভাসুন্দরী কার্ড আছে কি না তথ্য ফাকা রাখে যাবে না");
							return;
						}
						if (spnQ031.getSelectedItemPosition() > 3) {
							V31 = "9";
						} else {
							V31 = String.valueOf(spnQ031.getSelectedItemPosition());
						}

						try {
							SQL = "Update tTrans set Q030g='" + V30G + "',Q030h='" + V30H + "',Q030z='" + V30Z + "',Q031='" + V31 + "',PageNo='14',Upload='2' where ";
							SQL += " status='s' and Vill||Bari||HH = '" + Household + "'";
							SQL += " and SESNo='" + spnSESNo.getSelectedItem().toString() + "'";
							C.Save(SQL);
						} catch (Exception ex) {
							Connection.MessageBox(ses_orig.this, ex.getMessage());
							return;
						}
						//Connection.MessageBox(ses_orig.this, "সকল তথ্য সঠিকভাবে সেভ হয়েছে।");
						finish();
						//flipper.setDisplayedChild(0);
						//return;
					}
					catch (Exception e) {
                        if (spnVisit.getSelectedItem().toString().isEmpty() || spnVisit.getSelectedItem().toString()==null) {
                            Connection.MessageBox(ses_orig.this, "STATUS Empty");
                        }
                        else
                            Connection.MessageBox(ses_orig.this,"Error");
					}

				}
	  		  	
	    	});
  		
			spnSESNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					ClearForm();
					MainDataRetrieve(spnSESNo.getSelectedItem().toString());
	    	    }
	    	    @Override
	    	    public void onNothingSelected(AdapterView<?> parentView) {
	    	        // your code here
	    	    }    
			});
	    	
			MainDataRetrieve(spnSESNo.getSelectedItem().toString());

    }
   
    public void MainDataRetrieve(String SESNo)
    {
    	try
    	{
  		String SQLStr="Select ";
  		SQLStr+="Vill,";
  		SQLStr+="Bari,";
  		SQLStr+="HH,";
  		SQLStr+="SESNo,";
  		SQLStr+="ifnull(Visit,'')Visit,";
  		SQLStr+="(case when Q015a is null then '' else Q015a end) Q015a ,";
  		SQLStr+="(case when Q015b is null then '' else Q015b end) Q015b ,";
  		SQLStr+="(case when Q015c is null then '' else Q015c end) Q015c ,";
  		SQLStr+="(case when Q016a is null then '' else Q016a end) Q016a ,";
  		SQLStr+="(case when Q016b is null then '' else Q016b end) Q016b ,";
  		SQLStr+="(case when Q016c is null then '' else Q016c  end) Q016c ,";
  		SQLStr+="(case when Q017  is null then '' else Q017  end) Q017 ,";
  		SQLStr+="(case when Q018  is null then '' else Q018  end) Q018 ,";
  		SQLStr+="(case when Q019a is null then '' else Q019a end) Q019a ,";
  		SQLStr+="(case when Q019b is null then '' else Q019b end) Q019b ,";
  		SQLStr+="(case when Q019c is null then '' else Q019c end) Q019c ,";
  		SQLStr+="(case when Q019d is null then '' else Q019d end) Q019d ,";
  		SQLStr+="(case when Q019e is null then '' else Q019e end) Q019e ,";
  		SQLStr+="(case when Q019f is null then '' else Q019f end) Q019f ,";
  		SQLStr+="(case when Q019g is null then '' else Q019g end) Q019g ,";
  		SQLStr+="(case when Q019h is null then '' else Q019h end) Q019h ,";
  		SQLStr+="(case when Q019i is null then '' else Q019i end) Q019i ,";
  		SQLStr+="(case when Q019j is null then '' else Q019j end) Q019j ,";
  		SQLStr+="(case when Q019k is null then '' else Q019k end) Q019k ,";
  		SQLStr+="(case when Q019l is null then '' else Q019l end) Q019l ,";
  		SQLStr+="(case when Q019m is null then '' else Q019m end) Q019m ,";
  		SQLStr+="(case when Q019n is null then '' else Q019n end) Q019n ,";
  		SQLStr+="(case when Q019o is null then '' else Q019o end) Q019o ,";
  		SQLStr+="(case when Q019p is null then '' else Q019p end) Q019p ,";
  		SQLStr+="(case when Q019q is null then '' else Q019q end) Q019q ,";
  		SQLStr+="(case when Q019r is null then '' else Q019r end) Q019r ,";
  		SQLStr+="(case when Q019s is null then '' else Q019s end) Q019s ,";
  		SQLStr+="(case when Q019t is null then '' else Q019t end) Q019t ,";
  		SQLStr+="(case when Q019u is null then '' else Q019u end) Q019u ,";
  		SQLStr+="(case when Q019v is null then '' else Q019v end) Q019v ,";
  		SQLStr+="(case when Q019w is null then '' else Q019w end) Q019w ,";
  		SQLStr+="(case when Q019x is null then '' else Q019x end) Q019x ,";
  		SQLStr+="(case when Q019y is null then '' else Q019y end) Q019y ,";
  		SQLStr+="(case when Q019z is null then '' else Q019z end) Q019z ,";
  		SQLStr+="(case when Q020a is null then '' else Q020a end) Q020a ,";
  		SQLStr+="(case when Q020b is null then '' else Q020b end) Q020b ,";
  		SQLStr+="(case when Q020c is null then '' else Q020c end) Q020c ,";
  		SQLStr+="(case when Q020d is null then '' else Q020d end) Q020d ,";
  		SQLStr+="(case when Q020e is null then '' else Q020e end) Q020e ,";
  		SQLStr+="(case when Q020f is null then '' else Q020f end) Q020f ,";
  		SQLStr+="(case when Q020g is null then '' else Q020g end) Q020g ,";
  		SQLStr+="(case when Q020h is null then '' else Q020h end) Q020h ,";
  		SQLStr+="(case when Q021  is null then '' else Q021 end) Q021 ,";
  		SQLStr+="(case when Q022a is null then '' else Q022a end) Q022a ,";
  		SQLStr+="(case when Q022b is null then '' else Q022b end) Q022b ,";
  		SQLStr+="(case when Q022c is null then '' else Q022c end) Q022c ,";
  		SQLStr+="(case when Q023a is null then '' else Q023a end) Q023a ,";
  		SQLStr+="(case when Q023b is null then '' else Q023b end) Q023b ,";
  		SQLStr+="(case when Q024a is null then '' else Q024a end) Q024a ,";
  		SQLStr+="(case when Q024b is null then '' else Q024b end) Q024b ,";
  		SQLStr+="(case when Q025a is null then '' else Q025a end) Q025a ,";
  		SQLStr+="(case when Q025b is null then '' else Q025b end) Q025b ,";
  		SQLStr+="(case when Q026  is null then '' else Q026 end) Q026 ,";

  		SQLStr+="(case when Q027a is null then '' else Q027a end) Q027a ,";
  		SQLStr+="(case when Q027b is null then '' else Q027b end) Q027b ,";
  		SQLStr+="(case when Q027c is null then '' else Q027c end) Q027c ,";
  		SQLStr+="(case when Q027d is null then '' else Q027d end) Q027d ,";
  		SQLStr+="(case when Q027e is null then '' else Q027e end) Q027e ,";
  		SQLStr+="(case when Q027f is null then '' else Q027f end) Q027f ,";
  		SQLStr+="(case when Q027g is null then '' else Q027g end) Q027g ,";
  		SQLStr+="(case when Q027h is null then '' else Q027h end) Q027h ,";
  		SQLStr+="(case when Q027i is null then '' else Q027i end) Q027i ,";
  		SQLStr+="(case when Q027j is null then '' else Q027j end) Q027j ,";
  		SQLStr+="(case when Q027y is null then '' else Q027y end) Q027y ,";
  		SQLStr+="(case when Q027z is null then '' else Q027z end) Q027z ,";
  		SQLStr+="(case when Q028a is null then '' else Q028a end) Q028a ,";
  		SQLStr+="(case when Q028b is null then '' else Q028b end) Q028b ,";
  		SQLStr+="(case when Q028c is null then '' else Q028c end) Q028c ,";
  		SQLStr+="(case when Q028d is null then '' else Q028d end) Q028d ,";
  		SQLStr+="(case when Q028e is null then '' else Q028e end) Q028e ,";
  		SQLStr+="(case when Q028y is null then '' else Q028y end) Q028y ,";
  		SQLStr+="(case when Q029  is null then '' else Q029 end) Q029 ,";
  		SQLStr+="(case when Q030a is null then '' else Q030a end) Q030a ,";
  		SQLStr+="(case when Q030b is null then '' else Q030b end) Q030b ,";
  		SQLStr+="(case when Q030c is null then '' else Q030c end) Q030c ,";
  		SQLStr+="(case when Q030d is null then '' else Q030d end) Q030d ,";
  		SQLStr+="(case when Q030e is null then '' else Q030e end) Q030e ,";
  		SQLStr+="(case when Q030f is null then '' else Q030f end) Q030f ,";
  		SQLStr+="(case when Q030g is null then '' else Q030g end) Q030g ,";
  		SQLStr+="(case when Q030h is null then '' else Q030h end) Q030h ,";
  		SQLStr+="(case when Q030z is null then '' else Q030z end) Q030z ,";
  		SQLStr+="(case when Q031 is null then '' else Q031 end) Q031 ,Vdate, Rnd";
  			
  		SQLStr+=" from tTrans where status='s' and Vill||Bari||HH = '"+ Household +"'";
  		SQLStr+=" and SESNo='"+ SESNo +"'";  		
  		
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

	        		if(cur.getString(cur.getColumnIndex("Visit")).equals("1"))
	        			spnVisit.setSelection(1);
	        		else if(cur.getString(cur.getColumnIndex("Visit")).equals("3"))
	        			spnVisit.setSelection(2);
	        		else if(cur.getString(cur.getColumnIndex("Visit")).equals("7"))
	        			spnVisit.setSelection(3);
	        		else
	        			spnVisit.setSelection(0);
	        		
	        		if(cur.getString(cur.getColumnIndex("Q015a")).equals("11"))
	        		{
	        			spnQ015a.setSelection(1);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("12"))
	        		{
	        			spnQ015a.setSelection(2);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("21"))
	        		{
	        			spnQ015a.setSelection(3);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("22"))
	        		{
	        			spnQ015a.setSelection(4);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("23"))
	        		{
	        			spnQ015a.setSelection(5);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("31"))
	        		{
	        			spnQ015a.setSelection(6);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("32"))
	        		{
	        			spnQ015a.setSelection(7);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("41"))
	        		{
	        			spnQ015a.setSelection(8);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("51"))
	        		{
	        			spnQ015a.setSelection(9);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("61"))
	        		{
	        			spnQ015a.setSelection(10);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015a")).equals("77"))
	        		{
	        			spnQ015a.setSelection(11);
	        		}
	        		else
	        		{
	        			spnQ015a.setSelection(0);
	        		}
	        		
	        		
	        		
	        		if(cur.getString(cur.getColumnIndex("Q015b")).equals("11"))
	        		{
	        			spnQ015b.setSelection(1);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("12"))
	        		{
	        			spnQ015b.setSelection(2);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("21"))
	        		{
	        			spnQ015b.setSelection(3);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("22"))
	        		{
	        			spnQ015b.setSelection(4);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("23"))
	        		{
	        			spnQ015b.setSelection(5);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("31"))
	        		{
	        			spnQ015b.setSelection(6);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("32"))
	        		{
	        			spnQ015b.setSelection(7);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("41"))
	        		{
	        			spnQ015b.setSelection(8);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("51"))
	        		{
	        			spnQ015b.setSelection(9);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("61"))
	        		{
	        			spnQ015b.setSelection(10);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015b")).equals("77"))
	        		{
	        			spnQ015b.setSelection(11);
	        		}
	        		else
	        		{
	        			spnQ015b.setSelection(0);
	        		}
	        		
	        		
	        		if(cur.getString(cur.getColumnIndex("Q015c")).equals("11"))
	        		{
	        			spnQ015c.setSelection(1);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("12"))
	        		{
	        			spnQ015c.setSelection(2);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("21"))
	        		{
	        			spnQ015c.setSelection(3);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("22"))
	        		{
	        			spnQ015c.setSelection(4);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("23"))
	        		{
	        			spnQ015c.setSelection(5);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("31"))
	        		{
	        			spnQ015c.setSelection(6);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("32"))
	        		{
	        			spnQ015c.setSelection(7);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("41"))
	        		{
	        			spnQ015c.setSelection(8);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("51"))
	        		{
	        			spnQ015c.setSelection(9);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("61"))
	        		{
	        			spnQ015c.setSelection(10);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q015c")).equals("77"))
	        		{
	        			spnQ015c.setSelection(11);
	        		}
	        		else
	        		{
	        			spnQ015c.setSelection(0);
	        		}
	        		
	        		
	        		if(cur.getString(cur.getColumnIndex("Q016a")).equals("1"))
	        		{
	        			rdoYQ016a.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016a")).equals("2"))
	        		{
	        			rdoNQ016a.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016a")).equals("9"))
	        		{
	        			rdoDKQ016a.setChecked(true);
	        		}
	        		
	        		if(cur.getString(cur.getColumnIndex("Q016b")).equals("1"))
	        		{
	        			rdoYQ016b.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016b")).equals("2"))
	        		{
	        			rdoNQ016b.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016b")).equals("9"))
	        		{
	        			rdoDKQ016b.setChecked(true);
	        		}
	        		
	        		if(cur.getString(cur.getColumnIndex("Q016c")).equals("1"))
	        		{
	        			rdoYQ016c.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016c")).equals("2"))
	        		{
	        			rdoNQ016c.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q016c")).equals("9"))
	        		{
	        			rdoDKQ016c.setChecked(true);
	        		}
	        		
	        		
	        		if(cur.getString(cur.getColumnIndex("Q017")).equals("11"))
	        		{
	        			spnQ017.setSelection(1);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("21"))
	        		{
	        			spnQ017.setSelection(2);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("22"))
	        		{
	        			spnQ017.setSelection(3);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("23"))
	        		{
	        			spnQ017.setSelection(4);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("24"))
	        		{
	        			spnQ017.setSelection(5);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("31"))
	        		{
	        			spnQ017.setSelection(6);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q017")).equals("77"))
	        		{
	        			spnQ017.setSelection(7);
	        		}
	        		else
	        		{
	        			spnQ017.setSelection(0);
	        		}
	        		
	        		if(cur.getString(cur.getColumnIndex("Q018")).equals("1"))
	        		{
	        			rdoYQ018.setChecked(true);
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q018")).equals("2"))
	        		{
	        			rdoNQ018.setChecked(true);
	        		}
	        		
	        		txtQ019a.setText(cur.getString(cur.getColumnIndex("Q019a")));
	        		txtQ019b.setText(cur.getString(cur.getColumnIndex("Q019b")));
	        		txtQ019c.setText(cur.getString(cur.getColumnIndex("Q019c")));
	        		txtQ019d.setText(cur.getString(cur.getColumnIndex("Q019d")));
	        		txtQ019e.setText(cur.getString(cur.getColumnIndex("Q019e")));
	        		txtQ019f.setText(cur.getString(cur.getColumnIndex("Q019f")));
	        		txtQ019g.setText(cur.getString(cur.getColumnIndex("Q019g")));
	        		txtQ019h.setText(cur.getString(cur.getColumnIndex("Q019h")));
	        		txtQ019i.setText(cur.getString(cur.getColumnIndex("Q019i")));
	        		txtQ019j.setText(cur.getString(cur.getColumnIndex("Q019j")));
	        		txtQ019k.setText(cur.getString(cur.getColumnIndex("Q019k")));
	        		txtQ019l.setText(cur.getString(cur.getColumnIndex("Q019l")));
	        		txtQ019m.setText(cur.getString(cur.getColumnIndex("Q019m")));
	        		txtQ019n.setText(cur.getString(cur.getColumnIndex("Q019n")));
	        		txtQ019o.setText(cur.getString(cur.getColumnIndex("Q019o")));
	        		txtQ019p.setText(cur.getString(cur.getColumnIndex("Q019p")));
	        		txtQ019q.setText(cur.getString(cur.getColumnIndex("Q019q")));
	        		txtQ019r.setText(cur.getString(cur.getColumnIndex("Q019r")));
	        		txtQ019s.setText(cur.getString(cur.getColumnIndex("Q019s")));
	        		txtQ019t.setText(cur.getString(cur.getColumnIndex("Q019t")));
	        		txtQ019u.setText(cur.getString(cur.getColumnIndex("Q019u")));
	        		txtQ019v.setText(cur.getString(cur.getColumnIndex("Q019v")));
	        		txtQ019w.setText(cur.getString(cur.getColumnIndex("Q019w")));
	        		txtQ019x.setText(cur.getString(cur.getColumnIndex("Q019x")));
	        		txtQ019y.setText(cur.getString(cur.getColumnIndex("Q019y")));
	        		txtQ019z.setText(cur.getString(cur.getColumnIndex("Q019z")));
	        		
	        		txtQ020a.setText(cur.getString(cur.getColumnIndex("Q020a")));
	        		txtQ020b.setText(cur.getString(cur.getColumnIndex("Q020b")));
	        		txtQ020c.setText(cur.getString(cur.getColumnIndex("Q020c")));
	        		txtQ020d.setText(cur.getString(cur.getColumnIndex("Q020d")));
	        		txtQ020e.setText(cur.getString(cur.getColumnIndex("Q020e")));
	        		txtQ020f.setText(cur.getString(cur.getColumnIndex("Q020f")));
	        		txtQ020g.setText(cur.getString(cur.getColumnIndex("Q020g")));
	        		txtQ020h.setText(cur.getString(cur.getColumnIndex("Q020h")));
	        		
	        		txtQ021.setText(cur.getString(cur.getColumnIndex("Q021")));
	        		
	        		spnQ022a.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Q022a")).length()==0?"0":cur.getString(cur.getColumnIndex("Q022a"))));
	        		spnQ022b.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Q022b")).length()==0?"0":cur.getString(cur.getColumnIndex("Q022b"))));
	        		spnQ022c.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Q022c")).length()==0?"0":cur.getString(cur.getColumnIndex("Q022c"))));
	        		
	        		txtQ023a.setText(cur.getString(cur.getColumnIndex("Q023a")));
	        		txtQ023b.setText(cur.getString(cur.getColumnIndex("Q023b")));
	        		txtQ024a.setText(cur.getString(cur.getColumnIndex("Q024a")));
	        		txtQ024b.setText(cur.getString(cur.getColumnIndex("Q024b")));
	        		txtQ025a.setText(cur.getString(cur.getColumnIndex("Q025a")));
	        		txtQ025b.setText(cur.getString(cur.getColumnIndex("Q025b")));
	        		
	        		if(cur.getString(cur.getColumnIndex("Q026")).equals("1"))
	        		{
	        			rdoYQ026.setChecked(true);
	        			
	        			if(cur.getString(cur.getColumnIndex("Q027a")).equals("1"))  {chkQ027a.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027a")).equals("2"))	{chkQ027a.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027b")).equals("1"))  {chkQ027b.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027b")).equals("2"))	{chkQ027b.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027c")).equals("1"))  {chkQ027c.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027c")).equals("2"))	{chkQ027c.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027d")).equals("1"))  {chkQ027d.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027d")).equals("2"))	{chkQ027d.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027e")).equals("1"))  {chkQ027e.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027e")).equals("2"))	{chkQ027e.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027f")).equals("1"))  {chkQ027f.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027f")).equals("2"))	{chkQ027f.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027g")).equals("1"))  {chkQ027g.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027g")).equals("2"))	{chkQ027g.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027h")).equals("1"))  {chkQ027h.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027h")).equals("2"))	{chkQ027h.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027i")).equals("1"))  {chkQ027i.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027i")).equals("2"))	{chkQ027i.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027j")).equals("1"))  {chkQ027j.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027j")).equals("2"))	{chkQ027j.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027y")).equals("1"))  {chkQ027y.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027y")).equals("2"))	{chkQ027y.setChecked(false);}
		        		if(cur.getString(cur.getColumnIndex("Q027z")).equals("1"))  {chkQ027z.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q027z")).equals("2"))	{chkQ027z.setChecked(false);}
		        		
		        		txtQ028a.setText(cur.getString(cur.getColumnIndex("Q028a")));
		        		txtQ028b.setText(cur.getString(cur.getColumnIndex("Q028b")));
		        		txtQ028c.setText(cur.getString(cur.getColumnIndex("Q028c")));
		        		txtQ028d.setText(cur.getString(cur.getColumnIndex("Q028d")));
		        		txtQ028e.setText(cur.getString(cur.getColumnIndex("Q028e")));
		        		txtQ028y.setText(cur.getString(cur.getColumnIndex("Q028y")));
		        		

		        		if(cur.getString(cur.getColumnIndex("Q029")).equals("1"))
		        		{
		        			rdoYQ029.setChecked(true);
		        		}
		        		else if(cur.getString(cur.getColumnIndex("Q029")).equals("2"))
		        		{
		        			rdoNQ029.setChecked(true);
		        		}
		        		else if(cur.getString(cur.getColumnIndex("Q029")).equals("9"))
		        		{
		        			rdoDKQ029.setChecked(true);
		        		}
	        		}
	        		else if(cur.getString(cur.getColumnIndex("Q026")).equals("2"))
	        		{
	        			rdoNQ026.setChecked(true);
	        		}
	        		
	        			        		
	        		if(cur.getString(cur.getColumnIndex("Q030a")).equals("1"))  {chkQ030a.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030a")).equals("2"))	{chkQ030a.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030b")).equals("1"))  {chkQ030b.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030b")).equals("2"))	{chkQ030b.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030c")).equals("1"))  {chkQ030c.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030c")).equals("2"))	{chkQ030c.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030d")).equals("1"))  {chkQ030d.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030d")).equals("2"))	{chkQ030d.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030e")).equals("1"))  {chkQ030e.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030e")).equals("2"))	{chkQ030e.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030f")).equals("1"))  {chkQ030f.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030f")).equals("2"))	{chkQ030f.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030g")).equals("1"))  {chkQ030g.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030g")).equals("2"))	{chkQ030g.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030h")).equals("1"))  {chkQ030h.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030h")).equals("2"))	{chkQ030h.setChecked(false);}
	        		if(cur.getString(cur.getColumnIndex("Q030z")).equals("1"))  {chkQ030z.setChecked(true);	}else if(cur.getString(cur.getColumnIndex("Q030z")).equals("2"))	{chkQ030z.setChecked(false);}
	        		
	        		if(cur.getString(cur.getColumnIndex("Q031")).length()==0)
	        		{
	        			spnQ031.setSelection(0);
	        		}
	        		else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Q031")).length()==0?"0":cur.getString(cur.getColumnIndex("Q031")))>3)
	        		{
	        			spnQ031.setSelection(4);
	        		}
	        		else
	        		{
	        			spnQ031.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Q031"))));
	        		}
	        		
	        		
	        		cur.moveToNext(); 
	        }		
	                
	        cur.close();
    	}
    	catch(Exception ex)
    	{
    		Connection.MessageBox(ses_orig.this, ex.getMessage());
    		return;
    	}
	        
    }

    
    private void ClearForm()
    {
    	 spnVisit.setSelection(0);
    	 spnQ015a.setSelection(0);
	     spnQ015b.setSelection(0);
	     spnQ015c.setSelection(0);
	
	     rdogrpQ016a.clearCheck();
	     rdogrpQ016b.clearCheck();
	     rdogrpQ016c.clearCheck();
	     
	        spnQ017.setSelection(0);
	        rdogrpQ018.clearCheck();

	        txtQ019a.setText("");
	        txtQ019b.setText("");
	        txtQ019c.setText("");
	        txtQ019d.setText("");
	        txtQ019e.setText("");
	        txtQ019f.setText("");
	        txtQ019g.setText("");
	        txtQ019h.setText("");
	        txtQ019i.setText("");
	        txtQ019j.setText("");
	        txtQ019k.setText("");
	        txtQ019l.setText("");
	        txtQ019m.setText("");
	        txtQ019n.setText("");
	        txtQ019o.setText("");
	        txtQ019p.setText("");
	        txtQ019q.setText("");
	        txtQ019r.setText("");
	        txtQ019s.setText("");
	        txtQ019t.setText("");
	        txtQ019u.setText("");
	        txtQ019v.setText("");
	        txtQ019w.setText("");
	        txtQ019x.setText("");
	        txtQ019y.setText("");
	        txtQ019z.setText("");
	        
	        txtQ020a.setText("");
	        txtQ020b.setText("");
	        txtQ020c.setText("");
	        txtQ020d.setText("");
	        txtQ020e.setText("");
	        txtQ020f.setText("");
	        txtQ020g.setText("");
	        txtQ020h.setText("");
	      
	        txtQ021.setText("");
	        spnQ022a.setSelection(0);
	        spnQ022b.setSelection(0);
	        spnQ022c.setSelection(0);

	        txtQ023a.setSelection(0);
	        txtQ023b.setSelection(0);
	        txtQ024a.setSelection(0);
	        txtQ024b.setSelection(0);
	        txtQ025a.setSelection(0);
	        txtQ025b.setSelection(0);

	        rdogrpQ026.clearCheck();
	                
	        chkQ027a.setChecked(false);
	        chkQ027b.setChecked(false);
	        chkQ027c.setChecked(false);
	        chkQ027d.setChecked(false);
	        chkQ027e.setChecked(false);
	        chkQ027f.setChecked(false);
	        chkQ027g.setChecked(false);
	        chkQ027h.setChecked(false);
	        chkQ027i.setChecked(false);
	        chkQ027j.setChecked(false);
	        chkQ027y.setChecked(false);
	        chkQ027z.setChecked(false);
	        
	        txtQ028a.setText("");
	        txtQ028b.setText("");
	        txtQ028c.setText("");
	        txtQ028d.setText("");
	        txtQ028e.setText("");
	        txtQ028y.setText("");

	        rdogrpQ029.clearCheck();
	      
	        chkQ030a.setChecked(false);
	        chkQ030b.setChecked(false);
	        chkQ030c.setChecked(false);
	        chkQ030d.setChecked(false);
	        chkQ030e.setChecked(false);
	        chkQ030f.setChecked(false);
	        chkQ030g.setChecked(false);
	        chkQ030h.setChecked(false);
	        chkQ030z.setChecked(false);
	        spnQ031.setSelection(0);	
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