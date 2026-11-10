package com.dmaxtech.productmanager.dao;

import com.dmaxtech.productmanager.config.DatabaseConfig;
import com.dmaxtech.productmanager.model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementação da camada DAO para a entidade Product utilizando JDBC puro.
 * Conexões, PreparedStatements e ResultSets são gerenciados via try-with-resources.
 */
public class ProductDAOImpl implements ProductDAO {

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT id, name, description, category, price, stock, created_at, updated_at " +
                "FROM products ORDER BY id ASC";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Product> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, description, category, price, stock, created_at, updated_at " +
                "FROM products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> searchByName(String name) throws SQLException {
        String sql = "SELECT id, name, description, category, price, stock, created_at, updated_at " +
                "FROM products WHERE UPPER(name) LIKE UPPER(?) ORDER BY id ASC";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Product> findByCategory(String category) throws SQLException {
        String sql = "SELECT id, name, description, category, price, stock, created_at, updated_at " +
                "FROM products WHERE UPPER(category) = UPPER(?) ORDER BY id ASC";

        List<Product> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToProduct(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, category, price, stock, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
        }
        return product;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, category = ?, price = ?, stock = ?, updated_at = ? " +
                "WHERE id = ?";

        LocalDateTime now = LocalDateTime.now();
        product.setUpdatedAt(now);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStock());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setLong(7, product.getId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            p.setCreatedAt(createdAtTs.toLocalDateTime());
        }

        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            p.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }

        return p;
    }
}

