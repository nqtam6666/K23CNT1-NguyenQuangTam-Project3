package k23cnt1.nqt.project3.nqtController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Controller
public class NqtDatabaseSchemaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * API endpoint trả về cấu trúc database dạng JSON
     */
    @GetMapping("/admin/database-schema.json")
    @ResponseBody
    public Map<String, Object> getDatabaseSchemaJson() {
        Map<String, Object> schema = new LinkedHashMap<>();
        
        try {
            // Lấy tên database từ connection
            String databaseName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            schema.put("database", databaseName);
            schema.put("timestamp", new Date().toString());
            
            // Lấy danh sách tất cả các bảng
            String sql = "SELECT TABLE_NAME, TABLE_COMMENT " +
                        "FROM information_schema.TABLES " +
                        "WHERE TABLE_SCHEMA = ? " +
                        "ORDER BY TABLE_NAME";
            
            List<Map<String, Object>> tables = jdbcTemplate.query(sql, 
                new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return createTableMap(rs, databaseName);
                    }
                },
                databaseName);
            
            schema.put("tables", tables);
            schema.put("totalTables", tables.size());
            
        } catch (Exception e) {
            schema.put("error", e.getMessage());
            schema.put("errorType", e.getClass().getSimpleName());
        }
        
        return schema;
    }

    private Map<String, Object> createTableMap(ResultSet rs, String databaseName) throws SQLException {
        Map<String, Object> table = new LinkedHashMap<>();
        String tableName = rs.getString("TABLE_NAME");
        table.put("name", tableName);
        table.put("comment", rs.getString("TABLE_COMMENT"));
        
        // Lấy thông tin các cột
        String columnsSql = "SELECT " +
            "COLUMN_NAME, " +
            "COLUMN_TYPE, " +
            "IS_NULLABLE, " +
            "COLUMN_DEFAULT, " +
            "COLUMN_KEY, " +
            "EXTRA, " +
            "COLUMN_COMMENT " +
            "FROM information_schema.COLUMNS " +
            "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
            "ORDER BY ORDINAL_POSITION";
        
        List<Map<String, Object>> columns = jdbcTemplate.query(columnsSql,
            new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs2, int rowNum2) throws SQLException {
                    Map<String, Object> column = new LinkedHashMap<>();
                    column.put("name", rs2.getString("COLUMN_NAME"));
                    column.put("type", rs2.getString("COLUMN_TYPE"));
                    column.put("nullable", "YES".equals(rs2.getString("IS_NULLABLE")));
                    column.put("default", rs2.getString("COLUMN_DEFAULT"));
                    column.put("key", rs2.getString("COLUMN_KEY"));
                    column.put("extra", rs2.getString("EXTRA"));
                    column.put("comment", rs2.getString("COLUMN_COMMENT"));
                    return column;
                }
            },
            databaseName, tableName);
        
        table.put("columns", columns);
        
        // Lấy thông tin foreign keys
        String fkSql = "SELECT " +
            "CONSTRAINT_NAME, " +
            "COLUMN_NAME, " +
            "REFERENCED_TABLE_NAME, " +
            "REFERENCED_COLUMN_NAME " +
            "FROM information_schema.KEY_COLUMN_USAGE " +
            "WHERE TABLE_SCHEMA = ? " +
            "AND TABLE_NAME = ? " +
            "AND REFERENCED_TABLE_NAME IS NOT NULL";
        
        List<Map<String, Object>> foreignKeys = jdbcTemplate.query(fkSql,
            new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs3, int rowNum3) throws SQLException {
                    Map<String, Object> fk = new LinkedHashMap<>();
                    fk.put("constraint", rs3.getString("CONSTRAINT_NAME"));
                    fk.put("column", rs3.getString("COLUMN_NAME"));
                    fk.put("referencesTable", rs3.getString("REFERENCED_TABLE_NAME"));
                    fk.put("referencesColumn", rs3.getString("REFERENCED_COLUMN_NAME"));
                    return fk;
                }
            },
            databaseName, tableName);
        
        table.put("foreignKeys", foreignKeys);
        
        // Lấy thông tin indexes
        String indexSql = "SELECT " +
            "INDEX_NAME, " +
            "COLUMN_NAME, " +
            "NON_UNIQUE, " +
            "SEQ_IN_INDEX " +
            "FROM information_schema.STATISTICS " +
            "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? " +
            "ORDER BY INDEX_NAME, SEQ_IN_INDEX";
        
        Map<String, List<String>> indexes = new LinkedHashMap<>();
        jdbcTemplate.query(indexSql,
            new RowMapper<Object>() {
                @Override
                public Object mapRow(ResultSet rs4, int rowNum4) throws SQLException {
                    String indexName = rs4.getString("INDEX_NAME");
                    String columnName = rs4.getString("COLUMN_NAME");
                    indexes.computeIfAbsent(indexName, k -> new ArrayList<>()).add(columnName);
                    return null;
                }
            },
            databaseName, tableName);
        
        table.put("indexes", indexes);
        
        return table;
    }

    /**
     * View HTML để hiển thị cấu trúc database
     */
    @GetMapping("/admin/database-schema")
    public String getDatabaseSchemaView(Model model) {
        model.addAttribute("jsonUrl", "/admin/database-schema.json");
        return "admin/database-schema/view";
    }
}
