package net.soluspay.cashq.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.os.ConfigurationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gndi_sd.szzt.R;

import net.soluspay.cashq.model.Card;

import java.util.List;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    public int mSelectedItem = 0;

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Card> cardList;

    //getting the context and product list with constructor
    public CardAdapter(Context mCtx, List<Card> cardList) {
        this.mCtx = mCtx;
        this.cardList = cardList;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.card_design, parent, false );
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        //getting the product of the specified position
        Card card = cardList.get(position);

        //binding the data with the viewholder views
        // how to deal with rtl
        Locale current = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
        Log.i("locale", "the current locale is: " + current.toString());
        if (current.toString().equals("ar_SA")) {
            holder.name.setTextLocale(current);
            holder.pan.setTextLocale(current);

            holder.expDate.setTextLocale(current);
            holder.name.setText("بطاقة كاشك");
            holder.name.setTextLocale(current);
//            holder.name.setText(card.getName());
            holder.pan.setText(card.getPan());
            holder.expDate.setText(card.getExpDate());
            holder.logo.setImageResource(R.drawable.logo_white); // you can set a logo here. VERY interesting
            holder.radioButton.setChecked(position == mSelectedItem);
            return;
        }
        holder.name.setTextLocale(current);
        holder.pan.setTextLocale(current);
        holder.expDate.setTextLocale(current);

        holder.name.setText(card.getName());
        holder.pan.setText(card.getPan());
        holder.expDate.setText(card.getExpDate());
        holder.logo.setImageResource(R.drawable.logo_white); // you can set a logo here. VERY interesting
        holder.radioButton.setChecked(position == mSelectedItem);
    }

    public Card getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView name, pan, expDate;
        ImageView logo;
        RadioButton radioButton;

        public CardViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.card_holder);
            pan = itemView.findViewById(R.id.card_number);
            expDate = itemView.findViewById(R.id.expired_date);
            logo = itemView.findViewById(R.id.logo);
            radioButton = itemView.findViewById(R.id.radioButton);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                }
            };

            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);

            View.OnLongClickListener longClickListener = new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            };

            itemView.setOnLongClickListener(longClickListener);

        }
    }

}
