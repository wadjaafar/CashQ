package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import net.soluspay.cashq.utils.Globals;
import net.soluspay.cashq.utils.IPINBlockGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServicePayment extends AppCompatActivity {

    @BindView(R.id.service_id)
    EditText serviceId;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_payment);
        ButterKnife.bind(this);
        setTitle("Service Payment");
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    public void purchaseElectricity(final Card card){

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, "Purchasing", "Please wait...",false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN =  new IPINBlockGenerator().getIPINBlock(card.getIpin(),key, request.getUuid());

        request.setServiceProviderId(serviceId.getText().toString());
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

        AndroidNetworking.post(request.serverUrl() + Constants.PURCHASE)
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
                                Intent intent = new Intent(ServicePayment.this, ResultActivity.class);
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
                        if (error.getErrorCode() == 504){
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
                            Intent intent = new Intent(ServicePayment.this, ResultActivity.class);
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

    @OnClick(R.id.proceed)
    public void onViewClicked() {
        boolean error = false;

        if(serviceId.getText().toString().isEmpty())
        {
            error = true;
            serviceId.setError("Enter an id");
        }
        if(amount.getText().toString().isEmpty())
        {
            error = true;
            amount.setError("Amount cannot be empty");
        }
        if(!error)
        {
            Globals.service = "purchase";
            Globals.serviceName = "Service Payment";
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    purchaseElectricity(card);
                }

            });
            Bundle args = new Bundle();
            args.putString("service", "Service Payment");
            args.putString("amount",  amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }
    }
}
