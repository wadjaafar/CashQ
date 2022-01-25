package com.tutipay.app.fragment;


import android.app.Activity;
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

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.tutipay.app.CardDialog;
import com.tutipay.app.Constants;
import com.tutipay.app.ResultActivity;
import com.tutipay.app.model.Card;
import com.tutipay.app.model.EBSRequest;
import com.tutipay.app.model.EBSResponse;
import com.tutipay.app.utils.Globals;
import com.tutipay.app.utils.IPINBlockGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CovidFragment extends Fragment {


    @BindView(R.id.amount)
    EditText amount;

    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;


    private String serviceName, receipt;

    public CovidFragment() {
        // Required empty public constructor
    }

    public void topUp(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(getActivity(), serviceName, getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = Objects.requireNonNull(getActivity()).getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());

        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(card.getIpin(), key, request.getUuid());
        request.setPan(card.getPan());
        request.setExpDate(card.getExpDate());
        request.setIpin(encryptedIPIN);
        request.setServiceProviderId(Constants.COVID19);
        request.setTranAmount(Float.parseFloat(amount.getText().toString()));

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
        View view = inflater.inflate(R.layout.fragment_covid, container, false);
        unbinder = ButterKnife.bind(this, view);
        Globals.service = "billers";
        serviceName = "Covid19 Donations";
        receipt = "billers";
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

        if(amount.getText().toString().isEmpty())
        {
            error = true;
            amount.setError("Enter a valid amount");
        }

        if(!error)
        {
            Globals.serviceName = serviceName;
            Globals.service = "telecomTopup";
            Globals.service = receipt;
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(this::topUp);
            Bundle args = new Bundle();
            args.putString("service", serviceName);

            dialog.setArguments(args);
            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }

    }
}
