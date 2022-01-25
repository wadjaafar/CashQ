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
import java.util.Arrays;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoucherGenerateFragment extends Fragment {
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;

    public VoucherGenerateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_voucher_generate, container, false);
        unbinder = ButterKnife.bind(this, view);
        Globals.serviceName = getString(R.string.voucher_generation);
        //service name is a string inside globals class
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        //You will also have to use Unbinder to unbind the view in onDestroyView() because of the Life cycle
        // methods of Fragment.
    }

    @OnClick(R.id.proceed)
    public void onViewClicked() {

        boolean error = false;

        if (phone.getText().toString().isEmpty()) {
            error = true;
            phone.setError("Enter a phone number");
        }
        if (phone.getText().toString().length() != 10) {
            error = true;
            phone.setError("Enter a valid phone number");
        }
        if (amount.getText().toString().isEmpty()) {
            error = true;
            amount.setError("Amount cannot be empty");
        }

        if (!error) {

            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    TopUp(card);
                }

            });
            Bundle args = new Bundle();
            args.putString("service", Globals.serviceName);
            args.putString("amount", amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        }



    }

    public void TopUp(final Card card) {
        final ProgressDialog progressDialog;
        //ProgressDialog is a modal dialog, which prevents the user from interacting with the app. Instead of using this class,
        // you should use a progress indicator like ProgressBar.
        progressDialog = ProgressDialog.show(getActivity(), "Generate voucher", "Please wait...", false, false);
        EBSRequest request = new EBSRequest();
//opens it using the private mode so the file is accessible by only your app:
        SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN =  new IPINBlockGenerator().getIPINBlock(card.getIpin(),key, request.getUuid());

        request.setTranCurrencyCode("SDG");
        request.setPan(card.getPan());
        request.setExpDate(card.getExpDate());
        request.setIPIN(encryptedIPIN);
        request.setTranAmount(Float.parseFloat(amount.getText().toString()));
        request.setVoucherNumber(phone.getText().toString());

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        Log.i("MY REQUEST", json);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.GENERATE_VOUCHER)
                //public static final String PURCHASE = "/api/purchase";
                .addJSONObjectBody(object) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
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
                                Intent intent = new Intent(getActivity(), ResultActivity.class);
                                intent.putExtra("response", result);
                                intent.putExtra("card", card);
                                startActivity(intent);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


}
