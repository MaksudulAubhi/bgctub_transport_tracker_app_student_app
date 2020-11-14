package com.example.bgctub_transport_tracker_studentapp.model;

public class StudentInformation {
    private String user_id,name,id,gender,department,email,bus_stoppage,program,semester;

    public StudentInformation(String user_id,String name, String id, String gender, String department, String email,String bus_stoppage,String program,String semester) {
        this.user_id=user_id;
        this.name = name;
        this.id = id;
        this.gender = gender;
        this.department = department;
        this.email = email;
        this.bus_stoppage=bus_stoppage;
        this.program=program;
        this.semester=semester;
    }

    public String getBus_stoppage() {
        return bus_stoppage;
    }

    public void setBus_stoppage(String bus_stoppage) {
        this.bus_stoppage = bus_stoppage;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }
}
