package net.soluspay.cashq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import net.soluspay.cashq.fragment.MainFragment;
import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.model.EBSResponse;
import net.soluspay.cashq.utils.CardDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

//Azure imports

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    FragmentManager fragmentManager = getSupportFragmentManager();


    TextView username, emailtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Azure Stats (remind me to send you the link / invitation later
        AppCenter.start(getApplication(), "450069cc-94d3-49f9-9623-f38b3eb30831",
                Analytics.class, Crashes.class);

        if (isNetworkAvailable()) {
            getPublicKey();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again.")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Retry", (dialog, which) -> {
                        // Continue with delete operation
                        getPublicKey();
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
//                    .setNegativeButton("Close", (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }


        NavigationView navigationView = findViewById(R.id.mydrawer);
        username = navigationView.getHeaderView(0).findViewById(R.id.header_name);
        emailtext = navigationView.getHeaderView(0).findViewById(R.id.header_email);
        SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
        String name = sp.getString("username", "");
        String email = sp.getString("email", "");
        username.setText(name);
        emailtext.setText(email);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager.beginTransaction().replace(R.id.fragment, new MainFragment()).commit();
        navigationView.setNavigationItemSelectedListener(item -> {

            selectDrawerItem(item);
            return true;

        });


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    public void getPublicKey() {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, getResources().getText(R.string.loading), getResources().getText(R.string.loading_wait),false, false);
        EBSRequest request = new EBSRequest();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(request);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            Log.i("Request", String.valueOf(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("My Url", request.serverUrl());
        AndroidNetworking.post(request.serverUrl() + Constants.PUBLIC_KEY)
                .addJSONObjectBody(object) // posting java object
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        progressDialog.dismiss();
                        // do anything with response
                        if (response != null) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<EBSResponse>() {
                            }.getType();
                            EBSResponse result = null;
                            try {
                                result = gson.fromJson(response.get("ebs_response").toString(), type);
                                Log.i("Public key response", response.get("ebs_response").toString());
                                SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("public_key", result.getPubKeyValue());
                                editor.apply();
                                Toast.makeText(getApplicationContext(), R.string.key_downloaded, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        EBSResponse response = error.getErrorAsObject(EBSResponse.class);
                        Log.d("no internet", error.toString());
                        Log.i("Working Key Error", String.valueOf(error.getErrorBody()));
                        //Toast.makeText(getApplicationContext(), error.getErrorCode(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(), error.getErrorCode(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Key downloading failed. Code: " + response.getCode(), Toast.LENGTH_SHORT).show();
                        //android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void selectDrawerItem(MenuItem item) {

        Intent intent;
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (item.getItemId()) {
            case R.id.home:
                fragmentClass = MainFragment.class;
                break;
            case R.id.cards:
                intent = new Intent(MainActivity.this, CardActivity.class);
                startActivity(intent);
                break;
            case R.id.language: // settings
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                break;
            case R.id.ipin_side:
                intent = new Intent(MainActivity.this, IPinActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                // on-long press activity

                // Should delete the local data as well...

                SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                // very dirty code
                getSharedPreferences("result", Activity.MODE_PRIVATE).edit().clear().apply();

                CardDBManager dbmanager = new CardDBManager(this);
                dbmanager.open();
                dbmanager.deleteAll();

                intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                fragmentClass = MainFragment.class;
        }

    }

    @Override
    protected void onResume() {

        super.onResume();

    }


    // this function handles auth retry on onRestart method
    private void  authRetry(){
        // Send Request

        String token = getHeader();
        EBSRequest request = new EBSRequest();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.REFRESH_TOKEN)
                .addJSONObjectBody(jsonObject)
                .setTag("test")
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", token)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // do anything with response
                try {
                    // either get "authorization" or "token"
                    String token = response.getString("authorization");


                    SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", token);
                    editor.apply();
                    finish();

                } catch (JSONException e) {
                    Log.i("refresh_token", "There is an error: ", e);
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.i("Response", error.getErrorBody());
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getHeader() {
        SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
        return sp.getString("token", null);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


}
