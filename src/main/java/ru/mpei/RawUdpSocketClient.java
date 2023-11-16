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


        public void init(int port){

                List<PcapNetworkInterface> allDevs = null;
                try {
                        allDevs = Pcaps.findAllDevs();
                } catch (PcapNativeException e) {
                        throw new RuntimeException(e);
                }
                PcapNetworkInterface interfacePcap = null;
                for (PcapNetworkInterface allDev: allDevs){
                        if (allDev.getName().equals("\\Device\\NPF_Loopback")){
                                interfacePcap = allDev;
                                break;
                        }
                }
                try {
                        pcapHandle = interfacePcap.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
                } catch (PcapNativeException e) {
                        throw new RuntimeException(e);
                }


        }


//    @SneakyThrows
//    public void start(int port){
//
//        PcapNetworkInterface nif = Pcaps.getDevByAddress(InetAddress.getLoopbackAddress());
//
//        PcapHandle pcapHandle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
//
//
//
//        new Thread(() -> {
//
//                    File file = new File(
//                            "C:\\Users\\serge\\OneDrive\\Рабочий стол\\1 курс магистратура, Э-13м-23\\Мультиагентные системы\\лекции\\untitled1\\text.txt");
//
//                    // Note:  Double backquote is to avoid compiler
//                    // interpret words
//                    // like \test as \t (ie. as a escape sequence)
//
//                    // Creating an object of BufferedReader class
//            BufferedReader br
//                    = null;
//            try {
//                br = new BufferedReader(new FileReader(file));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Declaring a string variable
//                    String st;
//                    // Condition holds true till
//                    // there is character in a string
//
//                        // Print the strin
//            try {
//                String rer = br.readLine();
//                pcapHandle.sendPacket(rer.getBytes());
//            } catch (NotOpenException e) {
//                throw new RuntimeException(e);
//            } catch (PcapNativeException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println("dvsavdvs");
//
//
//        }).start();
//
//
//
//    }


}
