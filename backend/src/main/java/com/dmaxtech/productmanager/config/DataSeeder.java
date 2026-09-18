package com.dmaxtech.productmanager.config;

import com.dmaxtech.productmanager.model.User;
import com.dmaxtech.productmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Executado uma vez na inicializacao do backend. Garante que exista
 * um usuario administrador padrao para o professor conseguir logar
 * sem precisar cadastrar nada manualmente no banco.
 *
 * Credenciais padrao: admin / admin123
 * (a senha e sempre gravada com hash BCrypt, nunca em texto puro)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ADMIN");
            userRepository.save(admin);
            System.out.println(">> Usuario admin padrao criado (login: admin / senha: admin123)");
        }
    }
}
