package data.mirzapurdss;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.ViewFlipper;

public class Events extends Activity {
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
        setContentView(R.layout.eventpopup);

        Button close = (Button)findViewById(R.id.close);
    	close.setOnClickListener(new View.OnClickListener() {
    	    public void onClick(View arg0) {
    	    	finish();
    	    }
    	});
    	
        /*
        IDbundle  = getIntent().getExtras();
        CallinForm  = IDbundle.getString("callinform");
        dcode = IDbundle.getString("dcode");
        upcode= IDbundle.getString("upcode");
        vcode = IDbundle.getString("vcode");
        bari  = IDbundle.getString("bari");
        hh    = IDbundle.getString("hh");
        wid   = IDbundle.getString("wid");
        pgn   = IDbundle.getString("pgn");
        wname   = IDbundle.getString("wname");
        husband   = IDbundle.getString("husband");
        form=IDbundle.getString("formname");

        */
        
    }
    
   
}