package cn.eben.pcagent.agents;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;

public class AgentSysRecovery implements AgentBase{
	public static final String TAG = "AgentSysRecovery";
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		
		AgentLog.debug(TAG, "processCmd : "+data);
		
		return null;
	}


}
