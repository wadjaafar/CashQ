package com.tutipay.app.utils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CardsData {
    @PrimaryKey
    public int _ID;

    @ColumnInfo(name = "pan")
    public String pan;

    @ColumnInfo(name = "exp_date")
    public String expDate;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "count")
    public String count;

}


