package data.mirzapurdss;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
        spnMemList.setAdapter(C.getArrayAdapter("Select (SNo||':'||Name||',DOB:'||(substr(bdate,9,2)||'/'||substr(bdate,6,2)||'/'||substr(bdate,1,4))||', Age(Yr):'||cast((julianday(date('now'))-julianday(bdate))/365.25 as int))MemInfo from tTrans where status='m' and length(extype)=0 and vill||bari||hh='"+ (Vill+Bari+HH) +"' and cast((julianday(date('now'))-julianday(bdate))/30.4 as int)<=59"));

        ArrayAdapter listVaccineStatus = ArrayAdapter.createFromResource(this, R.array.listVaccineStatus, android.R.layout.simple_spinner_item);
        listVaccineStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVaccineStatus = (Spinner)findViewById(R.id.spnVaccineStatus);
        spnVaccineStatus.setAdapter(listVaccineStatus);

        dataSource = ArrayAdapter.createFromResource(this, R.array.listVaccineCenter, android.R.layout.simple_spinner_item);
        dataSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    
        final CheckBox chkBCG = (CheckBox)findViewById(R.id.chkBCG);
        final EditText BCGDT = (EditText)findViewById(R.id.BCGDT);
        final Spinner  spnBCG= (Spinner)findViewById(R.id.spnBCG);
        spnBCG.setAdapter(dataSource);
        
        final CheckBox chkPenta1 = (CheckBox)findViewById(R.id.chkPenta1);
        final EditText Penta1DT = (EditText)findViewById(R.id.Penta1DT);
        final Spinner  spnPenta1 = (Spinner)findViewById(R.id.spnPenta1);
        spnPenta1.setAdapter(dataSource);
        
        final CheckBox chkPenta2 = (CheckBox)findViewById(R.id.chkPenta2);
        final EditText Penta2DT = (EditText)findViewById(R.id.Penta2DT);
        final Spinner  spnPenta2 = (Spinner)findViewById(R.id.spnPenta2);
        spnPenta2.setAdapter(dataSource);
        
        final CheckBox chkPenta3 = (CheckBox)findViewById(R.id.chkPenta3);
        final EditText Penta3DT = (EditText)findViewById(R.id.Penta3DT);
        final Spinner  spnPenta3 = (Spinner)findViewById(R.id.spnPenta3);
        spnPenta3.setAdapter(dataSource);
        
        final CheckBox chkPCV1 = (CheckBox)findViewById(R.id.chkPCV1);
        final EditText PCV1DT = (EditText)findViewById(R.id.PCV1DT);
        final Spinner  spnPCV1 = (Spinner)findViewById(R.id.spnPCV1);
        spnPCV1.setAdapter(dataSource);
        
        final CheckBox chkPCV2 = (CheckBox)findViewById(R.id.chkPCV2);
        final EditText PCV2DT = (EditText)findViewById(R.id.PCV2DT);
        final Spinner  spnPCV2 = (Spinner)findViewById(R.id.spnPCV2);
        spnPCV2.setAdapter(dataSource);
        
        final CheckBox chkPCV3 = (CheckBox)findViewById(R.id.chkPCV3);
        final EditText PCV3DT = (EditText)findViewById(R.id.PCV3DT);
        final Spinner  spnPCV3 = (Spinner)findViewById(R.id.spnPCV3);
        spnPCV3.setAdapter(dataSource);
        
        final CheckBox chkOPV0 = (CheckBox)findViewById(R.id.chkOPV0);
        final EditText OPV0DT = (EditText)findViewById(R.id.OPV0DT);
        final Spinner  spnOPV0 = (Spinner)findViewById(R.id.spnOPV0);
        spnOPV0.setAdapter(dataSource);
        
        final CheckBox chkOPV1 = (CheckBox)findViewById(R.id.chkOPV1);
        final EditText OPV1DT = (EditText)findViewById(R.id.OPV1DT);
        final Spinner  spnOPV1 = (Spinner)findViewById(R.id.spnOPV1);
        spnOPV1.setAdapter(dataSource);
        
        final CheckBox chkOPV2 = (CheckBox)findViewById(R.id.chkOPV2);
        final EditText OPV2DT = (EditText)findViewById(R.id.OPV2DT);
        final Spinner  spnOPV2 = (Spinner)findViewById(R.id.spnOPV2);
        spnOPV2.setAdapter(dataSource);
        
        final CheckBox chkOPV3 = (CheckBox)findViewById(R.id.chkOPV3);
        final EditText OPV3DT = (EditText)findViewById(R.id.OPV3DT);
        final Spinner  spnOPV3 = (Spinner)findViewById(R.id.spnOPV3);
        spnOPV3.setAdapter(dataSource);
        
        final CheckBox chkOPV4 = (CheckBox)findViewById(R.id.chkOPV4);
        final EditText OPV4DT = (EditText)findViewById(R.id.OPV4DT);
        final Spinner  spnOPV4 = (Spinner)findViewById(R.id.spnOPV4);
        spnOPV4.setAdapter(dataSource);
        
        final CheckBox chkMeasles = (CheckBox)findViewById(R.id.chkMeasles);
        final EditText MeaslesDT = (EditText)findViewById(R.id.MeaslesDT);
        final Spinner  spnMeasles = (Spinner)findViewById(R.id.spnMeasles);
        spnMeasles.setAdapter(dataSource);
        
        final CheckBox chkMR = (CheckBox)findViewById(R.id.chkMR);
        final EditText  MRDT = (EditText)findViewById(R.id.MRDT);
        final Spinner  spnMR = (Spinner)findViewById(R.id.spnMR);
        spnMR.setAdapter(dataSource);
        
        final CheckBox chkRota = (CheckBox)findViewById(R.id.chkRota);
        final EditText  RotaDT = (EditText)findViewById(R.id.RotaDT);
        final Spinner  spnRota = (Spinner)findViewById(R.id.spnRota);
        spnRota.setAdapter(dataSource);
        
        final CheckBox chkMMR = (CheckBox)findViewById(R.id.chkMMR);
        final EditText  MMRDT = (EditText)findViewById(R.id.MMRDT);
        final Spinner  spnMMR = (Spinner)findViewById(R.id.spnMMR);
        spnMMR.setAdapter(dataSource);
        
        final CheckBox chkTyphoid = (CheckBox)findViewById(R.id.chkTyphoid);
        final EditText  TyphoidDT = (EditText)findViewById(R.id.TyphoidDT);
        final Spinner  spnTyphoid = (Spinner)findViewById(R.id.spnTyphoid);
        spnTyphoid.setAdapter(dataSource);
        
        final CheckBox chkInflu = (CheckBox)findViewById(R.id.chkInflu);
        final EditText  InfluDT = (EditText)findViewById(R.id.InfluDT);
        final Spinner  spnInflu = (Spinner)findViewById(R.id.spnInflu);
        spnInflu.setAdapter(dataSource);
        
        final CheckBox chkHepaA = (CheckBox)findViewById(R.id.chkHepaA);
        final EditText  HepaADT = (EditText)findViewById(R.id.HepaADT);
        final Spinner  spnHepaA = (Spinner)findViewById(R.id.spnHepaA);
        spnHepaA.setAdapter(dataSource);
        
        final CheckBox chkChickenPox = (CheckBox)findViewById(R.id.chkChickenPox);
        final EditText  ChickenPoxDT = (EditText)findViewById(R.id.ChickenPoxDT);
        final Spinner  spnChickenPox = (Spinner)findViewById(R.id.spnChickenPox);
        spnChickenPox.setAdapter(dataSource);
        
        final CheckBox chkRabies = (CheckBox)findViewById(R.id.chkRabies);
        final EditText  RabiesDT = (EditText)findViewById(R.id.RabiesDT);
        final Spinner  spnRabies = (Spinner)findViewById(R.id.spnRabies);
        spnRabies.setAdapter(dataSource);
        
        final CheckBox chkIPV = (CheckBox)findViewById(R.id.chkIPV);
        final EditText IPVDT = (EditText)findViewById(R.id.IPVDT);
        final Spinner  spnIPV = (Spinner)findViewById(R.id.spnIPV);
        spnIPV.setAdapter(dataSource);

        //23apr 2018
        final CheckBox chkfIPV1 = (CheckBox)findViewById(R.id.chkfIPV1);
        final EditText fIPVDT1 = (EditText)findViewById(R.id.fIPVDT1);
        final Spinner  spnfIPV1 = (Spinner)findViewById(R.id.spnfIPV1);
        spnfIPV1.setAdapter(dataSource);

        final CheckBox chkfIPV2 = (CheckBox)findViewById(R.id.chkfIPV2);
        final EditText fIPVDT2 = (EditText)findViewById(R.id.fIPVDT2);
        final Spinner  spnfIPV2 = (Spinner)findViewById(R.id.spnfIPV2);
        spnfIPV2.setAdapter(dataSource);

        final CheckBox chkVitaminA = (CheckBox)findViewById(R.id.chkVitaminA);
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
                    
                        if(PNoAvailable.equals("1"))
                                PNumber = C.ReturnSingleValue("Select PNo from tTrans Where Status='m' and Vill||Bari||HH||SNo='"+ Vill+Bari+HH+SN +"'");
                        else
                                PNumber = "";
                    
                            //if(PNumber.length()==0) return;
                            
                            chkBCG.setChecked(false);
                            chkPenta1.setChecked(false);
                            chkPenta2.setChecked(false);
                            chkPenta3.setChecked(false);
                            chkPCV1.setChecked(false);
                            chkPCV2.setChecked(false);
                            chkPCV3.setChecked(false);
                            chkOPV0.setChecked(false);
                            chkOPV1.setChecked(false);
                            chkOPV2.setChecked(false);
                            chkOPV3.setChecked(false);
                            chkOPV4.setChecked(false);
                            chkMeasles.setChecked(false);
                            chkMR.setChecked(false);
                            chkRota.setChecked(false);
                            chkMMR.setChecked(false);
                            chkTyphoid.setChecked(false);
                            chkInflu.setChecked(false);
                            chkHepaA.setChecked(false);
                            chkChickenPox.setChecked(false);
                            chkRabies.setChecked(false);
                            chkIPV.setChecked( false );
                            chkfIPV1.setChecked( false );
                            chkfIPV2.setChecked( false );
                            chkVitaminA.setChecked( false );
                            
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
                            //Cursor cur=C.ReadData("Select * from ImmunizationTemp where Vill||Bari||HH='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
                            //Cursor cur=C.ReadData("Select * from ImmunizationTemp where PNo='"+ PNumber +"'");
                            
                                    cur.moveToFirst();
                                    while(!cur.isAfterLast())
                                    {                               
                                            spnVaccineStatus.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Status"))));
                                            
                                            if(cur.getString(cur.getColumnIndex("BCG")).equals("1"))
                                            {
                                                    chkBCG.setChecked(true);
                                                BCGDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BCGDT"))));
                                                spnBCG.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("BCGSource"))));
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
                            chkPenta1.setChecked(false);
                            chkPenta2.setChecked(false);
                            chkPenta3.setChecked(false);
                            chkPCV1.setChecked(false);
                            chkPCV2.setChecked(false);
                            chkPCV3.setChecked(false);
                            chkOPV0.setChecked(false);
                            chkOPV1.setChecked(false);
                            chkOPV2.setChecked(false);
                            chkOPV3.setChecked(false);
                            chkOPV4.setChecked(false);
                            chkMeasles.setChecked(false);
                            chkMR.setChecked(false);
                            chkRota.setChecked(false);
                            chkMMR.setChecked(false);
                            chkTyphoid.setChecked(false);
                            chkInflu.setChecked(false);
                            chkHepaA.setChecked(false);
                            chkChickenPox.setChecked(false);
                            chkRabies.setChecked(false);
                            chkIPV.setChecked(false);
                            chkfIPV1.setChecked(false);
                            chkfIPV2.setChecked(false);
                            chkVitaminA.setChecked(false);
                            
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
            
                    if(chkBCG.isChecked()==true)
                    {
                            EDT = Global.DateValidate(BCGDT.getText().toString());
                    if(EDT.length()!=0)
                            {
                                    Connection.MessageBox(Immunization.this, EDT); 
                                    BCGDT.requestFocus();
                                    return;
                            }
                    else if(spnBCG.getSelectedItemPosition()==0)
                    {
                            Connection.MessageBox(Immunization.this, "Select a valid vaccination center.");
                            spnBCG.requestFocus();
                            return;
                    }
                    VacD = sdf.parse(Global.DateConvertYMD(BCGDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination data should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            BCGDT.requestFocus();
                            return;
                    }
                    
                            SQL +="BCG='1',";
                            SQL +="BCGDT='"+ Global.DateConvertYMD(BCGDT.getText().toString()) +"',";
                            SQL +="BCGSource='"+ Global.Left(spnBCG.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="BCG='',";
                            SQL +="BCGDT='',";
                            SQL +="BCGSource='',";
                    }
                    if(chkPenta1.isChecked()==true)
                    {
                            EDT = Global.DateValidate(Penta1DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(Penta1DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            Penta1DT.requestFocus();
                            return;
                    }

                            SQL +="Penta1='1',";
                            SQL +="Penta1DT='"+ Global.DateConvertYMD(Penta1DT.getText().toString()) +"',";
                            SQL +="Penta1Source='"+ Global.Left(spnPenta1.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="Penta1='',";
                            SQL +="Penta1DT='',";
                            SQL +="Penta1Source='',";
                    }
                    
                    if(chkPenta2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(Penta2DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(Penta2DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            Penta2DT.requestFocus();
                            return;
                    }
                    
                            SQL +="Penta2='1',";
                            SQL +="Penta2DT='"+ Global.DateConvertYMD(Penta2DT.getText().toString()) +"',";
                            SQL +="Penta2Source='"+ Global.Left(spnPenta2.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="Penta2='',";
                            SQL +="Penta2DT='',";
                            SQL +="Penta2Source='',";
                    }  
                    
                    if(chkPenta3.isChecked()==true)
                    {
                            EDT = Global.DateValidate(Penta3DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(Penta3DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            Penta3DT.requestFocus();
                            return;
                    }
                    
                            SQL +="Penta3='1',";                                    
                            SQL +="Penta3DT='"+ Global.DateConvertYMD(Penta3DT.getText().toString()) +"',";
                            SQL +="Penta3Source='"+ Global.Left(spnPenta3.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="Penta3='',";
                            SQL +="Penta3DT='',";
                            SQL +="Penta3Source='',";
                    }
                    
                    if(chkPCV1.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV1DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(PCV1DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            PCV1DT.requestFocus();
                            return;
                    }
                    
                            SQL +="PCV1='1',";                              
                            SQL +="PCV1DT='"+ Global.DateConvertYMD(PCV1DT.getText().toString()) +"',";
                            SQL +="PCV1Source='"+ Global.Left(spnPCV1.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="PCV1='',";
                            SQL +="PCV1DT='',";
                            SQL +="PCV1Source='',";
                    }
                    if(chkPCV2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV2DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(PCV2DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            PCV2DT.requestFocus();
                            return;
                    }
                    
                            SQL +="PCV2='"+ (chkPCV2.isChecked()==true?"1":"") +"',";                               
                            SQL +="PCV2DT='"+ Global.DateConvertYMD(PCV2DT.getText().toString()) +"',";
                            SQL +="PCV2Source='"+ Global.Left(spnPCV2.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="PCV2='',";
                            SQL +="PCV2DT='',";
                            SQL +="PCV2Source='',";
                    }
                    if(chkPCV3.isChecked()==true)
                    {
                            EDT = Global.DateValidate(PCV3DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(PCV3DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            PCV3DT.requestFocus();
                            return;
                    }
                    
                            SQL +="PCV3='1',";                              
                            SQL +="PCV3DT='"+ Global.DateConvertYMD(PCV3DT.getText().toString()) +"',";
                            SQL +="PCV3Source='"+ Global.Left(spnPCV3.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="PCV3='',";
                            SQL +="PCV3DT='',";
                            SQL +="PCV3Source='',";
                    }
                    if(chkOPV0.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV0DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(OPV0DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            OPV0DT.requestFocus();
                            return;
                    }
                    
                            SQL +="OPV0='1',";                              
                            SQL +="OPV0DT='"+ Global.DateConvertYMD(OPV0DT.getText().toString()) +"',";
                            SQL +="OPV0Source='"+ Global.Left(spnOPV0.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="OPV0='',";
                            SQL +="OPV0DT='',";
                            SQL +="OPV0Source='',";
                    }
                    
                    if(chkOPV1.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV1DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(OPV1DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            OPV1DT.requestFocus();
                            return;
                    }
                    
                            SQL +="OPV1='1',";                              
                            SQL +="OPV1DT='"+ Global.DateConvertYMD(OPV1DT.getText().toString()) +"',";
                            SQL +="OPV1Source='"+ Global.Left(spnOPV1.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="OPV1='',";
                            SQL +="OPV1DT='',";
                            SQL +="OPV1Source='',";
                    }
                    if(chkOPV2.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV2DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(OPV2DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            OPV2DT.requestFocus();
                            return;
                    }

                    
                            SQL +="OPV2='1',";                              
                            SQL +="OPV2DT='"+ Global.DateConvertYMD(OPV2DT.getText().toString()) +"',";
                            SQL +="OPV2Source='"+ Global.Left(spnOPV2.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="OPV2='',";
                            SQL +="OPV2DT='',";
                            SQL +="OPV2Source='',";
                    }
                    
                    
                    if(chkOPV3.isChecked()==true)
                    {       
                            EDT = Global.DateValidate(OPV3DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(OPV3DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            OPV3DT.requestFocus();
                            return;
                    }
                    
                            SQL +="OPV3='1',";                              
                            SQL +="OPV3DT='"+ Global.DateConvertYMD(OPV3DT.getText().toString()) +"',";
                            SQL +="OPV3Source='"+ Global.Left(spnOPV3.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="OPV3='',";
                            SQL +="OPV3DT='',";
                            SQL +="OPV3Source='',";
                    }
                    if(chkOPV4.isChecked()==true)
                    {
                            EDT = Global.DateValidate(OPV4DT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(OPV4DT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            OPV4DT.requestFocus();
                            return;
                    }
                    
                            SQL +="OPV4='1',";                              
                            SQL +="OPV4DT='"+ Global.DateConvertYMD(OPV4DT.getText().toString()) +"',";
                            SQL +="OPV4Source='"+ Global.Left(spnOPV4.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="OPV4='',";
                            SQL +="OPV4DT='',";
                            SQL +="OPV4Source='',";
                    }
                    
                    if(chkMeasles.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MeaslesDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(MeaslesDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            MeaslesDT.requestFocus();
                            return;
                    }
                    
                            SQL +="Measles='1',";                                   
                            SQL +="MeaslesDT='"+ Global.DateConvertYMD(MeaslesDT.getText().toString()) +"',";
                            SQL +="MeaslesSource='"+ Global.Left(spnMeasles.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="Measles='',";
                            SQL +="MeaslesDT='',";
                            SQL +="MeaslesSource='',";
                    }
                    
                    if(chkMR.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MRDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(MRDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            MRDT.requestFocus();
                            return;
                    }
                    
                            SQL +="MR='1',";                                
                            SQL +="MRDT='"+ Global.DateConvertYMD(MRDT.getText().toString()) +"',";
                            SQL +="MRSource='"+ Global.Left(spnMR.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="MR='',";
                            SQL +="MRDT='',";
                            SQL +="MRSource='',";
                    }
                    
                    if(chkRota.isChecked()==true)
                    {
                            EDT = Global.DateValidate(RotaDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(RotaDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            RotaDT.requestFocus();
                            return;
                    }
                    
                            SQL +="Rota='1',";                              
                            SQL +="RotaDT='"+ Global.DateConvertYMD(RotaDT.getText().toString()) +"',";
                            SQL +="RotaSource='"+ Global.Left(spnRota.getSelectedItem().toString(),2) +"',";                                
                    }
                    else
                    {
                            SQL +="Rota='',";                               
                            SQL +="RotaDT='',";
                            SQL +="RotaSource='',";                                         
                    }
                    
                    if(chkMMR.isChecked()==true)
                    {
                            EDT = Global.DateValidate(MMRDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(MMRDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            MMRDT.requestFocus();
                            return;
                    }
                    
                            SQL +="MMR='1',";                               
                            SQL +="MMRDT='"+ Global.DateConvertYMD(MMRDT.getText().toString()) +"',";
                            SQL +="MMRSource='"+ Global.Left(spnMMR.getSelectedItem().toString(),2) +"',";                                  
                    }
                    else
                    {
                            SQL +="MMR='',";                                
                            SQL +="MMRDT='',";
                            SQL +="MMRSource='',";                                  
                    }                       
                    
                    if(chkTyphoid.isChecked()==true)
                    {
                            EDT = Global.DateValidate(TyphoidDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(TyphoidDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            TyphoidDT.requestFocus();
                            return;
                    }
                    
                            SQL +="Typhoid='1',";                                   
                            SQL +="TyphoidDT='"+ Global.DateConvertYMD(TyphoidDT.getText().toString()) +"',";
                            SQL +="TyphoidSource='"+ Global.Left(spnTyphoid.getSelectedItem().toString(),2) +"',";                                  
                    }
                    else
                    {
                            SQL +="Typhoid='',";                                    
                            SQL +="TyphoidDT='',";
                            SQL +="TyphoidSource='',";                                                              
                    }

                    if(chkInflu.isChecked()==true)
                    {
                            EDT = Global.DateValidate(InfluDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(InfluDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            InfluDT.requestFocus();
                            return;
                    }
                    
                            SQL +="Influ='1',";                             
                            SQL +="InfluDT='"+ Global.DateConvertYMD(InfluDT.getText().toString()) +"',";
                            SQL +="InfluSource='"+ Global.Left(spnInflu.getSelectedItem().toString(),2) +"',";                              
                    }
                    else
                    {
                            SQL +="Influ='',";                              
                            SQL +="InfluDT='',";
                            SQL +="InfluSource='',";                                                                
                    }

                    if(chkHepaA.isChecked()==true)
                    {
                            EDT = Global.DateValidate(HepaADT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(HepaADT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            HepaADT.requestFocus();
                            return;
                    }
                    
                            SQL +="HepaA='1',";                             
                            SQL +="HepaADT='"+ Global.DateConvertYMD(HepaADT.getText().toString()) +"',";
                            SQL +="HepaASource='"+ Global.Left(spnHepaA.getSelectedItem().toString(),2) +"',";                              
                    }
                    else
                    {
                            SQL +="HepaA='',";                              
                            SQL +="HepaADT='',";
                            SQL +="HepaASource='',";                                        
                    }
                    
                    if(chkChickenPox.isChecked()==true)
                    {
                            EDT = Global.DateValidate(ChickenPoxDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(ChickenPoxDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            ChickenPoxDT.requestFocus();
                            return;
                    }
                    
                            SQL +="ChickenPox='1',";                                
                            SQL +="ChickenPoxDT='"+ Global.DateConvertYMD(ChickenPoxDT.getText().toString()) +"',";
                            SQL +="ChickenPoxSource='"+ Global.Left(spnChickenPox.getSelectedItem().toString(),2) +"',";                                    
                    }
                    else
                    {
                            SQL +="ChickenPox='',";                                 
                            SQL +="ChickenPoxDT='',";
                            SQL +="ChickenPoxSource='',";                                   
                    }
                    
                    if(chkRabies.isChecked()==true)
                    {
                            EDT = Global.DateValidate(RabiesDT.getText().toString());
                    if(EDT.length()!=0)
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
                    VacD = sdf.parse(Global.DateConvertYMD(RabiesDT.getText().toString()));
                    if(BD.after(VacD))
                    {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            RabiesDT.requestFocus();
                            return;
                    }
                    
                            SQL +="Rabies='1',";                                    
                            SQL +="RabiesDT='"+ Global.DateConvertYMD(RabiesDT.getText().toString()) +"',";
                            SQL +="RabiesSource='"+ Global.Left(spnRabies.getSelectedItem().toString(),2) +"',";                                    
                    }
                    else
                    {
                            SQL +="Rabies='',";                             
                            SQL +="RabiesDT='',";
                            SQL +="RabiesSource='',";                                                               
                    }

                    if(chkIPV.isChecked()==true)
                    {
                            EDT = Global.DateValidate(IPVDT.getText().toString());
                            if(EDT.length()!=0)
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
                            VacD = sdf.parse(Global.DateConvertYMD(IPVDT.getText().toString()));
                            if(BD.after(VacD))
                            {
                                    Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                                    IPVDT.requestFocus();
                                    return;
                            }

                            SQL +="IPV='1',";
                            SQL +="IPVDT='"+ Global.DateConvertYMD(IPVDT.getText().toString()) +"',";
                            SQL +="IPVSource='"+ Global.Left(spnIPV.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="IPV='',";
                            SQL +="IPVDT='',";
                            SQL +="IPVSource='',";
                    }

                    //23 apr 2018
                    if(chkfIPV1.isChecked()==true)
                    {
                        EDT = Global.DateValidate(fIPVDT1.getText().toString());
                        if(EDT.length()!=0)
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
                        VacD = sdf.parse(Global.DateConvertYMD(fIPVDT1.getText().toString()));
                        if(BD.after(VacD))
                        {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            fIPVDT1.requestFocus();
                            return;
                        }

                        SQL +="fIPV1='1',";
                        SQL +="fIPVDT1='"+ Global.DateConvertYMD(fIPVDT1.getText().toString()) +"',";
                        SQL +="fIPVSource1='"+ Global.Left(spnfIPV1.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                        SQL +="fIPV1='',";
                        SQL +="fIPVDT1='',";
                        SQL +="fIPVSource1='',";
                    }

                    if(chkfIPV2.isChecked()==true)
                    {
                        EDT = Global.DateValidate(fIPVDT2.getText().toString());
                        if(EDT.length()!=0)
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
                        VacD = sdf.parse(Global.DateConvertYMD(fIPVDT2.getText().toString()));
                        if(BD.after(VacD))
                        {
                            Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                            fIPVDT2.requestFocus();
                            return;
                        }

                        SQL +="fIPV2='1',";
                        SQL +="fIPVDT2='"+ Global.DateConvertYMD(fIPVDT2.getText().toString()) +"',";
                        SQL +="fIPVSource2='"+ Global.Left(spnfIPV2.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                        SQL +="fIPV2='',";
                        SQL +="fIPVDT2='',";
                        SQL +="fIPVSource2='',";
                    }




                    if(chkVitaminA.isChecked()==true)
                    {
                            EDT = Global.DateValidate(VitaminADT.getText().toString());
                            if(EDT.length()!=0)
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
                            VacD = sdf.parse(Global.DateConvertYMD(VitaminADT.getText().toString()));
                            if(BD.after(VacD))
                            {
                                    Connection.MessageBox(Immunization.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
                                    VitaminADT.requestFocus();
                                    return;
                            }
                    
                            SQL +="VitaminA='1',";                              
                            SQL +="VitaminADT='"+ Global.DateConvertYMD(VitaminADT.getText().toString()) +"',";
                            SQL +="VitaminASource='"+ Global.Left(spnVitaminA.getSelectedItem().toString(),2) +"',";
                    }
                    else
                    {
                            SQL +="VitaminA='',";
                            SQL +="VitaminADT='',";
                            SQL +="VitaminASource='',";
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
            final EditText BCGDT = (EditText)findViewById(R.id.BCGDT);
            final Spinner  spnBCG= (Spinner)findViewById(R.id.spnBCG);
            spnBCG.setAdapter(dataSource);

            final CheckBox chkPenta1 = (CheckBox)findViewById(R.id.chkPenta1);
            final EditText Penta1DT = (EditText)findViewById(R.id.Penta1DT);
            final Spinner  spnPenta1 = (Spinner)findViewById(R.id.spnPenta1);
            spnPenta1.setAdapter(dataSource);

            final CheckBox chkPenta2 = (CheckBox)findViewById(R.id.chkPenta2);
            final EditText Penta2DT = (EditText)findViewById(R.id.Penta2DT);
            final Spinner  spnPenta2 = (Spinner)findViewById(R.id.spnPenta2);
            spnPenta2.setAdapter(dataSource);

            final CheckBox chkPenta3 = (CheckBox)findViewById(R.id.chkPenta3);
            final EditText Penta3DT = (EditText)findViewById(R.id.Penta3DT);
            final Spinner  spnPenta3 = (Spinner)findViewById(R.id.spnPenta3);
            spnPenta3.setAdapter(dataSource);

            final CheckBox chkPCV1 = (CheckBox)findViewById(R.id.chkPCV1);
            final EditText PCV1DT = (EditText)findViewById(R.id.PCV1DT);
            final Spinner  spnPCV1 = (Spinner)findViewById(R.id.spnPCV1);
            spnPCV1.setAdapter(dataSource);

            final CheckBox chkPCV2 = (CheckBox)findViewById(R.id.chkPCV2);
            final EditText PCV2DT = (EditText)findViewById(R.id.PCV2DT);
            final Spinner  spnPCV2 = (Spinner)findViewById(R.id.spnPCV2);
            spnPCV2.setAdapter(dataSource);

            final CheckBox chkPCV3 = (CheckBox)findViewById(R.id.chkPCV3);
            final EditText PCV3DT = (EditText)findViewById(R.id.PCV3DT);
            final Spinner  spnPCV3 = (Spinner)findViewById(R.id.spnPCV3);
            spnPCV3.setAdapter(dataSource);

            final CheckBox chkOPV0 = (CheckBox)findViewById(R.id.chkOPV0);
            final EditText OPV0DT = (EditText)findViewById(R.id.OPV0DT);
            final Spinner  spnOPV0 = (Spinner)findViewById(R.id.spnOPV0);
            spnOPV0.setAdapter(dataSource);

            final CheckBox chkOPV1 = (CheckBox)findViewById(R.id.chkOPV1);
            final EditText OPV1DT = (EditText)findViewById(R.id.OPV1DT);
            final Spinner  spnOPV1 = (Spinner)findViewById(R.id.spnOPV1);
            spnOPV1.setAdapter(dataSource);

            final CheckBox chkOPV2 = (CheckBox)findViewById(R.id.chkOPV2);
            final EditText OPV2DT = (EditText)findViewById(R.id.OPV2DT);
            final Spinner  spnOPV2 = (Spinner)findViewById(R.id.spnOPV2);
            spnOPV2.setAdapter(dataSource);

            final CheckBox chkOPV3 = (CheckBox)findViewById(R.id.chkOPV3);
            final EditText OPV3DT = (EditText)findViewById(R.id.OPV3DT);
            final Spinner  spnOPV3 = (Spinner)findViewById(R.id.spnOPV3);
            spnOPV3.setAdapter(dataSource);

            final CheckBox chkOPV4 = (CheckBox)findViewById(R.id.chkOPV4);
            final EditText OPV4DT = (EditText)findViewById(R.id.OPV4DT);
            final Spinner  spnOPV4 = (Spinner)findViewById(R.id.spnOPV4);
            spnOPV4.setAdapter(dataSource);

            final CheckBox chkMeasles = (CheckBox)findViewById(R.id.chkMeasles);
            final EditText MeaslesDT = (EditText)findViewById(R.id.MeaslesDT);
            final Spinner  spnMeasles = (Spinner)findViewById(R.id.spnMeasles);
            spnMeasles.setAdapter(dataSource);

            final CheckBox chkMR = (CheckBox)findViewById(R.id.chkMR);
            final EditText  MRDT = (EditText)findViewById(R.id.MRDT);
            final Spinner  spnMR = (Spinner)findViewById(R.id.spnMR);
            spnMR.setAdapter(dataSource);

            final CheckBox chkRota = (CheckBox)findViewById(R.id.chkRota);
            final EditText  RotaDT = (EditText)findViewById(R.id.RotaDT);
            final Spinner  spnRota = (Spinner)findViewById(R.id.spnRota);
            spnRota.setAdapter(dataSource);

            final CheckBox chkMMR = (CheckBox)findViewById(R.id.chkMMR);
            final EditText  MMRDT = (EditText)findViewById(R.id.MMRDT);
            final Spinner  spnMMR = (Spinner)findViewById(R.id.spnMMR);
            spnMMR.setAdapter(dataSource);

            final CheckBox chkTyphoid = (CheckBox)findViewById(R.id.chkTyphoid);
            final EditText  TyphoidDT = (EditText)findViewById(R.id.TyphoidDT);
            final Spinner  spnTyphoid = (Spinner)findViewById(R.id.spnTyphoid);
            spnTyphoid.setAdapter(dataSource);

            final CheckBox chkInflu = (CheckBox)findViewById(R.id.chkInflu);
            final EditText  InfluDT = (EditText)findViewById(R.id.InfluDT);
            final Spinner  spnInflu = (Spinner)findViewById(R.id.spnInflu);
            spnInflu.setAdapter(dataSource);

            final CheckBox chkHepaA = (CheckBox)findViewById(R.id.chkHepaA);
            final EditText  HepaADT = (EditText)findViewById(R.id.HepaADT);
            final Spinner  spnHepaA = (Spinner)findViewById(R.id.spnHepaA);
            spnHepaA.setAdapter(dataSource);

            final CheckBox chkChickenPox = (CheckBox)findViewById(R.id.chkChickenPox);
            final EditText  ChickenPoxDT = (EditText)findViewById(R.id.ChickenPoxDT);
            final Spinner  spnChickenPox = (Spinner)findViewById(R.id.spnChickenPox);
            spnChickenPox.setAdapter(dataSource);

            final CheckBox chkRabies = (CheckBox)findViewById(R.id.chkRabies);
            final EditText  RabiesDT = (EditText)findViewById(R.id.RabiesDT);
            final Spinner  spnRabies = (Spinner)findViewById(R.id.spnRabies);
            spnRabies.setAdapter(dataSource);

            final CheckBox chkIPV = (CheckBox)findViewById(R.id.chkIPV);
            final EditText IPVDT = (EditText)findViewById(R.id.IPVDT);
            final Spinner  spnIPV = (Spinner)findViewById(R.id.spnIPV);
            spnIPV.setAdapter(dataSource);

            //23 apr 2018
            final CheckBox chkfIPV1 = (CheckBox)findViewById(R.id.chkfIPV1);
            final EditText fIPVDT1 = (EditText)findViewById(R.id.fIPVDT1);
            final Spinner  spnfIPV1 = (Spinner)findViewById(R.id.spnfIPV1);
            spnfIPV1.setAdapter(dataSource);

            final CheckBox chkfIPV2 = (CheckBox)findViewById(R.id.chkfIPV2);
            final EditText fIPVDT2 = (EditText)findViewById(R.id.fIPVDT2);
            final Spinner  spnfIPV2 = (Spinner)findViewById(R.id.spnfIPV2);
            spnfIPV2.setAdapter(dataSource);


            final CheckBox chkVitaminA = (CheckBox)findViewById(R.id.chkVitaminA);
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
            chkPenta1.setChecked(false);
            chkPenta2.setChecked(false);
            chkPenta3.setChecked(false);
            chkPCV1.setChecked(false);
            chkPCV2.setChecked(false);
            chkPCV3.setChecked(false);
            chkOPV0.setChecked(false);
            chkOPV1.setChecked(false);
            chkOPV2.setChecked(false);
            chkOPV3.setChecked(false);
            chkOPV4.setChecked(false);
            chkMeasles.setChecked(false);
            chkMR.setChecked(false);
            chkRota.setChecked(false);
            chkMMR.setChecked(false);
            chkTyphoid.setChecked(false);
            chkInflu.setChecked(false);
            chkHepaA.setChecked(false);
            chkChickenPox.setChecked(false);
            chkRabies.setChecked(false);

            chkIPV.setChecked( false );

            chkfIPV1.setChecked( false );
            chkfIPV2.setChecked( false );

            chkVitaminA.setChecked( false );

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