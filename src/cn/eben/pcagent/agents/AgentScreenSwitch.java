package cn.eben.pcagent.agents;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import cn.eben.agents.service.PduBase;
import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.MainActivity;

public class AgentScreenSwitch  implements AgentBase{
	
	public static final String TAG = "AgentScreenSwitch";
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		//{ver:1,op:lockscreen,on:true/false}
		AgentLog.debug(TAG, "processCmd : "+data);
		
		JSONObject jo;
		try {
			jo = new JSONObject(data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new PduBase("{result:\"error ,not a json data\",code:\"1\"}");
		}
		boolean lock = false;
		try {
			lock = jo.getBoolean("on");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String msg = "";
		try {
			msg=jo.getString("showmsg");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(lock) {
			Intent lockintent = new Intent();
			lockintent.setClass(App.getInstance().getApplicationContext(), MainActivity.class);
			lockintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			lockintent.putExtra("lock", 0);
			
			lockintent.putExtra("showmsg", msg);
			
			App.getInstance().getApplicationContext().startActivity(lockintent);
		} else {
			try{
//			App.getInstance().getLockActivity().exit();
//				MainActivity.mInstace.exit();
				
				Intent lockintent = new Intent("cn.eben.pcmsg");
//				lockintent.setClass(App.getInstance().getApplicationContext(), MainActivity.class);
//				lockintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				lockintent.putExtra("lock", 1);
				
//				App.getInstance().getApplicationContext().startActivity(lockintent);
				App.getInstance().getApplicationContext().sendBroadcast(lockintent);
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
//		File testfFile = new File("/sdcard/update.zip");
//		if(testfFile.exists()) {
//			AgentLog.debug(TAG, "file de pth: "+testfFile.getAbsolutePath());
//		}
//		String update = Environment.getExternalStorageDirectory().toString()+File.separator+"update.zip";
//		File file = new File(update);
//		if(!file.exists()) {
//			AgentLog.error(TAG, "file not exist ,try other ");
//			file = new File("/mnt/sdcard/update.zip");
//			if(!file.exists()) {
//				AgentLog.error(TAG, "mnt/sdcard/update.zip file not exist ,try other ");
////				file = new File
//			}
//			
//		}
		
//		new CallLogUtil().restoreCalllogs(App.getInstance().
//				getApplicationContext(), "/mnt/sdcard/calllog1.xml");
//		String prefix = SmsUtil.formatDate(System.currentTimeMillis());
//		String smsname = Contants.backUpRoot +prefix+File.separator+ "sms.vmg";
//		String mmsname = Contants.backUpRoot +prefix+File.separator+ "mms.vmg";
//		new MmsUtil().backupMms(App.getInstance().getApplicationContext(), mmsname);
//		SmsUtil.backupSms(smsname);
//		
//		String zipname = Contants.backUpRoot +prefix+"msg.zip";
//		try {
//			ZipUtils.zip(new File(Contants.backUpRoot +prefix+File.separator), new File(zipname));
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		String updateaction = "com.ebensz.SYSTEM_UPDATE_REMOTE";
//	     Intent localIntent = new Intent(updateaction);
//	      localIntent.putExtra("file_name", "/sdcard/update.zip");
//	      localIntent.putExtra("show_update_settings", true);
//	      App.getInstance().getApplicationContext().startService(localIntent);
//		Method recovery;
//		try {
//			try {
//				Class<?> c = Class.forName("android.os.RecoverySystem");
////				recovery = RecoverySystem.class.getMethod("installPackage", Context.class,File.class);
//				recovery = c.getMethod("installPackage", Context.class,File.class);
////				recovery.invoke(RecoverySystem.class, App.getInstance().getApplicationContext(),file);
//				try {
//					recovery.invoke(c, App.getInstance().getApplicationContext(),file);
//				} catch (IllegalAccessException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} catch (ClassNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//
//		} catch (NoSuchMethodException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
//		try {
//			RecoverySystem.installPackage(App.getInstance().getApplicationContext(), file);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
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

//		backupSms() ;
		return new PduBase(jPackage.toString());
	}


//	private void backupSms() {
//		ArrayList list = SmsUtil.getMessages(App.getInstance()
//				.getApplicationContext());
//
//		File file1 = new File(Contants.backUpRoot + "backup.vmg");
//		file1.getParentFile().mkdirs();
//		FileOutputStream fileoutputstream = null;
//		OutputStreamWriter outputstreamwriter = null;
//		try {
//			fileoutputstream = new FileOutputStream(file1);
//
//			outputstreamwriter = new OutputStreamWriter(fileoutputstream,
//					"UTF-8");
//			int i = 0;
//			try {
//				outputstreamwriter
//						.write((new StringBuilder(
//								"BEGIN:VMSG\nVERSION: 1.1\nX-IRMS-TYPE:MSG\nX-MESSAGE-TYPE:"))
//								.append(((SmsMessage) list.get(i))
//										.getMESSAGE_TYPE())
//								.append("\n")
//								.append("X-MA-TYPE:")
//								.append("")
//								.append("\nBEGIN:VCARD\nVERSION: 2.1\nTEL:")
//								.append(((SmsMessage) list.get(i)).getTEL())
//								.append("\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate ")
//								.append(((SmsMessage) list.get(i)).getVTIME())
//								.append("\n")
//								.append(((SmsMessage) list.get(i)).getVBODY())
//								.append("\nEND:VBODY\nEND:VENV\nEND:VMSG\n")
//								.toString());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			if(null != outputstreamwriter) {
//				try {
//					outputstreamwriter.close();
//					outputstreamwriter = null;
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}
//
//	}
}
