package net.soluspay.cashq;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import net.soluspay.cashq.utils.MessageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Mohamed Jaafar on 16/02/19.
 */

public class CardDialog extends DialogFragment {

    @BindView(R.id.pin)
    EditText pin;
    Unbinder unbinder;
    @BindView(R.id.confirm)
    Button confirm;

    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fullscreen_dialog_close)
    ImageButton fullscreenDialogClose;
    @BindView(R.id.service_name)
    TextView serviceName;

    private Callback callback;
    private Card card, selectedCard;
    CardAdapter adapter;

    private static final String TAG = "ZTDEMO";
    private LocalBroadcastManager mLocalBroadcastManager;
    MessageManager messageManager;
    HandlerThread mWaitForCardHandlerThread;

    List<Card> cards;

    CardDBManager dbManager;

    public static CardDialog newInstance() {

        return new CardDialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_card, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        //getCards();
        //creating recyclerview adapter
        List<Card> cardList = getAllCards();
        adapter = new CardAdapter(getActivity(), cardList);
        //setting adapter to recyclerview
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();

        // this is nullable interface. Let's avoid this thing in the future
        String service = args.getString("service");
        String amount = args.getString("amount");

        serviceName.setText(service);
        total.setText(amount);

    }

    @OnClick(R.id.confirm)
    public void onViewClicked() {

        //FIXME what to do when the adapter returns an empty card
        if (adapter.getItemCount() < 1) {
            Toast.makeText(getActivity(), R.string.card_null_error, Toast.LENGTH_SHORT).show();
            return;
        }
        selectedCard = Objects.requireNonNull(adapter.getItem(adapter.mSelectedItem));

        selectedCard.setIpin(pin.getText().toString());
        if (isNetworkAvailable()) {
            callback.onActionClick(selectedCard);
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @OnClick({R.id.fullscreen_dialog_close})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.fullscreen_dialog_close) {
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface Callback {

        void onActionClick(Card card);

    }

    // Maybe add a prompt here if cards were empty? To notify the user to submit a card
    public List<Card> getAllCards() {

        // sorting orders
        //String sortOrder = RECIPE_TITLE + " ASC";
        List<Card> cardList = new ArrayList<Card>();

        dbManager = new CardDBManager(getActivity());
        dbManager.open();

        Cursor cursor = dbManager.fetch();
        // Traversing through all rows and adding to list
        // nice do while
        if (cursor.moveToFirst()) {

            do {

                Card card = new Card();
                card.setPan(cursor.getString(cursor.getColumnIndex("pan")));
                card.setExpDate(cursor.getString(cursor.getColumnIndex("expdate")));
                card.setName(cursor.getString(cursor.getColumnIndex("name")));

                // Adding user record to list
                cardList.add(card);
            } while (cursor.moveToNext());
        }

        cursor.close();
        dbManager.close();

        // return user list
        return cardList;
    }

    public void getCards() {

        final ProgressDialog progressDialog;
        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.loading_wait), false, false);
        EBSRequest request = new EBSRequest();

        SharedPreferences sp = getActivity().getSharedPreferences("credentials", Activity.MODE_PRIVATE);
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
                try {
                    JSONArray cardArray = response.getJSONArray("cards");
                    cards = new ArrayList<>();
                    for (int i = 0; i < cardArray.length(); i++) {
                        String name = cardArray.getJSONObject(i).getString("name");
                        String pan = cardArray.getJSONObject(i).getString("pan");
                        String expDate = cardArray.getJSONObject(i).getString("exp_date");
                        Log.i("MESSAGE", cardArray.toString());
                        cards.add(new Card(name, pan, expDate));
                    }

                    //creating recyclerview adapter
                    adapter = new CardAdapter(getActivity(), cards);
                    //setting adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}


