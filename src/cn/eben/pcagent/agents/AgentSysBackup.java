package cn.eben.pcagent.agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;
import cn.eben.pcagent.utils.SmsUtil;

public class AgentSysBackup implements AgentBase {

	public static final String TAG = "AgentSysBackup";

	// {“ver”:1,”op”:”backupsys”,”srcs”:[{“package”:”cn.eben.enote”,”type”:”db”,”URI”:
	// “content://uri”},…]}
	// {“result”:”ok”,”code”:0,”srcs”:[ {“package”:”cn.eben.enote”,”type”:”db”,
	// “URI”:”/mydoc/enote”},…]}
	// 或者:{“result”:”reason”,”code”:x}
	// URI:响应中的URI应该为db产生的文件存放的新目录，建议为包名目录

	// private SimpleDateFormat mDateFormat = new
	// SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	private SimpleDateFormat mDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	private String getFileName(long time) {
		return mDateFormat.format(new Date(time));
	}

	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub

		AgentLog.debug(TAG, "processCmd : " + data);

		JSONObject jo;
		try {
			jo = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PduBase("{result:\"error ,not a json data\",code:\"1\"}");
		}
		// [{package:com.android.contacts,type:db,"URI":"content://com.android.contacts/contacts"}]}
		// String type = null;
		// try {
		// type = jo.getString("type");
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// return new PduBase("{result:\"error ,type not found\",code:\"1\"}");
		// }
		//
		// if(!"db".equals(type)) {
		// // return null;
		// return new
		// PduBase("{result:\"error ,type not support\",code:\"1\"}");
		// }
//		AgentLog.debug(TAG, "uri contact : "
//				+ ContactsContract.Contacts.CONTENT_URI.toString());
		JSONArray ja = null;
		try {
			ja = jo.getJSONArray("srcs");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PduBase("{result:\"error ,srcs not found\",code:\"1\"}");
		}

		JSONArray jAppResult = new JSONArray();
		String target = "";
		for (int i = 0; i < ja.length(); i++) {
			try {

				JSONObject jsrc = ja.getJSONObject(i);
//				String type = null;
//				try {
//					type = jsrc.getString("type");
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					return new PduBase(
//							"{\"result\":\"error ,type not found\",\"code\":1}");
//				}

//				if (!"db".equals(type)) {
//					// return null;
//					return new PduBase(
//							"{\"result\":\"error ,type not support\",\"code\":1}");
//				}

				String app = "";
				try {
					app = jsrc.getString("URI");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String name = "";
				try {
					name = jsrc.getString("package");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				if ("com.android.contacts".equalsIgnoreCase(name)) {
				if (name.contains("contacts")) {
					target = Contants.backUpRoot
							+ getFileName(System.currentTimeMillis()) + ".vcf";
					if (exportVcf(target)) {
						JSONObject jContact = new JSONObject();
						jContact.put("package", name);
						jContact.put("URI", target);

						jAppResult.put(jContact);
					}
				} else if("com.android.mms".equalsIgnoreCase(name)) {
					String smsfile = SmsUtil.backupSms();
					if(null != smsfile) {
						JSONObject jContact = new JSONObject();
						jContact.put("package", name);
						jContact.put("URI", smsfile);

						jAppResult.put(jContact);
					} else {
						AgentLog.error(TAG, "failed to backup sms");
					}
				}
				else if (ContactsContract.Contacts.CONTENT_URI.toString()
						.equalsIgnoreCase(app)) {

				} else {
					AgentLog.error(TAG, "uri not match : "
							+ ContactsContract.Contacts.CONTENT_URI.toString());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JSONObject jPackage = new JSONObject();
		try {
			jPackage.put("result", "ok");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			jPackage.put("code", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			jPackage.put("srcs", jAppResult);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// test ..
//		openBackup(new File("/mnt/sdcard/backup.vcf"));
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		isImportFinish();
		// App.getInstance().getApplicationContext().runOnUiThread(new
		// Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// openBackup(new File("/mnt/sdcard/backup.vcf"));
		// }});
		// //test
		return new PduBase(jPackage.toString());
	}

	private boolean exportVcf(String target) {

		File dir = new File(Contants.backUpRoot);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File targetFile = new File(target);

		if (targetFile.exists()) {
			return true;
		} else {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		ContentResolver cr = App.getInstance().getApplicationContext()
				.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		int index = cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
		try {

			FileOutputStream fout = new FileOutputStream(target);
			byte[] data = new byte[1024 * 1];
			while (cur.moveToNext()) {
				String lookupKey = cur.getString(index);
				Uri uri = Uri.withAppendedPath(
						ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
				AssetFileDescriptor fd = cr.openAssetFileDescriptor(uri, "r");
				FileInputStream fin = fd.createInputStream();
				int len = -1;
				while ((len = fin.read(data)) != -1) {
					fout.write(data, 0, len);
				}
				fin.close();
			}
			fout.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private boolean openBackup(File savedVCard) {
		if (!savedVCard.exists()) {
			AgentLog.error(TAG, "openBackup ,file not exist");
			return false;
		}
		try {
			String vcfMimeType = MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension("vcf");
			Intent openVcfIntent = new Intent(Intent.ACTION_VIEW);
			openVcfIntent.setDataAndType(Uri.fromFile(savedVCard), vcfMimeType);
			// Try to explicitly specify activity in charge of opening the vCard
			// so that the user doesn't have to choose
			// http://stackoverflow.com/questions/6827407/how-to-customize-share-intent-in-android/9229654#9229654
			try {
				if (App.getInstance().getApplicationContext()
						.getPackageManager() != null) {
					List<ResolveInfo> resolveInfos = App.getInstance()
							.getApplicationContext().getPackageManager()
							.queryIntentActivities(openVcfIntent, 0);
					if (resolveInfos != null) {
						for (ResolveInfo resolveInfo : resolveInfos) {
							ActivityInfo activityInfo = resolveInfo.activityInfo;
							if (activityInfo != null) {
								String packageName = activityInfo.packageName;
								String name = activityInfo.name;
								// Find the needed Activity based on Android
								// source files:
								// http://grepcode.com/search?query=ImportVCardActivity&start=0&entity=type&n=
								if (packageName != null
										&& packageName
												.equals("com.android.contacts")
										&& name != null
										&& name.contains("ImportVCardActivity")) {
									openVcfIntent.setPackage(packageName);
									break;
								}
							}
						}
					}
				}
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
			openVcfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			App.getInstance().getApplicationContext()
					.startActivity(openVcfIntent);
		} catch (Exception exception) {
			exception.printStackTrace();
			// No app for openning .vcf files installed (unlikely)
			return false;
		}

		return true;
	}
	
	
	private boolean isImportServiceRunning() {
		AgentLog.debug(TAG, "isMyServiceRunning,");
		ActivityManager manager = (ActivityManager) App.getInstance()
				.getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.android.contacts.vcard.VCardService"
					.equals(service.service.getClassName())) {
				return true;
			}
//			AgentLog.info(TAG,
//					"service name : " + service.service.getClassName());
		}
		return false;
	}
	
	private boolean isImportFinish() {
		boolean isrun = true;
		while(isrun) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!isImportServiceRunning()) {
			isrun = false;
		}
		}
		return true;
	}
}
