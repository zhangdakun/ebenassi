package cn.eben.pcagent.agents;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.tactel.contactcleanapp.cleanapp.CleanAppAdapter;
import se.tactel.contactcleanapp.cleanapp.CleanAppManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;
import cn.eben.agents.service.PduBase;
import cn.eben.agents.utils.CallLogUtil;
import cn.eben.agents.utils.MmsUtil;
import cn.eben.agents.utils.SmsUtil;
import cn.eben.agents.utils.ZipUtils;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;

public class AgentSysRecovery implements AgentBase {
	public static final String TAG = "AgentSysRecovery";
	private String errormsg = "";

	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		//{��ver��:1,��op��:��recoverysys��, ��dsts��:[{��package��:��cn.eben.enote��,��type��:��db��, ��URI��:��/mydoc/enote��},��]}
		//{��result��:��ok��,��code��:0}
		//����:{��result��:��reason��,��code��:x}
		//URI��Ϊdb��URI������ļ����Ŀ¼ͬ���ݣ�Agent�����ͬ�����ȡ�����ļ�
		
		AgentLog.debug(TAG, "processCmd : "+data);
		int errorcode = 0;
		JSONObject jo;
		try {
			jo = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PduBase("{result:\"error ,not a json data\",code:\"1\"}");
		}

		JSONArray ja = null;
		try {
			ja = jo.getJSONArray("dsts");
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
				// String type = null;
				// try {
				// type = jsrc.getString("type");
				// } catch (JSONException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// return new PduBase(
				// "{\"result\":\"error ,type not found\",\"code\":1}");
				// }

				// if (!"db".equals(type)) {
				// // return null;
				// return new PduBase(
				// "{\"result\":\"error ,type not support\",\"code\":1}");
				// }

				String address = "";
				try {
					address = jsrc.getString("URI");
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
				if (new File(address).exists()) {
					// if ("com.android.contacts".equalsIgnoreCase(name)) {
					if (name.contains("contacts")) {
						if (!address.isEmpty()) {
							 if(openBackup(new File(address))) {
								if( isImportFinish()) {
							        CleanAppManager cleanappmanager = CleanAppManager.get(App.getInstance().getApplicationContext());
							        cleanappmanager.startSearch(App.getInstance().getApplicationContext(), new CleanAppAdapter());
								}
							 } else {
								 AgentLog.error(TAG, "recovery contacts error");
							 }
//							new ContactsUtil().restore(address);
//							new File(address).delete();
						}

					} else if ("com.android.mms".equalsIgnoreCase(name)) {
						if (address.endsWith(".zip")) {
							String prefix = SmsUtil.formatDate(System
									.currentTimeMillis());
							String msg = Contants.backUpRoot + prefix
									+ File.separator;
							// String mmsname = Contants.backUpRoot
							// +prefix+File.separator+ "mms.vmg";
							try {
								ZipUtils.unzip(new File(address), new File(msg));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							File[] list = new File(msg).listFiles();

							for (File file : list) {
								AgentLog.debug(TAG,
										"msg file : " + file.getName());
								if (file.getName().contains("sms.vmg")) {
									SmsUtil.doRestoreVMG(App.getInstance()
											.getApplicationContext(), file
											.getAbsolutePath());
								} else if (file.getName().contains("mms.vmg")) {
									new MmsUtil().restoreMms(App.getInstance()
											.getApplicationContext(), file
											.getAbsolutePath());
								}
								file.delete();
							}
							new File(address).delete();
							new File(msg).delete();
						} else {
							SmsUtil.doRestoreVMG(App.getInstance()
									.getApplicationContext(), address);
						}
					} else if (name.contains("catlog")) {
						if (!address.isEmpty()) {
							if (new CallLogUtil().restoreCalllogs(App
									.getInstance().getApplicationContext(),
									address)) {

							} else {
								AgentLog.error(TAG, "recovery call log error");
							}
						}
					} else {
						AgentLog.error(
								TAG,
								"uri not match : "
										+ ContactsContract.Contacts.CONTENT_URI
												.toString());
					}
				} else {
					AgentLog.error(TAG, address + " file not exist");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JSONObject jPackage = new JSONObject();
		if (0 == errorcode) {
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
		} else {
			try {
				jPackage.put("result", "error : " + errormsg);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jPackage.put("code", errorcode);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new PduBase(jPackage.toString());
	}

	private String errDes = "error";

	private boolean openBackup(File savedVCard) {
		if (!savedVCard.exists()) {
			AgentLog.error(TAG, "openBackup ,file not exist");
			errDes = "vcard file not found";
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
									// openVcfIntent.setClassName(packageName,
									// name);
									break;
								} else if (packageName != null
										&& packageName
												.equals("com.ebensz.contacts")
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
			errDes = exception.getMessage();
			return false;
		}

		return true;
	}

	private boolean isImportServiceRunning() {
		AgentLog.debug(TAG, "isImportServiceRunning,");
		ActivityManager manager = (ActivityManager) App.getInstance()
				.getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);

		// com.android.contacts
		// com.ebensz.contacts
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
//			AgentLog.debug(TAG,service.service.getClassName());
//			if ("com.android.contacts.vcard.VCardService"
//					.equals(service.service.getClassName())) {
//				AgentLog.debug(TAG, "com.android.contacts.vcard.VCardService");
//				
//				return true;
//			} else if ("com.ebensz.contacts.vcard.VCardService"
//					.equals(service.service.getClassName())) {
//				AgentLog.debug(TAG, "com.ebensz.contacts.vcard.VCardService");
//				return true;
//			} else 
				if ("com.android.contacts.common.vcard.VCardService"
				.equals(service.service.getClassName())) {
				AgentLog.debug(TAG, "com.android.contacts.common.vcard.VCardService");
				return true;
			}			
			// AgentLog.info(TAG,
			// "service name : " + service.service.getClassName());
		}
		return false;
	}

	private boolean isImportFinish() {
		boolean isrun = true;
		while (isrun) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!isImportServiceRunning()) {
				isrun = false;
			}
		}
		
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
	}
}
