package com.msj.attendance.attendance;

import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import com.msj.generated.*;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

/**
 * gRPC endpoint implementation.
 */

public class RecordsConsumerRequestBase extends StudentRecordsServiceGrpc.StudentRecordsServiceImplBase {

    private AttendanceDatabase attendanceDatabase;
    private ReferenceDatabase referenceDatabase;

    public RecordsConsumerRequestBase(AttendanceDatabase attendanceDatabase, ReferenceDatabase referenceDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.referenceDatabase = referenceDatabase;
    }

    /**
     * Get the records for a certain period of a classroom. gRPC implementation.
     * If a room does not exist - set the response status to false.
     * If the room does exist - set the response to true - records returned does not matter.
     * @param request client request
     * @param responseObserver responder observable
     */
    @Override
    public void getPeriodRecords(RecordsRequest request, StreamObserver<RecordsResponse> responseObserver) {
        System.out.println("Got Request: " + request.toString());
        try {
            // gather present students
            String room = request.getRoom();
            ArrayList<Student> presentStudents = new ArrayList<>();
            ArrayList<String> presentStudentIds = attendanceDatabase.getPeriodRecords(Instant.now(), room);
            presentStudentIds.forEach(studentId ->
                presentStudents.add(Student.newBuilder()
                        .setStudentId(studentId)
                        .setName(referenceDatabase.lookupStudentName(studentId))
                        .build()));

            // gather missing students
            ArrayList<String> missingStudentIds = referenceDatabase.getMissingStudents(request.getRoom(), presentStudentIds);
            ArrayList<Student> missingStudents = new ArrayList<>();
            missingStudentIds.forEach(studentId ->
                    missingStudents.add(Student.newBuilder()
                            .setStudentId(studentId)
                            .setName(referenceDatabase.lookupStudentName(studentId))
                            .build()));
            RecordsResponse recordsResponse = RecordsResponse.newBuilder()
                    .setStatus(true)
                    .addAllPresentStudents(presentStudents)
                    .addAllAbsentStudents(missingStudents)
                    .build();
            responseObserver.onNext(recordsResponse);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            RecordsResponse recordsResponse = RecordsResponse.newBuilder().setStatus(false).addAllPresentStudents(new ArrayList<>()).addAllAbsentStudents(new ArrayList<>()).build();
            responseObserver.onNext(recordsResponse);
            responseObserver.onCompleted();
            e.printStackTrace();
        }
    }

    @Override
    public void produceRecord(AttendanceRecord request, StreamObserver<AttendanceResponse> responseObserver) {
        if (request.getStudentId().equals("")) {
            // regular attendance insertion
            System.out.printf("Inserting attendance record. Room: %s | FingerId: %s\n", request.getRoom(), request.getFingerId());
            try {
                attendanceDatabase.insertAttendanceRecord(request.getFingerId(), request.getRoom());
                String studentId = referenceDatabase.lookupStudentId(request.getRoom(), request.getFingerId());
                responseObserver.onNext(AttendanceResponse.newBuilder().setStatus(200).setStudentId(studentId).build());
                responseObserver.onCompleted();
                System.out.println("Sent status 200 OK.");
            } catch (SQLException e) {
                e.printStackTrace();
                responseObserver.onNext(AttendanceResponse.newBuilder().setStatus(0).build());
                responseObserver.onCompleted();
                System.out.println("Sent status 0 ERR.");
            }
        } else {
            System.out.printf("Inserting reference record. Room: %s | FingerId: %s | StudentId: %s\n", request.getRoom(), request.getFingerId(), request.getStudentId());
            // reference insertion
            try {
                referenceDatabase.insertReference(request.getRoom(), request.getFingerId(), request.getStudentId());
                responseObserver.onNext(AttendanceResponse.newBuilder().setStatus(200).build());
                responseObserver.onCompleted();
            } catch (SQLException e) {
                responseObserver.onNext(AttendanceResponse.newBuilder().setStatus(0).build());
                responseObserver.onCompleted();
            }
        }
    }
}
