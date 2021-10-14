package data.mirzapurdss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.widget.Spinner;

public class Global {
	public static String Namespace       = "http://203.190.254.42/"; 	
	public static String Soap_Address    = "http://mchd.icddrb.org/dssjson/datasync.asmx";
	public static char VariableSeperator = '^';
		
        //Database
        public static String DatabaseFolder = "DSSDatabase";
        public static String DatabaseName   = "DSSDatabase.db";
	public static String zipDatabaseName= "MZPDSS_Database.zip";

	//Global Variables for Events
	//...........................................................................................................
	public static String CurrentRound;
	public static String ClusterCode;
	public static String BlockCode;
	public static String VillageCode;
	public static String BariCode;
	public static String HouseholdCode;
	public static String SNo;
	public static String PNo;
	
	//Setting
	//...........................................................................................................
	public static String settingDcode;
	public static String settingUPcode="";
	public static String settingAreaCode="";

	
	
	Global()
	{
		
	}

	//Declaring Global variables
	//-----------------------------------------------------------------------------
	private String _userId;
	public void setUserId(String userId){this._userId = userId;}
	public String getUserId(){ return this._userId;}

	private String _clusterCode;
	public void setClusterCode(String clusterCode){this._clusterCode = clusterCode;}
	public String getClusterCode(){ return this._clusterCode;}

	private String _blockCode;
	public void setBlockCode(String blockCode){this._blockCode = blockCode;}
	public String getBlockCode(){ return this._blockCode;}
		
	private String _roundNumber;
	public void setRoundNumber(String roundNumber){this._roundNumber = roundNumber;}
	public String getRoundNumber(){ return this._roundNumber;}
	
	private String _villageCode;
	public void setVillageCode(String villageCode){this._villageCode = villageCode;}
	public String getVillageCode(){ return this._villageCode;}
	
	private String _villageName;
	public void setVillageName(String villageName){this._villageName = villageName;}
	public String getVillageName(){ return this._villageName;}
	
	private String _bariCode;
	public void setBariCode(String bariCode){this._bariCode = bariCode;}
	public String getBariCode(){ return this._bariCode;}
	
	private String _hhCode;
	public void setHouseholdNo(String hhCode){this._hhCode = hhCode;}
	public String getHouseholdNo(){ return this._hhCode;}

	private String _rsno;
	public void setRsNo(String rsno){this._rsno = rsno;}
	public String getRsNo(){ return this._rsno;}

	private String _memSlNo;
	public void setmemSlNo(String memSlNo){this._memSlNo = memSlNo;}
	public String getmemSlNo(){ return this._memSlNo;}

	private String _pno;
	public void setPNo(String pno){this._pno = pno;}
	public String getPNo(){ return this._pno;}

	private String _evdate;
	public void setEvDate(String evdate){this._evdate = evdate;}
	public String getEvDate(){ return this._evdate;}
	
	private String _prevHousehold;
	public void setPrevHousehold(String prevHousehold){this._prevHousehold = prevHousehold;}
	public String getPrevHousehold(){ return this._prevHousehold;}

	public String HouseholdNo(){ return getVillageCode()+getBariCode()+getHouseholdNo();}
	
	
	private String _SQLforNewRegis;
	public void setSQLforNewRegis(String SQLforNewRegis){this._SQLforNewRegis = SQLforNewRegis;}
	public String getSQLforNewRegis(){ return this._SQLforNewRegis;}

	private String _posmig;
	public void setPosMig(String posmig){this._posmig = posmig;}
	public String getPosMig(){ return this._posmig;}

	private String _posmigdate;
	public void setPosMigDate(String posmigdate){this._posmigdate = posmigdate;}
	public String getPosMigDate(){ return this._posmigdate;}
	
	private String _migvillage;
	public void setMigVillage(String migvillage){this._migvillage = migvillage;}
	public String getMigVillage(){ return this._migvillage;}

        private String _PregOnDeath;
        public void setPregOnDeath(String PregOnDeath){this._PregOnDeath = PregOnDeath;}
        public String getPregOnDeath(){ return this._PregOnDeath;}
	
	private static Global instance;
	public static synchronized Global getInstance()
	  {
		   if(instance==null)
		   {
			   instance=new Global();
		   }
		   return instance;
	  }
	
	
	
	//String Function
	//...........................................................................................................
    public static String Right(String text, int length) {
  	  return text.substring(text.length() - length, text.length());
    }  	
    public static String Left(String text, int length){
          return text.substring(0, length);
    }
    public static String Mid(String text, int start, int end){
          return text.substring(start, end);
    }  
    public static String Mid(String text, int start){
          return text.substring(start, text.length() - start);
    }

    
    
    //Date/Time now
    //...........................................................................................................
    public static String DateNow()
    {
        Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        
        String M = Right("00"+String.valueOf(mMonth),2);
        String Y = String.valueOf(mYear);
        String D = Right("00"+String.valueOf(mDay),2);
    	    
        String CurrentDateYYYYMMDD = String.valueOf(Y)+"-"+String.valueOf(M)+"-"+String.valueOf(D);
    
    	return CurrentDateYYYYMMDD;
    }

    public static String DateNowDMY()
    {
        Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        
        String M = Right("00"+String.valueOf(mMonth),2);
        String Y = String.valueOf(mYear);
        String D = Right("00"+String.valueOf(mDay),2);
    	    
        String CurrentDateDDMMYYYY = String.valueOf(D)+"/"+String.valueOf(M)+"/"+String.valueOf(Y);
    
    	return CurrentDateDDMMYYYY;
    }
    
    public static String DateTimeNow()
    {
        Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        
        String M = Right("00"+String.valueOf(mMonth),2);
        String Y = String.valueOf(mYear);
        String D = Right("00"+String.valueOf(mDay),2);
    	    
        int second = c.get(Calendar.SECOND);
        int minute = c.get(Calendar.MINUTE);
        int hour   = c.get(Calendar.HOUR_OF_DAY);  //24 hour format

        String CurrentDateTimeYMD = String.valueOf(Y)+"-"+String.valueOf(M)+"-"+String.valueOf(D)+" "+ String.valueOf(hour)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);
    
    	return CurrentDateTimeYMD;
    }

    public static String DateTimeNowYMDHMS()
    {
        Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        
        String M = Right("00"+String.valueOf(mMonth),2);
        String Y = String.valueOf(mYear);
        String D = Right("00"+String.valueOf(mDay),2);
            
        String second = Right("00"+String.valueOf(c.get(Calendar.SECOND)),2);
        String minute = Right("00"+String.valueOf(c.get(Calendar.MINUTE)),2);
        String hour   = Right("00"+String.valueOf(c.get(Calendar.HOUR_OF_DAY)),2);  //24 hour format

        String CurrentDateTimeYMD = String.valueOf(Y)+"-"+String.valueOf(M)+"-"+String.valueOf(D)+" "+ String.valueOf(hour)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);
    
        return CurrentDateTimeYMD;
    }
    
	//Current date
	//...........................................................................................................
    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH)+1;
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    
    String M = Right("00"+String.valueOf(mMonth),2);
    String Y = String.valueOf(mYear);
    String D = Right("00"+String.valueOf(mDay),2);
	    
    //public String TodaysDateforCheck = String.valueOf(mYear)+""+String.valueOf(mMonth)+""+String.valueOf(mDay);
    public String CurrentDateMMDDYYYY = String.valueOf(M)+"-"+String.valueOf(D)+"-"+String.valueOf(Y);
    public String CurrentDateDDMMYYYY = String.valueOf(D)+"-"+String.valueOf(M)+"-"+String.valueOf(Y);
    public String CurrentDateYYYYMMDD = String.valueOf(Y)+"-"+String.valueOf(M)+"-"+String.valueOf(D);
    
    public String CurrentDateDMY = String.valueOf(D)+"/"+String.valueOf(M)+"/"+String.valueOf(Y);
    
    
    //System date check
    public static String TodaysDateforCheck()
    {
        Calendar c = Calendar.getInstance();
        int mYear  = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH)+1;
        int mDay   = c.get(Calendar.DAY_OF_MONTH);
        
        String CurrentDateDDMMYYYY = String.valueOf(mYear)+""+String.valueOf(mMonth)+""+String.valueOf(mDay);
    
    	return CurrentDateDDMMYYYY;
    }
    
    //Current Time
    //...........................................................................................................
    int millisecond = c.get(Calendar.MILLISECOND);
    int second      = c.get(Calendar.SECOND);
    int minute      = c.get(Calendar.MINUTE);
    int hour = c.get(Calendar.HOUR);   //12 hour format
    int hourofday = c.get(Calendar.HOUR_OF_DAY);  //24 hour format
    public String CurrentTime12 = String.valueOf(hour)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);
    public String CurrentTime24 = String.valueOf(hourofday)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);
       
    //Current DateTime
    //...........................................................................................................    
    public String CurrentDateTimeMDY = String.valueOf(M)+"-"+String.valueOf(D)+"-"+String.valueOf(Y)+" "+ String.valueOf(hour)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);
    public String CurrentDateTimeYMD = String.valueOf(D)+"-"+String.valueOf(M)+"-"+String.valueOf(D)+" "+ String.valueOf(hour)+":"+ String.valueOf(minute)+":"+ String.valueOf(second);

    
    //Time Now
    //...........................................................................................................    
    public String CurrentTime24()
    {
    	Calendar TM = Calendar.getInstance();
    	return String.valueOf(TM.get(Calendar.HOUR_OF_DAY))+":"+ String.valueOf(TM.get(Calendar.MINUTE))+":"+ String.valueOf(TM.get(Calendar.SECOND));
    }
    
    
    public static String DateConvertYMD(String DateString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.
        Date dateObject;
        String date="";
	    try{
		    dateObject = formatter.parse(DateString);
		    date = new SimpleDateFormat("yyyy-MM-dd").format(dateObject);		    
	    }
	
	    catch (java.text.ParseException e) 
	        {
	            e.printStackTrace();
	        }
	    return date;
    }
    
    public static String DateConvertDMY(String DateString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Make sure user insert date into edittext in this format.
        Date dateObject;
        String date="";
	    try{
		    dateObject = formatter.parse(DateString);
		    date = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);		    
	    }
	
	    catch (java.text.ParseException e) 
	        {
	            e.printStackTrace();
	        }
	    return date;
    }

    //Add days: format: YYYY-MM-DD
    public static String addDaysYYYYMMDD(String date, int days)
    {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return sdf.format(cal.getTime());
    }	

    //Add days: format: DD-MM-YYYY
	public static String addDaysDDMMYYYY(String date, int days)
    {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return sdf.format(cal.getTime());
    }
	
    //Date field validate
    //...........................................................................................................
	public static String DateValidate(String DateString)
	{
		String DT=DateString;
		String Message = "";
		
		if(DT.length()!=10)
		{
			Message = "তারিখ ১০ digit হতে হবে [dd/mm/yyyy].";
		}
		else if(!DT.substring(2, 3).equals("/") | !DT.substring(5, 6).equals("/"))
		{
			Message = "তারিখ সঠিক নয়.";
		}
		else
		{
			String D=DT.substring(0,2);
			String M=DT.substring(3,5);
			Calendar c = Calendar.getInstance();
			int currentYear = c.get(Calendar.YEAR);
			
			int Y= Integer.parseInt(DT.substring(6,10));
			
			
			//Date format check
			//-------------------------------------------------------------------------------------------
			if(Integer.parseInt(M)<1 | Integer.parseInt(M)>12)
			{
				Message = "মাসের সংখ্যা 01 - 12 হতে হবে.";
			}
			else if(Integer.parseInt(D)<1 | Integer.parseInt(D)>31)
			{
				Message = "দিনের সংখ্যা 01 - 31 হতে হবে.";
			}
			else if(Y > currentYear | Y < 1900)
			{
				Message = "বছরের সংখ্যা 1900 - "+ String.valueOf(currentYear)+" হতে হবে.";
			}
			
			// only 1,3,5,7,8,10,12 has 31 days
			else if (D.equals("31") && 
	        		 (M.equals("4") || M .equals("6") || M.equals("9") ||
	                  M.equals("11") || M.equals("04") || M .equals("06") ||
	                  M.equals("09"))) {
				Message = "তারিখ সঠিক নয়.";
	         } 
	         else if (M.equals("2") || M.equals("02")) {
	              //leap year
	             if(Y % 4==0){
	                  if(D.equals("30") || D.equals("31")){
	                	  Message = "তারিখ সঠিক নয়.";
	                  }
	                  else{
	                	  //valid=true;
	                  }
	             }
	             else{
	                 if(D.equals("29")||D.equals("30")||D.equals("31")){
	                	 Message = "তারিখ সঠিক নয়.";
	                 }
	                 else{
	                	 //valid=true;
	                 }
	             }
	         }
	
	         else{               
	        	 //valid=true;                
	         }		
			
			//Validation check
			//-------------------------------------------------------------------------------------------
	        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	        Date dateObject;
	        try {
	        	Global g=new Global();
				dateObject = formatter.parse(DateString);
	            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	            String formattedDate = sdf.format(c.getTime());
	            Date currentDate = sdf.parse(g.CurrentDateDMY);                               
	            Date DateValue = sdf.parse(DateString);  		
	            
	            if(DateValue.after(currentDate))
		          {            
	                int mYear = c.get(Calendar.YEAR);
	                int mMonth = c.get(Calendar.MONTH)+1;
	                int mDay = c.get(Calendar.DAY_OF_MONTH);
	                
	            	String MM   = Right("00"+String.valueOf(c.get(Calendar.MONTH)+1),2);
	                String YYYY = String.valueOf(c.get(Calendar.YEAR));
	                String DD   = Right("00"+String.valueOf(c.get(Calendar.DAY_OF_MONTH)),2);
	                
	            	Message = "তারিখ আজকের তারিখের["+ (String.valueOf(DD)+"/"+String.valueOf(MM)+"/"+String.valueOf(YYYY)) +"]  সমান অথবা কম হতে হবে।";
		          } 
			} catch (ParseException e) {				
				e.printStackTrace();
			}
	        
		}
		
		return Message;
				
	}
	
	//difference between two data
	//End date  : dd/mm/yyyy
	//Start date: dd/mm/yyyy
    //...........................................................................................................
	public static int DateDifference(String EndDateDDMMYYYY,String StartDateDDMMYYYY)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int age = 0;
		Date VD;
		Date BD;
		try {
			VD = sdf.parse(Global.DateConvertYMD(EndDateDDMMYYYY));
			BD = sdf.parse(Global.DateConvertYMD(StartDateDDMMYYYY));
			int diffInDays = (int) ((VD.getTime() - BD.getTime())/ (1000 * 60 * 60 * 24));
			age = (int)(diffInDays/365.25);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return age;
	}
     
	public static int DateDifferenceDays(String EndDateDDMMYYYY,String StartDateDDMMYYYY)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//int age = 0;
		int diffInDays=0;
		Date VD;
		Date BD;
		try {
			VD = sdf.parse(Global.DateConvertYMD(EndDateDDMMYYYY));
			BD = sdf.parse(Global.DateConvertYMD(StartDateDDMMYYYY));
			diffInDays = (int) ((VD.getTime() - BD.getTime())/ (1000 * 60 * 60 * 24));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return diffInDays;
	}
	
	//...........................................................................................................
	// GPS Coordinates
	//...........................................................................................................
	public static String decimalToDMS(double coord) {
        String output, degrees, minutes, seconds;
 
        // gets the modulus the coordinate divided by one (MOD1).
        // in other words gets all the numbers after the decimal point.
        // e.g. mod = 87.728056 % 1 == 0.728056
        //
        // next get the integer part of the coord. On other words the whole number part.
        // e.g. intPart = 87
 
        double mod = coord % 1;
        int intPart = (int)coord;
 
        //set degrees to the value of intPart
        //e.g. degrees = "87"
 
        degrees = String.valueOf(intPart);
 
        // next times the MOD1 of degrees by 60 so we can find the integer part for minutes.
        // get the MOD1 of the new coord to find the numbers after the decimal point.
        // e.g. coord = 0.728056 * 60 == 43.68336
        //      mod = 43.68336 % 1 == 0.68336
        //
        // next get the value of the integer part of the coord.
        // e.g. intPart = 43
 
        coord = mod * 60;
        mod = coord % 1;
        intPart = (int)coord;
        if (intPart < 0) {
           // Convert number to positive if it's negative.
           intPart *= -1;
        }
 
        // set minutes to the value of intPart.
        // e.g. minutes = "43"
        minutes = String.valueOf(intPart);
 
        //do the same again for minutes
        //e.g. coord = 0.68336 * 60 == 41.0016
        //e.g. intPart = 41
        coord = mod * 60;
        intPart = (int)coord;
        if (intPart < 0) {
           // Convert number to positive if it's negative.
           intPart *= -1;
        }
 
        // set seconds to the value of intPart.
        // e.g. seconds = "41"
        seconds = String.valueOf(intPart);
 
        // I used this format for android but you can change it 
        // to return in whatever format you like
        // e.g. output = "87/1,43/1,41/1"
        //output = degrees + "/1," + minutes + "/1," + seconds + "/1";
 
        //Standard output of D°M′S″
        //output = degrees + "°" + minutes + "'" + seconds + "\"";
        	output = degrees + "," + minutes + "," + seconds;
        
        return output;
		}
 
       /*
        * Conversion DMS to decimal 
        *
        * Input: latitude or longitude in the DMS format ( example: N 43° 36' 15.894")
        * Return: latitude or longitude in decimal format   
        * hemisphereOUmeridien => {W,E,S,N}
        *
        */
        public double DMSToDecimal(String hemisphereOUmeridien,double degres,double minutes,double secondes)
        {
                double LatOrLon=0;
                double signe=1.0;
 
                if((hemisphereOUmeridien.equals("W"))||(hemisphereOUmeridien.equals("S"))) {signe=-1.0;}                
                LatOrLon = signe*(Math.floor(degres) + Math.floor(minutes)/60.0 + secondes/3600.0);
 
                return(LatOrLon);               
        }
        
        //Getting spinner item position with code: 08 Jan 2014
		public static int SpinnerItemPosition(Spinner spn,int CodeLength, String Value)
		{
		        int pos = 0;
	                if(Value.length()!=0)
	                {
	                    for(int i=0;i<spn.getCount();i++)
	                    {
	                        if(spn.getItemAtPosition(i).toString().length()>=CodeLength)
	                        {
	                                if(Global.Left(spn.getItemAtPosition(i).toString(),CodeLength).equalsIgnoreCase(Value))
	                                {
	                                        pos = i;
	                                        i   = spn.getCount();
	                                }
	                        }
	                    }
	                }
	            return pos;
	        }          
       
}
