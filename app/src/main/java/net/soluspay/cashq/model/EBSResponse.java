package net.soluspay.cashq.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class EBSResponse implements Serializable {

    private String responseMessage;
    private String responseStatus;
    private Integer responseCode;
    private String tranDateTime;
    private String terminalId;
    private Integer systemTraceAuditNumber;
    private String clientId;
    private String PAN;
    private Float tranAmount;
    private String EBSServiceName;
    private String workingKey;
    private String toCard;
    private String toAccount;
    private String referenceNumber;
    private String approvalCode;
    private Float tranFee;
    private Float additionalAmount;
    private Float acqTranFee;
    private Float issuerTranFee;
    private String pubKeyValue;
    private String tranCurrency;
    private String paymentInfo;

    private String message;
    private String code;

    private HashMap<String, Double> balance;

    private HashMap<String, String> billInfo;

    public HashMap<String, String> getBillInfo() {
        return billInfo;
    }

    public HashMap<String, Double> getBalance() {
        return balance;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public String getToAccount() {
        return toAccount;
    }

    public String getTranCurrency() {
        return tranCurrency;
    }

    public Float getAcqTranFee() {
        return acqTranFee;
    }

    public Float getIssuerTranFee() {
        return issuerTranFee;
    }

    public String getPubKeyValue() {
        return pubKeyValue;
    }

    public Float getAdditionalAmount() {
        return additionalAmount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getApprovalCode() {
        return approvalCode;
    }

    public Float getTranFee() {
        return tranFee;
    }

    public String getToCard() {
        return toCard;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getTranDateTime() {
            Date newDate = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                newDate = dateFormat.parse(tranDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return dateFormat.format(newDate);
    }

    public String getTerminalId() {
        return terminalId;
    }

    public Integer getSystemTraceAuditNumber() {
        return systemTraceAuditNumber;
    }

    public String getClientId() {
        return clientId;
    }

    public String getPAN() {
        return PAN;
    }

    public Float getTranAmount() {
        return tranAmount;
    }

    public String getEBSServiceName() {
        return EBSServiceName;
    }

    public String getWorkingKey() {
        return workingKey;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
