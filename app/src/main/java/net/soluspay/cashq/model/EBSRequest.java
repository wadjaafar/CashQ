package net.soluspay.cashq.model;

import org.apache.http.client.utils.URIBuilder;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EBSRequest implements Serializable {

    private final String uuid = generateUUID();
    private final String tranDateTime = getDate();
    private final String applicationId = "ACTSCon";
    private String pan, expDate, IPIN, newIPIN, otp, ipin, phoneNumber;
    private Float tranAmount;
    private String tranCurrencyCode;
    private String toCard;
    private String toAccount;
    private String payeeId;
    private String paymentInfo;
    private String serviceProviderId;
    private String merchantID;


    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public void setIPIN(String IPIN) {
        this.IPIN = IPIN;
    }


    public void setMerchantID(String id) {
        this.merchantID = id;
    }


    public void setNewIPIN(String newIPIN) {
        this.newIPIN = newIPIN;
    }

    public void setToCard(String toCard) {
        this.toCard = toCard;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public void setTranAmount(Float tranAmount) {
        this.tranAmount = tranAmount;
    }

    public void setTranCurrencyCode(String tranCurrencyCode) {
        this.tranCurrencyCode = tranCurrencyCode;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String generateUUID(){
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }

    public String serverUrl(boolean development) {
        String host;

        if (development) {
            host = "beta.soluspay.net/consumer/";
        } else {
            host = "beta.soluspay.net/api/consumer/";
        }

        URIBuilder builder = new URIBuilder();
        try {
            // how to handle https ones?
            // url is: https://beta.soluspay.net/api/
            builder.setScheme("https")
                    .setHost(host)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public String serverUrl() {
        String host = "beta.soluspay.net/api/consumer/";
        URIBuilder builder = new URIBuilder();
        try {
            // how to handle https ones?
            // url is: https://beta.soluspay.net/api/
            builder.setScheme("https")
                    .setHost(host)
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getIpin() {
        return ipin;
    }

    public void setIpin(String ipin) {
        this.ipin = ipin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
