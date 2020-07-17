
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;

public class Server {


    public static final int PLAYERS = 2;
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(6066);
        ArrayList<ServerPlayer> players = new ArrayList<>();
        byte map = (byte) (Math.random()*4);
        while (players.size() < PLAYERS) {
            DatagramPacket packet = new DatagramPacket(new byte[1], 1);
            socket.receive(packet);
            System.out.println(packet.getAddress() + ":" + packet.getPort());
            players.add(new ServerPlayer(packet));

        }
        for (ServerPlayer p : players)
            socket.send(new DatagramPacket(new byte[]{(byte) (p.num),map}, 2, p.getAddress(), p.getPort()));
        Timer t = new Timer("Server");
        ArrayList<Object[]> data = new ArrayList<>();

        for (int i = 0; i < 4; i++) data.add(null);
        data.add(null);
        new Thread(() -> {
            int ticks = 0;
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[7], 7);
                    socket.receive(packet);
                    byte[] bytes = packet.getData();
                    ServerPlayer p = players.get(bytes[6]);
                    for (int i = 0; i < p.keys.length; i++) {
                        p.keys[i] = bytes[i] != 0;
                    }
                    int dx = 0, dy = 0;
                    if (p.keys[0]) dy -= 4;
                    if (p.keys[1]) dx -= 4;
                    if (p.keys[2]) dy += 4;
                    if (p.keys[3]) dx += 4;
                    if (!p.keys[4]) p.releasedSpace = true;
                    if (p.keys[4]&&p.releasedSpace){
                        p.releasedSpace = false;
                        p.active =!p.active;
                    }
                    ticks++;
                    p.x+=dx;
                    p.y+=dy;
                    data.set(p.num, new Object[]{p.x, p.y,p.active});
                    if (ticks==8) {
                        ticks = 0;
                        int r = (int) (50 * Math.random());
                        if (r >= 48) {
                            int x = (int) (Math.random() * 30);
                            int y = (int) (Math.random() * 16);
                            byte rand = (byte)(Math.random()*Byte.MAX_VALUE*2);
                            data.set(4, new Object[]{r - 47, x, y,rand});
                        } else {
                            data.set(4, new Object[]{0, 0, 0, 0});
                        }
                    }
                    byte[] b;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(data);
                    b = baos.toByteArray();
                    p.setData(b);
                    socket.send(p.packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        System.out.println("done");
    }

    private static class ServerPlayer {
        private static int numb = 0;
        private final int num;
        final DatagramPacket packet;
        final boolean[] keys;
        int x, y;
        boolean active;
        boolean releasedSpace;

        ServerPlayer(DatagramPacket packet) {
            this.packet = packet;
            this.keys = new boolean[6];
            this.num = numb;
            active =false;
            switch (num) {
                case 0:
                    x = 100;
                    y = 100;
                    break;
                case 1:
                    x = 200;
                    y = 200;
                    break;
                case 2:
                    x = 300;
                    y = 300;
                    break;
                case 3:
                    x = 400;
                    y = 400;
                    break;
                default:
                    x = 100;
                    y = 100;
            }
            numb++;
        }

        InetAddress getAddress() {
            return packet.getAddress();
        }

        int getPort() {
            return packet.getPort();
        }

        void setData(byte[] b) {
            packet.setData(b);
        }
    }

}
