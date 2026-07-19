// Referral tracking fields
    private String referredToHospitalId = null;
    private String referredByDoctor = null;
    private String referralStatus = "Not Referred"; // Not Referred / Pending / Accepted / Completed
    private String referralDate = null;

    public void referToHospital(String hospitalId, String doctorName, String date) {
        this.referredToHospitalId = hospitalId;
        this.referredByDoctor = doctorName;
        this.referralStatus = "Pending";
        this.referralDate = date;
        System.out.println("Record referred to hospital: " + hospitalId + " by " + doctorName);
    }

    public String getReferralStatus() { return referralStatus; }
    public String getReferredToHospitalId() { return referredToHospitalId; }