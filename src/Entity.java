import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public abstract class Entity implements Serializable {
    final BufferedImage image;
    final Game game;
    int x,y;
    boolean destroy;

    Entity(BufferedImage image, Game game) {
        this.image = image;
        this.game = game;
        destroy =false;
    }

    public void draw(Graphics g){
        g.drawImage(image,x,y,null);
    }

    public abstract void destroy();

    public abstract void influence(Player player);

}
