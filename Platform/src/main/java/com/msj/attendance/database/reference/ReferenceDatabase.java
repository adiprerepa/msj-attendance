package com.msj.attendance.database.reference;

import com.msj.attendance.database.BaseDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Table Contains all room's finger to student id mappings.
 */
public class ReferenceDatabase extends BaseDatabase {

    private String tableName = "reference";

    /**
     * Initialize database connection - only done in super()
     *
     * @param databaseUrl      url of db (mysql)
     * @param databaseUsername username (with permissions on all databases)
     * @param databasePassword password
     */
    public ReferenceDatabase(String databaseUrl, String databaseUsername, String databasePassword) {
        super(databaseUrl, databaseUsername, databasePassword);
    }

    /**
     * This function is called in enrollment time - when a new student is enrolled.
     * @param room room name
     * @param fingerId id given by fingerprint sensor
     * @param studentId school-given id
     * @return status, return to pod
     * @throws SQLException something went wrong
     */
    public boolean insertReference(String room, String fingerId, String studentId) throws SQLException {
        Statement statement = super.connection.createStatement();
        String execStatement = String.format("insert into %s (room_id, finger_id, student_id) values (%s, %s, %s);", tableName,
                room, fingerId, studentId);
        return statement.execute(execStatement);
    }

    /**
     *
     * @param room room name
     * @param fingerId id given by fingerprint sensor
     * @return student id
     * @throws SQLException something went wrong
     */
    public String lookupStudentId(String room, String fingerId) throws SQLException {
        Statement statement = super.connection.createStatement();
        String execStatement = String.format("select * from %s where `%s` = %s and `%s` = %s;", tableName, "room_id",
                room, "finger_id", fingerId);
        ResultSet set = statement.executeQuery(execStatement);
        return set.getString("student_id");
    }
}
