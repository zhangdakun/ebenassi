package cn.eben.pcagent.contacts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cn.eben.agents.utils.SmsMessage;
import cn.eben.pcagent.AgentLog;

public class ContactsUtil {

	public void restore(String vfc) {
		BufferedReader bufferedreader = null;
		FileInputStream fileinputstream = null;

		File file = new File(vfc);
		if (!file.exists()) {
			AgentLog.error("pcagent", "vfc not exist");
			return ;
		}
		
		try {

			fileinputstream = new FileInputStream(file);
			bufferedreader = new BufferedReader(new InputStreamReader(
					fileinputstream));
			boolean flag1;
			boolean flag2;
			SmsMessage smsmessage;
			StringBuffer stringbuffer = null;
			flag1 = false;
			flag2 = false;
			smsmessage = null;

			String s;
			
			StringBuffer item = new StringBuffer();
			while(null != (s = bufferedreader.readLine())) {

			if (s.contains("BEGIN:VCARD")) {
				item.delete(0, item.length());
				item.append(s).append("\n");
				flag1 = true;

			} else if (flag1)
				if (s.contains("END:VCARD")) {
					item.append(s).append("\n");;
					flag1 = false;
					
					byte[] itemContent = item.toString().getBytes();
			        Contact c = new Contact();
			        c.setVCard(itemContent);
			        
				} else {
					item.append(s).append("\n");;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != fileinputstream) {
				try {
					fileinputstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			
				}
			}

			if (null != bufferedreader) {
				try {
					bufferedreader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
}
