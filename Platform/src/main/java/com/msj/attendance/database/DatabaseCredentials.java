package com.msj.attendance.database;

public class DatabaseCredentials {

    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;

    private DatabaseCredentials(String databaseUrl, String databaseUsername, String databasePassword) {
        this.databasePassword = databasePassword;
        this.databaseUrl = databaseUrl;
        this.databaseUsername = databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }
}