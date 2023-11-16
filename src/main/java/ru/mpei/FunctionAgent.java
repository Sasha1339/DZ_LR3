package ru.mpei;

import jade.core.AID;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FunctionAgent extends Agent {


    @Override
    protected void setup() {
        AgentService.registerAgent(this, "Thread");
        AgentDetectorClass agentDetectorClass = new AgentDetectorClass(1200, this, "Thread");

        ScheduledExecutorService service = Executors.newScheduledThreadPool(3);

        List<AID> agents = AgentService.findAgents(this, "Thread");
        AID aid1 = agents.stream()
                .filter(aid -> aid.getName().equals(this.getName()))
                .findFirst().orElseThrow();

        service.scheduleAtFixedRate(() -> {
            agentDetectorClass.startPublishing(aid1, 1200);
        }, 100, 1000, TimeUnit.MILLISECONDS);

        service.scheduleAtFixedRate(agentDetectorClass::startDiscovering, 200, 1000, TimeUnit.MILLISECONDS);

        service.scheduleAtFixedRate(agentDetectorClass::checkAgents, 10000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }


}
