package data.mirzapurdss;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.KeyEvent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ArrayAdapter;

public class Death extends Activity {
    boolean netwoekAvailable=false;
    Location currentLocation; 
    double currentLatitude,currentLongitude; 
    Location currentLocationNet; 
    double currentLatitudeNet,currentLongitudeNet; 
    //Disabled Back/Home key
    //--------------------------------------------------------------------------------------------------
    @Override 
    public boolean onKeyDown(int iKeyCode, KeyEvent event)
    {
        if(iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME) 
             { return false; }
        else { return true;  }
    }
    //Top menu
    //--------------------------------------------------------------------------------------------------
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuclose, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(Death.this);
        switch (item.getItemId()) {
            case R.id.menuClose:
                adb.setTitle("Close");
                  adb.setMessage("আপনি কি এই ফর্ম থেকে বের হতে চান[Yes/No]?");
                  adb.setNegativeButton("No", null);
                  adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                      public void onClick(DialogInterface dialog, int which) {
                          finish();
                      }});
                  adb.show();
               return true;
        }
        return false;
    }
    
    
    String VariableID;
    private static String StartTime;
    private static String EndTime;
    private static String Lat;
    private static String Lon;        
    private int hour;
    private int minute;
    private int mDay;
    private int mMonth;
    private int mYear;
    static final int DATE_DIALOG = 1;
    static final int TIME_DIALOG = 2;

    private static String Vil="1";
    private static String Bari="01";
    private static String HH="10101001";

    Connection C;
    Global g;
    SimpleAdapter dataAdapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
    private String TableName;

         LinearLayout secPNo;
         TextView VlblPNo;
         EditText txtPNo;
         LinearLayout secDthPlace;
         TextView VlblDthPlace;
         Spinner spnDthPlace;
         LinearLayout secFacName;
         TextView VlblFacName;
         Spinner spnFacName;
         LinearLayout secFacOther;
         TextView VlblFacOther;
         EditText txtFacOther;
         LinearLayout secTreatment;
         TextView VlblTreatment;
         RadioGroup rdogrpTreatment;         
         RadioButton rdoTreatment1;
         RadioButton rdoTreatment2;
         RadioButton rdoTreatment3;
         
         LinearLayout secWhenTreat;
         TextView VlblWhenTreat;
         EditText txtWhenTreat;
         LinearLayout secFacility;
         TextView VlblFacility;
         Spinner spnFacility;
         LinearLayout secDisp;
         TextView VlblDisp;
         Spinner spnDisp;
         LinearLayout secWhoDisp;
         TextView VlblWhoDisp;
         EditText txtWhoDisp;
         
         LinearLayout secType;
         Spinner spnType;
         EditText txtTime;
         RadioGroup rdogrpPreg;
         RadioButton rdoPreg1;
         RadioButton rdoPreg2;
         RadioGroup rdogrpPregDeath;
         RadioButton rdoPregDeath1;
         RadioButton rdoPregDeath2;
         
         LinearLayout secTime;
         EditText txtPregTime;
         RadioGroup rdogrpTimeType;
         RadioButton rdoTimeType1;
         RadioButton rdoTimeType2;
         RadioButton rdoTimeType3;
         
         LinearLayout secPreg;
         LinearLayout secPregDeath;
         LinearLayout secPergTime;
         LinearLayout secPergTime1;
         
         LinearLayout secCause;
         Spinner spnCause;
        
         TextView txtHHNo;
         TextView txtSNo;
         Spinner spnVisitStatus;
         LinearLayout secVisitStatus;
         
 public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
   try
     {
         setContentView(R.layout.death);
         C = new Connection(this);
         g = Global.getInstance();
         setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         
         StartTime = g.CurrentTime24();
         FindLocation();
         FindLocationNet();


         TableName = "Death_Temp";

         
         secVisitStatus = (LinearLayout) findViewById(R.id.secVisitStatus);
         txtHHNo = (TextView)findViewById(R.id.txtHHNo);
         txtHHNo.setText(g.getVillageCode()+g.getBariCode()+g.getHouseholdNo());
         txtSNo = (TextView)findViewById(R.id.txtSNo);
         txtSNo.setText(g.getmemSlNo());
         
         spnVisitStatus = (Spinner)findViewById( R.id.spnVisitStatus );
         List<String> listVStatus = new ArrayList<String>();
         
         listVStatus.add("");
         listVStatus.add("01-Complete");
         listVStatus.add("02-InComplete");
         listVStatus.add("03-All Member Migrated Out");
         ArrayAdapter<String> adptrVStatus= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listVStatus);
         spnVisitStatus.setAdapter(adptrVStatus);
         spnVisitStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
             if (spnVisitStatus.getSelectedItem().toString().length() == 0) return;
             String spnData = Global.Left(spnVisitStatus.getSelectedItem().toString(), 2);
                 if(spnData.equalsIgnoreCase("02")|spnData.equalsIgnoreCase("03"))
                 {
                     secVisitStatus.setVisibility( View.GONE );                           
                 }
                 
                 else
                 {                 
                     secVisitStatus.setVisibility( View.VISIBLE );                           
                 }
             }
             @Override
             public void onNothingSelected(AdapterView<?> parentView) {
             }
         });
         
         secPreg=(LinearLayout)findViewById(R.id.secPreg);
         secPregDeath=(LinearLayout)findViewById(R.id.secPregDeath);
         secPergTime=(LinearLayout)findViewById(R.id.secPergTime);
         secPergTime1=(LinearLayout)findViewById(R.id.secPergTime1);
         
         secDthPlace=(LinearLayout)findViewById(R.id.secDthPlace);
         VlblDthPlace=(TextView) findViewById(R.id.VlblDthPlace);
         spnDthPlace=(Spinner) findViewById(R.id.spnDthPlace);
         List<String> listDthPlace = new ArrayList<String>();
         
         listDthPlace.add("");
         listDthPlace.add("01-বাড়িতে");
         listDthPlace.add("03-খেলার মাঠ/ বাড়ির বাইরে");
         listDthPlace.add("04-জ্বলাধার(পুকুর,নদী,খাল ইত্যাদি)");
         listDthPlace.add("05-হাসপাতালে যাওয়ার পথে");
         listDthPlace.add("06-হাসপাতাল থেকে ফেরার সময় রাস্তায়");
         listDthPlace.add("11-কুমুদিনী হাসপাতাল");
         listDthPlace.add("12-জামুরকি সদর হাসপাতাল");
         listDthPlace.add("13-টাঙ্গাইল সদর হাসপাতাল");
         listDthPlace.add("14-উপজিলাস্বাস্থ্য কেন্দ্র");
         listDthPlace.add("15-কমিউনিটি ক্লিনিক");
         listDthPlace.add("16-প্রাইভেট/এনজিও ক্লিনিক");
         listDthPlace.add("17-ডাক্তারের চেম্বার (অভিজ্ঞ)");
         listDthPlace.add("18-ডাক্তারের চেম্বার (অনভিজ্ঞ)");
         listDthPlace.add("19-FWC/উপস্বাস্থ্য কেন্দ্র");
         listDthPlace.add("20-মেডিকেল কলেজ হাসপাতাল/শিশু হাসপাতাল");
         listDthPlace.add("21-ফার্মেসি");
         listDthPlace.add("22-অন্যান্য হাসপাতাল/স্বাস্থ্যকেন্দ্র/ক্লিনিক");
         listDthPlace.add("77-অন্যান্য");
         ArrayAdapter<String> adptrDthPlace= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listDthPlace);
         spnDthPlace.setAdapter(adptrDthPlace);

         secFacName=(LinearLayout)findViewById(R.id.secFacName);
         VlblFacName=(TextView) findViewById(R.id.VlblFacName);
         spnFacName=(Spinner) findViewById(R.id.spnFacName);
         List<String> listFacName = new ArrayList<String>();
         
         listFacName.add("");
         listFacName.add("01-কুমুদিনী হাসপাতাল");
         listFacName.add("02-জামুরকি সদর হাসপাতাল");
         listFacName.add("03-টাঙ্গাইল  সদর হাসপাতাল");
         listFacName.add("04-উপজিলা স্বাস্থ্য কেন্দ্র");
         listFacName.add("05-কমিউনিটি ক্লিনিক");
         listFacName.add("06-প্রাইভেট/এন জি ও ক্লিনিক");
         listFacName.add("07-ডাক্তারের চেম্বার (অভিজ্ঞ)");
         listFacName.add("08-ডাক্তারের চেম্বার (অনভিজ্ঞ)");
         listFacName.add("09-FWC/উপস্বাস্থ্য কেন্দ্র");
         listFacName.add("10-মেডিকেল কলেজ হাসপাতাল/শিশু হাসপাতাল");
         listFacName.add("11-ফার্মেসি");
         listFacName.add("12-অন্যান্য");
         ArrayAdapter<String> adptrFacName= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listFacName);
         spnFacName.setAdapter(adptrFacName);

         
         secFacOther=(LinearLayout)findViewById(R.id.secFacOther);
         VlblFacOther=(TextView) findViewById(R.id.VlblFacOther);
         txtFacOther=(EditText) findViewById(R.id.txtFacOther);
         secTreatment=(LinearLayout)findViewById(R.id.secTreatment);
         VlblTreatment = (TextView) findViewById(R.id.VlblTreatment);
         rdogrpTreatment = (RadioGroup) findViewById(R.id.rdogrpTreatment);
         
         rdoTreatment1 = (RadioButton) findViewById(R.id.rdoTreatment1);
         rdoTreatment2 = (RadioButton) findViewById(R.id.rdoTreatment2);
         rdoTreatment3 = (RadioButton) findViewById(R.id.rdoTreatment3);
         
         secWhenTreat=(LinearLayout)findViewById(R.id.secWhenTreat);
         VlblWhenTreat=(TextView) findViewById(R.id.VlblWhenTreat);
         txtWhenTreat=(EditText) findViewById(R.id.txtWhenTreat);
         secFacility=(LinearLayout)findViewById(R.id.secFacility);
         VlblFacility=(TextView) findViewById(R.id.VlblFacility);
         spnFacility=(Spinner) findViewById(R.id.spnFacility);
         
         spnFacName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
             if (spnFacName.getSelectedItem().toString().length() == 0) return;
             String spnData = Global.Left(spnFacName.getSelectedItem().toString(), 2);
                 if(spnData.equalsIgnoreCase("06")|spnData.equalsIgnoreCase("07")|spnData.equalsIgnoreCase("12"))
                 {
                         secFacOther.setVisibility(View.VISIBLE);                        
                 }
                 
                 else
                 {                 
                        txtFacOther.setText("");
                        secFacOther.setVisibility(View.GONE);
                        
                 }
             }
             @Override
             public void onNothingSelected(AdapterView<?> parentView) {
             }
         });
         
         spnDthPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
             if (spnDthPlace.getSelectedItem().toString().length() == 0) return;
             String spnData = Global.Left(spnDthPlace.getSelectedItem().toString(), 2);
                 if(spnData.equalsIgnoreCase("77"))
                 {
                     secFacOther.setVisibility(View.VISIBLE);                    
                 }
                 
                 else
                 {                 
                     txtFacOther.setText("");
                     secFacOther.setVisibility(View.GONE);                 
                 }
             }
             @Override
             public void onNothingSelected(AdapterView<?> parentView) {
             }
         });
         rdogrpTreatment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
             @Override
             public void onCheckedChanged(RadioGroup radioGroup,int radioButtonID) {
                 RadioButton rb = (RadioButton)findViewById(rdogrpTreatment.getCheckedRadioButtonId());
                 if(rb==null) return;
                 String rbData = Global.Left(rb.getText().toString(),1);
                     if(rbData.equalsIgnoreCase("2") | rbData.equalsIgnoreCase("3"))
                     {
                      
                         txtWhenTreat.setText("");
                         secWhenTreat.setVisibility(View.GONE);
                         spnFacility.setSelection( 0 );
                         secFacility.setVisibility(View.GONE);
                        
                     }
                     
                     else
                     {
                         secWhenTreat.setVisibility(View.VISIBLE);
                         secFacility.setVisibility(View.VISIBLE);
                     }
                  }
             public void onNothingSelected(AdapterView<?> adapterView) {
                 return;
                     } 
                 }); 
         
         List<String> listFacility = new ArrayList<String>();
         
         listFacility.add("");
         listFacility.add("01-বাড়িতে");
         listFacility.add("11-কুমুদিনী হাসপাতাল");
         listFacility.add("12-জামুরকি সদর হাসপাতাল");
         listFacility.add("13-টাঙ্গাইল সদর হাসপাতাল");
         listFacility.add("14-উপজিলাস্বাস্থ্য কেন্দ্র");
         listFacility.add("15-কমিউনিটি ক্লিনিক");
         listFacility.add("16-প্রাইভেট/এনজিও ক্লিনিক");
         listFacility.add("17-ডাক্তারের চেম্বার (অভিজ্ঞ)");
         listFacility.add("18-ডাক্তারের চেম্বার (অনভিজ্ঞ)");
         listFacility.add("19-FWC/উপস্বাস্থ্য কেন্দ্র");
         listFacility.add("20-মেডিকেল কলেজ হাসপাতাল/শিশু হাসপাতাল");
         listFacility.add("21-ফার্মেসি");
         listFacility.add("22-অন্যান্য হাসপাতাল/স্বাস্থ্যকেন্দ্র/ক্লিনিক");
         listFacility.add("77-অন্যান্য");
         
         ArrayAdapter<String> adptrFacility= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listFacility);
         spnFacility.setAdapter(adptrFacility);

         secDisp=(LinearLayout)findViewById(R.id.secDisp);
         VlblDisp=(TextView) findViewById(R.id.VlblDisp);
         spnDisp=(Spinner) findViewById(R.id.spnDisp);
         List<String> listDisp = new ArrayList<String>();
         
         listDisp.add("");
         listDisp.add("01-পারিবারিক কবর স্থান");
         listDisp.add("02-খানার কাছে/আশে পাশে");
         listDisp.add("03-একই গ্রামে কবর স্থান/শ্বশ্মান");
         listDisp.add("04-অন্য গ্রামে কবর স্থান/শ্বশ্মান");
         listDisp.add("05-অন্য জিলায় কবর স্থান/শ্বশ্মান (অনেক দূরবর্তী)");
         listDisp.add("77-অন্য এলাকায়");
         ArrayAdapter<String> adptrDisp= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listDisp);
         spnDisp.setAdapter(adptrDisp);
         
         secWhoDisp=(LinearLayout)findViewById(R.id.secWhoDisp);
         VlblWhoDisp=(TextView) findViewById(R.id.VlblWhoDisp);
         txtWhoDisp=(EditText) findViewById(R.id.txtWhoDisp);

         secType = (LinearLayout)findViewById( R.id.secType );
         spnType = (Spinner)findViewById( R.id.spnType );
         List<String> listType = new ArrayList<String>();
         
         listType.add("");
         listType.add("01-পুত্র");
         listType.add("02-পিতা");
         listType.add("03-পরিবারের সদস্য");
         listType.add("04-আত্মীয় স্বজন");
         listType.add("05-ইমাম/পুরহিত/ভ্রাহ্মন");
         listType.add("77-অন্যান্য");
         ArrayAdapter<String> adptrType= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listType);
         spnType.setAdapter(adptrType);
         
         secTime = (LinearLayout)findViewById( R.id.secTime );
         txtTime = (EditText)findViewById( R.id.txtTime );
         rdogrpPreg = (RadioGroup)findViewById(R.id.rdogrpPreg);
         rdoPreg1 = (RadioButton)findViewById( R.id.rdoPreg1 );
         rdoPreg2 = (RadioButton)findViewById( R.id.rdoPreg2 );
         rdogrpPreg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
             @Override
             public void onCheckedChanged(RadioGroup radioGroup,int radioButtonID) {
                 RadioButton rb = (RadioButton)findViewById(rdogrpPreg.getCheckedRadioButtonId());
                 if(rb==null) return;
                 String rbData = Global.Left(rb.getText().toString(),1);
                     if(rbData.equalsIgnoreCase("1"))
                     {
                         secPregDeath.setVisibility( View.VISIBLE );
                         secPergTime.setVisibility( View.VISIBLE );
                         secPergTime1.setVisibility( View.VISIBLE );                         
                     }
                     
                     else
                     {
                         rdogrpPregDeath.clearCheck();
                         secPregDeath.setVisibility( View.GONE );
                         secPergTime.setVisibility( View.GONE );
                         txtPregTime.setText("");
                         secPergTime1.setVisibility( View.GONE );                                                  
                     }
                  }
             public void onNothingSelected(AdapterView<?> adapterView) {
                 return;
                     } 
                 }); 
         
         rdogrpPregDeath = (RadioGroup)findViewById( R.id.rdogrpPregDeath );
         rdoPregDeath1 = (RadioButton)findViewById( R.id.rdoPregDeath1 );
         rdoPregDeath2 = (RadioButton)findViewById( R.id.rdoPregDeath2 );
         rdogrpPregDeath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
             @Override
             public void onCheckedChanged(RadioGroup radioGroup,int radioButtonID) {
                 RadioButton rb = (RadioButton)findViewById(rdogrpPregDeath.getCheckedRadioButtonId());
                 if(rb==null) return;
                 String rbData = Global.Left(rb.getText().toString(),1);
                     if(rbData.equalsIgnoreCase("1"))
                     {
                         secPergTime.setVisibility( View.GONE );
                         txtPregTime.setText("");
                         secPergTime1.setVisibility( View.GONE );                                                  
                     }                     
                     else
                     {
                         secPergTime.setVisibility( View.VISIBLE );
                         secPergTime1.setVisibility( View.VISIBLE );                         
                     }
                  }
             public void onNothingSelected(AdapterView<?> adapterView) {
                 return;
                     } 
                 }); 
         
         txtPregTime = (EditText)findViewById( R.id.txtPregTime );         
         
         rdogrpTimeType = (RadioGroup)findViewById( R.id.rdogrpTimeType );
         rdoTimeType1 = (RadioButton)findViewById( R.id.rdoTimeType1 );
         rdoTimeType2 = (RadioButton)findViewById( R.id.rdoTimeType2 );
         rdoTimeType3 = (RadioButton)findViewById( R.id.rdoTimeType3 );
         
         spnCause = (Spinner)findViewById( R.id.spnCause );
         List<String> listCause = new ArrayList<String>();
         
         listCause.add("");
         listCause.add("01-গর্ভকালীন জটিলতা");
         listCause.add("02-ডেলিভারীর সময় জটিলতা");
         listCause.add("03-প্রসব পরবর্তী জটিলতা");
         listCause.add("04-উচ্চ রক্তচাপ/স্ট্রোক");
         listCause.add("05-ডায়াবেটিক্স/বহুমুত্র");
         listCause.add("06-মৃগী রোগ");
         listCause.add("07-যক্ষ্মা/টিবি");
         listCause.add("08-হৃদরোগ");
         listCause.add("09-রক্তের রোগ");
         listCause.add("10-এ্যাজমা/ হাঁপানি");
         listCause.add("11-ক্যান্সার");
         listCause.add("12-আঘাতের কারনে");
         listCause.add("13-আত্নহত্যা");
         listCause.add("14-দুর্ঘটনা জনিত কারনে");
         listCause.add("15-কোন সমস্যা ছিল না");
         listCause.add("77-অন্যান্য কারনে মৃত্যু");
         ArrayAdapter<String> adptrCause= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listCause);
         spnCause.setAdapter(adptrCause);
                           
         secFacName.setVisibility(View.GONE);
         secFacOther.setVisibility(View.GONE);
         secWhenTreat.setVisibility(View.GONE);
         
        if(g.getPregOnDeath().equals("1"))
         {
             secPreg.setVisibility( View.VISIBLE);
             secPregDeath.setVisibility( View.VISIBLE );
             secPergTime.setVisibility( View.VISIBLE);
             secPergTime1.setVisibility( View.VISIBLE);               
         }
        else
         {
             secPreg.setVisibility( View.GONE );
             rdogrpPregDeath.clearCheck();
             secPregDeath.setVisibility( View.GONE );
             secPergTime.setVisibility( View.GONE );
             txtPregTime.setText("");
             secPergTime1.setVisibility( View.GONE );               
         }
         
        secCause = (LinearLayout)findViewById( R.id.secCause );
        
        DataSearch(txtHHNo.getText().toString(), txtSNo.getText().toString());

        Button cmdSave = (Button) findViewById(R.id.cmdSave);
        cmdSave.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) { 
            DataSave();
        }});
     }
     catch(Exception  e)
     {
         Connection.MessageBox(Death.this, e.getMessage());
         return;
     }
 }

 private void DataSave()
 {
   try
     {
 
         
         String DV="";

         if(txtHHNo.getText().toString().length()==0)
           {
             Connection.MessageBox(Death.this, "Required field:Household Number.");
             txtHHNo.requestFocus(); 
             return;    
           }
         else if(txtSNo.getText().toString().length()==0)
         {
           Connection.MessageBox(Death.this, "Required field:Serial Number.");
           txtSNo.requestFocus(); 
           return;    
         }
         
         if(Global.Left(spnVisitStatus.getSelectedItem().toString(),2).equals("01"))
         {
             if(spnDthPlace.getSelectedItemPosition()==0 & secDthPlace.isShown()) 
               {
                 Connection.MessageBox(Death.this, "Required field:Place of death.");
                 spnDthPlace.requestFocus(); 
                 return;    
               }
             
             String spnData = Global.Left(spnDthPlace.getSelectedItem().toString(), 2);
             if(spnData.equalsIgnoreCase("77"))
             {
                 if(txtFacOther.getText().toString().length()==0 & secFacOther.isShown()) 
                 {
                   Connection.MessageBox(Death.this, "Required field: Others death place.");
                   spnFacName.requestFocus(); 
                   return;    
                 }
             }
             
             /*else if(txtFacOther.getText().toString().length()==0)
               {
                 Connection.MessageBox(Death.this, "Required field:Others.");
                 txtFacOther.requestFocus(); 
                 return;    
               }*/
             
             if(!rdoTreatment1.isChecked() & !rdoTreatment2.isChecked() & !rdoTreatment3.isChecked() & secTreatment.isShown())
               {
                  Connection.MessageBox(Death.this, "Select anyone options from Treatment.");
                  rdoTreatment1.requestFocus();
                  return;
               }
             if(rdoTreatment1.isChecked()==true)
             {
                  if(txtWhenTreat.getText().toString().length()==0 & secWhenTreat.isShown())
                   {
                     Connection.MessageBox(Death.this, "Required field:if Yes when(how many days before death) did s/he visit health  facility?.");
                     txtWhenTreat.requestFocus(); 
                     return;    
                   }
                  else if(spnFacility.getSelectedItemPosition()==0 & secFacility.isShown()) 
                  {
                    Connection.MessageBox(Death.this, "Required field:Name of the hospital/Chinic  Facility where she/he saught care before died.");
                    spnFacility.requestFocus(); 
                    return;    
                  }
             }
             if(spnDisp.getSelectedItemPosition()==0 & secDisp.isShown())
               {
                 Connection.MessageBox(Death.this, "Required field:Where buried disposed.");
                 spnDisp.requestFocus(); 
                 return;    
               }
             else if(txtWhoDisp.getText().toString().length()==0 & secWhoDisp.isShown())
               {
                 Connection.MessageBox(Death.this, "Required field:Name of the person who buried/disposed the death person.");
                 txtWhoDisp.requestFocus(); 
                 return;    
               }
             else if(spnType.getSelectedItemPosition()==0 & secType.isShown())
             {
               Connection.MessageBox(Death.this, "Required field:Type of the person who buried/disposed the death person.");
               spnDisp.requestFocus(); 
               return;    
             
             }
             else if(txtTime.getText().toString().length()==0 & secTime.isShown())
             {
               Connection.MessageBox(Death.this, "Required field:Timing of disposal of dead body after death(Hours).");
               txtTime.requestFocus(); 
               return;    
             }
              
              if(!rdoPreg1.isChecked() & !rdoPreg2.isChecked() & secPreg.isShown())
              {
                 Connection.MessageBox(Death.this, "Select anyone options from [Was the women ever pregnant].");
                 rdoPreg1.requestFocus();
                 return;
              }
              if(rdoPreg1.isChecked() & rdogrpPreg.isShown())
              {
                  if(!rdoPregDeath1.isChecked() & !rdoPregDeath2.isChecked())
                  {
                     Connection.MessageBox(Death.this, "Select anyone options from [Was the women pregnant at the time of death].");
                     rdoPregDeath1.requestFocus();
                     return;
                  }
                  else if(rdoPregDeath2.isChecked())
                  {
                      if(txtPregTime.getText().length()==0)
                      {
                          Connection.MessageBox(Death.this, "Required field: how many days/months/years age did her last delivery/last birth/still birth/abortion/miscarriage/MR occur.");
                          txtPregTime.requestFocus();
                          return;                      
                      }
                      else if(!rdoTimeType1.isChecked() & !rdoTimeType2.isChecked() & !rdoTimeType3.isChecked())
                      {
                          Connection.MessageBox(Death.this, "Select anyone options from [days/month/years].");
                          rdoTimeType1.requestFocus();
                          return;                      
                      }
                  }
                  
              
              }
              if(spnCause.getSelectedItemPosition()==0 & secCause.isShown())
              {
                  Connection.MessageBox(Death.this, "Select valid cause of death from the list.");
                  spnCause.requestFocus();
                  return;                                    
              }
         }
         
         String SQL = "";
         
           if(!C.Existence("Select Vill from " + TableName + "  Where Vill||Bari||HH = '"+ txtHHNo.getText().toString() +"' and SNo='"+ txtSNo.getText().toString() +"'"))
           {
               SQL = "Insert into " + TableName + "(Vill,Bari,HH,SNo,PNo,UserId,EnDt,Upload,Status)Values('"+ g.getVillageCode() +"','"+ g.getBariCode() +"','"+ g.getHouseholdNo() +"','"+ g.getmemSlNo() +"','"+ g.getPNo() +"','"+ g.getUserId() +"','"+ Global.DateTimeNowYMDHMS() +"','2','')";
               C.Save(SQL);
           }
         SQL = "Update " + TableName + " Set Upload='2',Status='"+ Global.Left(spnVisitStatus.getSelectedItem().toString(),2) +"',";
         SQL+="StartTime= '"+StartTime +"',";
         SQL+="EndTime = '"+g.CurrentTime24() +"',";//g.CurrentTime24
         SQL+="Lat = '"+ Double.toString(currentLatitudeNet) +"',";
         SQL+="Lon = '"+ Double.toString(currentLongitudeNet) +"',";
         SQL+="DthPlace = '"+ (spnDthPlace.getSelectedItemPosition()==0?"":Global.Left(spnDthPlace.getSelectedItem().toString(),2)) +"',";
         //SQL+="FacName = '"+ (spnFacName.getSelectedItemPosition()==0?"":Global.Left(spnFacName.getSelectedItem().toString(),2)) +"',";
         SQL+="FacName = '',";
         SQL+="FacOther = '"+ txtFacOther.getText().toString() +"',";
         RadioButton rbTreatment = (RadioButton)findViewById(rdogrpTreatment.getCheckedRadioButtonId());
         SQL+="Treatment = '"+ (rbTreatment==null?"":(Global.Left(rbTreatment.getText().toString(),1))) +"',";
         SQL+="WhenTreat = '"+ txtWhenTreat.getText().toString() +"',";
         SQL+="Facility = '"+ (spnFacility.getSelectedItemPosition()==0?"":Global.Left(spnFacility.getSelectedItem().toString(),2)) +"',";
         SQL+="Disp = '"+ (spnDisp.getSelectedItemPosition()==0?"":Global.Left(spnDisp.getSelectedItem().toString(),2)) +"',";
         SQL+="WhoDisp = '"+ txtWhoDisp.getText().toString() +"',";
         SQL+="Type = '"+ (spnType.getSelectedItemPosition()==0?"":Global.Left(spnType.getSelectedItem().toString(),2)) +"',";
         SQL+="Time = '"+ txtTime.getText().toString() +"',";
         
         RadioButton rbeverPreg = (RadioButton)findViewById(rdogrpPreg.getCheckedRadioButtonId());
         SQL+="EverPreg = '"+ (rbeverPreg==null?"":(Global.Left(rbeverPreg.getText().toString(),1))) +"',";
         RadioButton rbPregondeath = (RadioButton)findViewById(rdogrpPregDeath.getCheckedRadioButtonId());
         SQL+="PregonDeath = '"+ (rbPregondeath==null?"":(Global.Left(rbPregondeath.getText().toString(),1))) +"',";         
         SQL+="LastPregTime = '"+ txtPregTime.getText().toString() +"',";
         RadioButton rbTimeType = (RadioButton)findViewById(rdogrpTimeType.getCheckedRadioButtonId());
         SQL+="DMY = '"+ (rbTimeType==null?"":(Global.Left(rbTimeType.getText().toString(),1))) +"',";
         SQL+="CauseofDeath = '"+ (spnCause.getSelectedItemPosition()==0?"":Global.Left(spnCause.getSelectedItem().toString(),2)) +"'";
         
         
         SQL+="  Where Vill||Bari||HH = '"+ txtHHNo.getText().toString() +"' and SNo='"+ txtSNo.getText().toString() +"'";
         C.Save(SQL);
         Connection.MessageBox(Death.this, "Saved Successfully");
         finish();
     }
     catch(Exception  e)
     {
         Connection.MessageBox(Death.this, e.getMessage());
         return;
     }
 }
 private void DataSearch(String HHNo, String SNo)
     {
       try
        {
           RadioButton rb;
           Cursor cur = C.ReadData("Select * from "+ TableName +"  Where Vill||Bari||HH='"+ HHNo +"' and SNo='"+ SNo +"'");
           cur.moveToFirst();
           while(!cur.isAfterLast())
           {
             spnVisitStatus.setSelection(Global.SpinnerItemPosition(spnVisitStatus, 2 ,cur.getString(cur.getColumnIndex("Status"))));
             spnDthPlace.setSelection(Global.SpinnerItemPosition(spnDthPlace, 2 ,cur.getString(cur.getColumnIndex("DthPlace"))));
             //spnFacName.setSelection(Global.SpinnerItemPosition(spnFacName, 2 ,cur.getString(cur.getColumnIndex("FacName"))));
             txtFacOther.setText(cur.getString(cur.getColumnIndex("FacOther")));
             for (int i = 0; i < rdogrpTreatment.getChildCount(); i++)
             {
                rb = (RadioButton)rdogrpTreatment.getChildAt(i);
                if (Global.Left(rb.getText().toString(), 1).equalsIgnoreCase(cur.getString(cur.getColumnIndex("Treatment"))))
                   rb.setChecked(true);
                else
                   rb.setChecked(false);
             }
             txtWhenTreat.setText(cur.getString(cur.getColumnIndex("WhenTreat")));
             spnFacility.setSelection(Global.SpinnerItemPosition(spnFacility, 2 ,cur.getString(cur.getColumnIndex("Facility"))));
             spnDisp.setSelection(Global.SpinnerItemPosition(spnDisp, 2 ,cur.getString(cur.getColumnIndex("Disp"))));
             txtWhoDisp.setText(cur.getString(cur.getColumnIndex("WhoDisp")));
             spnType.setSelection(Global.SpinnerItemPosition(spnDisp, 2 ,cur.getString(cur.getColumnIndex("Type"))));
             txtTime.setText(cur.getString(cur.getColumnIndex("Time")));
             
             for (int i = 0; i < rdogrpPreg.getChildCount(); i++)
             {
                rb = (RadioButton)rdogrpPreg.getChildAt(i);
                if (Global.Left(rb.getText().toString(), 1).equalsIgnoreCase(cur.getString(cur.getColumnIndex("EverPreg"))))
                   rb.setChecked(true);
                else
                   rb.setChecked(false);
             }
             for (int i = 0; i < rdogrpPregDeath.getChildCount(); i++)
             {
                rb = (RadioButton)rdogrpPregDeath.getChildAt(i);
                if (Global.Left(rb.getText().toString(), 1).equalsIgnoreCase(cur.getString(cur.getColumnIndex("PregonDeath"))))
                   rb.setChecked(true);
                else
                   rb.setChecked(false);
             }
             txtPregTime.setText(cur.getString(cur.getColumnIndex("LastPregTime")));
             for (int i = 0; i < rdogrpTimeType.getChildCount(); i++)
             {
                rb = (RadioButton)rdogrpTimeType.getChildAt(i);
                if (Global.Left(rb.getText().toString(), 1).equalsIgnoreCase(cur.getString(cur.getColumnIndex("DMY"))))
                   rb.setChecked(true);
                else
                   rb.setChecked(false);
             }
             spnCause.setSelection(Global.SpinnerItemPosition(spnCause, 2 ,cur.getString(cur.getColumnIndex("CauseofDeath"))));
             
             
             cur.moveToNext();
           }
           cur.close();
        }
        catch(Exception  e)
        {
            Connection.MessageBox(Death.this, e.getMessage());
            return;
        }
     }




 protected Dialog onCreateDialog(int id) {
   final Calendar c = Calendar.getInstance();
   hour = c.get(Calendar.HOUR_OF_DAY);
   minute = c.get(Calendar.MINUTE);
   switch (id) {
       case DATE_DIALOG:
           return new DatePickerDialog(this, mDateSetListener,g.mYear,g.mMonth-1,g.mDay);
         }
     return null;
 }


 private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
              mYear = year; mMonth = monthOfYear+1; mDay = dayOfMonth;
              EditText dtpDate;
            /*  dtpDate = (EditText)findViewById(R.id.dtpVdate);
             if (VariableID.equals("btnVdate"))
              {
                  dtpDate = (EditText)findViewById(R.id.dtpVdate);
              }
      dtpDate.setText(new StringBuilder()
      .append(Global.Right("00"+mDay,2)).append("/")
      .append(Global.Right("00"+mMonth,2)).append("/")
      .append(mYear));*/
      }
    };

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
        currentLocation = location; 
        currentLatitude = currentLocation.getLatitude(); 
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
        currentLocationNet = location1; 
        currentLatitudeNet = currentLocationNet.getLatitude(); 
        currentLongitudeNet = currentLocationNet.getLongitude(); 
    } 

  }
