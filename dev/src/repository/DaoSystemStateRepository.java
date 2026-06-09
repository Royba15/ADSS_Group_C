package repository;

import data.DatabaseInitializer;

public class DaoSystemStateRepository implements SystemStateRepository {

    @Override
    public void clearData() {
        DatabaseInitializer.clearData();
    }
}
