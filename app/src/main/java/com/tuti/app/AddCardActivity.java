package com.tutipay.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.gndi_sd.szzt.R;

import com.tutipay.app.model.EBSRequest;
import com.tutipay.app.utils.CardDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCardActivity extends AppCompatActivity {

    @BindView(R.id.pan)
    EditText pan;
    @BindView(R.id.card_name)
    EditText cardName;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.exp_date)
    TextView expDate;
    @BindView(R.id.checkBox)
    CheckBox checkBox;

    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.add_new_card_prompt));

    }

    public void addCard() {

        CardDBManager dbManager;
        dbManager = new CardDBManager(this);
        dbManager.open();

        if (checkBox.isChecked()) {
            final ProgressDialog progressDialog;
            progressDialog = ProgressDialog.show(this, getResources().getText(R.string.loading), getResources().getText(R.string.loading_wait), false, false);
            EBSRequest request = new EBSRequest();

            SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
            String token = sp.getString("token", null);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("pan", pan.getText().toString());
                jsonObject.put("name", cardName.getText().toString());
                jsonObject.put("exp_date", date);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AndroidNetworking.post(request.serverUrl() + Constants.ADD_CARD)
                    .addJSONObjectBody(jsonObject)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .addHeaders("Authorization", token)
                    .build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    // do anything with response

                    // successful response
                    toDb(dbManager, pan.getText().toString(), date, cardName.getText().toString());

                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
                    builder.setTitle(getString(R.string.success))
                            .setMessage("Your card has been added successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> {
                                //do things
                                finish();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    Log.i("Add Card", error.getErrorBody());
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
                    builder.setTitle(getString(R.string.failure))
                            .setMessage("Something went wrong")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, id) -> {
                                //do things
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.i("MESSAGE", error.getErrorBody());
                }
            });
        } else {
//            String pan
            toDb(dbManager, pan.getText().toString(), date, cardName.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(AddCardActivity.this);
            builder.setTitle(getString(R.string.success))
                    .setMessage("Your card has been added successfully")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        //do things
                        finish();
                        navigateUpTo(new Intent(AddCardActivity.this, CardActivity.class));
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        // save to the local db
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    private void toDb(CardDBManager dbManager, String pan, String expDate, String name) {
        dbManager.insert(pan, expDate, name);
        dbManager.close();
    }

    public void pickDate() {
        int yearSelected;
        int monthSelected;

        // Use the calendar for create ranges
        Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set(2019, 8, 1); // Set minimum date to show in dialog
        long minDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

        calendar.clear();
        calendar.set(2030, 11, 31); // Set maximum date to show in dialog
        long maxDate = calendar.getTimeInMillis(); // Get milliseconds of the modified date

        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);

        String customTitle = "Pick a date";

        MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                .getInstance(monthSelected, yearSelected, minDate, maxDate, customTitle);

        dialogFragment.show(getSupportFragmentManager(), null);

        dialogFragment.setOnDateSetListener((year, monthOfYear) -> {
            // do something
            String yearString = Integer.toString(year);
            int month = monthOfYear + 1;
            expDate.setText(String.format(Locale.US, "%02d / %s", month, year));
            date = String.format(Locale.US, "%s%02d", yearString.substring(2), month);
            Log.i("Date", date);
        });
    }


    @OnClick(R.id.save)
    public void onSaveClicked() {
        boolean error = false;
        if(pan.getText().toString().isEmpty())
        {
            error = true;
            pan.setError("Enter a card number");
        }
        if(!(pan.getText().toString().length() == 16 || pan.getText().toString().length() == 19))
        {
            error = true;
            pan.setError("Enter a valid card number");
        }
        if(cardName.getText().toString().isEmpty())
        {
            error = true;
            cardName.setError("Card name cannot be empty");
        }
        if(!error)
        {
            addCard();
        } else {
            //manage error case here
        }
    }

    @OnClick(R.id.exp_date)
    public void onViewClicked() {
        pickDate();
    }
}
