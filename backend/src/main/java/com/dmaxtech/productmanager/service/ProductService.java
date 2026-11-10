package com.dmaxtech.productmanager.service;

import com.dmaxtech.productmanager.dao.ProductDAO;
import com.dmaxtech.productmanager.dao.ProductDAOImpl;
import com.dmaxtech.productmanager.exception.ResourceNotFoundException;
import com.dmaxtech.productmanager.exception.ValidationException;
import com.dmaxtech.productmanager.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE (Camada de Regras de Negócio)
 * Intermediário entre os Controllers HTTP e a camada DAO.
 * Realiza as validações dos dados e regras de negócio antes de persistir no MySQL.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this(new ProductDAOImpl());
    }

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<Product> findAll() {
        try {
            return productDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar lista de produtos no banco de dados.", e);
        }
    }

    public Product findById(Long id) {
        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("ID inválido: " + id);
        }
        try {
            return productDAO.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto com id: " + id, e);
        }
    }

    public List<Product> search(String name) {
        if (name == null || name.isBlank()) {
            return findAll();
        }
        try {
            return productDAO.searchByName(name.trim());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar produtos por nome.", e);
        }
    }

    public Product create(Product product) {
        validateProduct(product);
        product.setId(null);

        try {
            return productDAO.save(product);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto no MySQL.", e);
        }
    }

    public Product update(Long id, Product updated) {
        validateProduct(updated);
        Product existing = findById(id);

        existing.setName(updated.getName().trim());
        existing.setDescription(updated.getDescription() != null ? updated.getDescription().trim() : null);
        existing.setCategory(updated.getCategory().trim());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());

        try {
            boolean success = productDAO.update(existing);
            if (!success) {
                throw new ResourceNotFoundException("Produto não encontrado para atualização com id: " + id);
            }
            return existing;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto com id: " + id, e);
        }
    }

    public void delete(Long id) {
        Product existing = findById(id);
        try {
            boolean success = productDAO.deleteById(existing.getId());
            if (!success) {
                throw new ResourceNotFoundException("Produto não encontrado para exclusão com id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto com id: " + id, e);
        }
    }

    /**
     * Validação rigorosa dos campos obrigatórios da entidade Product.
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new ValidationException("Os dados do produto não foram fornecidos.");
        }

        Map<String, String> errors = new HashMap<>();

        if (product.getName() == null || product.getName().trim().isBlank()) {
            errors.put("name", "O nome do produto é obrigatório.");
        } else if (product.getName().trim().length() > 120) {
            errors.put("name", "O nome do produto deve ter no máximo 120 caracteres.");
        }

        if (product.getCategory() == null || product.getCategory().trim().isBlank()) {
            errors.put("category", "A categoria é obrigatória.");
        } else if (product.getCategory().trim().length() > 60) {
            errors.put("category", "A categoria deve ter no máximo 60 caracteres.");
        }

        if (product.getPrice() == null) {
            errors.put("price", "O preço é obrigatório.");
        } else if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.put("price", "O preço não pode ser negativo.");
        }

        if (product.getStock() == null) {
            errors.put("stock", "O estoque é obrigatório.");
        } else if (product.getStock() < 0) {
            errors.put("stock", "O estoque não pode ser negativo.");
        }

        if (product.getDescription() != null && product.getDescription().length() > 500) {
            errors.put("description", "A descrição deve ter no máximo 500 caracteres.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
