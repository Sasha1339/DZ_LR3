package ru.mpei;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Slf4j
public class RawUdpSocketServer {

    private PcapHandle pcapHandle;

    private ObjectMapper mapper = new ObjectMapper();

    private AgentInfo agentInfo;

    private String nameAgent;

    private boolean isLoop = true;


    public RawUdpSocketServer(String nameAgent){
        this.nameAgent = nameAgent;
    }

//    public void clearAgents(){
//        agents.clear();
//    }

    @SneakyThrows
    public void init(int port){
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();

        PcapNetworkInterface interfacePcap = allDevs.stream()
                .filter(a -> a.getName().equals("\\Device\\NPF_Loopback")).findFirst().orElseThrow();
        System.out.println(interfacePcap);
        pcapHandle = interfacePcap.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 5000);
        pcapHandle.setFilter("ip proto \\udp && dst port "+port, BpfProgram.BpfCompileMode.NONOPTIMIZE);
    }


    @SneakyThrows
    public void start(List<String> agents){
        pcapHandle.loop(0, (PacketListener) p -> {
            byte[] rawData = p.getRawData();
            byte[] data = new byte[rawData.length-32];
            System.arraycopy(rawData, 32, data, 0, data.length);
            String stringData = new String(data, StandardCharsets.UTF_8);
            try {
                agentInfo = mapper.readValue(stringData, new TypeReference<AgentInfo>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if (!agentInfo.getAgentName().equals(nameAgent)){
                log.info(nameAgent+": Received message from "+agentInfo.getAgentName());
                agents.add(agentInfo.getAgentName());
            }
            if (!isLoop){
                try {
                    pcapHandle.breakLoop();
                } catch (NotOpenException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

//    @SneakyThrows
//    public void checkedAliveAgents(){
//        pcapHandle.loop(0, (PacketListener) p -> {
//            byte[] rawData = p.getRawData();
//            byte[] data = new byte[rawData.length-32];
//            System.arraycopy(rawData, 32, data, 0, data.length);
//            String stringData = data.toString();
//            try {
//                agentInfo = mapper.readValue(stringData, new TypeReference<AgentInfo>() {});
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//            if (!agentInfo.getAgentName().equals(nameAgent)){
//                agents.add(agentInfo.getAgentName());
//            }
//        });
//    }

}
