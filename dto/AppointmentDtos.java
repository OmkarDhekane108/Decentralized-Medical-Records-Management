package com.medvault.dto;

public class AppointmentDtos {

    public static class BookRequest {
        public String patientUsername;
        public Long slotId;
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

    public static class DoctorResponse {
        public String username;
        public String fullName;
        public String specialization;

        public DoctorResponse(String username, String fullName, String specialization) {
            this.username = username;
            this.fullName = fullName;
            this.specialization = specialization;
        }
    }

    public static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}