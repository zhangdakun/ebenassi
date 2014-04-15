package cn.eben.pcagent.agents;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;

public class AgentSysBackup implements AgentBase{

	public static final String TAG = "AgentSysBackup";
	
	@Override
	public PduBase processCmd(String data) {
		// TODO Auto-generated method stub
		
		AgentLog.debug(TAG, "processCmd : "+data);
		
		return null;
	}


}
