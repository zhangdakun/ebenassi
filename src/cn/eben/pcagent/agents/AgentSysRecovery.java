package cn.eben.pcagent.agents;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;
import cn.eben.pcagent.utils.MmsUtil;
import cn.eben.pcagent.utils.SmsUtil;
import cn.eben.pcagent.utils.ZipUtils;

public class AgentSysRecovery implements AgentBase{
	public static final String TAG = "AgentSysRecovery";
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		//{“ver”:1,”op”:”recoverysys”, “dsts”:[{“package”:”cn.eben.enote”,”type”:”db”, “URI”:”/mydoc/enote”},…]}
		//{“result”:”ok”,”code”:0}
		//或者:{“result”:”reason”,”code”:x}
		//URI：为db的URI，数据文件存放目录同备份，Agent根据相同规则获取本地文件
		
		AgentLog.debug(TAG, "processCmd : "+data);
		
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
//				if ("com.android.contacts".equalsIgnoreCase(name)) {
				if (name.contains("contacts")) {
					if(!address.isEmpty()) {
						if(openBackup(new File(address))) {
							isImportFinish();
						} else {
							AgentLog.error(TAG, "recovery contacts error");
						}
					}

				} else if("com.android.mms".equalsIgnoreCase(name)) {
					if(address.endsWith(".zip")) {
						String prefix = SmsUtil.formatDate(System.currentTimeMillis());
						String msg = Contants.backUpRoot +prefix+File.separator ;
//						String mmsname = Contants.backUpRoot +prefix+File.separator+ "mms.vmg";
						try {
							ZipUtils.unzip(new File(address), new File(msg));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						File[] list = new File(msg).listFiles();
						
						for(File file : list) {
							AgentLog.debug(TAG, "msg file : "+file.getName());
							if(file.getName().contains("sms.vmg")) {
								SmsUtil.doRestoreVMG(App.getInstance().getApplicationContext(),file.getAbsolutePath());
							}else if(file.getName().contains("mms.vmg")) {
								new MmsUtil().restoreMms(App.getInstance().getApplicationContext(), file.getAbsolutePath());
							}
						}
					}else {
						SmsUtil.doRestoreVMG(App.getInstance().getApplicationContext(),address);
					}
				}
				else {
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
//									openVcfIntent.setClassName(packageName, name);
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
