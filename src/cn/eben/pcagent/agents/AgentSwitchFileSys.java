package cn.eben.pcagent.agents;

import org.json.JSONException;
import org.json.JSONObject;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;

public class AgentSwitchFileSys implements AgentBase{

	public static final String TAG = "AgentSwitchFileSys";
	
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		
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
