package com.dmaxtech.productmanager.repository;

import com.dmaxtech.productmanager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO (Data Access Object)
 * Camada responsavel exclusivamente pelo acesso aos dados no MySQL.
 * O Spring Data JPA gera automaticamente a implementacao do CRUD basico
 * (save, findById, findAll, deleteById, etc.) a partir desta interface.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByNameContainingIgnoreCase(String name);
}
