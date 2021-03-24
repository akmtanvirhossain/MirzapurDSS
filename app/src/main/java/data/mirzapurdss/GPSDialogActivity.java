package data.mirzapurdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import Common.Connection;

public class GPSDialogActivity extends AppCompatActivity implements LocationListener {
    Location currentLocation;
    double currentLatitude, currentLongitude;

    EditText editTextID, editTextLat, editTextLong, editTextLandmarkName, editTextEstablished,
            editTextContact, editTextNote, editTextAccuracy, editTextSatCount;

    String mUserID = "", mLat = "0", mLong = "0", mLandmarkName = "", mEstablished = "", mContact = "",
            mNote = "", mAccuracy = "0", mSatCount = "0";
    String Type = "";
    String Landmark = "";
    String TimeEnd = "";

    RadioGroup radioGroupLocType;
    RadioButton radioButtonBari, radioButtonLandmark;
    Spinner spinnerLandmarkList;
    Button btnSave;
    LocationManager locationManager;
    Connection C;
    Global g;

    private static String IDNumber;

    public static void ID(String _ID) {
        IDNumber = _ID;
    }

    private static String GPSType;

    public static void Type(String _GPSType) {
        GPSType = _GPSType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        C = new Connection(this);
        g = Global.getInstance();

        turnGPSOn();
        FindLocation();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        setContentView(R.layout.activity_gpsdialog);

        editTextID = (EditText) findViewById(R.id.editTextUserID);
        editTextID.setText(IDNumber);
        editTextLat = (EditText) findViewById(R.id.editTextLatitude);
        editTextLong = (EditText) findViewById(R.id.editTextLongitude);
        editTextLandmarkName = (EditText) findViewById(R.id.editTextLandName);
        editTextEstablished = (EditText) findViewById(R.id.editTextLandEstablised);
        editTextContact = (EditText) findViewById(R.id.editTextContactNo);
        editTextNote = (EditText) findViewById(R.id.editTextNote);
        editTextAccuracy = (EditText) findViewById(R.id.editTextAccuracy);
        editTextSatCount = (EditText) findViewById(R.id.editTextSatelite);

        radioGroupLocType = (RadioGroup) findViewById(R.id.radioGroupLocType);
        radioButtonBari = (RadioButton) findViewById(R.id.radioButtonBari);
        radioButtonLandmark = (RadioButton) findViewById(R.id.radioButtonLandmark);

        spinnerLandmarkList = (Spinner) findViewById(R.id.spinnerLandType);
        spinnerLandmarkList.setSelection(1);

        final LinearLayout secLand1 = (LinearLayout)findViewById(R.id.secLand1);
        final LinearLayout secLand2 = (LinearLayout)findViewById(R.id.secLand2);
        final LinearLayout secLand3 = (LinearLayout)findViewById(R.id.secLand3);
        final LinearLayout secLand4 = (LinearLayout)findViewById(R.id.secLand4);
        final LinearLayout secLand5 = (LinearLayout)findViewById(R.id.secLand5);

        radioGroupLocType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioButtonBari.isChecked()) {
                    secLand1.setVisibility(View.GONE);
                    secLand2.setVisibility(View.GONE);
                    secLand3.setVisibility(View.GONE);
                    secLand4.setVisibility(View.GONE);
                    secLand5.setVisibility(View.GONE);
                    spinnerLandmarkList.setSelection(0);
                    editTextLandmarkName.setText("");
                    editTextEstablished.setText("");
                    editTextContact.setText("");
                    editTextNote.setText("");
                } else {
                    secLand1.setVisibility(View.VISIBLE);
                    secLand2.setVisibility(View.VISIBLE);
                    secLand3.setVisibility(View.VISIBLE);
                    secLand4.setVisibility(View.VISIBLE);
                    secLand5.setVisibility(View.VISIBLE);
                }
            }
        });

        radioButtonBari.setChecked(true);
        secLand1.setVisibility(View.GONE);
        secLand2.setVisibility(View.GONE);
        secLand3.setVisibility(View.GONE);
        secLand4.setVisibility(View.GONE);
        secLand5.setVisibility(View.GONE);
        spinnerLandmarkList.setSelection(0);
        editTextLandmarkName.setText("");
        editTextEstablished.setText("");
        editTextContact.setText("");
        editTextNote.setText("");

        if(GPSType.equals("B")) {
            radioButtonBari.setChecked(true);
            radioButtonLandmark.setVisibility(View.INVISIBLE);
            spinnerLandmarkList.setSelection(0);
            editTextLandmarkName.setText("");
            editTextEstablished.setText("");
            editTextContact.setText("");
            editTextNote.setText("");
        }
        else if(GPSType.equals("L")) {
            radioButtonLandmark.setChecked(true);
            radioButtonBari.setVisibility(View.GONE);
            spinnerLandmarkList.setSelection(0);
            editTextLandmarkName.setText("");
            editTextEstablished.setText("");
            editTextContact.setText("");
            editTextNote.setText("");
        }

        btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGPSFormData(IDNumber,g.CurrentTime24());
            }
        });

        Button buttonClose = (Button) findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void saveGPSFormData(final String IDNO,final String TimeStart) {
        mUserID = editTextID.getText().toString();
        mLat = editTextLat.getText().toString();
        mLong = editTextLong.getText().toString();

        if(radioButtonBari.isChecked()){
            Type="1";
        }
        else if(radioButtonLandmark.isChecked()){
            Type="2";
            Landmark = Global.Left(spinnerLandmarkList.getSelectedItem().toString(),2);
            mLandmarkName = editTextLandmarkName.getText().toString();
            mEstablished = editTextEstablished.getText().toString();
            mContact = editTextContact.getText().toString();
            mNote = editTextNote.getText().toString();
        }
        mAccuracy = editTextAccuracy.getText().toString();
        mSatCount = editTextSatCount.getText().toString();

        TimeEnd = g.CurrentTime24();
        String SQL = "";
        if(!C.Existence("Select IDNO from GPSData where IDNO='"+ IDNO +"'")) {
            SQL = "Insert into GPSData(IDNO, Lat, Lon, Type, Landmark, Landmark_Name, Established, ContactNo, Note, Accuracy, TimeStart, TimeEnd, Sat_Connected, EnDt, UserId, Upload)Values(";
            SQL += "'" + IDNO + "',";
            SQL += "'" + mLat + "',";
            SQL += "'" + mLong + "',";
            SQL += "'" + Type + "',";
            SQL += "'" + Landmark + "',";
            SQL += "'" + mLandmarkName + "',";
            SQL += "'" + mEstablished + "',";
            SQL += "'" + mContact + "',";
            SQL += "'" + mNote + "',";
            SQL += "'" + mAccuracy + "',";
            SQL += "'" + TimeStart + "',";
            SQL += "'" + TimeEnd + "',";
            SQL += "'" + mSatCount + "',";
            SQL += "'" + Global.DateTimeNowYMDHMS() + "',";
            SQL += "'','2')";
            C.Save(SQL);

            Connection.MessageBox(GPSDialogActivity.this,"GPS Data Saved Successfully.");
            finish();
        }
        else
        {
            //Confirm message
            AlertDialog.Builder builder = new AlertDialog.Builder(GPSDialogActivity.this);
            builder
                    .setTitle("Message")
                    .setMessage("আপনি কি জিপিএস এর তথ্য আপডেট করতে চান[হ্যাঁ/না]?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String SQLUpdate = "";
                                    SQLUpdate = "Update GPSData Set Upload='2',";
                                    SQLUpdate += "Lat='" + mLat + "',";
                                    SQLUpdate += "Lon='" + mLong + "',";
                                    SQLUpdate += "Type='" + Type + "',";
                                    SQLUpdate += "Landmark='" + Landmark + "',";
                                    SQLUpdate += "Landmark_Name='" + mLandmarkName + "',";
                                    SQLUpdate += "Established='" + mEstablished + "',";
                                    SQLUpdate += "ContactNo='" + mContact + "',";
                                    SQLUpdate += "Note='" + mNote + "',";
                                    SQLUpdate += "Accuracy='" + mAccuracy + "',";
                                    SQLUpdate += "TimeStart='" + TimeStart + "',";
                                    SQLUpdate += "TimeEnd='" + TimeEnd + "',";
                                    SQLUpdate += "Sat_Connected='" + mSatCount + "',";
                                    SQLUpdate += "EnDt='" + Global.DateTimeNowYMDHMS() + "'";
                                    SQLUpdate += " Where IDNO='"+ IDNO +"'";
                                    C.Save(SQLUpdate);

                                    Connection.MessageBox(GPSDialogActivity.this,"GPS Data Saved Successfully.");
                                    finish();

                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    })
                    .setNegativeButton("No", null)	//Do nothing on no
                    .show();
        }


    }

    //GPS Reading
    //............................................................................................
    public void FindLocation() {

/*        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

/*        LocationListener locationListener = new LocationListener() {
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
        };*/
      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    void updateLocation(Location location) {
      //  int nsat=location.getExtras().getInt("satellites", -1);
        mSatCount = String.valueOf(location.getExtras().getInt("satellites", -1));
        currentLocation = location;
        currentLatitude = currentLocation.getLatitude();
        currentLongitude = currentLocation.getLongitude();
        mAccuracy = String.valueOf(currentLocation.getAccuracy());
        editTextLat.setText(String.valueOf(currentLatitude));
        editTextLong.setText(String.valueOf(currentLongitude));
        editTextAccuracy.setText(String.valueOf(mAccuracy));
        editTextSatCount.setText(String.valueOf(mSatCount));
    }

    // Method to turn on GPS
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

    // Method to turn off the GPS
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

    // turning off the GPS if its in on state. to avoid the battery drain.
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        turnGPSOff();
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

