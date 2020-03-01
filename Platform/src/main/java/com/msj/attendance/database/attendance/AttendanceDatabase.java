package com.msj.attendance.database.attendance;

import com.msj.attendance.database.BaseDatabase;
import com.msj.attendance.database.reference.ReferenceDatabase;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class AttendanceDatabase extends BaseDatabase {

    private ReferenceDatabase referenceDatabase;

    /**
     * Initialize database connection - only done in super()
     *
     * @param databaseUrl      url of db (mysql)
     * @param databaseUsername username (with permissions on all databases)
     * @param databasePassword password
     */
    public AttendanceDatabase(String databaseUrl, String databaseUsername, String databasePassword, ReferenceDatabase referenceDatabase) {
        super(databaseUrl, databaseUsername, databasePassword);
    }

    public boolean insertAttendanceRecord(String fingerId, String roomId) throws SQLException {
        String studentId = referenceDatabase.lookupStudentId(roomId, fingerId);
        Statement statement = super.connection.createStatement();
        String execStatement = String.format("insert into %s (student_id, time) values (%s, %s);", roomId, Instant.now().toString());
        return statement.execute(execStatement);
    }


}
