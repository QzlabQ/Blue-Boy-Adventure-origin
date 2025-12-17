package maptool;

public class EntityEntry {
    String category;
    String type;
    int x;
    int y;
    String extra;

    public EntityEntry(String category, String type, int x, int y, String extra) {
        this.category = category;
        this.type = type;
        this.x = x;
        this.y = y;
        this.extra = extra;
    }

    @Override
    public String toString() {
        if (extra == null || extra.isEmpty()) {
            return category + "," + type + "," + x + "," + y;
        } else {
            return category + "," + type + "," + x + "," + y + "," + extra;
        }
    }

    // Getters
    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getExtra() {
        return extra;
    }
}