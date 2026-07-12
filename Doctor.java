public class Doctor {
    private String doctorId;
    private String name;
    private String specialization;
    private String contactNumber;
    private String walletAddress;   // blockchain identity
    private String hospitalName;

    public Doctor(String doctorId, String name, String specialization,
                  String contactNumber, String walletAddress, String hospitalName) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.walletAddress = walletAddress;
        this.hospitalName = hospitalName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId='" + doctorId + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                '}';
    }
}