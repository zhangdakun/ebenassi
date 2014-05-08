package cn.eben.pcagent.agents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;


import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.App;
import cn.eben.pcagent.service.PduBase;
import cn.eben.pcagent.utils.SmsMessage;
import cn.eben.pcagent.utils.SmsUtil;

public class AgentScreenSwitch  implements AgentBase{
	
	public static final String TAG = "AgentScreenSwitch";
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		//{ver:1,op:lockscreen,on:true/false}
		AgentLog.debug(TAG, "processCmd : "+data);

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
