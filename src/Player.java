import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends MovingEntity {
    private int ox, oy;
    final int num;
    private final Color color;
    private boolean active,tower,towerPut;
    private static final BufferedImage[] images = new BufferedImage[8];
    private ArrayList<MovingEntity> followers;
    private ArrayList<Peasant> peasants;


    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/players.png")).getSubimage(0, 0, 64, 128);
            images[1] = ImageIO.read(Game.class.getResource("assets/players.png")).getSubimage(64, 0, 64, 128);
            images[2] = ImageIO.read(Game.class.getResource("assets/players.png")).getSubimage(128, 0, 64, 128);
            images[3] = ImageIO.read(Game.class.getResource("assets/players.png")).getSubimage(192, 0, 64, 128);
            images[4] = ImageIO.read(Game.class.getResource("assets/playerTools.png")).getSubimage(0, 0, 64, 64);
            images[5] = ImageIO.read(Game.class.getResource("assets/playerTools.png")).getSubimage(64, 0, 64, 64);
            images[6] = ImageIO.read(Game.class.getResource("assets/playerTools.png")).getSubimage(128, 0, 64, 64);
            images[7] = ImageIO.read(Game.class.getResource("assets/playerTools.png")).getSubimage(192, 0, 64, 64);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(ox - image.getWidth() / 4, oy - image.getHeight() / 4);
        g2d.drawImage(image, 0, 0, image.getWidth() / 2, image.getHeight() / 2, null);
        if (!active) g2d.rotate(Math.PI / 8);
        g2d.drawImage(heldItem, 32, 0, heldItem.getWidth() / 2, heldItem.getHeight() / 2, null);
        if (!active) g2d.rotate(-Math.PI / 8);
        g2d.translate(-ox + image.getWidth() / 4, -oy + image.getHeight() / 4);
    }

    @Override
    public void influence(Player player) {

    }


    Player(int x, int y, int numb, Game game) {
        super(x, y, images[numb], images[4 + numb],game);
        followers = new ArrayList<>();
        peasants = new ArrayList<>();
        this.ox = x;
        this.oy = y;
        this.num = numb;
        switch (numb) {
            case 0:
                color = Color.red;
                break;
            case 1:
                color = Color.green;
                break;
            case 2:
                color = Color.orange;
                break;
            default:
                color = Color.BLUE;


        }
    }

    void add(MovingEntity e) {
        if (!followers.contains(e)) followers.add(e);
        else return;
        if (e instanceof Peasant) peasants.add((Peasant)e);
    }

    @Override
    void move(Tile[][] tiles) {
        //x += dx;
        //y += dy;
    }


    public void update(Object[] data) {
        x = (int) data[0];
        y = (int) data[1];
        boolean a = (boolean) data[2];
        boolean ePressed = (boolean) data[3];
        if (a != active && active) {
            for (MovingEntity e : followers) {
                e.destination = new Point(e.x,e.y);
            }
            followers = new ArrayList<>();
            peasants = new ArrayList<>();
        }
        active = a;
        if (!ePressed) tower = towerPut= false;
        else tower = !towerPut;
    }

    @Override
    public void tick(Tile[][] tiles) {
        tiles[ox / 64][oy / 64].remove(this);
        tiles[x / 64][y / 64].add(this);
        ox = x;
        oy = y;
        int tx = x / 64;
        int ty = y / 64;
        if (active) {
            for (int i = tx - 1; i <= tx + 1; i++) {
                for (int j = ty - 1; j <= ty + 1; j++) {
                    Tile.getTile(tiles, i, j).influence(this);
                }
            }
            for (MovingEntity e : followers) {
                e.destination = new Point(x, y);
            }
        }
        move(tiles);
        if (tower&&peasants.size()>=3){
            for (int i = 0; i < 3; i++) {
                Peasant p = peasants.get(0);
                followers.remove(p);
                peasants.remove(0);
                p.destroy = true;
            }
            game.createTower(x,y,num);
        }
    }
}
