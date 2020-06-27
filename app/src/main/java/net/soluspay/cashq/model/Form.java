package net.soluspay.cashq.model;

/**
 * Created by Mohamed Jaafar on 14/02/19.
 */
public class Form {

    private int id;
    private String fees;
    private String admissionType;

    public Form(){

    }

    public Form(int id, String fees, String admissionType) {
        this.id = id;
        this.fees = fees;
        this.admissionType = admissionType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return admissionType;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Form){
            Form f = (Form) obj;
            return f.getAdmissionType().equals(admissionType) && f.getFees() == fees;
        }

        return false;
    }

}
