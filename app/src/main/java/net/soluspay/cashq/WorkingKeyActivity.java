package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;

import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.model.EBSResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkingKeyActivity extends AppCompatActivity {

    @BindView(R.id.status)
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_key);
        ButterKnife.bind(this);
        if (isNetworkAvailable()){
            //getWorkingKey();
        }else {
            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
    }

//    public void getWorkingKey() {
//
//        final ProgressDialog progressDialog;
//        progressDialog = ProgressDialog.show(this, "Working Key", "Downloading key...", false, false);
//        EBSRequest request = new EBSRequest();
//        Gson gson = new GsonBuilder().create();
//        String json = gson.toJson(request);
//        JSONObject object = null;
//        try {
//            object = new JSONObject(json);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.i("My Url", request.serverUrl());
//        AndroidNetworking.post(request.serverUrl() + Constants.WORKING_KEY)
//                .addJSONObjectBody(object) // posting java object
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        progressDialog.dismiss();
//                        // do anything with response
//                        if (response != null) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<EBSResponse>() {
//                            }.getType();
//                            EBSResponse result = null;
//                            try {
//                                result = gson.fromJson(response.get("ebs_response").toString(), type);
//                                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString("working_key", result.getWorkingKey());
//                                editor.commit();
//                                status.setText(result.getResponseStatus());
//                                //Toast.makeText(getApplicationContext(), "Key downloaded successfully ", Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        // handle error
//                        Log.i("Working Key Error", String.valueOf(error.getErrorDetail()));
//                        //Toast.makeText(getApplicationContext(), error.getErrorCode(), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                        status.setText("Failed");
//                        //Toast.makeText(getApplicationContext(), "Key downloading failed", Toast.LENGTH_SHORT).show();
//                        //android.os.Process.killProcess(android.os.Process.myPid());
//                    }
//                });
//    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
