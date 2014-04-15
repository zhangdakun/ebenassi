package cn.eben.pcagent.agents;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;

public class AgentAppInfo implements AgentBase{

	public static final String TAG = "AgentAppInfo";
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		
		AgentLog.debug(TAG, "processCmd : "+data);
		return null;
	}

}
