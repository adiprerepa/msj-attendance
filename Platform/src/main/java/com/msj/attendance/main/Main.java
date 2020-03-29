package com.msj.attendance.main;

import com.msj.attendance.api.RecordsApi;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        RecordsApi api = new RecordsApi();
        int consumerPort, producerPort;
        if (args.length != 2) {
            consumerPort = 2002;
            producerPort = 2003;
        } else {
            consumerPort = Integer.parseInt(args[0]);
            producerPort = Integer.parseInt(args[1]);
        }
        api.start(Path.of("/home/aditya/Projects/AttendanceSystem/Platform/src/main/resources/database_credentials.json"),
                producerPort, consumerPort);
    }
}
