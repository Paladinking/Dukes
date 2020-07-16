import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Tree extends Solid {

    static int count;

    private static final BufferedImage[] images = new BufferedImage[1];

    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/tree.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    Tree(int x,int y ) {
        super(images[0]);
        count++;
        this.x = x*64;
        this.y = y*64;
    }

    @Override
    public void destroy() {
        count--;
    }

    @Override
    public void influence(Player player) {

    }
}
