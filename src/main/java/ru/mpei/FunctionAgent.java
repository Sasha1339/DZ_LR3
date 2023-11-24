package ru.mpei;

import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FunctionAgent extends Agent {

    @Override
    protected void setup() {
        AgentService.registerAgent(this, "Thread");
        AgentDetectorClass agentDetectorClass = new AgentDetectorClass(1200, this, "Thread");
        RunningSendDiscovering runningSendDiscovering = new RunningSendDiscovering(this, agentDetectorClass);
        runningSendDiscovering.run();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }


}
