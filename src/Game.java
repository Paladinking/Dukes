import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Game implements KeyListener {
    private final int width, height;
    private int added;
    private ArrayList<Tickable> tickList;
    private ArrayList<Entity> entities;
    private ArrayList<Object[]> updates;
    private ArrayList<Player> players;
    private byte[] keys;
    private byte team;
    private Tile[][] tiles;
    private long curTime = System.currentTimeMillis();
    boolean received;

    private Game(int width, int height) {
        this.width = width;
        this.height = height;
        this.tickList = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.updates = new ArrayList<>();
        this.players = new ArrayList<>();
        tiles = new Tile[30][17];
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 17; j++) {
                tiles[i][j] = new Tile();
            }
        }
        keys = new byte[7];
        createEntity(new Rock(10, 10));
        createEntity(new Rock(11, 10));
        createEntity(new Rock(12, 10));
        createEntity(new Rock(13, 10));
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Game game = new Game(1920, 1080);
            JFrame f = new JFrame();
            JPanel p = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    game.draw(g);
                }
            };
            p.setFocusable(true);
            p.addKeyListener(game);
            p.setPreferredSize(new Dimension(1920, 1080));
            f.setUndecorated(true);
            f.add(p);
            f.pack();
            f.setResizable(false);
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setExtendedState(Frame.MAXIMIZED_BOTH);
            f.setVisible(true);
            p.requestFocusInWindow();
            Timer t = new Timer("Loop");
            for (int i = 1; i <= Server.PLAYERS; i++) {

                game.createEntity(new Player(100 * i, 100 * i, i - 1));
            }
            try {
                DatagramSocket socket = game.connect();
                new Thread(() -> game.loop(p, socket)).start();
            } catch (IOException e) {

                e.printStackTrace();
            }


        });
    }

    private DatagramSocket connect() throws IOException {
        byte[] b = new byte[1];
        DatagramPacket packet = new DatagramPacket(b, b.length, InetAddress.getByName("localhost"), 6066);
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        DatagramPacket recp = new DatagramPacket(new byte[2], 2);
        socket.receive(recp);
        byte[] b2 = recp.getData();
        team = b2[0];
        int map = b2[1];
        int[][] tmap = Maps.getMap(map);
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 17; j++) {
                if (tmap[i][j] == 1) createEntity(new Rock(i, j));
                if (tmap[i][j] == 2) createEntity(new Tree(i, j));
            }
        }
        keys[6] = team;
        new Thread(() -> {
            while (true) {
                try {
                    receive(socket);
                    added++;
                    received = true;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        System.out.println("Your team is; " + team);

        return socket;
    }

    private void receive(DatagramSocket socket) throws IOException, ClassNotFoundException {
        DatagramPacket packet = new DatagramPacket(new byte[512], 512);
        socket.receive(packet);
        byte[] bytes = packet.getData();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        updates = (ArrayList<Object[]>) ois.readObject();
    }

    private void loop(JPanel p, DatagramSocket socket) {
        while (true) {
            tick();
            p.repaint();
            byte[] keys2 = keys;
            int dx = 0, dy = 0;
            if (keys[0] == 1) dy -= 4;
            if (keys[1] == 1) dx -= 4;
            if (keys[2] == 1) dy += 4;
            if (keys[3] == 1) dx += 4;
            if (!tiles[(players.get(team).x + dx) / 64][(players.get(team).y + dy) / 64].open()) {
                keys2[0] = 0;
                keys2[1] = 0;
                keys2[2] = 0;
                keys2[3] = 0;
            }
            try {
                DatagramPacket packet = new DatagramPacket(keys2, keys2.length, InetAddress.getByName("localhost"), 6066);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }

    private void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        for (Entity e : entities) e.draw(g);
        for (Player p : players) p.draw(g);
    }

    private void tick() {
        for (Tickable t : tickList) t.tick(tiles);
        for (int i=0;i<entities.size();i++){
            Entity e = entities.get(i);
            if (e.destroy) {
                tickList.remove(e);
                entities.remove(e);
                i--;
                e.destroy();
                if (e instanceof Hut){
                    createEntity(new Peasant(e.x,e.y));
                }
            }

        }
        updateAll();
    }

    private void updateAll() {
        for (int i = 0; i < updates.size() - 1; i++) {
            if (updates.get(i) != null) players.get(i).update(updates.get(i));
        }
        Object[] update = updates.get(updates.size() - 1);
        if (update == null) return;
        int i = (int) update[0];
        int x = (int) update[1];
        int y = (int) update[2];
        switch (i) {
            case 1:
                if (tiles[x][y].isEmty() && Tree.count < 100) createEntity(new Tree(x, y));
                break;
            case 2:
                if (tiles[x][y].isEmty() && Hut.count < 100) createEntity(new Hut(x, y));
        }
    }

    private Entity createEntity(Entity e) {
        if (e instanceof Player) {
            players.add((Player) e);
            updates.add(null);
        } else {
            entities.add(e);
        }
        if (e instanceof Tickable) tickList.add((Tickable) e);
        tiles[e.x / 64][e.y / 64].add(e);
        return e;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[0] = 1;
                break;
            case KeyEvent.VK_A:
                keys[1] = 1;
                break;
            case KeyEvent.VK_S:
                keys[2] = 1;
                break;
            case KeyEvent.VK_D:
                keys[3] = 1;
                break;
            case KeyEvent.VK_SPACE:
                keys[4] = 1;
                break;
            case KeyEvent.VK_E:
                keys[5] = 1;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                keys[0] = 0;
                break;
            case KeyEvent.VK_A:
                keys[1] = 0;
                break;
            case KeyEvent.VK_S:
                keys[2] = 0;
                break;
            case KeyEvent.VK_D:
                keys[3] = 0;
                break;
            case KeyEvent.VK_SPACE:
                keys[4] = 0;
                break;
            case KeyEvent.VK_E:
                keys[5] = 0;
                break;
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println(added);
            System.exit(0);
        }

    }
}