package Common;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class ExecuteCommand extends AsyncTask<String, Void, String> {
			//Method Name
			public String Method_Name="ExecuteCommand";
			
			public String WSDL_TARGET_NAMESPACE = Global.Namespace;
			public String SOAP_ACTION = Global.Namespace+Method_Name;
			public String SOAP_ADDRESS = Global.Soap_Address;
			ProgressDialog dialog;
			String Response=null;
			
			@Override
		    protected void onPreExecute() {
				
		    }
			
		    @Override
		    protected String doInBackground(String... urls) {
		    	
		        try {
		                SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,Method_Name);
		                HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_ADDRESS);

		                
		                request.addProperty("SQLStr",urls[0]);	                
		                
		                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		                envelope.dotNet=true;
		                envelope.setOutputSoapObject(request);
		                androidHttpTransport.call(Global.Namespace+Method_Name, envelope);
		                SoapObject result = (SoapObject)envelope.bodyIn;

		                Response=result.getProperty(0).toString();

		                return Response;
		            }
		             catch (Exception e) {
		                e.printStackTrace();
		            }
		        return Response;
		    }
		    
		    @Override
		    protected void onPostExecute(String result) {    	
		    	super.onPostExecute(result);	   
		    }
		    
}
