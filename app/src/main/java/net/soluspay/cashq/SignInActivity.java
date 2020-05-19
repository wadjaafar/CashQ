package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;

import net.soluspay.cashq.model.EBSRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignInActivity extends AppCompatActivity {

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.signin)
    Button signin;
    @BindView(R.id.signup)
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
        String token = sp.getString("token", null);
        if (token != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }

    public void signIn() {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, "Sign In", "Please wait...", false, false);

        EBSRequest request = new EBSRequest();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username.getText().toString());
            jsonObject.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.SIGN_IN)
                .addJSONObjectBody(jsonObject)
                .setTag("test")
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // do anything with response
                try {
                    String token = response.getString("authorization");
                    String username = response.getJSONObject("user").getString("fullname");
                    String email = response.getJSONObject("user").getString("email");

                    SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("token", token);
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.commit();
                    progressDialog.dismiss();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();

                } catch (JSONException e) {

                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                progressDialog.dismiss();
                Log.i("Response", error.getErrorBody());
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.signin, R.id.signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.signin:
                boolean error = false;

                if(username.getText().toString().isEmpty())
                {
                    error = true;
                    username.setError("Username cannot be empty");
                }
                if(password.getText().toString().isEmpty())
                {
                    error = true;
                    password.setError("Password cannot be empty");
                }
                if(!error)
                {
                    signIn();
                } else {
                    //manage error case here
                }
                break;
            case R.id.signup:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
        }
    }
}
