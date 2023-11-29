package ru.mpei;

import jade.core.AID;
import jade.core.Agent;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * каждые 400 мс происходит публикация сообщений от каждого агента
 * каждые 2 сек происходит проверка на то, жив ли агент
 * каждую секунду происходит накопление сообщений от остальных агентов
 */
public class RunningSendDiscovering {

    private Agent agent;
    private AgentDetectorClass agentDetectorClass;
    public RunningSendDiscovering(Agent agent, AgentDetectorClass agentDetectorClass){
        this.agent = agent;
        this.agentDetectorClass = agentDetectorClass;
    }

    public void run(){
        ScheduledExecutorService service = Executors.newScheduledThreadPool(3);
        List<AID> agents = AgentService.findAgents(agent, "Thread");
        AID aid1 = agents.stream()
                .filter(aid -> aid.getName().equals(agent.getName()))
                .findFirst().orElseThrow();

        service.scheduleWithFixedDelay(() -> {
            agentDetectorClass.startPublishing(aid1, 1200);
        }, 100, 400, TimeUnit.MILLISECONDS);
        service.scheduleWithFixedDelay(() -> agentDetectorClass.checkAgents(),
                1000, 2000, TimeUnit.MILLISECONDS);

        agentDetectorClass.startDiscovering();
    }

}
