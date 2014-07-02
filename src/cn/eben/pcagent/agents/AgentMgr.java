package cn.eben.pcagent.agents;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.eben.pcagent.AgentLog;
import cn.eben.pcagent.service.PduBase;


public class AgentMgr {
	
	public static final String TAG_LOG = "AgentMgr";
	
	private static Map<String, Class> agentMap = new HashMap();

	static {
		try {
			agentMap.put("getsysinfo", Class.forName("cn.eben.pcagent.agents.AgentSysInfo"));
			agentMap.put("backupsys", Class.forName("cn.eben.pcagent.agents.AgentSysBackup"));
			agentMap.put("recoverysys", Class.forName("cn.eben.pcagent.agents.AgentSysRecovery"));
			agentMap.put("switchfilesys", Class.forName("cn.eben.pcagent.agents.AgentSwitchFileSys"));
			agentMap.put("getappsinfo", Class.forName("cn.eben.pcagent.agents.AgentAppInfo"));
			agentMap.put("lockscreen", Class.forName("cn.eben.pcagent.agents.AgentScreenSwitch"));
			
			agentMap.put("updatepeijian", Class.forName("cn.eben.pcagent.agents.AgentPeijianSys"));
			agentMap.put("ispeijianready", Class.forName("cn.eben.pcagent.agents.AgentPeijianSys"));
			agentMap.put("getpeijianapps", Class.forName("cn.eben.pcagent.agents.AgentPeijianSys"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	{ver:1,op:getsysinfo}
//	{result:ok,code:0,dev:¡±Eben T8¡±,SN:H7xxxx,fwver:2.2,total:16G,free:7.3G}
	public PduBase processPdu(PduBase pdubase) {
		
		String data = pdubase.getData();
		PduBase sPdu = null;
		try {
			JSONObject jo = new JSONObject(data);
			
			String op = jo.getString("op");
			
			Class agentClass = agentMap.get(op);
			
			if(null != agentClass) {
				AgentBase agent = createAgent(agentClass);
				if(null != agent) {
					sPdu = agent.processCmd(data);
				} else {
					AgentLog.error(TAG_LOG, "error ,not create agent ,"+op);
				}
			}else {
				AgentLog.error(TAG_LOG, "error ,not find agent ,"+op);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sPdu;
		
	}
	
    public AgentBase createAgent(Class agentClass) {
    	AgentBase agent = null;
        if (agentClass != null) {
            try {
            	agent =  (AgentBase) agentClass.newInstance();
            } catch (Exception e) {
                AgentLog.error(TAG_LOG, "Cannot instantiate agentClass");
            }
        } else {
        	AgentLog.error(TAG_LOG, "Cannot create agentClass instance");
        }

        return agent;
    }
    
}
