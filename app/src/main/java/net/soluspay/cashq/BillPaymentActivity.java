package net.soluspay.cashq;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.gndi_sd.szzt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillPaymentActivity extends AppCompatActivity {

    @BindView(R.id.view_e15)
    ConstraintLayout viewE15;
    @BindView(R.id.view_customs)
    ConstraintLayout viewCustoms;
    @BindView(R.id.view_education)
    ConstraintLayout viewEducation;
    @BindView(R.id.view_telecom)
    ConstraintLayout viewTelecom;
    @BindView(R.id.view_electricity)
    ConstraintLayout viewElectricity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_payment);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bill Payment");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }


    @OnClick({R.id.view_e15, R.id.view_customs, R.id.view_education, R.id.view_telecom, R.id.view_electricity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_e15:
                startActivity(new Intent(BillPaymentActivity.this, E15Activity.class));
                break;
            case R.id.view_customs:
                startActivity(new Intent(BillPaymentActivity.this, CustomsActivity.class));
                break;
            case R.id.view_education:
                startActivity(new Intent(BillPaymentActivity.this, EducationActivity.class));
                break;
            case R.id.view_telecom:
                startActivity(new Intent(BillPaymentActivity.this, TelecomActivity.class));
                break;
            case R.id.view_electricity:
                startActivity(new Intent(BillPaymentActivity.this, ComingSoonActivity.class));
                break;
        }
    }
}
