package cn.eben.pcagent.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SmsUtil {

	public SmsUtil() {
	}

	public static long dateToTime(String s) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
		long l;
		try {
			l = simpledateformat.parse(s).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			l = System.currentTimeMillis();
		}

		return l;

	}

	private static String formatDate(long l) {
		String s;
		if (l > 0L)
			s = (new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
					Locale.ENGLISH)).format(new Date(l));
		else
			s = null;
		return s;
	}

	public static List getBackup(ArrayList arraylist) {
		ArrayList arraylist1;
		BufferedReader bufferedreader;
		int i;
		FileInputStream fileinputstream;
		arraylist1 = new ArrayList();
		bufferedreader = null;
		i = 0;
		fileinputstream = null;

		int j = arraylist.size();
		// if(i < j) goto _L2; else goto _L1
		// _L1:
		// if(fileinputstream == null) goto _L4; else goto _L3
		// _L3:
		// fileinputstream.close();
		// FileInputStream fileinputstream6 = null;
		// _L26:
		// if(bufferedreader == null) goto _L6; else goto _L5
		// _L5:
		// bufferedreader.close();
		// fileinputstream6;
		// _L18:
		// ArrayList arraylist2 = arraylist1;
		// _L11:
		// return arraylist2;
		// _L2:
		File file;
		boolean flag;
		file = new File((String) arraylist.get(i));
		flag = file.getParentFile().exists();

		try {

			FileInputStream fileinputstream4 = new FileInputStream(file);
			BufferedReader bufferedreader2 = new BufferedReader(
					new InputStreamReader(fileinputstream4));
			boolean flag1;
			boolean flag2;
			SmsMessage smsmessage;
			StringBuffer stringbuffer;
			flag1 = false;
			flag2 = false;
			smsmessage = null;
			stringbuffer = null;

			String s;

			{
				s = bufferedreader2.readLine();

				if (null == s) {
					return arraylist1;
				}
				i++;
				bufferedreader = bufferedreader2;
				fileinputstream = fileinputstream4;
			}

			if (s.contains("BEGIN:VMSG")) {
				flag1 = true;
				StringBuffer stringbuffer1 = new StringBuffer();
				smsmessage = new SmsMessage();
				stringbuffer = stringbuffer1;
			} else if (flag1)
				if (s.contains("X-MESSAGE-TYPE:")) {
					int k = 1 + s.indexOf(":");
					if (s.substring(k, s.length()).equals("DELIVER"))
						smsmessage.setMESSAGE_TYPE("1");
					else if (s.substring(k, s.length()).equals("SUBMIT"))
						smsmessage.setMESSAGE_TYPE("2");
					else
						smsmessage.setMESSAGE_TYPE("2");
				} else if (s.contains("X-MA-TYPE:"))
					smsmessage.setMA_TYPE(s.substring(1 + s.indexOf(":"),
							s.length()));
				else if (s.contains("TEL:"))
					smsmessage.setTEL(s.substring(4, s.length()));
				else if (s.contains("BEGIN:VBODY"))
					flag2 = true;
				else if (s.contains("END:VBODY"))
					flag2 = false;
				else if (s.contains("END:VMSG")) {
					arraylist1.add(smsmessage);
					flag1 = false;
				} else if (flag2)
					if (s.contains("Date")) {
						smsmessage.setVTIME(s.substring(4, s.length()).trim());
					} else {
						stringbuffer.append(s);
						smsmessage.setVBODY(stringbuffer.toString());
					}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return arraylist1;
	}

	public static String getCurrentTime() {
		return (new SimpleDateFormat("MMM dd yyyy HH:mm:ss aa zzz"))
				.format(new Date());
	}

	public static String getDate() {
		return (new SimpleDateFormat("yyyy-MM-dd hh-mm", Locale.getDefault()))
				.format(new Date(System.currentTimeMillis()));
	}

	public static ArrayList getMessages(Context context) {
		ArrayList arraylist = new ArrayList();
		String[] _tmp = (String[]) null;
		String as[] = new String[6];
		as[0] = "_id";
		as[1] = "address";
		as[2] = "date";
		as[3] = "type";
		as[4] = "body";
		Cursor cursor = context.getContentResolver().query(smsUri, as,
				"type !=3", null, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				SmsMessage smsmessage = new SmsMessage();
				smsmessage.setTEL(cursor.getString(1));
				smsmessage.setVTIME(formatDate(cursor.getLong(2)));
				int i = cursor.getInt(3);
				if (i == 1)
					smsmessage.setMESSAGE_TYPE("DELIVER");
				else if (i == 2)
					smsmessage.setMESSAGE_TYPE("SUBMIT");
				else
					smsmessage.setMESSAGE_TYPE("SUBMIT");
				smsmessage.setVBODY(cursor.getString(4));
				arraylist.add(smsmessage);
			}
			cursor.close();
		}
		return arraylist;
	}

	public static final String ADDRESS = "address";
	public static final String BODY = "body";
	public static final String DATE = "date";
	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;
	public static final String MODE = "band";
	public static final String PERSON = "person";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final Uri smsUri = Uri.parse("content://sms/");
	public static final String vmgAddress = (new StringBuilder(
			String.valueOf(Environment.getExternalStorageDirectory()
					.getAbsolutePath()))).append("/ct_backup/sms.vmg")
			.toString();

}
