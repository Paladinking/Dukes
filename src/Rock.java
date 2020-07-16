import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Rock extends Solid {
    static int count = 0;
    private final static BufferedImage[] images = new BufferedImage[4];

    static {
        try {
            images[0] = ImageIO.read(Game.class.getResource("assets/rocks.png")).getSubimage(0, 0, 64, 64);
            images[1] = ImageIO.read(Game.class.getResource("assets/rocks.png")).getSubimage(64, 0, 64, 64);
            images[2] = ImageIO.read(Game.class.getResource("assets/rocks.png")).getSubimage(128, 0, 64, 64);
            images[3] = ImageIO.read(Game.class.getResource("assets/rocks.png")).getSubimage(192, 0, 64, 64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    Rock(int x, int y) {
        super(images[(int)(Math.random()*4)]);
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
