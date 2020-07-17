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
            images[0] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(0,0,64,128);
            images[1] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(64,0,64,128);
            images[2] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(128,0,64,128);
            images[3] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(192,0,64,128);
            images[4] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(256,0,64,128);
            images[5] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(320,0,64,128);
            images[6] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(384,0,64,128);
            images[7] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(448,0,64,128);
            images[8] = ImageIO.read(Game.class.getResource("assets/peasant.png")).getSubimage(512,0,64,128);
            held[0] = ImageIO.read(Game.class.getResource("assets/spear.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private final byte rand;

    Peasant(int x, int y, byte rand,int playernum) {
        super(x, y, images[(int)(Math.random()*9)], held[0]);
        this.rand = rand;
        this.playernum = playernum;
        if (playernum!=-1) influence = 1000;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image,x,y,image.getWidth()/4,image.getHeight()/4,null);
        g.drawImage(heldItem,x+16,y,image.getWidth()/4,image.getHeight()/4,null);
    }
    @Override
    void move(Tile[][] tiles) {
        int dx = 0,dy =0;
       if (x-destination.x>=4)  dx-=4;
       if (x-destination.x<=-4)  dx+=4;
       if (y-destination.y>=4)  dy-=4;
       if (y-destination.y<=-4)  dy+=4;
       if (Tile.getTile(tiles,(x+dx)/64,(y+dy)/64).open()) {
           x+=dx;
           y+=dy;
       }

    }

    @Override
    public void influence(Player player) {
        if (playernum!=player.num){
            playernum = player.num;
            influence = 0;
        } else {
            if (influence<1000) influence+=100;
        }
        if (influence>=1000) destination = new Point(player.x,player.y);
        influenced = true;
    }

    @Override
    public void tick(Tile[][] tiles) {
        move(tiles);
        if (influence>0) influence--;
        else playernum =-1;
    }
}
