package k23cnt1.nqt.nqt_lesson07.nqtController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/table-structure")
public class nqtTableStructureController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{tableName}")
    public ResponseEntity<?> getTableStructure(@PathVariable String tableName) {
        try {
            String sql = "DESCRIBE " + tableName;
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
            
            Map<String, Object> result = new HashMap<>();
            result.put("tableName", tableName);
            result.put("columns", columns);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error getting table structure: " + e.getMessage());
            error.put("tableName", tableName);
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTables() {
        try {
            String sql = "SHOW TABLES";
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> table : tables) {
                String tableName = table.values().iterator().next().toString();
                String describeSql = "DESCRIBE " + tableName;
                List<Map<String, Object>> columns = jdbcTemplate.queryForList(describeSql);
                
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("tableName", tableName);
                tableInfo.put("columns", columns);
                result.add(tableInfo);
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error getting tables: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategoriesStructure() {
        return getTableStructure("categories");
    }

    @GetMapping("/books")
    public ResponseEntity<?> getBooksStructure() {
        return getTableStructure("books");
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProductsStructure() {
        return getTableStructure("products");
    }
}

