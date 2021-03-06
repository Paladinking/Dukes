import java.awt.*;
import java.util.ArrayList;

public class Tile {
    Tile() {
        entities = new ArrayList<>();
        towers = new ArrayList<>();
        color = Color.WHITE;
    }

    Color color;
    private ArrayList<Entity> entities;
    private ArrayList<Tower> towers;

    boolean open() {
        for (Entity e : entities) if (e instanceof Solid) return false;
        return true;
    }

    void add(Entity e) {
        entities.add(e);
    }

    void remove(Entity e) {
        entities.remove(e);
    }
    void addTower(Tower t){
        towers.add(t);
        color = Color.red;
    }


    public boolean isEmty() {
        return entities.size() == 0;
    }

    static Tile getTile(Tile[][] tiles, int x, int y) {
        try {
            return tiles[x][y];
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return new BlockTile();
        }


    }

    public void influence(Player player) {
        for (Entity e : entities) {
            if (!(e instanceof Player)) e.influence(player);
        }
    }

    public ArrayList<Entity> getEntities() {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).destroy) {
                entities.remove(entities.get(i));
                i--;
            }
        }
        return entities;
    }

    ArrayList<Tower> getTowers() {
       return towers;
    }

    ArrayList<Tower> getTowers(int num){
        ArrayList<Tower> t = new ArrayList<>();
        for (Tower tower: towers) if (tower.num == num) t.add(tower);
        return t;
    }

    void removeTower(Tower e) {
        towers.remove(e);
    }
}
