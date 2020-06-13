package net.soluspay.cashq;

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
import com.gndi_sd.szzt.R;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.soluspay.cashq.model.Card;
import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.model.EBSResponse;
import net.soluspay.cashq.utils.CardDBManager;
import net.soluspay.cashq.utils.Globals;
import net.soluspay.cashq.utils.IPINBlockGenerator;

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

    String resultCode;
    CardDBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrpay);
        ButterKnife.bind(this);
        setTitle("QR Payment");
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
        progressDialog = ProgressDialog.show(this, "QR Payment", "Please wait...", false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(card.getIpin(), key, request.getUuid());

        HashMap<String, String> map = new HashMap<>();
        map.put("METER", merchant.getText().toString());

        String paymentInfo = Joiner.on("/").withKeyValueSeparator("=").join(map);

        request.setPayeeId("0010020001");
        request.setPaymentInfo(paymentInfo);
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

    void parseQR(){
    // use this function to parse the QR (https://github.com/adonese/qr)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("intent", data.getStringExtra("data"));
                merchant.setText(data.getStringExtra("data"));
            }
        }
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
            Globals.serviceName = "QR Payment";
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
}
