package data.mirzapurdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MemberEvents extends Activity{
	SimpleAdapter mSchedule;
	SimpleAdapter eList;
	String VariableID;
	private int hour;
	private int minute;
	private int mDay;
	private int mMonth;
	private int mYear;
	static final int DATE_DIALOG = 1;
	static final int TIME_DIALOG = 2;

	ArrayList<HashMap<String, String>> mylist   = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> evmylist = new ArrayList<HashMap<String, String>>();
	Connection C;
	Global g;
	Bundle IDbundle;
	private static String vill;
	private static String bari;
	private static String hhno;
	private static String hhhead;
	private static String totalmember;
	private String ErrMsg;
	private static String vdate;

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.processtran, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
		switch (item.getItemId()) {
			case R.id.canceltran:
				adb.setTitle("Cancel Transaction");
				adb.setMessage("Do you want to cancel this transaction[Yes/No]?");
				adb.setNegativeButton("No", null);
				adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						C.Save("Delete from tTrans where Vill||Bari||HH='"+ (vill+bari+hhno) +"'");
						g.setBariCode("");
						g.setHouseholdNo("");

						finish();
					}});
				adb.show();

				return true;
			case R.id.process:
				adb.setTitle("Process Transaction");
				adb.setMessage("Do you want to process current transaction[Yes/No]?");
				adb.setNegativeButton("No", null);
				adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try
						{
							String msg = ProcessTransaction(vill+bari+hhno,g.getRoundNumber());
							if(msg.length()==0)
							{
								String totalmem = C.ReturnSingleValue("Select count(*)totalmem from Member where Vill||Bari||HH='"+ (vill+bari+hhno) +"' and length(ExType)=0");
								String posmig   = C.ReturnSingleValue("Select count(*)posmig from Member where Vill||Bari||HH='"+ (vill+bari+hhno) +"' and length(ExType)=0 and posmig='54'");
								C.Save("Update Household set totalmem='"+ (totalmem==null?'0':totalmem) +"',posmig='"+ (Integer.valueOf(posmig)>=1?"1":"2") +"',Upload='2' where Vill||bari||HH='"+ (vill+bari+hhno) +"'");

								g.setBariCode("");
								g.setHouseholdNo("");

								finish();
							}
							else
							{
								Connection.MessageBox(MemberEvents.this, msg);
								return;
							}
						}
						catch(Exception ex)
						{
							Connection.MessageBox(MemberEvents.this, ex.getMessage());
							return;
						}

					}});
				adb.show();

				return true;
		}
		return false;
	}

	//Disabled Back/Home key
	//--------------------------------------------------------------------------------------------------	
	@Override
	public boolean onKeyDown(int iKeyCode, KeyEvent event)
	{
		if(iKeyCode == KeyEvent.KEYCODE_BACK || iKeyCode == KeyEvent.KEYCODE_HOME)
		{ return false; }
		else { return true;  }
	}

	private static int TAKE_PICTURE = 1;
	private Uri outputFileUri;

	ImageView  mImageView1;
	ImageView  mImageView2;
	ImageView  mImageView3;
	ImageView  mImageView4;
	ImageView  mImageView5;

	String STR_POP;
	String STR_EDU;

	EditText EvDate;
	EditText txtLmpDt;
	EditText txtREvDT;
	EditText txtBDate;

	ImageButton btnEvDate;
	ImageButton btnLmpDt;
	ImageButton btnREvDT;

	//event update form
	ImageButton btnEvDate_Update;
	LinearLayout secDelivType;
	Spinner spnDelType;

	//***************************************************************************************************************************
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memberevents);
		C = new Connection(this);
		g = Global.getInstance();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();

		IDbundle	= getIntent().getExtras();
		vill        = IDbundle.getString("vill");
		bari        = IDbundle.getString("bari");
		hhno        = IDbundle.getString("hhno");
		hhhead      = IDbundle.getString("hhhead");
		totalmember = IDbundle.getString("totalmember");
		vdate       = IDbundle.getString("vdate");

		TextView lblHousehold=(TextView)findViewById(R.id.lblHousehold);
		TextView lblVillName=(TextView)findViewById(R.id.lblVillName);
		TextView lblBariName=(TextView)findViewById(R.id.lblBariName);
		lblHousehold.setText(": "+vill+"-"+bari+"-"+hhno);
		lblVillName.setText(": "+IDbundle.getString("villname"));
		lblBariName.setText(": "+bari+", "+IDbundle.getString("bariname"));


		STR_POP  = "Select '  '";
		STR_POP += " Union Select '01-নিজের বাড়ী/বাসা'";
		STR_POP += " Union Select '02-মির্জাপুরের ভিতরে বাবা/মা এর বাড়ী'";
		STR_POP += " Union Select '10-মির্জাপুরের ভিতরে অন্য কোন বাড়ী'";
		STR_POP += " Union Select '05-কুমুদীনি হাসপাতাল'";
		STR_POP += " Union Select '06-মির্জাপুরের ভিতরে অন্যান্য অলাভজনক এন.জি.ও/ক্লিনিক'";
		STR_POP += " Union Select '07-মির্জাপুরের ভিতরে বেসরকারী ক্লিনিক/হাসপাতাল (লাভজনক)'";
		STR_POP += " Union Select '11-মির্জাপুর উপজেলা স্বাস্থ্য কেন্দ্র (জামুর্কি)'";
		STR_POP += " Union Select '12-মির্জাপুরের ভিতরে সরকারী হাসপাতাল/FWC'";
		STR_POP += " Union Select '15-কমিউনিটি ক্লিনিক'";
		STR_POP += " Union Select '03-মির্জাপুরের বাহিরে বাবা/মা এর বাড়ী'";
		STR_POP += " Union Select '04-মির্জাপুরের বাহিরে অন্য কোন বাড়ী'";
		STR_POP += " Union Select '17-মির্জাপুরের বাহিরে অন্যান্য অলাভজনক এন.জি.ও/ক্লিনিক/সরকারী মাতৃসদন'";
		//STR_POP += " Union Select '08-মির্জপুরের বাইরে অন্য যে কোন হাসপাতাল/ক্লিনিক'";
		STR_POP += " Union Select '13-মির্জাপুরের বাহিরে অন্যান্য উপজেলা স্বাস্থ্যকেন্দ্র'";
		STR_POP += " Union Select '14-মির্জাপুরের বাহিরে সরকারী হাসপাতাল/FWC'";
		STR_POP += " Union Select '16-মির্জাপুরের বাহিরে বেসরকারী ক্লিনিক/হাসপাতাল(লাভজনক)'";
		STR_POP += " Union Select '09-অন্য কোন জায়গা'";
		STR_POP += " Union Select '99-জানিনা'";



		STR_EDU  = " Select '  '";
		STR_EDU += " Union Select '00-কোন শিক্ষাগত যোগ্যতা নাই'";
		STR_EDU += " Union Select '01-প্রথম শ্রেণী পাশ'";
		STR_EDU += " Union Select '02-২য় শ্রেণী পাশ'";
		STR_EDU += " Union Select '03-৩য়  শ্রেণী পাশ'";
		STR_EDU += " Union Select '04-চতুর্থ  শ্রেণী পাশ'";
		STR_EDU += " Union Select '05-পঞ্চম  শ্রেণী পাশ'";
		STR_EDU += " Union Select '06-ষষ্ট  শ্রেণী পাশ'";
		STR_EDU += " Union Select '07-সপ্তম  শ্রেণী পাশ'";
		STR_EDU += " Union Select '08-অষ্টম  শ্রেণী পাশ'";
		STR_EDU += " Union Select '09-নবম  শ্রেণী পাশ'";
		STR_EDU += " Union Select '10-S.S.C শ্রেণী পাশ'";
		STR_EDU += " Union Select '12-H.S.C শ্রেণী পাশ'";
		STR_EDU += " Union Select '14-B.A/B.Com/B.Sc/B.S.S পাশ'";
		STR_EDU += " Union Select '15-ডিপ্লোমা পাশ (৪ বছরের কোর্স):Diploma passed (4-years course)'";
		STR_EDU += " Union Select '16-B.A/B.Com/BSc(Hons)or B.Sc-Engineering পাশ'";
		STR_EDU += " Union Select '17-M.A/M.Sc/M.Com/M.S.S/M.B.B.S পাশ'";

		STR_EDU += " Union Select '30–ব্রাক শিশু শ্রেণী পাশ:Class 0 passed '";
		STR_EDU += " Union Select '31–ব্রাক প্রথম শ্রেণী পাশ:Class 1 passed '";
		STR_EDU += " Union Select '32–ব্রাক দ্বিতীয় শ্রেণী পাশ:Class 2 passed '";
		STR_EDU += " Union Select '33–ব্রাক তৃতীয় শ্রেণী পাশ:Class 3 passed '";
		STR_EDU += " Union Select '34–ব্রাক চতুর্থ শ্রেণী পাশ:Class 4 passed '";
		STR_EDU += " Union Select '35–ব্রাক পঞ্চম শ্রেণী পাশ:Class 5 passed '";
		STR_EDU += " Union Select '99-জানিনা'";

		final RadioGroup roMemberOption =(RadioGroup)findViewById(R.id.roMemberOption);
		roMemberOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup arg0, int id) {
				if(id == R.id.roActiveMember)
				{
					DataRetrieve(vill+bari+hhno, false,"active");
				}
				else if(id == R.id.roAllMember)
				{
					DataRetrieve(vill+bari+hhno, false,"all");
				}
			}});

		//C.Save("Delete from Death_Temp");

		DataRetrieve(vill+bari+hhno, true,"active");

		Button cmdNewMem = (Button)findViewById(R.id.cmdNewMem);
		Button cmdSES = (Button)findViewById(R.id.cmdSES);
		Button cmdPHis = (Button)findViewById(R.id.cmdPHis);
		Button cmdEvList = (Button)findViewById(R.id.cmdEvList);
		Button cmdVisitList = (Button)findViewById(R.id.cmdVisitList);
		Button cmdImmunization = (Button)findViewById(R.id.cmdImmunization);
		Button cmdCheckMember = (Button)findViewById(R.id.cmdCheckMember);
		Button cmdVisitNote = (Button)findViewById(R.id.cmdVisitNote);
		Button cmdContactNo = (Button)findViewById(R.id.cmdContactNo);

		cmdNewMem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				ShowEventForm(vill,bari,hhno,"","","");
			}
		});
		cmdSES.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Bundle IDbundle = new Bundle();
				IDbundle.putString("vill", vill);
				IDbundle.putString("bari", bari);
				IDbundle.putString("hh", hhno);
				IDbundle.putString("rnd", g.getRoundNumber());
				IDbundle.putString("household", vill+bari+hhno);
				Intent f11 = new Intent(getApplicationContext(),ses.class);
				f11.putExtras(IDbundle);
				startActivity(f11);
			}
		});
		cmdPHis.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				if(!C.Existence("select Sno from tTrans where status='m' and Vill||Bari||Hh='"+ (vill+bari+hhno) +"' and Sex='2' and ms<>'30' and ((julianday(date('now'))-julianday(bdate))/365.25)<50"))
				{
					Connection.MessageBox(MemberEvents.this, "No eligible women available for reproductive history(RH)");
					return;
				}
				Bundle IDbundle = new Bundle();
				IDbundle.putString("vill", vill);
				IDbundle.putString("bari", bari);
				IDbundle.putString("hh", hhno);
				IDbundle.putString("rnd", g.getRoundNumber());
				IDbundle.putString("household", vill+bari+hhno);
				Intent f11 = new Intent(getApplicationContext(),reproductive_history.class);
				f11.putExtras(IDbundle);
				startActivity(f11);
			}
		});
		cmdEvList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				ShowEventList(vill,bari,hhno);
				//Intent f11 = new Intent(getApplicationContext(),EventList.class);
				//startActivity(f11); 
			}
		});

		cmdVisitList.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				VisitList(vill+bari+hhno);
			}
		});

		cmdImmunization.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				//ImmunizationForm(vill,bari,hhno);
				g.setVillageCode( vill );
				g.setBariCode( bari );
				g.setHouseholdNo( hhno );
				Intent f11 = new Intent(getApplicationContext(),Immunization.class);
				startActivity(f11);
			}
		});
		cmdCheckMember.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

			}
		});

		cmdVisitNote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				VisitNoteForm(vill, bari, hhno);
			}
		});

		cmdContactNo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				ContactNoForm(vill, bari, hhno);
			}
		});


		C.Save("Update ImmunizationTemp Set PNo=ifnull((Select PNo from tTrans where status='m' and Vill||Bari||HH||SNo=ImmunizationTemp.vill||ImmunizationTemp.bari||ImmunizationTemp.hh||ImmunizationTemp.sno),'') where length(pno)=0 or pno is null");


		//Drop Image from the database if age>5 years
		//25 Jan 2018
		try {
			Cursor curH = C.ReadData("select vill as vill,bari as bari,hh as hh,sno as sno,name from Member where Vill||Bari||HH='"+ (vill+bari+hhno) +"' and Cast(((julianday(date('now'))-julianday(BDate))/365.25) as int) between 5 and 11");
			curH.moveToFirst();
			String filaName = "";
			while (!curH.isAfterLast()) {
				filaName = curH.getString(curH.getColumnIndex("vill"))+
						curH.getString(curH.getColumnIndex("bari"))+
						curH.getString(curH.getColumnIndex("hh"))+
						curH.getString(curH.getColumnIndex("sno"));

				File imageFile1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (filaName) +"1.jpg");
				if(imageFile1.exists()) imageFile1.delete();
				File imageFile2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (filaName) +"2.jpg");
				if(imageFile2.exists()) imageFile2.delete();
				File imageFile3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (filaName) +"3.jpg");
				if(imageFile3.exists()) imageFile3.delete();
				File imageFile4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (filaName) +"4.jpg");
				if(imageFile4.exists()) imageFile4.delete();
				File imageFile5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (filaName) +"5.jpg");
				if(imageFile5.exists()) imageFile5.delete();

				curH.moveToNext();
			}
			curH.close();
		}catch (Exception ex){

		}

	}


	//Retrieve member list
	//***************************************************************************************************************************	
	public void DataRetrieve(String HH, Boolean heading, String ActiveOrAll)
	{
		try
		{
			String SQLStr = "";

			if(ActiveOrAll.equalsIgnoreCase("active"))
			{
				SQLStr = "Select  (case when m.vill is null then 'n' else 'o' end)as NewOld, t.Vill, t.Bari, t.Hh, t.Sno, t.Pno, t.Name, t.Rth, t.Sex, t.BDate, Cast(((julianday(date('now'))-julianday(t.BDate))/365.25) as int) as Age,Cast(((julianday(t.ExDate)-julianday(t.BDate))/365.25) as int) as DeathAge, t.Mono, t.Fano, t.Edu,";
				SQLStr += " t.Ms, t.Pstat, t.LmpDt, t.Sp1, t.Sp2, t.Sp3, t.Sp4, t.Ocp, t.EnType, t.EnDate,";
				SQLStr += " (case when cast(strftime('%Y', ifnull(t.ExDate,'')) as int)>=2014 and t.ExType='55' then '1' else '2' end)as deathrep,";
				SQLStr += " ifnull(t.ExType,'')ExType,ifnull(t.ExDate,'')ExDate,cast(strftime('%Y', ifnull(t.ExDate,'')) as int)ExYear,ifnull(t.PosMig,'')PosMig,ifnull(t.PosMigDate,'')PosMigDate from tTrans t";
				SQLStr += " left outer join member m on t.vill||t.bari||t.hh||t.sno = m.vill||m.bari||m.hh||m.sno";
				SQLStr += " where t.status='m' and t.vill||t.bari||t.hh='"+ HH +"' and (length(t.extype)=0 or t.extype is null) order by cast(t.SNo as int) asc";
			}
			else if(ActiveOrAll.equalsIgnoreCase("all"))
			{
				SQLStr = "Select  (case when m.vill is null then 'n' else 'o' end)as NewOld, t.Vill, t.Bari, t.Hh, t.Sno, t.Pno, t.Name, t.Rth, t.Sex, t.BDate, Cast(((julianday(date('now'))-julianday(t.BDate))/365.25) as int) as Age, Cast(((julianday(t.ExDate)-julianday(t.BDate))/365.25) as int) as DeathAge, t.Mono, t.Fano, t.Edu,";
				SQLStr += " t.Ms, t.Pstat, t.LmpDt, t.Sp1, t.Sp2, t.Sp3, t.Sp4, t.Ocp, t.EnType, t.EnDate,";
				SQLStr += " (case when cast(strftime('%Y', ifnull(t.ExDate,'')) as int)>=2014 and t.ExType='55' then '1' else '2' end)as deathrep,";
				SQLStr += " ifnull(t.ExType,'')ExType,ifnull(t.ExDate,'')ExDate,cast(strftime('%Y', ifnull(t.ExDate,'')) as int)ExYear,ifnull(t.PosMig,'')PosMig,ifnull(t.PosMigDate,'')PosMigDate from tTrans t";
				SQLStr += " left outer join member m on t.vill||t.bari||t.hh||t.sno = m.vill||m.bari||m.hh||m.sno";
				SQLStr += " where t.status='m' and t.vill||t.bari||t.hh='"+ HH +"' order by cast(t.SNo as int) asc";
			}

			Cursor cur1 = C.ReadData(SQLStr);

			cur1.moveToFirst();
			mylist.clear();

			ListView list = (ListView) findViewById(R.id.lstMember);
			if(heading ==true)
			{
				View header = getLayoutInflater().inflate(R.layout.membereventsheading, null);
				list.addHeaderView(header);
			}

			int i=0;
			while(!cur1.isAfterLast())
			{
				HashMap<String, String> map = new HashMap<String, String>();

				if(i==0)
				{
					//View header = getLayoutInflater().inflate(R.layout.membereventsheading, null);
					//list.addHeaderView(header);	        		
				}
				map.put("newold", cur1.getString(cur1.getColumnIndex("NewOld")));
				map.put("vill", cur1.getString(cur1.getColumnIndex("Vill")));
				map.put("bari", cur1.getString(cur1.getColumnIndex("Bari")));
				map.put("hh", cur1.getString(cur1.getColumnIndex("Hh")));
				map.put("sno", cur1.getString(cur1.getColumnIndex("Sno")));
				map.put("pno", cur1.getString(cur1.getColumnIndex("Pno")));
				map.put("name", cur1.getString(cur1.getColumnIndex("Name")));
				map.put("rth", cur1.getString(cur1.getColumnIndex("Rth")));
				map.put("sex", cur1.getString(cur1.getColumnIndex("Sex")));
				map.put("dob", cur1.getString(cur1.getColumnIndex("BDate")));
				map.put("age", cur1.getString(cur1.getColumnIndex("Age")));
				map.put("mono", cur1.getString(cur1.getColumnIndex("Mono")));
				map.put("fano", cur1.getString(cur1.getColumnIndex("Fano")));
				map.put("edu", cur1.getString(cur1.getColumnIndex("Edu")));
				map.put("ms", cur1.getString(cur1.getColumnIndex("Ms")));
				map.put("pstat", cur1.getString(cur1.getColumnIndex("Pstat")));
				map.put("lmpdt", cur1.getString(cur1.getColumnIndex("LmpDt")));
				map.put("sp1", cur1.getString(cur1.getColumnIndex("Sp1")));
				map.put("sp2", cur1.getString(cur1.getColumnIndex("Sp2")));
				map.put("sp3", cur1.getString(cur1.getColumnIndex("Sp3")));
				map.put("sp4", cur1.getString(cur1.getColumnIndex("Sp4")));
				map.put("ocp", cur1.getString(cur1.getColumnIndex("Ocp")));
				map.put("entype", cur1.getString(cur1.getColumnIndex("EnType")));
				map.put("endate", cur1.getString(cur1.getColumnIndex("EnDate")));
				map.put("extype", cur1.getString(cur1.getColumnIndex("ExType")));
				map.put("exdate", cur1.getString(cur1.getColumnIndex("ExDate")));
				map.put("posmig", cur1.getString(cur1.getColumnIndex("PosMig")));
				map.put("posmigdate", cur1.getString(cur1.getColumnIndex("PosMigDate")));
				map.put("exyear", cur1.getString(cur1.getColumnIndex("ExYear")));
				map.put("deathage", cur1.getString(cur1.getColumnIndex("DeathAge")));
				map.put("deathrep", cur1.getString(cur1.getColumnIndex("deathrep")));

				mylist.add(map);
				mSchedule = new SimpleAdapter(MemberEvents.this, mylist, R.layout.membereventsrow,new String[] {"sno","name","rth","dob","mono","fano","edu","ms","sp1","ocp"},
						new int[] {R.id.sno,R.id.name,R.id.rth,R.id.bdate,R.id.mono,R.id.fano,R.id.edu,R.id.ms,R.id.sp1,R.id.ocp});
				list.setAdapter(new MemberListAdapter(this));

				i+=1;
				cur1.moveToNext();
			}
			cur1.close();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
		}
	}


	public class MemberListAdapter extends BaseAdapter
	{
		private Context context;

		public MemberListAdapter(Context c){
			context = c;
		}

		public int getCount() {
			return mSchedule.getCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.membereventsrow, null);
			}


			TextView sno= (TextView) convertView.findViewById(R.id.sno);
			TextView pno= (TextView) convertView.findViewById(R.id.pno);
			TextView name= (TextView) convertView.findViewById(R.id.name);
			TextView rth= (TextView) convertView.findViewById(R.id.rth);
			TextView sex= (TextView) convertView.findViewById(R.id.sex);
			TextView bdate= (TextView) convertView.findViewById(R.id.bdate);
			TextView mono= (TextView) convertView.findViewById(R.id.mono);
			TextView fano= (TextView) convertView.findViewById(R.id.fano);
			TextView edu= (TextView) convertView.findViewById(R.id.edu);
			TextView ms= (TextView) convertView.findViewById(R.id.ms);
			TextView pstat= (TextView) convertView.findViewById(R.id.pstat);
			TextView lmpdt= (TextView) convertView.findViewById(R.id.lmpdt);
			TextView sp1= (TextView) convertView.findViewById(R.id.sp1);
			TextView sp2= (TextView) convertView.findViewById(R.id.sp2);
			TextView sp3= (TextView) convertView.findViewById(R.id.sp3);
			TextView sp4= (TextView) convertView.findViewById(R.id.sp4);
			TextView ocp= (TextView) convertView.findViewById(R.id.ocp);
			TextView entype= (TextView) convertView.findViewById(R.id.entype);
			TextView endate= (TextView) convertView.findViewById(R.id.endate);
			TextView extype= (TextView) convertView.findViewById(R.id.extype);
			TextView exdate= (TextView) convertView.findViewById(R.id.exdate);

			TextView posmig= (TextView) convertView.findViewById(R.id.posmig);
			TextView posmigdate= (TextView) convertView.findViewById(R.id.posmigdate);

			final HashMap<String, String> o = (HashMap<String, String>) mSchedule.getItem(position);

			sno.setText(o.get("sno"));
			pno.setText(o.get("pno"));
			name.setText(o.get("name"));
			rth.setText(o.get("rth"));
			sex.setText(o.get("sex"));
			bdate.setText(Global.DateConvertDMY(o.get("dob")));
			mono.setText(o.get("mono"));
			fano.setText(o.get("fano"));
			edu.setText(o.get("edu"));
			ms.setText(o.get("ms"));
			pstat.setText(o.get("pstat"));
			if(o.get("lmpdt")==null | o.get("lmpdt").trim().length()==0)
				lmpdt.setText(o.get("lmpdt"));
			else
				lmpdt.setText(Global.DateConvertDMY(o.get("lmpdt")));


			sp1.setText(o.get("sp1"));
			sp2.setText(o.get("sp2"));
			sp3.setText(o.get("sp3"));
			sp4.setText(o.get("sp4"));
			ocp.setText(o.get("ocp"));
			entype.setText(o.get("entype"));
			endate.setText(Global.DateConvertDMY(o.get("endate")));
			extype.setText(o.get("extype"));
			if(o.get("exdate")==null | o.get("exdate").trim().length()==0)
				exdate.setText(o.get("exdate"));
			else
				exdate.setText(Global.DateConvertDMY(o.get("exdate")));

			//show only if possible migration
			if(o.get("posmig").equals("54"))
				posmig.setText(o.get("posmig"));
			else
				posmig.setText("");

			if(o.get("posmigdate")==null | o.get("posmigdate").trim().length()==0)
				posmigdate.setText(o.get("posmigdate"));
			else
				posmigdate.setText(Global.DateConvertDMY(o.get("posmigdate")));

			if(o.get("extype").trim().length()==0 & (o.get("posmig").trim().length()==0 | !o.get("posmig").trim().equals("54")))
			{
				sno.setTextColor(Color.BLACK);
				pno.setTextColor(Color.BLACK);
				name.setTextColor(Color.BLACK);
				rth.setTextColor(Color.BLACK);
				sex.setTextColor(Color.BLACK);
				bdate.setTextColor(Color.BLACK);
				mono.setTextColor(Color.BLACK);
				fano.setTextColor(Color.BLACK);
				edu.setTextColor(Color.BLACK);
				ms.setTextColor(Color.BLACK);
				pstat.setTextColor(Color.BLACK);
				lmpdt.setTextColor(Color.BLACK);
				sp1.setTextColor(Color.BLACK);
				sp2.setTextColor(Color.BLACK);
				sp3.setTextColor(Color.BLACK);
				sp4.setTextColor(Color.BLACK);
				ocp.setTextColor(Color.BLACK);
				entype.setTextColor(Color.BLACK);
				endate.setTextColor(Color.BLACK);
				extype.setTextColor(Color.BLACK);
				exdate.setTextColor(Color.BLACK);
			}
			else if(o.get("extype").trim().length()==0 & o.get("posmig").trim().equals("54"))
			{
				sno.setTextColor(Color.BLUE);
				pno.setTextColor(Color.BLUE);
				name.setTextColor(Color.BLUE);
				rth.setTextColor(Color.BLUE);
				sex.setTextColor(Color.BLUE);
				bdate.setTextColor(Color.BLUE);
				mono.setTextColor(Color.BLUE);
				fano.setTextColor(Color.BLUE);
				edu.setTextColor(Color.BLUE);
				ms.setTextColor(Color.BLUE);
				pstat.setTextColor(Color.BLUE);
				lmpdt.setTextColor(Color.BLUE);
				sp1.setTextColor(Color.BLUE);
				sp2.setTextColor(Color.BLUE);
				sp3.setTextColor(Color.BLUE);
				sp4.setTextColor(Color.BLUE);
				ocp.setTextColor(Color.BLUE);
				entype.setTextColor(Color.BLUE);
				endate.setTextColor(Color.BLUE);
				extype.setTextColor(Color.BLUE);
				exdate.setTextColor(Color.BLUE);
				posmig.setTextColor(Color.BLUE);
				posmigdate.setTextColor(Color.BLUE);

			}
			else
			{
				sno.setTextColor(Color.RED);
				pno.setTextColor(Color.RED);
				name.setTextColor(Color.RED);
				rth.setTextColor(Color.RED);
				sex.setTextColor(Color.RED);
				bdate.setTextColor(Color.RED);
				mono.setTextColor(Color.RED);
				fano.setTextColor(Color.RED);
				edu.setTextColor(Color.RED);
				ms.setTextColor(Color.RED);
				pstat.setTextColor(Color.RED);
				lmpdt.setTextColor(Color.RED);
				sp1.setTextColor(Color.RED);
				sp2.setTextColor(Color.RED);
				sp3.setTextColor(Color.RED);
				sp4.setTextColor(Color.RED);
				ocp.setTextColor(Color.RED);
				entype.setTextColor(Color.RED);
				endate.setTextColor(Color.RED);
				extype.setTextColor(Color.RED);
				exdate.setTextColor(Color.RED);
			}

			if(o.get("newold").equals("n"))
			{
				sno.setTextColor(Color.GREEN);
				pno.setTextColor(Color.GREEN);
				name.setTextColor(Color.GREEN);
				rth.setTextColor(Color.GREEN);
				sex.setTextColor(Color.GREEN);
				bdate.setTextColor(Color.GREEN);
				mono.setTextColor(Color.GREEN);
				fano.setTextColor(Color.GREEN);
				edu.setTextColor(Color.GREEN);
				ms.setTextColor(Color.GREEN);
				pstat.setTextColor(Color.GREEN);
				lmpdt.setTextColor(Color.GREEN);
				sp1.setTextColor(Color.GREEN);
				sp2.setTextColor(Color.GREEN);
				sp3.setTextColor(Color.GREEN);
				sp4.setTextColor(Color.GREEN);
				ocp.setTextColor(Color.GREEN);
				entype.setTextColor(Color.GREEN);
				endate.setTextColor(Color.GREEN);
				extype.setTextColor(Color.GREEN);
				exdate.setTextColor(Color.GREEN);
			}


			if(o.get("extype").trim().equals("55"))
			{
				if(o.get("deathrep").equals("1"))
				{
					name.setBackgroundColor(Color.RED);
					name.setTextColor( Color.WHITE );
				}
				else
				{
					name.setBackgroundColor(Color.WHITE);
					name.setTextColor( Color.RED );
				}
			}


			final AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
			final LinearLayout memtab = (LinearLayout)convertView.findViewById(R.id.memtab);
			final EditText lblsno = (EditText)convertView.findViewById(R.id.txtQSNo);
			final TextView lblName = (TextView)convertView.findViewById(R.id.lblName);



			memtab.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(o.get("extype").trim().length()==0)
					{
						if(o.get("posmigdate")==null | o.get("posmigdate").trim().length()==0)
						{
							ShowEventForm(o.get("vill"),o.get("bari"),o.get("hh"),o.get("sno"),o.get("pno"),o.get("name"));
						}
						else
						{
							//21 May 2014
							AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
							adb.setTitle("Possible Migration");
							adb.setMessage("আপনি কি ["+ o.get("name") +"]এর 54 Event এর তথ্য মুছে ফেলতে চান [Yes/No]?");

							adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog1, int which) {

								}});

							adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog1, int which) {
									C.Save("Update ttrans Set PosMig='',PosMigDate='' where status='m' and vill||bari||hh='"+ (o.get("vill")+o.get("bari")+o.get("hh")) +"' and SNo='"+ o.get("sno") +"'");

									DataRetrieve((o.get("vill")+o.get("bari")+o.get("hh")),false,"active");
								}});

							adb.show();
						}

					}
					else
					{
						if(o.get("extype").trim().equals("55") & Integer.valueOf(o.get("exyear"))>=2014)
						{
							if(Integer.valueOf(o.get("deathage"))>=13 & Integer.valueOf(o.get("deathage"))<=49 & o.get("sex").equals("2"))
							{
								g.setPregOnDeath("1");
							}
							else
							{
								g.setPregOnDeath("2");
							}
							g.setmemSlNo( o.get("sno") );
							Intent f = new Intent(getApplicationContext(),Death.class);
							startActivity( f );
						}
						else
						{
							Connection.MessageBox(MemberEvents.this, "সদস্য এ খানায় সক্রিয় নয়.");
							return;
						}
					}
				}
			});

			Button cmdDel = (Button)convertView.findViewById(R.id.cmdDel);
			cmdDel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
					adb.setTitle("Member Delete");
					adb.setMessage("আপনি কি ["+ o.get("name") +"]এর তথ্য মুছে ফেলতে চান [Yes/No]?");

					adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {

						}});

					adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {
							C.Save("delete from ttrans where status='m' and vill||bari||hh='"+ (o.get("vill")+o.get("bari")+o.get("hh")) +"' and SNo='"+ o.get("sno") +"'");
							C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ (o.get("vill")+o.get("bari")+o.get("hh")) +"' and SNo='"+ o.get("sno") +"'");
							DataRetrieve((o.get("vill")+o.get("bari")+o.get("hh")),false,"active");
						}});

					adb.show();
				}
			});
			Button cmdEdit = (Button)convertView.findViewById(R.id.cmdEdit);
			cmdEdit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
					adb.setTitle("Member Edit");
					adb.setMessage("আপনি কি ["+ o.get("name") +"]এর তথ্য পরিবর্তন করতে চান [Yes/No]?");

					adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {

						}});

					adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {
							MemberForm(o.get("vill")+o.get("bari")+o.get("hh"),o.get("sno"), "", o.get("entype"), "", "e");
						}});

					adb.show();

				}
			});

			if(o.get("newold").equals("n"))
			{
				cmdDel.setEnabled(true);
				cmdEdit.setEnabled(true);
			}
			else if(o.get("newold").equals("o"))
			{
				cmdDel.setEnabled(false);
				cmdEdit.setEnabled(false);
			}

			//21 may 2016
			ImageView imgPreg = (ImageView)convertView.findViewById(R.id.imgPreg);
			if(o.get("pstat").equals("41"))
				imgPreg.setVisibility(View.VISIBLE);
			else
				imgPreg.setVisibility(View.INVISIBLE);

			return convertView;
		}

	}

	//***************************************************************************************************************************
	private void UpdateEventForm(final String EvCode, final String EDate, final String Vill,final  String Bari,final  String HH, String SNo, String PNo, final ListView lvw, final String SPAge)
	{
		final Dialog dialog = new Dialog(MemberEvents.this);
		dialog.setTitle("Events Update Form");
		dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.eventupdateform);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.TOP;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);


		EditText txtQSNo = (EditText)dialog.findViewById(R.id.txtQSNo);
		final EditText txtQPNo = (EditText)dialog.findViewById(R.id.txtQPNo);
		TextView lblName = (TextView)dialog.findViewById(R.id.lblName);


		//Load Event Code List
		//..................................................................................................................................
		final Spinner EvType = (Spinner)dialog.findViewById(R.id.EvType);
		EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode"));

		String Sex = "";
		String MS  = "";
		String PS  = "";
		String Name = "";

		//String PNo  = "";
		int mage   = 0;

		Cursor cur = C.ReadData("Select cast(((julianday(date('now'))-julianday(bdate))/365.25)as int)mage,sex,ms,ifnull(pstat,'')pstat,name as name from tTrans where Status='m' and vill || bari || hh || SNo='"+ (Vill+Bari+HH+SNo) +"'");
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			Sex  = cur.getString(1).toString();
			MS   = cur.getString(2).toString();
			mage = Integer.valueOf(cur.getString(0));
			PS   = cur.getString(3).toString();
			Name = cur.getString(4).toString();
			//RAge = cur.getString(5).toString();

			cur.moveToNext();
		}
		cur.close();


		//..................................................................................................................................
		final LinearLayout secPOR = (LinearLayout)dialog.findViewById(R.id.secPOR);
		final LinearLayout secEvDate = (LinearLayout)dialog.findViewById(R.id.secEvDate);
		EvDate  = (EditText)dialog.findViewById(R.id.EvDate);
		btnEvDate_Update  = (ImageButton)dialog.findViewById(R.id.btnEvDate);
		btnLmpDt  = (ImageButton)dialog.findViewById(R.id.btnLmpDt);
		btnREvDT  = (ImageButton)dialog.findViewById(R.id.btnREvDT);

		btnEvDate_Update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnEvDate";
				showDialog(DATE_DIALOG);
			}
		});

		btnLmpDt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnLmpDt";
				showDialog(DATE_DIALOG);
			}
		});

		btnREvDT.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnREvDT";
				showDialog(DATE_DIALOG);
			}
		});


		final TextView lblCode=(TextView)dialog.findViewById(R.id.lblCode);
		final EditText txtCode = (EditText)dialog.findViewById(R.id.txtCode);
		final Spinner txtCodeList = (Spinner)dialog.findViewById(R.id.txtCodeList);

		final Spinner txtPOR = (Spinner)dialog.findViewById(R.id.txtPOR);
		final Spinner txt43 = (Spinner)dialog.findViewById(R.id.txt43);
		final Spinner txt44 = (Spinner)dialog.findViewById(R.id.txt44);

		final EditText SpNo = (EditText)dialog.findViewById(R.id.SpNo);
		final LinearLayout secSp = (LinearLayout)dialog.findViewById(R.id.secSp);
		final LinearLayout secDeliv = (LinearLayout)dialog.findViewById(R.id.secDeliv);
		final LinearLayout secLMP = (LinearLayout)dialog.findViewById(R.id.secLMP);
		txtLmpDt = (EditText)dialog.findViewById(R.id.LmpDt);
		final LinearLayout secReason = (LinearLayout)dialog.findViewById(R.id.secReason);
		final LinearLayout secQRAge = (LinearLayout)dialog.findViewById(R.id.secQRAge);
		final LinearLayout secQEvDT = (LinearLayout)dialog.findViewById(R.id.secQEvDT);
		final LinearLayout secQEvReason = (LinearLayout)dialog.findViewById(R.id.secQEvReason);

		final TextView lblQRAge = (TextView)dialog.findViewById(R.id.lblQRAge);
		final EditText txtRAge  = (EditText)dialog.findViewById(R.id.txtRAge);
		txtREvDT  = (EditText)dialog.findViewById(R.id.txtREvDT);
		final EditText txtRReason  = (EditText)dialog.findViewById(R.id.txtRReason);
		//---------------------------------------------------------------------
		secPOR.setVisibility(View.GONE);
		lblCode.setVisibility(View.GONE);
		txtCode.setVisibility(View.GONE);
		txtCodeList.setVisibility(View.GONE);
		secSp.setVisibility(View.GONE);
		secDeliv.setVisibility(View.GONE);
		secDelivType.setVisibility(View.GONE);
		secLMP.setVisibility(View.GONE);

		secReason.setVisibility(View.GONE);



		//---------------------------------------------------------------------
		txtQSNo.setText(SNo);
		lblName.setText(Name);
		txtQPNo.setText(PNo);
		EvDate.setText(Global.DateConvertDMY(EDate));
		//---------------------------------------------------------------------
		for(int i=0;i<EvType.getCount();i++)
		{
			if(EvCode.equals(Global.Left(EvType.getItemAtPosition(i).toString(),2)))
			{
				EvType.setSelection(i);
				i = EvType.getCount();
			}
		}

		final String ECode = EvType.getSelectedItem().toString().substring(0, 2);

		final String S = SNo;
		final String SEX = Sex;
		EvType.setEnabled(false);
		EvType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				//EvDate.setText("");
				EvDate.setEnabled(true);
				lblCode.setText("Code");
				txtCode.setText("");
				txtCodeList.setSelection(0);
				SpNo.setText("");
				txtPOR.setSelection(0);
				txt43.setSelection(0);
				txt44.setSelection(0);
				txtLmpDt.setText("");
				txtRAge.setText("");
				txtREvDT.setText("");
				txtRReason.setText("");

				lblCode.setVisibility(View.GONE);
				txtCode.setVisibility(View.GONE);
				txtCodeList.setVisibility(View.GONE);
				secSp.setVisibility(View.GONE);
				secDeliv.setVisibility(View.GONE);
				secDelivType.setVisibility(View.GONE);
				secLMP.setVisibility(View.GONE);

				secPOR.setVisibility(View.GONE);
				secReason.setVisibility(View.GONE);
				secQRAge.setVisibility(View.GONE);
				secQEvDT.setVisibility(View.GONE);
				secQEvReason.setVisibility(View.GONE);


				if(ECode.equals("21") | ECode.equals("31") | ECode.equals("32") | ECode.equals("33") | ECode.equals("34"))
				{
					if(ECode.equals("21"))
					{
						secReason.setVisibility(View.VISIBLE);
						secQRAge.setVisibility(View.VISIBLE);
						secQEvDT.setVisibility(View.VISIBLE);
						secQEvReason.setVisibility(View.VISIBLE);
						lblQRAge.setText("Reason/Spouse's Age");
					}
					else
					{
						secReason.setVisibility(View.VISIBLE);
						secQRAge.setVisibility(View.VISIBLE);
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
						lblQRAge.setText("Spouse's Age");
						txtRAge.setText(SPAge);
					}
				}
				else if(ECode.equals("22"))
				{
					EvDate.setEnabled(false);
					txtQPNo.setText("");
					g.setPNo("");
					g.setEvDate("");
					g.setPrevHousehold("");

					MigrationForm(dialog,"52");
				}
				else if(ECode.equals("23"))
				{
					EvDate.setEnabled(false);
					txtQPNo.setText("");
					g.setPNo("");
					g.setEvDate("");
					g.setPrevHousehold("");

					MigrationForm(dialog,"53");
				}
				else if(ECode.equals("25"))
				{
					secEvDate.setVisibility(View.VISIBLE);
					String BD = C.ReturnSingleValue("select BD from PS where vill||bari||HH = '"+ (Vill+Bari+HH) +"'");
					EvDate.setText(Global.DateConvertDMY(BD));
					EvDate.setEnabled(false);
				}
				else if(ECode.equals("41"))
				{
					lblCode.setText("Phone No");
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					txtCodeList.setVisibility(View.GONE);
				}
				else if(ECode.equals("42"))
				{
					//search LMP date
					String LMP = C.ReturnSingleValue("Select LmpDt from tTrans where status='m' and vill||bari||hh||sno='"+ (Vill+Bari+HH+S) +"'");
					secPOR.setVisibility(View.VISIBLE);
					secDeliv.setVisibility(View.VISIBLE);
					secDelivType.setVisibility(View.VISIBLE);
					secLMP.setVisibility(View.VISIBLE);
					txtLmpDt.setText(Global.DateConvertDMY(LMP));
					txtLmpDt.setEnabled(false);
					txtPOR.setAdapter(C.getArrayAdapter("Select distinct ' 'OutResult from POR union SELECT OutResult FROM POR"));
					//txt43.setAdapter(C.getArrayAdapter("Select distinct ' 'Place from POP union SELECT Place FROM POP"));
					//txt43.setAdapter( C.getArrayAdapter(STR_POP) );
					txt43.setAdapter(C.getArrayAdapterMultiline(STR_POP));
					txt44.setAdapter(C.getArrayAdapter("Select distinct ' 'Atten from POP union SELECT Atten FROM POA"));
				}
				else if(ECode.equals("61") | ECode.equals("62"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					txtCodeList.setVisibility(View.GONE);
				}
				else if(ECode.equals("63") & SEX.equals("1"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					//secSp.setVisibility(View.VISIBLE);
				}
				else if(ECode.equals("63") & SEX.equals("2"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					//secSp.setVisibility(View.GONE);
				}

				else if(ECode.equals("64") | ECode.equals("71") | ECode.equals("72"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.GONE);
					txtCodeList.setVisibility(View.VISIBLE);
					if(ECode.equals("64"))
						txtCodeList.setAdapter(C.getArrayAdapter("Select distinct ' 'Name union SELECT Name FROM RTH"));
					else if(ECode.equals("71"))
					{
						//txtCodeList.setAdapter(C.getArrayAdapter("Select distinct ' 'Name union SELECT Name FROM EDU"));
						txtCodeList.setAdapter(C.getArrayAdapter(STR_EDU));
					}
					else if(ECode.equals("72"))
						txtCodeList.setAdapter(C.getArrayAdapter("Select distinct ' 'Name union SELECT Name FROM OCP"));
				}
				if(ECode.equals("12") | ECode.equals("40") | ECode.equals("49"))
				{
					secEvDate.setVisibility(View.GONE);
				}
				else
				{
					secEvDate.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		txtRAge.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				secQEvDT.setVisibility(View.GONE);
				secQEvReason.setVisibility(View.GONE);

				if(txtRAge.getText().length()!=0)
				{
					String ECode = Global.Left(EvType.getSelectedItem().toString(),2);
					if(Integer.valueOf(txtRAge.getText().toString())==77)
					{
						txtREvDT.setText("");
						txtRReason.setText("");
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
					}
					else if(ECode.equals("21"))
					{
						secQEvDT.setVisibility(View.VISIBLE);
						secQEvReason.setVisibility(View.VISIBLE);
					}
					else
					{
						txtREvDT.setText("");
						txtRReason.setText("");
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
					}

				}
			}});

		txtCode.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				if(txtCode.getText().toString().equals("00") & SEX.equals("1"))
				{
					secSp.setVisibility(View.VISIBLE);
				}
				else
				{
					secSp.setVisibility(View.GONE);
				}
			}});

		Button cmdEventSave = (Button)dialog.findViewById(R.id.cmdEventSave);
		cmdEventSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				UpdateEvents(dialog, EDate, lvw);
			}
		});


		Button close = (Button)dialog.findViewById(R.id.close);
		close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}


	//***************************************************************************************************************************
	private void UpdateEvents(Dialog d, String EDate, ListView lvw)
	{
		try
		{
			//Dialog Event Form
			//----------------------------------------------------------------------------------------------------------
			final EditText txtSNo    = (EditText)d.findViewById(R.id.txtQSNo);
			final Spinner EvType     = (Spinner)d.findViewById(R.id.EvType);
			final EditText txtEvDate = (EditText)d.findViewById(R.id.EvDate);
			final EditText txtCode   = (EditText)d.findViewById(R.id.txtCode);
			final Spinner txtCodeList= (Spinner)d.findViewById(R.id.txtCodeList);
			final EditText txtQPNo   = (EditText)d.findViewById(R.id.txtQPNo);
			final EditText txtSpNo   = (EditText)d.findViewById(R.id.SpNo);
			final Spinner txtPOR     = (Spinner)d.findViewById(R.id.txtPOR);
			final Spinner txt43      = (Spinner)d.findViewById(R.id.txt43);
			final Spinner txt44      = (Spinner)d.findViewById(R.id.txt44);
			final EditText txtLmpDt  = (EditText)d.findViewById(R.id.LmpDt);
			final EditText txtRAge   = (EditText)d.findViewById(R.id.txtRAge);
			final EditText txtREvDT  = (EditText)d.findViewById(R.id.txtREvDT);
			final EditText txtRReason = (EditText)d.findViewById(R.id.txtRReason);

			if(EvType.getSelectedItemPosition()==0)
			{
				Connection.MessageBox(MemberEvents.this, "লিস্ট থেকে সঠিক ইভেন্ট কোড সিলেক্ট করুন।");
				return;
			}
			//----------------------------------------------------------------------------------------------------------
			String Rnd    = g.getRoundNumber();
			String VDate  = g.CurrentDateYYYYMMDD;
			String Household = vill+bari+hhno;
			String SNo    = txtSNo.getText().toString();
			Integer ECode = Integer.parseInt(EvType.getSelectedItem().toString().substring(0, 2));
			String PNo    = txtQPNo.getText().toString();
			String EvDate = Global.DateConvertYMD(txtEvDate.getText().toString());
			String Code   = txtCode.getText().toString();
			String CodeList = "";
			String SpNo   = txtSpNo.getText().toString();
			String POR    = "";
			String POP    = "";
			String POA    = "";
			String LmpDt  = txtLmpDt.getText().toString().length()==0?"":Global.DateConvertYMD(txtLmpDt.getText().toString());

			String RAge    = txtRAge.getText().toString();
			String REvDt   = txtREvDT.getText().toString().length()==0?"":Global.DateConvertYMD(txtREvDT.getText().toString());
			String RReason = txtRReason.getText().toString();
			//----------------------------------------------------------------------------------------------------------

			String SQL = "";
			if(ECode==31 | ECode==32 | ECode==33 | ECode==34)
			{
				String OriginalEventDate = EDate;
				String StartDate;
				String EndDate;

				StartDate = C.ReturnSingleValue("select EvDate from events where pno='"+ PNo +"' and evtype in('31','32','33') and evdate<'"+ EDate +"' order by cast(evdate as datetime)desc limit 1");
				EndDate   = C.ReturnSingleValue("select EvDate from events where pno='"+ PNo +"' and evtype in('31','32','33') and evdate>'"+ EDate +"' order by cast(evdate as datetime)asc limit 1");

				if(StartDate.length()==10)
				{
					if(Global.DateDifferenceDays(Global.DateConvertDMY(EvDate),Global.DateConvertDMY(StartDate))<0)
					{
						Connection.MessageBox(MemberEvents.this, "ঘটনার তারিখ সর্বশেষ ঘটিত বিবাহের ঘটনার তারিখ "+ Global.DateConvertDMY(StartDate) +" এর সমান অথবা বড় হবে।");
						return;
					}
				}
				if(EndDate.length()==10)
				{
					if(Global.DateDifferenceDays(Global.DateConvertDMY(EndDate),Global.DateConvertDMY(EvDate))<0)
					{
						Connection.MessageBox(MemberEvents.this, "ঘটনার তারিখ সর্বশেষ ঘটিত বিবাহের ঘটনার তারিখ "+ Global.DateConvertDMY(EndDate) +" এর সমান অথবা কম হবে।");
						return;
					}
				}

				//Event date should be greater than last occurred event date
	        	/*
	        	String LastEVDate = C.ReturnSingleValue("select a.EvDate from (select EvDate from events where pno='"+ PNo +"' and evtype in('31','32','33') order by cast(evdate as datetime)desc limit 2)a order by cast(evdate as datetime) asc limit 1");
	        	if(LastEVDate.length()==10)
	        	{
		        	if(Global.DateDifferenceDays(Global.DateConvertDMY(EvDate),Global.DateConvertDMY(LastEVDate))<0)
		        	{
		        		Connection.MessageBox(MemberEvents.this, "ঘটনার তারিখ সর্বশেষ ঘটিত বিবাহের ঘটনার তারিখ "+ Global.DateConvertDMY(LastEVDate) +" এর বড় হবে।");
		        		return;	        		
		        	}
	        	}
	        	*/

				//Temp Table: Event
				SQL = "Update tTrans set ";
				SQL += " EvDate='"+ EvDate +"',Info3='"+ RAge +"',upload='2'";
				SQL += " Where status='e' and ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"' and ";
				SQL += " EvDate = '"+ EDate +"' and EvType='"+ ECode +"'";
				C.Save(SQL);


				//Transfer events from main to UpdateEvents table
				SQL = "Insert into UpdateEvents(Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload)";
				SQL += "Select Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload from Events where";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"'  and EvType='"+ ECode +"' and EvDate='"+ EDate +"'";
				C.Save(SQL);

				//Table: Event
				SQL = "Update Events set ";
				SQL += " EvDate='"+ EvDate +"',Info3='"+ RAge +"',upload='2'";
				SQL += " Where ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"' and ";
				SQL += " EvDate = '"+ EDate +"' and EvType='"+ ECode +"'";
				C.Save(SQL);

				DataRetrieve(vill+bari+hhno,false,"active");
				d.dismiss();
			}
			else if(ECode == 41)
			{
				//date difference between lmp and visit date should be greater than 40 days	        	

				//Temp Table: Event
				SQL = "Update tTrans set ";
				SQL += " EvDate='"+ EvDate +"',upload='2'";
				SQL += " Where status='e' and ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"' and ";
				SQL += " EvDate = '"+ EDate +"' and EvType='41'";
				C.Save(SQL);

				//Temp Table: Member
				SQL = "Update tTrans set ";
				SQL += " LMPDt='"+ EvDate +"',upload='2'";
				SQL += " Where status='m' and ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"' and PStat='41'";
				C.Save(SQL);

				//Transfer events from main to UpdateEvents table
				SQL = "Insert into UpdateEvents(Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload)";
				SQL += "Select Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload from Events where";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"'  and EvType='41' and EvDate='"+ EDate +"'";
				C.Save(SQL);

				//Table: Event
				SQL = "Update Events set ";
				SQL += " EvDate='"+ EvDate +"',upload='2'";
				SQL += " Where ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"' and ";
				SQL += " EvDate = '"+ EDate +"' and EvType='41'";
				C.Save(SQL);

				//Table: Member
				SQL = "Update Member set ";
				SQL += " LMPDt='"+ EvDate +"',upload='2'";
				SQL += " Where ";
				SQL += " Vill||Bari||HH = '"+ vill+bari+hhno +"' and ";
				SQL += " PNo = '"+ PNo +"' and ";
				SQL += " SNo = '"+ SNo +"'  and PStat='41'";
				C.Save(SQL);
			}

			//String HH = vill+bari+hhno;
			//ListView lv = (ListView)d.findViewById(R.id.lstEvent);
			//EventDataList(d, HH, "old",lv);
			DataRetrieve(vill+bari+hhno,false,"active");
			d.dismiss();
		}
		catch(Exception ex)
		{
			Connection.MessageBox(MemberEvents.this, ex.getMessage());
			return;
		}
	}



	//***************************************************************************************************************************    
	private void ShowEventForm(final String Vill,final  String Bari,final  String HH, String SNo, String PNo, String Name)
	{
		String NewMember="2";
		//for new member
		if(SNo.length()==0)
		{
			NewMember = "1";
			SNo = C.ReturnSingleValue("select ifnull((substr('00'||cast((max(cast(sno as int))+1)as text),length('00'||cast((max(cast(sno as int))+1)as text))-1,2)),'01')SNo from tTrans where Status='m' and Vill||Bari||Hh='"+ (Vill+Bari+HH) +"'");
		}

		final Dialog dialog = new Dialog(MemberEvents.this);
		dialog.setTitle("Events Form");
		dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.eventpopup);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.TOP;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		secDelivType=(LinearLayout)dialog.findViewById(R.id.secDelivType);
		spnDelType=(Spinner)dialog.findViewById(R.id.spnDelType);

		List<String> listDelivType = new ArrayList<String>();

		listDelivType.add("");
		listDelivType.add("1-Normal Delivery");
		listDelivType.add("2-C Secttion Delivery");
		ArrayAdapter<String> adptrusecontraceptive= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listDelivType);
		spnDelType.setAdapter(adptrusecontraceptive);



		EditText txtQSNo = (EditText)dialog.findViewById(R.id.txtQSNo);
		final EditText txtQPNo = (EditText)dialog.findViewById(R.id.txtQPNo);
		TextView lblName = (TextView)dialog.findViewById(R.id.lblName);


		//Load Event Code List
		//..................................................................................................................................
		final Spinner EvType = (Spinner)dialog.findViewById(R.id.EvType);

		String Sex = "";
		String MS  = "";
		String PS  = "";
		int mage   = 0;

		//New Member
		if(NewMember.equals("1"))
		{
			EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode in('21','22','23','25')"));
		}

		//Old Member
		else
		{
			Cursor cur = C.ReadData("Select cast(((julianday(date('now'))-julianday(bdate))/365.25)as int)mage,sex,ms,ifnull(pstat,'')pstat from tTrans where Status='m' and vill || bari || hh || SNo='"+ (Vill+Bari+HH+SNo) +"'");
			cur.moveToFirst();
			while(!cur.isAfterLast())
			{
				Sex  = cur.getString(1).toString();
				MS   = cur.getString(2).toString();
				mage = Integer.valueOf(cur.getString(0));
				PS   = cur.getString(3).toString();
				cur.moveToNext();
			}
			cur.close();

			//1-Sex wise event
			if(Sex.equals("1"))
			{
				//MS
				if(MS.equals("30")) //Should not : 30,32,33,34
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','32','33','34','40','41','42','43','44','49','56','57','63')"));
				else if(MS.equals("31")) //Should not : 30,31
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','31','40','41','42','43','44','49','56','57')"));
				else if(MS.equals("33")) //Should not : 30,32,33,34
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','32','33','34','40','41','42','43','44','49','56','57')"));

				else
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','40','41','42','43','44','49','56','57')"));
			}
			else if(Sex.equals("2"))
			{
				//MS
				if(MS.equals("30")) //Should not : 30,32,33,34
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','32','33','34','41','42','43','44','56','57','63')"));
					//married and pregnant
				else if(MS.equals("31") & PS.equals("41")) //Should not : 30,31
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','31','41','43','44','56','57')"));
					//married and not pregnant
				else if(MS.equals("31") & !PS.equals("41")) //Should not : 30,31
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','31','42','43','44','56','57')"));
					//widowed but pregnant
				else if(MS.equals("33")) //Should not : 30,32,33,34
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','32','33','34','41','43','44','56','57')"));
					//widowed not pregnant
				else if(MS.equals("33")) //Should not : 30,32,33,34
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','32','33','34','41','42','43','44','56','57')"));

				else
					EvType.setAdapter(C.getArrayAdapter("Select distinct '  'EV from EventCode union SELECT (EvCode||'-'||EVName)Ev FROM EventCode where EvCode not in('20','21','22','23','24','25','26','30','43','44','56','57')"));
			}

		}

		//..................................................................................................................................
		final LinearLayout secPOR = (LinearLayout)dialog.findViewById(R.id.secPOR);
		final LinearLayout secEvDate = (LinearLayout)dialog.findViewById(R.id.secEvDate);

		EvDate  = (EditText)dialog.findViewById(R.id.EvDate);
		btnEvDate  = (ImageButton)dialog.findViewById(R.id.btnEvDate);
		btnLmpDt  = (ImageButton)dialog.findViewById(R.id.btnLmpDt);
		btnREvDT  = (ImageButton)dialog.findViewById(R.id.btnREvDT);

		btnEvDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnEvDate";
				showDialog(DATE_DIALOG);
			}
		});

		btnLmpDt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnLmpDt";
				showDialog(DATE_DIALOG);
			}
		});

		btnREvDT.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				VariableID = "btnREvDT";
				showDialog(DATE_DIALOG);
			}
		});


		final TextView lblCode=(TextView)dialog.findViewById(R.id.lblCode);
		final EditText txtCode = (EditText)dialog.findViewById(R.id.txtCode);
		final Spinner txtCodeList = (Spinner)dialog.findViewById(R.id.txtCodeList);

		final Spinner txtPOR = (Spinner)dialog.findViewById(R.id.txtPOR);
		final Spinner txt43 = (Spinner)dialog.findViewById(R.id.txt43);
		final Spinner txt44 = (Spinner)dialog.findViewById(R.id.txt44);

		final EditText SpNo = (EditText)dialog.findViewById(R.id.SpNo);
		final LinearLayout secSp = (LinearLayout)dialog.findViewById(R.id.secSp);
		final LinearLayout secDeliv = (LinearLayout)dialog.findViewById(R.id.secDeliv);
		final LinearLayout secLMP = (LinearLayout)dialog.findViewById(R.id.secLMP);
		txtLmpDt = (EditText)dialog.findViewById(R.id.LmpDt);
		final LinearLayout secReason = (LinearLayout)dialog.findViewById(R.id.secReason);
		final LinearLayout secQRAge = (LinearLayout)dialog.findViewById(R.id.secQRAge);
		final LinearLayout secQEvDT = (LinearLayout)dialog.findViewById(R.id.secQEvDT);
		final LinearLayout secQEvReason = (LinearLayout)dialog.findViewById(R.id.secQEvReason);

		final TextView lblQRAge = (TextView)dialog.findViewById(R.id.lblQRAge);
		final EditText txtRAge  = (EditText)dialog.findViewById(R.id.txtRAge);
		txtREvDT  = (EditText)dialog.findViewById(R.id.txtREvDT);
		final EditText txtRReason  = (EditText)dialog.findViewById(R.id.txtRReason);
		//---------------------------------------------------------------------
		secPOR.setVisibility(View.GONE);
		lblCode.setVisibility(View.GONE);
		txtCode.setVisibility(View.GONE);
		txtCodeList.setVisibility(View.GONE);
		secSp.setVisibility(View.GONE);
		secDeliv.setVisibility(View.GONE);
		secDelivType.setVisibility(View.GONE);
		secLMP.setVisibility(View.GONE);

		secReason.setVisibility(View.GONE);

		//---------------------------------------------------------------------
		txtQSNo.setText(SNo);
		lblName.setText(Name);
		txtQPNo.setText(PNo);
		//---------------------------------------------------------------------

		final String S = SNo;
		final String SEX = Sex;
		EvType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				EvDate.setText("");
				EvDate.setEnabled(true);
				lblCode.setText("Code");
				txtCode.setText("");
				txtCodeList.setSelection(0);
				SpNo.setText("");
				txtPOR.setSelection(0);
				txt43.setSelection(0);
				txt44.setSelection(0);
				txtLmpDt.setText("");
				txtRAge.setText("");
				txtREvDT.setText("");
				txtRReason.setText("");

				lblCode.setVisibility(View.GONE);
				txtCode.setVisibility(View.GONE);
				txtCodeList.setVisibility(View.GONE);
				secSp.setVisibility(View.GONE);
				secDeliv.setVisibility(View.GONE);
				secDelivType.setVisibility(View.GONE);
				secLMP.setVisibility(View.GONE);

				secPOR.setVisibility(View.GONE);
				secReason.setVisibility(View.GONE);
				secQRAge.setVisibility(View.GONE);
				secQEvDT.setVisibility(View.GONE);
				secQEvReason.setVisibility(View.GONE);

				//String selected = parentView.getItemAtPosition(position).toString();
				String ECode = EvType.getSelectedItem().toString().substring(0, 2);
				if(ECode.equals("21") | ECode.equals("31") | ECode.equals("32") | ECode.equals("33") | ECode.equals("34"))
				{
					if(ECode.equals("21"))
					{
						secReason.setVisibility(View.VISIBLE);
						secQRAge.setVisibility(View.VISIBLE);
						secQEvDT.setVisibility(View.VISIBLE);
						secQEvReason.setVisibility(View.VISIBLE);
						lblQRAge.setText("Reason/Spouse's Age");
					}
					else
					{
						secReason.setVisibility(View.VISIBLE);
						secQRAge.setVisibility(View.VISIBLE);
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
						lblQRAge.setText("Spouse's Age");
					}
				}
				else if(ECode.equals("22"))
				{
					EvDate.setEnabled(false);
					txtQPNo.setText("");
					g.setPNo("");
					g.setEvDate("");
					g.setPrevHousehold("");

					MigrationForm(dialog,"52");
				}
				else if(ECode.equals("23"))
				{
					EvDate.setEnabled(false);
					txtQPNo.setText("");
					g.setPNo("");
					g.setEvDate("");
					g.setPrevHousehold("");

					MigrationForm(dialog,"53");
				}
				else if(ECode.equals("25"))
				{
					secEvDate.setVisibility(View.VISIBLE);
					String BD = C.ReturnSingleValue("select BD from PS where vill||bari||HH = '"+ (Vill+Bari+HH) +"'");
					EvDate.setText(Global.DateConvertDMY(BD));
					EvDate.setEnabled(false);
				}
				else if(ECode.equals("41"))
				{
					lblCode.setText("Phone No");
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					txtCodeList.setVisibility(View.GONE);
				}
				else if(ECode.equals("42"))
				{
					//search LMP date
					String LMP = C.ReturnSingleValue("Select LmpDt from tTrans where status='m' and vill||bari||hh||sno='"+ (Vill+Bari+HH+S) +"'");
					secPOR.setVisibility(View.VISIBLE);
					secDeliv.setVisibility(View.VISIBLE);
					secDelivType.setVisibility(View.VISIBLE);
					secLMP.setVisibility(View.VISIBLE);
					txtLmpDt.setText(Global.DateConvertDMY(LMP));
					txtLmpDt.setEnabled(false);
					txtPOR.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'OutResult from POR union SELECT OutResult FROM POR"));
					txt43.setAdapter(C.getArrayAdapterMultiline(STR_POP));

					//txt43.setAdapter(C.getArrayAdapter("Select distinct ' 'Place from POP union SELECT Place FROM POP"));

					txt44.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'Atten from POP union SELECT Atten FROM POA"));
				}
				else if(ECode.equals("61") | ECode.equals("62"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					txtCodeList.setVisibility(View.GONE);
				}
				else if(ECode.equals("63") & SEX.equals("1"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					//secSp.setVisibility(View.VISIBLE);
				}
				else if(ECode.equals("63") & SEX.equals("2"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.VISIBLE);
					//secSp.setVisibility(View.GONE);
				}

				else if(ECode.equals("64") | ECode.equals("71") | ECode.equals("72"))
				{
					lblCode.setVisibility(View.VISIBLE);
					txtCode.setVisibility(View.GONE);
					txtCodeList.setVisibility(View.VISIBLE);
					if(ECode.equals("64"))
						txtCodeList.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'Name union SELECT Name FROM RTH"));
					else if(ECode.equals("71"))
					{
						//txtCodeList.setAdapter(C.getArrayAdapter("Select distinct ' 'Name union SELECT Name FROM EDU"));
						txtCodeList.setAdapter(C.getArrayAdapter(STR_EDU));
					}
					else if(ECode.equals("72"))
						txtCodeList.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'Name union SELECT Name FROM OCP"));
				}
				if(ECode.equals("12") | ECode.equals("40") | ECode.equals("49"))
				{
					secEvDate.setVisibility(View.GONE);
				}
				else
				{
					secEvDate.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
  
    	/*
    	final EditText txtEvDate  = (EditText)dialog.findViewById(R.id.EvDate);
    	txtEvDate.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){	        	
            	if(txtEvDate.getText().length()!=0)
            	{    	        	
            		if(txtEvDate.getText().length()==2 | txtEvDate.getText().length()==5)
            		{
            			txtEvDate.setText(txtEvDate.getText().toString()+"/");
            		}
            	}
            }});
    	*/

		txtRAge.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				//secReason.setVisibility(View.GONE);

				//secQRAge.setVisibility(View.GONE);
				secQEvDT.setVisibility(View.GONE);
				secQEvReason.setVisibility(View.GONE);

				if(txtRAge.getText().length()!=0)
				{
					String ECode = Global.Left(EvType.getSelectedItem().toString(),2);
					if(Integer.valueOf(txtRAge.getText().toString())==77)
					{
						txtREvDT.setText("");
						txtRReason.setText("");
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
					}
					else if(ECode.equals("21"))
					{
						secQEvDT.setVisibility(View.VISIBLE);
						secQEvReason.setVisibility(View.VISIBLE);
					}
					else
					{
						txtREvDT.setText("");
						txtRReason.setText("");
						secQEvDT.setVisibility(View.GONE);
						secQEvReason.setVisibility(View.GONE);
					}

				}
			}});

		txtCode.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){
				if(txtCode.getText().toString().equals("00") & SEX.equals("1"))
				{
					secSp.setVisibility(View.VISIBLE);
				}
				else
				{
					secSp.setVisibility(View.GONE);
				}
			}});

		Button cmdEventSave = (Button)dialog.findViewById(R.id.cmdEventSave);
		cmdEventSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				SaveEvents(dialog);
			}
		});


		Button close = (Button)dialog.findViewById(R.id.close);
		close.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		Button cmdEV = (Button)dialog.findViewById(R.id.cmdEvent);
		cmdEV.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				ShowEventList(Vill,Bari,HH);
			}
		});

		dialog.show();
	}


	//***************************************************************************************************************************
	private void SaveEvents(Dialog d)
	{
		try
		{
			//Dialog Event Form
			//----------------------------------------------------------------------------------------------------------
			final EditText txtSNo    = (EditText)d.findViewById(R.id.txtQSNo);
			final Spinner EvType     = (Spinner)d.findViewById(R.id.EvType);
			final EditText txtEvDate = (EditText)d.findViewById(R.id.EvDate);
			final EditText txtCode   = (EditText)d.findViewById(R.id.txtCode);
			final Spinner txtCodeList= (Spinner)d.findViewById(R.id.txtCodeList);
			final EditText txtQPNo   = (EditText)d.findViewById(R.id.txtQPNo);
			final EditText txtSpNo   = (EditText)d.findViewById(R.id.SpNo);
			final Spinner txtPOR     = (Spinner)d.findViewById(R.id.txtPOR);
			final Spinner txt43      = (Spinner)d.findViewById(R.id.txt43);
			final Spinner txt44      = (Spinner)d.findViewById(R.id.txt44);
			final EditText txtLmpDt  = (EditText)d.findViewById(R.id.LmpDt);
			final EditText txtRAge   = (EditText)d.findViewById(R.id.txtRAge);
			final EditText txtREvDT  = (EditText)d.findViewById(R.id.txtREvDT);
			final EditText txtRReason = (EditText)d.findViewById(R.id.txtRReason);

			if(EvType.getSelectedItemPosition()==0)
			{
				Connection.MessageBox(MemberEvents.this, "লিস্ট থেকে সঠিক ইভেন্ট কোড সিলেক্ট করুন।");
				return;
			}
			//----------------------------------------------------------------------------------------------------------
			String Rnd    = g.getRoundNumber();
			String VDate  = g.CurrentDateYYYYMMDD;
			String Household = vill+bari+hhno;
			String SNo    = txtSNo.getText().toString();
			Integer ECode = Integer.parseInt(EvType.getSelectedItem().toString().substring(0, 2));
			String PNo    = txtQPNo.getText().toString();
			String EvDate = Global.DateConvertYMD(txtEvDate.getText().toString());
			String Code   = txtCode.getText().toString();
			String CodeList = "";
			String SpNo   = txtSpNo.getText().toString();
			String POR    = "";
			String POP    = "";
			String POA    = "";
			String LmpDt  = txtLmpDt.getText().toString().length()==0?"":Global.DateConvertYMD(txtLmpDt.getText().toString());

			String RAge    = txtRAge.getText().toString();
			String REvDt   = txtREvDT.getText().toString().length()==0?"":Global.DateConvertYMD(txtREvDT.getText().toString());
			String RReason = txtRReason.getText().toString();
			//----------------------------------------------------------------------------------------------------------

			String PrevHH = g.getPrevHousehold();

			String MSNo = "";
			if(ECode == 25)
				MSNo = C.ReturnSingleValue("select MNo from PS where vill||bari||hh='"+ Household +"'");

			int age=0;
			String PStat="";
			String PMStatus = "";
			String SearchLMP = "";
			String PMNo = "";
			String PFNo = "";
			String Sex = "";
			String PRth = "";
			String PEdu = "";
			String POcp = "";

			String sex="";
			String name="";
			String sp1="";
			String sp2="";
			String sp3="";
			String sp4="";
			String endate = "";
			Cursor m = C.ReadData("Select rth,sex,ms,mono,fano,pstat,ifnull(lmpdt,'')lmpdt,edu,ocp,sp1,cast((julianday(date('now'))-julianday(bdate))/365.25 as int)age,PStat,endate,name from tTrans where status='m' and Vill||Bari||HH='"+ Household +"' and SNo='"+ SNo +"'");
			m.moveToFirst();
			while(!m.isAfterLast())
			{
				PRth = m.getString(0).toString();
				Sex  = m.getString(1).toString();
				PMStatus = m.getString(2).toString();
				PMNo = m.getString(3).toString();
				PFNo = m.getString(4).toString();
				SearchLMP = m.getString(6).toString();
				PEdu = m.getString(7).toString();
				POcp =m.getString(8).toString();
				sp1  =m.getString(9).toString();
				age  = Integer.parseInt(m.getString(10).toString());
				PStat  =m.getString(11).toString();
				endate =m.getString(12).toString();
				name   =m.getString(13).toString();
				m.moveToNext();
			}
			m.close();

			//----------------------------------------------------------------------------------------------------------------------------
			String EDT = Global.DateValidate(txtEvDate.getText().toString());
			SimpleDateFormat evdateformat = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.
			SimpleDateFormat endateformat = new SimpleDateFormat("yyyy-MM-dd"); // Make sure user insert date into edittext in this format.

			//event date
			Date evd;
			if(ECode == 12 | ECode == 40 | ECode == 49)
				evd = evdateformat.parse(Global.DateConvertDMY(VDate));
			else
				evd = evdateformat.parse(txtEvDate.getText().toString());

			//Event date should be greater than the entry date 
			if(ECode != 41 & ECode != 42  & ECode != 21 & ECode != 22 & ECode !=23 & ECode != 25 & ECode != 31 & ECode != 32 & ECode != 33 & ECode != 34 & ECode != 71)
			{
				//entry date
				Date end = endateformat.parse(endate);
				if(evd.before(end))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য খানায় যে দিন এন্ট্রি হয়েছে["+ Global.DateConvertDMY(endate) +"] তার আগে ইভেন্ট এর তারিখ হতে পারে না।");
					return;
				}
			}
			//----------------------------------------------------------------------------------------------------------------------------


			if (ECode == 12)
			{

			}
			else if ((ECode >= 20 & ECode <= 25) | (ECode >= 30 & ECode <= 34) | (ECode >= 51 & ECode <= 56))
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT);
					return;
				}

				//SimpleDateFormat evdateformat = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into edittext in this format.
				//SimpleDateFormat endateformat = new SimpleDateFormat("yyyy-MM-dd"); // Make sure user insert date into edittext in this format.
				//Date evd = evdateformat.parse(txtEvDate.getText().toString());
				//Date end = endateformat.parse(endate);
				//if(evd.before(end))
				//{
				//	Connection.MessageBox(MemberEvents.this, "সদস্য খানায় যে দিন এন্ট্রি হয়েছে["+ Global.DateConvertDMY(endate) +"] তার আগে ইভেন্ট এর তারিখ হতে পারে না।");
				//	return;
				//}


				//migration - in
				//-----------------------------------------------------------------------------------------------
				if(ECode == 21)
				{
					String ED = Global.DateValidate(txtREvDT.getText().toString());
					if(txtRAge.getText().toString().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "Reason/Spouse's age খালি রাখা যাবে না।");
						return;
					}
					else if(!txtRAge.getText().toString().equals("77") & ED.length()!=0)
					{
						Connection.MessageBox(MemberEvents.this, ED);
						return;
					}
					else if(!txtRAge.getText().toString().equals("77") &
							(!txtRReason.getText().toString().equals("31") &
									!txtRReason.getText().toString().equals("32") &
									!txtRReason.getText().toString().equals("33") &
									!txtRReason.getText().toString().equals("34")))
					{
						Connection.MessageBox(MemberEvents.this, "সদস্য কি কারনে এ খানায় আসল সেটি অবশ্যই ৩১/৩২/৩৩/৩৪ হতে হবে।");
						return;
					}
					//**reason date should be less than or equal in migration date
				}

				//marital status
				//-----------------------------------------------------------------------------------------------
				else if((ECode >= 31 & ECode <= 34))
				{
					if(age < 10)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ১০ এর কম হলে ইভেন্ট ৩১,৩২,৩৩,৩৪ হতে পারে না।"); return;
					}
					else if(txtRAge.getText().toString().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর বয়স খালি রাখা যাবে না।");
						return;
					}
					else if(Integer.parseInt(txtRAge.getText().toString()) < 10)
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর বয়স অবশ্যই ১০ বছরের সমান/বেশী হতে হবে।");
						return;
					}
					//event date should be equal/greater last occured event date
					//String LstEvDate = C.ReturnSingleValue("");
				}

			}

			else if (ECode == 40 | ECode == 49)
			{
				if(Sex.equals("1"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য অবশ্যই মহিলা হতে হবে।"); return;
				}
				else if(!PMStatus.equals("31"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য অবশ্যই বিবাহিত হতে হবে।"); return;
				}
				else if(age < 10 | age > 49)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ১০ এর কম অথবা ৪৯ এর বেশী হলে ইভেন্ট ৪০/৪৯ প্রযোজ্য নয়।"); return;
				}
				else if(PStat.equals("41"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য বর্তমানে গর্ভবতী, ইভেন্ট ৪০/৪৯ প্রযোজ্য নয়।"); return;
				}
			}

			else if (ECode == 41)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(Sex.equals("1"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য অবশ্যই মহিলা হতে হবে।"); return;
				}
				else if(!PMStatus.equals("31"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য অবশ্যই বিবাহিত হতে হবে।"); return;
				}
				else if(age < 10)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স অবশ্যই ১০ বছরের বেশী হতে হবে।"); return;
				}

				//difference between lmp and visit date should be equal or greater than 40 days
			}

			else if (ECode == 42)
			{
				String LMPDT = Global.DateValidate(txtLmpDt.getText().toString());
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(LMPDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, "Invalid LMP date."); return;
				}
				else if(!PStat.equals("41"))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্য অবশ্যই গর্ভবতী হতে হবে।"); return;
				}
				else if(txtPOR.getSelectedItemPosition()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক গর্ভের ফলাফল সিলেক্ট করুন।");
					return;
				}
				else if(txt43.getSelectedItemPosition()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক প্রসবের স্থান সিলেক্ট করুন।");
					return;
				}
				else if(txt44.getSelectedItemPosition()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক প্রসবের সময় উপস্থিত ব্যক্তি/সহায়তা কারী সিলেক্ট করুন।");
					return;
				}
				else if(spnDelType.getSelectedItemPosition()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক প্রসবের ধরণ সিলেক্ট করুন।");
					spnDelType.requestFocus();
					return;
				}

				//21 may 2016
				//difference between LMP and EDD Check
				int outcode_difference = Global.DateDifferenceDays(txtEvDate.getText().toString(),txtLmpDt.getText().toString());
				int outcome_result 	   = Integer.valueOf(Global.Left(txtPOR.getSelectedItem().toString(),2));

				if(outcode_difference < 0)
				{
					Connection.MessageBox(MemberEvents.this, "প্রসবের তারিখ অবশ্যই LMP এর তারিখের বেশী হতে হবে।");
					return;
				}
				else if (outcome_result==11 | outcome_result==12 | outcome_result==21 | outcome_result==22 | outcome_result==23 | outcome_result==31 | outcome_result==32 | outcome_result==33 | outcome_result==34)
				{
					if(outcode_difference < 180) {
						Connection.MessageBox(MemberEvents.this, "LMP এবং প্রসবের তারিখের পার্থক্য ১৮০ দিনের বেশী হতে হবে।");
						return;
					}
					else if(outcode_difference > 310) {
						Connection.MessageBox(MemberEvents.this, "LMP এবং প্রসবের তারিখের পার্থক্য অবশ্যই ৩১০ দিনের বেশী হতে পারে না।");
						return;
					}
				}
				else if (outcome_result==1 | outcome_result==2)
				{
					if(outcode_difference < 42) {
						Connection.MessageBox(MemberEvents.this, "LMP এবং প্রসবের তারিখের পার্থক্য ৪২ দিনের কম হতে পারে না।");
						return;
					}
					else if(outcode_difference > 180) {
						Connection.MessageBox(MemberEvents.this, "LMP এবং প্রসবের তারিখের পার্থক্য ১৮০ দিনের বেশী হতে পারে না।");
						return;
					}
				}

				POR = Global.Left(txtPOR.getSelectedItem().toString(),2);
				POP = Global.Left(txt43.getSelectedItem().toString(),2);
				POA = Global.Left(txt44.getSelectedItem().toString(),1);

				/*else
				{
					POR = Global.Left(txtPOR.getSelectedItem().toString(),2);
					POP = Global.Left(txt43.getSelectedItem().toString(),2);
					POA = Global.Left(txt44.getSelectedItem().toString(),1);
				}*/
			}
			else if (ECode == 25)
			{

			}
			else if(ECode == 61)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(ECode == 61 & Code.length() == 0)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের মায়ের সঠিক সিরিয়াল নাম্বার লিখুন।");return;
				}
				else if(ECode == 61 & (Code.equals("0") | Code.equals("00")))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের মায়ের সিরিয়াল নাম্বার 00 হবে না।");return;
				}
				if(SNo.equals(Code))
				{
					Connection.MessageBox(MemberEvents.this, "মায়ের সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
					return;
				}
			}
			else if(ECode == 62)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(ECode == 62 & Code.length() == 0)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বাবার সঠিক সিরিয়াল নাম্বার লিখুন।");return;
				}
				else if(ECode == 62 & (Code.equals("0") | Code.equals("00")))
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বাবার সিরিয়াল নাম্বার 00 হবে না।");return;
				}
				if(SNo.equals(Code))
				{
					Connection.MessageBox(MemberEvents.this, "বাবার সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
					return;
				}

			}


			else if (ECode == 63)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(Code.length()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক স্বামী/স্ত্রী এর সিরিয়াল নাম্বার লিখুন(Code)।");
					return;
				}
				else if(Code.length()!=2)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক স্বামী/স্ত্রী এর সিরিয়াল নাম্বার ২ সংখ্যা হতে হবে(Code)।");
					return;
				}
				else if(Sex.equals("1") & Code.equals("00") & SpNo.length()==0)
				{
					Connection.MessageBox(MemberEvents.this, "সঠিক স্ত্রী এর সিরিয়াল নাম্বার লিখুন(Spouse's No)।");
					return;
				}
				else if(age < 10)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স অবশ্যই ১০ বছরের বেশী হতে হবে।");
					return;
				}

				//spouse's is not available in the member list
				if(!Code.equals("00") & !C.Existence("select vill from ttrans where status='m' and vill||bari||hh='"+ Household +"' and SNo='"+ Code +"'"))
				{
					Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার "+ Code +" এই খানার তালিকায় নেই।");
					return;
				}
				else if(C.Existence("select vill from ttrans where status='m' and vill||bari||hh='"+ Household +"' and SNo='"+ SNo +"' and (Sp1='"+ Code +"' or Sp2='"+ Code +"' or Sp3='"+ Code +"' or Sp4='"+ Code +"')"))
				{
					Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার "+ Code +" পূর্বের নাম্বার এর সমান হবে না।");
					return;
				}

			}


			else if (ECode == 64)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(ECode == 64 & txtCodeList.getSelectedItemPosition() == 0)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের সঠিক সম্পর্ক কি লিখুন।");return;
				}
				else
				{
					CodeList = Global.Left(txtCodeList.getSelectedItem().toString(),2);
				}

				if(CodeList.equals("01") & C.Existence("Select * from tTrans where hh='"+ Household +"' and Status='m' and Rth='01' and (ExType is null or length(ExType)=0)")==true)
				{
					Connection.MessageBox(MemberEvents.this, "একই খানায় ২ জন খানা প্রধান থাকতে পারে না।");
					return;
				}
				else if(CodeList.equals("01") & age < 10)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স "+ age +",খানা প্রধান হতে হলে বয়স ১০ বছরের সমান/বেশী হতে হবে।");
					return;
				}


			}
			else if(ECode == 71)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(ECode == 71 & txtCodeList.getSelectedItemPosition() == 0)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের সঠিক শিক্ষাগত যোগ্যতা কি লিখুন।");return;
				}
				else
				{
					CodeList = Global.Left(txtCodeList.getSelectedItem().toString(),2);
				}

				//education
				int edu = Integer.parseInt(CodeList);
				if( edu >= 1 & edu != 99)
				{
					if((edu>=1 & edu<=18) & Math.abs(age-4) < edu)
					{
						Connection.MessageBox(MemberEvents.this, "শিক্ষার কোড "+ Math.abs(age-4) +" এর সমান অথবা কম হতে হবে।");
						return;
					}
					else if(edu == 11 || edu == 13 || (edu >= 18 & edu <= 29))
					{
						Connection.MessageBox(MemberEvents.this, "শিক্ষার কোড অবশ্যই 00-10,12,14,15,16,17,30,31,32,33,34,35,99 হতে হবে।");
						return;
					}
					//age should not have < 4 years
					//else if(age < 4 & edu > 0)
					else if(age < 4 & (edu>=1 & edu<=18))
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স শিক্ষার জন্য প্রযোজ্য নয়।");
						return;
					}
					//else if(age < 4 & (edu>=30 & edu<=35))
					//{
					//        Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স শিক্ষার জন্য প্রযোজ্য নয়।");
					//        return;                                                 
					//}

					//education should be consistent with age
					else if((edu>=1 & edu<=18) & (age - edu) < 4)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের বয়সের ("+ age +" বছর) সাথে শিক্ষার কোড "+ edu +" সঠিক নয়।");
						return;
					}
				}

			}


			else if(ECode == 72)
			{
				if(EDT.length()!=0)
				{
					Connection.MessageBox(MemberEvents.this, EDT); return;
				}
				else if(ECode == 72 & txtCodeList.getSelectedItemPosition() == 0)
				{
					Connection.MessageBox(MemberEvents.this, "সদস্যের সঠিক পেশা কি লিখুন।");return;
				}
				else
				{
					CodeList = Global.Left(txtCodeList.getSelectedItem().toString(),2);
				}

				int edu = Integer.valueOf(PEdu);
				int ocp = Integer.valueOf(CodeList);

				//occupation		            
				if(ocp >= 1)
				{
					if(age < 12)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ১২ বছরের কম হলে ইভেন্ট ৭২ প্রযোজ্য নয়।");
						return;
					}
					//check education code should be greater 12 for occupation code 34
					else if(edu < 12 & ocp == 34)
					{
						Connection.MessageBox(MemberEvents.this, "পেশার কোড ৩৪ এর জন্য শিক্ষার কোড অবশ্যই ১২ হতে হবে।");
						return;
					}
					//check education code should be greater 1 for occupation code 32
					/*else if(edu < 1 & ocp == 32) //stop on 25 jan 2018
					{
						Connection.MessageBox(MemberEvents.this, "পেশার কোড ৩২ এর জন্য সদস্য অবশ্যই শিক্ষিত হতে হবে।");
						return;
					}*/
					//student
					else if(ocp == 2 & edu == 0 & age > 30)
					{
						Connection.MessageBox(MemberEvents.this, "পেশার কোড ০২ এর জন্য শিক্ষার কোড ০০ সঠিক নয়।");
						return;
					}
					//age>40, ocp should not 02
					else if(ocp == 2 & age > 40)
					{
						Connection.MessageBox(MemberEvents.this, "যাদের বয়স ৪০ বছরের বেশী তাদের পেশার কোড ০২ হতে পারে না।");
						return;
					}
					//check occupation event=03 for woman
					else if(ocp == 3 & sex.equals("1"))
					{
						Connection.MessageBox(MemberEvents.this, "পুরুষ লোকের পেশা ০৩ হতে পারে না।");
						return;
					}
					//Retired person but age < 30
					else if(ocp == 6 & age < 30)
					{
						Connection.MessageBox(MemberEvents.this, "বয়স ৩০ এর কম হলে পেশা অবসরপ্রাপ্ত (চাকুরি করেন না) হতে পারে না।");
						return;
					}
					//Occupation (31), but education = 00
					else if(ocp == 31 & edu == 0)
					{
						Connection.MessageBox(MemberEvents.this, "পেশা মেধাসম্পন্ন (হাতের কাজ নয়) এর জন্য শিক্ষা ০০ হতে পারে না।");
						return;
					}
					//Occupation (34), but education < 10
					else if(ocp == 34 & edu < 10)
					{
						Connection.MessageBox(MemberEvents.this, "পেশা পেশাদার-ডাক্তার,কৃষি-কর্মকর্তা,শিক্ষক,ইঞ্জিনিয়ার(মেধাসম্পন্ন-হাতের কাজ নয়) এর জন্য শিক্ষা ১০ এর কম হতে পারে না।");
						return;
					}
				}
			}






			String ED = "";
			if (ECode==12 | ECode == 40 | ECode == 49)
			{
				ED = VDate;
			}
			else
			{
				ED = EvDate;
			}

			//(Temporary Table) check the information is available or not for those(household, sno, round no, event type,event date) combination in Transaction and event table
			if(C.Existence("Select * from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ ECode.toString() +"' and EvDate='"+ ED +"' and Rnd='"+ Rnd +"'"))
			{
				Connection.MessageBox(MemberEvents.this, "ইভেন্ট কোড ("+ ECode +") রউন্ড নাম্বার "+ Rnd +" এ ঘটানো হয়েছে।");
				return;
			}

			//(Event Table) check the information is available or not for those(household, sno, round no, event type,event date) combination in Transaction and event table
			else if(C.Existence("Select * from Events where vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ ECode.toString() +"' and EvDate='"+ ED +"' and Rnd='"+ Rnd +"'"))
			{
				Connection.MessageBox(MemberEvents.this, "ইভেন্ট কোড ("+ ECode +") রউন্ড নাম্বার "+ Rnd +" এ ঘটানো হয়েছে।");
				return;
			}


			//if all are ok then save event info. and call Member form
			else
			{
				String SQL = "";

				//--Save Event Information-----------------------------------------------------------------
				SQL = "Insert into tTrans";
				SQL = SQL + "(Status,Vill,Bari,HH,PNo,SNo,EvType,EvDate,";
				if(ECode == 21)
				{
					SQL = SQL + "Info1,";
					SQL = SQL + "Info2,";
					SQL = SQL + "Info4,";
				}
				else if(ECode == 22 | ECode == 23 | ECode == 25 | ECode == 41)
				{
					SQL = SQL + "Info1,";
				}
				else if(ECode == 31 | ECode == 32 | ECode == 33 | ECode == 34)
				{
					SQL = SQL + "Info1,";
					SQL = SQL + "Info2,";
					SQL = SQL + "Info3,";
				}
				else if(ECode == 61 | ECode == 62 | ECode == 63 | ECode == 64 | ECode == 71 | ECode == 72)
				{
					SQL = SQL + "Info1,";
					SQL = SQL + "Info2,";
				}
				else if(ECode == 42)
				{
					SQL = SQL + "Info1,";
					SQL = SQL + "Info2,";
					SQL = SQL + "Info3,";
					SQL = SQL + "Info4,";
					SQL = SQL + "Info5,";
				}

				SQL = SQL + "VDate,";
				SQL = SQL + "Rnd, Upload)";
				SQL = SQL + "Values(";
				SQL = SQL + "'e',";
				SQL = SQL + "'"+ vill +"',";
				SQL = SQL + "'"+ bari +"',";
				SQL = SQL + "'"+ hhno +"',";
				SQL = SQL + "'"+ PNo +"',";
				SQL = SQL + "'"+ SNo +"',";
				SQL = SQL + "'"+ ECode.toString() +"',";

				//-------------------------------------------------------
				if(ECode == 11 | ECode == 12 | ECode == 30 | ECode == 40 | ECode == 49)
				{
					SQL = SQL + "'"+ VDate +"',";
				}
				else if(ECode == 21)
				{
					//Reason of migration in
					SQL = SQL + "'"+ EvDate +"',";

					//if 77 then only reason other wise age, reason, date
					SQL = SQL + "'"+ RAge +"',";
					SQL = SQL + "'"+ RReason +"',";
					SQL = SQL + "'"+ REvDt +"',";
				}
				else if(ECode == 22 | ECode == 23)
				{
					//Previous household no
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ PrevHH +"',";
					//****************************
				}

				else if(ECode == 25)
				{
					//Mother serial no
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ MSNo +"',";
					//****************************
				}

				//Current & Previous Marital Status
				else if(ECode == 31 | ECode == 32 | ECode == 33 |ECode == 34)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ ECode +"',";
					SQL = SQL + "'"+ PMStatus +"',";
					//****************************
					SQL = SQL + "'"+ RAge +"',";
				}

				//Pregnancy information
				else if(ECode == 41)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ Code +"',";
				}
				else if(ECode == 42)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ POR +"',";
					SQL = SQL + "'"+ POP +"',";
					SQL = SQL + "'"+ POA +"',";
					SQL = SQL + "'"+ LmpDt +"',";
					SQL = SQL + "'"+ spnDelType.getSelectedItem().toString().split("-")[0] +"',";
				}

				//Current & Previous Mother Serial No
				else if(ECode == 61)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ Code +"',";
					SQL = SQL + "'"+ PMNo +"',";
					//****************************
				}

				//Current & previous Father Serial No
				else if(ECode == 62)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ Code +"',";
					SQL = SQL + "'"+ PFNo +"',";

				}

				//Current & previous Husband Serial No
				else if(ECode == 63)
				{
					SQL = SQL + "'"+ EvDate +"',";

					if(Sex.equals("1"))
					{
						SQL = SQL + "'"+ Code +"',";
						SQL = SQL + "'"+ SpNo +"',";
					}
					else
					{
						SQL = SQL + "'"+ Code +"',";
						SQL = SQL + "'"+ sp1 +"',";
					}
				}

				//Current & previous relation to Head code
				else if(ECode == 64)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ CodeList +"',";
					SQL = SQL + "'"+ PRth +"',";
					//****************************
				}

				//Current & previous education code
				else if(ECode == 71)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ CodeList +"',";
					SQL = SQL + "'"+ PEdu +"',";
					//****************************
				}

				//Current & previous Occupation code
				else if(ECode == 72)
				{
					SQL = SQL + "'"+ EvDate +"',";
					SQL = SQL + "'"+ CodeList +"',";
					SQL = SQL + "'"+ POcp +"',";
					//****************************
				}
				else
				{
					SQL = SQL + "'"+ EvDate +"',";
				}

				//-------------------------------------------------------
				SQL = SQL + "'"+ VDate +"',";
				SQL = SQL + "'"+ Rnd +"','2')";
				//-------------------------------------------------------------------

				//consider new registration (21,22,23,25): will save combinely with member form
				if(ECode == 12 | ECode == 21 | ECode == 22 | ECode == 23 | ECode == 25)
				{
					g.setSQLforNewRegis(SQL);
				}
				else
				{
					C.Save(SQL);
				}


				//===================================================================
				//Marital Status update
				if(ECode == 31 | ECode == 32 | ECode == 33 | ECode == 34)
					C.Save("Update tTrans set MS='"+ ECode +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Spouse's Serial No update
					//if(ECode == 31 | ECode == 32 | ECode == 33 | ECode == 34)
					//'Conn.Execute "Update tTrans set sp1='00',sp2='',sp3='',sp4='' where HH='" & HouseHold & "' and Status='m' and SNo='" & SNo.Text & "'"


					//Not pregnant
				else if(ECode == 40)
					C.Save("Update tTrans set PStat='"+ ECode +"',LmpDt='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Pregnant
				else if(ECode == 41)
					C.Save("Update tTrans set PStat='"+ ECode +"',LmpDt='"+ EvDate +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Pregnancy outcome result
				else if(ECode == 42)
					C.Save("Update tTrans set PStat='',LmpDt='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Pregnancy information not collected
				else if(ECode == 49)
					C.Save("Update tTrans set PStat='"+ ECode +"',LmpDt='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Exit from household
				else if(ECode == 51 | ECode == 52 | ECode == 53 | ECode == 55 | ECode == 56)
					C.Save("Update tTrans set ExType='"+ ECode +"',ExDate='"+ EvDate +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Possible migration-out
				else if(ECode == 54)
					C.Save("Update tTrans set PosMig='"+ ECode +"',PosMigDate='"+ EvDate +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Mother's serial no update
				else if(ECode == 61)
					C.Save("Update tTrans set Mono='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Father's serial no update
				else if(ECode == 62)
					C.Save("Update tTrans set Fano='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Husband's serial no update
				else if(ECode == 63)
				{
					//Update Husband's serial no
					Cursor cur = C.ReadData("Select sex,sp1,sp2,sp3,sp4 from tTrans where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
					cur.moveToFirst();
					while(!cur.isAfterLast())
					{
						sex = cur.getString(0).toString();
						sp1 = cur.getString(1).toString();
						sp2 = cur.getString(2).toString();
						sp3 = cur.getString(3).toString();
						sp4 = cur.getString(4).toString();

						//Male
						if (sex.equals("1") & Integer.valueOf(Code) == 0)
						{
							if(sp1.equals(SpNo))
							{
								C.Save("Update tTrans Set Sp1='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp2.equals(SpNo))
							{
								C.Save("Update tTrans Set Sp2='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp3.equals(SpNo))
							{
								C.Save("Update tTrans Set Sp3='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp4.equals(SpNo))
							{
								C.Save("Update tTrans Set Sp4='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
						}
						else if (sex.equals("1") & Integer.valueOf(Code) != 0)
						{
							if(sp1.length()==0 | sp1.equals("0") | sp1.equals("00"))
							{
								C.Save("Update tTrans Set Sp1='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp2.length()==0 | sp2.equals("0") | sp2.equals("00"))
							{
								C.Save("Update tTrans Set Sp2='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp3.length()==0 | sp3.equals("0") | sp3.equals("00"))
							{
								C.Save("Update tTrans Set Sp3='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
							else if(sp4.length()==0 | sp4.equals("0") | sp4.equals("00"))
							{
								C.Save("Update tTrans Set Sp4='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
							}
						}

						//Female
						else
						{
							C.Save("Update tTrans Set Sp1='"+ Code +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
						}

						cur.moveToNext();
					}
					cur.close();

					//-If more than one spouses found and sp1 is blank---------------------------------------------------------
					Cursor cur1 = C.ReadData("Select sex,sp1,sp2,sp3,sp4 from tTrans where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
					cur1.moveToFirst();
					while(!cur1.isAfterLast())
					{
						sex = cur1.getString(0).toString();
						sp1 = cur1.getString(1).toString();
						sp2 = cur1.getString(2).toString();
						sp3 = cur1.getString(3).toString();
						sp4 = cur1.getString(4).toString();


						if((sp1.length()==0 | sp1.equals("0") | sp1.equals("00")) & (!sp2.equals("0") | !sp2.equals("00")))
						{
							C.Save("Update tTrans Set Sp1=sp2,sp2=sp3,sp3=sp4,sp4='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
						}
						else if(sp2.length()==0 | sp2.equals("0") | sp2.equals("00"))
						{
							C.Save("Update tTrans Set sp2=sp3,sp3=sp4,sp4='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
						}
						else if(sp3.length()==0 | sp3.equals("0") | sp3.equals("00"))
						{
							C.Save("Update tTrans Set sp3=sp4,sp4='' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");
						}

						cur1.moveToNext();
					}
					cur1.close();
				}



				//Relation to head update
				else if(ECode == 64)
					C.Save("Update tTrans Set Rth='"+ CodeList +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Education update
				else if(ECode == 71)
					C.Save("Update tTrans Set Edu='"+ CodeList +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");

					//Occupation update
				else if(ECode == 72)
					C.Save("Update tTrans Set OCP='"+ CodeList +"' where Vill||Bari||HH='"+ Household +"' and Status='m' and SNo='"+ SNo +"'");


				//===================================================================
				//Call member form
				if (ECode >= 20 & ECode <= 23)
				{
					d.dismiss();
					MemberForm(Household,SNo,txtQPNo.getText().toString(),ECode.toString(),EvDate,"n");
				}
				else if(ECode == 12)
				{
					d.dismiss();
					MemberForm(Household,SNo,txtQPNo.getText().toString(),ECode.toString(),EvDate,"o");
				}
				else if(ECode == 42)
				{
					Integer TC = 0;
					if(POR.equals("12") | POR.equals("22") | POR.equals("32"))
					{
						TC = 1;
					}
					else if(POR.equals("23") | POR.equals("33"))
					{
						TC = 2;
					}
					else if(POR.equals("34"))
					{
						TC = 3;
					}

					C.Save("Delete from PS where vill||bari||HH='"+ Household +"'");
					C.Save("Insert into PS(Vill,Bari,HH,BD,MNo,TChild)values('"+ vill +"','"+ bari +"','"+ hhno +"','"+ EvDate +"','"+ SNo +"',"+ TC +")");

					d.dismiss();
					//MemberForm(Household,SNo,ECode.toString(),EvDate);
				}
				else if(ECode == 25)
				{
					d.dismiss();
					MemberForm(Household,SNo,txtQPNo.getText().toString(),ECode.toString(),EvDate,"n");

				}
				else if(ECode == 51 | ECode == 52 | ECode == 53 | ECode == 54 | ECode == 55)
				{
					d.dismiss();
				}
                             /*
                             //Death Reporting Form
                             else if(ECode == 55)
                             {
                                 g.setmemSlNo(SNo);
                                 g.setPNo( txtQPNo.getText().toString());
                                 d.dismiss();
                                 Intent f11 = new Intent(getApplicationContext(),Death.class);
                                 startActivity(f11);                                    
                             }
		             */
				else
				{
					txtEvDate.setText(null);
					txtCode.setText(null);
					txtCodeList.setSelection(0);
					txtSpNo.setText(null);
					txt43.setSelection(0);
					txt44.setSelection(0);

					txtLmpDt.setText(null);
					txtRAge.setText(null);

					EvType.setSelection(0);
				}

				DataRetrieve(vill+bari+hhno,false,"active");
			}

		}
		catch(Exception ex)
		{
			Connection.MessageBox(MemberEvents.this, ex.getMessage());
			return;
		}
	}

	private String LastEvDate(String Household, String SNo, String EventCode)
	{
		String SQL = "";
		if(EventCode.equals("31") | EventCode.equals("32") | EventCode.equals("33") | EventCode.equals("34"))
		{
			SQL  = " select evdate from events where evtype in('31','32','33','34') and vill||bari||hh='"+ Household +"' and sno='"+ SNo +"' union";
			SQL += " select evdate from ttrans where evtype in('31','32','33','34') and vill||bari||hh='"+ Household +"' and sno='"+ SNo +"' and status='e'";
			SQL += " order by evdate desc limit 1";
		}
		else if(EventCode.equals("41"))
		{

		}
		else if(EventCode.equals("51") | EventCode.equals("52") | EventCode.equals("53") | EventCode.equals("55") | EventCode.equals("57"))
		{
		}
		else if(EventCode.equals("61"))
		{

		}
		else if(EventCode.equals("62"))
		{

		}
		else if(EventCode.equals("63"))
		{

		}
		else if(EventCode.equals("64"))
		{

		}
		else if(EventCode.equals("71"))
		{

		}
		else if(EventCode.equals("72"))
		{

		}

		String LastEVDate = "";

		Cursor cur= C.ReadData(SQL);
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			LastEVDate = cur.getString(0);
			cur.moveToNext();
		}
		cur.close();

		return LastEVDate;
	}

	//***************************************************************************************************************************
	String PStat = "";
	String LmpDt = "";
	String PS    = "";
	String LMP   = "";
	private void MemberForm(final String household, final String memsl,String pno, final String entype,final String endate, String NewEdit)
	{
		try
		{
			final Dialog dialog = new Dialog(MemberEvents.this);
			dialog.setTitle("Member Form");
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.member);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);


			final EditText txtName;
			//final EditText txtBDate;
			final RadioGroup rdogrpSex;
			final RadioButton rdoSexM;
			final RadioButton rdoSexF;
			final Spinner spnRth;
			final EditText txtMono;
			final EditText txtFano;
			final Spinner spnMs;
			final TextView lblSp1;
			final TextView lblSp2;
			final EditText txtSp1;
			final EditText txtSp2;
			final Spinner spnEdu;
			final Spinner spnOcp;
			final Button btnMemberSaveContinue;
			final Button cmdMemberClose;
			final Button cmdEvCode = (Button)dialog.findViewById(R.id.cmdEvCode);
			final ImageButton btnBDate = (ImageButton)dialog.findViewById(R.id.btnBDate);
			btnBDate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					VariableID = "btnBDate";
					showDialog(DATE_DIALOG);
					}
			});

			cmdEvCode.setText(entype);

			txtName=(EditText)dialog.findViewById(R.id.txtName);
			txtBDate=(EditText)dialog.findViewById(R.id.txtBDate);
			rdogrpSex=(RadioGroup)dialog.findViewById(R.id.rdogrpSex);
			rdoSexM=(RadioButton)dialog.findViewById(R.id.rdoSexM);
			rdoSexF=(RadioButton)dialog.findViewById(R.id.rdoSexF);
			spnRth=(Spinner)dialog.findViewById(R.id.spnRth);
			txtMono=(EditText)dialog.findViewById(R.id.txtMono);
			txtFano=(EditText)dialog.findViewById(R.id.txtFano);
			spnMs=(Spinner)dialog.findViewById(R.id.spnMs);
			spnEdu=(Spinner)dialog.findViewById(R.id.spnEdu);

			lblSp1=(TextView)dialog.findViewById(R.id.VlblSp1);
			lblSp2=(TextView)dialog.findViewById(R.id.VlblSp2);
			txtSp1 =(EditText)dialog.findViewById(R.id.txtSp1);
			txtSp2 =(EditText)dialog.findViewById(R.id.txtSp2);
			spnOcp=(Spinner)dialog.findViewById(R.id.spnOcp);
			btnMemberSaveContinue=(Button)dialog.findViewById(R.id.cmdMemberSaveandContinue);
			cmdMemberClose=(Button)dialog.findViewById(R.id.cmdMemberClose);

			TextView lblHead = (TextView)dialog.findViewById(R.id.lblHead);
			lblHead.setText("Member Form: [Head:"+C.ReturnSingleValue("Select SNo||', '||Name from tTrans where status='m' and Rth='01' and vill||bari||hh='"+ household +"' and length(extype)=0")+"]");
			Spinner  spnMemList = (Spinner)dialog.findViewById(R.id.spnMemList);
			spnMemList.setAdapter(C.getArrayAdapter("Select ifnull('[Rth:'||Rth||']'||SNo||', '||Name,'')MemInfo from tTrans where status='m' and vill||bari||hh='"+ household +"' and length(extype)=0"));

			spnRth.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'Name union SELECT Name FROM RTH"));

			//spnEdu.setAdapter(C.getArrayAdapter("Select distinct ' 'Name union SELECT Name FROM EDU"));
			spnEdu.setAdapter(C.getArrayAdapterMultiline(STR_EDU));

			spnOcp.setAdapter(C.getArrayAdapterMultiline("Select distinct ' 'Name union SELECT Name FROM OCP"));

			ArrayAdapter adptrspnMs=ArrayAdapter.createFromResource(this, R.array.MeMlistMs, android.R.layout.simple_spinner_item);
			adptrspnMs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnMs.setAdapter(adptrspnMs);

			String TChild="0";


			if(entype.equals("25"))
			{
				Cursor cur = C.ReadData("Select * from PS where vill||bari||hh='"+ household +"'");
				cur.moveToFirst();
				while(!cur.isAfterLast())
				{
					txtName.setText("");
					txtBDate.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BD"))));
					txtBDate.setEnabled(false);
					txtMono.setText(cur.getString(cur.getColumnIndex("MNo")));
					txtFano.setText("");
					spnMs.setSelection(1);
					txtSp1.setText("");
					txtSp1.setEnabled(false);
					txtSp1.setText("");
					txtSp1.setEnabled(false);
					spnEdu.setSelection(1);
					spnEdu.setEnabled(false);
					spnOcp.setSelection(18);
					spnOcp.setEnabled(false);
					TChild = cur.getString(cur.getColumnIndex("MNo")).toString();
					cur.moveToNext();
				}
				cur.close();
			}


			if((entype.equals("22") | entype.equals("23")) & NewEdit.equals("n"))
			{
				C.Save("Delete from MigMember");
				//if(C.Existence("Select vill from Household where clust='"+ g.getClusterCode() +"' and vill='"+ g.getVillageCode() +"'"))
				if(C.Existence("Select vill from Member where pno='"+ pno +"' and ExDate='"+ endate +"'"))
				{
					if(entype.equals("22"))
						C.Save("Insert into MigMember(Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate) select Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate from Member where PNo='"+ pno +"' and ExType='52' and ExDate='"+ endate +"'");
					else if(entype.equals("23"))
						C.Save("Insert into MigMember(Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate) select Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate from Member where PNo='"+ pno +"' and ExType='53' and ExDate='"+ endate +"'");
				}

				//download from server if migration from outside cluster
				else
				{
					String S = "";
					if(entype.equals("22"))
						S  = "select Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate from Member where PNo='"+ pno +"' and ExType='52' and ExDate='"+ endate +"'";
					else if(entype.equals("23"))
						S  = "select Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate from Member where PNo='"+ pno +"' and ExType='53' and ExDate='"+ endate +"'";

					//C.DownloadMigData(S,"MigMember", "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate");
					Common.Connection CJson = new Common.Connection(MemberEvents.this);
					CJson.DownloadJSON(S,"MigMember", "Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload, PosMig, PosMigDate","Vill, Bari, Hh, Sno");
				}

				//Pregnancy History : 03 05 2016
				String V = household.substring( 0,3 );
				String B = household.substring( 3,7 );
				String H = household.substring( 7,9 );

				String SQL = " Insert into tTrans";
				SQL += " (Status,Vill,bari,Hh,Pno,Sno,Visit,MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut,";
				SQL += " Died, SDied, DDied, Abor, TAbor, TotPreg,VDate,Rnd)";
				SQL += " Select 'p','"+ V +"' Vill,'"+ B +"' bari,'"+ H +"' Hh, '"+ pno +"' Pno,'"+ memsl +"' sno,Visit,MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut,";
				SQL += " Died, SDied, DDied, Abor, TAbor, TotPreg,VDate,Rnd From Mig_PregHis where PNo = '"+ pno +"' order by cast(visit as int) limit 1";
				C.Save(SQL);

				//Immunization Information : 03 05 2016
				SQL = "Insert into ImmunizationTemp(Vill, Bari, HH, Sno, PNO, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource, IPV, IPVDT, IPVSource, fIPV1,fIPVDT1,fIPVSource1,fIPV2,fIPVDT2,fIPVSource2, VitaminA, VitaminADT, VitaminASource, EnDt, Upload)";
				SQL += "Select '"+ V +"' Vill,'"+ B +"' Bari,'"+ H +"' HH, '"+ memsl +"' Sno, '"+ pno +"' PNO, Status, BCG, BCGDT, BCGSource, Penta1, Penta1DT, Penta1Source, Penta2, Penta2DT, Penta2Source, Penta3, Penta3DT, Penta3Source, PCV1, PCV1DT, PCV1Source, PCV2, PCV2DT, PCV2Source, PCV3, PCV3DT, PCV3Source, OPV0, OPV0DT, OPV0Source, OPV1, OPV1DT, OPV1Source, OPV2, OPV2DT, OPV2Source, OPV3, OPV3DT, OPV3Source, OPV4, OPV4DT, OPV4Source, Measles, MeaslesDT, MeaslesSource, MR, MRDT, MRSource, Rota, RotaDT, RotaSource, MMR, MMRDT, MMRSource, Typhoid, TyphoidDT, TyphoidSource, Influ, InfluDT, InfluSource, HepaA, HepaADT, HepaASource, ChickenPox, ChickenPoxDT, ChickenPoxSource, Rabies, RabiesDT, RabiesSource, IPV, IPVDT, IPVSource, fIPV1,fIPVDT1,fIPVSource1,fIPV2,fIPVDT2,fIPVSource2, VitaminA, VitaminADT, VitaminASource, EnDt, Upload from Mig_Immunization";
				SQL += " where PNo = '"+ pno +"' order by cast(status as int) limit 1";
				C.Save(SQL);

				Cursor cur=null;
				cur = C.ReadData("Select * from MigMember where PNo='"+ pno +"'");
				cur.moveToFirst();
				while(!cur.isAfterLast())
				{
					txtName.setText(cur.getString(cur.getColumnIndex("Name")));
					txtBDate.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BDate"))));
					txtBDate.setEnabled(false);
					if(cur.getString(cur.getColumnIndex("Sex")).equals("1"))
					{
						rdoSexM.setChecked(true);
					}
					else if(cur.getString(cur.getColumnIndex("Sex")).equals("2"))
					{
						rdoSexF.setChecked(true);
					}
					rdogrpSex.setEnabled(false);
					rdoSexM.setEnabled(false);
					rdoSexF.setEnabled(false);

					txtMono.setText("");
					txtFano.setText("");

					if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==30)
					{
						spnMs.setSelection(1);
					}
					else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==31)
					{
						spnMs.setSelection(2);
					}
					else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==32)
					{
						spnMs.setSelection(3);
					}
					else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==33)
					{
						spnMs.setSelection(4);
					}
					else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==34)
					{
						spnMs.setSelection(5);
					}
					spnMs.setEnabled(false);

					//pstat
					PStat = cur.getString(cur.getColumnIndex("Pstat")).equals("41")?"41":"";
					//lmp
					LmpDt = PStat.equals("41")?cur.getString(cur.getColumnIndex("LmpDt")):"";
	              	
	              	/*
	              	if(cur.getString(cur.getColumnIndex("Edu")).trim().length()==0)
	            	{
	            		spnEdu.setSelection(0);
	            	}
	              	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))<=10)
	            	{
	            		spnEdu.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))+1);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==12)
	            	{
	            		spnEdu.setSelection(12);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==14)
	            	{
	            		spnEdu.setSelection(13);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==16)
	            	{
	            		spnEdu.setSelection(14);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==17)
	            	{
	            		spnEdu.setSelection(15);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==99)
	            	{
	            		spnEdu.setSelection(16);
	            	}
	              	*/

					spnEdu.setSelection(Global.SpinnerItemPosition( spnEdu, 2, cur.getString(cur.getColumnIndex("Edu"))));
					spnEdu.setEnabled(false);


					txtSp1.setText("");
					txtSp2.setText("");

					spnOcp.setEnabled(false);
					if(cur.getString(cur.getColumnIndex("Ocp")).trim().length()==0)
					{
						spnOcp.setSelection(0);
						spnOcp.setEnabled(true);
					}
					else
					{
						spnOcp.setSelection(Global.SpinnerItemPosition( spnOcp, 2, cur.getString(cur.getColumnIndex("Ocp"))));
					}
	            	
	            	/*
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))<=6)
	            	{
	            		spnOcp.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp"))));
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==11)
	            	{
	            		spnOcp.setSelection(7);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==17)
	            	{
	            		spnOcp.setSelection(8);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==21)
	            	{
	            		spnOcp.setSelection(9);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==22)
	            	{
	            		spnOcp.setSelection(10);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==27)
	            	{
	            		spnOcp.setSelection(11);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==31)
	            	{
	            		spnOcp.setSelection(12);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==32)
	            	{
	            		spnOcp.setSelection(13);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==33)
	            	{
	            		spnOcp.setSelection(14);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==34)
	            	{
	            		spnOcp.setSelection(15);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==37)
	            	{
	            		spnOcp.setSelection(16);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==77)
	            	{
	            		spnOcp.setSelection(17);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==88)
	            	{
	            		spnOcp.setSelection(18);
	            	}
	            	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==99)
	            	{
	            		spnOcp.setSelection(19);
	            	}
	            	*/

					cur.moveToNext();
				}
				cur.close();
			}
			else
			{
				MemberData(household, memsl, dialog,entype);
			}

			PS  = PStat;
			LMP = LmpDt;
			final String EntryDate = endate;
			btnMemberSaveContinue.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view)
				{
					String SQL="";
					int i=0;
					String  Rth="",Sex="";


					if(txtName.getText().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের নাম খালি রাখা যাবে না");
						return;
					}
					else if(txtBDate.getText().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্যের জন্ম তারিখ খালি রাখা যাবে না");
						return;
					}

					String DateVal = Global.DateValidate(txtBDate.getText().toString());
					if(DateVal.length()!=0)
					{
						Connection.MessageBox(MemberEvents.this, DateVal);
						return;
					}

					//Age of member (year)
					int age = Global.DateDifference(vdate, txtBDate.getText().toString());

					//dob should be less than entry date: 04 Jan 2014
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					Date EnDt;
					Date DOB;
					try {
						EnDt = sdf1.parse(EntryDate);
						DOB  = sdf.parse(txtBDate.getText().toString());
						if(DOB.after(EnDt))
						{
							Connection.MessageBox(MemberEvents.this, "জন্ম তারিখ সদস্যের এন্ট্রি এর তারিখ "+ Global.DateConvertDMY(endate) +" এর বেশী হবে না।");
							return;
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}                               
		              		
		            
					
					/*
					ElseIf Val(EnType) = 25 Then
                                        If CDate(Format(txtBDate.Text, "DD/MM/YYYY")) <> CDate(Format(BDate, "DD/MM/YYYY")) Then
                                            MsgBox "Birth date sould be " & Format(BDate, "DD/MM/YYYY") & ".", vbExclamation, "Invalid Birth Date"
                                            txtBDate.SetFocus
                                            Exit Sub
                                        End If	
                                        End If
                                        */
					if(rdoSexM.isChecked()==false && rdoSexF.isChecked()==false)
					{
						Connection.MessageBox(MemberEvents.this, "সদস্য ছেলে না মেয়ে এ তথ্য খালি রাখা যাবে না");
						return;
					}
					else if(rdoSexM.isChecked()==true)
					{
						Sex="1";
					}
					else if(rdoSexF.isChecked()==true)
					{
						Sex="2";
					}

					if(spnRth.getSelectedItemPosition()==0)
					{
						Connection.MessageBox(MemberEvents.this, "খানা প্রধানের সাথে সদস্যের সম্পর্কের তথ্য খালি রাখা যাবে না");
						return;
					}
					else
					{
						Rth= Global.Left(String.valueOf(spnRth.getSelectedItem().toString()),2);
					}

					if(Rth.equals("01") & C.Existence("Select * from tTrans where hh='"+ household +"' and Status='m' and Rth='01' and (ExType is null or length(ExType)=0)")==true)
					{
						Connection.MessageBox(MemberEvents.this, "খানা প্রধানের তথ্য সদস্যের তালিকায় অন্তর্ভুক্ত আছে।");
						return;
					}
					else if(Rth.equals("01") & age < 10)
					{
						Connection.MessageBox(MemberEvents.this, "খানা প্রধানের বয়স ১০ বছরের সমান/বেশী হতে হবে।");
						return;
					}

					if(txtMono.getText().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "মায়ের সিরিয়াল নাম্বার কত এ তথ্য খালি রাখা যাবে না");
						return;
					}
					if(memsl.equals(txtMono.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "মায়ের সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
						return;
					}

					if(txtFano.getText().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, "বাবার সিরিয়াল নাম্বার কত এ তথ্য খালি রাখা যাবে না");
						return;
					}
					if(memsl.equals(txtFano.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "বাবার সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
						return;
					}




					String MS="",Edu="",Ocp="";
					if(!entype.equals("22") & !entype.equals("23"))
					{
						if(spnMs.getSelectedItemPosition()==0)
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের বৈবাহিক অবস্থা কি এ তথ্য খালি রাখা যাবে না");
							return;
						}
						else if(spnMs.getSelectedItemPosition()==2 & txtSp1.getText().length()==0)
						{
							Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রীর সিরিয়াল নাম্বার কত এ তথ্য খালি রাখা যাবে না");
							return;
						}
						else if(age>=4 & spnEdu.getSelectedItemPosition()==0)
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের শিক্ষাগত যোগ্যতা কি এ তথ্য খালি রাখা যাবে না");
							return;
						}
						else if(age<4 & spnEdu.getSelectedItemPosition() != 1)
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ৪ বছরের কম হলে শিক্ষাগত যোগ্যতা ০০ হতে হবে।");
							return;
						}
						else if(spnOcp.getSelectedItemPosition()==0)
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের পেশাগত যোগ্যতা কি এ তথ্য খালি রাখা যাবে না");
							return;
						}
						else if(age>=12 & Global.Left(spnOcp.getSelectedItem().toString(),2).equals("88"))
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ১২ বছরের বেশী হলে পেশার কোড ৮৮ হতে পারে না।");
							return;
						}
						else if(age < 12 & !Global.Left(spnOcp.getSelectedItem().toString(),2).equals("88"))
						{
							Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স ১২ বছরের কম হলে পেশার কোড ৮৮ হতে হবে।");
							return;
						}
					}


					MS  = Global.Left(spnMs.getSelectedItem().toString(),2);



					//variable for consistency check
					int edu = 0;
					if(spnEdu.getSelectedItemPosition() > 0)
					{
						edu = Integer.parseInt(Global.Left(spnEdu.getSelectedItem().toString(),2));
						Edu = Global.Left(spnEdu.getSelectedItem().toString(),2);
					}

					int ocp = 0;
					if(spnOcp.getSelectedItemPosition() > 0)
					{
						ocp = Integer.parseInt(Global.Left(spnOcp.getSelectedItem().toString(),2));
						Ocp = Global.Left(spnOcp.getSelectedItem().toString(),2);
					}
					else
					{
						Ocp = "";
					}


					if(!entype.equals("25"))
					{
						//String MS = Global.Left(spnMs.getSelectedItem().toString(),2);						
						if((MS.equals("31") | MS.equals("32")| MS.equals("33")| MS.equals("34")) &  age < 10)
						{
							Connection.MessageBox(MemberEvents.this, "বয়স " + String.valueOf(age) +" বছর :"+ spnMs.getSelectedItem().toString() +" হবার জন্য বয়স ১০ বছরের বেশি হতে হবে।");
							return;
						}
					}

					if(memsl.equals(txtSp1.getText().toString()))
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
						return;
					}
					else if(memsl.equals(txtSp2.getText().toString()))
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার এবং সদস্যের সিরিয়াল নাম্বার একই রকম হবে না।");
						return;
					}
					else if((txtSp1.getText().length()>0 & txtSp2.getText().length()>0) & txtSp1.getText().equals(txtSp2.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার(১ এবং ২) একই রকম হবে না।");
						return;
					}
					else if((txtSp1.getText().length()==0 | txtSp1.getText().equals("0") | txtSp1.getText().equals("00")) & txtSp2.getText().length()!=0)
					{
						Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর সিরিয়াল নাম্বার ১ খালি রেখে ২ এন্ট্রি করা যাবে না।");
						return;
					}

					//spouse's serial
					else if(Integer.valueOf(txtSp1.getText().toString().length()==0?"0":txtSp1.getText().toString()) > 0)
					{
						//marital status
						if(!Global.Left(spnMs.getSelectedItem().toString(),2).equals("31"))
						{
							Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী অবশ্যই বিবাহিত হতে হবে।");
							return;
						}
						//age
						else if(age < 10)
						{
							Connection.MessageBox(MemberEvents.this, "স্বামী/স্ত্রী এর বয়স ১০ বছরের সমান/বেশী হতে হবে।");
							return;
						}
					}


					//education
					else if(age > 4 & spnEdu.getSelectedItemPosition()==0)
					{
						Connection.MessageBox(MemberEvents.this, "বয়স ৪ এর বেশী হলে অবশ্যই শিক্ষার কোড থাকতে হবে।");
						return;
					}
					else if(spnEdu.getSelectedItemPosition() > 1)
					{
						//int edu = Integer.parseInt(Global.Left(spnEdu.getSelectedItem().toString(),2));
						if(edu != 99 & (edu>=1 & edu<=18))
						{
							if(Math.abs(age-4) < edu)
							{
								Connection.MessageBox(MemberEvents.this, "শিক্ষার কোড "+ Math.abs(age-4) +" এর সমান অথবা কম হতে হবে।");
								return;
							}
							//else if(edu == 11 || edu == 13 | edu == 15)
							else if(edu == 11 || edu == 13)
							{
								Connection.MessageBox(MemberEvents.this, "শিক্ষার কোড অবশ্যই 00-10,12,14,15,16,17,99 হতে হবে।");
								return;
							}
							//age should not have < 4 years
							else if(age < 4 & edu > 0)
							{
								Connection.MessageBox(MemberEvents.this, "সদস্যের বয়স শিক্ষার জন্য প্রযোজ্য নয়।");
								return;
							}
							//education should be consistent with age
							else if((age - edu) < 4)
							{
								Connection.MessageBox(MemberEvents.this, "সদস্যের বয়সের ("+ age +" বছর) সাথে শিক্ষার কোড "+ edu +" সঠিক নয়।");
								return;
							}

							//***education event 99 would not treat as an event
							//else if(edu == 99)
							//{
							//	Connection.MessageBox(MemberEvents.this, "Not a valid education code.");
							//	return;																					
							//}
						}
					}


					//occupation		            
					else if(spnOcp.getSelectedItemPosition()>0)
					{

						//check education code should be greater 12 for occupation code 34
						if(edu < 12 & ocp == 34)
						{
							Connection.MessageBox(MemberEvents.this, "পেশার কোড ৩৪ এর জন্য শিক্ষার কোড অবশ্যই ১২ হতে হবে।");
							return;
						}
						//check education code should be greater 1 for occupation code 32
						else if(edu < 1 & ocp == 32)
						{
							Connection.MessageBox(MemberEvents.this, "পেশার কোড ৩২ এর জন্য সদস্য অবশ্যই শিক্ষিত হতে হবে।");
							return;
						}
						//student
						else if(ocp == 2 & edu == 0 & age > 30)
						{
							Connection.MessageBox(MemberEvents.this, "পেশার কোড ০২ এর জন্য শিক্ষার কোড ০০ সঠিক নয়।");
							return;
						}
						//age>40, ocp should not 02
						else if(ocp == 2 & age > 40)
						{
							Connection.MessageBox(MemberEvents.this, "যাদের বয়স ৪০ বছরের বেশী তাদের পেশার কোড ০২ হতে পারে না।");
							return;
						}
						//check occupation event=03 for woman
						else if(ocp == 3 & rdoSexM.isChecked()==true)
						{
							Connection.MessageBox(MemberEvents.this, "পুরুষ লোকের পেশা ০৩ হতে পারে না।");
							return;
						}
						//Retired person but age < 30
						else if(ocp == 6 & age < 30)
						{
							Connection.MessageBox(MemberEvents.this, "বয়স ৩০ এর কম হলে পেশা অবসরপ্রাপ্ত (চাকুরি করেন না) হতে পারে না।");
							return;
						}
						//Occupation (31), but education = 00
						else if(ocp == 31 & edu == 0)
						{
							Connection.MessageBox(MemberEvents.this, "পেশা মেধাসম্পন্ন (হাতের কাজ নয়) এর জন্য শিক্ষা ০০ হতে পারে না।");
							return;
						}
						//Occupation (34), but education < 10
						else if(ocp == 34 & edu < 10)
						{
							Connection.MessageBox(MemberEvents.this, "পেশা পেশাদার-ডাক্তার,কৃষি-কর্মকর্তা,শিক্ষক,ইঞ্জিনিয়ার(মেধাসম্পন্ন-হাতের কাজ নয়) এর জন্য শিক্ষা ১০ এর কম হতে পারে না।");
							return;
						}
						
			            /*'Occupation (31,34), but age > 70
			            ElseIf (txtOcp.Text = "34" Or txtOcp.Text = "31") And Int(DateDiff("D", CDate(txtBDate.Text), CDate(VDate.Text)) / 365.25) > 70 Then
			                YN = MsgBox("Occupation code is not consistent with age. Would you like to accept it(Y/N)?", vbExclamation + vbYesNo + vbDefaultButton2, "Occupation")
			                If YN = vbNo Then
			                    txtOcp.SetFocus
			                    Exit Sub
			                End If
						'Occupation (11,17), but education (16,17)
			            ElseIf (txtOcp.Text = "11" Or txtOcp.Text = "17") And (txtEdu.Text = "16" Or txtEdu.Text = "17") Then
			                YN = MsgBox("Occupation is not consistent with education. Would you like to accept it(Y/N)?", vbExclamation + vbYesNo + vbDefaultButton2, "Occupation")
			                If YN = vbNo Then
			                    txtOcp.SetFocus
			                    Exit Sub
			                End If
			            
			            'Occupation (21,22), but education (17)
			            ElseIf (txtOcp.Text = "21" Or txtOcp.Text = "22") And (txtEdu.Text = "17") Then
			                YN = MsgBox("Occupation is not consistent with education. Would you like to accept it(Y/N)?", vbExclamation + vbYesNo + vbDefaultButton2, "Occupation")
			                If YN = vbNo Then
			                    txtOcp.SetFocus
			                    Exit Sub
			                End If
			            
			            Else						
						//Occupation (31,34), but age > 70
						else if((ocp == 34 | ocp == 31) & age > 70)
						{
							Connection.MessageBox(MemberEvents.this, "পেশা পেশাদার-ডাক্তার,কৃষি-কর্মকর্তা,শিক্ষক,ইঞ্জিনিয়ার(মেধাসম্পন্ন-হাতের কাজ নয়) এর জন্য শিক্ষা ১০ এর কম হতে পারে না।");
							return;
						}*/

					}
					//-MOTHER, FATHER, Spouse's Serial
					else if(txtMono.getText().equals(txtFano.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "মা এবং বাবার সিরিয়াল নাম্বার এক হতে পারে না।");
						return;
					}
					else if(txtMono.getText().equals(txtSp1.getText()) | txtMono.getText().equals(txtSp2.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "মা এবং স্বামী/স্ত্রী এর সিরিয়াল নাম্বার এক হতে পারে না।");
						return;
					}
					else if(txtFano.getText().equals(txtSp1.getText()) | txtFano.getText().equals(txtSp2.getText()))
					{
						Connection.MessageBox(MemberEvents.this, "বাবা এবং স্বামী/স্ত্রী এর সিরিয়াল নাম্বার এক হতে পারে না।");
						return;
					}


					boolean available=false;

					try
					{
						SQL ="Select * from tTrans Where Status='m' and Vill||Bari||HH='"+ household +"' and SNo='"+ memsl +"'";

						available=C.Existence(SQL);

						if(!available)
						{
							C.Save(g.getSQLforNewRegis());

							SQL="Insert into tTrans";
							SQL+="(Status,Vill,Bari,HH,sno,Pno,Name, Rth,Sex,BDate,Age,Mono,Fano,";
							SQL+="Edu,Ms,Pstat,LmpDt,Sp1,Sp2,Sp3,Sp4,Ocp,EnType,EnDate,ExType,ExDate,";
							SQL+="PageNo,Upload,posmig,posmigdate) values(";
							SQL+="'m',";
							SQL+="'" + vill + "',";
							SQL+="'" + bari + "',";
							SQL+="'" + hhno + "',";
							SQL+="'" + memsl + "',";
							if(entype.equals("22") | entype.equals("23"))
							{
								SQL+="'"+ g.getPNo() +"',";
							}
							else
							{
								SQL+="'',";
							}

							SQL+="'" + txtName.getText() + "','" + Rth + "','" +  Sex + "','" + Global.DateConvertYMD(txtBDate.getText().toString()) + "','','" + Global.Right("00"+txtMono.getText(),2) + "','" + Global.Right("00"+txtFano.getText(),2) + "',";
							SQL+="'" + Edu + "',";
							SQL+="'" + MS + "', ";
							SQL+="'" + PS + "',";
							SQL+="'" + LMP + "',";
							if(txtSp1.getText().length()>0)
								SQL+="'" + Global.Right("00"+txtSp1.getText(),2) + "',";
							else
								SQL+="'" + txtSp1.getText() + "',";
							if(txtSp2.getText().length()>0)
								SQL+="'" + Global.Right("00"+txtSp2.getText(),2) + "',";
							else
								SQL+="'" + txtSp2.getText() + "',";

							SQL+="'',";
							SQL+="'',";
							SQL+="'" + Ocp + "',";
							SQL+="'"+ entype +"',";	//EnType
							SQL+="'"+ endate +"',"; //EnDate
							SQL+="'',"; //ExType
							SQL+="'',"; //ExDate
							SQL+="'0','2','','')";


							C.Save(SQL);

							PStat = "";
							LmpDt = "";
							PS    = "";
							LMP   = "";
							dialog.dismiss();
							DataRetrieve(household, false,"active");
						}
						else
						{
							//save only if ammendment
							if(entype.equals("12"))  C.Save(g.getSQLforNewRegis());

							if(entype.equals("25"))
							{
								SQL="Update tTrans set Name='" + txtName.getText() + "', Rth='" + Rth + "',Sex='" + Sex + "',BDate='" + Global.DateConvertYMD(txtBDate.getText().toString()) + "',EnDate='" + Global.DateConvertYMD(txtBDate.getText().toString()) + "', Mono='" + Global.Right("00"+txtMono.getText(),2) + "', Fano='" + Global.Right("00"+txtFano.getText(),2) + "', ";
								SQL+=" Edu='" + Edu + "', Ms='" + MS + "', Pstat='"+ PS +"',LmpDt='"+ LMP +"', Sp1='" + (txtSp1.getText().length()==0?txtSp1.getText():Global.Right("00"+txtSp1.getText(),2)) + "',Sp2='"+ (txtSp2.getText().length()==0?txtSp2.getText():Global.Right("00"+txtSp2.getText(),2)) +"',Sp3='',Sp4='',Ocp='" + Ocp + "' where ";
								SQL+=" Status='m' and Vill||Bari||HH='"+ household +"' and SNo='"+ memsl +"'";
								C.Save(SQL);

								//update on events table
								//C.Save("");
							}
							else
							{
								SQL="Update tTrans set Name='" + txtName.getText() + "', Rth='" + Rth + "',Sex='" + Sex + "',BDate='" + Global.DateConvertYMD(txtBDate.getText().toString()) + "', Mono='" + Global.Right("00"+txtMono.getText(),2) + "', Fano='" + Global.Right("00"+txtFano.getText(),2) + "', ";
								SQL+=" Edu='" + Edu + "', Ms='" + MS + "', Pstat='"+ PS +"',LmpDt='"+ LMP +"', Sp1='" + (txtSp1.getText().length()==0?txtSp1.getText():Global.Right("00"+txtSp1.getText(),2)) + "',Sp2='"+ (txtSp2.getText().length()==0?txtSp2.getText():Global.Right("00"+txtSp2.getText(),2)) +"',Sp3='',Sp4='',Ocp='" + Ocp + "' where ";
								SQL+=" Status='m' and Vill||Bari||HH='"+ household +"' and SNo='"+ memsl +"'";
								C.Save(SQL);
							}

							dialog.dismiss();
							DataRetrieve(household, false,"active");
						}
					}
					catch(Exception ex)
					{
						Connection.MessageBox(MemberEvents.this, ex.getMessage());
						return;
					}
				}
			});

			cmdMemberClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view)
				{
					g.setSQLforNewRegis("");
					dialog.dismiss();
				}
			});

			spnMs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					if(spnMs.getSelectedItemPosition()==2)
					{
						lblSp1.setTextColor(Color.BLACK);
						lblSp2.setTextColor(Color.BLACK);
						txtSp1.setEnabled(true);
						txtSp2.setEnabled(true);
					}
					else
					{
						txtSp1.setText("");
						txtSp2.setText("");
						lblSp1.setTextColor(Color.GRAY);
						lblSp2.setTextColor(Color.GRAY);
						txtSp1.setEnabled(false);
						txtSp2.setEnabled(false);
					}
				}
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}
			});
			dialog.show();

		}
		catch(Exception ex)
		{
			Connection.MessageBox(MemberEvents.this, ex.getMessage());
			return;
		}

	}

	private void MemberData(String Household, String SNo,Dialog dialog, String EvCode)
	{
		EditText txtName;
		EditText txtBDate;
		RadioGroup rdogrpSex;
		RadioButton rdoSexM;
		RadioButton rdoSexF;
		Spinner spnRth;
		EditText txtMono;
		EditText txtFano;
		Spinner spnMs;
		TextView lblSp1;
		EditText txtSp1;
		EditText txtSp2;
		Spinner spnEdu;
		Spinner spnOcp;

		txtName=(EditText)dialog.findViewById(R.id.txtName);
		txtBDate=(EditText)dialog.findViewById(R.id.txtBDate);
		rdogrpSex=(RadioGroup)dialog.findViewById(R.id.rdogrpSex);
		rdoSexM=(RadioButton)dialog.findViewById(R.id.rdoSexM);
		rdoSexF=(RadioButton)dialog.findViewById(R.id.rdoSexF);
		spnRth=(Spinner)dialog.findViewById(R.id.spnRth);
		txtMono=(EditText)dialog.findViewById(R.id.txtMono);
		txtFano=(EditText)dialog.findViewById(R.id.txtFano);
		spnMs=(Spinner)dialog.findViewById(R.id.spnMs);
		spnEdu=(Spinner)dialog.findViewById(R.id.spnEdu);
		lblSp1=(TextView)dialog.findViewById(R.id.VlblSp1);
		txtSp1 =(EditText)dialog.findViewById(R.id.txtSp1);
		txtSp2 =(EditText)dialog.findViewById(R.id.txtSp2);
		spnOcp=(Spinner)dialog.findViewById(R.id.spnOcp);

		Cursor cur=C.ReadData("Select pno," +
				" (case when sno  is null then '' else sno    end) sno," +
				" (case when Name  is null then '' else Name  end) Name," +
				" (case when Rth  is null then 0 else Rth  end) Rth," +
				" (case when Sex  is null then '' else Sex  end) Sex," +
				" (case when BDate  is null then '' else BDate  end) BDate," +
				" (case when Mono  is null then '' else Mono  end) Mono," +
				" (case when Fano  is null then '' else Fano  end) Fano," +
				" (case when Edu  is null then 0 else Edu  end) Edu," +
				" (case when Ms  is null then 0 else Ms  end) Ms, ifnull(PStat,'')PStat, ifnull(LmpDt,'')LmpDt," +
				" (case when Sp1  is null then '' else Sp1  end) Sp1,(case when Sp2  is null then '' else Sp2  end) Sp2," +
				" (case when Ocp is null then 0 else Ocp end) Ocp, EnType" +
				" from tTrans where status='m' and vill||bari||hh = '"+ Household +"' and SNo='"+ SNo +"'");

		cur.moveToFirst();
		String Rth="";
		while(!cur.isAfterLast())
		{
			txtName.setText(cur.getString(cur.getColumnIndex("Name")));
			txtBDate.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BDate"))));

			if(cur.getString(cur.getColumnIndex("Sex")).equals("1"))
			{
				rdoSexM.setChecked(true);
			}
			else if(cur.getString(cur.getColumnIndex("Sex")).equals("2"))
			{
				rdoSexF.setChecked(true);
			}

			if(Integer.valueOf(cur.getString(cur.getColumnIndex("Rth")))<=18)
			{
				spnRth.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Rth")))+1);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Rth")))==77)
			{
				spnRth.setSelection(20);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Rth")))==99)
			{
				spnRth.setSelection(21);
			}
			txtMono.setText(cur.getString(cur.getColumnIndex("Mono")));
			txtFano.setText(cur.getString(cur.getColumnIndex("Fano")));

			if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==30)
			{
				spnMs.setSelection(1);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==31)
			{
				spnMs.setSelection(2);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==32)
			{
				spnMs.setSelection(3);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==33)
			{
				spnMs.setSelection(4);
			}
			else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ms")))==34)
			{
				spnMs.setSelection(5);
			}

			//pstat
			PStat = cur.getString(cur.getColumnIndex("PStat"));
			//lmp
			LmpDt = cur.getString(cur.getColumnIndex("LmpDt"));

          	/*
        	if(cur.getString(cur.getColumnIndex("Edu")).trim().length()==0)
        	{
        		spnEdu.setSelection(0);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))<=10)
        	{
        		spnEdu.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))+1);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==12)
        	{
        		spnEdu.setSelection(12);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==14)
        	{
        		spnEdu.setSelection(13);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==16)
        	{
        		spnEdu.setSelection(14);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==17)
        	{
        		spnEdu.setSelection(15);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Edu")))==99)
        	{
        		spnEdu.setSelection(16);
        	}
        	*/

			spnEdu.setSelection(Global.SpinnerItemPosition( spnEdu, 2, cur.getString(cur.getColumnIndex("Edu")) ));
			txtSp1.setText(cur.getString(cur.getColumnIndex("Sp1")));
			txtSp2.setText(cur.getString(cur.getColumnIndex("Sp2")));


			if(cur.getString(cur.getColumnIndex("Ocp")).trim().length()==0)
			{
				spnOcp.setSelection(0);
			}
			else
			{
				spnOcp.setSelection(Global.SpinnerItemPosition( spnOcp, 2, cur.getString(cur.getColumnIndex("Ocp")) ));
			}
        	
        	/*
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))<=6)
        	{
        		spnOcp.setSelection(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp"))));
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==11)
        	{
        		spnOcp.setSelection(7);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==17)
        	{
        		spnOcp.setSelection(8);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==21)
        	{
        		spnOcp.setSelection(9);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==22)
        	{
        		spnOcp.setSelection(10);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==27)
        	{
        		spnOcp.setSelection(11);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==31)
        	{
        		spnOcp.setSelection(12);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==32)
        	{
        		spnOcp.setSelection(13);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==33)
        	{
        		spnOcp.setSelection(14);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==34)
        	{
        		spnOcp.setSelection(15);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==37)
        	{
        		spnOcp.setSelection(16);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==77)
        	{
        		spnOcp.setSelection(17);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==88)
        	{
        		spnOcp.setSelection(18);
        	}
        	else if(Integer.valueOf(cur.getString(cur.getColumnIndex("Ocp")))==99)
        	{
        		spnOcp.setSelection(19);
        	}
        	*/

			if(EvCode.equals("12"))
			{
				txtBDate.setEnabled(true);
				String EV = "";
				EV = "64"; //Relation to head
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ EV +"'") & !C.Existence("Select vill from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType='"+ EV +"'"))
					spnRth.setEnabled(true);
				else
					spnRth.setEnabled(false);

				EV = "61"; //mother serial
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ EV +"'") & !C.Existence("Select vill from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType='"+ EV +"'"))
					txtMono.setEnabled(true);
				else
					txtMono.setEnabled(false);

				EV = "62"; //father serial
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ EV +"'") & !C.Existence("Select vill from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType='"+ EV +"'"))
					txtFano.setEnabled(true);
				else
					txtFano.setEnabled(false);

				EV = ""; //marital status
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType in('31','32','33','34')") & !C.Existence("Select vill from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType in('31','32','33','34')"))
					spnMs.setEnabled(true);
				else
					spnMs.setEnabled(false);

				txtSp1.setEnabled(false);
				txtSp2.setEnabled(false);

				EV = "71"; //education
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ EV +"'") & !C.Existence("Select vill from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType='"+ EV +"'"))
					spnEdu.setEnabled(true);
				else
					spnEdu.setEnabled(false);

				EV = "72"; //occupation
				if(!C.Existence("Select vill from tTrans where status='e' and vill||Bari||hh='"+ Household +"' and sno='"+ SNo +"' and EvType='"+ EV +"'") & !C.Existence("Select * from Events where Pno='"+ cur.getString(cur.getColumnIndex("Pno")) +"' and EvType='"+ EV +"'"))
					spnOcp.setEnabled(true);
				else
					spnOcp.setEnabled(false);

			}
			else if(cur.getString(cur.getColumnIndex("EnType")).equals("22") | cur.getString(cur.getColumnIndex("EnType")).equals("23"))
			{
				txtBDate.setEnabled(false);
				rdoSexF.setEnabled(false);
				rdoSexM.setEnabled(false);
				spnMs.setEnabled(false);
				if(spnEdu.getSelectedItemPosition()==0)
					spnEdu.setEnabled(true);
				else
					spnEdu.setEnabled(false);

				if(spnOcp.getSelectedItemPosition()==0)
					spnOcp.setEnabled(true);
				else
					spnOcp.setEnabled(false);
			}
			cur.moveToNext();
		}
		cur.close();

	}

	//***************************************************************************************************************************
	private void ShowEventList(String Vill, String Bari, String HH)
	{
		try
		{

			final Dialog dialog = new Dialog(MemberEvents.this);
			dialog.setTitle("Events List");
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.eventlistpopup);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);


			final ListView evlist = (ListView)dialog.findViewById(R.id.lstEvent);
			View header = getLayoutInflater().inflate(R.layout.eventlistheading, null);
			evlist.addHeaderView(header);

			final RadioGroup roEventList =(RadioGroup)dialog.findViewById(R.id.roEventList);
			roEventList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup arg0, int id) {
					if(id == R.id.roCurrentEvent)
					{
						EventDataList(dialog, vill+bari+hhno, "current",evlist);
					}
					else if(id == R.id.roAllEvent)
					{
						EventDataList(dialog, vill+bari+hhno, "all",evlist);
					}
				}});
			EventDataList(dialog, vill+bari+hhno, "current",evlist);



			Button cmdEvListClose = (Button)dialog.findViewById(R.id.cmdEvListClose);
			cmdEvListClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}

	private void EventDataList(Dialog d, String Household,String Status, ListView evList)
	{
		Cursor cur1 = null;

		if(Status.equals("current"))
		{
			cur1 = C.ReadData("select sno,pno as pno,evtype,evdate,ifnull(info1,'')info1,ifnull(info2,'')info2,ifnull(info3,'')info3,ifnull(info4,'')info4,ifnull(info5,'')info5,Rnd from ttrans where status='e' and Vill||Bari||HH='"+ Household +"' order by sno,Rnd,evtype");
		}
		else if(Status.equals("all"))
		{
			cur1 = C.ReadData("select sno,pno as pno,evtype,evdate,ifnull(info1,'')info1,ifnull(info2,'')info2,ifnull(info3,'')info3,ifnull(info4,'')info4,ifnull(info5,'')info5,Rnd from Events where Vill||Bari||HH='"+ Household +"' order by sno,Rnd,evtype");
		}

		if(cur1.getCount()==0)
		{
			evList.setAdapter(null);
			cur1.close();
			return;
		}

		cur1.moveToFirst();
		evmylist.clear();

		int i=0;

		while(!cur1.isAfterLast())
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("sno", cur1.getString(cur1.getColumnIndex("Sno")));
			map.put("pno", cur1.getString(cur1.getColumnIndex("pno")));
			map.put("evtype", cur1.getString(cur1.getColumnIndex("EvType")));
			map.put("evdate", cur1.getString(cur1.getColumnIndex("EvDate")));
			map.put("info1", cur1.getString(cur1.getColumnIndex("info1")));
			map.put("info2", cur1.getString(cur1.getColumnIndex("info2")));
			map.put("info3", cur1.getString(cur1.getColumnIndex("info3")));
			map.put("info4", cur1.getString(cur1.getColumnIndex("info4")));
			map.put("info5", cur1.getString(cur1.getColumnIndex("info5")));
			map.put("rnd", cur1.getString(cur1.getColumnIndex("Rnd")));
			map.put("status", Status);

			evmylist.add(map);

			eList = new SimpleAdapter(MemberEvents.this, evmylist, R.layout.eventlistrow,
					new String[] {"sno"},
					new int[] {R.id.e_sno});
			evList.setAdapter(new EventListAdapter(this,d,evList));
			i+=1;
			cur1.moveToNext();
		}
		cur1.close();
	}

	public class EventListAdapter extends BaseAdapter
	{
		private Context context;
		Dialog dg;
		ListView lv;
		public EventListAdapter(Context c,Dialog d,ListView evlist){
			context = c;
			dg=d;
			lv=evlist;
		}

		public int getCount() {
			return eList.getCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.eventlistrow, null);
			}

			final HashMap<String, String> o = (HashMap<String, String>) eList.getItem(position);

			TextView e_sno=(TextView)convertView.findViewById(R.id.e_sno);
			TextView e_evtype=(TextView)convertView.findViewById(R.id.e_evtype);
			TextView e_evdate=(TextView)convertView.findViewById(R.id.e_evdate);
			TextView e_info1=(TextView)convertView.findViewById(R.id.e_info1);
			TextView e_info2=(TextView)convertView.findViewById(R.id.e_info2);
			TextView e_info3=(TextView)convertView.findViewById(R.id.e_info3);
			TextView e_info4=(TextView)convertView.findViewById(R.id.e_info4);
			TextView e_info5=(TextView)convertView.findViewById(R.id.e_info5);
			TextView e_round=(TextView)convertView.findViewById(R.id.e_round);

			e_sno.setText(o.get("sno").toString());
			e_evtype.setText(o.get("evtype").toString());
			e_evdate.setText(Global.DateConvertDMY(o.get("evdate").toString()));
			e_info1.setText(o.get("info1").toString());
			e_info2.setText(o.get("info2").toString());
			e_info3.setText(o.get("info3").toString());
			e_info4.setText(o.get("info4").toString().trim().length()==0?o.get("info4").toString():Global.DateConvertDMY(o.get("info4").toString()));
			e_info5.setText(o.get("info5").toString());
			e_round.setText(o.get("rnd"));

			Button cmdEvListDel = (Button)convertView.findViewById(R.id.cmdEvListDel);
			Button cmdEvListEdit = (Button)convertView.findViewById(R.id.cmdEvListEdit);

			cmdEvListDel.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
					adb.setTitle("Event Delete");
					adb.setMessage("সদস্যের নাম্বারঃ "+ o.get("sno").toString() +" এবং ইভেন্ট কোডঃ "+ o.get("evtype").toString() +" কি মুছে ফেলতে চান [Yes/No]?");

					adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {

						}});

					adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {
							String HH = vill+bari+hhno;
							String SN = o.get("sno").toString();
							String EV = o.get("evtype").toString();
							String EVD= o.get("evdate").toString();

							//event specific update
							if(EV.equals("12"))
							{
								C.Save("Delete from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
							}
							else if(EV.equals("21") | EV.equals("22") | EV.equals("23"))
							{
								//C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Delete from tTrans where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
								C.Save("Delete from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("25"))
							{
								//C.Save("Delete from tTrans where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("31") | EV.equals("32") | EV.equals("33") | EV.equals("34"))
							{
								String PMS = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set MS='"+ PMS +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("40") | EV.equals("49"))
							{
								C.Save("Delete from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
							}
							else if(EV.equals("41"))
							{
								//Check 42 event available or not
								if(C.Existence("select vill from tTrans where status='e' and EvType='42' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and info4='"+ EVD +"'"))
								{
									Connection.MessageBox(MemberEvents.this, "এই গর্ভের ডেলিভারি হয়ে গেছে, প্রথমে ইভেন্ট ৪২ মুছতে হবে এবং তারপর ৪১। ।।  .");
									return;
								}

								//Update from temporary table
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set PStat='',LMPDt='' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");

								//Transfer events from main to UpdateEvents table
								String SQL = "Insert into UpdateEvents(Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload)";
								SQL += "Select Vill, Bari, Hh, Pno, Sno, EvType, EvDate, Info1, Info2, Info3, Info4, Vdate, Rnd, Upload from Events where";
								SQL += " Vill||Bari||HH = '"+ HH +"' and ";
								SQL += " SNo = '"+ SN +"' and EvType='41' and EvDate='"+ EVD +"'";
								C.Save(SQL);

								//Update from main table
								C.Save("delete from Events where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='41' and EvDate='"+ EVD +"'");
								C.Save("Update Member set PStat='',LMPDt='' where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");

							}
							else if(EV.equals("42"))
							{
								String LMP = o.get("info4");
								//Update from main table
								//C.Save("Update Member set PStat='41',LMPDt='"+ LMP +"' where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
								//C.Save("delete from Events where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='42' and EvDate='"+ EVD +"'");
							}
							else if(EV.equals("51") | EV.equals("52") | EV.equals("53") | EV.equals("55"))
							{
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set ExType='',ExDate='' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("54") | EV.equals("57"))
							{
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set PosMig='',PosMigDate='' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("61"))
							{
								String PMono = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set MoNo='"+ PMono +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("62"))
							{
								String PFano = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set FaNo='"+ PFano +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("63"))
							{
								//String PFano = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								//C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								//C.Save("Update tTrans set FaNo='"+ PFano +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");	  		            			  		            			  		            		
							}
							else if(EV.equals("64"))
							{
								String PRth = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set Rth='"+ PRth +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("71"))
							{
								String PEdu = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set Edu='"+ PEdu +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}
							else if(EV.equals("72"))
							{
								String POcp = C.ReturnSingleValue("Select Info2 from tTrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
								C.Save("Update tTrans set Ocp='"+ POcp +"' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
							}

							EventDataList(dg, HH, "current",lv);
							DataRetrieve(vill+bari+hhno,false,"active");
						}});

					adb.show();
				}
			});

			cmdEvListEdit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
					adb.setTitle("Event Update");
					adb.setMessage("সদস্যের নাম্বারঃ "+ o.get("sno").toString() +" এবং ইভেন্ট কোডঃ "+ o.get("evtype").toString() +" কি আপডেট করতে চান [Yes/No]?");

					adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {

						}});

					adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog1, int which) {
							//Show event update form
							UpdateEventForm(o.get("evtype"),o.get("evdate"), vill, bari, hhno, o.get("sno"),o.get("pno"),lv,o.get("info3"));
	  		            	  /*String HH = vill+bari+hhno;
	  		            	  EventDataList(dg, HH, "current",lv);*/
						}});
					adb.show();
				}});


			if(o.get("status").equals("current"))
			{
				cmdEvListDel.setEnabled(true);
				cmdEvListEdit.setEnabled(false);
			}
			else
			{
				if(o.get("evtype").toString().equals("41"))
				{
					cmdEvListDel.setEnabled(true);
					cmdEvListEdit.setEnabled(true);
				}
				else if(o.get("evtype").toString().equals("31"))
				{
					cmdEvListDel.setEnabled(false);
					cmdEvListEdit.setEnabled(true);

				}
				else if(o.get("evtype").toString().equals("32"))
				{
					cmdEvListDel.setEnabled(false);
					cmdEvListEdit.setEnabled(true);

				}
				else if(o.get("evtype").toString().equals("33"))
				{
					cmdEvListDel.setEnabled(false);
					cmdEvListEdit.setEnabled(true);

				}
				else if(o.get("evtype").toString().equals("34"))
				{
					cmdEvListDel.setEnabled(false);
					cmdEvListEdit.setEnabled(true);

				}
				else
				{
					cmdEvListDel.setEnabled(false);
					cmdEvListEdit.setEnabled(false);
				}
			}

			return convertView;
		}

	}

	//Event Update Form
	//***************************************************************************************************************************
    /*
    private void EventUpdateForm(String Vill, String Bari, String HH)
    {
    	try
    	{ 

    	final Dialog dialog = new Dialog(MemberEvents.this);
		dialog.setTitle("Events Update List:");
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	dialog.setContentView(R.layout.eventlistupdate);
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.setCancelable(true);
    	
    	
    	final ListView evlist = (ListView)dialog.findViewById(R.id.lstEventUpdate);
		View header = getLayoutInflater().inflate(R.layout.eventlistupdateheading, null);
		evlist.addHeaderView(header);
		
    	final RadioGroup roEventList =(RadioGroup)dialog.findViewById(R.id.roEventList);
    	roEventList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                if(id == R.id.roCurrentEvent)
                {
                	EventUpdateDataList(dialog, vill+bari+hhno, "current",evlist);
                }
                else if(id == R.id.roAllEvent)
                {
                	EventUpdateDataList(dialog, vill+bari+hhno, "all",evlist);
                }            	
            }});
		
		EventUpdateDataList(dialog, vill+bari+hhno, "current",evlist);
			
	    Button cmdEvListClose = (Button)dialog.findViewById(R.id.cmdEvListClose);
	    cmdEvListClose.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View arg0) {
	        	dialog.dismiss();
	        }
	    });
	    	
    	dialog.show();
    	}
	    catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}	
    }
    
    private void EventUpdateDataList(Dialog d, String Household,String Status, ListView evList)
    {
    	Cursor cur1 = null;
    	
    	if(Status.equals("current"))
    	{
    		cur1 = C.ReadData("select sno,evtype,evdate,ifnull(info1,'')info1,ifnull(info2,'')info2,ifnull(info3,'')info3,ifnull(info4,'')info4,Rnd from ttrans where status='e' and Vill||Bari||HH='"+ Household +"' order by sno,Rnd,evtype");	
    	}
    	else if(Status.equals("all"))
    	{
    		cur1 = C.ReadData("select sno,evtype,evdate,ifnull(info1,'')info1,ifnull(info2,'')info2,ifnull(info3,'')info3,ifnull(info4,'')info4,Rnd from Events where Vill||Bari||HH='"+ Household +"' order by sno,Rnd,evtype");
    	}
			
    	if(cur1.getCount()==0)
    	{
    		evList.setAdapter(null);
    		cur1.close();
    		return;
    	}

  		cur1.moveToFirst();
  		evmylist.clear();
		
  		int i=0;
	        
  		while(!cur1.isAfterLast())
	        {    
	        	HashMap<String, String> map = new HashMap<String, String>();
	        		map.put("sno", cur1.getString(cur1.getColumnIndex("Sno")));
	        		map.put("evtype", cur1.getString(cur1.getColumnIndex("EvType")));
	  				map.put("evdate", cur1.getString(cur1.getColumnIndex("EvDate")));
	  				map.put("info1", cur1.getString(cur1.getColumnIndex("info1")));
					map.put("info2", cur1.getString(cur1.getColumnIndex("info2")));
					map.put("info3", cur1.getString(cur1.getColumnIndex("info3")));
					map.put("info4", cur1.getString(cur1.getColumnIndex("info4")));
					map.put("rnd", cur1.getString(cur1.getColumnIndex("Rnd")));
					map.put("status", Status);
					
					evmylist.add(map);		  										
					
					eList = new SimpleAdapter(MemberEvents.this, evmylist, R.layout.eventlistupdaterow,
							new String[] {"sno"},
							new int[] {R.id.e_sno});  
					evList.setAdapter(new EventUpdateListAdapter(this,d,evList));
				i+=1;
	        	cur1.moveToNext();
	        }  		        
	        cur1.close();
    }
    
    public class EventUpdateListAdapter extends BaseAdapter 
    {
        private Context context;
        Dialog dg;
        ListView lv;
        public EventUpdateListAdapter(Context c,Dialog d,ListView evlist){
            context = c;
            dg=d;
            lv=evlist;
        }
 
        public int getCount() {
            return eList.getCount();
        }
 
        public Object getItem(int position) {
            return position;
        }
 
        public long getItemId(int position) {
            return position;
        }
        
        
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.eventlistupdaterow, null); 
			}
			
		
			final HashMap<String, String> o = (HashMap<String, String>) eList.getItem(position);
			
			TextView e_sno=(TextView)convertView.findViewById(R.id.e_sno);
			TextView e_evtype=(TextView)convertView.findViewById(R.id.e_evtype);
			TextView e_evdate=(TextView)convertView.findViewById(R.id.e_evdate);
			TextView e_info1=(TextView)convertView.findViewById(R.id.e_info1);
			TextView e_info2=(TextView)convertView.findViewById(R.id.e_info2);
			TextView e_info3=(TextView)convertView.findViewById(R.id.e_info3);
			TextView e_info4=(TextView)convertView.findViewById(R.id.e_info4);
			TextView e_round=(TextView)convertView.findViewById(R.id.e_round);
			
			e_sno.setText(o.get("sno").toString());
			e_evtype.setText(o.get("evtype").toString());
			e_evdate.setText(Global.DateConvertDMY(o.get("evdate").toString()));
			e_info1.setText(o.get("info1").toString());
			e_info2.setText(o.get("info2").toString());
			e_info3.setText(o.get("info3").toString());
			e_info4.setText(o.get("info4").toString().trim().length()==0?o.get("info4").toString():Global.DateConvertDMY(o.get("info4").toString()));			
			e_round.setText(o.get("rnd"));
			
			Button cmdEvListDel = (Button)convertView.findViewById(R.id.cmdEvListDel);
			Button cmdEvListEdit = (Button)convertView.findViewById(R.id.cmdEvListEdit);
			
			cmdEvListDel.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	 AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
		    		  adb.setTitle("Event Delete");
	  		          adb.setMessage("সদস্যের নাম্বারঃ "+ o.get("sno").toString() +" এবং ইভেন্ট কোডঃ "+ o.get("evtype").toString() +" কি মুছে ফেলতে চান [Yes/No]?");
	  		          
	  		          adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
	  		              public void onClick(DialogInterface dialog1, int which) {		  
	   	        		            		  	        		            		  
	  		              }});

	  		          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
	  		              public void onClick(DialogInterface dialog1, int which) {	                 		        		            	  
	  		            	String HH = vill+bari+hhno;
	  		            	String SN = o.get("sno").toString();
	  		            	String EV = o.get("evtype").toString();
	  		            	String EVD= o.get("evdate").toString();
	  		            	
	  		            	//event specific update
	  		            	if(EV.equals("41"))
	  		            	{
	  		            		//Check 42 event available or not
	  		            		if(C.Existence("select vill from tTrans status='e' and where EvType='42' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and info4='"+ EVD +"'"))
	  		            		{
	  		            			Connection.MessageBox(MemberEvents.this, "এই গর্ভের ডেলিভারি হয়ে গেছে, প্রথমে ইভেন্ট ৪২ মুছতে হবে এবং তারপর ৪১। ।।  .");
	  		            			return;
	  		            		}
	  		            		
	  		            		//Update from temporary table
	  		            		C.Save("delete from ttrans where status='e' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
	  		            		C.Save("Update tTrans set PStat='',LMPDt='' where status='m' and vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
	  		            		
	  		            		//Update from main table
	  		            		C.Save("delete from Events where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"' and EvType='"+ EV +"' and EvDate='"+ EVD +"'");
	  		            		C.Save("Update Member set PStat='',LMPDt='' where vill||bari||hh='"+ HH +"' and SNo='"+ SN +"'");
	  		            	}	  		            	
	  		            	
	  		            	EventUpdateDataList(dg, HH, "current",lv);
	  		            	DataRetrieve(vill+bari+hhno,false,"active");
	  		              }});
	  		          
	  		          adb.show();	
	            }
	        });				

			
			cmdEvListEdit.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	 AlertDialog.Builder adb = new AlertDialog.Builder(MemberEvents.this);
		    		  adb.setTitle("Event Delete");
	  		          adb.setMessage("সদস্যের নাম্বারঃ "+ o.get("sno").toString() +" এবং ইভেন্ট কোডঃ "+ o.get("evtype").toString() +" কি আপডেট করতে চান [Yes/No]?");
	  		          
	  		          adb.setNegativeButton("No", new AlertDialog.OnClickListener() {
	  		              public void onClick(DialogInterface dialog1, int which) {		  
	   	        		            		  	        		            		  
	  		              }});

	  		          adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
	  		              public void onClick(DialogInterface dialog1, int which) {	    	  		            	  
	  		            	  //Show event update form
	  		            	  
	  		              }});
	  		 }});
	  		          
			if(o.get("evtype").toString().equals("41"))
			{
				cmdEvListDel.setEnabled(true);	
				cmdEvListEdit.setEnabled(true);
			}
			else
			{
				cmdEvListDel.setEnabled(false);	
				cmdEvListEdit.setEnabled(false);
			}
			
	        return convertView;
		}
				
	}
    */

	//***************************************************************************************************************************
	private void MigrationForm(final Dialog d, final String EvCode)
	{
		try
		{

			final Dialog dialog = new Dialog(MemberEvents.this);

			dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.migration);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);
			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);


			TextView lblMigTitle = (TextView)dialog.findViewById(R.id.lblMigTitle);
			final Spinner VillageList = (Spinner)dialog.findViewById(R.id.VillageList);
			//VillageList.setAdapter(C.getArrayAdapter("select vill||' - '||vname from mdssvill where cluster='"+ g.getClusterCode() +"' order by vname asc"));

			//from different village
			if(EvCode.equals("52"))
			{
				VillageList.setAdapter(C.getArrayAdapter("select vill||' - '||vname from mdssvill order by vname asc"));
				lblMigTitle.setText("Internal Mig.-In");
			}
			//should be same village
			else if(EvCode.equals("53"))
			{
				VillageList.setAdapter(C.getArrayAdapter("select vill||' - '||vname from mdssvill where cluster='"+ g.getClusterCode() +"' and vill='"+ g.getVillageCode() +"' order by vname asc"));
				lblMigTitle.setText("Split-In");
			}




			final EditText txtMember = (EditText)dialog.findViewById(R.id.txtMember);

			final ListView evlist = (ListView)dialog.findViewById(R.id.lstMigration);
			View header = getLayoutInflater().inflate(R.layout.migrationheading, null);
			evlist.addHeaderView(header);


			VillageList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					String V = "";
					V = Global.Left(VillageList.getSelectedItem().toString(),3);
        	    	/*
        	    	if(g.getMigVillage().toString().length()==0)
        	    	{
        	    		V = Global.Left(VillageList.getSelectedItem().toString(),3);
        	    		g.setMigVillage(V);
        	    	}
        	    	else
        	    	{        	    		
        	    		V = g.getMigVillage();
        	    	}
        	    	*/

					MigrationData(V, EvCode,txtMember.getText().toString(),dialog,evlist,"no");

				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}

			});

			txtMember.addTextChangedListener(new TextWatcher(){
				public void afterTextChanged(Editable s) {}
				public void beforeTextChanged(CharSequence s, int start, int count, int after){}
				public void onTextChanged(CharSequence s, int start, int before, int count){
					String V = Global.Left(VillageList.getSelectedItem().toString(),3);
					//need to consider Same cluster/different cluster
					MigrationData(V, EvCode,txtMember.getText().toString(),dialog,evlist,"yes");
				}});

			Button cmdMigListClose = (Button)dialog.findViewById(R.id.cmdMigListClose);
			cmdMigListClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
			Button cmdMigListOk = (Button)dialog.findViewById(R.id.cmdMigListOk);
			cmdMigListOk.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					if(g.getPNo().trim().length()==0)
					{
						Connection.MessageBox(MemberEvents.this, " সদস্য সিলেক্ট করা হয়নি, প্রথমে তালিকা থেকে সদস্য সিলেক্ট করুন।");
						return;
					}
					EditText txtPNo = (EditText)d.findViewById(R.id.txtQPNo);
					EditText EvDate = (EditText)d.findViewById(R.id.EvDate);
					txtPNo.setText(g.getPNo());
					EvDate.setText(g.getEvDate());
					dialog.dismiss();

				}
			});

			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}

	private void MigrationData(String Village,String EvCode, String Name,final Dialog d,final ListView evlist, String Search)
	{
		String SQL = "";
		SQL = "Select hh, Sno,Pno,Name,ExDate from MigDatabase where substr(hh,1,3)='"+ Village +"' and ExType='"+ EvCode +"' and (Name like('"+ Name +"%') or PNo like('"+ Name +"%'))order by name asc";


		if(EvCode.equals("53"))
		{
    		/*if(Search.equals("yes"))
    		{
        		SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
    		}
    		else
    		{
	    		C.Save("delete from migdata");
	    		
		    	SQL =  "Insert into migdata(hh, Sno,Pno,Name,ExDate) select (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where Vill='"+ Village +"' and ExType='53' and Name like('"+ Name +"%')";
		    	SQL += "and not exists(select sno from Member where pno=m.pno and entype='23' and endate=m.exdate)order by Name asc";
		    	C.Save(SQL);
		    	
		    	SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
    		}*/
		}
		else if(EvCode.equals("52"))
		{
        	/*if(C.Existence("Select vill from mdssvill where cluster='"+ g.getClusterCode() +"' and vill='"+ Village +"'"))
        	{
        		if(Search.equals("yes"))
        		{
	        		SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
        		}
        		else
        		{
	        		C.Save("delete from migdata");
	    	    	SQL =  "Insert into migdata(hh, Sno,Pno,Name,ExDate) select (vill||bari||hh)hh, Sno,Pno,Name,ExDate from Member m where Vill='"+ Village +"' and ExType='52' and Name like('"+ Name +"%')";
	    	    	SQL += "and not exists(select sno from Member where pno=m.pno and entype='22' and endate=m.exdate)order by Name asc";  
	    	    	C.Save(SQL);
	    	    	
	    	    	SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
        		}
        	}
        	else
        	{
        		if(Search.equals("yes"))
        		{
	        		SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
        		}
        		else
        		{
	        		//need to download member from server
	        		String S  = "select (vill+bari+hh)hh, Sno,Pno,Name,ExDate from Member m where Vill='"+ Village +"' and ExType='52'";
	        			   S += " and not exists(select sno from Member where pno=m.pno and entype='22' and endate=m.exdate)";
				
	        		C.Save("delete from migdata");
	        		C.DownloadMigData(S,"MigData", "hh, Sno,Pno,Name,ExDate");
	        		SQL = "Select hh, Sno,Pno,Name,ExDate from MigData where Name like('"+ Name +"%') order by name asc";
        		}
        	}*/
		}
		Cursor cur1 = C.ReadData(SQL);

		cur1.moveToFirst();
		evmylist.clear();
		eList = null;
		evlist.setAdapter(null);

		while(!cur1.isAfterLast())
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("hh",   cur1.getString(cur1.getColumnIndex("hh")));
			map.put("sno",  cur1.getString(cur1.getColumnIndex("Sno")));
			map.put("pno",  cur1.getString(cur1.getColumnIndex("Pno")));
			map.put("name", cur1.getString(cur1.getColumnIndex("Name")));
			map.put("exdate", Global.DateConvertDMY(cur1.getString(cur1.getColumnIndex("ExDate"))));

			evmylist.add(map);

			eList = new SimpleAdapter(MemberEvents.this, evmylist, R.layout.migrationrow,
					new String[] {"sno","pno","name","exdate","hh"},
					new int[] {R.id.m_sno,R.id.m_pno,R.id.m_name,R.id.m_exdate,R.id.m_hhno});
			evlist.setAdapter(eList);

			cur1.moveToNext();
		}

		cur1.close();

		evlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view,int position, long id) {
				TextView m_hhno = (TextView) view.findViewById(R.id.m_hhno);
				TextView m_sno = (TextView) view.findViewById(R.id.m_sno);
				TextView m_pno = (TextView) view.findViewById(R.id.m_pno);
				TextView m_name = (TextView) view.findViewById(R.id.m_name);
				TextView m_exdate = (TextView) view.findViewById(R.id.m_exdate);

				TextView lblName = (TextView)d.findViewById(R.id.lblName);
				lblName.setText("Name: "+ m_name.getText().toString() +" [Outdate: "+ m_exdate.getText().toString() + "]");
				g.setPNo(m_pno.getText().toString().trim());
				g.setEvDate(m_exdate.getText().toString().trim());
				g.setPrevHousehold(m_hhno.getText().toString().trim());

				/*String temp = listView.getItemAtPosition(position).toString().replace("{","");
				temp = temp.replace("}","");
				temp = temp.replace("name=","");
				temp = temp.replace("pno=","");
				temp = temp.replace("sno=","");
				temp = temp.replace("exdate=","");
				temp = temp.replace("hh=","");
				String[] a = temp.split(",");

				TextView lblName = (TextView)d.findViewById(R.id.lblName);
				lblName.setText("Name: "+ a[0] +" [Outdate: "+ a[3] + "]");
				g.setPNo(a[1].trim());
				g.setEvDate(a[3].trim());
				g.setPrevHousehold(a[4].trim());*/

				/*String[] a = listView.getItemAtPosition(position).toString().split(",");
				String H = a[0].substring(4, a[0].length());
				String E = a[2].substring(8, a[2].length());
				String P = a[3].substring(5, a[3].length());
				String N = a[4].substring(6, a[4].length()-1);

				TextView lblName = (TextView)d.findViewById(R.id.lblName);
				lblName.setText("Name: "+ N +" [Outdate: "+ E + "]");
				g.setPNo(P);
				g.setEvDate(E);
				g.setPrevHousehold(H);*/
				//Object o =  listView.getItemAtPosition(position);
			}
		});
	}


	public class MigrationListAdapter extends BaseAdapter
	{
		private Context context;

		public MigrationListAdapter(Context c){
			context = c;
		}

		public int getCount() {
			return eList.getCount();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}


		public View getView(final int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.migrationrow, null);
			}


			final HashMap<String, String> o = (HashMap<String, String>) eList.getItem(position);

			TextView m_sno   = (TextView)convertView.findViewById(R.id.m_sno);
			TextView m_pno   = (TextView)convertView.findViewById(R.id.m_pno);
			TextView m_name  = (TextView)convertView.findViewById(R.id.m_name);
			TextView m_exdate= (TextView)convertView.findViewById(R.id.m_exdate);
			LinearLayout secRow = (LinearLayout)convertView.findViewById(R.id.secRow);

			m_sno.setText(o.get("sno").toString());
			m_pno.setText(o.get("pno").toString());
			m_name.setText(o.get("name").toString());
			m_exdate.setText(Global.DateConvertDMY(o.get("exdate").toString()));

			final TextView lblName = (TextView)convertView.findViewById(R.id.lblName);
			m_sno.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					g.setPNo(o.get("pno").toString());
					//lblName.setText(o.get("name").toString());
				}
			});
			m_pno.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					g.setPNo(o.get("pno").toString());
					//lblName.setText(o.get("name").toString());
				}
			});
			m_name.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					g.setPNo(o.get("pno").toString());
					//lblName.setText(o.get("name").toString());
				}
			});
			m_exdate.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					g.setPNo(o.get("pno").toString());
					//lblName.setText(o.get("name").toString());
				}
			});

			/*secRow.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					g.setPNo(o.get("pno").toString());
					//lblName.setText(o.get("name").toString());
				}
			});*/

			return convertView;
		}
	}



	//Member Check
	//***************************************************************************************************************************
	public void MemberCheck(String HHold)
	{
		try
		{
			String ErrMsg = "";
    		
    		/*
	        //Check Household head present or not
	        if(C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and status='m'  and cast(Rth as int)=1 and (length(extype)=0 or extype is null)")==false)
	        {
	        	ErrMsg = "খানায় কোন সক্রিয় খানা প্রধান নেই.*";
	        }

	        //-check the more than one active head exists/not
	        if(C.Existence("Select vill,bari,hh,rth,count(*)Total from tTrans where vill||bari||hh='"+ HHold +"' and status='m'  and cast(Rth as int)=1 and (length(extype)=0 or extype is null)group by vill,bari,hh,rth having count(*)>0")==true)
	        {
	        	ErrMsg += "খানায় একের বেশী খানা প্রাধান থাকতে পারে না.*";
	        }*/

			String LNo = "";
			String RelCode = "";
			String Sex = "";
			String MNo = "";
			String FNo = "";
			String Age = "";
			String MS = "";
			String Sp1 = "";
			String Sp2 = "";
			String Sp3 = "";
			String Sp4 = "";

			Cursor cur = C.ReadData("Select SNo,Rth,Sex,cast((julianday(date('now'))-julianday(bdate))/365.25 as int)age,Mono,Fano,Sno,Ms,Sp1,Sp2,Sp3,Sp4 from tTrans where vill||bari||hh='"+ HHold +"' and status='m'and (length(extype)=0 or extype is null) order by cast(SNo as int) asc");
			cur.moveToFirst();
			while(!cur.isAfterLast())
			{
				LNo 	= cur.getString(cur.getColumnIndex("Sno"));
				RelCode = cur.getString(cur.getColumnIndex("Rth"));
				Sex 	= cur.getString(cur.getColumnIndex("Sex"));
				MNo 	= cur.getString(cur.getColumnIndex("Mono"));
				FNo 	= cur.getString(cur.getColumnIndex("Fano"));
				Age 	= cur.getString(cur.getColumnIndex("age"));
				MS 		= cur.getString(cur.getColumnIndex("Ms"));
				Sp1 	= cur.getString(cur.getColumnIndex("Sp1"));
				Sp2 	= cur.getString(cur.getColumnIndex("Sp2"));
				Sp3 	= cur.getString(cur.getColumnIndex("Sp3"));
				Sp4 	= cur.getString(cur.getColumnIndex("Sp4"));

				switch(Integer.valueOf(RelCode)) {

					case 1:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
								CheckMFLine(HHold, LNo, 4, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
						}
						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
								CheckMFLine(HHold, LNo, 4, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 2);
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//check the spouse's of the member
							if((Sp1.length()==0 | Sp1.equals("00") | Sp1.equals("0")) & C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and rth='02' and ms='31' and (ExType is null or length(ExType)=0)"))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": স্বামী/স্ত্রী এর নাম্বার খালি হবে না (খানা প্রধানের স্বামী/স্ত্রী এই খানার সদস্য).*";
							}


							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 2, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 2, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 2, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 2, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}

						}

						break;

					case 2:
						//*********************************************************************************************			        	
						//- If relation=2 then sex opposite to HH head----------------------
						if(C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and sex='"+ Sex +"' and (ExType is null or length(ExType)=0)"))
						{
							ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": সেক্স অবশ্যই খানা প্রধানের সেক্স এর বিপরীত হতে হবে.*";
						}

						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 10, 77, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 10, 77, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//check the spouse's of the member
							if((Sp1.length()==0 | Sp1.equals("00") | Sp1.equals("0")) & C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and rth='01' and ms='31' and (ExType is null or length(ExType)=0)"))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": খানা প্রধানের স্বামী/স্ত্রী এর স্বামী/স্ত্রী এর নাম্বার অবশ্যই থাকতে হবে.*";
							}

							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 1, 77, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 1, 77, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 1, 77, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 1, 77, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 3:
						//*********************************************************************************************			        	
						//- If relation=3 & sex of head=1 then ----------------------
						if((FNo.length()==0 | FNo.equals("00") | FNo.equals("0")) & C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and sex='1' and (ExType is null or length(ExType)=0)"))
						{
							ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": বাবার সিরিয়াল নাম্বার ০০ হবে না, কারন বাবা খানা প্রধান.*";
						}

						//- If relation=3 & sex of head=1 then ----------------------
						if((MNo.length()==0 | MNo.equals("00") | MNo.equals("0")) & C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and sex='2' and (ExType is null or length(ExType)=0)"))
						{
							ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": মা এর সিরিয়াল নাম্বার ০০ হবে না, কারন মা খানা প্রধান.*";
						}

						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 1, 2, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 1, 2, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 15, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 15, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 15, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 15, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}

						}
						break;
					case 4:
						//*********************************************************************************************			        	
						if(Sex.equals("2") & !C.Existence("Select * from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and mono<>'00' and (ExType is null or length(ExType)=0)"))
						{
							ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": খানা প্রধানের মা এর সিরিয়াল নাম্বার খালি হবে না.*";
						}
						else if(Sex.equals("1") & !C.Existence("Select * from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and mono<>'00' and (ExType is null or length(ExType)=0)"))
						{
							ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": খানা প্রধানের বাবা এর সিরিয়াল নাম্বার খালি হবে না.*";
						}

						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//-Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 4, 11, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 4, 11, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 4, 11, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 4, 11, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 5:
						//*********************************************************************************************			        	
						//-Check mother's/father's no between hh head and brother's/sister's of head----------------------
						Cursor cur1 = C.ReadData("Select mono,fano from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='01' and (ExType is null or length(ExType)=0)");
						cur1.moveToFirst();
						while(!cur1.isAfterLast())
						{
							if(!MNo.equals(cur1.getString(cur1.getColumnIndex("mono"))))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": সদস্য এবং খানা প্রধানের মা এর সিরিয়াল একই হবে.*";
							}
							else if(!FNo.equals(cur1.getString(cur1.getColumnIndex("fano"))))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": সদস্য এবং খানা প্রধানের বাবা এর সিরিয়াল একই হবে.*";
							}
							cur1.moveToNext();
						}
						cur1.close();

						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 4, 15, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}
						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 4, 15, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 17, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}
							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 17, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}
							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 17, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 17, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 6:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 7, 77, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}
						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 7, 77, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 6, 77, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}
							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 6, 77, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 6, 77, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 6, 77, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 7:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 7, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 8:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 3, 15, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 3, 15, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 8, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 8, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 8, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 8, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 9:
						//*********************************************************************************************
						//-Check mother's/father's no between hh head and brother's/sister's of head----------------------
						Cursor cur2 = C.ReadData("Select mono,fano from tTrans where vill||bari||hh='"+ HHold +"' and Status='m' and Rth='02' and (ExType is null or length(ExType)=0)");
						cur2.moveToFirst();
						while(!cur2.isAfterLast())
						{
							if(!MNo.equals(cur2.getString(cur2.getColumnIndex("mono"))))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": সদস্য এবং খানা প্রধানের মা এর সিরিয়াল একই হবে.*";
							}
							else if(!FNo.equals(cur2.getString(cur2.getColumnIndex("fano"))))
							{
								ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ LNo +": সদস্য এবং খানা প্রধানের বাবা এর সিরিয়াল একই হবে.*";
							}
							cur2.moveToNext();
						}
						cur2.close();

						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 10, 16, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 10, 16, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}


						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}

						}
						break;

					case 10:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 10, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 10, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 10, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 10, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 11:
						//*********************************************************************************************			        	
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 11, 4, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 11, 4, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 11, 4, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 11, 4, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 12:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 11, 2, 77, 99, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 11, 2, 77, 99, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 13:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}


						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}

						}
						break;

					case 14:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 11, 4, 77, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 11, 4, 77, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 15:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 5, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 5, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 3, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 3, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 3, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 3, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 16:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 5, 17, 9, 77, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 5, 17, 9, 77, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 17:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 5, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 5, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 5, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 5, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 18:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, 0, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, 0, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, 0, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 0, 0, 0, 0, 0, 0, 0, 0, 0, Sp4, Sex);
								}
							}
						}
						break;

					case 77:
						//*********************************************************************************************
						//-Mother Serial No-----------------
						if(MNo.length()!=0 & !MNo.equals("00") & !MNo.equals("0"))
						{
							if(!MemExist(HHold, MNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 8, 9, 16, 6, 12, 13, 14, MNo, Age, 2);
							}
						}

						//-Father Serial No------------------------------------------------------------------------
						if(FNo.length()!=0 & !FNo.equals("00") & !FNo.equals("0"))
						{
							if(!MemExist(HHold, FNo, LNo))
							{
								CheckMFLine(HHold, LNo, 77, 8, 9, 16, 6, 12, 13, 14, FNo, Age, 1);
							}
						}

						//Spouse Check
						if(MS.equals("31"))
						{
							//-Spouse-1------------------------------------------------------------------------
							if(Sp1.length()!=0 & !Sp1.equals("00") & !Sp1.equals("0"))
							{
								if(!SpExist(HHold, Sp1, 1, LNo))
								{
									CheckSpouseLine(HHold, LNo, 8, 9, 13, 16, 77, 12, 2, 6, 14, Sp1, Sex);
								}
							}

							//-Spouse-2------------------------------------------------------------------------
							if(Sp2.length()!=0 & !Sp2.equals("00") & !Sp2.equals("0"))
							{
								if(!SpExist(HHold, Sp2, 2, LNo))
								{
									CheckSpouseLine(HHold, LNo, 8, 9, 13, 16, 77, 12, 2, 6, 14, Sp2, Sex);
								}
							}

							//-Spouse-3------------------------------------------------------------------------
							if(Sp3.length()!=0 & !Sp3.equals("00") & !Sp3.equals("0"))
							{
								if(!SpExist(HHold, Sp3, 3, LNo))
								{
									CheckSpouseLine(HHold, LNo, 8, 9, 13, 16, 77, 12, 2, 6, 14, Sp3, Sex);
								}
							}

							//-Spouse-4------------------------------------------------------------------------
							if(Sp4.length()!=0 & !Sp4.equals("00") & !Sp4.equals("0"))
							{
								if(!SpExist(HHold, Sp4, 4, LNo))
								{
									CheckSpouseLine(HHold, LNo, 8, 9, 13, 16, 77, 12, 2, 6, 14, Sp4, Sex);
								}
							}
						}
						break;
					default:

				}
				cur.moveToNext();
			}
			cur.close();
		}
		catch(Exception ex)
		{
			Connection.MessageBox(MemberEvents.this, ex.getMessage());
			return;
		}
	        
    	
    /*
	        
	'** OVERALL MEMBER CHECK
	'--------------------------------------------------------------
	        Dim H As New ADODB.Recordset
	        H.Open "SELECT * FROM TTRANS WHERE STATUS='m' AND HH='" & Household & "' ORDER BY CONVERT(INT,SNO) ASC", Conn
	        
	        Dim MTS, S1, S2, S3, S4
	            While Not H.EOF
	                
	                '** SPOUSES NO CHECK FOR NOT 31 CODE(30,32,33,34)
	                If (IsNull(H!ExType) = True) And ((H!MS = 30 Or H!MS = 32 Or H!MS = 33 Or H!MS = 34) And (Val(IIf(IsNull(H!Sp1) = True, "", H!Sp1)) <> 0 Or Val(IIf(IsNull(H!Sp2) = True, "", H!Sp2)) <> 0 Or Val(IIf(IsNull(H!Sp3) = True, "", H!Sp3)) <> 0 Or Val(IIf(IsNull(H!Sp4) = True, "", H!Sp4)) <> 0)) Then
	                
	                    MTS = H!MS
	                    S1 = IIf(IsNull(H!Sp1) = True, "", H!Sp1)
	                    S2 = IIf(IsNull(H!Sp1) = True, "", H!Sp1)
	                    S3 = IIf(IsNull(H!Sp1) = True, "", H!Sp1)
	                    S4 = IIf(IsNull(H!Sp1) = True, "", H!Sp1)
	                
	                    ErrMsg = ErrMsg + "Spouse's serial need to update for( SNo=" & H!SNO & " : Name= " & H!Name & " : MS= " & MTS & " : SP1=" & S1 & " : SP2=" & S2 & " : SP3=" & S3 & " : SP4=" & S4 & " ).*"
	                End If
	                
	                '**BIRTH DATE AND ENROLLMENT DATE SHOULD BE SAME FOR EVENT 25
	                If H!EnType = 25 And (H!BDate <> H!EnDate) Then
	                    ErrMsg = ErrMsg + "Event: 25 , Birth date is not same as entry date ( SNo=" & H!SNO & " : Name= " & H!Name & " : Birth Date: " & H!BDate & " : Entry Date: " & H!EnDate & " ).*"
	                End If
	                
	                '**EXIT DATE SHOULD BE GREATER THAN BIRTH DATE
	                If H!ExDate < H!BDate Then
	                    ErrMsg = ErrMsg + "Exit date should be greater than birth date ( SNo=" & H!SNO & " : Name= " & H!Name & " : Birth Date: " & H!BDate & " : Exit Date: " & H!ExDate & " ).*"
	                End If
	                
	                '**EXIT DATE SHOULD BE GREATER THAN ENTRY DATE
	                If H!ExDate < H!EnDate Then
	                    ErrMsg = ErrMsg + "Exit date should be greater than entry date ( SNo=" & H!SNO & " : Name= " & H!Name & " : Entry Date: " & H!EnDate & " : Exit Date: " & H!ExDate & " ).*"
	                End If
	                
	            H.MoveNext
	            Wend
	        H.Close
	        Set H = Nothing
	
	'-CHECK FATHER, MOTHER-------------------------------------------------------------
	        Dim FM As New ADODB.Recordset
	        Dim SQL As String
	        SQL = "SELECT 'F' FM,* FROM TTRANS WHERE HH+SNO IN(SELECT DISTINCT HH+FANO FROM TTRANS WHERE STATUS='M' AND HH='" & Household & "' AND CONVERT(INT,FANO)>0) AND STATUS='M' UNION"
	        SQL = SQL + " SELECT 'M' FM,* FROM TTRANS WHERE HH+SNO IN(SELECT DISTINCT HH+MONO FROM TTRANS WHERE STATUS='M' AND HH='" & Household & "' AND CONVERT(INT,MONO)>0) AND STATUS='M'"
	        
	        FM.Open SQL, Conn
	        
	            While Not FM.EOF
	                If FM!FM = "F" And FM!Sex = 2 Then
	                    ErrMsg = ErrMsg + "Serial No=" & FM!SNO & " :Person is father but its sex is female.*"
	                ElseIf FM!FM = "M" And FM!Sex = 1 Then
	                    ErrMsg = ErrMsg + "Serial No=" & FM!SNO & " :Person is mother but its sex is male.*"
	                End If
	                FM.MoveNext
	            Wend
	        
	        FM.Close
	        Set FM = Nothing
	
	'--------------------------------------------------------------
	            Dim i As Integer
	            Dim MSG As String
	            Dim A
	            A = Split(ErrMsg, "*")
	                For i = 0 To UBound(A)
	                    MSG = MSG + vbCrLf & A(i)
	                Next
	        '-----------------------------------------------------
	            If Len(ErrMsg) = 0 Then
	                'cmdSave.Enabled = True
	                'cmdSave.SetFocus
	                cmdClose.SetFocus
	            Else
	                MsgBox MSG, , "Consistency Check"
	            End If
	        '-----------------------------------------------------
	        
	        */

	}



	//Check the Member's active/not
	private Boolean MemExist(String HHold, String FMno, String LNo)
	{
		Boolean MemExist = false;

		if (!C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and status='m'  and SNo='"+ FMno +"' "))
		{
			ErrMsg += "\n-> সদস্যের নাম্বার="+ LNo +": বাবা/মা এর সিরিয়াল নাম্বার "+ FMno +" এই খানার সদস্য নয়।*";
			MemExist = true;
		}
		else if(!C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and status='m'  and SNo='"+ FMno +"' and (ExType is null or length(ExType)=0)"))
		{
			ErrMsg += "\n-> সদস্যের নাম্বার="+ LNo +": বাবা/মা এর সিরিয়াল নাম্বার "+ FMno +" এই খানায় সক্রিয় নয়।*";
			MemExist = true;
		}
		return MemExist;
	}


	//Check the Spouse's active/not
	private Boolean SpExist(String HHold, String SPLine, int SpNo, String LNo)
	{
		Boolean SpExist = false;
		if(!C.Existence("Select vill from tTrans where vill||bari||hh='"+ HHold +"' and status='m'  and SNo='"+ SPLine +"'"))
		{
			ErrMsg += "\n-> সদস্যের নাম্বার="+ LNo +": Spouse-"+ SpNo +" এই খানার সদস্য নয়।*";
			SpExist = true;
		}
		else if(!C.Existence("Select vill from tTrans where Vill||bari||hh='"+ HHold +"'  and status='m' and SNo='"+ SPLine +"' and (ExType is null or length(ExType)=0)"))
		{
			ErrMsg += "\n-> সদস্যের নাম্বার="+ LNo +": Spouse-"+ SpNo +" এই খানায় সক্রিয় নয়।*";
			SpExist = true;
		}
		return SpExist;
	}

	//Check Father,Mother Serial No
	//--------------------------------------------------------------------
	private Boolean CheckMFLine(String HH, String LineNo, int RelCode1, int RelCode2, int RelCode3, int RelCode4, int RelCode5, int RelCode6, int RelCode7, int RelCode8, String MFNo, String Age, int Sex)
	{
		Boolean CheckMFLine = true;
		Cursor cur = C.ReadData("Select rth,sex,ms,extype,cast((julianday(date('now'))-julianday(bdate))/365.25 as int)age from tTrans where vill||bari||hh='"+ HH +"' and status='m'  and SNo='"+ MFNo +"'");
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			String rth = cur.getString(cur.getColumnIndex("Rth"));
			String sex = cur.getString(cur.getColumnIndex("Sex"));
			String ms  = cur.getString(cur.getColumnIndex("Ms"));
			String extype = cur.getString(cur.getColumnIndex("ExType"));
			int age = Integer.parseInt(cur.getString(cur.getColumnIndex("age")));

			//check activeness
			if(extype.trim().length()!=0)
			{
				ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা/বাবা - "+ MFNo +" এই খানায় সক্রিয় নয়।*";
				CheckMFLine = false;
			}

			int R = Integer.valueOf(rth);
			//Check Relation
			if(R!=RelCode1 &
					R!=RelCode2 &
					R!=RelCode3 &
					R!=RelCode4 &
					R!=RelCode5 &
					R!=RelCode6 &
					R!=RelCode7 &
					R!=RelCode8)
			{
				ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা/বাবা - "+ MFNo +" এর সাথে সদস্যের সম্পর্ক সঠিক নয়।*";
				CheckMFLine = false;
			}

			//Check sex
			if(Sex == 2)
			{
				if(!sex.equals("2"))
				{
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা -"+ MFNo +" অবশ্যই মহিলা হতে হবে।*";
					CheckMFLine = false;
				}
			}
			else if(Sex == 1)
			{
				if(!sex.equals("1"))
				{
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": বাবা - "+ MFNo +" অবশ্যই পুরুষ হতে হবে।*";
					CheckMFLine = false;
				}
			}


			//Check age difference between 0 and 10
			if ((age - Integer.parseInt(Age) < 10 & (age - Integer.parseInt(Age)) > 0))
			{
				if(sex.equals("1"))
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": বাবা - "+ MFNo +" এর সাথে সন্তানের বয়সের পার্থক্য "+ (age - Integer.parseInt(Age)) +" বছর সঠিক নয়।*";
				else if(sex.equals("2"))
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা - "+ MFNo +" এর সাথে সন্তানের বয়সের পার্থক্য "+ (age - Integer.parseInt(Age)) +" বছর সঠিক নয়।*";
			}

			//Check age difference less than zero
			else if ((age - Integer.parseInt(Age)) <= 0)
			{
				if(sex.equals("1"))
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": বাবা - "+ MFNo +" এর বয়স "+ age +" অবশ্যই সন্তানের বয়স "+ Age +" এর বেশী হবে।*";
				else if(sex.equals("2"))
					ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা - "+ MFNo +" এর বয়স "+ age +" অবশ্যই সন্তানের বয়স "+ Age +" এর বেশী হবে।*";
			}

			//Check maritial status
			if(cur.getString(cur.getColumnIndex("Ms")).equals("30"))
			{
				ErrMsg += "\n-> সদস্যের নাম্বার="+ LineNo +": মা/বাবা -"+ MFNo +" অবশ্যই বিবাহিত হতে হবে।*";
				CheckMFLine = false;
			}


			cur.moveToNext();
		}
		cur.close();

		return CheckMFLine;
	}


	//Check Spouse Serial No
	//--------------------------------------------------------------------
	private boolean CheckSpouseLine(String HH, String SPLine, int RelCode, int RelCode1 , int RelCode2, int RelCode3, int RelCode4, int RelCode5, int RelCode6, int RelCode7, int RelCode8, String SpNo, String Sex)
	{
		String ErrMsg = "";
		String Sp1;
		String Sp2;
		String Sp3;
		String Sp4;
		String Rth;
		String sex;
		String MS;
		Boolean SpousesLineNo = true;

		Cursor cur = C.ReadData("Select ExType,Sp1,Sp2,Sp3,Sp4,Rth,Sex,MS from tTrans where vill||bari||hh='"+ HH +"'  and status='m' and SNo='"+ SpNo +"'");
		cur.moveToFirst();
		while(!cur.isAfterLast())
		{
			Sp1 = cur.getString(cur.getColumnIndex("Sp1"));
			Sp2 = cur.getString(cur.getColumnIndex("Sp2"));
			Sp3 = cur.getString(cur.getColumnIndex("Sp3"));
			Sp4 = cur.getString(cur.getColumnIndex("Sp4"));
			Rth = cur.getString(cur.getColumnIndex("Rth"));
			sex = cur.getString(cur.getColumnIndex("Sex"));
			MS = cur.getString(cur.getColumnIndex("Ms"));

			int R= Integer.valueOf(Rth);

			//check activeness
			if(cur.getString(cur.getColumnIndex("ExType")).length()!=0)
			{
				ErrMsg = ErrMsg + "\n-> সদস্যের নাম্বার="+ SPLine +": সদস্যের স্বামী/স্ত্রী এই খানায় সক্রিয় নয় "+ SpNo +".*";
				SpousesLineNo = false;
			}
	           /*if(RelCode == 1)
	           {
		        	if(C.Existence("Select vill from tTrans where vill||bari||hh='"+ HH +"' and status='m' and rth='02' and length(extype)=0"))
		        	{
		        		ErrMsg = ErrMsg + "সদস্যের নাম্বার="+ SPLine +": The member's spouse is "+ SpNo +" but spouse's number of Serial No "+ SpNo +" blank.*";
		        		SpousesLineNo = false;
		        	}
	           }*/

			//Spouse Check--------------------------------------------------------------------
			if(!Sp1.equals("+ SpLine +") & !Sp2.equals("+ SpLine +") & !Sp3.equals("+ SpLine +") & !Sp4.equals("+ SpLine +"))
			{
				ErrMsg = ErrMsg + "\n-> সদস্যের নাম্বার="+ SPLine +": সদস্যের স্বামী/স্ত্রী এর নাম্বার  "+ SpNo +" কিন্ত সদস্যের নাম্বার  "+ SpNo +" এর স্বামী/স্ত্রী এর নাম্বার খালি আছে.*";
				SpousesLineNo = false;
			}

			//-Check relation------------------------------------------------------------------
			//if(!Rth.equals("+ RelCode +") & !Rth.equals("+ RelCode1 +") & !Rth.equals("+ RelCode2 +") & !Rth.equals("+ RelCode3 +") & !Rth.equals("+ RelCode4 +") & !Rth.equals("+ RelCode5 +") & !Rth.equals("+ RelCode6 +") & !Rth.equals("+ RelCode7 +") & !Rth.equals("+ RelCode8 +"))
			if(R!=RelCode1 &
					R!=RelCode2 &
					R!=RelCode3 &
					R!=RelCode4 &
					R!=RelCode5 &
					R!=RelCode6 &
					R!=RelCode7 &
					R!=RelCode8)
			{
				ErrMsg = ErrMsg + "\n-> সদস্যের নাম্বার="+ SPLine +": স্বামী/স্ত্রী এর সম্পর্ক সদস্যের সিরিয়াল :"+ SpNo +" এর সাথে সঠিক নয়.*";
				SpousesLineNo = false;
			}

			//-Check sex-----------------------------------------------------------------------
			if(sex.equals("+ Sex +"))
			{
				ErrMsg = ErrMsg + "\n-> সদস্যের নাম্বার="+ SPLine +": স্বামী/স্ত্রী এর সেক্স সদস্যের সিরিয়াল :"+ SpNo +" এর বিপরীত হতে হবে.*";
				SpousesLineNo = false;
			}

			//-Check maritial status-----------------------------------------------------------
			if(!MS.equals("31"))
			{
				ErrMsg = ErrMsg + "\n-> সদস্যের নাম্বার="+ SPLine +": স্বামী/স্ত্রী এর মেরিটাল স্ট্যাটাস অবশ্যই ৩১ হতে হবে-সদস্যের সিরিয়াল : "+ SpNo +".*";
				SpousesLineNo = false;
			}

			cur.moveToNext();
		}
		cur.close();

		return SpousesLineNo;
	}

	private String ProcessTransaction(String Household, String Rnd)
	{
		String SQLS = "";
		ErrMsg = "";

		if(!g.getRsNo().equals("77"))
		{
			//household member available/not
			if(!C.Existence("Select vill from tTrans where status='m' and vill||bari||hh='"+ Household +"' and (extype is null or length(extype)=0)"))
			{
				ErrMsg += "\n-> খানায় কমপক্ষে একজন সদস্য সক্রিয় থাকতে হবে।";
			}
			//at least one household head should be available
			if(!C.Existence("select vill from tTrans where status='m' and vill||bari||hh='"+ Household +"' and rth='01' and (extype is null or length(extype)=0)"))
			{
				ErrMsg += "\n-> খানায় একজন খানা প্রধান অবশ্যই থাকতে হবে।";
			}
			//only one active household head applicable
			if(C.Existence("select count(*) from tTrans where status='m' and vill||bari||hh='"+ Household +"' and rth='01' and (extype is null or length(extype)=0) group by vill||bari||hh having count(*)>1"))
			{
				ErrMsg += "\n-> খানায় একের বেশী খানা প্রধান থাকতে পারে না।";
			}
		}

		//Not pregnant event(40) missing
		SQLS = "SELECT M.SNO as sno,M.NAME as name";
		SQLS += " FROM TTRANS M WHERE M.STATUS='m' AND M.VILL||M.BARI||M.HH='"+ Household +"' AND M.MS='31' AND cast((julianday(date('now'))-julianday(bdate))/365.25 as int)<50  ";
		SQLS += " AND M.SEX='2' AND ifnull(M.PSTAT,'0')<>'41' AND (EXTYPE IS NULL OR LENGTH(EXTYPE)=0) and (posmig IS NULL OR LENGTH(posmig)=0) AND NOT EXISTS";
		SQLS += " (SELECT VILL,BARI,HH,SNO,PNO,EVTYPE,RND FROM TTRANS WHERE STATUS='e' AND vill||bari||HH=m.vill||m.bari||M.HH AND SNO=M.SNO AND EVTYPE IN('40','49') AND RND='"+ Rnd +"'";
		SQLS += " UNION SELECT VILL,BARI,HH,SNO,PNO,EVTYPE,RND FROM EVENTS WHERE EVTYPE IN('40','49') AND RND='"+ Rnd +"' AND PNO=M.PNO)";

		Cursor cur40 = C.ReadData(SQLS);
		cur40.moveToFirst();
		while(!cur40.isAfterLast())
		{
			ErrMsg += "\n-> ইভেন্ট ৪০ ঘটানো হয় নাই (সিরিয়াল নাম্বার= "+  cur40.getString(cur40.getColumnIndex("sno")) +" এবং নাম= "+ cur40.getString(cur40.getColumnIndex("name")) +" ).";

			cur40.moveToNext();
		}
		cur40.close();


		//Pregnancy history missing: 26 Nov 2013
		SQLS  = "select sno as sno, (case when pno is null or length(pno)=0 then 'pno' else pno end)as pno, t.Name as name from tTrans t where t.status='m' and";
		SQLS += " t.Vill||t.Bari||t.Hh='"+ Household +"' and length(extype)=0 and length(posmig)=0";
		SQLS += " and t.Sex='2' and t.ms<>'30' and ((julianday(date('now'))-julianday(t.bdate))/365.25)<50";

		Cursor curphis = C.ReadData(SQLS);
		curphis.moveToFirst();
		while(!curphis.isAfterLast())
		{
			if(!C.Existence("select vill from tTrans where vill||bari||hh='"+ Household +"' and sno='"+ curphis.getString(curphis.getColumnIndex("sno")) +"' and status='p'") & !C.Existence("select vill from PregHis where pno='"+ curphis.getString(curphis.getColumnIndex("pno")) +"'"))
			{
				ErrMsg += "\n-> RHQ হয় নাই (সিরিয়াল নাম্বার= "+  curphis.getString(curphis.getColumnIndex("sno")) +" এবং নাম= "+ curphis.getString(curphis.getColumnIndex("name")) +" ).";
			}
			curphis.moveToNext();
		}
		curphis.close();



		//occupation missing (age >= 12 years): 25 Jan 2018
		SQLS = "Select SNo as sno, Name as name from tTrans where status='m' and VILL||BARI||HH='"+ Household +"' and cast((julianday(date('now'))-julianday(bdate))/365.25 as int)>=12 and length(OCp)=0 and (extype is null or length(extype)=0)";
		Cursor CR = null;
		CR = C.ReadData(SQLS);
		CR.moveToFirst();
		while(!CR.isAfterLast())
		{
			ErrMsg += "\n-> বয়স ১২ এর সমান/বেশী হলে পেশা থাকতে হবে(সিরিয়াল নাম্বার= "+  CR.getString(CR.getColumnIndex("sno")) +" এবং নাম= "+ CR.getString(CR.getColumnIndex("name")) +" ).";

			CR.moveToNext();
		}
		CR.close();


		//age of last SES collection
		if(C.Existence("select vill from tTrans where status='s' and vill||bari||hh='"+ Household +"'"))
		{
			//***need to collect ses again from 22 rnd
			//int sesage = Integer.parseInt(C.ReturnSingleValue("select cast((julianday(date('now'))-julianday(vdate))/365.25 as int)ageses from tTrans where status='s' and vill||bari||hh='"+ Household +"' order by sesno desc limit 1"));
			//if(sesage >= 3)
			//	ErrMsg += "-> SES এর বয়স ৩ বছরের বেশী হয়েছে, আবার সংগ্রহ করতে হবে।\n";
		}
		else
		{
			ErrMsg += "\n-> SES এর তথ্য সংগ্রহ করতে হবে।";
		}

		//Immunization Information : 04 Jan 2014
		SQLS  = "Select Vill as vill, bari as bari, hh as hh, SNo as sno,ifnull(PNo,'') as pno, Name as name from tTrans t where status='m' and length(extype)=0 and vill||bari||hh='"+ Household +"'";
		SQLS += " and cast((julianday(date('now'))-julianday(bdate))/30.44 as int)<=59 ";

		Cursor IM = null;
		IM = C.ReadData(SQLS);
		IM.moveToFirst();
		String VillBariHHSNo = "";
		while(!IM.isAfterLast())
		{
			VillBariHHSNo = IM.getString(IM.getColumnIndex("vill"))+IM.getString(IM.getColumnIndex("bari"))+IM.getString(IM.getColumnIndex("hh"))+IM.getString(IM.getColumnIndex("sno"));
			if(IM.getString(IM.getColumnIndex("pno")).toString().length()==0)
			{
				if(!C.Existence("select vill from ImmunizationTemp where vill||bari||hh||sno='"+ VillBariHHSNo +"'"))
				{
					ErrMsg += "\n-> Immunization এর তথ্য সংগ্রহ করতে হবে(সিরিয়াল নাম্বার= "+  IM.getString(IM.getColumnIndex("sno")) +" এবং নাম= "+ IM.getString(IM.getColumnIndex("name")) +" ).";
				}
			}
			else
			{
				if(!C.Existence("select vill from ImmunizationTemp where pno='"+ IM.getString(IM.getColumnIndex("pno")) +"'"))
				{
					ErrMsg += "\n-> Immunization এর তথ্য সংগ্রহ করতে হবে(সিরিয়াল নাম্বার= "+  IM.getString(IM.getColumnIndex("sno")) +" এবং নাম= "+ IM.getString(IM.getColumnIndex("name")) +" ).";
				}
			}
			//ErrMsg += "\n-> Immunization এর তথ্য সংগ্রহ করতে হবে(সিরিয়াল নাম্বার= "+  IM.getString(IM.getColumnIndex("sno")) +" এবং নাম= "+ IM.getString(IM.getColumnIndex("name")) +" ).";

			IM.moveToNext();
		}
		IM.close();


		//03 Jun 2015
		SQLS  = "Select Vill as vill, bari as bari, hh as hh, SNo as sno, Name as name from tTrans t where status='m' and extype='55' and vill||bari||hh='"+ Household +"' and CAST(strftime('%Y', ifnull(t.ExDate,'')) AS INT)>=2014";
		//and cast((julianday(date('now'))-julianday(bdate))/365.25 as int) between 13 and 49

		Cursor D = null;
		D = C.ReadData(SQLS);
		D.moveToFirst();
		while(!D.isAfterLast())
		{
			VillBariHHSNo = D.getString(D.getColumnIndex("vill"))+D.getString(D.getColumnIndex("bari"))+D.getString(D.getColumnIndex("hh"))+D.getString(D.getColumnIndex("sno"));

			if(!C.Existence("select vill from Death_Temp where vill||bari||hh||sno='"+ VillBariHHSNo +"'"))
			{
				ErrMsg += "\n-> Death এর তথ্য সংগ্রহ করতে হবে(সিরিয়াল নাম্বার= "+  D.getString(D.getColumnIndex("sno")) +" এবং নাম= "+ D.getString(D.getColumnIndex("name")) +" ).";
			}

			D.moveToNext();
		}
		D.close();


		//Phone number missing: 27 Sep 2014
		if(C.Existence("select vill from tTrans where status='h' and vill||bari||hh='"+ Household +"' and length(contactno)=0"))
		{
			ErrMsg += "\n-> খানার যোগাযোগের(ফোন/মোবাইল) নাম্বার নেয়া হয়নি।.";
		}


		//09 Apr 2014
		//stop on 23 Jun 2014
		//MemberCheck(Household);

		//Stop process if any error have
		if (ErrMsg.length()!=0)
		{
			return ErrMsg;
		}
		else
		{
			return FinalDataProcess(Household, Rnd);
		}
	}


	//data transfer to main tables
	//***********************************************************************************************
	private String FinalDataProcess(String Household, String Rnd)
	{
		String Err = "";
		try
		{
			//C.Save("Delete from PregHis where vill||bari||hh='"+ Household +"'");
			C.Save("Delete from SES where vill||bari||hh='"+ Household +"'");

			//Search Maximun PNo from Member table
			//-- ---------------------------------------
			String CP = C.ReturnSingleValue("Select (ifnull(max(cast(substr(pno,4,8)as int)),0)+1)MaxPno from Member where substr(pno,1,3)='"+ Global.Left(Household, 3)  +"' group by substr(pno,1,3)");
			int CPN = Integer.parseInt(CP.length()==0?"1":CP);

			Cursor cur = C.ReadData("Select Rth,Sno,PNo,ExType from tTrans where status='m' and vill||bari||hh='"+ Household +"' and length(PNo)=0 order by cast(SNO as int) asc");
			cur.moveToFirst();
			String CPNo = "";
			while(!cur.isAfterLast())
			{
				//Generate Permanent no(PNo) for member, preghis, events table--------------------------
				CPNo = Global.Left(Household, 3) + Global.Right("00000"+String.valueOf(CPN),5);
				//-- --------------------------------------------------------------------------------
				C.Save("update tTrans set PNo='"+ CPNo +"' where Status='m' and vill||bari||hh ='"+ Household +"' and SNo='"+ cur.getString(cur.getColumnIndex("Sno")) +"'");
				C.Save("update tTrans set PNo='"+ CPNo +"' where Status='e' and vill||bari||hh ='"+ Household +"' and SNo='"+ cur.getString(cur.getColumnIndex("Sno")) +"'");
				C.Save("update tTrans set PNo='"+ CPNo +"' where Status='p' and vill||bari||hh ='"+ Household +"' and SNo='"+ cur.getString(cur.getColumnIndex("Sno")) +"'");
				C.Save("update ImmunizationTemp set PNo='"+ CPNo +"' Where vill||bari||hh ='"+ Household +"' and SNo='"+ cur.getString(cur.getColumnIndex("Sno")) +"'");

				CPN += 1;
				//-- --------------------------------------------------------------------------------	        	
				cur.moveToNext();
			}
			cur.close();

			//Household Table
			//-- ---------------------------------------
			String SQL="";
			String Head = "";
			String Active_Head = "";
			//***** need current household head name
			if(C.Existence("Select vill from Household where vill||bari||hh='"+ Household +"'"))
			{
				Active_Head = C.ReturnSingleValue("Select name from tTrans where status='m' and vill||bari||hh='"+ Household +"' and Rth='01' and length(ExType)=0 order by date(endate) desc limit 1");
				if(Active_Head==null | Active_Head.length()==0) {
					Head = C.ReturnSingleValue("Select name from tTrans where status='m' and vill||bari||hh='" + Household + "' and Rth='01' order by date(endate) desc limit 1");
				}else{
					Head = Active_Head;
				}
				Cursor curH = C.ReadData("Select ContactNo,ifnull(ExType,'')as ExType,ifnull(ExDate,'')as ExDate,HHHead,Note from tTrans where status='h' and vill||bari||hh='"+ Household +"'");
				curH.moveToFirst();
				while(!curH.isAfterLast())
				{
					SQL = "Update Household set upload='2',";
					SQL += " ExType='"+ curH.getString(curH.getColumnIndex("ExType")) +"',";
					SQL += " Note='"+ curH.getString(curH.getColumnIndex("Note")) +"',";
					SQL += " ContactNo='"+ curH.getString(curH.getColumnIndex("ContactNo")) +"',";
					if(Head != null & Head.length()!=0)
					{
						SQL += " ExDate='"+ curH.getString(curH.getColumnIndex("ExDate")) +"',";
						SQL += " HHHead='"+ Head +"'";
					}
					else
					{
						SQL += " ExDate='"+ curH.getString(curH.getColumnIndex("ExDate")) +"'";
					}

					SQL += " where vill||bari||hh='"+ Household +"'";
					C.Save(SQL);
					curH.moveToNext();
				}
				curH.close();

				C.Save(SQL);
			}
			else
			{
				Head = C.ReturnSingleValue("select name from tTrans where status='m' and vill||bari||hh='"+ Household +"' and Rth='01' and (length(extype)=0 or extype is null)");

				SQL = "Insert into Household";
				SQL += "(Vill, Bari, Hh, Pno, EnType, EnDate, ExType, ExDate, Rel, HHHead, Clust, Block, EnDt, Rnd,Upload,Note,ContactNo)";
				SQL += " select h.Vill, h.Bari, h.Hh, h.Pno, h.EnType, h.EnDate, h.ExType, h.ExDate, h.Rel, '"+ Head +"', h.Clust, h.Block, h.EnterDt,h.Rnd,'2',Note,ContactNo";
				SQL += " from tTrans h where h.status='h' and h.vill||h.bari||h.hh='"+ Household +"'";

				C.Save(SQL);
			}

			//-- -Member Table-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			SQL = "Select (case when m.vill is null then 'n' else 'o' end)as NewOld, t.Vill, t.Bari, t.Hh, t.Sno, t.Pno, t.Name, t.Rth, t.Sex, t.BDate, t.Age, t.Mono, t.Fano, t.Edu,";
			SQL += " t.Ms, t.Pstat, t.LmpDt, t.Sp1, t.Sp2, t.Sp3, t.Sp4, t.Ocp, t.EnType, t.EnDate,";
			SQL += " t.ExType, t.ExDate, t.PageNo, t.Status, t.Upload,t.PosMig,t.PosMigDate from tTrans t";
			SQL += " left outer join member m on t.vill||t.bari||t.hh||t.sno = m.vill||m.bari||m.hh||m.sno";
			SQL += " where t.status='m' and t.vill||t.bari||t.hh='"+ Household +"' order by cast(t.SNo as int) asc";

			Cursor curM = C.ReadData(SQL);
			curM.moveToFirst();
			while(!curM.isAfterLast())
			{
				if(curM.getString(curM.getColumnIndex("NewOld")).equals("n"))
				{
					SQL = "Insert into Member";
					SQL += " (Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, Upload,PosMig,PosMigDate)";
					SQL += " Select Vill, Bari, Hh, Sno, Pno, Name, Rth, Sex, BDate, Age, Mono, Fano, Edu, Ms, Pstat, LmpDt, Sp1, Sp2, Sp3, Sp4, Ocp, EnType, EnDate, ExType, ExDate, PageNo, Status, '2',PosMig,PosMigDate";
					SQL += " from tTrans where vill||bari||hh='"+ Household +"' and Status='m' and SNo='"+ curM.getString(curM.getColumnIndex("Sno")) +"'";

					C.Save(SQL);
				}
				else if(curM.getString(curM.getColumnIndex("NewOld")).equals("o"))
				{
					SQL = "Update Member Set ";
					SQL += " Name='"+ curM.getString(curM.getColumnIndex("Name")) +"',";
					SQL += " Rth='"+ curM.getString(curM.getColumnIndex("Rth")) +"',";
					SQL += " Sex='"+ curM.getString(curM.getColumnIndex("Sex")) +"',";
					SQL += " BDate='"+ curM.getString(curM.getColumnIndex("BDate")) +"',";
					SQL += " Mono='"+ curM.getString(curM.getColumnIndex("Mono")) +"',";
					SQL += " Fano='"+ curM.getString(curM.getColumnIndex("Fano")) +"',";
					SQL += " Edu='"+ curM.getString(curM.getColumnIndex("Edu")) +"',";
					SQL += " Ms='"+ curM.getString(curM.getColumnIndex("Ms")) +"',";
					SQL += " Pstat='"+ curM.getString(curM.getColumnIndex("Pstat")) +"',";
					SQL += " LmpDt='"+ curM.getString(curM.getColumnIndex("LmpDt")) +"',";
					SQL += " Sp1='"+ curM.getString(curM.getColumnIndex("Sp1")) +"',";
					SQL += " Sp2='"+ curM.getString(curM.getColumnIndex("Sp2")) +"',";
					SQL += " Sp3='"+ curM.getString(curM.getColumnIndex("Sp3")) +"',";
					SQL += " Sp4='"+ curM.getString(curM.getColumnIndex("Sp4")) +"',";
					SQL += " Ocp='"+ curM.getString(curM.getColumnIndex("Ocp")) +"',";
					SQL += " EnType='"+ curM.getString(curM.getColumnIndex("EnType")) +"',";
					SQL += " EnDate='"+ curM.getString(curM.getColumnIndex("EnDate")) +"',";
					SQL += " ExType='"+ curM.getString(curM.getColumnIndex("ExType")) +"',";
					SQL += " ExDate='"+ curM.getString(curM.getColumnIndex("ExDate")) +"',";
					SQL += " Status='C',";
					SQL += " Upload='2',";
					SQL += " PosMig='"+ curM.getString(curM.getColumnIndex("PosMig")) +"',";
					SQL += " PosMigDate='"+ curM.getString(curM.getColumnIndex("PosMigDate")) +"'";

					SQL += " where  vill||bari||hh='"+ Household +"' and SNo='"+ curM.getString(curM.getColumnIndex("Sno")) +"'";

					C.Save(SQL);
				}
				curM.moveToNext();
			}
			curM.close();



			//-- -SES Table-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			SQL = "Insert into SES";
			SQL += "(Vill, Bari, Hh, SESNo, Visit, Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d, Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q, Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d, Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a, Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y, Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e, Q030f, Q030g, Q030h, Q030z, Q031, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon)";
			SQL += " Select Vill, Bari, Hh, SESNo, Visit, Q015a, Q015b, Q015c, Q016a, Q016b, Q016c, Q017, Q018, Q019a, Q019b, Q019c, Q019d, Q019e, Q019f, Q019g, Q019h, Q019i, Q019j, Q019k, Q019l, Q019m, Q019n, Q019o, Q019p, Q019q, Q019r, Q019s, Q019t, Q019u, Q019v, Q019w, Q019x, Q019y, Q019z, Q020a, Q020b, Q020c, Q020d, Q020e, Q020f, Q020g, Q020h, Q021, Q022a, Q022b, Q022c, Q023a, Q023b, Q024a, Q024b, Q025a, Q025b, Q026, Q027a, Q027b, Q027c, Q027d, Q027e, Q027f, Q027g, Q027h, Q027i, Q027j, Q027y, Q027z, Q028a, Q028b, Q028c, Q028d, Q028e, Q028y, Q029, Q030a, Q030b, Q030c, Q030d, Q030e, Q030f, Q030g, Q030h, Q030z, Q031, Vdate, Rnd, ifnull(PageNo,'')PageNo, Status, '2', ifnull(Lat,'')Lat, ifnull(Lon,'')Lon";
			SQL += " from ttrans where status='s' and vill||bari||hh='"+ Household +"'";

			C.Save(SQL);

			//-- -Pregnancy Info. Table--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			//SQL = "Insert into PregHis(Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon)";
			//SQL += " select Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, ifnull(PageNo,'')PageNo, ifnull(Status,'C')Status, ifnull(Upload,'2')Upload, ifnull(Lat,'')Lat, ifnull(Lon,'')Lon";
			//SQL += " from tTrans t where t.status='p' and t.vill||t.bari||t.hh='"+ Household +"'";

			//C.Save(SQL);

			//Pregnancy History: 03 05 2016
			Cursor pHis = C.ReadData("select Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate as vdate, Rnd, PageNo, Status from tTrans where status='p' and vill||bari||hh='"+ Household +"'");
			pHis.moveToFirst();
			while(!pHis.isAfterLast())
			{
				if(C.Existence("select Vill, Bari, Hh,SNo,PNo from PregHis where vill||bari||hh='"+ Household +"' and SNo='"+ pHis.getString(pHis.getColumnIndex("Sno")) +"'"))
				{
					SQL = "Update PregHis set Upload='2',";
					SQL += "Visit='"+ pHis.getString(pHis.getColumnIndex("Visit")) +"',";
					SQL += "MarM='"+ pHis.getString(pHis.getColumnIndex("MarM")) +"',";
					SQL += "MarY='"+ pHis.getString(pHis.getColumnIndex("MarY")) +"',";
					SQL += "Births='"+ pHis.getString(pHis.getColumnIndex("Births")) +"',";
					SQL += "LiveHh='"+ pHis.getString(pHis.getColumnIndex("LiveHh")) +"',";
					SQL += "SLiveHh='"+ pHis.getString(pHis.getColumnIndex("SLiveHh")) +"',";
					SQL += "DLiveHh='"+ pHis.getString(pHis.getColumnIndex("DLiveHh")) +"',";
					SQL += "LiveOut='"+ pHis.getString(pHis.getColumnIndex("LiveOut")) +"',";
					SQL += "SLiveOut='"+ pHis.getString(pHis.getColumnIndex("SLiveOut")) +"',";
					SQL += "DLiveOut='"+ pHis.getString(pHis.getColumnIndex("DLiveOut")) +"',";
					SQL += "Died='"+ pHis.getString(pHis.getColumnIndex("Died")) +"',";
					SQL += "SDied='"+ pHis.getString(pHis.getColumnIndex("SDied")) +"',";
					SQL += "DDied='"+ pHis.getString(pHis.getColumnIndex("DDied")) +"',";
					SQL += "Abor='"+ pHis.getString(pHis.getColumnIndex("Abor")) +"',";
					SQL += "TAbor='"+ pHis.getString(pHis.getColumnIndex("TAbor")) +"',";
					SQL += "TotPreg='"+ pHis.getString(pHis.getColumnIndex("TotPreg")) +"',";
					SQL += "Vdate='"+ pHis.getString(pHis.getColumnIndex("vdate")) +"'";
					SQL += " Where vill||bari||hh='"+ Household +"' and SNo='"+ pHis.getString(pHis.getColumnIndex("Sno")) +"'";
					C.Save( SQL );
				}
				else
				{
					SQL = "Insert into PregHis(Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, PageNo, Status, Upload, Lat, Lon)";
					SQL += " select Vill, Bari, Hh, Sno, Pno, Visit, MarM, MarY, Births, LiveHh, SLiveHh, DLiveHh, LiveOut, SLiveOut, DLiveOut, Died, SDied, DDied, Abor, TAbor, TotPreg, Vdate, Rnd, ifnull(PageNo,'')PageNo, ifnull(Status,'C')Status, ifnull(Upload,'2')Upload, ifnull(Lat,'')Lat, ifnull(Lon,'')Lon";
					SQL += " from tTrans t where t.status='p' and t.vill||t.bari||t.hh='"+ Household +"' and t.sno='"+ pHis.getString(pHis.getColumnIndex("Sno")) +"' ";
					C.Save( SQL );
				}

				pHis.moveToNext();
			}
			pHis.close();


			//-- -Visit Table-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			if(C.Existence("Select vill from Visits where vill||bari||hh='"+ Household +"' and Rnd ='"+ Rnd +"'")==true)
			{
				Cursor curV = C.ReadData("select Vill, Bari, Hh, ifnull(Resp,'')Resp, Dma, EnterDt, Vdate, Rnd, Lat, Lon,ExType,ExDate,Note from tTrans where status='v' and vill||bari||hh='"+ Household +"' and Rnd ='"+ Rnd +"'");
				curV.moveToFirst();
				while(!curV.isAfterLast())
				{
					SQL = "Update Visits set Upload='2',";
					SQL += " Rsno='"+ curV.getString(curV.getColumnIndex("Resp")) +"',";
					SQL += " VDate='"+ curV.getString(curV.getColumnIndex("VDate")) +"',"; //date of visit
					SQL += " Note='"+ curV.getString(curV.getColumnIndex("Note")) +"',";
					SQL += " Dma='"+ curV.getString(curV.getColumnIndex("Dma")) +"'"; //DC code
					SQL += " where vill||bari||hh='"+ Household +"' and Rnd='"+ Rnd +"'";
					C.Save(SQL);

					if(curV.getString(curV.getColumnIndex("Resp")).equals("77"))
					{
						SQL = "Update Household set Upload='2',ExType='"+ curV.getString(curV.getColumnIndex("ExType")) +"',ExDate='"+ curV.getString(curV.getColumnIndex("ExDate")) +"' where vill||bari||hh='"+ Household +"'";
						C.Save(SQL);
					}


					//update temp table: 16 may 2016
					SQL = "Update Visits_temp set ";
					SQL += " Rsno='"+ curV.getString(curV.getColumnIndex("Resp")) +"',";
					SQL += " VDate='"+ curV.getString(curV.getColumnIndex("VDate")) +"'"; //date of visit
					SQL += " where vill||bari||hh='"+ Household +"' and Rnd='"+ Rnd +"'";
					C.Save(SQL);

					curV.moveToNext();
				}
				curV.close();
			}
			else
			{
				Cursor curV = C.ReadData("select ifnull(Resp,'')Resp,ExType,ExDate from tTrans where status='v' and vill||bari||hh='"+ Household +"' and Rnd ='"+ Rnd +"'");
				curV.moveToFirst();
				while(!curV.isAfterLast())
				{
					if(curV.getString(curV.getColumnIndex("Resp")).equals("77"))
					{
						SQL = "Update Household set Upload='2',ExType='"+ curV.getString(curV.getColumnIndex("ExType")) +"',ExDate='"+ curV.getString(curV.getColumnIndex("ExDate")) +"' where vill||bari||hh='"+ Household +"'";
						C.Save(SQL);
					}
					curV.moveToNext();
				}
				curV.close();

				SQL = "Insert into Visits(Vill, Bari, Hh, Rsno, Dma, EnterDt, Vdate, Rnd, Lat, Lon,LatNet,LonNet,Upload,Note)";
				SQL += " Select Vill, Bari, Hh, Resp, Dma, EnterDt, Vdate, Rnd, Lat, Lon,LatNet,LonNet,'2',Note from tTrans where status='v' and vill||bari||hh='"+ Household +"'";
				C.Save(SQL);

				//update temp data
				SQL = "Insert into Visits_temp(Vill, Bari, Hh, Rsno, Vdate, Rnd)";
				SQL += " Select Vill, Bari, Hh, Resp, Vdate, Rnd from tTrans where status='v' and vill||bari||hh='"+ Household +"'";
				C.Save(SQL);

			}

			//-- -Event Table----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			SQL = "Insert into Events";
			SQL += " (Vill,Bari,Hh,Pno,Sno,EvType,EvDate,Info1,Info2,Info3,Info4,Info5,VDate,Rnd,Upload)";
			SQL += " Select Vill,Bari,HH,PNo,SNo,EvType,EvDate,ifnull(Info1,'')Info1,ifnull(Info2,'')Info2,ifnull(Info3,'')Info3,ifnull(Info4,'')Info4,ifnull(Info5,'')Info5,VDate,Rnd,'2'";
			SQL += " from tTrans where Status='e' and vill||bari||hh='"+ Household +"'";
			C.Save(SQL);


			//Need to create/remove a record in migration database
			String migSQL="";
			String evtype="";
			Cursor curMig = C.ReadData("select t.EvType evtype,(t.vill||t.bari||t.HH) hh,t.SNo sno,t.PNo pno,m.Name name,t.EvDate exdate from tTrans t,Member m where t.vill||t.bari||t.hh=m.vill||m.bari||m.hh and t.sno=m.sno and t.status='e' and t.vill||t.bari||t.hh='"+ Household +"' and t.Rnd ='"+ Rnd +"' and t.EvType in('52','22','53','23')");
			curMig.moveToFirst();
			while(!curMig.isAfterLast())
			{
				evtype = curMig.getString(curMig.getColumnIndex("evtype")).toString();

				//insert data in to migration table
				if(evtype.equals("52") | evtype.equals("53"))
				{
					migSQL = "Insert into MigDatabase(ExType,HH,SNo,PNo,Name,ExDate)Values(";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("evtype")) +"',";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("hh")) +"',";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("sno")) +"',";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("pno")) +"',";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("name")) +"',";
					migSQL += "'"+ curMig.getString(curMig.getColumnIndex("exdate")) +"')";
					C.Save(migSQL);
				}

				//remove data in to migration table
				else if(evtype.equals("22"))
				{
					C.Save("Delete from MigDatabase where pno='"+ curMig.getString(curMig.getColumnIndex("pno")) +"' and extype='52'");
				}

				//remove data in to migration table
				else if(evtype.equals("23"))
				{
					C.Save("Delete from MigDatabase where pno='"+ curMig.getString(curMig.getColumnIndex("pno")) +"' and extype='53'");
				}
				curMig.moveToNext();
			}
			curMig.close();

			//-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

			C.Save("Delete from tTrans where vill||bari||hh='"+ Household +"'");

			//Immunization data
			//-- ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
			try {
				C.Save("Delete from Immunization where vill||bari||hh='"+ Household +"'");

				//20 May 2016
				C.Save("Insert into Immunization Select * from ImmunizationTemp");
			}catch(Exception ex)
			{

			}

			//Death Report : 22 Feb 2015
			C.Save("Delete from Death where vill||bari||hh='"+ Household +"'");
			C.Save("Insert into Death Select * from Death_Temp where vill||bari||hh='"+ Household +"'");
		}
		catch(Exception ex)
		{
			Err = ex.getMessage();
		}

		return Err;
	}



	//***************************************************************************************************************************
	private void VisitList(String Household)
	{
		try
		{
			final Dialog dialog = new Dialog(MemberEvents.this);

			dialog .requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.visitlist);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			final ListView evlist = (ListView)dialog.findViewById(R.id.lstVisit);
			View header = getLayoutInflater().inflate(R.layout.visitlistheading, null);
			evlist.addHeaderView(header);
			Cursor cur1 = C.ReadData("select Rnd , Rsno, Vdate from Visits where vill||bari||hh='"+ Household +"' order by cast(rnd as int) desc");

			cur1.moveToFirst();
			evmylist.clear();
			eList = null;
			evlist.setAdapter(null);

			while(!cur1.isAfterLast())
			{
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("rnd", cur1.getString(cur1.getColumnIndex("Rnd")));
				map.put("rsno", cur1.getString(cur1.getColumnIndex("Rsno")));
				map.put("vdate", Global.DateConvertDMY(cur1.getString(cur1.getColumnIndex("Vdate"))));

				evmylist.add(map);

				eList = new SimpleAdapter(MemberEvents.this, evmylist, R.layout.visitlistrow,
						new String[] {"rnd","rsno","vdate"},
						new int[] {R.id.v_rnd,R.id.v_rsno,R.id.v_vdate});
				evlist.setAdapter(eList);

				cur1.moveToNext();
			}


			cur1.close();

			Button cmdVisitListClose = (Button)dialog.findViewById(R.id.cmdVisitListClose);
			cmdVisitListClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});


			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}


	//Immunization Form
	//------------------------------------------------------------------------------------
	String PNumber="";
	private void ImmunizationForm(final String Vill, final String Bari, final String HH)
	{
		try
		{
			final Dialog dialog = new Dialog(MemberEvents.this);

			dialog.setTitle("Immunization History");
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.immunization);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(true);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			final Spinner  spnMemList = (Spinner)dialog.findViewById(R.id.spnMemList);
			spnMemList.setAdapter(C.getArrayAdapter("Select (SNo||':'||Name||',DOB:'||(substr(bdate,9,2)||'/'||substr(bdate,6,2)||'/'||substr(bdate,1,4))||', Age(Yr):'||cast((julianday(date('now'))-julianday(bdate))/365.25 as int))MemInfo from tTrans where status='m' and length(extype)=0 and vill||bari||hh='"+ (Vill+Bari+HH) +"' and cast((julianday(date('now'))-julianday(bdate))/30.4 as int)<=59"));

			ArrayAdapter listVaccineStatus = ArrayAdapter.createFromResource(this, R.array.listVaccineStatus, android.R.layout.simple_spinner_item);
			listVaccineStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			final Spinner  spnVaccineStatus = (Spinner)dialog.findViewById(R.id.spnVaccineStatus);
			spnVaccineStatus.setAdapter(listVaccineStatus);

			ArrayAdapter dataSource = ArrayAdapter.createFromResource(this, R.array.listVaccineCenter, android.R.layout.simple_spinner_item);
			dataSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


			final CheckBox chkBCG = (CheckBox)dialog.findViewById(R.id.chkBCG);
			final EditText BCGDT = (EditText)dialog.findViewById(R.id.BCGDT);
			final Spinner  spnBCG= (Spinner)dialog.findViewById(R.id.spnBCG);
			spnBCG.setAdapter(dataSource);

			final CheckBox chkPenta1 = (CheckBox)dialog.findViewById(R.id.chkPenta1);
			final EditText Penta1DT = (EditText)dialog.findViewById(R.id.Penta1DT);
			final Spinner  spnPenta1 = (Spinner)dialog.findViewById(R.id.spnPenta1);
			spnPenta1.setAdapter(dataSource);

			final CheckBox chkPenta2 = (CheckBox)dialog.findViewById(R.id.chkPenta2);
			final EditText Penta2DT = (EditText)dialog.findViewById(R.id.Penta2DT);
			final Spinner  spnPenta2 = (Spinner)dialog.findViewById(R.id.spnPenta2);
			spnPenta2.setAdapter(dataSource);

			final CheckBox chkPenta3 = (CheckBox)dialog.findViewById(R.id.chkPenta3);
			final EditText Penta3DT = (EditText)dialog.findViewById(R.id.Penta3DT);
			final Spinner  spnPenta3 = (Spinner)dialog.findViewById(R.id.spnPenta3);
			spnPenta3.setAdapter(dataSource);

			final CheckBox chkPCV1 = (CheckBox)dialog.findViewById(R.id.chkPCV1);
			final EditText PCV1DT = (EditText)dialog.findViewById(R.id.PCV1DT);
			final Spinner  spnPCV1 = (Spinner)dialog.findViewById(R.id.spnPCV1);
			spnPCV1.setAdapter(dataSource);

			final CheckBox chkPCV2 = (CheckBox)dialog.findViewById(R.id.chkPCV2);
			final EditText PCV2DT = (EditText)dialog.findViewById(R.id.PCV2DT);
			final Spinner  spnPCV2 = (Spinner)dialog.findViewById(R.id.spnPCV2);
			spnPCV2.setAdapter(dataSource);

			final CheckBox chkPCV3 = (CheckBox)dialog.findViewById(R.id.chkPCV3);
			final EditText PCV3DT = (EditText)dialog.findViewById(R.id.PCV3DT);
			final Spinner  spnPCV3 = (Spinner)dialog.findViewById(R.id.spnPCV3);
			spnPCV3.setAdapter(dataSource);

			final CheckBox chkOPV0 = (CheckBox)dialog.findViewById(R.id.chkOPV0);
			final EditText OPV0DT = (EditText)dialog.findViewById(R.id.OPV0DT);
			final Spinner  spnOPV0 = (Spinner)dialog.findViewById(R.id.spnOPV0);
			spnOPV0.setAdapter(dataSource);

			final CheckBox chkOPV1 = (CheckBox)dialog.findViewById(R.id.chkOPV1);
			final EditText OPV1DT = (EditText)dialog.findViewById(R.id.OPV1DT);
			final Spinner  spnOPV1 = (Spinner)dialog.findViewById(R.id.spnOPV1);
			spnOPV1.setAdapter(dataSource);

			final CheckBox chkOPV2 = (CheckBox)dialog.findViewById(R.id.chkOPV2);
			final EditText OPV2DT = (EditText)dialog.findViewById(R.id.OPV2DT);
			final Spinner  spnOPV2 = (Spinner)dialog.findViewById(R.id.spnOPV2);
			spnOPV2.setAdapter(dataSource);

			final CheckBox chkOPV3 = (CheckBox)dialog.findViewById(R.id.chkOPV3);
			final EditText OPV3DT = (EditText)dialog.findViewById(R.id.OPV3DT);
			final Spinner  spnOPV3 = (Spinner)dialog.findViewById(R.id.spnOPV3);
			spnOPV3.setAdapter(dataSource);

			final CheckBox chkOPV4 = (CheckBox)dialog.findViewById(R.id.chkOPV4);
			final EditText OPV4DT = (EditText)dialog.findViewById(R.id.OPV4DT);
			final Spinner  spnOPV4 = (Spinner)dialog.findViewById(R.id.spnOPV4);
			spnOPV4.setAdapter(dataSource);

			final CheckBox chkMeasles = (CheckBox)dialog.findViewById(R.id.chkMeasles);
			final EditText MeaslesDT = (EditText)dialog.findViewById(R.id.MeaslesDT);
			final Spinner  spnMeasles = (Spinner)dialog.findViewById(R.id.spnMeasles);
			spnMeasles.setAdapter(dataSource);

			final CheckBox chkMR = (CheckBox)dialog.findViewById(R.id.chkMR);
			final EditText  MRDT = (EditText)dialog.findViewById(R.id.MRDT);
			final Spinner  spnMR = (Spinner)dialog.findViewById(R.id.spnMR);
			spnMR.setAdapter(dataSource);

			final CheckBox chkRota = (CheckBox)dialog.findViewById(R.id.chkRota);
			final EditText  RotaDT = (EditText)dialog.findViewById(R.id.RotaDT);
			final Spinner  spnRota = (Spinner)dialog.findViewById(R.id.spnRota);
			spnRota.setAdapter(dataSource);

			final CheckBox chkMMR = (CheckBox)dialog.findViewById(R.id.chkMMR);
			final EditText  MMRDT = (EditText)dialog.findViewById(R.id.MMRDT);
			final Spinner  spnMMR = (Spinner)dialog.findViewById(R.id.spnMMR);
			spnMMR.setAdapter(dataSource);

			final CheckBox chkTyphoid = (CheckBox)dialog.findViewById(R.id.chkTyphoid);
			final EditText  TyphoidDT = (EditText)dialog.findViewById(R.id.TyphoidDT);
			final Spinner  spnTyphoid = (Spinner)dialog.findViewById(R.id.spnTyphoid);
			spnTyphoid.setAdapter(dataSource);

			final CheckBox chkInflu = (CheckBox)dialog.findViewById(R.id.chkInflu);
			final EditText  InfluDT = (EditText)dialog.findViewById(R.id.InfluDT);
			final Spinner  spnInflu = (Spinner)dialog.findViewById(R.id.spnInflu);
			spnInflu.setAdapter(dataSource);

			final CheckBox chkHepaA = (CheckBox)dialog.findViewById(R.id.chkHepaA);
			final EditText  HepaADT = (EditText)dialog.findViewById(R.id.HepaADT);
			final Spinner  spnHepaA = (Spinner)dialog.findViewById(R.id.spnHepaA);
			spnHepaA.setAdapter(dataSource);

			final CheckBox chkChickenPox = (CheckBox)dialog.findViewById(R.id.chkChickenPox);
			final EditText  ChickenPoxDT = (EditText)dialog.findViewById(R.id.ChickenPoxDT);
			final Spinner  spnChickenPox = (Spinner)dialog.findViewById(R.id.spnChickenPox);
			spnChickenPox.setAdapter(dataSource);

			final CheckBox chkRabies = (CheckBox)dialog.findViewById(R.id.chkRabies);
			final EditText  RabiesDT = (EditText)dialog.findViewById(R.id.RabiesDT);
			final Spinner  spnRabies = (Spinner)dialog.findViewById(R.id.spnRabies);
			spnRabies.setAdapter(dataSource);

			final CheckBox chkIPV = (CheckBox)dialog.findViewById(R.id.chkIPV);
			final EditText IPVDT = (EditText)dialog.findViewById(R.id.IPVDT);
			final Spinner  spnIPV = (Spinner)dialog.findViewById(R.id.spnIPV);
			spnIPV.setAdapter(dataSource);

			final Button cmdImmSave  = (Button)dialog.findViewById(R.id.cmdImmSave);
			final Button cmdImmClose = (Button)dialog.findViewById(R.id.cmdImmClose);
			mImageView1 = (ImageView)dialog.findViewById(R.id.imgForm1);
			mImageView2 = (ImageView)dialog.findViewById(R.id.imgForm2);
			mImageView3 = (ImageView)dialog.findViewById(R.id.imgForm3);
			mImageView4 = (ImageView)dialog.findViewById(R.id.imgForm4);
			mImageView5 = (ImageView)dialog.findViewById(R.id.imgForm5);
			mImageView1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					PhotoView(Vill+Bari+HH+SN+"1");
				}});
			mImageView2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					PhotoView(Vill+Bari+HH+SN+"2");
				}});
			mImageView3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					PhotoView(Vill+Bari+HH+SN+"3");
				}});
			mImageView4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					PhotoView(Vill+Bari+HH+SN+"4");
				}});
			mImageView5.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					PhotoView(Vill+Bari+HH+SN+"5");
				}});

			Button cmdPhoto1 = (Button)dialog.findViewById(R.id.cmdPhoto1);
			Button cmdPhoto2 = (Button)dialog.findViewById(R.id.cmdPhoto2);
			Button cmdPhoto3 = (Button)dialog.findViewById(R.id.cmdPhoto3);
			Button cmdPhoto4 = (Button)dialog.findViewById(R.id.cmdPhoto4);
			Button cmdPhoto5 = (Button)dialog.findViewById(R.id.cmdPhoto5);


			cmdPhoto1.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(spnMemList.getCount()==0) return;
					if(spnMemList.getSelectedItem().toString().length()==0) return;
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					TakePhoto(Vill+Bari+HH+SN+"1");
				}});
			cmdPhoto2.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(spnMemList.getCount()==0) return;
					if(spnMemList.getSelectedItem().toString().length()==0) return;
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					TakePhoto(Vill+Bari+HH+SN+"2");
				}});
			cmdPhoto3.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(spnMemList.getCount()==0) return;
					if(spnMemList.getSelectedItem().toString().length()==0) return;
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					TakePhoto(Vill+Bari+HH+SN+"3");
				}});
			cmdPhoto4.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(spnMemList.getCount()==0) return;
					if(spnMemList.getSelectedItem().toString().length()==0) return;
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					TakePhoto(Vill+Bari+HH+SN+"4");
				}});
			cmdPhoto5.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(spnMemList.getCount()==0) return;
					if(spnMemList.getSelectedItem().toString().length()==0) return;
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
					TakePhoto(Vill+Bari+HH+SN+"5");
				}});

			if(spnMemList.getCount()>0)
			{   
                    /*
                    String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
                    mImageView1 = (ImageView)dialog.findViewById(R.id.imgForm1);
                    mImageView2 = (ImageView)dialog.findViewById(R.id.imgForm2);
                    mImageView3 = (ImageView)dialog.findViewById(R.id.imgForm3);
                    mImageView4 = (ImageView)dialog.findViewById(R.id.imgForm4);
                    mImageView5 = (ImageView)dialog.findViewById(R.id.imgForm5);

                    File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"1.jpg");
                    if(mFichier1.exists())
                    {
                            mImageView1.setImageURI(Uri.fromFile(mFichier1));
                    }
                    mImageView1.setVisibility(View.VISIBLE);
                    
                    File mFichier2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"2.jpg");
                    if(mFichier2.exists())
                    {
                            mImageView2.setImageURI(Uri.fromFile(mFichier2));
                    }
                    mImageView2.setVisibility(View.VISIBLE);
                    
                    File mFichier3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"3.jpg");
                    if(mFichier3.exists())
                    {
                            mImageView3.setImageURI(Uri.fromFile(mFichier3));
                    }
                    mImageView3.setVisibility(View.VISIBLE);
    
                    File mFichier4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"4.jpg");
                    if(mFichier4.exists())
                    {
                            mImageView4.setImageURI(Uri.fromFile(mFichier4));
                    }
                    mImageView4.setVisibility(View.VISIBLE);
    
                    File mFichier5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"5.jpg");
                    if(mFichier5.exists())
                    {
                            mImageView5.setImageURI(Uri.fromFile(mFichier5));
                    }
                    mImageView5.setVisibility(View.VISIBLE);
                    */
			}


			Button cmdShow = (Button)dialog.findViewById(R.id.cmdShow);
			cmdShow.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);

					File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"1.jpg");
					if(mFichier1.exists())
					{
						mImageView1.setImageURI(Uri.fromFile(mFichier1));
					}
					mImageView1.setVisibility(View.VISIBLE);

					File mFichier2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"2.jpg");
					if(mFichier2.exists())
					{
						mImageView2.setImageURI(Uri.fromFile(mFichier2));
					}
					mImageView2.setVisibility(View.VISIBLE);

					File mFichier3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"3.jpg");
					if(mFichier3.exists())
					{
						mImageView3.setImageURI(Uri.fromFile(mFichier3));
					}
					mImageView3.setVisibility(View.VISIBLE);

					File mFichier4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"4.jpg");
					if(mFichier4.exists())
					{
						mImageView4.setImageURI(Uri.fromFile(mFichier4));
					}
					mImageView4.setVisibility(View.VISIBLE);

					File mFichier5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", (Vill+Bari+HH+SN) +"5.jpg");
					if(mFichier5.exists())
					{
						mImageView5.setImageURI(Uri.fromFile(mFichier5));
					}
					mImageView5.setVisibility(View.VISIBLE);
				}});



			spnMemList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					try
					{
						String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
						String PNoAvailable="1";
						if(C.Existence("Select Vill from tTrans Where status='m' and Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"' and (length(pno)=0 or pno is null)"))
						{
							PNoAvailable="2";
						}
						else
						{
						}

						if(PNoAvailable.equals("1"))
							PNumber = C.ReturnSingleValue("Select PNo from tTrans Where Status='m' and Vill||Bari||HH||SNo='"+ Vill+Bari+HH+SN +"'");
						else
							PNumber = "";

						//if(PNumber.length()==0) return;

						chkBCG.setChecked(false);
						chkPenta1.setChecked(false);
						chkPenta2.setChecked(false);
						chkPenta3.setChecked(false);
						chkPCV1.setChecked(false);
						chkPCV2.setChecked(false);
						chkPCV3.setChecked(false);
						chkOPV0.setChecked(false);
						chkOPV1.setChecked(false);
						chkOPV2.setChecked(false);
						chkOPV3.setChecked(false);
						chkOPV4.setChecked(false);
						chkMeasles.setChecked(false);
						chkMR.setChecked(false);
						chkRota.setChecked(false);
						chkMMR.setChecked(false);
						chkTyphoid.setChecked(false);
						chkInflu.setChecked(false);
						chkHepaA.setChecked(false);
						chkChickenPox.setChecked(false);
						chkRabies.setChecked(false);
						chkIPV.setChecked( false );

						BCGDT.setText("");
						spnBCG.setSelection(0);

						Penta1DT.setText("");
						spnPenta1.setSelection(0);

						Penta2DT.setText("");
						spnPenta2.setSelection(0);

						Penta3DT.setText("");
						spnPenta3.setSelection(0);

						PCV1DT.setText("");
						spnPCV1.setSelection(0);

						PCV2DT.setText("");
						spnPCV2.setSelection(0);

						PCV3DT.setText("");
						spnPCV3.setSelection(0);

						OPV0DT.setText("");
						spnOPV0.setSelection(0);

						OPV1DT.setText("");
						spnOPV1.setSelection(0);

						OPV2DT.setText("");
						spnOPV2.setSelection(0);

						OPV3DT.setText("");
						spnOPV3.setSelection(0);

						OPV4DT.setText("");
						spnOPV4.setSelection(0);

						MeaslesDT.setText("");
						spnMeasles.setSelection(0);

						MRDT.setText("");
						spnMR.setSelection(0);

						RotaDT.setText("");
						spnRota.setSelection(0);

						MMRDT.setText("");
						spnMMR.setSelection(0);

						TyphoidDT.setText("");
						spnTyphoid.setSelection(0);

						InfluDT.setText("");
						spnInflu.setSelection(0);

						HepaADT.setText("");
						spnHepaA.setSelection(0);

						ChickenPoxDT.setText("");
						spnChickenPox.setSelection(0);

						RabiesDT.setText("");
						spnRabies.setSelection(0);

						IPVDT.setText("");
						spnIPV.setSelection(0);

						Cursor cur = null;
						if(PNoAvailable.equals("1"))
						{
							cur=C.ReadData("Select * from ImmunizationTemp where PNo='"+ PNumber +"'");
						}
						else
						{
							cur=C.ReadData("Select * from ImmunizationTemp where Vill||Bari||HH='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
						}
						//Cursor cur=C.ReadData("Select * from ImmunizationTemp where Vill||Bari||HH='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
						//Cursor cur=C.ReadData("Select * from ImmunizationTemp where PNo='"+ PNumber +"'");

						cur.moveToFirst();
						while(!cur.isAfterLast())
						{
							spnVaccineStatus.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Status"))));

							if(cur.getString(cur.getColumnIndex("BCG")).equals("1"))
							{
								chkBCG.setChecked(true);
								BCGDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("BCGDT"))));
								spnBCG.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("BCGSource"))));
							}
							if(cur.getString(cur.getColumnIndex("Penta1")).equals("1"))
							{
								chkPenta1.setChecked(true);
								Penta1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta1DT"))));
								spnPenta1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta1Source"))));
							}
							if(cur.getString(cur.getColumnIndex("Penta2")).equals("1"))
							{
								chkPenta2.setChecked(true);
								Penta2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta2DT"))));
								spnPenta2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta2Source"))));
							}
							if(cur.getString(cur.getColumnIndex("Penta3")).equals("1"))
							{
								chkPenta3.setChecked(true);
								Penta3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("Penta3DT"))));
								spnPenta3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("Penta3Source"))));
							}
							if(cur.getString(cur.getColumnIndex("PCV1")).equals("1"))
							{
								chkPCV1.setChecked(true);
								PCV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV1DT"))));
								spnPCV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV1Source"))));
							}
							if(cur.getString(cur.getColumnIndex("PCV2")).equals("1"))
							{
								chkPCV2.setChecked(true);
								PCV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV2DT"))));
								spnPCV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV2Source"))));
							}
							if(cur.getString(cur.getColumnIndex("PCV3")).equals("1"))
							{
								chkPCV3.setChecked(true);
								PCV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("PCV3DT"))));
								spnPCV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("PCV3Source"))));
							}
							if(cur.getString(cur.getColumnIndex("OPV0")).equals("1"))
							{
								chkOPV0.setChecked(true);
								OPV0DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV0DT"))));
								spnOPV0.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV0Source"))));
							}
							if(cur.getString(cur.getColumnIndex("OPV1")).equals("1"))
							{
								chkOPV1.setChecked(true);
								OPV1DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV1DT"))));
								spnOPV1.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV1Source"))));
							}
							if(cur.getString(cur.getColumnIndex("OPV2")).equals("1"))
							{
								chkOPV2.setChecked(true);
								OPV2DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV2DT"))));
								spnOPV2.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV2Source"))));
							}
							if(cur.getString(cur.getColumnIndex("OPV3")).equals("1"))
							{
								chkOPV3.setChecked(true);
								OPV3DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV3DT"))));
								spnOPV3.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV3Source"))));
							}
							if(cur.getString(cur.getColumnIndex("OPV4")).equals("1"))
							{
								chkOPV4.setChecked(true);
								OPV4DT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("OPV4DT"))));
								spnOPV4.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("OPV4Source"))));
							}
							if(cur.getString(cur.getColumnIndex("Measles")).equals("1"))
							{
								chkMeasles.setChecked(true);
								MeaslesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MeaslesDT"))));
								spnMeasles.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MeaslesSource"))));
							}
							if(cur.getString(cur.getColumnIndex("MR")).equals("1"))
							{
								chkMR.setChecked(true);
								MRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MRDT"))));
								spnMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MRSource"))));
							}
							if(cur.getString(cur.getColumnIndex("Rota")).equals("1"))
							{
								chkRota.setChecked(true);
								RotaDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RotaDT"))));
								spnRota.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RotaSource"))));
							}
							if(cur.getString(cur.getColumnIndex("MMR")).equals("1"))
							{
								chkMMR.setChecked(true);
								MMRDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("MMRDT"))));
								spnMMR.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("MMRSource"))));
							}
							if(cur.getString(cur.getColumnIndex("Typhoid")).equals("1"))
							{
								chkTyphoid.setChecked(true);
								TyphoidDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("TyphoidDT"))));
								spnTyphoid.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("TyphoidSource"))));
							}
							if(cur.getString(cur.getColumnIndex("MMR")).equals("1"))
							{
								chkInflu.setChecked(true);
								InfluDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("InfluDT"))));
								spnInflu.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("InfluSource"))));
							}
							if(cur.getString(cur.getColumnIndex("HepaA")).equals("1"))
							{
								chkHepaA.setChecked(true);
								HepaADT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("HepaADT"))));
								spnHepaA.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("HepaASource"))));
							}
							if(cur.getString(cur.getColumnIndex("ChickenPox")).equals("1"))
							{
								chkChickenPox.setChecked(true);
								ChickenPoxDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("ChickenPoxDT"))));
								spnChickenPox.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("ChickenPoxSource"))));
							}
							if(cur.getString(cur.getColumnIndex("Rabies")).equals("1"))
							{
								chkRabies.setChecked(true);
								RabiesDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("RabiesDT"))));
								spnRabies.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("RabiesSource"))));
							}
							if(cur.getString(cur.getColumnIndex("IPV")).equals("1"))
							{
								chkIPV.setChecked(true);
								IPVDT.setText(Global.DateConvertDMY(cur.getString(cur.getColumnIndex("IPVDT"))));
								spnIPV.setSelection(SpinnerSelection(cur.getString(cur.getColumnIndex("IPVSource"))));
							}

							cur.moveToNext();
						}
						cur.close();
					}
					catch(Exception ex)
					{
						Connection.MessageBox(MemberEvents.this, ex.getMessage());
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}

			});

			final LinearLayout secVaccine = (LinearLayout)dialog.findViewById(R.id.secVaccine);

			spnVaccineStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					try
					{
						String Status = "";
						if(spnVaccineStatus.getSelectedItemPosition()==0)
							Status = "00";
						else
							Status = Global.Left(spnVaccineStatus.getSelectedItem().toString(),2);


						if(Status.equals("01"))
						{
							secVaccine.setVisibility(View.VISIBLE);
							for ( int i = 0; i < secVaccine.getChildCount();  i++ ){
								View view = secVaccine.getChildAt(i);
								view.setEnabled(true); // Or whatever you want to do with the view.
							}
						}
						else
						{
							chkBCG.setChecked(false);
							chkPenta1.setChecked(false);
							chkPenta2.setChecked(false);
							chkPenta3.setChecked(false);
							chkPCV1.setChecked(false);
							chkPCV2.setChecked(false);
							chkPCV3.setChecked(false);
							chkOPV0.setChecked(false);
							chkOPV1.setChecked(false);
							chkOPV2.setChecked(false);
							chkOPV3.setChecked(false);
							chkOPV4.setChecked(false);
							chkMeasles.setChecked(false);
							chkMR.setChecked(false);
							chkRota.setChecked(false);
							chkMMR.setChecked(false);
							chkTyphoid.setChecked(false);
							chkInflu.setChecked(false);
							chkHepaA.setChecked(false);
							chkChickenPox.setChecked(false);
							chkRabies.setChecked(false);
							chkIPV.setChecked(false);

							secVaccine.setVisibility(View.GONE);
							for ( int i = 0; i < secVaccine.getChildCount();  i++ ){
								View view = secVaccine.getChildAt(i);
								view.setEnabled(false); // Or whatever you want to do with the view.
							}
						}
					}
					catch(Exception ex)
					{
						Connection.MessageBox(MemberEvents.this, ex.getMessage());
						return;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}

			});



			cmdImmSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String SQL = "";
					String EDT = "";
					try
					{
						if(spnMemList.getCount()==0)
						{
							Connection.MessageBox(MemberEvents.this, "Select a child from the member list.");
							return;
						}
						else if(spnVaccineStatus.getSelectedItemPosition()==0)
						{
							Connection.MessageBox(MemberEvents.this, "Select a valid status from list.");
							return;
						}
						String SN = Global.Left(spnMemList.getSelectedItem().toString(),2);
						if(SN.length()==0) return;

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String DOB = C.ReturnSingleValue("Select BDate from tTrans where status='m' and vill||bari||hh='"+ Vill+Bari+HH +"' and SNo='"+ SN +"'");
						Date BD = sdf.parse(DOB);
						Date VacD;
	    		
    	    	/*if(!C.Existence("Select Vill from ImmunizationTemp Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'"))
    	    		C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ Global.DateTimeNow() +"','2')");
				*/
						//18 Sep 2014
						//if(!C.Existence("Select Vill from ImmunizationTemp Where PNo='"+ PNumber +"'"))
						//	C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,PNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ PNumber +"','"+ Global.DateTimeNow() +"','2')");

						//for new member those have no pno
						String PNoAvailable="1";
						if(C.Existence("Select Vill from tTrans Where status='m' and Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"' and (length(pno)=0 or pno is null)"))
						{
							PNoAvailable="2";
							if(!C.Existence("Select Vill from ImmunizationTemp Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'"))
								C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ Global.DateTimeNow() +"','2')");
						}
						else
						{
							if(!C.Existence("Select Vill from ImmunizationTemp Where PNo='"+ PNumber +"'"))
								C.Save("Insert into ImmunizationTemp(Vill,Bari,HH,SNo,PNo,EnDt,Upload)Values('"+ Vill +"','"+ Bari +"','"+ HH +"','"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"','"+ PNumber +"','"+ Global.DateTimeNow() +"','2')");
						}

						String Status = "";
						if(spnVaccineStatus.getSelectedItemPosition()==0)
							Status = "";
						else
							Status = Global.Left(spnVaccineStatus.getSelectedItem().toString(),2);

						SQL = "Update ImmunizationTemp Set Status='"+ Status +"',";

						if(chkBCG.isChecked()==true)
						{
							EDT = Global.DateValidate(BCGDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								BCGDT.requestFocus();
								return;
							}
							else if(spnBCG.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnBCG.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(BCGDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination data should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								BCGDT.requestFocus();
								return;
							}

							SQL +="BCG='1',";
							SQL +="BCGDT='"+ Global.DateConvertYMD(BCGDT.getText().toString()) +"',";
							SQL +="BCGSource='"+ Global.Left(spnBCG.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="BCG='',";
							SQL +="BCGDT='',";
							SQL +="BCGSource='',";
						}
						if(chkPenta1.isChecked()==true)
						{
							EDT = Global.DateValidate(Penta1DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								Penta1DT.requestFocus();
								return;
							}
							else if(spnPenta1.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPenta1.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(Penta1DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								Penta1DT.requestFocus();
								return;
							}

							SQL +="Penta1='1',";
							SQL +="Penta1DT='"+ Global.DateConvertYMD(Penta1DT.getText().toString()) +"',";
							SQL +="Penta1Source='"+ Global.Left(spnPenta1.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Penta1='',";
							SQL +="Penta1DT='',";
							SQL +="Penta1Source='',";
						}

						if(chkPenta2.isChecked()==true)
						{
							EDT = Global.DateValidate(Penta2DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								Penta2DT.requestFocus();
								return;
							}
							else if(spnPenta2.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPenta2.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(Penta2DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								Penta2DT.requestFocus();
								return;
							}

							SQL +="Penta2='1',";
							SQL +="Penta2DT='"+ Global.DateConvertYMD(Penta2DT.getText().toString()) +"',";
							SQL +="Penta2Source='"+ Global.Left(spnPenta2.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Penta2='',";
							SQL +="Penta2DT='',";
							SQL +="Penta2Source='',";
						}

						if(chkPenta3.isChecked()==true)
						{
							EDT = Global.DateValidate(Penta3DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								Penta3DT.requestFocus();
								return;
							}
							else if(spnPenta3.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPenta3.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(Penta3DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								Penta3DT.requestFocus();
								return;
							}

							SQL +="Penta3='1',";
							SQL +="Penta3DT='"+ Global.DateConvertYMD(Penta3DT.getText().toString()) +"',";
							SQL +="Penta3Source='"+ Global.Left(spnPenta3.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Penta3='',";
							SQL +="Penta3DT='',";
							SQL +="Penta3Source='',";
						}

						if(chkPCV1.isChecked()==true)
						{
							EDT = Global.DateValidate(PCV1DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								PCV1DT.requestFocus();
								return;
							}
							else if(spnPCV1.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPCV1.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(PCV1DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								PCV1DT.requestFocus();
								return;
							}

							SQL +="PCV1='1',";
							SQL +="PCV1DT='"+ Global.DateConvertYMD(PCV1DT.getText().toString()) +"',";
							SQL +="PCV1Source='"+ Global.Left(spnPCV1.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="PCV1='',";
							SQL +="PCV1DT='',";
							SQL +="PCV1Source='',";
						}
						if(chkPCV2.isChecked()==true)
						{
							EDT = Global.DateValidate(PCV2DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								PCV2DT.requestFocus();
								return;
							}
							else if(spnPCV2.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPCV2.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(PCV2DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								PCV2DT.requestFocus();
								return;
							}

							SQL +="PCV2='"+ (chkPCV2.isChecked()==true?"1":"") +"',";
							SQL +="PCV2DT='"+ Global.DateConvertYMD(PCV2DT.getText().toString()) +"',";
							SQL +="PCV2Source='"+ Global.Left(spnPCV2.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="PCV2='',";
							SQL +="PCV2DT='',";
							SQL +="PCV2Source='',";
						}
						if(chkPCV3.isChecked()==true)
						{
							EDT = Global.DateValidate(PCV3DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								PCV3DT.requestFocus();
								return;
							}
							else if(spnPCV3.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnPCV3.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(PCV3DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								PCV3DT.requestFocus();
								return;
							}

							SQL +="PCV3='1',";
							SQL +="PCV3DT='"+ Global.DateConvertYMD(PCV3DT.getText().toString()) +"',";
							SQL +="PCV3Source='"+ Global.Left(spnPCV3.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="PCV3='',";
							SQL +="PCV3DT='',";
							SQL +="PCV3Source='',";
						}
						if(chkOPV0.isChecked()==true)
						{
							EDT = Global.DateValidate(OPV0DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								OPV0DT.requestFocus();
								return;
							}
							else if(spnOPV0.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnOPV0.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(OPV0DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								OPV0DT.requestFocus();
								return;
							}

							SQL +="OPV0='1',";
							SQL +="OPV0DT='"+ Global.DateConvertYMD(OPV0DT.getText().toString()) +"',";
							SQL +="OPV0Source='"+ Global.Left(spnOPV0.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="OPV0='',";
							SQL +="OPV0DT='',";
							SQL +="OPV0Source='',";
						}

						if(chkOPV1.isChecked()==true)
						{
							EDT = Global.DateValidate(OPV1DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								OPV1DT.requestFocus();
								return;
							}
							else if(spnOPV1.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnOPV1.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(OPV1DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								OPV1DT.requestFocus();
								return;
							}

							SQL +="OPV1='1',";
							SQL +="OPV1DT='"+ Global.DateConvertYMD(OPV1DT.getText().toString()) +"',";
							SQL +="OPV1Source='"+ Global.Left(spnOPV1.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="OPV1='',";
							SQL +="OPV1DT='',";
							SQL +="OPV1Source='',";
						}
						if(chkOPV2.isChecked()==true)
						{
							EDT = Global.DateValidate(OPV2DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								OPV2DT.requestFocus();
								return;
							}
							else if(spnOPV2.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnOPV2.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(OPV2DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								OPV2DT.requestFocus();
								return;
							}


							SQL +="OPV2='1',";
							SQL +="OPV2DT='"+ Global.DateConvertYMD(OPV2DT.getText().toString()) +"',";
							SQL +="OPV2Source='"+ Global.Left(spnOPV2.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="OPV2='',";
							SQL +="OPV2DT='',";
							SQL +="OPV2Source='',";
						}


						if(chkOPV3.isChecked()==true)
						{
							EDT = Global.DateValidate(OPV3DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								OPV3DT.requestFocus();
								return;
							}
							else if(spnOPV3.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnOPV3.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(OPV3DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								OPV3DT.requestFocus();
								return;
							}

							SQL +="OPV3='1',";
							SQL +="OPV3DT='"+ Global.DateConvertYMD(OPV3DT.getText().toString()) +"',";
							SQL +="OPV3Source='"+ Global.Left(spnOPV3.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="OPV3='',";
							SQL +="OPV3DT='',";
							SQL +="OPV3Source='',";
						}
						if(chkOPV4.isChecked()==true)
						{
							EDT = Global.DateValidate(OPV4DT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								OPV4DT.requestFocus();
								return;
							}
							else if(spnOPV4.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnOPV4.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(OPV4DT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								OPV4DT.requestFocus();
								return;
							}

							SQL +="OPV4='1',";
							SQL +="OPV4DT='"+ Global.DateConvertYMD(OPV4DT.getText().toString()) +"',";
							SQL +="OPV4Source='"+ Global.Left(spnOPV4.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="OPV4='',";
							SQL +="OPV4DT='',";
							SQL +="OPV4Source='',";
						}

						if(chkMeasles.isChecked()==true)
						{
							EDT = Global.DateValidate(MeaslesDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								MeaslesDT.requestFocus();
								return;
							}
							else if(spnMeasles.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnMeasles.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(MeaslesDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								MeaslesDT.requestFocus();
								return;
							}

							SQL +="Measles='1',";
							SQL +="MeaslesDT='"+ Global.DateConvertYMD(MeaslesDT.getText().toString()) +"',";
							SQL +="MeaslesSource='"+ Global.Left(spnMeasles.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Measles='',";
							SQL +="MeaslesDT='',";
							SQL +="MeaslesSource='',";
						}

						if(chkMR.isChecked()==true)
						{
							EDT = Global.DateValidate(MRDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								MRDT.requestFocus();
								return;
							}
							else if(spnMR.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnMR.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(MRDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								MRDT.requestFocus();
								return;
							}

							SQL +="MR='1',";
							SQL +="MRDT='"+ Global.DateConvertYMD(MRDT.getText().toString()) +"',";
							SQL +="MRSource='"+ Global.Left(spnMR.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="MR='',";
							SQL +="MRDT='',";
							SQL +="MRSource='',";
						}

						if(chkRota.isChecked()==true)
						{
							EDT = Global.DateValidate(RotaDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								RotaDT.requestFocus();
								return;
							}
							else if(spnRota.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnRota.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(RotaDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								RotaDT.requestFocus();
								return;
							}

							SQL +="Rota='1',";
							SQL +="RotaDT='"+ Global.DateConvertYMD(RotaDT.getText().toString()) +"',";
							SQL +="RotaSource='"+ Global.Left(spnRota.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Rota='',";
							SQL +="RotaDT='',";
							SQL +="RotaSource='',";
						}

						if(chkMMR.isChecked()==true)
						{
							EDT = Global.DateValidate(MMRDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								MMRDT.requestFocus();
								return;
							}
							else if(spnMMR.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnMMR.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(MMRDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								MMRDT.requestFocus();
								return;
							}

							SQL +="MMR='1',";
							SQL +="MMRDT='"+ Global.DateConvertYMD(MMRDT.getText().toString()) +"',";
							SQL +="MMRSource='"+ Global.Left(spnMMR.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="MMR='',";
							SQL +="MMRDT='',";
							SQL +="MMRSource='',";
						}

						if(chkTyphoid.isChecked()==true)
						{
							EDT = Global.DateValidate(TyphoidDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								TyphoidDT.requestFocus();
								return;
							}
							else if(spnTyphoid.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnTyphoid.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(TyphoidDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								TyphoidDT.requestFocus();
								return;
							}

							SQL +="Typhoid='1',";
							SQL +="TyphoidDT='"+ Global.DateConvertYMD(TyphoidDT.getText().toString()) +"',";
							SQL +="TyphoidSource='"+ Global.Left(spnTyphoid.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Typhoid='',";
							SQL +="TyphoidDT='',";
							SQL +="TyphoidSource='',";
						}

						if(chkInflu.isChecked()==true)
						{
							EDT = Global.DateValidate(InfluDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								InfluDT.requestFocus();
								return;
							}
							else if(spnInflu.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnInflu.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(InfluDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								InfluDT.requestFocus();
								return;
							}

							SQL +="Influ='1',";
							SQL +="InfluDT='"+ Global.DateConvertYMD(InfluDT.getText().toString()) +"',";
							SQL +="InfluSource='"+ Global.Left(spnInflu.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Influ='',";
							SQL +="InfluDT='',";
							SQL +="InfluSource='',";
						}

						if(chkHepaA.isChecked()==true)
						{
							EDT = Global.DateValidate(HepaADT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								HepaADT.requestFocus();
								return;
							}
							else if(spnHepaA.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnHepaA.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(HepaADT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								HepaADT.requestFocus();
								return;
							}

							SQL +="HepaA='1',";
							SQL +="HepaADT='"+ Global.DateConvertYMD(HepaADT.getText().toString()) +"',";
							SQL +="HepaASource='"+ Global.Left(spnHepaA.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="HepaA='',";
							SQL +="HepaADT='',";
							SQL +="HepaASource='',";
						}

						if(chkChickenPox.isChecked()==true)
						{
							EDT = Global.DateValidate(ChickenPoxDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								ChickenPoxDT.requestFocus();
								return;
							}
							else if(spnChickenPox.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnChickenPox.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(ChickenPoxDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								ChickenPoxDT.requestFocus();
								return;
							}

							SQL +="ChickenPox='1',";
							SQL +="ChickenPoxDT='"+ Global.DateConvertYMD(ChickenPoxDT.getText().toString()) +"',";
							SQL +="ChickenPoxSource='"+ Global.Left(spnChickenPox.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="ChickenPox='',";
							SQL +="ChickenPoxDT='',";
							SQL +="ChickenPoxSource='',";
						}

						if(chkRabies.isChecked()==true)
						{
							EDT = Global.DateValidate(RabiesDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								RabiesDT.requestFocus();
								return;
							}
							else if(spnRabies.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnRabies.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(RabiesDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								RabiesDT.requestFocus();
								return;
							}

							SQL +="Rabies='1',";
							SQL +="RabiesDT='"+ Global.DateConvertYMD(RabiesDT.getText().toString()) +"',";
							SQL +="RabiesSource='"+ Global.Left(spnRabies.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="Rabies='',";
							SQL +="RabiesDT='',";
							SQL +="RabiesSource='',";
						}


						if(chkIPV.isChecked()==true)
						{
							EDT = Global.DateValidate(IPVDT.getText().toString());
							if(EDT.length()!=0)
							{
								Connection.MessageBox(MemberEvents.this, EDT);
								IPVDT.requestFocus();
								return;
							}
							else if(spnIPV.getSelectedItemPosition()==0)
							{
								Connection.MessageBox(MemberEvents.this, "Select a valid vaccination center.");
								spnIPV.requestFocus();
								return;
							}
							VacD = sdf.parse(Global.DateConvertYMD(IPVDT.getText().toString()));
							if(BD.after(VacD))
							{
								Connection.MessageBox(MemberEvents.this, "Vaccination date should be greater than date of birth["+ Global.DateConvertDMY(DOB) +"].");
								IPVDT.requestFocus();
								return;
							}

							SQL +="IPV='1',";
							SQL +="IPVDT='"+ Global.DateConvertYMD(IPVDT.getText().toString()) +"',";
							SQL +="IPVSource='"+ Global.Left(spnIPV.getSelectedItem().toString(),2) +"',";
						}
						else
						{
							SQL +="IPV='',";
							SQL +="IPVDT='',";
							SQL +="IPVSource='',";
						}

						if(PNoAvailable.equals("1"))
						{
							SQL +="Upload='2' Where PNo='"+ PNumber +"'";
						}
						else
						{
							SQL +="Upload='2' Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'";
						}
						//SQL +="Upload='2' Where Vill||Bari||HH='"+ (Vill+Bari+HH) +"' and SNo='"+ Global.Left(spnMemList.getSelectedItem().toString(),2) +"'";


						C.Save(SQL);

						Connection.MessageBox(MemberEvents.this, "Saved successfully.");
					}
					catch(Exception ex)
					{
						Connection.MessageBox(MemberEvents.this, ex.getMessage());
						return;
					}
				}});


			cmdImmClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});

			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}

	private void PhotoView(String ImageID)
	{
		final Dialog dialog = new Dialog(MemberEvents.this);
		dialog.setTitle("Photo View");
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photoview);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		ImageView photo = (ImageView)dialog.findViewById(R.id.photoZoom);
		File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", ImageID +".jpg");
		if(mFichier1.exists())
		{
			photo.setImageURI(Uri.fromFile(mFichier1));
		}

		dialog.show();
	}


	private void VisitNoteForm(final String Vill, final String Bari, final String HH)
	{
		try
		{
			final Dialog dialog = new Dialog(MemberEvents.this);
			dialog.setTitle("Visit Note");
			//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.visitnote);
			dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(true);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			final TextView txtVisitNote = (TextView)dialog.findViewById(R.id.txtVisitNote);
			txtVisitNote.setText(C.ReturnSingleValue("Select note from tTrans where vill||bari||hh = '"+ (Vill+Bari+HH) +"' and Status='h'"));
			if(txtVisitNote.getText().length()==0)
				txtVisitNote.setText(C.ReturnSingleValue("select note from visits where vill||bari||hh = '"+ (Vill+Bari+HH) +"' order by cast(rnd as int) desc limit 1"));

			Button cmdVisitNoteSave = (Button)dialog.findViewById(R.id.cmdVisitNoteSave);
			cmdVisitNoteSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					C.Save("Update tTrans set Note='"+ txtVisitNote.getText() +"' where status='h' and Vill||bari||hh='"+ (Vill+Bari+HH) +"'");
					dialog.dismiss();
				}
			});

			Button cmdImmClose = (Button)dialog.findViewById(R.id.cmdVisitNoteClose);
			cmdImmClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});


			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}

	private void ContactNoForm(final String Vill, final String Bari, final String HH)
	{
		try
		{
			final Dialog dialog = new Dialog(MemberEvents.this);
			dialog.setTitle("Contact Number");
			//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.contactno);
			dialog.setCanceledOnTouchOutside(true);
			dialog.setCancelable(true);

			Window window = dialog.getWindow();
			WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.TOP;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);


			final TextView txtContactNo = (TextView)dialog.findViewById(R.id.txtContactNo);
			txtContactNo.setText(C.ReturnSingleValue("Select contactno from tTrans where vill||bari||hh = '"+ (Vill+Bari+HH) +"' and Status='h'"));

			Button cmdContactNoSave = (Button)dialog.findViewById(R.id.cmdContactNoSave);
			cmdContactNoSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					C.Save("Update tTrans set ContactNo='"+ txtContactNo.getText() +"' where status='h' and Vill||bari||hh='"+ (Vill+Bari+HH) +"'");
					dialog.dismiss();
				}
			});

			Button cmdContactNoClose = (Button)dialog.findViewById(R.id.cmdContactNoClose);
			cmdContactNoClose.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});


			dialog.show();
		}
		catch(Exception  e)
		{
			Connection.MessageBox(MemberEvents.this, e.getMessage());
			return;
		}
	}

	private int SpinnerSelection(String DataValue)
	{
		int i=0;
		if(DataValue.length()==0)
		{
			i=0;
		}
		else
		{
			i = Integer.parseInt(DataValue);
		}
		return i;
	}




	//Take Photo: Immunization Card
	private static final int ACTION_TAKE_PHOTO_S = 1;
	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private Bitmap mImageBitmap = null;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		File mFichier1 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", "1.jpg");

		if(mFichier1.exists())
		{
			mImageView1.setImageURI(Uri.fromFile(mFichier1));
		}
		mImageView1.setVisibility(View.VISIBLE);

		File mFichier2 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", "2.jpg");
		if(mFichier2.exists())
		{
			mImageView2.setImageURI(Uri.fromFile(mFichier2));
		}
		mImageView2.setVisibility(View.VISIBLE);

		File mFichier3 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", "2.jpg");
		if(mFichier3.exists())
		{
			mImageView3.setImageURI(Uri.fromFile(mFichier3));
		}
		mImageView3.setVisibility(View.VISIBLE);

		File mFichier4 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", "2.jpg");
		if(mFichier4.exists())
		{
			mImageView4.setImageURI(Uri.fromFile(mFichier4));
		}
		mImageView4.setVisibility(View.VISIBLE);

		File mFichier5 = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos", "2.jpg");
		if(mFichier5.exists())
		{
			mImageView5.setImageURI(Uri.fromFile(mFichier5));
		}
		mImageView5.setVisibility(View.VISIBLE);

	}


	public void TakePhoto(String PhotoName)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStorageDirectory()+ File.separator + Global.DatabaseFolder + File.separator + "Photos" , PhotoName+".jpg");

		Uri outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri); //EXTRA_OUTPUT
		startActivityForResult(intent, TAKE_PICTURE);
	}



	protected Dialog onCreateDialog(int id) {
		final Calendar c = Calendar.getInstance();
		String DT = ""; EvDate.getText().toString();

		if (VariableID.equals("btnEvDate"))
		{
			DT = EvDate.getText().toString();
		}
		else if (VariableID.equals("btnLmpDt"))
		{
			DT = txtLmpDt.getText().toString();
		}
		else if (VariableID.equals("btnREvDT"))
		{
			DT = txtREvDT.getText().toString();
		}

		int mYear;
		int mMonth;
		int mDay;

		if(DT.length()==10) {
			mYear = Integer.valueOf(Global.Right(DT, 4));
			mMonth = Integer.valueOf(DT.substring(3, 5));
			mDay = Integer.valueOf(Global.Left(DT, 2));
		}
		else
		{
			mYear = g.mYear;
			mMonth = g.mMonth;
			mDay = g.mDay;
		}

		switch (id) {
			case DATE_DIALOG:
				return new DatePickerDialog(this, mDateSetListener,mYear,mMonth-1,mDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mYear = year; mMonth = monthOfYear+1; mDay = dayOfMonth;
			EditText dtpDate;

			//dtpDate = (EditText)findViewById(R.id.VisitDate);
			if (VariableID.equals("btnEvDate"))
			{
				EvDate.setText(new StringBuilder()
						.append(Global.Right("00"+mDay,2)).append("/")
						.append(Global.Right("00"+mMonth,2)).append("/")
						.append(mYear));
			}
			else if (VariableID.equals("btnLmpDt"))
			{
				txtLmpDt.setText(new StringBuilder()
						.append(Global.Right("00"+mDay,2)).append("/")
						.append(Global.Right("00"+mMonth,2)).append("/")
						.append(mYear));
			}
			else if (VariableID.equals("btnREvDT"))
			{
				txtREvDT.setText(new StringBuilder()
						.append(Global.Right("00"+mDay,2)).append("/")
						.append(Global.Right("00"+mMonth,2)).append("/")
						.append(mYear));
			}
			else if (VariableID.equals("btnBDate"))
			{
				txtBDate.setText(new StringBuilder()
						.append(Global.Right("00"+mDay,2)).append("/")
						.append(Global.Right("00"+mMonth,2)).append("/")
						.append(mYear));
			}

		}
	};


}
