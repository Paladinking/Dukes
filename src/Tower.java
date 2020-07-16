import java.awt.*;

public class Tower extends Solid{
    Tower(int x,int y){
        super(null);
        this.x = x;
        this.y = y;
    }
    @Override
    public void draw(Graphics g) {
        g.fillRect(x,y-30,20,100);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void influence(Player player) {

    }

}
