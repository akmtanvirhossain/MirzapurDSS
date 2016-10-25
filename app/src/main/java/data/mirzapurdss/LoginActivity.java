package data.mirzapurdss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class LoginActivity extends Activity{
    Connection C;
    Global g;
    boolean netwoekAvailable=false;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    private ProgressDialog dialog;
    int count=0;
    String SystemUpdateDT;
    TextView lblSystemDate;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	try
	{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login);
            C = new Connection(this);
            g = Global.getInstance();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            
            g.setClusterCode(C.ReturnSingleValue("Select Cluster from CurrentCluster"));

            lblSystemDate = (TextView)findViewById(R.id.lblSystemDate);
            
            //Need to update date every time whenever shared updated system
            //Format: DDMMYYYY
            //*********************************************************************
            SystemUpdateDT = "09102016";
            lblSystemDate.setText(Global.Left(SystemUpdateDT, 2)+" - "+SystemUpdateDT.substring(2,4)+" - "+Global.Right(SystemUpdateDT,4));
            //*********************************************************************
    
            final Spinner txtBlock = (Spinner)findViewById(R.id.blockNumber);
            txtBlock.setAdapter(C.getArrayAdapter("Select distinct block from baris order by cast(block as int)"));
            txtBlock.setSelection(Global.SpinnerItemPosition(txtBlock, 2, C.ReturnSingleValue("Select BlockNo from LastLogin")));


    	    //Check for Internet connectivity
            //*******************************************************************************
        	if (Connection.haveNetworkConnection(this)) {    		 
        		netwoekAvailable=true;
        		
        		//30 Jun 2015
        		//Downlaod SQL Command from server and execute on local device
        		C.ExecuteSQLQuery(g.getClusterCode());
        	 
        	} else {     	 
        		netwoekAvailable=false;
        	} 
    	
    	
    	
    	
        	//User List -----------------------------------------------------------------------
      		Spinner s = (Spinner) findViewById(R.id.userId);    
      		s.setAdapter(C.getArrayAdapter("select (UserId||', '||UserName)User from Login"));
      		
      		Button btnClose=(Button)findViewById(R.id.btnClose);
      		btnClose.setOnClickListener(new View.OnClickListener() {	       	 
	            public void onClick(View view) {
	            	finish();
	            	System.exit(0);
	            }
	        });
      		
      		
            //Login -----------------------------------------------------------------------
	        Button loginButton = (Button) findViewById(R.id.btnLogin);
	        final Spinner uid=(Spinner) findViewById(R.id.userId);
	        final EditText pass=(EditText) findViewById(R.id.pass);
	        
	        loginButton.setOnClickListener(new View.OnClickListener() {
	 
	            public void onClick(View view) {
	            		try
	            		{	  	            			
	            			g.setUserId(Global.Left(uid.getSelectedItem().toString(),2));
	            			
	                                //Store Last Login information
	                                C.Save("Delete from LastLogin");
	                                C.Save("Insert into LastLogin(BlockNo)Values('"+ Global.Left(txtBlock.getSelectedItem().toString(),2) +"')");
	                                g.setBlockCode(Global.Left(txtBlock.getSelectedItem().toString(), 2));

	            			//netwoekAvailable = false;
	            			
	            	    	//Download Updated System	            			
	            	        //...................................................................................
    	            		if(netwoekAvailable==true)
    	                	{
									//Retrieve data from server for checking local device
	    	            			String[] ServerVal  = Connection.split(C.ReturnResult("ReturnSingleValue","sp_ServerCheck '"+ g.getClusterCode() +"'"), ',');
	    	            			String SystemUpdate = ServerVal[0].toString(); //System Update Status
	    	            			String ServerDate   = ServerVal[1].toString(); //System Date
	    	            			String UpdateDT     = ServerVal[2].toString(); //Updated System Date
    	            			
    		            			//Check Internet connectivity & Update system
    	            				if (!UpdateDT.equals(SystemUpdateDT) & Connection.haveNetworkConnection(LoginActivity.this))
    		            			{    		 	            			
    			            			C.ExecuteCommandOnServer("Update SystemUpdate set UpdateNeeded='2' where Cluster = '"+ g.getClusterCode() +"' and UpdateNeeded='1'");
    	
    				            	    systemDownload d = new systemDownload();
    				            	    d.setContext(getApplicationContext());
    				            	    //d.execute("http://203.190.254.42/mobdata/Update/mzpdss.txt");
										//d.execute("http://203.190.254.42/dssjson/Update/mzpdss.txt");
										d.execute(Common.Global.UpdatedSystem);
    		            			}
    		            			else
    		            			{    		           		            				
    			            			if(ServerDate.equals(Global.TodaysDateforCheck())==false)
    			            			{
    				            			Connection.MessageBox(LoginActivity.this, "আপনার ট্যাব এর তারিখ সঠিক নয় ["+ g.CurrentDateDDMMYYYY +"]।");
    				            			startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
    				            			return;		            				
    			            			}    		            				
    		            				
    		            				//Call main Menu Form	            			
    			            	        //...................................................................................	 
    		            				finish();    		            				
    			            	    	Intent f11 = new Intent(getApplicationContext(),BlockList.class);
    			            	    	startActivity(f11);

    		            			}
    	            			    	            			
    	                	}
    	            		else
    	            		{
    	            			//Call main Menu Form	            			
    	            	        //...................................................................................	 
		            	    	finish();  		            				
		            	    	Intent f11 = new Intent(getApplicationContext(),BlockList.class);
		            	    	startActivity(f11);
    	            		}	            	    	
	            		}
	            		catch(Exception ex)
	            		{
	            			Connection.MessageBox(LoginActivity.this, ex.getMessage());
	            			return;
	            		}
	            }
	        });
		}
		catch(Exception ex)
		{
			Connection.MessageBox(LoginActivity.this, ex.getMessage());
			return;
		}
    }
	

   private void InstallApplication()
   {
	       //Install application
	       //----------------------------------------------------------------------------------------------------------
	       String PackageName = "data.mirzapurdss".toString();
	       Uri packageURI = Uri.parse(PackageName.toString());
	       
	       File apkfile = new File(Environment.getExternalStorageDirectory() + File.separator + "DSS_Update.apk");
	       Intent intent = new Intent(Intent.ACTION_VIEW, packageURI);        
	       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
	       intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
	       //intent.setDataAndType(Uri.fromFile(new File(file.getAbsolutePath()+ File.separator + "DSS_Update.apk")), "application/vnd.android.package-archive");
	       
	       startActivity(intent);	
       
		   //Intent intent = new Intent(this, ServiceInstallApplication.class);
		   //startService(intent);
   }
	
   
   
   //Downloading updated system from the central server 
   class systemDownload extends AsyncTask<String,String,Void>{
		private Context context;

		public void setContext(Context contextf){
		    context = contextf;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setMessage("Downloading Updated System...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			dialog.show();			
		}

		
		protected void onProgressUpdate(String... progress) {
			dialog.setProgress(Integer.parseInt(progress[0]));
			publishProgress(progress);
			
		}

		//@Override
		protected void onPostExecute(String unused) {
			dialog.dismiss();
		}
		
		
		@Override
		protected Void doInBackground(String... arg0) {
		      try {
		            URL url = new URL(arg0[0]);
		            HttpURLConnection c = (HttpURLConnection) url.openConnection();
		            c.setRequestMethod("GET");
		            c.connect();
		            int lenghtOfFile = c.getContentLength();
		            
		            File file=Environment.getExternalStorageDirectory();
		            
		            file.mkdirs();
		            File outputFile = new File(file.getAbsolutePath()+ File.separator + "DSS_Update.apk");
		            
		            if(outputFile.exists()){
		                outputFile.delete();
		            }
		            else
		            {
		            	outputFile.createNewFile();
		            }
		            
		            FileOutputStream fos = new FileOutputStream(outputFile);

		            InputStream is = c.getInputStream();
		            
		            
		            byte[] buffer = new byte[1024];
		            int len1 = 0;
		            long total = 0;
		            while ((len1 = is.read(buffer)) != -1) {
		                fos.write(buffer, 0, len1);
		                count++;
		            }
		            fos.close();
		            is.close();		            
		            
		            
		            //Permission for Unknown Sources
		            //----------------------------------------------------------------------------------------------------------
		            /*boolean success;
		            int result = Settings.Secure.getInt(getContentResolver(),
		            Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		            if (result == 0) {
		                success = Settings.Secure.putString(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, "1");    
		            }*/
		            
		            //Install application
		            //----------------------------------------------------------------------------------------------------------
		            /*
		            String PackageName = "data.mirzapurdss".toString();
		            Uri packageURI = Uri.parse(PackageName.toString());
		            //Install application
		            Intent intent = new Intent(Intent.ACTION_VIEW, packageURI);
		            intent.setDataAndType(Uri.fromFile(new File(file.getAbsolutePath()+ File.separator + "DSS_Update.apk")), "application/vnd.android.package-archive");
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
		            context.startActivity(intent);
		            */
		            
		            //Install application from service
		            //----------------------------------------------------------------------------------------------------------
            	    InstallApplication();
            	    
		            dialog.dismiss();
		            
		        } catch (IOException e) {
		            //Log.e("UpdateAPP", "Update error! " + e.getMessage());
		        }
		    return null;
	}



}


}
