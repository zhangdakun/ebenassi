package cn.eben.pcagent.agents;

import org.json.JSONException;
import org.json.JSONObject;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;

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


}
