import java.util.ArrayList;
import java.util.List;

public class Hospital {
    private String hospitalId;
    private String name;
    private double latitude;
    private double longitude;
    private String specialization;
    private int availableSlots;

    public Hospital(String hospitalId, String name, double latitude, double longitude,
                     String specialization, int availableSlots) {
        this.hospitalId = hospitalId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.specialization = specialization;
        this.availableSlots = availableSlots;
    }

    public String getHospitalId() { return hospitalId; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getSpecialization() { return specialization; }
    public int getAvailableSlots() { return availableSlots; }

    public void bookSlot() {
        if (availableSlots > 0) {
            availableSlots--;
            System.out.println("Slot booked at " + name + ". Remaining: " + availableSlots);
        } else {
            System.out.println("No slots available at " + name);
        }
    }

    @Override
    public String toString() {
        return name + " | " + specialization + " | Slots: " + availableSlots
                + " | Location: (" + latitude + ", " + longitude + ")";
    }

    // Demo run
    public static void main(String[] args) {
        List<Hospital> hospitals = new ArrayList<>();
        hospitals.add(new Hospital("H001", "City Multispecialty Hospital", 18.5204, 73.8567, "Cardiology", 5));
        hospitals.add(new Hospital("H002", "Sunrise Ortho Center", 18.5300, 73.8400, "Orthopedics", 3));

        System.out.println("--- Registered Hospitals ---");
        for (Hospital h : hospitals) {
            System.out.println(h);
        }

        hospitals.get(0).bookSlot();
    }
}