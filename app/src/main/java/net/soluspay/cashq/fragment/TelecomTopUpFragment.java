package net.soluspay.cashq.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import net.soluspay.cashq.CardDialog;
import net.soluspay.cashq.Constants;
import net.soluspay.cashq.ResultActivity;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class TelecomTopUpFragment extends Fragment {

    @BindView(R.id.radio)
    RadioGroup radio;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;

    @BindView(R.id.radio_zain)
    RadioButton radioZain;
    @BindView(R.id.radio_sudani)
    RadioButton radioSudani;
    @BindView(R.id.radio_mtn)
    RadioButton radioMtn;

    CardDBManager db;

    private String payeeId, serviceName, receipt;

    public TelecomTopUpFragment() {
        // Required empty public constructor
    }

    public void topUp(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(getActivity(), serviceName, "Please wait...", false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(card.getIpin(), key, request.getUuid());

        HashMap<String, String> map = new HashMap<>();
        map.put("MPHONE", phone.getText().toString());

        String paymentInfo = Joiner.on("/").withKeyValueSeparator("=").join(map);

        request.setPayeeId(payeeId);
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

        Log.i("my request", String.valueOf(object.toString()));

        AndroidNetworking.post(request.serverUrl() + Constants.BILL_PAYMENT)
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
                                Intent intent = new Intent(getActivity(), ResultActivity.class);
                                intent.putExtra("response", result);
                                intent.putExtra("card", card);
                                startActivity(intent);
                                getActivity().finish();
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
                            Toast.makeText(getActivity(), "Unable to connect to host", Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(getActivity(), ResultActivity.class);
                            intent.putExtra("response", result);
                            intent.putExtra("card", card);
                            startActivity(intent);
                            getActivity().finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new CardDBManager(this.getActivity());
        db.open();

        View view = inflater.inflate(R.layout.fragment_telecom_top_up, container, false);
        unbinder = ButterKnife.bind(this, view);
        Globals.service = "telecom_topup";
        radioZain.setChecked(true);
        serviceName = "Zain Top-up";
        receipt = "zainTopup";
        payeeId = "0010010001";


        radio.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_zain:
                    payeeId = "0010010001";
                    serviceName = "Zain Top-up";
                    receipt = "zainTopup";
                    break;
                case R.id.radio_sudani:
                    payeeId = "0010010005";
                    serviceName = "Sudani Top-up";
                    receipt = "sudaniTopup";
                    break;
                case R.id.radio_mtn:
                    payeeId = "0010010003";
                    serviceName = "MTN Top-up";
                    receipt = "mtnTopup";
                    break;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.proceed)
    public void onViewClicked() {
        boolean error = false;

        if(phone.getText().toString().isEmpty())
        {
            error = true;
            phone.setError("Enter a phone number");
        }
        if(phone.getText().toString().length() != 10)
        {
            error = true;
            phone.setError("Enter a valid phone number");
        }
        if(amount.getText().toString().isEmpty())
        {
            error = true;
            amount.setError("Amount cannot be empty");
        }
        if(!error)
        {
            Globals.serviceName = serviceName;
            Globals.service = "telecomTopup";
            Globals.service = receipt;
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    topUp(card);
                    db.open();
                    db.updateCount(card.getPan());

                }

            });
            Bundle args = new Bundle();
            args.putString("service", serviceName);
            args.putString("amount", amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }

    }
}
