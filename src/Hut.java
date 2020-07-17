import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Hut extends Entity {
    static int count;
    final byte rand;
    static final private BufferedImage[] images =new BufferedImage[1];
    public int influence;
    public int playernum;

    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/hut.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    Hut(int x,int y, byte rand) {
        super(images[0]);
        count++;
        this.x = x*64;
        this.y = y*64;
        this.playernum = -1;
        this.influence = 0;
        this.rand = rand;
    }
    @Override
    public void draw(Graphics g){
        g.setColor(new Color(255-(int)(((double)influence/60)*255),255-(int)(((double)influence/60)*255),255-(int)(((double)influence/60)*255)));
        g.fillRect(x,y,64,64);
        g.drawImage(image,x,y,image.getWidth()/2,image.getHeight()/2,null);
    }

    @Override
    public void destroy() {
        count--;
    }

    @Override
    public void influence(Player player) {
        if (playernum!=player.num){
            playernum = player.num;
            influence=0;
        }
        influence++;
        if (influence>=60){
            destroy = true;
        }
    }
}
