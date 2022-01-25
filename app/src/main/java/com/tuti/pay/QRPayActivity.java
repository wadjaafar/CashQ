package com.tutipay.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.emv.qrcode.decoder.mpm.DecoderMpm;
import com.emv.qrcode.model.mpm.MerchantPresentedMode;
import com.gndi_sd.szzt.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.tutipay.app.model.Card;
import com.tutipay.app.model.EBSRequest;
import com.tutipay.app.model.EBSResponse;
import com.tutipay.app.utils.CardDBManager;
import com.tutipay.app.utils.Globals;
import com.tutipay.app.utils.IPINBlockGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRPayActivity extends AppCompatActivity {

    @BindView(R.id.merchant)
    EditText merchant;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;
    @BindView(R.id.qr_image)
    ImageButton qrImage;
    @BindView(R.id.qr_more)
    Button qr_more;

    String resultCode;
    CardDBManager db;

    private String qrcode;
    private String merchantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpay);
        ButterKnife.bind(this);
        setTitle(getString(R.string.qr_pay_service));
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Globals.service = "qrpay";

        db = new CardDBManager(this);
        db.open();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    public void purchaseElectricity(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, "QR Payment", getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(card.getIpin(), key, request.getUuid());

        HashMap<String, String> map = new HashMap<>();

        request.setPayeeId(merchantId);
        request.setMerchantID(merchantId);
        request.setTranAmount(Float.parseFloat(amount.getText().toString()));
        request.setTranCurrencyCode("SDG");
        request.setPan(card.getPan());

        request.setExpDate(card.getExpDate());
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

        AndroidNetworking.post(request.serverUrl(true) + Constants.QR_PAYMENT)
                .addJSONObjectBody(object) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.i("Smoke Response", response.toString());
                        if (response != null) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<EBSResponse>() {
                            }.getType();
                            EBSResponse result = null;
                            try {
                                progressDialog.dismiss();
                                result = gson.fromJson(response.get("ebs_response").toString(), type);
                                Log.i("MY Response", response.toString());
                                Intent intent = new Intent(QRPayActivity.this, ResultActivity.class);
                                intent.putExtra("response", result);
                                intent.putExtra("card", card);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.i("Purchase Error", String.valueOf(error.getErrorBody()));
                        if (error.getErrorCode() == 504) {
                            Toast.makeText(getApplicationContext(), "Unable to connect to host", Toast.LENGTH_SHORT).show();
                        }
                        Gson gson = new Gson();
                        Type type = new TypeToken<EBSResponse>() {
                        }.getType();
                        EBSResponse result = null;
                        try {
                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(error.getErrorBody());
                            result = gson.fromJson(obj.get("details").toString(), type);
                            Log.i("MY Error", result.getResponseMessage());
                            Intent intent = new Intent(QRPayActivity.this, ResultActivity.class);
                            intent.putExtra("response", result);
                            intent.putExtra("card", card);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    @OnClick(R.id.qr_image)
    public void onQrImageClicked() {
        startActivityForResult(new Intent(QRPayActivity.this, ScanCodeActivity.class), 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("intent", data.getStringExtra("data"));
                // Parse QR here ...
                qrcode = data.getStringExtra("data");
//                merchant.setText(data.getStringExtra("data"));
                try {

                    final MerchantPresentedMode merchantPresentedMode = DecoderMpm.decode(qrcode, MerchantPresentedMode.class);
                    if (merchantPresentedMode.getPointOfInitiationMethod().getValue().equals("12")) { //dynamic QR
                        amount.setText(merchantPresentedMode.getTransactionAmount().getValue());
                    }
                    merchantId = getAccount(merchantPresentedMode.getMerchantAccountInformation().get("26").getValue().toString());
                    merchant.setText(merchantPresentedMode.getMerchantName().getValue() + " - " + merchantId);

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "QR error", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.qr_more)
    public void onShowMore(){
        Intent intent = new Intent(QRPayActivity.this, QRWebview.class);
        intent.putExtra("qr", qrcode);
        startActivity(intent);
        // Really just show a webview here
//        QRWebview;
//        QRWebview("00020101021226410014A000000615000101065016640209123456789520499995303458540510.005802MY5909QRCSDNBHD6005BAHRI6105436506304BFCA");
    }

    @OnClick(R.id.proceed)
    public void onProceedClicked() {
        boolean error = false;

        if (merchant.getText().toString().isEmpty()) {
            error = true;
            merchant.setError("Enter merchant id");
        }
        if (amount.getText().toString().isEmpty()) {
            error = true;
            amount.setError("Amount cannot be empty");
        }
        if (!error) {
            Globals.serviceName = getString(R.string.qr_pay_service);
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    purchaseElectricity(card);
                    // get base context is not tested!
                    db.open();
                    db.updateCount(card.getPan());
                }

            });
            Bundle args = new Bundle();
            args.putString("service", "QR Payment");
            args.putString("amount", amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }
    }


    private String getAccount(String payload) {
        /*
        2641
        00 14 A0000006150001 acquirer id
        01 06 501664 token
        02 09 123456789 merchant id

        00: something
        01: another thing
        02: merchant id
         */
        Log.i("payload debug-beginning", payload);
//        QR qr = new Qr();

        Log.i("payload debug", payload);
        for (int i = 0; i < 2; i++) {
            // get the idxPrefix third and 4th item
            String idxPrefix;
            payload = payload.substring(2);
            Log.i("payload debug-no zeros", payload);
            idxPrefix = payload.substring(0, 2);
            Log.i("payload-idx", idxPrefix); // 14
            int prefixLength = Integer.parseInt(idxPrefix);
            String[] data = payload.split(payload.substring(0, prefixLength + 1));
            Log.i("payload-zeroed" + i, data[0]);
            payload = data[1];


            Log.i("payload-aftermath" + i, payload); // A0000006150001
        }
        Log.i("payload-final", payload.substring(2));
        return payload.substring(2);
    }
}


