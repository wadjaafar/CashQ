package com.tutipay.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QR implements Serializable {
    /*
      "merchant_id": "123456789",
      "merchant_name": "QRCSDNBHD",
      "merchant_bank_id": "123456789",
      "amount": 10,
      "acquirer_id": "501664",
     */
    @SerializedName("merchant_id")
    public String merchantId;

    @SerializedName("merchant_name")
    public String merchantName;

    @SerializedName("merchant_bank_id")
    public String merchantBankId;

    @SerializedName("amount")
    public double amount;

    @SerializedName("acquirer_id")
    public String acquirerId;
}
