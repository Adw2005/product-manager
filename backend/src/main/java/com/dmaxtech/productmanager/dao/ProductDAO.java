package com.dmaxtech.productmanager.dao;

import com.dmaxtech.productmanager.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object)
 * Interface responsável exclusivamente pelas operações de persistência e consulta
 * da entidade Product no MySQL. Não depende de nenhum framework.
 */
public interface ProductDAO {

    /**
     * Retorna todos os produtos cadastrados ordenados pelo identificador.
     */
    List<Product> findAll() throws SQLException;

    /**
     * Localiza um produto pelo seu identificador único.
     */
    Optional<Product> findById(Long id) throws SQLException;

    /**
     * Busca produtos cujo nome contenha o termo informado (case-insensitive).
     */
    List<Product> searchByName(String name) throws SQLException;

    /**
     * Busca produtos pertencentes à categoria informada.
     */
    List<Product> findByCategory(String category) throws SQLException;

    /**
     * Insere um novo produto no banco de dados e atribui o ID gerado automaticamente.
     */
    Product save(Product product) throws SQLException;

    /**
     * Atualiza os dados de um produto existente no banco.
     *
     * @return true se o registro foi atualizado com sucesso
     */
    boolean update(Product product) throws SQLException;

    /**
     * Remove o produto correspondente ao ID informado.
     *
     * @return true se o registro foi excluído
     */
    boolean deleteById(Long id) throws SQLException;
}
