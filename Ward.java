import java.util.ArrayList;
import java.util.List;

public class Ward {

    public static final int ROWS = 4;
    public static final int COLUMNS = 5;
    public static final int TOTAL_BEDS = ROWS * COLUMNS; // 20

    private final Bed[][] beds;

    public Ward() {
        beds = new Bed[ROWS][COLUMNS];
        int bedCounter = 1;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                
                String bedNumber = String.format("B%02d", bedCounter);
                beds[row][col] = new Bed(bedNumber);
                bedCounter++;
            }
        }
    }

    public Bed findAvailableBed() {
        for (Bed[] row : beds) {
            for (Bed bed : row) {
                if (!bed.isOccupied()) {
                    return bed;
                }
            }
        }
        return null;
    }

    public Bed findBedByNumber(String bedNumber) {
        for (Bed[] row : beds) {
            for (Bed bed : row) {
                if (bed.getBedNumber().equalsIgnoreCase(bedNumber)) {
                    return bed;
                }
            }
        }
        return null;
    }

    public Bed findBedByPatientID(String patientID) {
        for (Bed[] row : beds) {
            for (Bed bed : row) {
                if (bed.isOccupied() && bed.getOccupiedByPatientID().equals(patientID)) {
                    return bed;
                }
            }
        }
        return null;
    }

    public Bed allocateBed(Inpatient inpatient) {
        Bed bed = findAvailableBed();
        if (bed == null) {
            return null;
        }
        bed.allocate(inpatient.getPatientID());
        inpatient.setBedNumber(bed.getBedNumber());
        return bed;
    }

    public boolean releaseBedForPatient(String patientID) {
        Bed bed = findBedByPatientID(patientID);
        if (bed == null) {
            return false;
        }
        bed.release();
        return true;
    }

    public void displayWardLayout() {
        System.out.println("\n===== Ward Layout (4x5) =====");
        for (Bed[] row : beds) {
            StringBuilder line = new StringBuilder();
            for (Bed bed : row) {
                String status = bed.isOccupied() ? "[X]" : "[ ]";
                line.append(bed.getBedNumber()).append(status).append("  ");
            }
            System.out.println(line.toString().trim());
        }
        System.out.println("([ ] = available, [X] = occupied)");
    }

    public List<Bed> getAvailableBeds() {
        List<Bed> available = new ArrayList<>();
        for (Bed[] row : beds) {
            for (Bed bed : row) {
                if (!bed.isOccupied()) {
                    available.add(bed);
                }
            }
        }
        return available;
    }

    public List<Bed> getOccupiedBeds() {
        List<Bed> occupied = new ArrayList<>();
        for (Bed[] row : beds) {
            for (Bed bed : row) {
                if (bed.isOccupied()) {
                    occupied.add(bed);
                }
            }
        }
        return occupied;
    }

    public int getTotalOccupiedCount() {
        return getOccupiedBeds().size();
    }

    public int getTotalAvailableCount() {
        return TOTAL_BEDS - getTotalOccupiedCount();
    }

    public double getOccupancyPercentage() {
        return (getTotalOccupiedCount() * 100.0) / TOTAL_BEDS;
    }
}
