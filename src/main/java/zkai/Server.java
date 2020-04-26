package zkai;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[65536];

    public Server() throws Exception {
        socket = new DatagramSocket(4445);
    }

    public void run() {
        running = true;
        try {
            MultiLayerNetwork model = KerasModelImport.importKerasSequentialModelAndWeights("model.h5");
            while (running) {
                try {
                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    long time = System.currentTimeMillis();

                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    byte[] out = new byte[packet.getLength()];
                    byte[] data = packet.getData();
                    for (int i = packet.getOffset(); i < packet.getOffset() + packet.getLength(); i++) {
                        out[i - packet.getOffset()] = data[i];
                    }
                    ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(out));
                    INDArray inputData = Nd4j.create((float[][][][]) iStream.readObject());
                    iStream.close();
                    INDArray res = model.output(inputData);
                    //System.out.println(res.toStringFull());
                    float dx = res.getFloat(0,0);
                    float dy = res.getFloat(0,1);
                    String msg = dx + "," + dy;
                    System.out.println("Sending " + msg + " took " + (System.currentTimeMillis() - time) + "ms.");
                    byte[] ret = msg.getBytes();
                    DatagramPacket packet2 = new DatagramPacket(ret, ret.length, address, port);
                    socket.send(packet2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        socket.close();
    }
}
