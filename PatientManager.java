import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//Handles registering, searching, updating, deleting and sorting patients.
public class PatientManager {

    private final List<Patient> patients = new ArrayList<>();

    public void registerPatient(Patient patient) {
        if (findByID(patient.getPatientID()) != null) {
            throw new IllegalArgumentException(
                    "A patient with ID " + patient.getPatientID() + " already exists.");
        }
        patients.add(patient);
    }

    public Patient findByID(String patientID) {
        for (Patient p : patients) {
            if (p.getPatientID().equalsIgnoreCase(patientID)) {
                return p;
            }
        }
        return null;
    }

    public boolean deletePatient(String patientID) {
        Patient p = findByID(patientID);
        if (p == null) {
            return false;
        }
        patients.remove(p);
        return true;
    }

    public List<Patient> getAllPatients() {
        return patients;
    }

    public int getTotalPatientCount() {
        return patients.size();
    }

    public List<Patient> getPatientsSortedBySurname() {
        List<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getLastName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public List<Patient> getPatientsSortedByID() {
        List<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getPatientID, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
            return;
        }
        for (Patient p : patients) {
            p.displayDetails();
        }
    }
}
