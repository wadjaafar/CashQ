package com.tutipay.app.fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.gndi_sd.szzt.R;

import com.tutipay.app.BalanceInquiryActivity;
import com.tutipay.app.BillersActivity;
import com.tutipay.app.E15Activity;
import com.tutipay.app.ElectricityActivity;
import com.tutipay.app.FundsTransferActivity;
import com.tutipay.app.IPinActivity;
import com.tutipay.app.QRPayActivity;
import com.tutipay.app.TelecomActivity;
import com.tutipay.app.VoucherActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.view_balance)
    ConstraintLayout viewBalance;

    @BindView(R.id.view_transfer)
    ConstraintLayout viewTransfer;

    @BindView(R.id.request_ipin)
    ConstraintLayout viewIpin;

    @BindView(R.id.view_telecom)
    ConstraintLayout viewTelecom;

    @BindView(R.id.view_electricity)
    ConstraintLayout viewElectricity;

    @BindView(R.id.view_qr_payment)
    ConstraintLayout viewQrPayment;

    @BindView(R.id.view_e15)
    ConstraintLayout viewE15;

    @BindView(R.id.view_billers)
    ConstraintLayout viewBillers;

    @BindView(R.id.view_voucher)
    ConstraintLayout viewVouchers;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        updateDashboard(rootView);

        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

//    private void updateDashboard(View rootView) {
//        SharedPreferences sp = getActivity().getSharedPreferences("result", Activity.MODE_PRIVATE);
//        // how can we debug shared preferences...
//        String b = sp.getString("balance", "");
//
//        TextView tv = rootView.findViewById(R.id.dashboardBalance);
//        tv.setText(b);
//        tv.invalidate();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.view_qr_payment, R.id.view_balance, R.id.view_transfer, R.id.view_telecom, R.id.view_electricity, R.id.view_e15, R.id.request_ipin,
            R.id.view_billers, R.id.view_voucher})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_balance:
                startActivity(new Intent(getActivity(), BalanceInquiryActivity.class));
                break;
            case R.id.view_transfer:
                startActivity(new Intent(getActivity(), FundsTransferActivity.class));
                break;
            case R.id.view_telecom:
                startActivity(new Intent(getActivity(), TelecomActivity.class));
                break;
            case R.id.view_electricity:
                startActivity(new Intent(getActivity(), ElectricityActivity.class));
                break;
            case R.id.view_qr_payment:
                startActivity(new Intent(getActivity(), QRPayActivity.class));
                break;

            case R.id.view_e15:
                startActivity(new Intent(getActivity(), E15Activity.class));
                break;
            case R.id.request_ipin:
                startActivity(new Intent(getActivity(), IPinActivity.class));
                break;
            case R.id.view_billers:
                startActivity(new Intent(getActivity(), BillersActivity.class));
                break;
            case R.id.view_voucher:
                startActivity(new Intent(getActivity(), VoucherActivity.class));
                break;
        }
    }
}
