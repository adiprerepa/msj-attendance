package com.msj.proxy;

import java.util.ArrayList;

public class AttendanceResponse {

    private boolean status;
    private ArrayList<Student> presentStudents;
    private ArrayList<Student> missingStudents;

    public AttendanceResponse(boolean status, ArrayList<Student> presentStudents, ArrayList<Student> missingStudents) {
        this.status = status;
        this.presentStudents = presentStudents;
        this.missingStudents = missingStudents;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<Student> getPresentStudents() {
        return presentStudents;
    }

    public void setPresentStudents(ArrayList<Student> presentStudents) {
        this.presentStudents = presentStudents;
    }

    public ArrayList<Student> getMissingStudents() {
        return missingStudents;
    }

    public void setMissingStudents(ArrayList<Student> missingStudents) {
        this.missingStudents = missingStudents;
    }

    public static class Student {

        private String id;
        private String name;

        public Student(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
