package ru.mpei;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        PacketDecoder packetDecoder = new PacketDecoder();
            byte[] packetCode = packetDecoder.code("hello2");
            System.out.println(Arrays.toString(packetCode));

        AgentInfo agentInfo;
        try {
            ObjectMapper mapper = new ObjectMapper();
            agentInfo = mapper.readValue("{\"agentName\":" + "\"aid.getName()\"" + ", \"guid\":" + true + "}", new TypeReference<AgentInfo>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(agentInfo.getAgentName() + agentInfo.isGuid());

        }



}
