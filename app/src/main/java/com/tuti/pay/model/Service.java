package com.tutipay.app.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {

    private int id;
    private String name;
    private double fees;
    private String amount;


    protected Service(Parcel in) {
        id = in.readInt();
        name = in.readString();
        fees = in.readDouble();
        amount = in.readString();
    }

    public Service(int id, String name, double fees, String amount) {
        this.id = id;
        this.name = name;
        this.fees = fees;
        this.amount = amount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(fees);
        dest.writeString(amount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
