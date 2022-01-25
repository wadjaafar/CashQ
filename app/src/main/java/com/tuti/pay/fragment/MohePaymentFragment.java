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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.tutipay.app.CardDialog;
import com.tutipay.app.Constants;
import com.tutipay.app.ResultActivity;
import com.tutipay.app.model.Card;
import com.tutipay.app.model.Course;
import com.tutipay.app.model.EBSRequest;
import com.tutipay.app.model.EBSResponse;
import com.tutipay.app.model.Form;
import com.tutipay.app.utils.CardDBManager;
import com.tutipay.app.utils.Globals;
import com.tutipay.app.utils.IPINBlockGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MohePaymentFragment extends Fragment {

    CardDBManager db;

    @BindView(R.id.course_spinner)
    Spinner courseSpinner;
    @BindView(R.id.form_spinner)
    Spinner formSpinner;
    Unbinder unbinder;
    @BindView(R.id.seat_number)
    EditText seatNumber;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;

    Course course;
    Form form;


    public MohePaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new CardDBManager(this.getActivity());
        db.open();

        View view = inflater.inflate(R.layout.fragment_mohe_payment, container, false);
        unbinder = ButterKnife.bind(this, view);
        setCourseData();
        setFormData();
        amount.setEnabled(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setCourseData() {

        ArrayList<Course> courseList = new ArrayList<>();
        //Add countries

        courseList.add(new Course(1, "Academic"));
        courseList.add(new Course(2, "Agricultural"));
        courseList.add(new Course(3, "Commercial"));
        courseList.add(new Course(4, "Industrial"));
        courseList.add(new Course(5, "Womanly"));
        courseList.add(new Course(6, "Ahlia"));
        courseList.add(new Course(7, "Readings"));

        //fill data in spinner
        ArrayAdapter<Course> adapter = new ArrayAdapter<Course>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courseList);
        courseSpinner.setAdapter(adapter);
        //courseSpinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                course = courseList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setFormData() {

        ArrayList<Form> formList = new ArrayList<>();
        //Add countries

        formList.add(new Form(1, "50", "General admission-first round"));
        formList.add(new Form(2, "200", "Special admission"));
        formList.add(new Form(3, "200", "Sons of higher education staff"));
        formList.add(new Form(6, "50", "General admission-second round"));
        formList.add(new Form(7, "200", "Special admission-vacant seats"));
        formList.add(new Form(8, "200", "Private institutions direct admission"));
        formList.add(new Form(9,"200", "Diploma in public institutions"));

        //fill data in spinner
        ArrayAdapter<Form> adapter = new ArrayAdapter<Form>(getActivity(), android.R.layout.simple_spinner_dropdown_item, formList);
        formSpinner.setAdapter(adapter);
        //courseSpinner.setSelection(adapter.getPosition(myItem));//Optional to set the selected item.
        adapter.notifyDataSetChanged();
        formSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                form = formList.get(position);
                amount.setText(form.getFees());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void mohePayment(final Card card) {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(getActivity(), "MOHE Bill Payment", getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String key = sp.getString("public_key", "");
        Log.i("Public Key", card.getIpin());
        String encryptedIPIN = new IPINBlockGenerator().getIPINBlock(card.getIpin(), key, request.getUuid());

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("SETNUMBER", seatNumber.getText().toString());
        map.put("STUDCOURSEID", String.valueOf(course.getId()));
        map.put("STUDFORMKIND", String.valueOf(form.getId()));

        String paymentInfo = Joiner.on("/").withKeyValueSeparator("=").join(map);
//
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            sb.append(entry.getKey());
//            sb.append('=');
//            sb.append(entry.getValue());
//            sb.append('/');
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        String paymentInfo = sb.toString();

        request.setPayeeId("0010030002");
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

    @OnClick(R.id.proceed)
    public void onViewClicked() {
        boolean error = false;

        if(seatNumber.getText().toString().isEmpty())
        {
            error = true;
            seatNumber.setError("Seat Number cannot be empty");
        }
        if(!error)
        {
            Globals.service = "mohePayment";
            Globals.serviceName = getString(R.string.mohe_bill_payment_title);
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    mohePayment(card);
                    db.open();
                    db.updateCount(card.getPan());

                }

            });
            Bundle args = new Bundle();
            args.putString("service", "MOHE Bill Payment");
            args.putString("amount", amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        } else {
            //manage error case here
        }

    }

}
