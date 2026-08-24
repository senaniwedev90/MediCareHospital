import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class HospitalSystemTest {

    private PatientManager patientManager;
    private Ward ward;


    @BeforeEach
    void setUp() {
        patientManager = new PatientManager();
        ward = new Ward();
    }

    // ---------- Feature 1: Patient Management ----------

    @Test
    void registerPatient_addsPatientSuccessfully() {
        Patient p = new Patient("P001", "John", "Doe", 40, "Male", "Flu", PatientCategory.OUTPATIENT);
        patientManager.registerPatient(p);

        assertEquals(1, patientManager.getTotalPatientCount());
        assertEquals("P001", patientManager.findByID("P001").getPatientID());
    }

    @Test
    void registerPatient_duplicateID_throwsException() {
        Patient p1 = new Patient("P001", "John", "Doe", 40, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Jane", "Smith", 25, "Female", "Cold", PatientCategory.EMERGENCY);
        patientManager.registerPatient(p1);

        assertThrows(IllegalArgumentException.class, () -> patientManager.registerPatient(p2));
        assertEquals(1, patientManager.getTotalPatientCount()); // second patient must NOT have been added
    }

    @Test
    void findByID_returnsCorrectPatient() {
        Patient p = new Patient("P010", "Alice", "Nkosi", 33, "Female", "Migraine", PatientCategory.OUTPATIENT);
        patientManager.registerPatient(p);

        Patient found = patientManager.findByID("P010");

        assertNotNull(found);
        assertEquals("Nkosi", found.getLastName());
    }

    @Test
    void findByID_unknownID_returnsNull() {
        assertNull(patientManager.findByID("DOES-NOT-EXIST"));
    }

    @Test
    void updatePatient_changesAreReflected() {
        Patient p = new Patient("P020", "Sam", "Old", 50, "Male", "Cough", PatientCategory.OUTPATIENT);
        patientManager.registerPatient(p);

        Patient found = patientManager.findByID("P020");
        found.setLastName("New");
        found.setAge(51);

        assertEquals("New", patientManager.findByID("P020").getLastName());
        assertEquals(51, patientManager.findByID("P020").getAge());
    }

    @Test
    void deletePatient_removesPatient() {
        Patient p = new Patient("P030", "Bob", "Marley", 60, "Male", "Checkup", PatientCategory.OUTPATIENT);
        patientManager.registerPatient(p);

        boolean removed = patientManager.deletePatient("P030");

        assertTrue(removed);
        assertNull(patientManager.findByID("P030"));
        assertEquals(0, patientManager.getTotalPatientCount());
    }

    @Test
    void deletePatient_unknownID_returnsFalse() {
        assertFalse(patientManager.deletePatient("GHOST"));
    }

    @Test
    void sortPatientsBySurname_ordersAlphabetically() {
        patientManager.registerPatient(new Patient("P1", "A", "Zulu", 20, "M", "-", PatientCategory.OUTPATIENT));
        patientManager.registerPatient(new Patient("P2", "B", "Adams", 20, "M", "-", PatientCategory.OUTPATIENT));
        patientManager.registerPatient(new Patient("P3", "C", "Mokoena", 20, "M", "-", PatientCategory.OUTPATIENT));

        List<Patient> sorted = patientManager.getPatientsSortedBySurname();

        assertEquals("Adams", sorted.get(0).getLastName());
        assertEquals("Mokoena", sorted.get(1).getLastName());
        assertEquals("Zulu", sorted.get(2).getLastName());
    }

    @Test
    void sortPatientsByID_ordersByPatientID() {
        patientManager.registerPatient(new Patient("P3", "C", "X", 20, "M", "-", PatientCategory.OUTPATIENT));
        patientManager.registerPatient(new Patient("P1", "A", "Y", 20, "M", "-", PatientCategory.OUTPATIENT));
        patientManager.registerPatient(new Patient("P2", "B", "Z", 20, "M", "-", PatientCategory.OUTPATIENT));

        List<Patient> sorted = patientManager.getPatientsSortedByID();

        assertEquals("P1", sorted.get(0).getPatientID());
        assertEquals("P2", sorted.get(1).getPatientID());
        assertEquals("P3", sorted.get(2).getPatientID());
    }

    // ---------- Feature 2: Bed Management ----------

    @Test
    void allocateBed_assignsFirstAvailableBed() {
        Inpatient inpatient = new Inpatient("P100", "Thabo", "Mahlangu", 45, "Male", "Surgery", "Ward-1");

        Bed bed = ward.allocateBed(inpatient);

        assertNotNull(bed);
        assertEquals("B01", bed.getBedNumber());
        assertTrue(bed.isOccupied());
        assertEquals("B01", inpatient.getBedNumber());
    }

    @Test
    void allocateBed_cannotAllocateAlreadyOccupiedBed() {
        Inpatient first = new Inpatient("P100", "Thabo", "Mahlangu", 45, "Male", "Surgery", "Ward-1");
        ward.allocateBed(first); // takes B01


        Bed occupiedBed = ward.findBedByNumber("B01");
        assertTrue(occupiedBed.isOccupied());


        Inpatient second = new Inpatient("P101", "Nomsa", "Dube", 29, "Female", "Fracture", "Ward-1");
        Bed secondBed = ward.allocateBed(second);

        assertNotEquals("B01", secondBed.getBedNumber());
    }

    @Test
    void allocateBed_wardFull_returnsNull() {

        for (int i = 1; i <= Ward.TOTAL_BEDS; i++) {
            Inpatient inpatient = new Inpatient("P" + i, "First" + i, "Last" + i, 30, "Male", "Cond", "Ward-1");
            assertNotNull(ward.allocateBed(inpatient));
        }


        Inpatient extra = new Inpatient("P999", "Extra", "Patient", 30, "Male", "Cond", "Ward-1");
        Bed result = ward.allocateBed(extra);

        assertNull(result);
        assertEquals(Ward.TOTAL_BEDS, ward.getTotalOccupiedCount());
    }

    @Test
    void releaseBed_freesUpTheBed() {
        Inpatient inpatient = new Inpatient("P200", "Lerato", "Khumalo", 38, "Female", "Observation", "Ward-1");
        ward.allocateBed(inpatient);

        boolean released = ward.releaseBedForPatient("P200");

        assertTrue(released);
        assertFalse(ward.findBedByNumber(inpatient.getBedNumber()) == null);
        assertEquals(Ward.TOTAL_BEDS, ward.getTotalAvailableCount());
    }

    @Test
    void releaseBed_patientWithoutBed_returnsFalse() {
        assertFalse(ward.releaseBedForPatient("NO-SUCH-PATIENT"));
    }

    @Test
    void wardOccupancyPercentage_calculatesCorrectly() {
        // 5 out of 20 beds occupied = 25%
        for (int i = 1; i <= 5; i++) {
            Inpatient inpatient = new Inpatient("P" + i, "First" + i, "Last" + i, 30, "Male", "Cond", "Ward-1");
            ward.allocateBed(inpatient);
        }

        assertEquals(25.0, ward.getOccupancyPercentage(), 0.001);
    }

    // ---------- Feature 4: Inheritance ----------

    @Test
    void inpatient_inheritsFromPatient_andHasExtraFields() {
        Inpatient inpatient = new Inpatient("P300", "Sipho", "Ndlovu", 55, "Male", "Diabetes", "Ward-1");

        assertTrue(inpatient instanceof Patient); // confirms the inheritance relationship
        assertEquals(PatientCategory.INPATIENT, inpatient.getCategory());
        assertEquals("Ward-1", inpatient.getWardNumber());
        assertNull(inpatient.getBedNumber()); // no bed allocated yet
    }
}
