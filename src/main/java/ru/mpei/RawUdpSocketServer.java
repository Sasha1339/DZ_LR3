package ru.mpei;

import lombok.SneakyThrows;
import org.pcap4j.core.*;

import java.util.Arrays;
import java.util.List;

public class RawUdpSocketServer {


    @SneakyThrows
    public void start(int port){
/**
 * Pcap - это универсальный и большой инструмент, который позваляет делать много чего
 * у него есть много разных статических методов, если вспомнить Wireshark - прежде чем слушать, мы выбираем где мы будем слушать
 * так и с Pcaps, нужно выбрать где мы будем слушать
 * мы хотим слушать на локалхосте - делая метод findAllDevs
 */
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();

        /**
         * снизу так мы можем вывести все интерфесы,
         * нам нужен конкретный
         */
//        for(PcapNetworkInterface pni: allDevs){
//            System.out.println(pni);
//        }
        /**
         * для того чтобы найти конкретный интерфейс
         */
        /**
         * наш интерфейс - это определенный класс PcapNetworkInterface
         */
        PcapNetworkInterface interfacePcap = allDevs.stream()
                .filter(a -> a.getName().equals("\\Device\\NPF_Loopback")).findFirst().orElseThrow();
        System.out.println(interfacePcap);

        /**
         * здесь хорошо бы реализовать случай если интерфейс найдем не был
         */
        //TODO: if interfacePcap was no found

        /**
         * далее рассмторим какие методы вообще есть, одним из методов openLive() - открыть прием пакетов
         */
        /**
         * параметры которые подаем - рекомендуемые параметры 65536, PROMISCUOUS
         * ВАЖНО! тайм оут - как часто будут выдаваться пакеты, если сдеалть -1 - то на каждый пакет который приходит будет
         * вызываться функция, которая эти пакеты будет выдавать
         * Евгений Игоревич предложил сделать около 50 мс, иначе при -1 система будет сильно нагружаться.
         * 50 мс накапливает и потом выдаем
         */

        PcapHandle pcapHandle = interfacePcap.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        /**
         * теперь что касается фильтра
         * мы фильтруем по протоколу, который нам нужен UDP и по порту
         * опять же все остальное заполняется исходя из рекомендаций
         */

        pcapHandle.setFilter("ip proto \\udp && dst port "+port, BpfProgram.BpfCompileMode.NONOPTIMIZE);

        /**
         *  теперь обработка пакетов, каждые получается 50 мс он накапливает пакеты и пихает их ниже
         */


        new Thread(() -> {
            try {
                pcapHandle.loop(0, (PacketListener) p -> {
                    /**
                     * берем наш пакет берем у него rawData() то бишь получение данных из пакета
                     * и пихаем их в байты (поскольку получаем именно байты)
                     */
                    byte[] rawData = p.getRawData();
                    /**
                     * внимание сюда придет и заголовки как для UDP так и для IPv4
                     */
//                    Path path = Paths.get("text.txt");
//                    try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
//                    {
//                        bw.write(new String(rawData));
//                        System.out.println("Successfully written data to the file");
//                    }
//                    catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    byte[] data = new byte[rawData.length-32];
//                    System.arraycopy(rawData, 32, data, 0, data.length);
                    System.out.println(Arrays.toString(rawData));
                });

            } catch (PcapNativeException | InterruptedException | NotOpenException e) {
                throw new RuntimeException(e);
            }
        }).start();



    }

}
