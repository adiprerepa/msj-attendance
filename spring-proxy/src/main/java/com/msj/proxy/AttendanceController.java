package com.msj.proxy;

import com.msj.generated.RecordsRequest;
import com.msj.generated.RecordsResponse;
import com.msj.generated.StudentRecordsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
public class AttendanceController {

    Logger logger = LoggerFactory.getLogger(AttendanceController.class);

    static String server = "localhost:2002";

    @ModelAttribute
    public void setResponseHeader(HttpServletResponse response) {
        logger.info("Sending header Access-Control-Allow-Origin");
        response.setHeader("Access-Control-Allow-Origin", "*");
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/attendance/getRecords/{room}")
    public AttendanceResponse getRecords(@PathVariable String room) {
        logger.info("Got Request, room = " + room);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(server).usePlaintext().build();
        StudentRecordsServiceGrpc.StudentRecordsServiceBlockingStub blockingStub =
                StudentRecordsServiceGrpc.newBlockingStub(channel);
        RecordsRequest recordsRequest = RecordsRequest.newBuilder().setRoom(room).build();
        RecordsResponse recordsResponse =  blockingStub.getPeriodRecords(recordsRequest);
        ArrayList<AttendanceResponse.Student> presentStudents = new ArrayList<>();
        ArrayList<AttendanceResponse.Student> missingStudents = new ArrayList<>();
        recordsResponse.getPresentStudentsList().forEach(student -> presentStudents.add(new AttendanceResponse.Student(student.getStudentId(), student.getName())));
        recordsResponse.getAbsentStudentsList().forEach(student -> missingStudents.add(new AttendanceResponse.Student(student.getStudentId(), student.getName())));
        return new AttendanceResponse(recordsResponse.getStatus(), presentStudents, missingStudents);
    }
}
