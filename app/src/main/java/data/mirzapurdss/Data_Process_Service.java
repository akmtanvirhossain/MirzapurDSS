package data.mirzapurdss;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import Common.Connection;
/*
 * Created by TanvirHossain on 08/03/2015.
 */
public class Data_Process_Service extends Service {
    public Data_Process_Service m_service;

    public class MyBinder extends Binder {
        public Data_Process_Service getService() {
            return Data_Process_Service.this;
        }
    }

    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            m_service = ((Data_Process_Service.MyBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            m_service = null;
        }
    };

    PowerManager c;
    Bundle IDbundle;
    Global g;
    Connection C;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        g=Global.getInstance();
        C=new Connection(this);
    }


    static String CLUSTER = "";

    private void handleIntent(Intent intent) {
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }

        CLUSTER = g.getClusterCode();

        // do the actual work, in a separate thread
        new DataSyncTask().execute(CLUSTER);
    }


    //@SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        handleIntent(intent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        //return START_NOT_STICKY;
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


    private class DataSyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            final String CLUSTERID = params[0].toString();
            try {
                new Thread() {
                    public void run() {
                        try {
                            C.HouseholdHead_Update();

                        } catch (Exception e) {

                        }
                    }
                }.start();

            } catch (Exception e) {

            }
            // do stuff!
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            stopSelf();
        }
    }
}