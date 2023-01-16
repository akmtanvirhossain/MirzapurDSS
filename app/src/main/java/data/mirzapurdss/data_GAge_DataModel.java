package data.mirzapurdss;

import android.content.Context;
 import android.database.Cursor;
 import Common.Connection;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Date;
 import Common.Global;
 import android.content.ContentValues;
 public class data_GAge_DataModel{

        private String _Vill = "";
        public String getVill(){
              return _Vill;
         }
        public void setVill(String newValue){
              _Vill = newValue;
         }
        private String _Bari = "";
        public String getBari(){
              return _Bari;
         }
        public void setBari(String newValue){
              _Bari = newValue;
         }
        private String _HH = "";
        public String getHH(){
              return _HH;
         }
        public void setHH(String newValue){
              _HH = newValue;
         }
        private String _SNo = "";
        public String getSNo(){
              return _SNo;
         }
        public void setSNo(String newValue){
              _SNo = newValue;
         }
        private String _PNo = "";
        public String getPNo(){
              return _PNo;
         }
        public void setPNo(String newValue){
              _PNo = newValue;
         }
        private String _GAge = "";
        public String getGAge(){
              return _GAge;
         }
        public void setGAge(String newValue){
              _GAge = newValue;
         }
        private String _StartTime = "";
        public void setStartTime(String newValue){
              _StartTime = newValue;
         }
        private String _EndTime = "";
        public void setEndTime(String newValue){
              _EndTime = newValue;
         }
        private String _DeviceID = "";
        public void setDeviceID(String newValue){
              _DeviceID = newValue;
         }
        private String _EntryUser = "";
        public void setEntryUser(String newValue){
              _EntryUser = newValue;
         }
        private String _Lat = "";
        public void setLat(String newValue){
              _Lat = newValue;
         }
        private String _Lon = "";
        public void setLon(String newValue){
              _Lon = newValue;
         }
        private String _EnDt = Global.DateTimeNowYMDHMS();
        private int _Upload = 2;
        private String _modifyDate = Global.DateTimeNowYMDHMS();

        String TableName = "data_GAge";

        public String SaveUpdateData(Context context)
        {
            String response = "";
            C = new Connection(context);
            String SQL = "";
            try
            {
                 if(C.Existence("Select * from "+ TableName +"  Where Vill='"+ _Vill +"' and Bari='"+ _Bari +"' and HH='"+ _HH +"' and SNo='"+ _SNo +"' "))
                    response = UpdateData(context);
                 else
                    response = SaveData(context);
            }
            catch(Exception  e)
            {
                 response = e.getMessage();
            }
           return response;
        }
        Connection C;

        private String SaveData(Context context)
        {
            String response = "";
            C = new Connection(context);
            try
              {
                 ContentValues contentValues = new ContentValues();
                 contentValues.put("Vill", _Vill);
                 contentValues.put("Bari", _Bari);
                 contentValues.put("HH", _HH);
                 contentValues.put("SNo", _SNo);
                 contentValues.put("PNo", _PNo);
                 contentValues.put("GAge", _GAge);
                 contentValues.put("StartTime", _StartTime);
                 contentValues.put("EndTime", _EndTime);
                 contentValues.put("DeviceID", _DeviceID);
                 contentValues.put("EntryUser", _EntryUser);
                 contentValues.put("Lat", _Lat);
                 contentValues.put("Lon", _Lon);
                 contentValues.put("EnDt", _EnDt);
                 contentValues.put("Upload", _Upload);
                 contentValues.put("modifyDate", _modifyDate);
                 C.InsertData(TableName, contentValues);
              }
              catch(Exception  e)
              {
                 response = e.getMessage();
              }
           return response;
        }

        private String UpdateData(Context context)
        {
            String response = "";
            C = new Connection(context);
            try
              {
                 ContentValues contentValues = new ContentValues();
                 contentValues.put("Vill", _Vill);
                 contentValues.put("Bari", _Bari);
                 contentValues.put("HH", _HH);
                 contentValues.put("SNo", _SNo);
                 contentValues.put("PNo", _PNo);
                 contentValues.put("GAge", _GAge);
                 contentValues.put("Upload", _Upload);
                 contentValues.put("modifyDate", _modifyDate);
                 C.UpdateData(TableName, "Vill,Bari,HH,SNo", (""+ _Vill +", "+ _Bari +", "+ _HH +", "+ _SNo +""), contentValues);
              }
              catch(Exception  e)
              {
                 response = e.getMessage();
              }
           return response;
        }

          int Count = 0;          private int _Count = 0;          public int getCount(){ return _Count; }
        public List<data_GAge_DataModel> SelectAll(Context context, String SQL)
        {
            Connection C = new Connection(context);
            List<data_GAge_DataModel> data = new ArrayList<data_GAge_DataModel>();
            data_GAge_DataModel d = new data_GAge_DataModel();
            Cursor cur = C.ReadData(SQL);

            cur.moveToFirst();
            while(!cur.isAfterLast())
            {
                Count += 1;
                d = new data_GAge_DataModel();
                d._Count = Count;
                d._Vill = cur.getString(cur.getColumnIndex("Vill"));
                d._Bari = cur.getString(cur.getColumnIndex("Bari"));
                d._HH = cur.getString(cur.getColumnIndex("HH"));
                d._SNo = cur.getString(cur.getColumnIndex("SNo"));
                d._PNo = cur.getString(cur.getColumnIndex("PNo"));
                d._GAge = cur.getString(cur.getColumnIndex("GAge"));
                data.add(d);

                cur.moveToNext();
            }
            cur.close();
          return data;
        }
 }