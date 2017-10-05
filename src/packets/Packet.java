/*
 * CLIENT
 */
package packets;

import packets.ping.PingResponsePacket;
import packets.ping.PingPacket;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * CLIENT
 * @author Gabriel.Maxfield
 */
public class Packet {

    public final int type;
    public final char[] data;

    public Packet(int type, char[] data) {
        this.type = type;
        this.data = data;
    }
    
    @Override //CLIENT
    public String toString() {
        String out = "Packet{";
        switch (type) {
            case 0:
                out += "Empty: "; 
                break;
            case 1:
                out += "Ping: ";
                break;
            case 2:
                out += "PingResponse: ";
                break;
            default:
                out += "Other: ";
        }
        
        return out + Arrays.toString(data) + "}";
    }

    public static Packet parse(char[] data) {
        switch ((data[0]<<8)|data[1]) {
            case 1:
                return new PingPacket();
            case 2:
                return new PingResponsePacket(data);
        }
        //Not Good
        return new EmptyPacket();
    }

    public static Packet parse(BufferedReader input) {
        int type = -1;
        char data[] = null;
        try {
            type = input.read();//Reads a char (upper byte of char)
            type = (type<<8) | input.read();
            //Length of data
            int dataLen = input.read();
            dataLen = (dataLen<<8) | input.read();
            //Read data
            System.out.println("Type: "+(int)type+" Length: "+(int)dataLen);
            data = new char[dataLen];
            System.out.println("Read chars:"+input.read(data, 0, dataLen));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        switch (type) {
            case 1:
                System.out.println("Recieved Ping Packet");
                return new PingPacket();
            case 2:
                return new PingResponsePacket(data);
        }
        //Not Good
        return new EmptyPacket();
    }

    public static char[] serialize(Packet packet) {
        //int-type,int length
        char out[] = new char[packet.data.length + 4];
        //Ugly: grab higher byte from int
        out[0] = (char) ((packet.type >> 8) & 0xFF);
        //Get lower byte of int
        out[1] = (char) (packet.type & 0xFF);

        out[2] = (char) ((packet.data.length >> 8) & 0xFF);
        out[3] = (char) (packet.data.length & 0xFF);
        for (int i = 0; i < packet.data.length; i++) {
            out[i + 4] = packet.data[i];
        }
        System.out.println("Serialized Packet: "+(int)out[0]+(int)out[1]+(int)out[2]+(int)out[3]);
        return out;
    }
}