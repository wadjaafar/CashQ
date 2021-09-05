package net.soluspay.cashq.fragment;


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

import net.soluspay.cashq.CardCompletionActivity;
import net.soluspay.cashq.CardDialog;
import net.soluspay.cashq.Constants;
import net.soluspay.cashq.IPinConfirmActivity;
import net.soluspay.cashq.ResultActivity;
import net.soluspay.cashq.model.Card;
import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.model.EBSResponse;
import net.soluspay.cashq.utils.Globals;

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
public class IPinRequestFragment extends Fragment {


    @BindView(R.id.pan)
    EditText pan;

    @BindView(R.id.exp_date)
    EditText exp_date;


    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;


    private String  serviceName, receipt;

    public IPinRequestFragment() {
        // Required empty public constructor
    }

    public void topUp(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(getActivity(), serviceName, getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = Objects.requireNonNull(getActivity()).getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");


        request.setOtherPan(pan.getText().toString());
        request.setExpDate(exp_date.getText().toString());
        request.setPhoneNumber(phone.getText().toString());


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
        AndroidNetworking.post(request.serverUrl() + Constants.START_IPIN)
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
                                    Intent intent = new Intent(getActivity(), CardCompletionActivity.class);
                                    intent.putExtra("response", result);
                                    intent.putExtra("uuid", request.getUuid());
                                    intent.putExtra("phone", phone.getText().toString());
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
                            Log.i("Purchase Error code", String.valueOf(error.getErrorCode()));
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
                                Intent intent = new Intent(getActivity(), IPinConfirmActivity.class);
                                intent.putExtra("uuid", request.getUuid());
                                intent.putExtra("pan", pan.getText().toString());
                                intent.putExtra("expdate", exp_date.getText().toString());

                                startActivity(intent);
                                getActivity().finish();
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), R.string.unexpected_error, Toast.LENGTH_LONG).show();
                                getActivity().finish();
                                e.printStackTrace();
                            }
                        }
                    });

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ipin, container, false);
        unbinder = ButterKnife.bind(this, view);
        Globals.service = "ipin";
        serviceName = getString(R.string.ipin_generation_service);
        receipt = "ipin";
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

        if(pan.getText().toString().isEmpty())
        {
            error = true;
            pan.setError(getString(R.string.pan_prompt));
        }
//        if(pan.getText().toString().length() != 16 || pan.getText().toString().length() != 19)
//        {
//            error = true;
//            pan.setError(getString(R.string.pan_length_validation));
//        }
        if(exp_date.getText().toString().isEmpty())
        {
            error = true;
            exp_date.setError(getString(R.string.empty_expdate_error));
        }
        if(!error)
        {
            Globals.serviceName = serviceName;
            Globals.service = "ipin";
            Globals.service = receipt;
            // call the static method here...
            topUp(null);
        } else {
            //manage error case here

        }

    }
}
