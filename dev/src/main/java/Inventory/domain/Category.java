package Inventory.domain;

public class Category {
    private String name;  // primary key
    private int level;    // 0=main, 1=sub, 2=subsub

    public Category(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { this.level = level; }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category c = (Category) o;
        return name.equals(c.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}