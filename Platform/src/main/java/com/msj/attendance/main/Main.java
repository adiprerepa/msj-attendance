package com.msj.attendance.main;

import com.msj.attendance.api.RecordsApi;
import java.io.IOException;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {
        RecordsApi api = new RecordsApi();
        int consumerPort, producerPort;
        if (args.length != 2) {
            consumerPort = 2000;
            producerPort = 2001;
        } else {
            consumerPort = Integer.parseInt(args[0]);
            producerPort = Integer.parseInt(args[1]);
        }
        api.start(Path.of(String.format("%s/Platform/src/main/resources/database_credentials.json", System.getenv("ATT_HOME"))),
                producerPort, consumerPort);
    }
}
