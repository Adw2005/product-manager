package com.dmaxtech.productmanager.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gerenciador de conexão com o banco de dados MySQL via JDBC puro.
 * Não utiliza frameworks de injeção ou JPA.
 * Lê configurações de variáveis de ambiente com fallbacks para application.properties e padrões locais.
 */
public class DatabaseConfig {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "product_manager";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String host;
    private static String port;
    private static String database;
    private static String user;
    private static String password;
    private static String jdbcUrl;

    static {
        Properties fileProps = new Properties();
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                fileProps.load(is);
            }
        } catch (Exception ignored) {
        }

        // Carrega as configurações: Ambiente > Arquivo de Propriedades > Padrão
        host = getVal("DB_HOST", fileProps.getProperty("db.host"), DEFAULT_HOST);
        port = getVal("DB_PORT", fileProps.getProperty("db.port"), DEFAULT_PORT);
        database = getVal("DB_NAME", fileProps.getProperty("db.name"), DEFAULT_DATABASE);
        user = getVal("DB_USER", fileProps.getProperty("db.user"), DEFAULT_USER);
        password = getVal("DB_PASSWORD", fileProps.getProperty("db.password"), DEFAULT_PASSWORD);

        jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&useUnicode=true&characterEncoding=UTF-8",
                host, port, database
        );

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC do MySQL não encontrado no classpath.", e);
        }
    }

    private static String getVal(String envName, String propVal, String defaultValue) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String sysProp = System.getProperty(envName);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }
        if (propVal != null && !propVal.isBlank()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    /**
     * Abre e retorna uma nova conexão JDBC com o MySQL.
     *
     * @return Conexão JDBC ativa
     * @throws SQLException Em caso de falha na autenticação ou rede
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, user, password);
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getUser() {
        return user;
    }
}
