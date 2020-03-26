package net.soluspay.cashq.model;

import net.soluspay.cashq.utils.CardDBHelper;
import net.soluspay.cashq.utils.CardDBManager;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Created by jaafaralmahy on 1/13/19.
 */

public class Card implements Serializable {



    private String name, pan, expDate, ipin;
    private int id;

    public Card() {
    }


    public Card(int id, String name, String pan, String expDate) {
        this.name = name;
        this.pan = pan;
        this.expDate = expDate;
    }

    public Card(String name, String pan, String expDate) {
        this.name = name;
        this.pan = pan;
        this.expDate = expDate;
    }

    public Card(String name, String pan, String expDate, String ipin) {
        this.name = name;
        this.pan = pan;
        this.expDate = expDate;
        this.ipin = ipin;
    }

    public Card(String name, String pan, String expDate, String ipin, int id) {
        this.name = name;
        this.pan = pan;
        this.expDate = expDate;
        this.ipin = ipin;
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpin() {
        return ipin;
    }

    public void setIpin(String ipin) {
        this.ipin = ipin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getMaskerPan(){
        return maskNumber(getPan());
    }

    public String getMaskedExpdate(){
        return maskExpdate(expDate);
    }

    public static String maskNumber(final String creditCardNumber) {
        final String s = creditCardNumber.replaceAll("\\D", "");
        final char MASK_CHAR = '*';
        final int start = 6;
        final int end = s.length() - 4;
        final String overlay = StringUtils.repeat(MASK_CHAR, end - start);

        return StringUtils.overlay(s, overlay, start, end);
    }

    public static String maskExpdate(final String creditCardExpdate){

        String year = creditCardExpdate.substring(0,2);
        String month = creditCardExpdate.substring(2,4);
        String maskedExpdate = month + "/" + year;

        return maskedExpdate;

    }

}
