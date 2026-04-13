package Inventory.domain;

public class Category {
    private String main;
    private String sub;
    private String subsub;

    // Constructor - all three levels
    public Category(String main, String sub, String subsub) {
        this.main = main;
        this.sub = sub;
        this.subsub = subsub;
    }

    // Constructor - main and sub only
    public Category(String main, String sub) {
        this.main = main;
        this.sub = sub;
        this.subsub = null;
    }

    // Constructor - main only
    public Category(String main) {
        this.main = main;
        this.sub = null;
        this.subsub = null;
    }

    // Get the full category path
    public String getFullPath() {
        StringBuilder path = new StringBuilder();
        path.append(main);

        if (sub != null && !sub.isEmpty()) {
            path.append(" > ").append(sub);
        }

        if (subsub != null && !subsub.isEmpty()) {
            path.append(" > ").append(subsub);
        }

        return path.toString();
    }

    // Get category name (full path for display)
    public String getCategoryName() {
        return getFullPath();
    }

    // Getters
    public String getMain() {
        return main;
    }

    public String getSub() {
        return sub;
    }

    public String getSubsub() {
        return subsub;
    }

    // Setters
    public void setMain(String main) {
        if (main != null && !main.isEmpty()) {
            this.main = main;
        }
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setSubsub(String subsub) {
        this.subsub = subsub;
    }

    // Get category summary
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Main: ").append(main);
        if (sub != null) details.append(" | Sub: ").append(sub);
        if (subsub != null) details.append(" | Subsub: ").append(subsub);
        return details.toString();
    }

    @Override
    public String toString() {
        return getFullPath();
    }

}