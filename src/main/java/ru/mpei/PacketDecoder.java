package ru.mpei;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketDecoder {

    int id = 40035;
    String messageStr = "";
    String nullLoopback = "";
    String headerIPv4 = "";
    String headerUDP = "";
    String addressCode = "";
    String portCode = "";
    int sumForCheckSum = 0;

    public String calculateCheckSum(){
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
        return Integer.toHexString(checkSumInt);
    }

    public String codeCheckSum(){
        String data = calculateCheckSum();
        String[] checkSumArr = new String[]{"", ""};
        for(int i = 0; i < data.length(); i++){
            if (i < 2){
                checkSumArr[0] += Character.toString(data.charAt(i));
            } else {
                checkSumArr[1] += Character.toString(data.charAt(i));
            }
        }

        String checkSumCode = "";
        try {
            for (String hexString: checkSumArr){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                checkSumCode += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return checkSumCode;
    }

    public String codeIntToOneBite(int number, boolean isCalculateInCheckSum){
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

        String timeLiveCod = "";
        try {
            for (String hexString: timeLiveArr){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                timeLiveCod += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return timeLiveCod;
    }

    public String codeIntToTwoBite(int number, boolean isCalculateInCheckSum, int countInCheckSum){
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

        try {
            for (String hexString: portArr){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                result += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String codeKnownCountAndValueBite(String[] data){
        String result = "";
        try {
            for (String hexString: data){
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                result += new String(bytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException | DecoderException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String codeAddress(){
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
        return result;
    }

    public void codeDataAndSumForChekSum(String data){
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


    public String code(String data) {
        codeDataAndSumForChekSum(data);
        this.messageStr = data;
        // формирвоание закодированной строки для адреса
        this.addressCode = codeAddress();
        // формирвоание закодированного порта
        this.portCode = codeIntToTwoBite(1200, true, 1);
        //кодировка Null/Loopback
        this.nullLoopback = codeKnownCountAndValueBite(new String[]{"02", "00", "00", "00"});
        //кодирование протокола IPv4
        this.headerIPv4 += codeKnownCountAndValueBite(new String[]{"45", "00"});

        int commonLen = 20 + 8 + messageStr.length();
        // кодировка идентификатора и общей длины
        String identCode = codeIntToTwoBite(id, false, 0);
        this.id += 1;
        // общей длины
        String comLenCode = codeIntToTwoBite(commonLen, false, 0);
        this.headerIPv4 += comLenCode + identCode;
        // кодировка флагов и смещения фрагментов
        this.headerIPv4 += codeKnownCountAndValueBite(new String[]{"00", "00"});
        // кодировка времени жизни = 64
        String timeLiveCode = codeIntToOneBite(64, false);
        this.headerIPv4 += timeLiveCode;
        // кодировка верхнеуровненго протокола UDP = 17
        String protocolCode = codeIntToOneBite(17, true);
        this.headerIPv4 += protocolCode;
        //формирование чек суммы = 0х0000
        this.headerIPv4 += codeKnownCountAndValueBite(new String[]{"00", "00"});
        //прибавляем к нашему IPv4 адреса источника и назначения
        headerIPv4 += addressCode + addressCode;
        // формирвоание заголовка протокола UDP
        // порт источника
        String portClientCode = codeIntToTwoBite(2682, true, 1);

        int commonLenUdp = 8 + messageStr.length();
        String comLenUdpCode = codeIntToTwoBite(commonLenUdp, true, 2);
        // кодирование чек суммы
        String checkSumCode = codeCheckSum();
        headerUDP += portClientCode + portCode + comLenUdpCode + checkSumCode;
        return this.nullLoopback + headerIPv4 + headerUDP + messageStr;
    }

//

}
