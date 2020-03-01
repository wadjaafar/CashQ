package net.soluspay.cashq.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gndi_sd.szzt.R;

import net.soluspay.cashq.BalanceInquiryActivity;
import net.soluspay.cashq.E15Activity;
import net.soluspay.cashq.ElectricityActivity;
import net.soluspay.cashq.FundsTransferActivity;
import net.soluspay.cashq.QRPayActivity;
import net.soluspay.cashq.ServicePayment;
import net.soluspay.cashq.TelecomActivity;

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
    //    @BindView(R.id.view_voucher)
//    ConstraintLayout viewVoucher;
    @BindView(R.id.view_transfer)
    ConstraintLayout viewTransfer;
    @BindView(R.id.view_telecom)
    ConstraintLayout viewTelecom;
    @BindView(R.id.view_electricity)
    ConstraintLayout viewElectricity;
    @BindView(R.id.view_qr_payment)
    ConstraintLayout viewQrPayment;
//    @BindView(R.id.view_education)
//    ConstraintLayout viewEducation;
    @BindView(R.id.view_e15)
    ConstraintLayout viewE15;
//    @BindView(R.id.view_customs)
//    ConstraintLayout viewCustoms;
    @BindView(R.id.view_purchase)
    ConstraintLayout viewPurchase;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.view_qr_payment, R.id.view_balance, R.id.view_purchase, R.id.view_transfer, R.id.view_telecom, R.id.view_electricity, R.id.view_e15})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_balance:
                startActivity(new Intent(getActivity(), BalanceInquiryActivity.class));
                break;
            case R.id.view_purchase:
                startActivity(new Intent(getActivity(), ServicePayment.class));
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
//            case R.id.view_education:
//                startActivity(new Intent(getActivity(), EducationActivity.class));
//                break;
            case R.id.view_e15:
                startActivity(new Intent(getActivity(), E15Activity.class));
                break;
//            case R.id.view_customs:
//                startActivity(new Intent(getActivity(), CustomsActivity.class));
//                break;
        }
    }
}
