package net.soluspay;

public class Payment {
    public String apiKey;
    public String pan;
    public String expDate;

    public Payment(String apiKey, String pan, String pin, String expDate) {
        this.apiKey = apiKey;
        this.pan = pan;
        this.expDate = expDate;
    }

    public NewClient() {

    }
}
