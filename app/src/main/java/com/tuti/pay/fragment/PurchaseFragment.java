package com.tutipay.app.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.gndi_sd.szzt.R;

import com.tutipay.app.CardDialog;
import com.tutipay.app.model.Card;
import com.tutipay.app.utils.Globals;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */

public class PurchaseFragment extends Fragment {

    @BindView(R.id.meter)
    EditText amount;
    @BindView(R.id.proceed)
    Button proceed;
    Unbinder unbinder;


    public PurchaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);
        unbinder = ButterKnife.bind(this, view);
        Globals.serviceName = getString(R.string.purchase_title);
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
    }

    @OnClick(R.id.proceed)
    public void onViewClicked(){
        if(!TextUtils.isEmpty(amount.getText().toString())) {
            CardDialog dialog = CardDialog.newInstance();
            dialog.setCallback(new CardDialog.Callback() {
                @Override
                public void onActionClick(Card card) {
                    //makePurchase(card);
                }

            });
            Bundle args = new Bundle();
            args.putString("service", "Purchase");
            args.putString("amount", amount.getText().toString() + " SDG");

            dialog.setArguments(args);
            dialog.show(getActivity().getSupportFragmentManager(), "tag");
        } else {
            amount.setError("Please enter amount");
        }

    }



//    public void makePurchase(final Card card){
//
//        final ProgressDialog progressDialog;
//        progressDialog = ProgressDialog.show(getActivity(), "Purchase", getString(R.string.loading_wait),false, false);
//        EBSRequest request = new EBSRequest();
//
//        SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
//        String key = sp.getString("working_key", "");
//        Log.i("mk", request.getMasterKey());
//        Log.i("key", key);
//        Log.i("pan", card.getPan());
//        Log.i("PIN", card.getPin());
//        AnsiX98PinHandler pinHandler = new AnsiX98PinHandler(request.getMasterKey(), key, card.getPan(), card.getPin());
//
//        request.setTranAmount(Float.parseFloat(amount.getText().toString()));
//        request.setTranCurrencyCode("SDG");
//        request.setPan(card.getPan());
//        request.setExpDate(card.getExpDate());
//        request.setPin(pinHandler.getPinBlock());
//
//        Gson gson = new GsonBuilder().create();
//        String json = gson.toJson(request);
//        Log.i("MY REQUEST", json);
//        JSONObject object = null;
//        try {
//            object = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        AndroidNetworking.post(request.serverUrl() + Constants.PURCHASE)
//                .addJSONObjectBody(object) // posting java object
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                        if (response != null) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<EBSResponse>() {
//                            }.getType();
//                            EBSResponse result = null;
//                            try {
//                                progressDialog.dismiss();
//                                result = gson.fromJson(response.get("ebs_response").toString(), type);
//                                Log.i("MY Response", response.toString());
//                                Intent intent = new Intent(getActivity(), ResultActivity.class);
//                                intent.putExtra("response", result);
//                                intent.putExtra("card", card);
//                                startActivity(intent);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                        Log.i("Purchase Error", String.valueOf(error.getErrorBody()));
//                        if (error.getErrorCode() == 504){
//                            Toast.makeText(getActivity(), "Unable to connect to host", Toast.LENGTH_SHORT).show();
//                        }
//                        Gson gson = new Gson();
//                        Type type = new TypeToken<EBSResponse>() {
//                        }.getType();
//                        EBSResponse result = null;
//                        try {
//                            progressDialog.dismiss();
//                            JSONObject obj = new JSONObject(error.getErrorBody());
//                            result = gson.fromJson(obj.get("details").toString(), type);
//                            Log.i("MY Error", result.getResponseMessage());
//                            Intent intent = new Intent(getActivity(), ResultActivity.class);
//                            intent.putExtra("response", result);
//                            intent.putExtra("card", card);
//                            startActivity(intent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//    }

}




