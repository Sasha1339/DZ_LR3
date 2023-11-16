package ru.mpei;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        PacketDecoder packetDecoder = new PacketDecoder();
            byte[] packetCode = packetDecoder.code("hello2");
            System.out.println(Arrays.toString(packetCode));
        }

    }
