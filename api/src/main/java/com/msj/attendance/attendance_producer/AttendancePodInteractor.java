package com.msj.attendance.attendance_producer;

import com.google.protobuf.ByteString;
import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import com.msj.generated.AttendanceRecord;
import com.msj.generated.AttendanceResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Interact with Attendance Pods.
 * Operations:
 *  - read received records
 *  - write status to pod
 *  todo shutdown serversocket on JVM shutdown
 *   add a shutdown hook
 *   i.e. @see Runtime#getRuntime()#addShutdownHook
 */
public class AttendancePodInteractor extends Thread {

    private ServerSocketChannel serverSocket;
    private ReferenceDatabase referenceDatabase;
    private AttendanceDatabase attendanceDatabase;

    public AttendancePodInteractor(ReferenceDatabase referenceDatabase, AttendanceDatabase attendanceDatabase) {
        this.referenceDatabase = referenceDatabase;
        this.attendanceDatabase = attendanceDatabase;
    }

    /**
     * Open to connection.
     * Shutdown hook closes server.
     * @param port open port
     */
    public void startServer(int port) throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        System.out.printf("Producer Service started on port %d...\n", port);
    }

    /**
     * Thread Process:
     *  - Accept Connection
     *  - Determine record type
     *  - Insert based on record type
     *  - Send back status
     */
    @Override
    public void run() {
        // noinspection InfiniteLoopStatement
        while (true) {
            try {
                SocketChannel socket = serverSocket.accept();
                System.out.printf("Got a connection from %s at %s\n", socket.getRemoteAddress().toString(), Instant.now().toString());
                AttendanceRecord record = readRecord(socket);
                if (record.getStudentId() == null) {
                    // regular attendance insertion
                    System.out.printf("Inserting attendance record. Room: %s | FingerId: %s\n", record.getRoom(), record.getFingerId());
                    if (attendanceDatabase.insertAttendanceRecord(record.getFingerId(), record.getRoom())) {
                        sendStatus(socket, AttendanceResponse.newBuilder().setStatus(200).build());
                        System.out.println("Sent status 200 OK.");
                    } else {
                        sendStatus(socket, AttendanceResponse.newBuilder().setStatus(0).build());
                        System.out.println("Sent status 0 ERR.");
                    }
                } else {
                    // reference insertion
                    System.out.printf("Inserting reference record. Room: %s | FingerId: %s | StudentId: %s\n", record.getRoom(), record.getFingerId(), record.getStudentId());
                    if (referenceDatabase.insertReference(record.getRoom(), record.getFingerId(), record.getStudentId())) {
                        sendStatus(socket, AttendanceResponse.newBuilder().setStatus(200).build());
                        System.out.println("Sent status 200 OK.");
                    } else {
                        sendStatus(socket, AttendanceResponse.newBuilder().setStatus(0).build());
                        System.out.println("Sent status 0 ERR.");
                    }
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read the Attendance Record from a socket.
     * @param socket sock
     * @throws IOException connection closes on us
     */
    private AttendanceRecord readRecord(SocketChannel socket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        int numBytesRead = socket.read(buffer);
        if (numBytesRead == -1) socket.close();
        buffer.clear();
        return AttendanceRecord.parseFrom(ByteString.copyFrom(buffer));
    }

    /**
     * Send the status of the database operation to the esp8266.
     * @param socket sock
     * @param response response we send back
     */
    private void sendStatus(SocketChannel socket, AttendanceResponse response) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.put(response.toByteArray());
        buffer.flip();
        try {
            socket.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shut down ServerSocket to avoid future {@link java.nio.channels.AlreadyBoundException}.
     * @throws IOException unable to shut down
     */
    private void shutdownProducerServer() throws IOException {
        serverSocket.close();
    }
}