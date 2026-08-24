//Represents a single bed in the 20 bed ward (as in assignment layout: B01..B20).
//Keeps track of whether it is occupied and by which patient ID.

public class Bed {

    private final String bedNumber; //example B01 - fixed for the beds lifetime
    private boolean occupied;
    private String occupiedByPatientID; 

    public Bed(String bedNumber) {
        this.bedNumber = bedNumber;
        this.occupied = false;
        this.occupiedByPatientID = null;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public String getOccupiedByPatientID() {
        return occupiedByPatientID;
    }

    //Marks this bed as occupied by the given patient.
    public void allocate(String patientID) {
        this.occupied = true;
        this.occupiedByPatientID = patientID;
    }

    //Frees this bed up again.
    public void release() {
        this.occupied = false;
        this.occupiedByPatientID = null;
    }

    @Override
    public String toString() {
        return occupied
                ? bedNumber + " [Occupied - Patient " + occupiedByPatientID + "]"
                : bedNumber + " [Available]";
    }
}
