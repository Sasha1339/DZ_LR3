package ru.mpei;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AgentService {

    public static void registerAgent(Agent agent, String serviceType){
        DFAgentDescription ad = new DFAgentDescription();
        ad.setName(agent.getAID());

        ServiceDescription description = new ServiceDescription();
        description.setName("MyService");
        description.setType(serviceType);

        ad.addServices(description);

        try {
            DFService.register(agent, ad);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<AID> findAgents(Agent agent, String serviceName) {
        DFAgentDescription ad = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName);
        ad.addServices(sd);

        try {
            return Arrays.stream(DFService.search(agent, ad)).map(DFAgentDescription::getName).collect(Collectors.toList());
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

    }

}
