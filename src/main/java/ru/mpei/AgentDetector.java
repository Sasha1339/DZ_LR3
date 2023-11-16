package ru.mpei;

import jade.core.AID;

import java.util.List;

public interface AgentDetector {

    void startPublishing(AID aid, int port);
    void startDiscovering();
    List<AID> getActiveAgents();

}
