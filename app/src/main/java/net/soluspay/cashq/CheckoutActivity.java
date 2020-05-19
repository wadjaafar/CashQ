package net.soluspay.cashq;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gndi_sd.szzt.R;

import net.soluspay.cashq.model.Card;
import net.soluspay.cashq.model.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckoutActivity extends AppCompatActivity {


    @BindView(R.id.text_card_number)
    TextView textCardNumber;
    @BindView(R.id.text_expired_date)
    TextView textExpiredDate;
    @BindView(R.id.text_card_holder)
    TextView textCardHolder;
    @BindView(R.id.text_cvv_code)
    TextView textCvvCode;
    @BindView(R.id.pin)
    EditText pin;
    @BindView(R.id.service_name)
    TextView serviceName;
    @BindView(R.id.total)
    TextView total;

    Service service;
    @BindView(R.id.confirm)
    Button confirm;

    Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        Bundle data = getIntent().getExtras();
        if (data != null) {
            service = data.getParcelable("service");
            String title = service.getName();
            setTitle(title);
            serviceName.setText(title);
            total.setText(service.getAmount() + " SDG");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @OnClick(R.id.confirm)
    public void onViewClicked() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        AlertDialog alertDialog = new AlertDialog.Builder(CheckoutActivity.this).create();
        alertDialog.setTitle("Success");
        alertDialog.setMessage("Successful Transaction");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Print",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkout_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.card_reader:
                //doMsr();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
