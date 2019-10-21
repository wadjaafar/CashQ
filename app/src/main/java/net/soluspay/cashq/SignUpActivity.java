package net.soluspay.cashq;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;

import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.model.EBSResponse;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.fullname)
    EditText fullname;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password2)
    EditText password2;
    @BindView(R.id.signup)
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }

    public void signUp() {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, "Sign Up", "Please wait...", false, false);
        EBSRequest request = new EBSRequest();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username.getText().toString());
            jsonObject.put("password", password.getText().toString());
            jsonObject.put("password2", password2.getText().toString());
            jsonObject.put("email", email.getText().toString());
            jsonObject.put("fullname", fullname.getText().toString());
            jsonObject.put("mobile", mobile.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(request.serverUrl() + Constants.SIGN_UP)
                .addJSONObjectBody(jsonObject)
                .setTag("test")
                .setContentType("application/json")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", "Basic dGVzdDp0ZXN0MTI=")
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // do anything with response
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Successful")
                        .setMessage("You have been registered successfully")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                finish();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

            @Override
            public void onError(ANError error) {
                // handle error
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                EBSResponse response = error.getErrorAsObject(EBSResponse.class);
                builder.setTitle("Failed")
                        .setMessage("There is an error: " + response.getCode())
//                        .setMessage(error.getResponse().message())

                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                Log.i("registration_error", error.getErrorBody());
            }

        });
    }

    @OnClick(R.id.signup)
    public void onViewClicked() {
        boolean error = false;

        if(username.getText().toString().isEmpty())
        {
            error = true;
            username.setError("Username cannot be empty");
        }
        if(mobile.getText().toString().isEmpty())
        {
            error = true;
            mobile.setError("Mobile number cannot be empty");
        }
        if (mobile.getText().toString().length() != 10) {
            error = true;
            mobile.setError("Mobile number should be 10 digits");
        }
        if (email.getText().toString().isEmpty()) {
            error = true;
            email.setError("Mobile number cannot be empty");
        }

        if(fullname.getText().toString().isEmpty())
        {
            error = true;
            fullname.setError("Full name cannot be empty");
        }
        if(password2.getText().toString().isEmpty())
        {
            error = true;
            password2.setError("Please confirm your password");
        }
        if(password.getText().toString().isEmpty())
        {
            error = true;
            password.setError("Password cannot be empty");
        }
        if(password.getText().toString().length() < 8)
        {
            error = true;
            password.setError("Password should be more than 8 characters");
        }
        if(!password.getText().toString().equals(password2.getText().toString()))
        {
            error = true;
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }
        if(!error)
        {
            signUp();
        } else {
            //manage error case here
        }
    }
}
