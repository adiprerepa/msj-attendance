package com.msj.attendance.database.reference;

import com.msj.attendance.database.BaseDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
        System.out.println(String.format("Inserting reference: room %s fingerid %s studentid %s\n", room, fingerId, studentId));
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
        System.out.println(String.format("Looking up student Id with room %s and fingerid %s\n", room, fingerId));
        Statement statement = super.connection.createStatement();
        String execStatement = String.format("select * from %s where `%s`=%s and `%s`=%s;", tableName, "room_id",
                room, "finger_id", fingerId);
        ResultSet set = statement.executeQuery(execStatement);
        return set.getString("student_id");
    }

    /**
     * Lookup student's name from table of Ids.
     * Note: This is located in a different table: "idMap".
     * Schema can be found in {@see create_map.sql}
     * todo check if name is null in calling function.
     * @param studentId student's Id
     * @return student name
     */
    public String lookupStudentName(String studentId) {
        System.out.println("Looking up student Name: " + studentId);
        String mapTableName = "idMap";
        try {
            Statement statement = super.connection.createStatement();
            String execStatement = String.format("select * from %s where `%s`=%s;", mapTableName, "studentId", studentId);
            ResultSet set = statement.executeQuery(execStatement);
            set.next();
            return set.getString("studentName");
        } catch (SQLException e) {
            System.out.println("Returning null in lookupStudentName()");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Given a set of students present, cross-reference with
     * database to find missing students, and return list of missing students ids.
     * @param presentStudents students who scanned in
     * @return missing students ids.
     */
    public ArrayList<String> getMissingStudents(String room, ArrayList<String> presentStudents) {
        System.out.println("Getting Missing Students for " + room);
        ArrayList<String> referenceStudents = new ArrayList<>();
        try {
            Statement statement = super.connection.createStatement();
            String execStatement = String.format("select * from %s where `%s`='%s';", tableName, "room_id", room);
            System.out.println(execStatement);
            ResultSet set = statement.executeQuery(execStatement);
            while (set.next()) {
                referenceStudents.add(set.getString("student_id"));
            }
            System.out.println("Got reference size of " + referenceStudents.size());
        } catch (SQLException e) {
            System.out.println("sql error in getMissingStudents()");
            e.printStackTrace();
        }
        // remove all the present students from the reference list to get the missing.
        referenceStudents.removeAll(presentStudents);
        return referenceStudents;
    }
}
