package Common;

/*
 * Created by TanvirHossain on 19/07/2016.
 */
public class ProjectSetting {
    public static String ProjectName    = "COVID19_Mortality";
    public static String Namespace      = "http://chu.icddrb.org/";
    public static String ServerURL      = "http://mchd.icddrb.org/";

    public static String apiName        = ProjectName.toLowerCase();
    //public static String NewVersionName = ProjectName.toLowerCase() +"_update";
    //public static String DatabaseFolder = ProjectName.toUpperCase() +"DB";
    //public static String DatabaseName   = ProjectName.toUpperCase() +"Database.db";
    //public static String zipDatabaseName= ProjectName.toUpperCase() +"Database.zip";
    public static String DBSecurityPass = "a";
    public static String Organization   = "ICDDR,B";
    //public static String Soap_Address    = ProjectSetting.ServerURL + "/"+ ProjectSetting.apiName +"/datasync.asmx";
    //public static String UpdatedSystem   = ProjectSetting.ServerURL + "/"+ ProjectSetting.apiName +"/Update/"+ ProjectSetting.NewVersionName +".txt";

    public static String VersionDate    = "26022025"; //Format: DDMMYYYY
}
