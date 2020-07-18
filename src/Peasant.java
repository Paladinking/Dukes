import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Peasant extends MovingEntity {
    private static final BufferedImage[] images = new BufferedImage[9];
    private static final BufferedImage[] held = new BufferedImage[1];

    private int playernum;
    private int influence;
    private boolean influenced;

    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(0, 0, 64, 128);
            images[1] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(64, 0, 64, 128);
            images[2] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(128, 0, 64, 128);
            images[3] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(192, 0, 64, 128);
            images[4] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(256, 0, 64, 128);
            images[5] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(320, 0, 64, 128);
            images[6] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(384, 0, 64, 128);
            images[7] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(448, 0, 64, 128);
            images[8] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(512, 0, 64, 128);
            held[0] = ImageIO.read(Game.class.getResource("assets/spear.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private final int rand;

    Peasant(int x, int y, int rand, int playernum) {
        super(x, y, images[(int) (Math.random() * 9)], held[0]);
        this.rand = rand;
        this.playernum = playernum;
        if (playernum != -1) influence = 1000;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x-8, y-16, image.getWidth() / 4, image.getHeight() / 4, null);
        g.drawImage(heldItem, x + 16, y, image.getWidth() / 4, image.getHeight() / 4, null);
    }

    @Override
    void move(Tile[][] tiles) {
        if (destination.distance(x,y)<16) return;
        double angle = Math.atan2(destination.y-y,destination.x-x);
        int dx = (int)(4*Math.cos(angle)), dy = (int)(4*Math.sin(angle));
        if (Tile.getTile(tiles, (x + dx) / 64, (y + dy) / 64).open()) {
            boolean canmove = true;
            for (Tile t: getRelevantTiles(tiles,x+dx,y+dy)){
                for (Entity e:t.getEntities()){
                    if (e==this) continue;
                    if (new Point(x+dx,y+dy).distance(e.x,e.y)<16){
                        canmove = false;
                        if (dx>0&&(x-e.x)/dx>0&&Math.abs(dx)>=Math.abs(dy)) canmove = true;
                        else if (dy>0&&(y-e.y)/dy>0&&Math.abs(dx)<=Math.abs(dy)) canmove = true;

                    }
                }
            }
            if (canmove) {
                Tile.getTile(tiles, x / 64, y / 64).remove(this);
                x += dx;
                y += dy;
                Tile.getTile(tiles, x / 64, y / 64).add(this);
            }

        }

    }

    private static Tile[] getRelevantTiles(Tile[][] tiles, int x2,int y2) {
        Tile[] t = new Tile[4];
        int dx=8,dy=8;
        if (x2%64<10) dx= -8;
        if (y2%64<10) dy= -8;
        t[0] = Tile.getTile(tiles,x2/64,y2/64);
        t[1] = Tile.getTile(tiles,(x2+dx)/64,y2/64);
        t[2] = Tile.getTile(tiles,x2/64,(y2+dy)/64);
        t[3] = Tile.getTile(tiles,(x2+dx)/64,(y2+dy)/64);
        return t;
    }

    @Override
    public void influence(Player player) {
        if (playernum != player.num) {
            playernum = player.num;
            influence = 0;
        } else {
            if (influence < 1000) influence += 100;
        }
        if (influence >= 1000) player.add(this);
    }

    @Override
    public void tick(Tile[][] tiles) {
        move(tiles);
        if (influence > 0) influence--;
        else playernum = -1;
    }
}
