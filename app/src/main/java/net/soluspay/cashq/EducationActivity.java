package net.soluspay.cashq;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gndi_sd.szzt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EducationActivity extends AppCompatActivity {

    @BindView(R.id.view_mohe)
    ConstraintLayout viewMohe;
    @BindView(R.id.view_mohe_arab)
    ConstraintLayout viewMoheArab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        setTitle(getString(R.string.education_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @OnClick({R.id.view_mohe, R.id.view_mohe_arab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_mohe:
                startActivity(new Intent(EducationActivity.this, MOHEActivity.class));
                break;
            case R.id.view_mohe_arab:
                startActivity(new Intent(EducationActivity.this, MOHEArabActivity.class));
                break;
        }
    }
}
