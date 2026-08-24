public class Inpatient extends Patient {

    private String wardNumber;
    private String bedNumber; //example. B07 - set when a bed is allocated, nothing until then

    public Inpatient(String patientID, String firstName, String lastName, int age,
                      String gender, String medicalCondition, String wardNumber) {
        super(patientID, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        this.wardNumber = wardNumber;
        this.bedNumber = null; 
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    
    //Overrides Patieny display and also prints ward and bed information.
    @Override
    public void displayDetails() {
        super.displayDetails(); // reuse the base class printing first
        System.out.println("Ward Number      : " + wardNumber);
        System.out.println("Bed Number       : " + (bedNumber == null ? "Not allocated" : bedNumber));
    }
}
