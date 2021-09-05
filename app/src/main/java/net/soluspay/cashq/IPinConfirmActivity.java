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
 */
public class IPinConfirmActivity extends AppCompatActivity {



    @BindView(R.id.pan)
    EditText pan;

    @BindView(R.id.exp_date)
    EditText exp_date;

    @BindView(R.id.otp)
    EditText otp;

    @BindView(R.id.ipin)
    EditText ipin;

    @BindView(R.id.ipin2)
    EditText ipin2;

    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;


    private String payeeId, serviceName, receipt;
    CardDBManager dbManager;

    public IPinConfirmActivity() {
        // Required empty public constructor
    }

    public void topUp(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, serviceName, getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");

        request.setOtherPan(pan.getText().toString());
        request.setExpDate(exp_date.getText().toString());

        String encryptedIPIN =  new IPINBlockGenerator().getIPINBlock(ipin.getText().toString(),key, request.getUuid());
        String encryptedOTP =  new IPINBlockGenerator().getIPINBlock(otp.getText().toString(),key, request.getUuid());
        request.setIpin(encryptedIPIN);
        request.setOtp(encryptedOTP);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        Log.i("MY REQUEST", json);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("my request", object.toString());

        AndroidNetworking.post(request.serverUrl() + Constants.CONFIRM_IPIN)
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


                                new AlertDialog.Builder(IPinConfirmActivity.this)
                                        .setTitle(R.string.ipin_changed_prompt)
                                        .setMessage(R.string.ipin_changed_prompt)

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(R.string.back_home_prompt, (dialog, which) -> {
                                            // Continue with delete operation
                                            finish();
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        // .setNegativeButton("Close", (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()))
                                        .setIcon(R.drawable.ic_padlock)
                                        .show();

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
                            Toast.makeText(IPinConfirmActivity.this, "Unable to connect to host", Toast.LENGTH_SHORT).show();
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

                            new AlertDialog.Builder(IPinConfirmActivity.this)
                                    .setTitle(R.string.ipin_changed_error)
                                    .setMessage(R.string.ipin_changed_error)

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(R.string.Rety, (dialog, which) -> {
                                        // Continue with delete operation

                                    }).setNegativeButton(R.string.back_home_prompt, (dialog, which) -> {
                                Intent intent = new Intent(IPinConfirmActivity.this, MainActivity.class);

                                startActivity(intent);
                                finish();
                            })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
//                    .setNegativeButton("Close", (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

//                            Intent intent = new Intent(IPinConfirmActivity.this, ResultActivity.class);
//                            intent.putExtra("response", result);
//                            intent.putExtra("card", card);
//                            startActivity(intent);
//                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new CardDBManager(this);
        dbManager.open();
        setContentView(R.layout.fragment_ipin_verify);
        setTitle(getString(R.string.ipin_completion_title));
        ButterKnife.bind(this);
        Globals.service = "ipin_completion";
        serviceName = "IPIN completion";
        receipt = "ipin_completion";

        // update pan and expdate values here...
        Intent intent = getIntent();
        pan.setText(( String) intent.getSerializableExtra("pan"));
        exp_date.setText(( String) intent.getSerializableExtra("expdate"));
    }


    @OnClick(R.id.proceed)
    public void onViewClicked() {
        boolean error = false;

        if(pan.getText().toString().isEmpty())
        {
            error = true;
            pan.setError("Enter a phone number");
        }
//        if(pan.getText().toString().length() != 16 || pan.getText().toString().length() != 19)
//        {
//            error = true;
//            pan.setError(getString(R.string.pan_length_validation));
//        }
        if(exp_date.getText().toString().isEmpty())
        {
            error = true;
            exp_date.setError("Amount cannot be empty");
        }
        if (!ipin2.getText().toString().equals(ipin.getText().toString())) {
            error = true;
            ipin2.setError(getString(R.string.ipin_mismatched_error));
        }
        if(!error)
        {
            Globals.serviceName = serviceName;
            Globals.service = "ipin_completion";
            Globals.service = receipt;
            topUp(null);
        } else {
            //manage error case here
        }

    }
}
