package cn.eben.pcagent.agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;

public class AgentSysBackup implements AgentBase {

	public static final String TAG = "AgentSysBackup";

	// {ver:1,op:sysbackup,srcs:[{package:cn.eben.enote,type:db,URI:[¡°content://uri¡±,¡­]},¡­]}
	//
	// {result:ok,code:0,srcs:[ {package:cn.eben.enote,type:db,
	// ¡°content://uri¡±:¡±/mydoc/enote¡±},¡­]}
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
		AgentLog.debug(TAG, "uri contact : "
				+ ContactsContract.Contacts.CONTENT_URI.toString());
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
				String type = null;
				try {
					type = jsrc.getString("type");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new PduBase(
							"{result:\"error ,type not found\",code:\"1\"}");
				}

				if (!"db".equals(type)) {
					// return null;
					return new PduBase(
							"{result:\"error ,type not support\",code:\"1\"}");
				}

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
				if ("com.android.contacts".equalsIgnoreCase(name)) {
					target = Contants.backUpRoot
							+ getFileName(System.currentTimeMillis()) + ".vcf";
					if(exportVcf(target)) {
					JSONObject jContact = new JSONObject();
					jContact.put("package", name);
					jContact.put("URI", target);
					

					jAppResult.put(jContact);
					}
				} else if (ContactsContract.Contacts.CONTENT_URI.toString()
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

		return new PduBase(jPackage.toString());
	}

	private boolean exportVcf(String target) {
		
		File dir = new File(Contants.backUpRoot);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File targetFile = new File(target) ;
		
		if(targetFile.exists()) {
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
}
