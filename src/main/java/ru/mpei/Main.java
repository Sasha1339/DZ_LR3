package ru.mpei;

public class Main {
    public static void main(String[] args) {

        PacketDecoder packetDecoder = new PacketDecoder();
            String packetCode = packetDecoder.code("hello2");
            System.out.println(packetCode);
        }

    }
