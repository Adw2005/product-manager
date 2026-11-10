package com.dmaxtech.productmanager.dao;

import com.dmaxtech.productmanager.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * DAO (Data Access Object) para a entidade User.
 */
public interface UserDAO {

    Optional<User> findByUsername(String username) throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    User save(User user) throws SQLException;
}
