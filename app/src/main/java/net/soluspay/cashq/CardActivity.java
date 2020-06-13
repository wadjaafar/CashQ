package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.gndi_sd.szzt.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.soluspay.cashq.adapter.CardAdapter;
import net.soluspay.cashq.model.Card;
import net.soluspay.cashq.model.EBSRequest;
import net.soluspay.cashq.utils.CardDBManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardActivity extends AppCompatActivity {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.sync)
    FloatingActionButton sync;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Card> cards;

    CardDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.my_cards_title));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbManager = new CardDBManager(this);
        dbManager.open();
        getCardsOffline();

    }

    public void getCards() {
        // this should fetch results locally *FIRST* and then opt-in to
        // remote updates

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(this, getResources().getText(R.string.loading), getResources().getText(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getSharedPreferences("credentials", Activity.MODE_PRIVATE);
        String token = sp.getString("token", null);

        AndroidNetworking.get(request.serverUrl() + Constants.GET_CARDS)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization", token)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // do anything with response
                Log.i("MESSAGE", response.toString());
                // remove the old db FIXME
                dbManager.deleteAll(); // this is where we fuck up...

                try {
                    JSONArray cardArray = response.getJSONArray("cards");
                    cards = new ArrayList<Card>();
                    for (int i = 0; i < cardArray.length(); i++) {
                        String name = cardArray.getJSONObject(i).getString("name");
                        String pan = cardArray.getJSONObject(i).getString("pan");
                        String expDate = cardArray.getJSONObject(i).getString("exp_date");
                        int id = cardArray.getJSONObject(i).getInt("id");
                        Log.i("MESSAGE", cardArray.toString());
                        cards.add(new Card(id, name, pan, expDate));
                        // should remove the previous DB!
                        // and replace them with the current ones
                        dbManager.insert(pan, expDate, name);
                    }

                    //creating recyclerview adapter
                    CardAdapter adapter = new CardAdapter(getApplicationContext(), cards);
                    //setting adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                    Log.i("MESSAGE", "Success");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onError(ANError error) {
                // handle error
                progressDialog.dismiss();
                Log.i("MESSAGE", "Fail");


            }
        });
    }


    public void getCardsOffline() {
        // this should fetch results locally *FIRST* and then opt-in to
        // remote updates

        Cursor cursor = dbManager.fetch();

        cards = new ArrayList<Card>();
        for (int i = 0; i < cursor.getCount(); i++) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String pan = cursor.getString(cursor.getColumnIndex("pan"));
            String expDate = cursor.getString(cursor.getColumnIndex("expdate"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            cards.add(new Card(id, name, pan, expDate));
            cursor.moveToNext();
        }
        //creating recyclerview adapter

        CardAdapter adapter = new CardAdapter(getApplicationContext(), cards);
        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        dbManager.getAll();

    }


    @Override
    protected void onResume() {
        super.onResume();
//        getCards();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        startActivity(new Intent(CardActivity.this, AddCardActivity.class));
    }

    @OnClick(R.id.sync)
    public void onSyncClicked() {
        getCards();
    }


}
