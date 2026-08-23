package com.medvault.dto;

public class AppointmentDtos {

    public static class BookRequest {
        public String patientUsername;
        public Long hospitalId;
        public String appointmentTime; // ISO format: "2026-08-25T10:30:00"
        public String reason;
    }

    public static class HospitalRequest {
        public String name;
        public Double lat;
        public Double lng;
        public String specialization;
        public Integer slots;
        public String address;
        public String phone;
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}