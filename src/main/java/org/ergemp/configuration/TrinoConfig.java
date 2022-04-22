package org.ergemp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
public class TrinoConfig {

    @Value("${trino.jdbcUrl}")
    String jdbcUrl;

    @Value("${trino.username}")
    String username;

    @Value("${trino.password}")
    String password;

    @Value("${trino.catalog}")
    String catalog;

    @Value("${trino.schema}")
    String schema;

    Connection connection;

    public TrinoConfig(){
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Bean
    public void createConnection() {

        Properties properties = new Properties();
        properties.setProperty("user", this.username);
        //properties.setProperty("password", this.password);
        properties.setProperty("SSL", "false");

        try {
            this.connection = DriverManager.getConnection(jdbcUrl, properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
