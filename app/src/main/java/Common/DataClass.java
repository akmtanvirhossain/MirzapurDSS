package Common;

import java.util.List;

/**
 * Created by TanvirHossain on 28/11/2015.
 */
public class DataClass
{
    String tablename;
    public void settablename(String _tablename){this.tablename=_tablename;}
    public String gettablename(){return this.tablename;}
    String columnlist;
    public void setcolumnlist(String _columnlist){this.columnlist=_columnlist;}
    public String getcolumnlist(){return this.columnlist;}

    List<DataClassProperty> data;
    public void setdata(List<DataClassProperty> _data){this.data=_data;}
    public List<DataClassProperty> getdata(){return this.data;}
}