package ru.mpei;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketDecoder {

    private int id = 594;
    private String messageStr = "";
    private byte[] nullLoopback;
    private byte[] headerIPv4 = new byte[0];
    private byte[] headerUDP = new byte[0];
    private byte[] addressCode;
    private byte[] portCode;
    private int sumForCheckSum = 0;

    private String calculateCheckSum(){
        String checkHex = Integer.toHexString(sumForCheckSum);
        int newSum = 0;
        if (checkHex.length() > 4){
            newSum = Integer.parseInt(Character.toString(checkHex.charAt(0)), 16) +
                    Integer.parseInt(Character.toString(checkHex.charAt(1))
                            + checkHex.charAt(2)
                            + checkHex.charAt(3)
                            + checkHex.charAt(4), 16);
        }
        String invertCheckSumStr = Integer.toBinaryString(newSum);
        String checkSumStr = "";
        for (int i = 0; i < invertCheckSumStr.length(); i++){
            if (Character.toString(invertCheckSumStr.charAt(i)).equals("1")) checkSumStr += "0";
            else checkSumStr += "1";
        }
        int checkSumInt = Integer.parseInt(checkSumStr, 2);
        if (Integer.toHexString(checkSumInt).length() < 3){
            return "00"+Integer.toHexString(checkSumInt);
        } else if (Integer.toHexString(checkSumInt).length() < 4){
            return "0"+Integer.toHexString(checkSumInt);
        }
        return Integer.toHexString(checkSumInt);
    }

    private byte[] codeCheckSum(){
        String data = calculateCheckSum();
        String[] checkSumArr = new String[]{"", ""};
        for(int i = 0; i < data.length(); i++){
            if (i < 2){
                checkSumArr[0] += Character.toString(data.charAt(i));
            } else {
                checkSumArr[1] += Character.toString(data.charAt(i));
            }
        }

        byte[] checkSumCode = new byte[2];
        try {
            for (int i = 0; i < checkSumCode.length; i++){
                checkSumCode[i] = Hex.decodeHex(checkSumArr[i].toCharArray())[0];
            }
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        return checkSumCode;
    }

    private byte[] codeIntToOneBite(int number, boolean isCalculateInCheckSum){
        String timeLive = Integer.toHexString(number);
        if (timeLive.length() % 2 != 0){
            timeLive = "0"+timeLive;
        }
        String[] timeLiveArr = new String[]{""};
        for(int i = 0; i < timeLive.length(); i++){
            if (i < 2){
                timeLiveArr[0] += Character.toString(timeLive.charAt(i));
            } else {
                timeLiveArr[1] += Character.toString(timeLive.charAt(i));
            }
        }

        if (isCalculateInCheckSum){
            String sumPortDes = new String(timeLiveArr[0]);
            sumForCheckSum += Integer.parseInt(sumPortDes, 16);
        }

        byte[] timeLiveCod = new byte[1];
        try {
            for (String hexString: timeLiveArr){
                timeLiveCod = Hex.decodeHex(hexString.toCharArray());
            }
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        return timeLiveCod;
    }

    private byte[] codeIntToTwoBite(int number, boolean isCalculateInCheckSum, int countInCheckSum){
        String port = Integer.toHexString(number);
        if (port.length() < 3){
            port = "00"+port;
        }
        if (port.length() % 2 != 0){
            port = "0"+port;
        }
        String[] portArr = new String[]{"", ""};
        for(int i = 0; i < port.length(); i++){
            if (i < 2){
                portArr[0] += Character.toString(port.charAt(i));
            } else {
                portArr[1] += Character.toString(port.charAt(i));
            }
        }
        String result = "";
        if (isCalculateInCheckSum){
            String sumPortDes = new String(portArr[0]+portArr[1]);
            sumForCheckSum += countInCheckSum * Integer.parseInt(sumPortDes, 16);
        }
        byte[] resultByte = new byte[2];
        try {
            for (int i = 0; i < resultByte.length; i++){
                resultByte[i] = Hex.decodeHex(portArr[i].toCharArray())[0];
            }
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        return resultByte;
    }

    private byte[] codeKnownCountAndValueBite(String[] data){
        String result = "";
        try {
            for (String hexString: data){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                result += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return result.getBytes();
    }

    private byte[] codeAddress(){
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String[] addressArr = address.split("\\.");
        int[] addressInt = new int[4];
        for(int i = 0; i < addressInt.length; i++){
            addressInt[i] = Integer.parseInt(addressArr[i]);
            addressArr[i] = Integer.toHexString(addressInt[i]);
            if (addressArr[i].length() % 2 != 0){
                addressArr[i] = "0"+addressArr[i];
            }
        }

        String[] sumAddr = new String[]{addressArr[0]+addressArr[1], addressArr[2]+addressArr[3]};


        int[] addressIntArr = new int[2];
        addressIntArr[0] = Integer.parseInt(sumAddr[0], 16);
        addressIntArr[1] = Integer.parseInt(sumAddr[1], 16);
        sumForCheckSum += 2 * (addressIntArr[0]+addressIntArr[1]);
        String result = "";
        try {
            for (String hexString: addressArr){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                result += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return result.getBytes();
    }

    private void codeDataAndSumForChekSum(String data){
        String[] messageArr = new String[data.length()];
        char[] messageChar = data.toCharArray();
        for (int i = 0; i < data.length(); i++){
            messageArr[i] = Integer.toHexString(messageChar[i]);
            if (messageArr[i].length() % 2 != 0){
                messageArr[i] = "0"+messageArr[i];
            }
        }
        String[] messageSum = new String[messageArr.length/2+messageArr.length%2];
        for (int i = 0; i < messageSum.length; i++){
            try{
                messageSum[i] = messageArr[i*2]+messageArr[i*2+1];
            } catch (IndexOutOfBoundsException e){
                messageSum[i] = messageArr[i*2]+"00";
            }
        }
        for(int i = 0; i < messageSum.length; i++){
            sumForCheckSum += Integer.parseInt(messageSum[i], 16);
        }
    }

    private byte[] sumTwoArray(byte[] array1, byte[] array2){
        byte[] array3 = new byte[array1.length+array2.length];
        System.arraycopy(array1, 0, array3, 0, array1.length);
        System.arraycopy(array2, 0, array3, array1.length, array2.length);
        return array3;
    }


    public byte[] code(String data) {
        codeDataAndSumForChekSum(data);
        this.messageStr = data;
        // формирвоание закодированной строки для адреса
        this.addressCode = codeAddress();
        // формирвоание закодированного порта
        this.portCode = codeIntToTwoBite(1200, true, 1);
        //кодировка Null/Loopback
        this.nullLoopback = codeKnownCountAndValueBite(new String[]{"02", "00", "00", "00"});
        //кодирование протокола IPv4
        byte[] startHeader = codeKnownCountAndValueBite(new String[]{"45", "00"});
        int commonLen = 20 + 8 + messageStr.length();
        // кодировка идентификатора и общей длины
        byte[] identCode = codeIntToTwoBite(id, false, 0);
        //this.id += 1;
        // общей длины
        byte[] comLenCode = codeIntToTwoBite(commonLen, false, 0);

        // кодировка флагов и смещения фрагментов
        byte[] flagAndOffset = codeKnownCountAndValueBite(new String[]{"00", "00"});
        // кодировка времени жизни = 64
        byte[] timeLiveCode = codeIntToOneBite(64, false);
        //this.headerIPv4 += timeLiveCode;
        // кодировка верхнеуровненго протокола UDP = 17
        byte[] protocolCode = codeIntToOneBite(17, true);
        //Ормирование чек суммы для заголовка IPv4
        byte[] checkIp = codeKnownCountAndValueBite(new String[]{"00", "00"});

        this.headerIPv4 = sumTwoArray(this.headerIPv4, startHeader);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, comLenCode);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, identCode);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, flagAndOffset);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, timeLiveCode);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, protocolCode);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, checkIp);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, addressCode);
        this.headerIPv4 = sumTwoArray(this.headerIPv4, addressCode);


        // формирвоание заголовка протокола UDP
        // порт источника
        byte[] portClientCode = codeIntToTwoBite(38689, true, 1);

        int commonLenUdp = 8 + messageStr.length();
        byte[] comLenUdpCode = codeIntToTwoBite(commonLenUdp, true, 2);
        // кодирование чек суммы
        byte[] checkSumCode = codeCheckSum();

        this.headerUDP = sumTwoArray(headerUDP, portClientCode);
        this.headerUDP = sumTwoArray(headerUDP, portCode);
        this.headerUDP = sumTwoArray(headerUDP, comLenUdpCode);
        this.headerUDP = sumTwoArray(headerUDP, checkSumCode);
        this.sumForCheckSum = 0;
        byte[] result = sumTwoArray(nullLoopback, sumTwoArray(headerIPv4, sumTwoArray(headerUDP, messageStr.getBytes())));
        this.headerIPv4 = new byte[0];
        this.headerUDP = new byte[0];
        return result;
    }

//

}
