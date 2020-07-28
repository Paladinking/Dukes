import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Tower extends Entity{
    final int num;


    static final private BufferedImage[] images =new BufferedImage[1];

    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/tower.png")).getSubimage(0,0,64,128);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Tower(int x,int y, int num,Game game){
        super(images[0], game);
        this.x = x;
        this.y = y;
        this.num = num;
    }
    @Override
    public void draw(Graphics g) {
        g.drawImage(image,x-image.getWidth()/2,y-96,null);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void influence(Player player) {

    }

}
