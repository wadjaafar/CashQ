package com.tutipay.app.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mohamed Jaafar on 1/17/2019.
 */

public class Transaction {

    private int systemTraceAuditNumber;
    private String tranDateTime;
    private String terminalId;
    private String clientId;
    private String pan, expDate, pin;
    private Float tranAmount;
    private String tranCurrencyCode;

    public Transaction(TransactionBuilder builder) {

        this.systemTraceAuditNumber = builder.systemTraceAuditNumber;
//        this.tranDateTime = getDate();
//        this.terminalId = Constants.TERMINAL_ID;
//        this.clientId = Constants.CLIENT_ID;
        this.pan = builder.pan;
        this.expDate = builder.expDate;
        this.pin = builder.pin;
        this.tranAmount = builder.tranAmount;
        this.tranCurrencyCode = builder.tranCurrencyCode;

    }

    public static class TransactionBuilder {

        private int systemTraceAuditNumber;
        private String tranDateTime;
        private String terminalId;
        private String clientId;
        private String pan, expDate, pin;
        private Float tranAmount;
        private String tranCurrencyCode;

        public TransactionBuilder stan(int stan){
            this.systemTraceAuditNumber = stan;
            return this;
        }


//        public TransactionBuilder date(String date){
//            this.tranDateTime = date;
//            return this;
//        }


//        public TransactionBuilder terminalId(String terminalId){
//            this.terminalId = terminalId;
//            return this;
//        }
//
//        public TransactionBuilder clientId(String clientId){
//            this.clientId = clientId;
//            return this;
//        }

        public TransactionBuilder pan(String pan){
            this.pan = pan;
            return this;
        }

        public TransactionBuilder expDate(String expDate){
            this.expDate = expDate;
            return this;
        }

        public TransactionBuilder pin(String pin){
            this.pin = pin;
            return this;
        }

        public TransactionBuilder amount(float amount) {
            this.tranAmount = amount;
            return this;

        }

        public TransactionBuilder currency(String currency){
            this.tranCurrencyCode = currency;
            return this;
        }


        public Transaction build(){
            return new Transaction(this);
        }


    }

    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
