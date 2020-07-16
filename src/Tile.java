import java.util.ArrayList;

public class Tile {
    Tile(){
        entities = new ArrayList<>();
        towers = new ArrayList<>();
    }
    private ArrayList<Entity> entities;
    private ArrayList<Tower> towers;

    boolean open(){
        for (Entity e:entities) if (e instanceof Solid) return false;
        return true;
    }
    void add(Entity e){
        entities.add(e);
    }
    void remove(Entity e){
        entities.remove(e);
    }


    public boolean isEmty() {
        return entities.size()==0;
    }
    static Tile getTile(Tile[][] tiles,int x,int y){
        try {
            return tiles[x][y];
        } catch (ArrayIndexOutOfBoundsException ignored){
            return new BlockTile();
        }



    }

    public void influence(Player player) {
        for (Entity e:entities){
            if (!(e instanceof Player)) e.influence(player);
        }
    }
}
