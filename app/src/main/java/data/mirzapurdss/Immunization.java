package data.mirzapurdss;

import static data.mirzapurdss.MemberEvents.DATE_DIALOG;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;



public class Immunization extends AppCompatActivity {
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuclose, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
                 AlertDialog.Builder adb = new AlertDialog.Builder(Immunization.this);
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
    
    //Disabled Back/Home key
    //--------------------------------------------------------------------------------------------------
    @Override 
    public boolean onKeyDown(int iKeyCode, KeyEvent event)
    {
        if(iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME) 
             { return false; }
        else { return true;  }
    }


    Connection C;
    Global g;

    private static int TAKE_PICTURE = 1;
    private Uri outputFileUri;

    ImageButton  mImageView1;
    ImageButton  mImageView2;
    ImageButton  mImageView3;
    ImageButton  mImageView4;
    ImageButton  mImageView5;
    String Vill;
    String Bari;
    String HH;
    String PNumber;
    Spinner  spnMemList;
    ArrayAdapter dataSource;
    Spinner  spnVaccineStatus;
    String VariableID;

    private int mDay;
    private int mMonth;
    private int mYear;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.immunization);
        C = new Connection(this);
        g = Global.getInstance();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        try
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Vill = g.getVillageCode();
            Bari = g.getBariCode();
            HH   = g.getHouseholdNo();

        spnMemList = (Spinner)findViewById(R.id.spnMemList);
        //spnMemList.setAdapter(C.getArrayAdapter("Select (SNo||':'||Name||',DOB:'||(substr(bdate,9,2)||'/'||substr(bdate,6,2)||'/'||substr(bdate,1,4))||', Age(Yr):'||cast((julianday(date('now'))-julianday(bdate))/365.25 as int))MemInfo from tTrans where status='m' and length(extype)=0 and vill||bari||hh='"+ (Vill+Bari+HH) +"' " +
        //        " and cast((julianday(date('now'))-julianday(bdate))/30.4 as int)<=59"));

        spnMemList.setAdapter(C.getArrayAdapter("Select (SNo||':'||Name||',DOB:'||(substr(bdate,9,2)||'/'||substr(bdate,6,2)||'/'||substr(bdate,1,4))||', Age(Yr):'||cast((julianday(date('now'))-julianday(bdate))/365.25 as int))MemInfo from tTrans where status='m' and vill||bari||hh='"+ (Vill+Bari+HH) +"'" +
                " and cast((julianday(date('now'))-julianday(bdate))/30.4 as int)<=180"));

        ArrayAdapter listVaccineStatus = ArrayAdapter.createFromResource(this, R.array.listVaccineStatus, android.R.layout.simple_spinner_item);
        listVaccineStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVaccineStatus = (Spinner)findViewById(R.id.spnVaccineStatus);
        spnVaccineStatus.setAdapter(listVaccineStatus);

        dataSource = ArrayAdapter.createFromResource(this, R.array.listVaccineCenter, android.R.layout.simple_spinner_item);
        dataSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    
        final CheckBox chkBCG = (CheckBox)findViewById(R.id.chkBCG);
        final CheckBox chkBCGDTDk = (CheckBox)findViewById(R.id.chkBCGDTDk);
        final EditText BCGDT = (EditText)findViewById(R.id.BCGDT);
        final Spinner  spnBCG= (Spinner)findViewById(R.id.spnBCG);
        spnBCG.setAdapter(dataSource);
        
        final CheckBox chkPenta1 = (CheckBox)findViewById(R.id.chkPenta1);
        final CheckBox chkPenta1DTDK = (CheckBox)findViewById(R.id.chkPenta1DTDK);
        final EditText Penta1DT = (EditText)findViewById(R.id.Penta1DT);
        final Spinner  spnPenta1 = (Spinner)findViewById(R.id.spnPenta1);
        spnPenta1.setAdapter(dataSource);
        
        final CheckBox chkPenta2 = (CheckBox)findViewById(R.id.chkPenta2);
        final CheckBox chkPenta2DTDK = (CheckBox)findViewById(R.id.chkPenta2DTDK);
        final EditText Penta2DT = (EditText)findViewById(R.id.Penta2DT);
        final Spinner  spnPenta2 = (Spinner)findViewById(R.id.spnPenta2);
        spnPenta2.setAdapter(dataSource);
        
        final CheckBox chkPenta3 = (CheckBox)findViewById(R.id.chkPenta3);
        final CheckBox chkPenta3DTDK = (CheckBox)findViewById(R.id.chkPenta3DTDK);
        final EditText Penta3DT = (EditText)findViewById(R.id.Penta3DT);
        final Spinner  spnPenta3 = (Spinner)findViewById(R.id.spnPenta3);
        spnPenta3.setAdapter(dataSource);
        
        final CheckBox chkPCV1 = (CheckBox)findViewById(R.id.chkPCV1);
        final CheckBox chkPCV1DTDK = (CheckBox)findViewById(R.id.chkPCV1DTDK);
        final EditText PCV1DT = (EditText)findViewById(R.id.PCV1DT);
        final Spinner  spnPCV1 = (Spinner)findViewById(R.id.spnPCV1);
        spnPCV1.setAdapter(dataSource);
        
        final CheckBox chkPCV2 = (CheckBox)findViewById(R.id.chkPCV2);
        final CheckBox chkPCV2DTDK = (CheckBox)findViewById(R.id.chkPCV2DTDK);
        final EditText PCV2DT = (EditText)findViewById(R.id.PCV2DT);
        final Spinner  spnPCV2 = (Spinner)findViewById(R.id.spnPCV2);
        spnPCV2.setAdapter(dataSource);
        
        final CheckBox chkPCV3 = (CheckBox)findViewById(R.id.chkPCV3);
        final CheckBox chkPCV3DTDK = (CheckBox)findViewById(R.id.chkPCV3DTDK);
        final EditText PCV3DT = (EditText)findViewById(R.id.PCV3DT);
        final Spinner  spnPCV3 = (Spinner)findViewById(R.id.spnPCV3);
        spnPCV3.setAdapter(dataSource);
        
        final CheckBox chkOPV0 = (CheckBox)findViewById(R.id.chkOPV0);
        final CheckBox chkOPV0DTDK = (CheckBox)findViewById(R.id.chkOPV0DTDK);
        final EditText OPV0DT = (EditText)findViewById(R.id.OPV0DT);
        final Spinner  spnOPV0 = (Spinner)findViewById(R.id.spnOPV0);
        spnOPV0.setAdapter(dataSource);
        
        final CheckBox chkOPV1 = (CheckBox)findViewById(R.id.chkOPV1);
        final CheckBox chkOPV1DTDK = (CheckBox)findViewById(R.id.chkOPV1DTDK);
        final EditText OPV1DT = (EditText)findViewById(R.id.OPV1DT);
        final Spinner  spnOPV1 = (Spinner)findViewById(R.id.spnOPV1);
        spnOPV1.setAdapter(dataSource);
        
        final CheckBox chkOPV2 = (CheckBox)findViewById(R.id.chkOPV2);
        final CheckBox chkOPV2DTDK = (CheckBox)findViewById(R.id.chkOPV2DTDK);
        final EditText OPV2DT = (EditText)findViewById(R.id.OPV2DT);
        final Spinner  spnOPV2 = (Spinner)findViewById(R.id.spnOPV2);
        spnOPV2.setAdapter(dataSource);
        
        final CheckBox chkOPV3 = (CheckBox)findViewById(R.id.chkOPV3);
        final CheckBox chkOPV3DTDK = (CheckBox)findViewById(R.id.chkOPV3DTDK);
        final EditText OPV3DT = (EditText)findViewById(R.id.OPV3DT);
        final Spinner  spnOPV3 = (Spinner)findViewById(R.id.spnOPV3);
        spnOPV3.setAdapter(dataSource);
        
        final CheckBox chkOPV4 = (CheckBox)findViewById(R.id.chkOPV4);
        final CheckBox chkOPV4DTDK = (CheckBox)findViewById(R.id.chkOPV4DTDK);
        final EditText OPV4DT = (EditText)findViewById(R.id.OPV4DT);
        final Spinner  spnOPV4 = (Spinner)findViewById(R.id.spnOPV4);
        spnOPV4.setAdapter(dataSource);
        
        final CheckBox chkMeasles = (CheckBox)findViewById(R.id.chkMeasles);
        final CheckBox chkMeaslesDTDK = (CheckBox)findViewById(R.id.chkMeaslesDTDK);
        final EditText MeaslesDT = (EditText)findViewById(R.id.MeaslesDT);
        final Spinner  spnMeasles = (Spinner)findViewById(R.id.spnMeasles);
        spnMeasles.setAdapter(dataSource);
        
        final CheckBox chkMR = (CheckBox)findViewById(R.id.chkMR);
        final CheckBox chkMRDTDK = (CheckBox)findViewById(R.id.chkMRDTDK);
        final EditText  MRDT = (EditText)findViewById(R.id.MRDT);
        final Spinner  spnMR = (Spinner)findViewById(R.id.spnMR);
        spnMR.setAdapter(dataSource);
        
        final CheckBox chkRota = (CheckBox)findViewById(R.id.chkRota);
        final CheckBox chkRotaDTDK = (CheckBox)findViewById(R.id.chkRotaDTDK);
        final EditText  RotaDT = (EditText)findViewById(R.id.RotaDT);
        final Spinner  spnRota = (Spinner)findViewById(R.id.spnRota);
        spnRota.setAdapter(dataSource);
        
        final CheckBox chkMMR = (CheckBox)findViewById(R.id.chkMMR);
        final CheckBox chkMMRDTDK = (CheckBox)findViewById(R.id.chkMMRDTDK);
        final EditText  MMRDT = (EditText)findViewById(R.id.MMRDT);
        final Spinner  spnMMR = (Spinner)findViewById(R.id.spnMMR);
        spnMMR.setAdapter(dataSource);
        
        final CheckBox chkTyphoid = (CheckBox)findViewById(R.id.chkTyphoid);
        final CheckBox chkTyphoidDTDK = (CheckBox)findViewById(R.id.chkTyphoidDTDK);
        final EditText  TyphoidDT = (EditText)findViewById(R.id.TyphoidDT);
        final Spinner  spnTyphoid = (Spinner)findViewById(R.id.spnTyphoid);
        spnTyphoid.setAdapter(dataSource);
        
        final CheckBox chkInflu = (CheckBox)findViewById(R.id.chkInflu);
        final CheckBox chkInfluDTDK = (CheckBox)findViewById(R.id.chkInfluDTDK);
        final EditText  InfluDT = (EditText)findViewById(R.id.InfluDT);
        final Spinner  spnInflu = (Spinner)findViewById(R.id.spnInflu);
        spnInflu.setAdapter(dataSource);
        
        final CheckBox chkHepaA = (CheckBox)findViewById(R.id.chkHepaA);
        final CheckBox chkHepaADTDk = (CheckBox)findViewById(R.id.chkHepaADTDk);
        final EditText  HepaADT = (EditText)findViewById(R.id.HepaADT);
        final Spinner  spnHepaA = (Spinner)findViewById(R.id.spnHepaA);
        spnHepaA.setAdapter(dataSource);
        
        final CheckBox chkChickenPox = (CheckBox)findViewById(R.id.chkChickenPox);
        final CheckBox chkChickenPoxDTDk = (CheckBox)findViewById(R.id.chkChickenPoxDTDk);
        final EditText  ChickenPoxDT = (EditText)findViewById(R.id.ChickenPoxDT);
        final Spinner  spnChickenPox = (Spinner)findViewById(R.id.spnChickenPox);
        spnChickenPox.setAdapter(dataSource);
        
        final CheckBox chkRabies = (CheckBox)findViewById(R.id.chkRabies);
        final CheckBox chkRabiesDTDk = (CheckBox)findViewById(R.id.chkRabiesDTDk);
        final EditText  RabiesDT = (EditText)findViewById(R.id.RabiesDT);
        final Spinner  spnRabies = (Spinner)findViewById(R.id.spnRabies);
        spnRabies.setAdapter(dataSource);
        
        final CheckBox chkIPV = (CheckBox)findViewById(R.id.chkIPV);
        final CheckBox chkIPVDTDk = (CheckBox)findViewById(R.id.chkIPVDTDk);
        final EditText IPVDT = (EditText)findViewById(R.id.IPVDT);
        final Spinner  spnIPV = (Spinner)findViewById(R.id.spnIPV);
        spnIPV.setAdapter(dataSource);

        //23apr 2018
        final CheckBox chkfIPV1 = (CheckBox)findViewById(R.id.chkfIPV1);
        final CheckBox chkFipvdt1dk = (CheckBox)findViewById(R.id.chkFipvdt1dk);
        final EditText fIPVDT1 = (EditText)findViewById(R.id.fIPVDT1);
        final Spinner  spnfIPV1 = (Spinner)findViewById(R.id.spnfIPV1);
        spnfIPV1.setAdapter(dataSource);

        final CheckBox chkfIPV2 = (CheckBox)findViewById(R.id.chkfIPV2);
        final CheckBox chkFipvdt2Dk = (CheckBox)findViewById(R.id.chkFipvdt2Dk);
        final EditText fIPVDT2 = (EditText)findViewById(R.id.fIPVDT2);
        final Spinner  spnfIPV2 = (Spinner)findViewById(R.id.spnfIPV2);
        spnfIPV2.setAdapter(dataSource);

        final CheckBox chkVitaminA = (CheckBox)findViewById(R.id.chkVitaminA);
        final CheckBox chkVitaminADTDk = (CheckBox)findViewById(R.id.chkVitaminADTDk);
        final EditText VitaminADT = (EditText)findViewById(R.id.VitaminADT);
        final Spinner  spnVitaminA = (Spinner)findViewById(R.id.spnVitaminA);
        spnVitaminA.setAdapter(dataSource);

        final Button cmdImmSave  = (Button)findViewById(R.id.cmdImmSave);
        final Button cmdImmClose = (Button)findViewById(R.id.cmdImmClose);
        mImageView1 = (ImageButton)findViewById(R.id.imgForm1);
        mImageView2 = (ImageButton)findViewById(R.id.imgForm2);
        mImageView3 = (ImageButton)findViewById(R.id.imgForm3);
        mImageView4 = (ImageButton)findViewById(R.id.imgForm4);
        mImageView5 = (ImageButton)findViewById(R.id.imgForm5);

        mImageView1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;

                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                PhotoView(Vill+Bari+HH+SN+"1");
                }catch(Exception ex)
                {
                    
                }
            }});
        mImageView2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;

                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                PhotoView(Vill+Bari+HH+SN+"2");
                }catch(Exception ex)
                {
                    
                }                
            }});
        mImageView3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;

                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                PhotoView(Vill+Bari+HH+SN+"3");
                }catch(Exception ex)
                {
                    
                }                
            }});
        mImageView4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;

                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                PhotoView(Vill+Bari+HH+SN+"4");
                }catch(Exception ex)
                {
                    
                }                
            }});
        mImageView5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try
                {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;

                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                PhotoView(Vill+Bari+HH+SN+"5");
                }catch(Exception ex)
                {
                    
                }
            }});
            
            Button cmdPhoto1 = (Button)findViewById(R.id.cmdPhoto1);
            Button cmdPhoto2 = (Button)findViewById(R.id.cmdPhoto2);
            Button cmdPhoto3 = (Button)findViewById(R.id.cmdPhoto3);
            Button cmdPhoto4 = (Button)findViewById(R.id.cmdPhoto4);
            Button cmdPhoto5 = (Button)findViewById(R.id.cmdPhoto5);
            
            
            cmdPhoto1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    TakePhoto(Vill+Bari+HH+SN+"1");
                    }catch(Exception ex)
                    {
                        
                    }
                }});
            cmdPhoto2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    TakePhoto(Vill+Bari+HH+SN+"2");
                    }catch(Exception ex)
                    {
                        
                    }                    
                }});
            cmdPhoto3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    TakePhoto(Vill+Bari+HH+SN+"3");
                    }catch(Exception ex)
                    {
                        
                    }                    
                }});
            cmdPhoto4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    TakePhoto(Vill+Bari+HH+SN+"4");
                    }catch(Exception ex)
                    {
                        
                    }                    
                }});
            cmdPhoto5.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                    if(spnMemList.getCount()==0) return;
                    if(spnMemList.getSelectedItem().toString().length()==0) return;
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    TakePhoto(Vill+Bari+HH+SN+"5");
                    }catch(Exception ex)
                    {
                        
                    }                    
                }});

            if(spnMemList.getCount()>0)
            {   
                
                String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);

                File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"1.jpg");
                if(mFichier1.exists())
                {
                        mImageView1.setImageURI(Uri.fromFile(mFichier1));
                }
                mImageView1.setVisibility(View.VISIBLE);
                
                File mFichier2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"2.jpg");
                if(mFichier2.exists())
                {
                        mImageView2.setImageURI(Uri.fromFile(mFichier2));
                }
                mImageView2.setVisibility(View.VISIBLE);
                
                File mFichier3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"3.jpg");
                if(mFichier3.exists())
                {
                        mImageView3.setImageURI(Uri.fromFile(mFichier3));
                }
                mImageView3.setVisibility(View.VISIBLE);

                File mFichier4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"4.jpg");
                if(mFichier4.exists())
                {
                        mImageView4.setImageURI(Uri.fromFile(mFichier4));
                }
                mImageView4.setVisibility(View.VISIBLE);

                File mFichier5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"5.jpg");
                if(mFichier5.exists())
                {
                        mImageView5.setImageURI(Uri.fromFile(mFichier5));
                }
                mImageView5.setVisibility(View.VISIBLE);
                
            }
            
            
            Button cmdShow = (Button)findViewById(R.id.cmdShow);
            cmdShow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try
                    {
                        if(spnMemList.getCount()==0) return;
                        if(spnMemList.getSelectedItem().toString().length()==0) return;

                        String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
    
                        File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"1.jpg");
                        if(mFichier1.exists())
                        {
                                mImageView1.setImageURI(Uri.fromFile(mFichier1));
                        }
                        mImageView1.setVisibility(View.VISIBLE);
                        
                        File mFichier2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"2.jpg");
                        if(mFichier2.exists())
                        {
                                mImageView2.setImageURI(Uri.fromFile(mFichier2));
                        }
                        mImageView2.setVisibility(View.VISIBLE);
                        
                        File mFichier3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"3.jpg");
                        if(mFichier3.exists())
                        {
                                mImageView3.setImageURI(Uri.fromFile(mFichier3));
                        }
                        mImageView3.setVisibility(View.VISIBLE);
                        
                        File mFichier4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"4.jpg");
                        if(mFichier4.exists())
                        {
                                mImageView4.setImageURI(Uri.fromFile(mFichier4));
                        }
                        mImageView4.setVisibility(View.VISIBLE);
    
                        File mFichier5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"5.jpg");
                        if(mFichier5.exists())
                        {
                                mImageView5.setImageURI(Uri.fromFile(mFichier5));
                        }
                        mImageView5.setVisibility(View.VISIBLE);
                        
                    }
                    catch(Exception ex)
                    {
                        Connection.MessageBox( Immunization.this, ex.getMessage() );
                        return;
                    }

                }});

            chkBCGDTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        BCGDT.setEnabled(false);
                        BCGDT.setText("");
                    }
                    else
                    {
                        BCGDT.setEnabled(true);
                    }
                }
            });

            chkPenta1DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        Penta1DT.setEnabled(false);
                        Penta1DT.setText("");
                    }
                    else
                    {
                        Penta1DT.setEnabled(true);
                    }
                }
            });

            chkPenta2DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        Penta2DT.setEnabled(false);
                        Penta2DT.setText("");
                    }
                    else
                    {
                        Penta2DT.setEnabled(true);
                    }
                }
            });

            chkPenta3DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        Penta3DT.setEnabled(false);
                        Penta3DT.setText("");
                    }
                    else
                    {
                        Penta3DT.setEnabled(true);
                    }
                }
            });
            chkPCV1DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        PCV1DT.setEnabled(false);
                        PCV1DT.setText("");
                    }
                    else
                    {
                        PCV1DT.setEnabled(true);
                    }
                }
            });
            chkPCV2DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        PCV2DT.setEnabled(false);
                        PCV2DT.setText("");
                    }
                    else
                    {
                        PCV2DT.setEnabled(true);
                    }
                }
            });
            chkPCV3DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        PCV3DT.setEnabled(false);
                        PCV3DT.setText("");
                    }
                    else
                    {
                        PCV3DT.setEnabled(true);
                    }
                }
            });
            chkOPV0DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        OPV0DT.setEnabled(false);
                        OPV0DT.setText("");
                    }
                    else
                    {
                        OPV0DT.setEnabled(true);
                    }
                }
            });
            chkOPV1DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        OPV1DT.setEnabled(false);
                        OPV1DT.setText("");
                    }
                    else
                    {
                        OPV1DT.setEnabled(true);
                    }
                }
            });
            chkOPV2DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        OPV2DT.setEnabled(false);
                        OPV2DT.setText("");
                    }
                    else
                    {
                        OPV2DT.setEnabled(true);
                    }
                }
            });
            chkOPV3DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        OPV3DT.setEnabled(false);
                        OPV3DT.setText("");
                    }
                    else
                    {
                        OPV3DT.setEnabled(true);
                    }
                }
            });
            chkOPV4DTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        OPV4DT.setEnabled(false);
                        OPV4DT.setText("");
                    }
                    else
                    {
                        OPV4DT.setEnabled(true);
                    }
                }
            });
            chkMeaslesDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        MeaslesDT.setEnabled(false);
                        MeaslesDT.setText("");
                    }
                    else
                    {
                        MeaslesDT.setEnabled(true);
                    }
                }
            });
            chkMRDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        MRDT.setEnabled(false);
                        MRDT.setText("");
                    }
                    else
                    {
                        MRDT.setEnabled(true);
                    }
                }
            });
            chkRotaDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        RotaDT.setEnabled(false);
                        RotaDT.setText("");
                    }
                    else
                    {
                        RotaDT.setEnabled(true);
                    }
                }
            });
            chkRotaDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        RotaDT.setEnabled(false);
                        RotaDT.setText("");
                    }
                    else
                    {
                        RotaDT.setEnabled(true);
                    }
                }
            });
            chkMMRDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        MMRDT.setEnabled(false);
                        MMRDT.setText("");
                    }
                    else
                    {
                        MMRDT.setEnabled(true);
                    }
                }
            });
            chkTyphoidDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        TyphoidDT.setEnabled(false);
                        TyphoidDT.setText("");
                    }
                    else
                    {
                        TyphoidDT.setEnabled(true);
                    }
                }
            });
            chkInfluDTDK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        InfluDT.setEnabled(false);
                        InfluDT.setText("");
                    }
                    else
                    {
                        InfluDT.setEnabled(true);
                    }
                }
            });
            chkHepaADTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        HepaADT.setEnabled(false);
                        HepaADT.setText("");
                    }
                    else
                    {
                        HepaADT.setEnabled(true);
                    }
                }
            });
            chkChickenPoxDTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        ChickenPoxDT.setEnabled(false);
                        ChickenPoxDT.setText("");
                    }
                    else
                    {
                        ChickenPoxDT.setEnabled(true);
                    }
                }
            });
            chkRabiesDTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        RabiesDT.setEnabled(false);
                        RabiesDT.setText("");
                    }
                    else
                    {
                        RabiesDT.setEnabled(true);
                    }
                }
            });
            chkIPVDTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        IPVDT.setEnabled(false);
                        IPVDT.setText("");
                    }
                    else
                    {
                        IPVDT.setEnabled(true);
                    }
                }
            });
            chkFipvdt1dk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        fIPVDT1.setEnabled(false);
                        fIPVDT1.setText("");
                    }
                    else
                    {
                        fIPVDT1.setEnabled(true);
                    }
                }
            });
            chkFipvdt2Dk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        fIPVDT2.setEnabled(false);
                        fIPVDT2.setText("");
                    }
                    else
                    {
                        fIPVDT2.setEnabled(true);
                    }
                }
            });
            chkVitaminADTDk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        VitaminADT.setEnabled(false);
                        VitaminADT.setText("");
                    }
                    else
                    {
                        VitaminADT.setEnabled(true);
                    }
                }
            });

            BCGDT.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int DRAWABLE_RIGHT = 2;
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (BCGDT.getRight() - BCGDT.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            VariableID = "BCGDT";
                            showDialog(DATE_DIALOG);

                            return true;
                        }
                    }
                    return false;
                }
            });

        spnMemList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    try
                    {
                        String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                        String PNoAvailable="1";
                        if(C.Existence("Select Vill from tTrans Where status='m' and Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"' and (length(pno)=0 or pno is null)"))
                        {
                                PNoAvailable="2";
                        }
                        else
                        {
                        }
                    
                        if(PNoAvailable.equals("1")) {
                            PNumber = C.ReturnSingleValue("Select PNo from tTrans Where Status='m' and Vill||Bari||HH||SNo='" + Vill + Bari + HH + SN + "'");
                        }else {
                            PNumber = "";
                        }
                            //if(PNumber.length()==0) return;

                            chkBCG.setChecked(false);
                            chkBCGDTDk.setChecked(false);

                            chkPenta1.setChecked(false);
                            chkPenta1DTDK.setChecked(false);
                            chkPenta2.setChecked(false);
                            chkPenta2DTDK.setChecked(false);
                            chkPenta3.setChecked(false);
                            chkPenta3DTDK.setChecked(false);
                            chkPCV1.setChecked(false);
                            chkPCV1DTDK.setChecked(false);
                            chkPCV2.setChecked(false);
                            chkPCV2DTDK.setChecked(false);
                            chkPCV3.setChecked(false);
                            chkPCV3DTDK.setChecked(false);
                            chkOPV0.setChecked(false);
                            chkOPV0DTDK.setChecked(false);
                            chkOPV1.setChecked(false);
                            chkOPV2.setChecked(false);
                            chkOPV3.setChecked(false);
                            chkOPV4.setChecked(false);
                            chkOPV1DTDK.setChecked(false);
                            chkOPV2DTDK.setChecked(false);
                            chkOPV3DTDK.setChecked(false);
                            chkOPV4DTDK.setChecked(false);

                            chkMeasles.setChecked(false);
                            chkMeaslesDTDK.setChecked(false);
                            chkMR.setChecked(false);
                            chkMRDTDK.setChecked(false);
                            chkRota.setChecked(false);
                            chkRotaDTDK.setChecked(false);
                            chkMMR.setChecked(false);
                            chkMMRDTDK.setChecked(false);
                            chkTyphoid.setChecked(false);
                            chkTyphoidDTDK.setChecked(false);
                            chkInflu.setChecked(false);
                            chkInfluDTDK.setChecked(false);
                            chkHepaA.setChecked(false);
                            chkHepaADTDk.setChecked(false);
                            chkChickenPox.setChecked(false);
                            chkChickenPoxDTDk.setChecked(false);
                            chkRabies.setChecked(false);
                            chkRabiesDTDk.setChecked(false);
                            chkIPV.setChecked(false);
                            chkIPVDTDk.setChecked(false);
                            chkfIPV1.setChecked(false);
                            chkFipvdt1dk.setChecked(false);
                            chkfIPV2.setChecked(false);
                            chkFipvdt2Dk.setChecked(false);
                            chkVitaminA.setChecked(false);
                            chkVitaminADTDk.setChecked(false);

                            BCGDT.setText("");
                            spnBCG.setSelection(0);

                            Penta1DT.setText("");
                            spnPenta1.setSelection(0);

                            Penta2DT.setText("");
                            spnPenta2.setSelection(0);

                            Penta3DT.setText("");
                            spnPenta3.setSelection(0);

                            PCV1DT.setText("");
                            spnPCV1.setSelection(0);

                            PCV2DT.setText("");
                            spnPCV2.setSelection(0);

                            PCV3DT.setText("");
                            spnPCV3.setSelection(0);

                            OPV0DT.setText("");
                            spnOPV0.setSelection(0);

                            OPV1DT.setText("");
                            spnOPV1.setSelection(0);

                            OPV2DT.setText("");
                            spnOPV2.setSelection(0);

                            OPV3DT.setText("");
                            spnOPV3.setSelection(0);

                            OPV4DT.setText("");
                            spnOPV4.setSelection(0);

                            MeaslesDT.setText("");
                            spnMeasles.setSelection(0);

                            MRDT.setText("");
                            spnMR.setSelection(0);

                            RotaDT.setText("");
                            spnRota.setSelection(0);

                            MMRDT.setText("");
                            spnMMR.setSelection(0);

                            TyphoidDT.setText("");
                            spnTyphoid.setSelection(0);

                            InfluDT.setText("");
                            spnInflu.setSelection(0);

                            HepaADT.setText("");
                            spnHepaA.setSelection(0);

                            ChickenPoxDT.setText("");
                            spnChickenPox.setSelection(0);

                            RabiesDT.setText("");
                            spnRabies.setSelection(0);

                            IPVDT.setText("");
                            spnIPV.setSelection(0);

                            fIPVDT1.setText("");
                            spnfIPV1.setSelection(0);

                            fIPVDT2.setText("");
                            spnfIPV2.setSelection(0);

                            VitaminADT.setText("");
                            spnVitaminA.setSelection(0);

                            Cursor cur = null;
                            if (PNoAvailable.equals("1")) {
                                cur = C.ReadData("Select Vill,Bari,HH,Sno,PNO,Status,BCG,BCGDT,BCGSource,Penta1,Penta1DT,Penta1Source,Penta2,Penta2DT,Penta2Source,Penta3,Penta3DT,Penta3Source,PCV1,PCV1DT,PCV1Source,PCV2,PCV2DT,PCV2Source,PCV3,PCV3DT,PCV3Source,OPV0,OPV0DT,OPV0Source,OPV1,OPV1DT,OPV1Source,OPV2,OPV2DT,OPV2Source,OPV3,OPV3DT,OPV3Source,\n" +
                                        "OPV4,OPV4DT,OPV4Source,Measles,MeaslesDT,MeaslesSource,MR,MRDT,MRSource,Rota,RotaDT,RotaSource,MMR,MMRDT,MMRSource,Typhoid,TyphoidDT,TyphoidSource,Influ,InfluDT,InfluSource,HepaA,HepaADT,HepaASource,ChickenPox,ChickenPoxDT,ChickenPoxSource,Rabies,RabiesDT,RabiesSource,IPV,IPVDT,IPVSource,fIPV1,fIPVDT1,\n" +
                                        "fIPVSource1,fIPV2,fIPVDT2,fIPVSource2,VitaminA,VitaminADT,VitaminASource,ifnull(BCGDTDk,'')BCGDTDk,ifnull(Penta1DTDK,'')Penta1DTDK,ifnull(Penta2DTDK,'')Penta2DTDK,ifnull(Penta3DTDK,'')Penta3DTDK,ifnull(PCV1DTDK,'')PCV1DTDK,ifnull(PCV2DTDK,'')PCV2DTDK,ifnull(PCV3DTDK,'')PCV3DTDK,ifnull(OPV0DTDK,'')OPV0DTDK,\n" +
                                        "ifnull(OPV1DTDK,'')OPV1DTDK, ifnull(OPV2DTDK,'')OPV2DTDK,ifnull(OPV3DTDK,'')OPV3DTDK,ifnull(OPV4DTDK,'')OPV4DTDK,ifnull(MeaslesDTDK,'')MeaslesDTDK,ifnull(MRDTDK,'')MRDTDK,ifnull(RotaDTDK,'')RotaDTDK,ifnull(MMRDTDK,'')MMRDTDK,ifnull(TyphoidDTDK,'')TyphoidDTDK, ifnull(InfluDTDK,'')InfluDTDK,ifnull(HepaADTDk,'')HepaADTDk,\n" +
                                        "ifnull(ChickenPoxDTDk,'')ChickenPoxDTDk,ifnull(RabiesDTDk,'')RabiesDTDk,ifnull(IPVDTDk,'')IPVDTDk,ifnull(IPVDTDk,'')IPVDTDk,ifnull(Fipvdt1dk,'')Fipvdt1dk,ifnull(Fipvdt2Dk,'')Fipvdt2Dk,ifnull(VitaminADTDk,'')VitaminADTDk\n " +
                                        " from ImmunizationTemp where PNo='" + PNumber + "'");
                            } else {
                                cur = C.ReadData("Select Vill,Bari,HH,Sno,PNO,Status,BCG,BCGDT,BCGSource,Penta1,Penta1DT,Penta1Source,Penta2,Penta2DT,Penta2Source,Penta3,Penta3DT,Penta3Source,PCV1,PCV1DT,PCV1Source,PCV2,PCV2DT,PCV2Source,PCV3,PCV3DT,PCV3Source,OPV0,OPV0DT,OPV0Source,OPV1,OPV1DT,OPV1Source,OPV2,OPV2DT,OPV2Source,OPV3,OPV3DT,OPV3Source,\n" +
                                        "OPV4,OPV4DT,OPV4Source,Measles,MeaslesDT,MeaslesSource,MR,MRDT,MRSource,Rota,RotaDT,RotaSource,MMR,MMRDT,MMRSource,Typhoid,TyphoidDT,TyphoidSource,Influ,InfluDT,InfluSource,HepaA,HepaADT,HepaASource,ChickenPox,ChickenPoxDT,ChickenPoxSource,Rabies,RabiesDT,RabiesSource,IPV,IPVDT,IPVSource,fIPV1,fIPVDT1,\n" +
                                        "fIPVSource1,fIPV2,fIPVDT2,fIPVSource2,VitaminA,VitaminADT,VitaminASource,ifnull(BCGDTDk,'')BCGDTDk,ifnull(Penta1DTDK,'')Penta1DTDK,ifnull(Penta2DTDK,'')Penta2DTDK,ifnull(Penta3DTDK,'')Penta3DTDK,ifnull(PCV1DTDK,'')PCV1DTDK,ifnull(PCV2DTDK,'')PCV2DTDK,ifnull(PCV3DTDK,'')PCV3DTDK,ifnull(OPV0DTDK,'')OPV0DTDK,\n" +
                                        "ifnull(OPV1DTDK,'')OPV1DTDK, ifnull(OPV2DTDK,'')OPV2DTDK,ifnull(OPV3DTDK,'')OPV3DTDK,ifnull(OPV4DTDK,'')OPV4DTDK,ifnull(MeaslesDTDK,'')MeaslesDTDK,ifnull(MRDTDK,'')MRDTDK,ifnull(RotaDTDK,'')RotaDTDK,ifnull(MMRDTDK,'')MMRDTDK,ifnull(TyphoidDTDK,'')TyphoidDTDK, ifnull(InfluDTDK,'')InfluDTDK,ifnull(HepaADTDk,'')HepaADTDk,\n" +
                                        "ifnull(ChickenPoxDTDk,'')ChickenPoxDTDk,ifnull(RabiesDTDk,'')RabiesDTDk,ifnull(IPVDTDk,'')IPVDTDk,ifnull(IPVDTDk,'')IPVDTDk,ifnull(Fipvdt1dk,'')Fipvdt1dk,ifnull(Fipvdt2Dk,'')Fipvdt2Dk,ifnull(VitaminADTDk,'')VitaminADTDk\n" +
                                        " from ImmunizationTemp where Vill||Bari||HH='" + Vill + Bari + HH + "' and SNo='" + SN + "'");
                            }
                            //Cursor cur=C.ReadData("Select * from ImmunizationTemp where Vill||Bari||HH='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
                            //Cursor cur=C.ReadData("Select * from ImmunizationTemp where PNo='"+ PNumber +"'");

                            cur.moveToFirst();
                            while (!cur.isAfterLast()) {
                                spnVaccineStatus.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Status"))));

                                if (cur.getString(cur.getColumnIndex("BCG")).equals("1")) {
                                    chkBCG.setChecked(true);
                                    BCGDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BCGDT"))));
                                    spnBCG.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("BCGSource"))));

                                    if (cur.getString(cur.getColumnIndex("BCGDTDk")).equals("1")) {
                                        chkBCGDTDk.setChecked(true);
                                    } else {
                                        chkBCGDTDk.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Penta1")).equals("1")) {
                                    chkPenta1.setChecked(true);
                                    Penta1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta1DT"))));
                                    spnPenta1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta1Source"))));

                                    if (cur.getString(cur.getColumnIndex("Penta1DTDK")).equals("1")) {
                                        chkPenta1DTDK.setChecked(true);
                                    } else {
                                        chkPenta1DTDK.setChecked(false);
                                    }

                                }
                                if (cur.getString(cur.getColumnIndex("Penta2")).equals("1")) {
                                    chkPenta2.setChecked(true);
                                    Penta2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta2DT"))));
                                    spnPenta2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta2Source"))));
                                    if (cur.getString(cur.getColumnIndex("Penta2DTDK")).equals("1")) {
                                        chkPenta2DTDK.setChecked(true);
                                    } else {
                                        chkPenta2DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Penta3")).equals("1")) {
                                    chkPenta3.setChecked(true);
                                    Penta3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta3DT"))));
                                    spnPenta3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta3Source"))));
                                    if (cur.getString(cur.getColumnIndex("Penta3DTDK")).equals("1")) {
                                        chkPenta3DTDK.setChecked(true);
                                    } else {
                                        chkPenta3DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("PCV1")).equals("1")) {
                                    chkPCV1.setChecked(true);
                                    PCV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV1DT"))));
                                    spnPCV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV1Source"))));
                                    if (cur.getString(cur.getColumnIndex("PCV1DTDK")).equals("1")) {
                                        chkPCV1DTDK.setChecked(true);
                                    } else {
                                        chkPCV1DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("PCV2")).equals("1")) {
                                    chkPCV2.setChecked(true);
                                    PCV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV2DT"))));
                                    spnPCV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV2Source"))));
                                    if (cur.getString(cur.getColumnIndex("PCV2DTDK")).equals("1")) {
                                        chkPCV2DTDK.setChecked(true);
                                    } else {
                                        chkPCV2DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("PCV3")).equals("1")) {
                                    chkPCV3.setChecked(true);
                                    PCV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV3DT"))));
                                    spnPCV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV3Source"))));
                                    if (cur.getString(cur.getColumnIndex("PCV3DTDK")).equals("1")) {
                                        chkPCV3DTDK.setChecked(true);
                                    } else {
                                        chkPCV3DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("OPV0")).equals("1")) {
                                    chkOPV0.setChecked(true);
                                    OPV0DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV0DT"))));
                                    spnOPV0.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV0Source"))));
                                    if (cur.getString(cur.getColumnIndex("OPV0DTDK")).equals("1")) {
                                        chkOPV0DTDK.setChecked(true);
                                    } else {
                                        chkOPV0DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("OPV1")).equals("1")) {
                                    chkOPV1.setChecked(true);
                                    OPV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV1DT"))));
                                    spnOPV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV1Source"))));
                                    if (cur.getString(cur.getColumnIndex("OPV1DTDK")).equals("1")) {
                                        chkOPV1DTDK.setChecked(true);
                                    } else {
                                        chkOPV1DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("OPV2")).equals("1")) {
                                    chkOPV2.setChecked(true);
                                    OPV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV2DT"))));
                                    spnOPV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV2Source"))));
                                    if (cur.getString(cur.getColumnIndex("OPV2DTDK")).equals("1")) {
                                        chkOPV2DTDK.setChecked(true);
                                    } else {
                                        chkOPV2DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("OPV3")).equals("1")) {
                                    chkOPV3.setChecked(true);
                                    OPV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV3DT"))));
                                    spnOPV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV3Source"))));
                                    if (cur.getString(cur.getColumnIndex("OPV3DTDK")).equals("1")) {
                                        chkOPV3DTDK.setChecked(true);
                                    } else {
                                        chkOPV3DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("OPV4")).equals("1")) {
                                    chkOPV4.setChecked(true);
                                    OPV4DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV4DT"))));
                                    spnOPV4.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV4Source"))));
                                    if (cur.getString(cur.getColumnIndex("OPV4DTDK")).equals("1")) {
                                        chkOPV4DTDK.setChecked(true);
                                    } else {
                                        chkOPV4DTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Measles")).equals("1")) {
                                    chkMeasles.setChecked(true);
                                    MeaslesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MeaslesDT"))));
                                    spnMeasles.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MeaslesSource"))));
                                    if (cur.getString(cur.getColumnIndex("MeaslesDTDK")).equals("1")) {
                                        chkMeaslesDTDK.setChecked(true);
                                    } else {
                                        chkMeaslesDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("MR")).equals("1")) {
                                    chkMR.setChecked(true);
                                    MRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MRDT"))));
                                    spnMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MRSource"))));
                                    if (cur.getString(cur.getColumnIndex("MRDTDK")).equals("1")) {
                                        chkMRDTDK.setChecked(true);
                                    } else {
                                        chkMRDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Rota")).equals("1")) {
                                    chkRota.setChecked(true);
                                    RotaDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RotaDT"))));
                                    spnRota.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RotaSource"))));
                                    if (cur.getString(cur.getColumnIndex("RotaDTDK")).equals("1")) {
                                        chkRotaDTDK.setChecked(true);
                                    } else {
                                        chkRotaDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("MMR")).equals("1")) {
                                    chkMMR.setChecked(true);
                                    MMRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MMRDT"))));
                                    spnMMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MMRSource"))));
                                    if (cur.getString(cur.getColumnIndex("MMRDTDK")).equals("1")) {
                                        chkMMRDTDK.setChecked(true);
                                    } else {
                                        chkMMRDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Typhoid")).equals("1")) {
                                    chkTyphoid.setChecked(true);
                                    TyphoidDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("TyphoidDT"))));
                                    spnTyphoid.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("TyphoidSource"))));
                                    if (cur.getString(cur.getColumnIndex("TyphoidDTDK")).equals("1")) {
                                        chkTyphoidDTDK.setChecked(true);
                                    } else {
                                        chkTyphoidDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("MMR")).equals("1")) {
                                    chkInflu.setChecked(true);
                                    InfluDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("InfluDT"))));
                                    spnInflu.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("InfluSource"))));
                                    if (cur.getString(cur.getColumnIndex("InfluDTDK")).equals("1")) {
                                        chkInfluDTDK.setChecked(true);
                                    } else {
                                        chkInfluDTDK.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("HepaA")).equals("1")) {
                                    chkHepaA.setChecked(true);
                                    HepaADT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("HepaADT"))));
                                    spnHepaA.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("HepaASource"))));
                                    if (cur.getString(cur.getColumnIndex("HepaADTDk")).equals("1")) {
                                        chkHepaADTDk.setChecked(true);
                                    } else {
                                        chkHepaADTDk.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("ChickenPox")).equals("1")) {
                                    chkChickenPox.setChecked(true);
                                    ChickenPoxDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("ChickenPoxDT"))));
                                    spnChickenPox.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("ChickenPoxSource"))));
                                    if (cur.getString(cur.getColumnIndex("ChickenPoxDTDk")).equals("1")) {
                                        chkChickenPoxDTDk.setChecked(true);
                                    } else {
                                        chkChickenPoxDTDk.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("Rabies")).equals("1")) {
                                    chkRabies.setChecked(true);
                                    RabiesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RabiesDT"))));
                                    spnRabies.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RabiesSource"))));
                                    if (cur.getString(cur.getColumnIndex("RabiesDTDk")).equals("1")) {
                                        chkRabiesDTDk.setChecked(true);
                                    } else {
                                        chkRabiesDTDk.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("IPV")).equals("1")) {
                                    chkIPV.setChecked(true);
                                    IPVDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("IPVDT"))));
                                    spnIPV.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("IPVSource"))));
                                    if (cur.getString(cur.getColumnIndex("IPVDTDk")).equals("1")) {
                                        chkIPVDTDk.setChecked(true);
                                    } else {
                                        chkIPVDTDk.setChecked(false);
                                    }
                                }

                                //23 apr 2018
                                if (cur.getString(cur.getColumnIndex("fIPV1")).equals("1")) {
                                    chkfIPV1.setChecked(true);
                                    fIPVDT1.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("fIPVDT1"))));
                                    spnfIPV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("fIPVSource1"))));
                                    if (cur.getString(cur.getColumnIndex("Fipvdt1dk")).equals("1")) {
                                        chkFipvdt1dk.setChecked(true);
                                    } else {
                                        chkFipvdt1dk.setChecked(false);
                                    }
                                }

                                if (cur.getString(cur.getColumnIndex("fIPV2")).equals("1")) {
                                    chkfIPV2.setChecked(true);
                                    fIPVDT2.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("fIPVDT2"))));
                                    spnfIPV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("fIPVSource2"))));
                                    if (cur.getString(cur.getColumnIndex("Fipvdt2Dk")).equals("1")) {
                                        chkFipvdt2Dk.setChecked(true);
                                    } else {
                                        chkFipvdt2Dk.setChecked(false);
                                    }
                                }
                                if (cur.getString(cur.getColumnIndex("VitaminA")).equals("1")) {
                                    chkVitaminA.setChecked(true);
                                    VitaminADT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("VitaminADT"))));
                                    spnVitaminA.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("VitaminASource"))));
                                    if (cur.getString(cur.getColumnIndex("VitaminADTDk")).equals("1")) {
                                        chkVitaminADTDk.setChecked(true);
                                    } else {
                                        chkVitaminADTDk.setChecked(false);
                                    }
                                }

                                cur.moveToNext();
                            }
                            cur.close();

                    }
                    catch(Exception ex)
                    {
                        //ex.printStackTrace();
                            Connection.MessageBox(Immunization.this, ex.getMessage());
                    }

        }
        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }

    });
    
        final LinearLayout secVaccine = (LinearLayout)findViewById(R.id.secVaccine);
        
        spnVaccineStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            try
            {
                    String Status = "";
                    if(spnVaccineStatus.getSelectedItemPosition()==0)
                            Status = "00";
                    else
                            Status = Global.Left(spnVaccineStatus.getSelectedItem().toString(),2);
                    
                    
                    if(Status.equals("01"))
                    {
                            secVaccine.setVisibility(View.VISIBLE);
                            for ( int i = 0; i < secVaccine.getChildCount();  i++ ){
                                View view = secVaccine.getChildAt(i);
                                view.setEnabled(true); // Or whatever you want to do with the view.
                             }
                    }
                    else
                    {
                            chkBCG.setChecked(false);
                            chkBCGDTDk.setChecked(false);
                            chkPenta1.setChecked(false);
                            chkPenta1DTDK.setChecked(false);
                            chkPenta2.setChecked(false);
                            chkPenta2DTDK.setChecked(false);
                            chkPenta3.setChecked(false);
                            chkPenta3DTDK.setChecked(false);
                            chkPCV1.setChecked(false);
                            chkPCV1DTDK.setChecked(false);
                            chkPCV2.setChecked(false);
                            chkPCV2DTDK.setChecked(false);
                            chkPCV3.setChecked(false);
                            chkPCV3DTDK.setChecked(false);
                            chkOPV0.setChecked(false);
                            chkOPV0DTDK.setChecked(false);
                            chkOPV1.setChecked(false);
                            chkOPV2.setChecked(false);
                            chkOPV3.setChecked(false);
                            chkOPV4.setChecked(false);
                            chkOPV1DTDK.setChecked(false);
                            chkOPV2DTDK.setChecked(false);
                            chkOPV3DTDK.setChecked(false);
                            chkOPV4DTDK.setChecked(false);

                            chkMeasles.setChecked(false);
                            chkMeaslesDTDK.setChecked(false);
                            chkMR.setChecked(false);
                            chkMRDTDK.setChecked(false);
                            chkRota.setChecked(false);
                            chkRotaDTDK.setChecked(false);
                            chkMMR.setChecked(false);
                            chkMMRDTDK.setChecked(false);
                            chkTyphoid.setChecked(false);
                            chkTyphoidDTDK.setChecked(false);
                            chkInflu.setChecked(false);
                            chkInfluDTDK.setChecked(false);
                            chkHepaA.setChecked(false);
                            chkHepaADTDk.setChecked(false);
                            chkChickenPox.setChecked(false);
                            chkChickenPoxDTDk.setChecked(false);
                            chkRabies.setChecked(false);
                            chkRabiesDTDk.setChecked(false);
                            chkIPV.setChecked(false);
                            chkIPVDTDk.setChecked(false);
                            chkfIPV1.setChecked(false);
                            chkFipvdt1dk.setChecked(false);
                            chkfIPV2.setChecked(false);
                            chkFipvdt2Dk.setChecked(false);
                            chkVitaminA.setChecked(false);
                            chkVitaminADTDk.setChecked(false);

                            secVaccine.setVisibility(View.GONE);
                            for ( int i = 0; i < secVaccine.getChildCount();  i++ ){
                                View view = secVaccine.getChildAt(i);
                                view.setEnabled(false); // Or whatever you want to do with the view.
                             }
                    }
            }
            catch(Exception ex)
            {
                    Connection.MessageBox(Immunization.this, ex.getMessage());
                    return;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
        }

    });
        
        
        
        cmdImmSave.setOnClickListener(new View.OnClickListener() {
        public void onClick(View arg0) {
            String SQL = "";
            String EDT = "";
            try
            {
                if(spnMemList.getCount()==0)
                {
                        Connection.MessageBox(Immunization.this, "Select a child from the member list.");
                        return;
                }
                else if(spnVaccineStatus.getSelectedItemPosition()==0)
                {
                        Connection.MessageBox(Immunization.this, "Select a valid status from list.");
                        return;
                }
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    if(SN.length()==0) return;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String DOB = C.ReturnSingleValue("Select BDate from tTrans where status='m' and vill||bari||hh='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
                    Date BD = sdf.parse(DOB);
                    Date VacD;
                    
            /*if(!C.Existence("Select Vill from ImmunizationTemp Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'"))
                    C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ Global.DateTimeNow() +"','2')");
                            */
                    //18 Sep 2014
            //if(!C.Existence("Select Vill from ImmunizationTemp Where PNo='"+ PNumber +"'"))
            //      C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,PNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ PNumber +"','"+ Global.DateTimeNow() +"','2')");

            //for new member those have no pno
            String PNoAvailable="1";
            if(C.Existence("Select Vill from tTrans Where status='m' and Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"' and (length(pno)=0 or pno is null)"))
            {
                    PNoAvailable="2";
                    if(!C.Existence("Select Vill from ImmunizationTemp Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'"))
                            C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ Global.DateTimeNow() +"','2')");                       
            }
            else
            {
                    if(!C.Existence("Select Vill from ImmunizationTemp Where PNo='"+ PNumber +"'"))
                            C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,PNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ PNumber +"','"+ Global.DateTimeNow() +"','2')");
            }
            
            String Status = "";
            if(spnVaccineStatus.getSelectedItemPosition()==0)
                    Status = "";
            else
                    Status = Global.Left(spnVaccineStatus.getSelectedItem().toString(),2);

            SQL = "Update ImmunizationTemp Set Status='"+ Status +"',";
            
                    if(chkBCG.isChecked()==true) {
                        EDT = Global.DateValidate(BCGDT.getText().toString());
                        if (EDT.length() != 0 & !chkBCGDTDk.isChecked()) {
                            Connection.MessageBox(Immunization.this, EDT);
                            BCGDT.requestFocus();
                            return;
                        } else if (spnBCG.getSelectedItemPosition() == 0) {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnBCG.requestFocus();
                            return;
                        }
                        if (!BCGDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(BCGDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination data should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                BCGDT.requestFocus();
                                return;
                            }

                            SQL += "BCG='1',";
                            SQL += "BCGDT='" + Global.DateConvertYMD(BCGDT.getText().toString()) + "',";
                            SQL += "BCGSource='" + Global.Left(spnBCG.getSelectedItem().toString(), 2) + "',";
                            SQL += "BCGDTDk='',";
                        }else
                        {
                            SQL += "BCG='1',";
                            SQL += "BCGDT='',";
                            SQL += "BCGSource='" + Global.Left(spnBCG.getSelectedItem().toString(), 2) + "',";
                            SQL += "BCGDTDk='1',";
                        }
                    }
                    else
                    {
                            SQL +="BCG='',";
                            SQL +="BCGDT='',";
                            SQL +="BCGSource='',";
                            SQL +="BCGDTDk='',";
                    }

                    if(chkPenta1.isChecked()==true)
                    {
                        EDT = Global.DateValidate(Penta1DT.getText().toString());
                        if(EDT.length()!=0 & !chkPenta1DTDK.isChecked())
                                {
                                        Connection.MessageBox(Immunization.this, EDT);
                                        Penta1DT.requestFocus();
                                        return;
                                }
                        else if(spnPenta1.getSelectedItemPosition()==0)
                        {
                                Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                                spnPenta1.requestFocus();
                                return;
                        }
                        if (!Penta1DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(Penta1DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                Penta1DT.requestFocus();
                                return;
                            }
                            SQL += "Penta1='1',";
                            SQL += "Penta1DT='" + Global.DateConvertYMD(Penta1DT.getText().toString()) + "',";
                            SQL += "Penta1Source='" + Global.Left(spnPenta1.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta1DTDK='',";
                        }else
                        {
                            SQL += "Penta1='1',";
                            SQL += "Penta1DT='',";
                            SQL += "Penta1Source='" + Global.Left(spnPenta1.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta1DTDK='1',";
                        }
                    }
                    else
                    {
                            SQL +="Penta1='',";
                            SQL +="Penta1DT='',";
                            SQL +="Penta1Source='',";
                            SQL +="Penta1DTDK='',";
                    }

                    if(chkPenta2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(Penta2DT.getText().toString());
                    if(EDT.length()!=0 & !chkPenta2DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    Penta2DT.requestFocus();
                                    return;
                            }
                    else if(spnPenta2.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnPenta2.requestFocus();
                            return;
                    }
                        if (!Penta2DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(Penta2DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                Penta2DT.requestFocus();
                                return;
                            }
                            SQL += "Penta2='1',";
                            SQL += "Penta2DT='" + Global.DateConvertYMD(Penta2DT.getText().toString()) + "',";
                            SQL += "Penta2Source='" + Global.Left(spnPenta2.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta2DTDK='',";
                        }else
                        {
                            SQL += "Penta2='1',";
                            SQL += "Penta2DT='',";
                            SQL += "Penta2Source='" + Global.Left(spnPenta2.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta2DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="Penta2='',";
                        SQL +="Penta2DT='',";
                        SQL +="Penta2Source='',";
                        SQL +="Penta2DTDK='',";
                    }

                    if(chkPenta3.isChecked()==true)
                    {
                            EDT = Global.DateValidate(Penta3DT.getText().toString());
                    if(EDT.length()!=0 & !chkPenta3DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    Penta3DT.requestFocus();
                                    return;
                            }
                    else if(spnPenta3.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnPenta3.requestFocus();
                            return;
                    }
                        if (!Penta3DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(Penta3DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                Penta3DT.requestFocus();
                                return;
                            }
                            SQL += "Penta3='1',";
                            SQL += "Penta3DT='" + Global.DateConvertYMD(Penta3DT.getText().toString()) + "',";
                            SQL += "Penta3Source='" + Global.Left(spnPenta3.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta3DTDK='',";
                        }else
                        {
                            SQL += "Penta3='1',";
                            SQL += "Penta3DT='',";
                            SQL += "Penta3Source='" + Global.Left(spnPenta3.getSelectedItem().toString(), 2) + "',";
                            SQL += "Penta3DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="Penta3='',";
                        SQL +="Penta3DT='',";
                        SQL +="Penta3Source='',";
                        SQL +="Penta3DTDK='',";
                    }
                    
                    if(chkPCV1.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV1DT.getText().toString());
                    if(EDT.length()!=0 & !chkPCV1DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    PCV1DT.requestFocus();
                                    return;
                            }
                    else if(spnPCV1.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnPCV1.requestFocus();
                            return;
                    }
                        if (!PCV1DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(PCV1DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                PCV1DT.requestFocus();
                                return;
                            }
                            SQL += "PCV1='1',";
                            SQL += "PCV1DT='" + Global.DateConvertYMD(PCV1DT.getText().toString()) + "',";
                            SQL += "PCV1Source='" + Global.Left(spnPCV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV1DTDK='',";
                        }else
                        {
                            SQL += "PCV1='1',";
                            SQL += "PCV1DT='',";
                            SQL += "PCV1Source='" + Global.Left(spnPCV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV1DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="PCV1='',";
                        SQL +="PCV1DT='',";
                        SQL +="PCV1Source='',";
                        SQL +="PCV1DTDK='',";
                    }
                    if(chkPCV2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV2DT.getText().toString());
                    if(EDT.length()!=0 & !chkPCV2DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    PCV2DT.requestFocus();
                                    return;
                            }
                    else if(spnPCV2.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnPCV2.requestFocus();
                            return;
                    }
                        if (!PCV2DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(PCV2DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                PCV2DT.requestFocus();
                                return;
                            }
                            SQL += "PCV2='1',";
                            SQL += "PCV2DT='" + Global.DateConvertYMD(PCV2DT.getText().toString()) + "',";
                            SQL += "PCV2Source='" + Global.Left(spnPCV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV2DTDK='',";
                        }else
                        {
                            SQL += "PCV2='1',";
                            SQL += "PCV2DT='',";
                            SQL += "PCV2Source='" + Global.Left(spnPCV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV2DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="PCV2='',";
                        SQL +="PCV2DT='',";
                        SQL +="PCV2Source='',";
                        SQL +="PCV2DTDK='',";
                    }

                    if(chkPCV3.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV3DT.getText().toString());
                    if(EDT.length()!=0 & !chkPCV3DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    PCV3DT.requestFocus();
                                    return;
                            }
                    else if(spnPCV3.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnPCV3.requestFocus();
                            return;
                    }
                        if (!PCV3DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(PCV3DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                PCV3DT.requestFocus();
                                return;
                            }
                            SQL += "PCV3='1',";
                            SQL += "PCV3DT='" + Global.DateConvertYMD(PCV3DT.getText().toString()) + "',";
                            SQL += "PCV3Source='" + Global.Left(spnPCV3.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV3DTDK='',";
                        }else
                        {
                            SQL += "PCV3='1',";
                            SQL += "PCV3DT='',";
                            SQL += "PCV3Source='" + Global.Left(spnPCV3.getSelectedItem().toString(), 2) + "',";
                            SQL += "PCV3DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="PCV3='',";
                        SQL +="PCV3DT='',";
                        SQL +="PCV3Source='',";
                        SQL +="PCV3DTDK='',";
                    }
                    if(chkOPV0.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV0DT.getText().toString());
                    if(EDT.length()!=0 & !chkOPV0DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    OPV0DT.requestFocus();
                                    return;
                            }
                    else if(spnOPV0.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnOPV0.requestFocus();
                            return;
                    }
                        if (!OPV0DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(OPV0DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                OPV0DT.requestFocus();
                                return;
                            }
                            SQL += "OPV0='1',";
                            SQL += "OPV0DT='" + Global.DateConvertYMD(OPV0DT.getText().toString()) + "',";
                            SQL += "OPV0Source='" + Global.Left(spnOPV0.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV0DTDK='',";
                        }else
                        {
                            SQL += "OPV0='1',";
                            SQL += "OPV0DT='',";
                            SQL += "OPV0Source='" + Global.Left(spnOPV0.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV0DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="OPV0='',";
                        SQL +="OPV0DT='',";
                        SQL +="OPV0Source='',";
                        SQL +="OPV0DTDK='',";
                    }
                    
                    if(chkOPV1.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV1DT.getText().toString());
                    if(EDT.length()!=0 & !chkOPV1DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    OPV1DT.requestFocus();
                                    return;
                            }
                    else if(spnOPV1.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnOPV1.requestFocus();
                            return;
                    }

                        if (!OPV1DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(OPV1DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                OPV1DT.requestFocus();
                                return;
                            }
                            SQL += "OPV1='1',";
                            SQL += "OPV1DT='" + Global.DateConvertYMD(OPV1DT.getText().toString()) + "',";
                            SQL += "OPV1Source='" + Global.Left(spnOPV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV1DTDK='',";
                        }else
                        {
                            SQL += "OPV1='1',";
                            SQL += "OPV1DT='',";
                            SQL += "OPV1Source='" + Global.Left(spnOPV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV1DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="OPV1='',";
                        SQL +="OPV1DT='',";
                        SQL +="OPV1Source='',";
                        SQL +="OPV1DTDK='',";
                    }
                    if(chkOPV2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV2DT.getText().toString());
                    if(EDT.length()!=0 & !chkOPV2DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    OPV2DT.requestFocus();
                                    return;
                            }
                    else if(spnOPV2.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnOPV2.requestFocus();
                            return;
                    }
                        if (!OPV2DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(OPV2DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                OPV2DT.requestFocus();
                                return;
                            }
                            SQL += "OPV2='1',";
                            SQL += "OPV2DT='" + Global.DateConvertYMD(OPV2DT.getText().toString()) + "',";
                            SQL += "OPV2Source='" + Global.Left(spnOPV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV2DTDK='',";
                        }else
                        {
                            SQL += "OPV2='1',";
                            SQL += "OPV2DT='',";
                            SQL += "OPV2Source='" + Global.Left(spnOPV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV2DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="OPV2='',";
                        SQL +="OPV2DT='',";
                        SQL +="OPV2Source='',";
                        SQL +="OPV2DTDK='',";
                    }
                    
                    
                    if(chkOPV3.isChecked()==true)
                    {       
                            EDT = Global.DateValidate(OPV3DT.getText().toString());
                    if(EDT.length()!=0 & !chkOPV3DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    OPV3DT.requestFocus();
                                    return;
                            }
                    else if(spnOPV3.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnOPV3.requestFocus();
                            return;
                    }
                    if (!OPV3DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(OPV3DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                OPV3DT.requestFocus();
                                return;
                            }
                            SQL += "OPV3='1',";
                            SQL += "OPV3DT='" + Global.DateConvertYMD(OPV3DT.getText().toString()) + "',";
                            SQL += "OPV3Source='" + Global.Left(spnOPV3.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV3DTDK='',";
                        }else
                        {
                            SQL += "OPV3='1',";
                            SQL += "OPV3DT='',";
                            SQL += "OPV3Source='" + Global.Left(spnOPV3.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV3DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="OPV3='',";
                        SQL +="OPV3DT='',";
                        SQL +="OPV3Source='',";
                        SQL +="OPV3DTDK='',";
                    }
                    if(chkOPV4.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV4DT.getText().toString());
                    if(EDT.length()!=0 & !chkOPV4DTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    OPV4DT.requestFocus();
                                    return;
                            }
                    else if(spnOPV4.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnOPV4.requestFocus();
                            return;
                    }

                        if (!OPV4DT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(OPV4DT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                OPV4DT.requestFocus();
                                return;
                            }
                            SQL += "OPV4='1',";
                            SQL += "OPV4DT='" + Global.DateConvertYMD(OPV4DT.getText().toString()) + "',";
                            SQL += "OPV4Source='" + Global.Left(spnOPV4.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV4DTDK='',";
                        }else
                        {
                            SQL += "OPV4='1',";
                            SQL += "OPV4DT='',";
                            SQL += "OPV4Source='" + Global.Left(spnOPV4.getSelectedItem().toString(), 2) + "',";
                            SQL += "OPV4DTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="OPV4='',";
                        SQL +="OPV4DT='',";
                        SQL +="OPV4Source='',";
                        SQL +="OPV4DTDK='',";
                    }
                    
                    if(chkMeasles.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MeaslesDT.getText().toString());
                    if(EDT.length()!=0 & !chkMeaslesDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    MeaslesDT.requestFocus();
                                    return;
                            }
                    else if(spnMeasles.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnMeasles.requestFocus();
                            return;
                    }
                        if (!MeaslesDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(MeaslesDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                MeaslesDT.requestFocus();
                                return;
                            }
                            SQL += "Measles='1',";
                            SQL += "MeaslesDT='" + Global.DateConvertYMD(MeaslesDT.getText().toString()) + "',";
                            SQL += "MeaslesSource='" + Global.Left(spnMeasles.getSelectedItem().toString(), 2) + "',";
                            SQL += "MeaslesDTDK='',";
                        }else
                        {
                            SQL += "Measles='1',";
                            SQL += "MeaslesDT='',";
                            SQL += "MeaslesSource='" + Global.Left(spnMeasles.getSelectedItem().toString(), 2) + "',";
                            SQL += "MeaslesDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="Measles='',";
                        SQL +="MeaslesDT='',";
                        SQL +="MeaslesSource='',";
                        SQL +="MeaslesDTDK='',";
                    }
                    
                    if(chkMR.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MRDT.getText().toString());
                    if(EDT.length()!=0 & !chkMRDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    MRDT.requestFocus();
                                    return;
                            }
                    else if(spnMR.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnMR.requestFocus();
                            return;
                    }
                        if (!MRDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(MRDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                MRDT.requestFocus();
                                return;
                            }
                            SQL += "MR='1',";
                            SQL += "MRDT='" + Global.DateConvertYMD(MRDT.getText().toString()) + "',";
                            SQL += "MRSource='" + Global.Left(spnMR.getSelectedItem().toString(), 2) + "',";
                            SQL += "MRDTDK='',";
                        }else
                        {
                            SQL += "MR='1',";
                            SQL += "MRDT='',";
                            SQL += "MRSource='" + Global.Left(spnMR.getSelectedItem().toString(), 2) + "',";
                            SQL += "MRDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="MR='',";
                        SQL +="MRDT='',";
                        SQL +="MRSource='',";
                        SQL +="MRDTDK='',";
                    }
                    
                    if(chkRota.isChecked()==true)
                    {
                            EDT = Global.DateValidate(RotaDT.getText().toString());
                    if(EDT.length()!=0 & !chkRotaDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    RotaDT.requestFocus();
                                    return;
                            }
                    else if(spnRota.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnRota.requestFocus();
                            return;
                    }
                        if (!RotaDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(RotaDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                RotaDT.requestFocus();
                                return;
                            }
                            SQL += "Rota='1',";
                            SQL += "RotaDT='" + Global.DateConvertYMD(RotaDT.getText().toString()) + "',";
                            SQL += "RotaSource='" + Global.Left(spnRota.getSelectedItem().toString(), 2) + "',";
                            SQL += "RotaDTDK='',";
                        }else
                        {
                            SQL += "Rota='1',";
                            SQL += "RotaDT='',";
                            SQL += "RotaSource='" + Global.Left(spnRota.getSelectedItem().toString(), 2) + "',";
                            SQL += "RotaDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="MR='',";
                        SQL +="MRDT='',";
                        SQL +="MRSource='',";
                        SQL +="MRDTDK='',";
                    }
                    
                    if(chkMMR.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MMRDT.getText().toString());
                    if(EDT.length()!=0 & !chkMMRDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    MMRDT.requestFocus();
                                    return;
                            }    
                    else if(spnMMR.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnMMR.requestFocus();
                            return;
                    }
                        if (!MMRDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(MMRDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                MMRDT.requestFocus();
                                return;
                            }
                            SQL += "MMR='1',";
                            SQL += "MMRDT='" + Global.DateConvertYMD(MMRDT.getText().toString()) + "',";
                            SQL += "MMRSource='" + Global.Left(spnMMR.getSelectedItem().toString(), 2) + "',";
                            SQL += "MMRDTDK='',";
                        }else
                        {
                            SQL += "MMR='1',";
                            SQL += "MMRDT='',";
                            SQL += "MMRSource='" + Global.Left(spnMMR.getSelectedItem().toString(), 2) + "',";
                            SQL += "MMRDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="MMR='',";
                        SQL +="MMRDT='',";
                        SQL +="MMRSource='',";
                        SQL +="MMRDTDK='',";
                    }
                    
                    if(chkTyphoid.isChecked()==true)
                    {
                            EDT = Global.DateValidate(TyphoidDT.getText().toString());
                    if(EDT.length()!=0 & !chkTyphoidDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    TyphoidDT.requestFocus();
                                    return;
                            }
                    else if(spnTyphoid.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnTyphoid.requestFocus();
                            return;
                    }
                        if (!TyphoidDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(TyphoidDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                TyphoidDT.requestFocus();
                                return;
                            }
                            SQL += "Typhoid='1',";
                            SQL += "TyphoidDT='" + Global.DateConvertYMD(TyphoidDT.getText().toString()) + "',";
                            SQL += "TyphoidSource='" + Global.Left(spnTyphoid.getSelectedItem().toString(), 2) + "',";
                            SQL += "TyphoidDTDK='',";
                        }else
                        {
                            SQL += "Typhoid='1',";
                            SQL += "TyphoidDT='',";
                            SQL += "TyphoidSource='" + Global.Left(spnTyphoid.getSelectedItem().toString(), 2) + "',";
                            SQL += "TyphoidDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="Typhoid='',";
                        SQL +="TyphoidDT='',";
                        SQL +="TyphoidSource='',";
                        SQL +="TyphoidDTDK='',";
                    }

                    if(chkInflu.isChecked()==true)
                    {
                            EDT = Global.DateValidate(InfluDT.getText().toString());
                    if(EDT.length()!=0 & !chkInfluDTDK.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    InfluDT.requestFocus();
                                    return;
                            }
                    else if(spnInflu.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnInflu.requestFocus();
                            return;
                    }
                        if (!InfluDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(InfluDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                InfluDT.requestFocus();
                                return;
                            }
                            SQL += "Influ='1',";
                            SQL += "InfluDT='" + Global.DateConvertYMD(InfluDT.getText().toString()) + "',";
                            SQL += "InfluSource='" + Global.Left(spnInflu.getSelectedItem().toString(), 2) + "',";
                            SQL += "InfluDTDK='',";
                        }else
                        {
                            SQL += "Influ='1',";
                            SQL += "InfluDT='',";
                            SQL += "InfluSource='" + Global.Left(spnInflu.getSelectedItem().toString(), 2) + "',";
                            SQL += "InfluDTDK='1',";
                        }
                    }
                    else
                    {
                        SQL +="Influ='',";
                        SQL +="InfluDT='',";
                        SQL +="InfluSource='',";
                        SQL +="InfluDTDK='',";
                    }

                    if(chkHepaA.isChecked()==true)
                    {
                            EDT = Global.DateValidate(HepaADT.getText().toString());
                    if(EDT.length()!=0 & !chkHepaADTDk.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    HepaADT.requestFocus();
                                    return;
                            }
                    else if(spnHepaA.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnHepaA.requestFocus();
                            return;
                    }

                        if (!HepaADT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(HepaADT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                HepaADT.requestFocus();
                                return;
                            }
                            SQL += "HepaA='1',";
                            SQL += "HepaADT='" + Global.DateConvertYMD(HepaADT.getText().toString()) + "',";
                            SQL += "HepaASource='" + Global.Left(spnHepaA.getSelectedItem().toString(), 2) + "',";
                            SQL += "HepaADTDk='',";
                        }else
                        {
                            SQL += "HepaA='1',";
                            SQL += "HepaADT='',";
                            SQL += "HepaASource='" + Global.Left(spnHepaA.getSelectedItem().toString(), 2) + "',";
                            SQL += "HepaADTDk='1',";
                        }
                    }
                    else
                    {
                        SQL +="HepaA='',";
                        SQL +="HepaADT='',";
                        SQL +="HepaASource='',";
                        SQL +="HepaADTDk='',";
                    }
                    
                    if(chkChickenPox.isChecked()==true)
                    {
                            EDT = Global.DateValidate(ChickenPoxDT.getText().toString());
                    if(EDT.length()!=0 & !chkChickenPoxDTDk.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT);
                                    ChickenPoxDT.requestFocus();
                                    return;
                            }
                    else if(spnChickenPox.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnChickenPox.requestFocus();
                            return;
                    }
                        if (!ChickenPoxDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(ChickenPoxDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                ChickenPoxDT.requestFocus();
                                return;
                            }
                            SQL += "ChickenPox='1',";
                            SQL += "ChickenPoxDT='" + Global.DateConvertYMD(ChickenPoxDT.getText().toString()) + "',";
                            SQL += "ChickenPoxSource='" + Global.Left(spnChickenPox.getSelectedItem().toString(), 2) + "',";
                            SQL += "ChickenPoxDTDk='',";
                        }else
                        {
                            SQL += "ChickenPox='1',";
                            SQL += "ChickenPoxDT='',";
                            SQL += "ChickenPoxSource='" + Global.Left(spnChickenPox.getSelectedItem().toString(), 2) + "',";
                            SQL += "ChickenPoxDTDk='1',";
                        }
                    }
                    else
                    {
                        SQL +="ChickenPox='',";
                        SQL +="ChickenPoxDT='',";
                        SQL +="ChickenPoxSource='',";
                        SQL +="ChickenPoxDTDk='',";
                    }
                    
                    if(chkRabies.isChecked()==true)
                    {
                            EDT = Global.DateValidate(RabiesDT.getText().toString());
                    if(EDT.length()!=0 & !chkRabiesDTDk.isChecked())
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    RabiesDT.requestFocus();
                                    return;
                            }
                    else if(spnRabies.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnRabies.requestFocus();
                            return;
                    }
                        if (!RabiesDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(RabiesDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                RabiesDT.requestFocus();
                                return;
                            }
                            SQL += "Rabies='1',";
                            SQL += "RabiesDT='" + Global.DateConvertYMD(RabiesDT.getText().toString()) + "',";
                            SQL += "RabiesSource='" + Global.Left(spnRabies.getSelectedItem().toString(), 2) + "',";
                            SQL += "RabiesDTDk='',";
                        }else
                        {
                            SQL += "Rabies='1',";
                            SQL += "RabiesDT='',";
                            SQL += "RabiesSource='" + Global.Left(spnRabies.getSelectedItem().toString(), 2) + "',";
                            SQL += "RabiesDTDk='1',";
                        }
                    }
                    else
                    {
                        SQL +="Rabies='',";
                        SQL +="RabiesDT='',";
                        SQL +="RabiesSource='',";
                        SQL +="RabiesDTDk='',";
                    }

                    if(chkIPV.isChecked()==true)
                    {
                            EDT = Global.DateValidate(IPVDT.getText().toString());
                            if(EDT.length()!=0 & !chkIPVDTDk.isChecked())
                                    {
                                            Connection.MessageBox(Immunization.this, EDT);
                                            IPVDT.requestFocus();
                                            return;
                                    }
                            else if(spnIPV.getSelectedItemPosition()==0)
                            {
                                    Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                                    spnIPV.requestFocus();
                                    return;
                            }

                        if (!IPVDT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(IPVDT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                IPVDT.requestFocus();
                                return;
                            }
                            SQL += "IPV='1',";
                            SQL += "IPVDT='" + Global.DateConvertYMD(IPVDT.getText().toString()) + "',";
                            SQL += "IPVSource='" + Global.Left(spnIPV.getSelectedItem().toString(), 2) + "',";
                            SQL += "IPVDTDk='',";
                        }else
                        {
                            SQL += "IPV='1',";
                            SQL += "IPVDT='',";
                            SQL += "IPVSource='" + Global.Left(spnIPV.getSelectedItem().toString(), 2) + "',";
                            SQL += "IPVDTDk='1',";
                        }
                    }
                    else
                    {
                        SQL +="IPV='',";
                        SQL +="IPVDT='',";
                        SQL +="IPVSource='',";
                        SQL +="IPVDTDk='',";
                    }

                    //23 apr 2018
                    if(chkfIPV1.isChecked()==true)
                    {
                        EDT = Global.DateValidate(fIPVDT1.getText().toString());
                        if(EDT.length()!=0 & !chkFipvdt1dk.isChecked())
                        {
                            Connection.MessageBox(Immunization.this, EDT);
                            fIPVDT1.requestFocus();
                            return;
                        }
                        else if(spnfIPV1.getSelectedItemPosition()==0)
                        {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnfIPV1.requestFocus();
                            return;
                        }
                        if (!fIPVDT1.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(fIPVDT1.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                fIPVDT1.requestFocus();
                                return;
                            }
                            SQL += "fIPV1='1',";
                            SQL += "fIPVDT1='" + Global.DateConvertYMD(fIPVDT1.getText().toString()) + "',";
                            SQL += "fIPVSource1='" + Global.Left(spnfIPV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "Fipvdt1dk='',";
                        }else
                        {
                            SQL += "fIPV1='1',";
                            SQL += "fIPVDT1='',";
                            SQL += "fIPVSource1='" + Global.Left(spnfIPV1.getSelectedItem().toString(), 2) + "',";
                            SQL += "Fipvdt1dk='1',";
                        }
                    }
                    else
                    {
                        SQL +="fIPV1='',";
                        SQL +="fIPVDT1='',";
                        SQL +="fIPVSource1='',";
                        SQL +="Fipvdt1dk='',";
                    }

                    if(chkfIPV2.isChecked()==true)
                    {
                        EDT = Global.DateValidate(fIPVDT2.getText().toString());
                        if(EDT.length()!=0 & !chkFipvdt2Dk.isChecked())
                        {
                            Connection.MessageBox(Immunization.this, EDT);
                            fIPVDT2.requestFocus();
                            return;
                        }
                        else if(spnfIPV2.getSelectedItemPosition()==0)
                        {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnfIPV2.requestFocus();
                            return;
                        }
                        if (!fIPVDT2.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(fIPVDT2.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                fIPVDT2.requestFocus();
                                return;
                            }
                            SQL += "fIPV2='1',";
                            SQL += "fIPVDT2='" + Global.DateConvertYMD(fIPVDT2.getText().toString()) + "',";
                            SQL += "fIPVSource2='" + Global.Left(spnfIPV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "Fipvdt2dk='',";
                        }else
                        {
                            SQL += "fIPV2='1',";
                            SQL += "fIPVDT2='',";
                            SQL += "fIPVSource2='" + Global.Left(spnfIPV2.getSelectedItem().toString(), 2) + "',";
                            SQL += "Fipvdt2dk='1',";
                        }
                    }
                    else
                    {
                        SQL +="fIPV2='',";
                        SQL +="fIPVDT2='',";
                        SQL +="fIPVSource2='',";
                        SQL +="Fipvdt2dk='',";
                    }

                    if(chkVitaminA.isChecked()==true)
                    {
                            EDT = Global.DateValidate(VitaminADT.getText().toString());
                            if(EDT.length()!=0 & !chkVitaminADTDk.isChecked())
                                    {
                                            Connection.MessageBox(Immunization.this, EDT);
                                            VitaminADT.requestFocus();
                                            return;
                                    }
                            else if(spnVitaminA.getSelectedItemPosition()==0)
                            {
                                    Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                                    spnVitaminA.requestFocus();
                                    return;
                            }

                        if (!VitaminADT.getText().toString().isEmpty()) {
                            VacD = sdf.parse(Global.DateConvertYMD(VitaminADT.getText().toString()));
                            if (BD.after(VacD)) {
                                Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth[" + Global.DateConvertDMY(DOB) + "].");
                                VitaminADT.requestFocus();
                                return;
                            }
                            SQL += "VitaminA='1',";
                            SQL += "VitaminADT='" + Global.DateConvertYMD(VitaminADT.getText().toString()) + "',";
                            SQL += "VitaminASource='" + Global.Left(spnVitaminA.getSelectedItem().toString(), 2) + "',";
                            SQL += "VitaminADTDk='',";
                        }else
                        {
                            SQL += "VitaminA='1',";
                            SQL += "VitaminADT='',";
                            SQL += "VitaminASource='" + Global.Left(spnVitaminA.getSelectedItem().toString(), 2) + "',";
                            SQL += "VitaminADTDk='1',";
                        }
                    }
                    else
                    {
                        SQL +="VitaminA='',";
                        SQL +="VitaminADT='',";
                        SQL +="VitaminASource='',";
                        SQL +="VitaminADTDk='',";
                    }

                    
            if(PNoAvailable.equals("1"))
            {
                    SQL +="Upload='2' Where PNo='"+ PNumber +"'";   
            }
            else
            {
                    SQL +="Upload='2' Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'";
            }
            //SQL +="Upload='2' Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'";
            
            
            C.Save(SQL);
            
            Connection.MessageBox(Immunization.this, "Saved successfully.");
        }
        catch(Exception ex)
        {
            Connection.MessageBox(Immunization.this, ex.getMessage());
            return;
        }
    }});
    

        cmdImmClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    finish();
                }
            });
    }
    catch(Exception  e)
    {
        Connection.MessageBox(Immunization.this, e.getMessage());
        return;
    }       
}


    private void DataSearch()
    {
        try
        {
            final CheckBox chkBCG = (CheckBox)findViewById(R.id.chkBCG);
            final CheckBox chkBCGDTDk = (CheckBox)findViewById(R.id.chkBCGDTDk);
            final EditText BCGDT = (EditText)findViewById(R.id.BCGDT);
            final Spinner  spnBCG= (Spinner)findViewById(R.id.spnBCG);
            spnBCG.setAdapter(dataSource);

            final CheckBox chkPenta1 = (CheckBox)findViewById(R.id.chkPenta1);
            final CheckBox chkPenta1DTDK = (CheckBox)findViewById(R.id.chkPenta1DTDK);
            final EditText Penta1DT = (EditText)findViewById(R.id.Penta1DT);
            final Spinner  spnPenta1 = (Spinner)findViewById(R.id.spnPenta1);
            spnPenta1.setAdapter(dataSource);

            final CheckBox chkPenta2 = (CheckBox)findViewById(R.id.chkPenta2);
            final CheckBox chkPenta2DTDK = (CheckBox)findViewById(R.id.chkPenta2DTDK);
            final EditText Penta2DT = (EditText)findViewById(R.id.Penta2DT);
            final Spinner  spnPenta2 = (Spinner)findViewById(R.id.spnPenta2);
            spnPenta2.setAdapter(dataSource);

            final CheckBox chkPenta3 = (CheckBox)findViewById(R.id.chkPenta3);
            final CheckBox chkPenta3DTDK = (CheckBox)findViewById(R.id.chkPenta3DTDK);
            final EditText Penta3DT = (EditText)findViewById(R.id.Penta3DT);
            final Spinner  spnPenta3 = (Spinner)findViewById(R.id.spnPenta3);
            spnPenta3.setAdapter(dataSource);

            final CheckBox chkPCV1 = (CheckBox)findViewById(R.id.chkPCV1);
            final CheckBox chkPCV1DTDK = (CheckBox)findViewById(R.id.chkPCV1DTDK);
            final EditText PCV1DT = (EditText)findViewById(R.id.PCV1DT);
            final Spinner  spnPCV1 = (Spinner)findViewById(R.id.spnPCV1);
            spnPCV1.setAdapter(dataSource);

            final CheckBox chkPCV2 = (CheckBox)findViewById(R.id.chkPCV2);
            final CheckBox chkPCV2DTDK = (CheckBox)findViewById(R.id.chkPCV2DTDK);
            final EditText PCV2DT = (EditText)findViewById(R.id.PCV2DT);
            final Spinner  spnPCV2 = (Spinner)findViewById(R.id.spnPCV2);
            spnPCV2.setAdapter(dataSource);

            final CheckBox chkPCV3 = (CheckBox)findViewById(R.id.chkPCV3);
            final CheckBox chkPCV3DTDK = (CheckBox)findViewById(R.id.chkPCV3DTDK);
            final EditText PCV3DT = (EditText)findViewById(R.id.PCV3DT);
            final Spinner  spnPCV3 = (Spinner)findViewById(R.id.spnPCV3);
            spnPCV3.setAdapter(dataSource);

            final CheckBox chkOPV0 = (CheckBox)findViewById(R.id.chkOPV0);
            final CheckBox chkOPV0DTDK = (CheckBox)findViewById(R.id.chkOPV0DTDK);
            final EditText OPV0DT = (EditText)findViewById(R.id.OPV0DT);
            final Spinner  spnOPV0 = (Spinner)findViewById(R.id.spnOPV0);
            spnOPV0.setAdapter(dataSource);

            final CheckBox chkOPV1 = (CheckBox)findViewById(R.id.chkOPV1);
            final CheckBox chkOPV1DTDK = (CheckBox)findViewById(R.id.chkOPV1DTDK);
            final EditText OPV1DT = (EditText)findViewById(R.id.OPV1DT);
            final Spinner  spnOPV1 = (Spinner)findViewById(R.id.spnOPV1);
            spnOPV1.setAdapter(dataSource);

            final CheckBox chkOPV2 = (CheckBox)findViewById(R.id.chkOPV2);
            final CheckBox chkOPV2DTDK = (CheckBox)findViewById(R.id.chkOPV2DTDK);
            final EditText OPV2DT = (EditText)findViewById(R.id.OPV2DT);
            final Spinner  spnOPV2 = (Spinner)findViewById(R.id.spnOPV2);
            spnOPV2.setAdapter(dataSource);

            final CheckBox chkOPV3 = (CheckBox)findViewById(R.id.chkOPV3);
            final CheckBox chkOPV3DTDK = (CheckBox)findViewById(R.id.chkOPV3DTDK);
            final EditText OPV3DT = (EditText)findViewById(R.id.OPV3DT);
            final Spinner  spnOPV3 = (Spinner)findViewById(R.id.spnOPV3);
            spnOPV3.setAdapter(dataSource);

            final CheckBox chkOPV4 = (CheckBox)findViewById(R.id.chkOPV4);
            final CheckBox chkOPV4DTDK = (CheckBox)findViewById(R.id.chkOPV4DTDK);
            final EditText OPV4DT = (EditText)findViewById(R.id.OPV4DT);
            final Spinner  spnOPV4 = (Spinner)findViewById(R.id.spnOPV4);
            spnOPV4.setAdapter(dataSource);

            final CheckBox chkMeasles = (CheckBox)findViewById(R.id.chkMeasles);
            final CheckBox chkMeaslesDTDK = (CheckBox)findViewById(R.id.chkMeaslesDTDK);
            final EditText MeaslesDT = (EditText)findViewById(R.id.MeaslesDT);
            final Spinner  spnMeasles = (Spinner)findViewById(R.id.spnMeasles);
            spnMeasles.setAdapter(dataSource);

            final CheckBox chkMR = (CheckBox)findViewById(R.id.chkMR);
            final CheckBox chkMRDTDK = (CheckBox)findViewById(R.id.chkMRDTDK);
            final EditText  MRDT = (EditText)findViewById(R.id.MRDT);
            final Spinner  spnMR = (Spinner)findViewById(R.id.spnMR);
            spnMR.setAdapter(dataSource);

            final CheckBox chkRota = (CheckBox)findViewById(R.id.chkRota);
            final CheckBox chkRotaDTDK = (CheckBox)findViewById(R.id.chkRotaDTDK);
            final EditText  RotaDT = (EditText)findViewById(R.id.RotaDT);
            final Spinner  spnRota = (Spinner)findViewById(R.id.spnRota);
            spnRota.setAdapter(dataSource);

            final CheckBox chkMMR = (CheckBox)findViewById(R.id.chkMMR);
            final CheckBox chkMMRDTDK = (CheckBox)findViewById(R.id.chkMMRDTDK);
            final EditText  MMRDT = (EditText)findViewById(R.id.MMRDT);
            final Spinner  spnMMR = (Spinner)findViewById(R.id.spnMMR);
            spnMMR.setAdapter(dataSource);

            final CheckBox chkTyphoid = (CheckBox)findViewById(R.id.chkTyphoid);
            final CheckBox chkTyphoidDTDK = (CheckBox)findViewById(R.id.chkTyphoidDTDK);
            final EditText  TyphoidDT = (EditText)findViewById(R.id.TyphoidDT);
            final Spinner  spnTyphoid = (Spinner)findViewById(R.id.spnTyphoid);
            spnTyphoid.setAdapter(dataSource);

            final CheckBox chkInflu = (CheckBox)findViewById(R.id.chkInflu);
            final CheckBox chkInfluDTDK = (CheckBox)findViewById(R.id.chkInfluDTDK);
            final EditText  InfluDT = (EditText)findViewById(R.id.InfluDT);
            final Spinner  spnInflu = (Spinner)findViewById(R.id.spnInflu);
            spnInflu.setAdapter(dataSource);

            final CheckBox chkHepaA = (CheckBox)findViewById(R.id.chkHepaA);
            final CheckBox chkHepaADTDk = (CheckBox)findViewById(R.id.chkHepaADTDk);
            final EditText  HepaADT = (EditText)findViewById(R.id.HepaADT);
            final Spinner  spnHepaA = (Spinner)findViewById(R.id.spnHepaA);
            spnHepaA.setAdapter(dataSource);

            final CheckBox chkChickenPox = (CheckBox)findViewById(R.id.chkChickenPox);
            final CheckBox chkChickenPoxDTDk = (CheckBox)findViewById(R.id.chkChickenPoxDTDk);
            final EditText  ChickenPoxDT = (EditText)findViewById(R.id.ChickenPoxDT);
            final Spinner  spnChickenPox = (Spinner)findViewById(R.id.spnChickenPox);
            spnChickenPox.setAdapter(dataSource);

            final CheckBox chkRabies = (CheckBox)findViewById(R.id.chkRabies);
            final CheckBox chkRabiesDTDk = (CheckBox)findViewById(R.id.chkRabiesDTDk);
            final EditText  RabiesDT = (EditText)findViewById(R.id.RabiesDT);
            final Spinner  spnRabies = (Spinner)findViewById(R.id.spnRabies);
            spnRabies.setAdapter(dataSource);

            final CheckBox chkIPV = (CheckBox)findViewById(R.id.chkIPV);
            final CheckBox chkIPVDTDk = (CheckBox)findViewById(R.id.chkIPVDTDk);
            final EditText IPVDT = (EditText)findViewById(R.id.IPVDT);
            final Spinner  spnIPV = (Spinner)findViewById(R.id.spnIPV);
            spnIPV.setAdapter(dataSource);

            //23 apr 2018
            final CheckBox chkfIPV1 = (CheckBox)findViewById(R.id.chkfIPV1);
            final CheckBox chkFipvdt1dk = (CheckBox)findViewById(R.id.chkFipvdt1dk);
            final EditText fIPVDT1 = (EditText)findViewById(R.id.fIPVDT1);
            final Spinner  spnfIPV1 = (Spinner)findViewById(R.id.spnfIPV1);
            spnfIPV1.setAdapter(dataSource);

            final CheckBox chkfIPV2 = (CheckBox)findViewById(R.id.chkfIPV2);
            final CheckBox chkFipvdt2Dk = (CheckBox)findViewById(R.id.chkFipvdt2Dk);
            final EditText fIPVDT2 = (EditText)findViewById(R.id.fIPVDT2);
            final Spinner  spnfIPV2 = (Spinner)findViewById(R.id.spnfIPV2);
            spnfIPV2.setAdapter(dataSource);


            final CheckBox chkVitaminA = (CheckBox)findViewById(R.id.chkVitaminA);
            final CheckBox chkVitaminADTDk = (CheckBox)findViewById(R.id.chkVitaminADTDk);
            final EditText VitaminADT = (EditText)findViewById(R.id.VitaminADT);
            final Spinner  spnVitaminA = (Spinner)findViewById(R.id.spnVitaminA);

            String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
            String PNoAvailable="1";
            if(C.Existence("Select Vill from tTrans Where status='m' and Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"' and (length(pno)=0 or pno is null)"))
            {
                PNoAvailable="2";
            }
            else
            {
            }

            if(PNoAvailable.equals("1"))
                PNumber = C.ReturnSingleValue("Select PNo from tTrans Where Status='m' and Vill||Bari||HH||SNo='"+ Vill+Bari+HH+SN +"'");
            else
                PNumber = "";

            //if(PNumber.length()==0) return;

            chkBCG.setChecked(false);
            chkBCGDTDk.setChecked(false);
            chkPenta1.setChecked(false);
            chkPenta1DTDK.setChecked(false);
            chkPenta2.setChecked(false);
            chkPenta2DTDK.setChecked(false);
            chkPenta3.setChecked(false);
            chkPenta3DTDK.setChecked(false);
            chkPCV1.setChecked(false);
            chkPCV1DTDK.setChecked(false);
            chkPCV2.setChecked(false);
            chkPCV2DTDK.setChecked(false);
            chkPCV3.setChecked(false);
            chkPCV3DTDK.setChecked(false);
            chkOPV0.setChecked(false);
            chkOPV0DTDK.setChecked(false);
            chkOPV1.setChecked(false);
            chkOPV2.setChecked(false);
            chkOPV3.setChecked(false);
            chkOPV4.setChecked(false);
            chkOPV1DTDK.setChecked(false);
            chkOPV2DTDK.setChecked(false);
            chkOPV3DTDK.setChecked(false);
            chkOPV4DTDK.setChecked(false);

            chkMeasles.setChecked(false);
            chkMeaslesDTDK.setChecked(false);
            chkMR.setChecked(false);
            chkMRDTDK.setChecked(false);
            chkRota.setChecked(false);
            chkRotaDTDK.setChecked(false);
            chkMMR.setChecked(false);
            chkMMRDTDK.setChecked(false);
            chkTyphoid.setChecked(false);
            chkTyphoidDTDK.setChecked(false);
            chkInflu.setChecked(false);
            chkInfluDTDK.setChecked(false);
            chkHepaA.setChecked(false);
            chkHepaADTDk.setChecked(false);
            chkChickenPox.setChecked(false);
            chkChickenPoxDTDk.setChecked(false);
            chkRabies.setChecked(false);
            chkRabiesDTDk.setChecked(false);

            chkIPV.setChecked( false );
            chkIPVDTDk.setChecked( false );

            chkfIPV1.setChecked( false );
            chkFipvdt1dk.setChecked( false );
            chkfIPV2.setChecked( false );
            chkFipvdt2Dk.setChecked( false );

            chkVitaminA.setChecked( false );
            chkVitaminADTDk.setChecked( false );

            BCGDT.setText("");
            spnBCG.setSelection(0);

            Penta1DT.setText("");
            spnPenta1.setSelection(0);

            Penta2DT.setText("");
            spnPenta2.setSelection(0);

            Penta3DT.setText("");
            spnPenta3.setSelection(0);

            PCV1DT.setText("");
            spnPCV1.setSelection(0);

            PCV2DT.setText("");
            spnPCV2.setSelection(0);

            PCV3DT.setText("");
            spnPCV3.setSelection(0);

            OPV0DT.setText("");
            spnOPV0.setSelection(0);

            OPV1DT.setText("");
            spnOPV1.setSelection(0);

            OPV2DT.setText("");
            spnOPV2.setSelection(0);

            OPV3DT.setText("");
            spnOPV3.setSelection(0);

            OPV4DT.setText("");
            spnOPV4.setSelection(0);

            MeaslesDT.setText("");
            spnMeasles.setSelection(0);

            MRDT.setText("");
            spnMR.setSelection(0);

            RotaDT.setText("");
            spnRota.setSelection(0);

            MMRDT.setText("");
            spnMMR.setSelection(0);

            TyphoidDT.setText("");
            spnTyphoid.setSelection(0);

            InfluDT.setText("");
            spnInflu.setSelection(0);

            HepaADT.setText("");
            spnHepaA.setSelection(0);

            ChickenPoxDT.setText("");
            spnChickenPox.setSelection(0);

            RabiesDT.setText("");
            spnRabies.setSelection(0);

            IPVDT.setText("");
            spnIPV.setSelection(0);

            fIPVDT1.setText("");
            spnfIPV1.setSelection(0);

            fIPVDT2.setText("");
            spnfIPV2.setSelection(0);

            VitaminADT.setText("");
            spnVitaminA.setSelection(0);

            Cursor cur = null;
            if(PNoAvailable.equals("1"))
            {
                cur = C.ReadData("Select * from ImmunizationTemp where PNo='"+ PNumber +"'");
            }
            else
            {
                cur = C.ReadData("Select * from ImmunizationTemp where Vill||Bari||HH='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
            }

            cur.moveToFirst();
            while(!cur.isAfterLast())
            {
                spnVaccineStatus.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Status"))));

                if(cur.getString(cur.getColumnIndex("BCG")).equals("1"))
                {
                    chkBCG.setChecked(true);
                    BCGDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BCGDT"))));
                    spnBCG.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("BCGSource"))));

                    /*if(cur.getString(cur.getColumnIndex("BCGDTDk")).equals("1")){
                        chkBCGDTDk.setChecked(true);
                    }else {
                        chkBCGDTDk.setChecked(false);
                    }*/
                }
                if(cur.getString(cur.getColumnIndex("Penta1")).equals("1"))
                {
                    chkPenta1.setChecked(true);
                    Penta1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta1DT"))));
                    spnPenta1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta1Source"))));
                }
                if(cur.getString(cur.getColumnIndex("Penta2")).equals("1"))
                {
                    chkPenta2.setChecked(true);
                    Penta2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta2DT"))));
                    spnPenta2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta2Source"))));
                }
                if(cur.getString(cur.getColumnIndex("Penta3")).equals("1"))
                {
                    chkPenta3.setChecked(true);
                    Penta3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta3DT"))));
                    spnPenta3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta3Source"))));
                }
                if(cur.getString(cur.getColumnIndex("PCV1")).equals("1"))
                {
                    chkPCV1.setChecked(true);
                    PCV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV1DT"))));
                    spnPCV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV1Source"))));
                }
                if(cur.getString(cur.getColumnIndex("PCV2")).equals("1"))
                {
                    chkPCV2.setChecked(true);
                    PCV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV2DT"))));
                    spnPCV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV2Source"))));
                }
                if(cur.getString(cur.getColumnIndex("PCV3")).equals("1"))
                {
                    chkPCV3.setChecked(true);
                    PCV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV3DT"))));
                    spnPCV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV3Source"))));
                }
                if(cur.getString(cur.getColumnIndex("OPV0")).equals("1"))
                {
                    chkOPV0.setChecked(true);
                    OPV0DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV0DT"))));
                    spnOPV0.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV0Source"))));
                }
                if(cur.getString(cur.getColumnIndex("OPV1")).equals("1"))
                {
                    chkOPV1.setChecked(true);
                    OPV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV1DT"))));
                    spnOPV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV1Source"))));
                }
                if(cur.getString(cur.getColumnIndex("OPV2")).equals("1"))
                {
                    chkOPV2.setChecked(true);
                    OPV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV2DT"))));
                    spnOPV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV2Source"))));
                }
                if(cur.getString(cur.getColumnIndex("OPV3")).equals("1"))
                {
                    chkOPV3.setChecked(true);
                    OPV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV3DT"))));
                    spnOPV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV3Source"))));
                }
                if(cur.getString(cur.getColumnIndex("OPV4")).equals("1"))
                {
                    chkOPV4.setChecked(true);
                    OPV4DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV4DT"))));
                    spnOPV4.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV4Source"))));
                }
                if(cur.getString(cur.getColumnIndex("Measles")).equals("1"))
                {
                    chkMeasles.setChecked(true);
                    MeaslesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MeaslesDT"))));
                    spnMeasles.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MeaslesSource"))));
                }
                if(cur.getString(cur.getColumnIndex("MR")).equals("1"))
                {
                    chkMR.setChecked(true);
                    MRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MRDT"))));
                    spnMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MRSource"))));
                }
                if(cur.getString(cur.getColumnIndex("Rota")).equals("1"))
                {
                    chkRota.setChecked(true);
                    RotaDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RotaDT"))));
                    spnRota.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RotaSource"))));
                }
                if(cur.getString(cur.getColumnIndex("MMR")).equals("1"))
                {
                    chkMMR.setChecked(true);
                    MMRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MMRDT"))));
                    spnMMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MMRSource"))));
                }
                if(cur.getString(cur.getColumnIndex("Typhoid")).equals("1"))
                {
                    chkTyphoid.setChecked(true);
                    TyphoidDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("TyphoidDT"))));
                    spnTyphoid.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("TyphoidSource"))));
                }
                if(cur.getString(cur.getColumnIndex("MMR")).equals("1"))
                {
                    chkInflu.setChecked(true);
                    InfluDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("InfluDT"))));
                    spnInflu.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("InfluSource"))));
                }
                if(cur.getString(cur.getColumnIndex("HepaA")).equals("1"))
                {
                    chkHepaA.setChecked(true);
                    HepaADT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("HepaADT"))));
                    spnHepaA.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("HepaASource"))));
                }
                if(cur.getString(cur.getColumnIndex("ChickenPox")).equals("1"))
                {
                    chkChickenPox.setChecked(true);
                    ChickenPoxDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("ChickenPoxDT"))));
                    spnChickenPox.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("ChickenPoxSource"))));
                }
                if(cur.getString(cur.getColumnIndex("Rabies")).equals("1"))
                {
                    chkRabies.setChecked(true);
                    RabiesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RabiesDT"))));
                    spnRabies.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RabiesSource"))));
                }
                if(cur.getString(cur.getColumnIndex("IPV")).equals("1"))
                {
                    chkIPV.setChecked(true);
                    IPVDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("IPVDT"))));
                    spnIPV.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("IPVSource"))));
                }

                //23 apr 2018
                if(cur.getString(cur.getColumnIndex("fIPV1")).equals("1"))
                {
                    chkfIPV1.setChecked(true);
                    fIPVDT1.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("fIPVDT1"))));
                    spnfIPV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("fIPVSource1"))));
                }

                if(cur.getString(cur.getColumnIndex("fIPV2")).equals("1"))
                {
                    chkfIPV2.setChecked(true);
                    fIPVDT2.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("fIPVDT2"))));
                    spnfIPV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("fIPVSource2"))));
                }


                if(cur.getString(cur.getColumnIndex("VitaminA")).equals("1"))
                {
                    chkVitaminA.setChecked(true);
                    VitaminADT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("VitaminADT"))));
                    spnVitaminA.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("VitaminASource"))));
                }

                cur.moveToNext();
            }
            cur.close();
        }
        catch(Exception ex)
        {
            //Connection.MessageBox(Immunization.this, ex.getMessage());
        }
    }



private void PhotoView(String ImageID)
{
    try
    {
      final Dialog dialog = new Dialog(Immunization.this);
      dialog.setTitle("Photo View");
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      dialog.setContentView(R.layout.photoview);
      dialog.setCanceledOnTouchOutside(true);
      dialog.setCancelable(true);
          ImageView photo = (ImageView)dialog.findViewById(R.id.photoZoom);
          File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", ImageID +".jpg");
          if(mFichier1.exists())
          {
              photo.setImageURI(Uri.fromFile(mFichier1));
          }
          
      dialog.show();
    }
    catch(Exception ex)
    {
        Connection.MessageBox( Immunization.this, ex.getMessage() );
        return;
    }
}


private int SpinnerSelection(String DataValue)
{
    int i=0;
    if(DataValue.length()==0)
    {
            i=0;
    }
    else
    {
            i = Integer.parseInt(DataValue);
    }
    return i;
}

/*
@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
     super.onRestoreInstanceState(savedInstanceState);
     try
     {
        String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
        File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"5.jpg");
        if(mFichier1.exists())
        {
         mImageView5.setImageURI(Uri.fromFile(mFichier1));
        }
        //mImageView5.setVisibility(View.VISIBLE);
     }
     catch(Exception ex)
     {
         Connection.MessageBox( Immunization.this, ex.getMessage() );
         return;
     }
    
}
*/

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year; mMonth = monthOfYear+1; mDay = dayOfMonth;
            EditText dtpDate;


            dtpDate = (EditText)findViewById(R.id.BCGDT);
            if (VariableID.equals("BCGDT"))
            {
                dtpDate = (EditText)findViewById(R.id.BCGDT);
            }
            /*else if (VariableID.equals("btnExDate"))
            {
                dtpDate = (EditText)findViewById(R.id.dtpExDate);
            }*/

            dtpDate.setText(new StringBuilder()
                    .append(Common.Global.Right("00"+mDay,2)).append("/")
                    .append(Common.Global.Right("00"+mMonth,2)).append("/")
                    .append(mYear));
        }
    };
//private static int TAKE_PICTURE = 1;
//private Uri outputFileUri;
public void TakePhoto(String PhotoName)
{
    try
    {
             /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             File file = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos" , PhotoName+".jpg");

             Uri outputFileUri = Uri.fromFile(file);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); //EXTRA_OUTPUT
             startActivityForResult(intent, TAKE_PICTURE);
             */
        
        
             /*
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File outFile = getFileStreamPath(Config.IMAGE_FILENAME);
            outFile.createNewFile();
            outFile.setWritable(true, false);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(outFile));
            startActivityForResult(intent, 2);
            }
              */

        /*
             Intent intent1 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
             String fname = Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos" + File.separator + PhotoName +".jpg";
             File outFile = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos" , PhotoName+".jpg");
             //File outFile = getFileStreamPath(fname);
             outFile.createNewFile();
             outFile.setWritable(true, false);
             //intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,Uri.fromFile(outFile));
             Uri outputFileUri = FileProvider.getUriForFile(Immunization.this, BuildConfig.APPLICATION_ID, outFile);
             intent1.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);
             startActivityForResult(intent1, 2);
*/
        final int REQUEST_IMAGE_CAPTURE = 1;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File outFile = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos" , PhotoName+".jpg");
            outFile.createNewFile();
            Uri outputFileUri = FileProvider.getUriForFile(Immunization.this, "android.arch.core.provider", outFile);
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }

    }
    catch(Exception ex)
    {
        Connection.MessageBox( Immunization.this, ex.getMessage() );
        return;
    }
             
             /*
        File mFichier = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", txtPatientID.getText().toString() +".jpg");

        if(mFichier.exists())
        {
             mImageView.setImageURI(Uri.fromFile(mFichier));
        }
        mImageView.setVisibility(View.VISIBLE);*/
}

}