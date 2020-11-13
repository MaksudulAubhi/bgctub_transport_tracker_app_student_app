package com.example.bgctub_transport_tracker_studentapp.model;

public class ReportFeedback {
    private String userId;
    private String report_feedback_title;
    private String report_feedback_info;

    public ReportFeedback(String userId, String report_feedback_title, String report_feedback_info) {
        this.userId = userId;
        this.report_feedback_title = report_feedback_title;
        this.report_feedback_info = report_feedback_info;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReport_feedback_title() {
        return report_feedback_title;
    }

    public void setReport_feedback_title(String report_feedback_title) {
        this.report_feedback_title = report_feedback_title;
    }

    public String getReport_feedback_info() {
        return report_feedback_info;
    }

    public void setReport_feedback_info(String report_feedback_info) {
        this.report_feedback_info = report_feedback_info;
    }
}
