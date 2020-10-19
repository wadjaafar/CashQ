package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PinChangeActivity extends AppCompatActivity {

    @BindView(R.id.new_pin)
    EditText newPin;
    @BindView(R.id.proceed)
    Button proceed;
    @BindView(R.id.confirm_new_pin)
    EditText confirmNewPin;

    static Card myCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_change);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.pin_change));
        Globals.serviceName = getString(R.string.pin_change);
        CardDialog dialog = CardDialog.newInstance();
        CardDBManager db = new CardDBManager(this);
        db.open();
        dialog.setCallback(new CardDialog.Callback() {
            @Override
            public void onActionClick(Card card) {
                myCard = card;
                db.open();
                db.updateCount(card.getPan());
            }

        });
        Bundle args = new Bundle();
        args.putString("service", "Pin Change");
        args.putString("amount", "0 SDG");
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "tag");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


    public void changePin(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, "Pin Change", getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());


        String encryptedIPIN =  new IPINBlockGenerator().getIPINBlock(card.getIpin(),key, request.getUuid());
        String encryptedNewIPIN =  new IPINBlockGenerator().getIPINBlock(newPin.getText().toString(),key, request.getUuid());

        request.setPan(card.getPan());
        request.setExpDate(card.getExpDate());
        request.setIPIN(encryptedIPIN);
        request.setNewIPIN(encryptedNewIPIN);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        Log.i("MY REQUEST", json);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.CHANGE_IPIN)
                .addJSONObjectBody(object) // posting java object
                .setTag("test")
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        if (response != null) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<EBSResponse>() {
                            }.getType();
                            EBSResponse result = null;
                            try {
                                progressDialog.dismiss();
                                result = gson.fromJson(response.get("ebs_response").toString(), type);
                                Log.i("MY Response", response.toString());
                                Intent intent = new Intent(PinChangeActivity.this, ResultActivity.class);
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
                        Log.i("Pin Change Error", String.valueOf(error.getErrorBody()));
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
                            Intent intent = new Intent(PinChangeActivity.this, ResultActivity.class);
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
        if (!TextUtils.isEmpty(newPin.getText().toString()) && !TextUtils.isEmpty(confirmNewPin.getText().toString())) {
            if(newPin.getText().toString().equals(confirmNewPin.getText().toString())) {
                changePin(myCard);
            } else {
                Toast.makeText(this, "iPIN doesn't match", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(TextUtils.isEmpty(newPin.getText().toString())) {
                newPin.setError("Please enter the new ipin");
            } else if (TextUtils.isEmpty(confirmNewPin.getText().toString())) {
                confirmNewPin.setError("Please confirm the ipin");
            }
        }

    }
}
