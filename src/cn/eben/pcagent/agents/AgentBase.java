package cn.eben.pcagent.agents;

import cn.eben.pcagent.service.PduBase;

public interface AgentBase {

	public PduBase processCmd(String data);
}
