import java.util.List;
import java.util.Scanner;

//Entry point for MediCare Hospital Patient Admission System.
public class HospitalSystem {

    private static final PatientManager patientManager = new PatientManager();
    private static final Ward ward = new Ward();
    private static final ReportGenerator reportGenerator = new ReportGenerator(patientManager, ward);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: registerPatient(); break;
                case 2: searchPatient(); break;
                case 3: updatePatient(); break;
                case 4: deletePatient(); break;
                case 5: patientManager.displayAllPatients(); break;
                case 6: allocateBed(); break;
                case 7: releaseBed(); break;
                case 8: ward.displayWardLayout(); break;
                case 9: reportGenerator.displayAllAvailableBeds(); break;
                case 10: reportGenerator.displayAllOccupiedBeds(); break;
                case 11: showReportsMenu(); break;
                case 12: sortPatients(); break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n========= MediCare Hospital Patient Admission System =========");
        System.out.println(" 1. Register a new patient");
        System.out.println(" 2. Search for a patient (by Patient ID)");
        System.out.println(" 3. Update patient details");
        System.out.println(" 4. Delete a patient");
        System.out.println(" 5. Display all registered patients");
        System.out.println(" 6. Allocate a bed to an inpatient");
        System.out.println(" 7. Release a bed (discharge)");
        System.out.println(" 8. Display ward layout");
        System.out.println(" 9. Display available beds");
        System.out.println("10. Display occupied beds");
        System.out.println("11. Reports menu");
        System.out.println("12. Sort patients (surname / Patient ID)");
        System.out.println(" 0. Exit");
    }

    // ---------------- Feature 1: Patient Management ----------------

    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readLine("Patient ID: ");
        if (patientManager.findByID(id) != null) {
            System.out.println("Error: a patient with that ID already exists.");
            return;
        }
        String firstName = readLine("First Name: ");
        String lastName = readLine("Last Name: ");
        int age = readInt("Age: ");
        String gender = readLine("Gender: ");
        String condition = readLine("Medical Condition: ");
        PatientCategory category = readCategory();

        try {
            if (category == PatientCategory.INPATIENT) {
                String wardNumber = readLine("Ward Number (e.g. Ward-1): ");
                Inpatient inpatient = new Inpatient(id, firstName, lastName, age, gender, condition, wardNumber);
                patientManager.registerPatient(inpatient);
                System.out.println("Inpatient registered successfully. No bed allocated yet - use option 6.");
            } else {
                Patient patient = new Patient(id, firstName, lastName, age, gender, condition, category);
                patientManager.registerPatient(patient);
                System.out.println("Patient registered successfully.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchPatient() {
        String id = readLine("\nEnter Patient ID to search: ");
        Patient p = patientManager.findByID(id);
        if (p == null) {
            System.out.println("No patient found with ID " + id);
        } else {
            p.displayDetails();
        }
    }

    private static void updatePatient() {
        String id = readLine("\nEnter Patient ID to update: ");
        Patient p = patientManager.findByID(id);
        if (p == null) {
            System.out.println("No patient found with ID " + id);
            return;
        }
        System.out.println("Leave a field blank to keep its current value.");

        String firstName = readLine("First Name [" + p.getFirstName() + "]: ");
        if (!firstName.isBlank()) p.setFirstName(firstName);

        String lastName = readLine("Last Name [" + p.getLastName() + "]: ");
        if (!lastName.isBlank()) p.setLastName(lastName);

        String ageStr = readLine("Age [" + p.getAge() + "]: ");
        if (!ageStr.isBlank()) {
            try {
                p.setAge(Integer.parseInt(ageStr));
            } catch (NumberFormatException e) {
                System.out.println("Invalid age - keeping previous value.");
            }
        }

        String gender = readLine("Gender [" + p.getGender() + "]: ");
        if (!gender.isBlank()) p.setGender(gender);

        String condition = readLine("Medical Condition [" + p.getMedicalCondition() + "]: ");
        if (!condition.isBlank()) p.setMedicalCondition(condition);

        System.out.println("Patient updated successfully.");
    }

    private static void deletePatient() {
        String id = readLine("\nEnter Patient ID to delete: ");
        //If they were an inpatient with a bed free it too will be in the ward stays consistent.
        ward.releaseBedForPatient(id);
        boolean removed = patientManager.deletePatient(id);
        System.out.println(removed ? "Patient deleted." : "No patient found with ID " + id);
    }

    // ---------------- Feature 2: Bed Management ----------------

    private static void allocateBed() {
        String id = readLine("\nEnter Patient ID to allocate a bed to: ");
        Patient p = patientManager.findByID(id);
        if (p == null) {
            System.out.println("No patient found with ID " + id);
            return;
        }
        if (!(p instanceof Inpatient)) {
            System.out.println("Error: only Inpatients may be allocated a hospital bed.");
            return;
        }
        Inpatient inpatient = (Inpatient) p;
        if (inpatient.getBedNumber() != null) {
            System.out.println("This patient already has bed " + inpatient.getBedNumber());
            return;
        }
        Bed bed = ward.allocateBed(inpatient);
        if (bed == null) {
            System.out.println("Error: no beds are available.");
        } else {
            System.out.println("Bed " + bed.getBedNumber() + " allocated to " + inpatient.getFirstName()
                    + " " + inpatient.getLastName());
        }
    }

    private static void releaseBed() {
        String id = readLine("\nEnter Patient ID to release bed for (discharge): ");
        Patient p = patientManager.findByID(id);
        if (p instanceof Inpatient) {
            ((Inpatient) p).setBedNumber(null);
        }
        boolean released = ward.releaseBedForPatient(id);
        System.out.println(released ? "Bed released." : "That patient does not currently occupy a bed.");
    }

    // ---------------- Feature 3: Reports ----------------

    private static void showReportsMenu() {
        System.out.println("\n-- Reports --");
        System.out.println("1. All registered patients");
        System.out.println("2. All available beds");
        System.out.println("3. All occupied beds");
        System.out.println("4. Summary (totals + occupancy %)");
        int choice = readInt("Choose a report: ");
        switch (choice) {
            case 1: reportGenerator.displayAllRegisteredPatients(); break;
            case 2: reportGenerator.displayAllAvailableBeds(); break;
            case 3: reportGenerator.displayAllOccupiedBeds(); break;
            case 4: reportGenerator.displaySummary(); break;
            default: System.out.println("Invalid choice.");
        }
    }

    // ---------------- Feature 5 support: sorting ----------------

    private static void sortPatients() {
        System.out.println("\nSort by: 1) Surname  2) Patient ID");
        int choice = readInt("Choice: ");
        List<Patient> sorted = choice == 2
                ? patientManager.getPatientsSortedByID()
                : patientManager.getPatientsSortedBySurname();

        if (sorted.isEmpty()) {
            System.out.println("No patients registered yet.");
        } else {
            sorted.forEach(Patient::displayDetails);
        }
    }

    // ---------------- Input help ----------------

    private static PatientCategory readCategory() {
        while (true) {
            System.out.println("Patient Category: 1) Inpatient  2) Outpatient  3) Emergency");
            int choice = readInt("Choice: ");
            switch (choice) {
                case 1: return PatientCategory.INPATIENT;
                case 2: return PatientCategory.OUTPATIENT;
                case 3: return PatientCategory.EMERGENCY;
                default: System.out.println("Invalid choice, try again.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
