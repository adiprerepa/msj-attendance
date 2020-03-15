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

public class RecordsConsumerRequestBase extends StudentRecordsServiceGrpc.StudentRecordsServiceImplBase {

    private AttendanceDatabase attendanceDatabase;
    private ReferenceDatabase referenceDatabase;

    public RecordsConsumerRequestBase(AttendanceDatabase attendanceDatabase, ReferenceDatabase referenceDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.referenceDatabase = referenceDatabase;
    }

    @Override
    public void getPeriodRecords(RecordsRequest request, StreamObserver<RecordsResponse> responseObserver) {
        String room = request.getRoom();
        try {
            ArrayList<Student> recordedStudents = new ArrayList<>();
            ArrayList<String> studentIds = attendanceDatabase.getPeriodRecords(Instant.now(), room);
            studentIds.forEach(studentId ->
                recordedStudents.add(Student.newBuilder()
                        .setStudentId(studentId)
                        .setName(referenceDatabase.lookupStudentName(studentId))
                        .build())
            );
            RecordsResponse recordsResponse = RecordsResponse.newBuilder().addAllStudents(recordedStudents).build();
            responseObserver.onNext(recordsResponse);
            responseObserver.onCompleted();
        } catch (SQLException e) {
            RecordsResponse recordsResponse = RecordsResponse.newBuilder().addAllStudents(null).build();
            responseObserver.onNext(recordsResponse);
            responseObserver.onCompleted();
            e.printStackTrace();
        }
    }
}
