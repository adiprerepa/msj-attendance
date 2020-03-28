package com.msj.attendance.api;

import com.google.gson.Gson;
import com.msj.attendance.attendance_consumer.RecordsConsumerRequestBase;
import com.msj.attendance.attendance_producer.AttendancePodInteractor;
import com.msj.attendance.database.DatabaseCredentials;
import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clients Pull records from records Api.
 */

public class RecordsApi {

    private Server server;


    /**
     * Start the client and pod endpoints.
     * @param credentialsPath path to json database credentials
     * @param podPort port pods will connect to.
     * @param consumerPort port api consumers will connect to.
     */
    public void start(Path credentialsPath, int podPort, int consumerPort) throws IOException {
        String jsonContent = null;
        try {
            jsonContent = Files.readString(credentialsPath, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseCredentials databaseCredentials = new Gson().fromJson(jsonContent, DatabaseCredentials.class);
        ReferenceDatabase referenceDatabase = new ReferenceDatabase(databaseCredentials.getDatabaseUrl(), databaseCredentials.getDatabaseUsername(), databaseCredentials.getDatabasePassword());
        AttendanceDatabase attendanceDatabase = new AttendanceDatabase(databaseCredentials.getDatabaseUrl(), databaseCredentials.getDatabaseUsername(), databaseCredentials.getDatabasePassword(), referenceDatabase);
        try {
            server = ServerBuilder
                    .forPort(consumerPort)
                    .addService(new RecordsConsumerRequestBase(attendanceDatabase, referenceDatabase))
                    .build()
                    .start();
            System.out.printf("Consumer Service started on port %d\n", consumerPort);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.err.println("JVM Shutdown, shutting down consumer service.");
                this.stopConsumerService();
                System.err.println("Shut down consumer service successfully.");
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.startPodServer(podPort, referenceDatabase, attendanceDatabase);
    }

    private void startPodServer(int port, ReferenceDatabase referenceDatabase, AttendanceDatabase attendanceDatabase) throws IOException {
        AttendancePodInteractor interactor = new AttendancePodInteractor(referenceDatabase, attendanceDatabase);
        interactor.startServer(port);
        interactor.start();
    }

    private void stopConsumerService() {
        if (server != null) server.shutdown();
    }
}
