package net.soluspay.cashq;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gndi_sd.szzt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.proceed)
    Button proceed;

    private String trueCode;

    int id;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        Bundle bundle= getIntent().getExtras();
        id = bundle.getInt("id");
        if (id == 0){
            title.setText("Merchant Only");
            trueCode = "0000";
            intent = new Intent(LockActivity.this, MerchantActivity.class);
        } else {
            title.setText("Admin Only");
            trueCode = "1111";
            intent = new Intent(LockActivity.this, SettingsActivity.class);
        }
        password.requestFocus();
    }

    @OnClick(R.id.proceed)
    public void onViewClicked() {

        if (!TextUtils.isEmpty(password.getText().toString())) {
            if (password.getText().toString().equals(trueCode)){
                Log.i("PASSWORD", password.getText().toString());
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
            }
        } else {
            password.setError("Password cannot be empty!");
        }
    }

}
