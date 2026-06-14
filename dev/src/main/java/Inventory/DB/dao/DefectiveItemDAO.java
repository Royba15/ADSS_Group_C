package Inventory.DB.dao;

import Inventory.dto.DefectiveItemDTO;
import java.sql.SQLException;
import java.util.List;

public interface DefectiveItemDAO {
    void save(DefectiveItemDTO dto) throws SQLException;
    List<DefectiveItemDTO> findAll() throws SQLException;
    List<DefectiveItemDTO> findByProductId(int productId) throws SQLException;
}
