package cn.eben.pcagent.agents;

import cn.eben.agents.service.PduBase;

public interface AgentBase {

	public PduBase processCmd(String data);
}
