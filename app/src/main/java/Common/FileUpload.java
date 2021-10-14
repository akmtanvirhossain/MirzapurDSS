package Common;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;


public class FileUpload extends AsyncTask<String, Integer, Void>
{
    @Override
    protected Void doInBackground(String... params)
    {
        try
        {
            //Upload File
            FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + params[0].toString());

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload("http://mchd.icddrb.org/dssjson/fileup.aspx", params[1].toString(),"description");

            hfu.Send_Now(fstrm);
        }

        catch(Exception ex)
        {

        }
        return null;
    }



    @Override
    protected void onCancelled() 
    {



        super.onCancelled();
    }

    @SuppressWarnings({ "static-access" })
    @Override
    protected void onPostExecute(Void result) 
    {

        //Make changes to UI from here
        super.onPostExecute(result);

    }

    @Override
    protected void onPreExecute() 
    {
        super.onPreExecute();
        //Do opertaions
    }

    @Override
    protected void onProgressUpdate(Integer... values) 
    {
        //Update if you have a progress bar
    }

}
