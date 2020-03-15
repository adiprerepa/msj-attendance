package com.msj.attendance.main;

import com.google.gson.Gson;
import com.msj.attendance.attendance_pods.AttendancePodInteractor;
import com.msj.attendance.database.DatabaseCredentials;
import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        Path path = Path.of(String.format("%s/Platform/src/main/resources/database_credentials.json", System.getenv("ATT_HOME")));
        String jsonContent = null;
        try {
            jsonContent = Files.readString(path, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseCredentials databaseCredentials = new Gson().fromJson(jsonContent, DatabaseCredentials.class);
        ReferenceDatabase referenceDatabase = new ReferenceDatabase(databaseCredentials.getDatabaseUrl(), databaseCredentials.getDatabaseUsername(), databaseCredentials.getDatabasePassword());
        AttendanceDatabase attendanceDatabase = new AttendanceDatabase(databaseCredentials.getDatabaseUrl(), databaseCredentials.getDatabaseUsername(), databaseCredentials.getDatabasePassword(), referenceDatabase);
        AttendancePodInteractor interactor = new AttendancePodInteractor(referenceDatabase, attendanceDatabase);
        interactor.startServer(2000);
        interactor.start();
    }
}
