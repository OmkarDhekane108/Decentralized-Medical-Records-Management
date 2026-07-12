public class Patient {
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String walletAddress;      // blockchain identity
    private String medicalRecordHash;  // IPFS hash reference

    public Patient(String patientId, String name, int age, String gender,
                   String contactNumber, String walletAddress) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.walletAddress = walletAddress;
        this.medicalRecordHash = "";
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getMedicalRecordHash() {
        return medicalRecordHash;
    }

    public void setMedicalRecordHash(String medicalRecordHash) {
        this.medicalRecordHash = medicalRecordHash;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", medicalRecordHash='" + medicalRecordHash + '\'' +
                '}';
    }
}