package net.soluspay;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Request implements Serializable {

    public Request(String pan, String expDate, String IPIN, Float tranAmount) {
        this.pan = pan;
        this.expDate = expDate;
        this.IPIN = IPIN;
        this.tranAmount = tranAmount;
    }

    private final String uuid = generateUUID();
    private final String tranDateTime = getDate();
    private final String applicationId = "ACTSCon";
    private String pan, expDate, IPIN, newIPIN, otp, ipin, phoneNumber;
    private Float tranAmount;
    private String tranCurrencyCode;
    private String payeeId;
    private String paymentInfo;
    private String serviceProviderId;
    private String merchantID;

    public ResponseData NewRequest() {
        Request request = new Request();

        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(this.IPIN, key, request.getUuid());
        request.setServiceProviderId(Constants.CONTRIBUTE_SUDAN); // it is better not to use it this way
        request.setTranAmount(Float.parseFloat(amount.getText().toString()));
        request.setTranCurrencyCode("SDG");
        request.setPan(this.pan);
        request.setExpDate(this.expDate);
        request.setIPIN(encryptedIPIN);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        Log.i("MY REQUEST", json);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl())
                .addJSONObjectBody(object) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", this.apiKey)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {

                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.i("Smoke Response", response.toString());
                        if (response != null) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Response>() {
                            }.getType();
                            EBSResponse result = null;
                            try {
                                result = gson.fromJson(response.get("ebs_response").toString(), type);
                                Log.i("MY Response", response.toString());
                                return result;

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("Purchase Error", String.valueOf(error.getErrorBody()));
                        if (error.getErrorCode() == 504){
                            // handle the error here!
                       }
                        Gson gson = new Gson();
                        Type type = new TypeToken<Response>() {
                        }.getType();
                        EBSResponse result = null;
                        try {
                            JSONObject obj = new JSONObject(error.getErrorBody());
                            result = gson.fromJson(obj.get("details").toString(), type);
                            return result;
                            // Model the error response here!
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


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