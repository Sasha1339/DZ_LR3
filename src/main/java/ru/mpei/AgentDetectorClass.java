package ru.mpei;

import com.sun.jna.NativeLibrary;
import jade.core.AID;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class AgentDetectorClass implements AgentDetector{
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private Agent agent;

    private int count = 1;

    private String nameService;

    private RawUdpSocketServer rawUdpSocketServer;

    private RawUdpSocketClient rawUdpSocketClient;

    private PacketDecoder packetDecoder;
    private List<AID> firstAgents = new CopyOnWriteArrayList<>();
    private List<String> agents = new CopyOnWriteArrayList<>();

    public AgentDetectorClass(int port, Agent agent, String nameService) {
        this.agent = agent;
        this.nameService = nameService;
        this.rawUdpSocketServer = new RawUdpSocketServer(agent.getLocalName());
        this.rawUdpSocketClient = new RawUdpSocketClient();
        this.packetDecoder = new PacketDecoder();
        rawUdpSocketServer.init(port);
        rawUdpSocketClient.init(port);
    }

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")){
            NativeLibrary.addSearchPath("wpcap", "C:\\Windows\\System32\\Npcap");
        }
    }

    public void clearList(){
        agents.clear();
    }

    @Override
    public void startPublishing(AID aid, int port) {
            if (agent.isAlive()){
                byte[] data = packetDecoder.code("{\"agentName\":" + "\""+aid.getLocalName()+"\"" + ", \"guid\":" + true + "}");
                //byte[] data = packetDecoder.code("hello0");
                //rawUdpSocketClient.send(new byte[]{2, 0, 0, 0, 69, 0, 0, 34, 2, 82, 0, 0, 64, 17, 0, 0, 10, 3, 2, 71, 10, 3, 2, 71, -105, 33, 4, -80, 0, 14, 7, 107, 104, 101, 108, 108, 111, 48});
                rawUdpSocketClient.send(data);
            } else {
               rawUdpSocketServer.setLoop(false);
            }
    }

    @Override
    public void startDiscovering() {
        rawUdpSocketServer.start(agents);
    }

    @Override
    public List<AID> getActiveAgents() {
        List<AID> aids = new ArrayList<>();
        List<AID> agents = AgentService.findAgents(agent, nameService);
        List<String> nameAgents = this.agents;
        for(AID nameAgent: agents){
            if (nameAgents.contains(nameAgent.getLocalName())){
                aids.add(nameAgent);
            }
        }
        return aids;
    }

    public void checkAgents(){
            List<AID> newAgents = this.getActiveAgents();
            //System.out.println(newAgents.toString());
            if (this.getCount() == 1 && !newAgents.isEmpty()){
                firstAgents = newAgents;
                this.setCount(2);
            } else if((firstAgents.size()>1 && !newAgents.isEmpty()) || (firstAgents.size() == 1 && newAgents.isEmpty())) {
                if (firstAgents.size() != newAgents.size()){
                    for (AID aid: firstAgents){
                        if (!newAgents.contains(aid)){
                            log.warn(agent.getLocalName()+": Agent "+aid.getLocalName()+" was dead!");
                            firstAgents.remove(aid);
                            break;
                        }
                    }
                }
            }
            this.clearList();
    }

}
