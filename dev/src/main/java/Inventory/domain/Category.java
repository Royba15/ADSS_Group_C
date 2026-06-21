package Inventory.domain;

public class Category {
    private final String name;  // primary key
    private int level;    // 0=main, 1=sub, 2=subsub

    // Constructor
    public Category(String name, int level) {
        this.name = name;
        this.level = level;
    }

    // Set & Get
    public String getName() { return name; }
    public int getLevel() { return level; }


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