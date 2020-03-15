package com.msj.attendance.database.attendance;

import com.msj.attendance.database.BaseDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class AttendanceDatabase extends BaseDatabase {

    private ReferenceDatabase referenceDatabase;

    /**
     * Initialize database connection - only done in super()
     *
     * @param databaseUrl      url of db (mysql)
     * @param databaseUsername username (with permissions on all databases)
     * @param databasePassword password
     * @param referenceDatabase for acquiring student id from pod finger id
     */
    public AttendanceDatabase(String databaseUrl, String databaseUsername, String databasePassword, ReferenceDatabase referenceDatabase) {
        super(databaseUrl, databaseUsername, databasePassword);
        this.referenceDatabase = referenceDatabase;
    }

    /**
     * Insert a student attendance record. This is the driver function -
     *  when a student scans their finger in, this is called.
     * @param fingerId scanner-assigned id
     * @param roomId table name
     * @return status
     * @throws SQLException something went wrong
     */
    public boolean insertAttendanceRecord(String fingerId, String roomId) throws SQLException {
        String studentId = referenceDatabase.lookupStudentId(roomId, fingerId);
        Statement statement = super.connection.createStatement();
        String execStatement = String.format("insert into %s (student_id, time, period) values (%s, %s);", roomId, studentId, Instant.now().toString());
        return statement.execute(execStatement);
    }

    /**
     * This function will be called when the desktop computer requests it - reactive.
     * Hard - coded 10 minute window of collection
     * @param reqTime time of request; reqTime - 10min is window
     * @param roomId table name of room.
     * @return list of Student Ids
     */
    public ArrayList<String> getPeriodRecords(Instant reqTime, String roomId) throws SQLException {
        ArrayList<String> periodRecords = new ArrayList<>();
        Date reqDate = Date.from(reqTime);
        // ms in 10 min
        long window = 1000*60*10;
        long windowLowerBound = reqDate.getTime() - window;
        long windowHigherBound = reqDate.getTime();
        String retrieveQuery = String.format("select * from %s;", roomId);
        Statement statement = super.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(retrieveQuery);
        while (resultSet.next()) {
            long timeStamp = Date.from(Instant.parse(resultSet.getString("time"))).getTime();
            // in 10 minute window
            if (timeStamp > windowLowerBound && timeStamp < windowHigherBound) {
                periodRecords.add(resultSet.getString("student_id"));
            }
        }
        return periodRecords;
    }
}
