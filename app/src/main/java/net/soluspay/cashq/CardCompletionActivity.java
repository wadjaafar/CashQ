package net.soluspay.cashq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardCompletionActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardCompletionActivity extends AppCompatActivity {


    @BindView(R.id.otp)
    EditText otp;

    @BindView(R.id.pin)
    EditText pin;

    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;

    CardDBManager dbManager;

    private String payeeId, serviceName, receipt, uuid, phone;

    public CardCompletionActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        dbManager = new CardDBManager(this);
        dbManager.open();


        setContentView(R.layout.fragment_card_completion);
        setTitle("Card issuance");
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String uuid = (String) intent.getSerializableExtra("uuid");
        String phone = (String) intent.getSerializableExtra("phone");
        Globals.service = "register_card";
        serviceName = "Card Issuance";

    }

    public void cardIssuance(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, serviceName, getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");

        String encryptedIPIN =  new IPINBlockGenerator().getIPINBlock(otp.getText().toString(),key, request.getUuid());
        String encryptedOTP =  new IPINBlockGenerator().getIPINBlock(pin.getText().toString(),key, request.getUuid());
        request.setOtp(encryptedOTP); // encrypt here man...
        request.setIPIN(encryptedIPIN);
        request.setOriginalTranUUID(uuid);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        Log.i("MY REQUEST", json);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.CARD_ISSUANCE)
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

                                dbManager.insert(result.getPAN(), result.getExpDate(), "12");
                                // redirect the user to their card!
                                new AlertDialog.Builder(CardCompletionActivity.this)
                                        .setTitle(R.string.card_added_successfully)
                                        .setMessage(R.string.card_added_successfully)

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(R.string.Rety, (dialog, which) -> {
                                            // Continue with delete operation
                                            Intent intent = new Intent(CardCompletionActivity.this, CardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        // .setNegativeButton("Close", (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()))
                                        .setIcon(R.drawable.ic_wallet)
                                        .show();

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
                            Toast.makeText(CardCompletionActivity.this, "Unable to connect to host", Toast.LENGTH_SHORT).show();
                        }
                        Gson gson = new Gson();
                        Type type = new TypeToken<EBSResponse>() {
                        }.getType();
                        EBSResponse result = null;
                        try {
                            progressDialog.dismiss();
                            JSONObject obj = new JSONObject(error.getErrorBody());
                            new AlertDialog.Builder(CardCompletionActivity.this)
                                    .setTitle(R.string.card_added_successfully)
                                    .setMessage(R.string.card_added_successfully)

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(R.string.Rety, (dialog, which) -> {
                                        // Continue with delete operation
                                        Intent intent = new Intent(CardCompletionActivity.this, CardActivity.class);

                                        startActivity(intent);
                                        finish();

                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
//                    .setNegativeButton("Close", (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()))
                                    .setIcon(R.drawable.ic_wallet)
                                    .show();
                        } catch (JSONException e) {
                            Toast.makeText(CardCompletionActivity.this, R.string.unexpected_error, Toast.LENGTH_LONG).show();
                            finish();

                            e.printStackTrace();
                        }
                    }
                });

    }



    @OnClick(R.id.proceed)
    public void onViewClicked() {
        boolean error = false;

        if (otp.getText().toString().isEmpty()) {
            error = true;
            otp.setError(getString(R.string.enter_otp_prompt));
        }

        if (!error) {
            Globals.service = receipt;
            Globals.serviceName = serviceName;
            cardIssuance(null);
//            CardDialog dialog = CardDialog.newInstance();
//            dialog.setCallback(new CardDialog.Callback() {
//                @Override
//                public void onActionClick(Card card) {
//                    cardIssuance(card);
//                    db.open();
//                    db.updateCount(card.getPan());
//                }
//
//            });
//            Bundle args = new Bundle();
//            args.putString("service", serviceName);
//            args.putString("amount", "0 SDG");
//            dialog.setArguments(args);
//            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }
    }
}