package com.dmaxtech.productmanager.service;

import com.dmaxtech.productmanager.dao.ProductDAO;
import com.dmaxtech.productmanager.exception.ResourceNotFoundException;
import com.dmaxtech.productmanager.exception.ValidationException;
import com.dmaxtech.productmanager.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                "Notebook Ultra 15",
                "Notebook para uso profissional",
                "Eletrônicos",
                new BigDecimal("4599.90"),
                12
        );
        sampleProduct.setId(1L);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos com sucesso")
    void shouldFindAllProducts() throws SQLException {
        when(productDAO.findAll()).thenReturn(Arrays.asList(sampleProduct));

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Notebook Ultra 15", result.get(0).getName());
        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID existente")
    void shouldFindProductById() throws SQLException {
        when(productDAO.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Notebook Ultra 15", result.getName());
        verify(productDAO, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void shouldThrowExceptionWhenProductNotFound() throws SQLException {
        when(productDAO.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
        verify(productDAO, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve cadastrar um novo produto com sucesso")
    void shouldCreateProduct() throws SQLException {
        Product newProduct = new Product("Mouse Gamer", "RGB 16000 DPI", "Acessórios", new BigDecimal("199.90"), 30);
        when(productDAO.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.create(newProduct);

        assertNotNull(result);
        verify(productDAO, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Deve lançar ValidationException se o nome for vazio")
    void shouldThrowValidationExceptionWhenNameIsEmpty() {
        Product invalidProduct = new Product("", "Desc", "Acessórios", new BigDecimal("10.00"), 5);

        assertThrows(ValidationException.class, () -> productService.create(invalidProduct));
    }

    @Test
    @DisplayName("Deve atualizar um produto existente")
    void shouldUpdateProduct() throws SQLException {
        Product updateData = new Product("Notebook Ultra 15 Pro", "Com 32GB RAM", "Eletrônicos", new BigDecimal("5599.90"), 10);
        when(productDAO.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productDAO.update(any(Product.class))).thenReturn(true);

        Product result = productService.update(1L, updateData);

        assertNotNull(result);
        assertEquals("Notebook Ultra 15 Pro", sampleProduct.getName());
        assertEquals(new BigDecimal("5599.90"), sampleProduct.getPrice());
        verify(productDAO, times(1)).findById(1L);
        verify(productDAO, times(1)).update(sampleProduct);
    }

    @Test
    @DisplayName("Deve excluir um produto existente")
    void shouldDeleteProduct() throws SQLException {
        when(productDAO.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productDAO.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> productService.delete(1L));
        verify(productDAO, times(1)).findById(1L);
        verify(productDAO, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve buscar produtos pelo nome")
    void shouldSearchProductsByName() throws SQLException {
        when(productDAO.searchByName("Notebook")).thenReturn(Arrays.asList(sampleProduct));

        List<Product> result = productService.search("Notebook");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Notebook Ultra 15", result.get(0).getName());
        verify(productDAO, times(1)).searchByName("Notebook");
    }
}
