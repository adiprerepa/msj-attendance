package com.msj.attendance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDatabase {

    public Connection connection;

    /**
     * Initialize database connection - only done in super()
     * @param databaseUrl url of db (mysql)
     * @param databaseUsername username (with permissions on all databases)
     * @param databasePassword password
     */
    public BaseDatabase(String databaseUrl, String databaseUsername, String databasePassword) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to mySQL database...");
            connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
            System.out.println("Connected to mySQL database successfully!");
        } catch (SQLException sqlException) {
            System.err.println("An error happened with sql");
        } catch (ClassNotFoundException classNotFoundException) {
            System.err.println("Unable to find class");
        }
    }
}
