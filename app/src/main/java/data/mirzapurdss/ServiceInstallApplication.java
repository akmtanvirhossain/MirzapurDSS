package data.mirzapurdss;

import java.io.File;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.app.Service;

public class ServiceInstallApplication  extends Service{

	@Override
    public void onCreate() {
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
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	//InstallAPK(Environment.getExternalStorageDirectory() + File.separator + "DSS_Update.apk");
	public static void InstallAPK(String filename){
	    File file = new File(filename); 
	    if(file.exists()){
	        try {   
	            String command;
	            //filename = StringUtils.insertEscape(filename);
	            command = "adb install -r " + filename;
	            Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
	            proc.waitFor();
	        } catch (Exception e) {
	        e.printStackTrace();
	        }
	     }
	  }
}