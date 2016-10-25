package data.mirzapurdss;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.app.ProgressDialog;
import android.os.*;


public class DownloadData extends AsyncTask<String, Void, String[]> {
		//Method Name
		public String Method_Name;
		public String SQLStr;
		
		
		public String WSDL_TARGET_NAMESPACE = Global.Namespace;
		public String SOAP_ACTION = Global.Namespace+Method_Name;
		public String SOAP_ADDRESS =Global.Soap_Address;
		ProgressDialog dialog;
		String[] Data=null;
		
		@Override
	    protected void onPreExecute() {			
	    }
		
	    @Override
	    protected String[] doInBackground(String... urls) {
	    	
	        try {
	                SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,Method_Name);
	                HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);

	                request.addProperty("SQL",SQLStr);
	                
	                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	                envelope.dotNet=true;
	                envelope.setOutputSoapObject(request);

	                androidHttpTransport.call(Global.Namespace+Method_Name, envelope);
	                SoapObject result = (SoapObject)envelope.getResponse();

	                
	                Data=new String[result.getPropertyCount()];
	                for(int i=0;i<result.getPropertyCount();i++)
	                {
	                    Data[i]=result.getProperty(i).toString();
	                }
	                return Data;
	            }
	             catch (Exception e) {
	                e.printStackTrace();
	            }
	        return Data;
	    }
	    
	    @Override
	    protected void onPostExecute(String[] result) {    	
	    	super.onPostExecute(result);	   
	    }
	    
	}