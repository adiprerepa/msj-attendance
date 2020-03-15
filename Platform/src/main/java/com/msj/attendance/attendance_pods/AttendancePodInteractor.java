package com.msj.attendance.attendance_pods;

import com.google.protobuf.ByteString;
import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import com.msj.generated.AttendanceRecord;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;

/**
 * Interact with Esp8266
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
     * Open to connection
     * @param port
     */
    public void startServer(int port) throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
    }

    /**
     * While true accept connections
     *  recieve attendance record - determine if its
     *  a reference insertion of an attendance insertion
     *
     */
    @Override
    public void run() {
        // noinspection InfiniteLoopStatement
        while (true) {
            try {
                SocketChannel socket = serverSocket.accept();
                AttendanceRecord record = readRecord(socket);
                if (record.getStudentId() == null) {
                    // regular attendance insertion
                    attendanceDatabase.insertAttendanceRecord(record.getFingerId(), record.getRoom());
                } else {
                    // reference insertion
                    referenceDatabase.insertReference(record.getRoom(), record.getFingerId(), record.getStudentId());

                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private AttendanceRecord readRecord(SocketChannel socket) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        int numBytesRead = socket.read(buffer);
        if (numBytesRead == -1) socket.close();
        buffer.clear();
        return AttendanceRecord.parseFrom(ByteString.copyFrom(buffer));
    }
}
