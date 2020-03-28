package com.msj.attendance.attendance_consumer;

import com.msj.attendance.database.attendance.AttendanceDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import com.msj.generated.RecordsRequest;
import com.msj.generated.RecordsResponse;
import com.msj.generated.Student;
import com.msj.generated.StudentRecordsServiceGrpc;
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
}
