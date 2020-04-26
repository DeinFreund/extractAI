/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zkai;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 *
 * @author user
 */
public class Client {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf = new byte[1024];
    zkai zkai;

    public Client(zkai zkai) throws Exception {
        socket = new DatagramSocket();
        this.zkai = zkai;
        address = InetAddress.getByName("localhost");
    }

    public String send(float[][][][] msg) throws Exception {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream);
        oo.writeObject(msg);
        oo.close();

        byte[] obj = bStream.toByteArray();
        //zkai.debug("Sending " + obj.length + " bytes.");
        
        DatagramPacket packet = new DatagramPacket(obj, obj.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(100);
        socket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
        //return "1,0";
    }

    public void close() {
        socket.close();
    }
}
