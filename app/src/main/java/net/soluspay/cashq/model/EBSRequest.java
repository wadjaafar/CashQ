package net.soluspay.cashq.model;

import com.google.gson.annotations.SerializedName;

import org.apache.http.client.utils.URIBuilder;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EBSRequest implements Serializable {

    @SerializedName("UUID")
    private final String uuid = generateUUID();
    private final String tranDateTime = getDate();
    private final String applicationId = "ACTSCon";

    @SerializedName("PAN")
    private String pan;

    private String expDate, IPIN, newIPIN, originalTranUUID, otp, ipin,  entityId, voucherNumber;
    private Float tranAmount;
    private String tranCurrencyCode;
    private String toCard;
    private String toAccount;
    private String payeeId;
    private String paymentInfo;
    private String serviceProviderId;
    private String merchantID;

    private String phoneNo;

    private String phoneNumber;

    @SerializedName("pan")
    private String otherPan;

    private final String entityType = "Phone No";
    private final String entityGroup = "1";
    private final String registrationType = "01";



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
        return uuid.toString();
    }

    public String serverUrl(boolean development) {
        String host;

        if (development) {
            host = "beta.soluspay.sd/consumer/";
        } else {
            host = "beta.soluspay.sd/api/consumer/";
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
        String host = "beta.soluspay.sd/api/consumer/";
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

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public String getOriginalTranUUID() {
        return originalTranUUID;
    }

    public void setOriginalTranUUID(String originalTranUUID) {
        this.originalTranUUID = originalTranUUID;
    }

    public String getOtherPan() {
        return otherPan;
    }

    public void setOtherPan(String otherPan) {
        this.otherPan = otherPan;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
