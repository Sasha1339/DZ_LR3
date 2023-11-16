package ru.mpei;

import lombok.SneakyThrows;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.List;

public class RawUdpSocketClient {

        private PcapHandle pcapHandle;

        @SneakyThrows
        public void send(byte[] data){
                pcapHandle.sendPacket(data);
        }


        @SneakyThrows
        public void init(int port){
                List<PcapNetworkInterface> allDevs = null;
                allDevs = Pcaps.findAllDevs();
                PcapNetworkInterface interfacePcap = null;
                for (PcapNetworkInterface allDev: allDevs){
                        if (allDev.getName().equals("\\Device\\NPF_Loopback")){
                                interfacePcap = allDev;
                                break;
                        }
                }
                pcapHandle = interfacePcap.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);


        }

}
