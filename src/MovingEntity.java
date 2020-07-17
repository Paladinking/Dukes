import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class MovingEntity extends Entity implements Tickable{
    Point destination;
    final BufferedImage heldItem;
    MovingEntity(int x, int y, BufferedImage image,BufferedImage heldItem){
        super(image);
        this.heldItem = heldItem;
        this.x = x;
        this.y = y;
        destination = new Point(x,y);
    }

    abstract void move(Tile[][] tiles);

    void setDestination(int x,int y){
        destination.x = x;
        destination.y = y;
    }

    public void draw(Graphics g){
       super.draw(g);
       g.drawImage(heldItem,x+32,y,null);
    }

    public void destroy(){

    }

}
