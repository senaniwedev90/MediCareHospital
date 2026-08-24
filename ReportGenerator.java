public class ReportGenerator {

    private final PatientManager patientManager;
    private final Ward ward;

    public ReportGenerator(PatientManager patientManager, Ward ward) {
        this.patientManager = patientManager;
        this.ward = ward;
    }

    public void displayAllRegisteredPatients() {
        System.out.println("\n===== All Registered Patients =====");
        patientManager.displayAllPatients();
    }

    public void displayAllAvailableBeds() {
        System.out.println("\n===== Available Beds =====");
        if (ward.getAvailableBeds().isEmpty()) {
            System.out.println("No beds available.");
        } else {
            ward.getAvailableBeds().forEach(System.out::println);
        }
    }

    public void displayAllOccupiedBeds() {
        System.out.println("\n===== Occupied Beds =====");
        if (ward.getOccupiedBeds().isEmpty()) {
            System.out.println("No beds currently occupied.");
        } else {
            ward.getOccupiedBeds().forEach(System.out::println);
        }
    }

    public void displaySummary() {
        System.out.println("\n===== Ward Summary Report =====");
        System.out.println("Total registered patients : " + patientManager.getTotalPatientCount());
        System.out.println("Total occupied beds        : " + ward.getTotalOccupiedCount());
        System.out.println("Total available beds       : " + ward.getTotalAvailableCount());
        System.out.printf("Ward occupancy percentage  : %.1f%%%n", ward.getOccupancyPercentage());
    }
}
