package data.mirzapurdss;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import Common.Connection;

public class data_HHList_DataModel {
    private String _bari = "";
    public String getBari(){
        return _bari;
    }
    private String _hhno = "";
    public String getHHNo(){
        return _hhno;
    }
    private String _hhhead = "";
    public String getHHHead(){
        return _hhhead;
    }
    private String _totalmem = "";
    public String getTotalMem(){
        return _totalmem;
    }
    private String _vcode = "";
    public String getVCode(){
        return _vcode;
    }
    private String _bariname = "";
    public String getBariName(){
        return _bariname;
    }
    private String _visit = "";
    public String getVisit(){
        return _visit;
    }
    private String _rel = "";
    public String getRel(){
        return _rel;
    }
    private String _rsno = "";
    public String getRSNO(){
        return _rsno;
    }
    private String _vdate = "";
    public String getVDate(){
        return _vdate;
    }
    private String _posmig = "";
    public String getPosMig(){
        return _posmig;
    }

    int Count = 0;          private int _Count = 0;          public int getCount(){ return _Count; }
    public List<data_HHList_DataModel> SelectAll(Context context, String SQL)
    {
        Common.Connection C = new Connection(context);
        List<data_HHList_DataModel> data = new ArrayList<data_HHList_DataModel>();
        data_HHList_DataModel d = new data_HHList_DataModel();
        Cursor cur = C.ReadData(SQL);

        cur.moveToFirst();
        while(!cur.isAfterLast())
        {
            Count += 1;
            d = new data_HHList_DataModel();
            d._Count = Count;
            d._bari = cur.getString(cur.getColumnIndex("bari"));
            d._hhno = cur.getString(cur.getColumnIndex("hh"));
            d._hhhead = cur.getString(cur.getColumnIndex("hhhead"));
            d._totalmem = cur.getString(cur.getColumnIndex("totalmem"));
            d._vcode = cur.getString(cur.getColumnIndex("vill"));
            d._bariname = cur.getString(cur.getColumnIndex("bariname"));
            d._visit = cur.getString(cur.getColumnIndex("roundvisit"));
            d._rel = cur.getString(cur.getColumnIndex("rel"));
            d._rsno = cur.getString(cur.getColumnIndex("rsno"));
            d._vdate = cur.getString(cur.getColumnIndex("vdate"));
            d._posmig = cur.getString(cur.getColumnIndex("posmig"));
            data.add(d);

            cur.moveToNext();
        }
        cur.close();
        return data;
    }
}
