package net.soluspay.cashq;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gndi_sd.szzt.R;
import com.google.common.base.Splitter;

import net.soluspay.cashq.model.Card;
import net.soluspay.cashq.model.EBSResponse;
import net.soluspay.cashq.utils.Globals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.result_table)
    TableLayout resultTable;

    EBSResponse ebsResponse;
    Card card;

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.response)
    TextView response;
    @BindView(R.id.message)
    TextView message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setTitle(getString(R.string.result_word));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        ebsResponse = (EBSResponse) intent.getSerializableExtra("response");
        // get sp here and save it
        SharedPreferences sp = getSharedPreferences("result", Activity.MODE_PRIVATE);
        // how can we debug shared preferences...


        card = (Card) intent.getSerializableExtra("card");
        response.setText(Globals.serviceName);
        message.setText(ebsResponse.getResponseMessage());
        if (ebsResponse.getResponseStatus().equals("Successful")) {
            sp.edit().putString("balance", ebsResponse.getAvailableBalance()).apply();
            image.setImageResource(R.drawable.ic_success);
            Log.d(" ServiceName " , Globals.service);
            Map<String, String> paymentInfo;
            switch (Globals.service) {
                case "purchase":
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    addRow("To account", ebsResponse.getToAccount());
                    break;
                case "electricity":
                    addRow("Token", ebsResponse.getBillInfo().get("token"));
                    addRow("Units in Kwh", ebsResponse.getBillInfo().get("unitsInKWh"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Net Amount", ebsResponse.getBillInfo().get("netAmount"));
                    addRow("Meter Number", ebsResponse.getBillInfo().get("meterNumber"));
                    addRow("Customer Name", ebsResponse.getBillInfo().get("customerName"));
                    addRow("Meter Fees", ebsResponse.getBillInfo().get("meterFees"));
                    addRow("Water Fees", ebsResponse.getBillInfo().get("waterFees"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "customsInquiry":
                    addRow("Amount", ebsResponse.getBillInfo().get("Amount"));
                    addRow("AmountToBePaid", ebsResponse.getBillInfo().get("AmountToBePaid"));
                    addRow("Bank Code", ebsResponse.getBillInfo().get("BankCode"));
                    addRow("Declarant Code", ebsResponse.getBillInfo().get("DeclarantCode"));
                    addRow("RegistrationNumber", ebsResponse.getBillInfo().get("RegistrationNumber"));
                    addRow("RegistrationSerial", ebsResponse.getBillInfo().get("RegistrationSerial"));
                    addRow("Status", ebsResponse.getBillInfo().get("Status"));
                    addRow("Year", ebsResponse.getBillInfo().get("Year"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "customsPayment":
                    addRow("Amount", ebsResponse.getBillInfo().get("Amount"));
                    addRow("Bank Code", ebsResponse.getBillInfo().get("BankCode"));
                    addRow("Declarant Code", ebsResponse.getBillInfo().get("DeclarantCode"));
                    addRow("E-15 Receipt Number", ebsResponse.getBillInfo().get("E-15ReceiptNumber"));
                    addRow("Receipt Date", ebsResponse.getBillInfo().get("ReceiptDate"));
                    addRow("Receipt Number", ebsResponse.getBillInfo().get("ReceiptNumber"));
                    addRow("Receipt Serial", ebsResponse.getBillInfo().get("ReceiptSerial"));
                    addRow("Status", ebsResponse.getBillInfo().get("Status"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "balance":
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    addRow("Balance", formatter.format(ebsResponse.getBalance().get("available")));
                    addRow("Ledger", formatter.format(ebsResponse.getBalance().get("leger")));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", formatter.format(ebsResponse.getIssuerTranFee()));
                    break;
                case "cardTransfer":
                    addRow("To Card", ebsResponse.getToCard());
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "accountTransfer":
                    addRow("To Account", ebsResponse.getToAccount());
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "zainTopup":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "sudaniTopup":
                    paymentInfo= Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "mtnTopup":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "zainInquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Total Amount", ebsResponse.getBillInfo().get("totalAmount"));
                    addRow("Billed Amount", ebsResponse.getBillInfo().get("billedAmount"));
                    addRow("Unbilled Amount", ebsResponse.getBillInfo().get("unbilledAmount"));
                    addRow("Contract Number", ebsResponse.getBillInfo().get("contractNumber"));
                    addRow("Last 4 Digits", ebsResponse.getBillInfo().get("last4Digits"));
                    addRow("Last Invoice Date", ebsResponse.getBillInfo().get("lastInvoiceDate"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "sudaniInquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Subscriber ID", ebsResponse.getBillInfo().get("SubscriberID"));
                    addRow("Bill Amount", ebsResponse.getBillInfo().get("billAmount"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "mtnInquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Total Amount", ebsResponse.getBillInfo().get("total"));
                    addRow("Bill Amount", ebsResponse.getBillInfo().get("BillAmount"));
                    addRow("Unbilled Amount", ebsResponse.getBillInfo().get("unbilledAmount"));
                    addRow("Contract Number", ebsResponse.getBillInfo().get("contractNumber"));
                    addRow("Last Invoice Date", ebsResponse.getBillInfo().get("lastInvoiceDate"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "zainPayment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Receipt No", ebsResponse.getBillInfo().get("receiptNo"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "sudaniPayment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Subscriber ID", ebsResponse.getBillInfo().get("SubscriberID"));
                    addRow("Bill Amount", ebsResponse.getBillInfo().get("billAmount"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "mtnPayment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Phone", paymentInfo.get("MPHONE"));
                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "moheInquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Seat No", paymentInfo.get("SETNUMBER"));
                    addRow("Name", ebsResponse.getBillInfo().get("arabicName"));
                    addRow("Due Amount", ebsResponse.getBillInfo().get("dueAmount"));
                    addRow("Form No", ebsResponse.getBillInfo().get("formNo"));
                    addRow("Receipt No", ebsResponse.getBillInfo().get("receiptNo"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "mohePayment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Seat No", paymentInfo.get("SETNUMBER"));
                    addRow("Name", ebsResponse.getBillInfo().get("arabicName"));
                    addRow("Form No", ebsResponse.getBillInfo().get("formNo"));
                    addRow("Receipt No", ebsResponse.getBillInfo().get("receiptNo"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "moheArabInquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Name", ebsResponse.getBillInfo().get("arabicName"));
                    addRow("Student Phone", paymentInfo.get("STUCPHONE"));
                    addRow("Due Amount", ebsResponse.getBillInfo().get("dueAmount"));
                    addRow("Form No", ebsResponse.getBillInfo().get("formNo"));
                    addRow("Receipt No", ebsResponse.getBillInfo().get("receiptNo"));
                    addRow("Student No", ebsResponse.getBillInfo().get("studentNo"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "moheArabPayment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Name", ebsResponse.getBillInfo().get("arabicName"));
                    addRow("Student Phone", paymentInfo.get("STUCPHONE"));
                    addRow("Form No", ebsResponse.getBillInfo().get("formNo"));
                    addRow("Receipt No", ebsResponse.getBillInfo().get("receiptNo"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "e15Inquiry":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Invoice No", paymentInfo.get("INVOICENUMBER"));
                    addRow("Phone No", paymentInfo.get("PHONENUMBER"));
                    addRow("Due Amount", ebsResponse.getBillInfo().get("DueAmount"));
                    addRow("Invoice Expiry", ebsResponse.getBillInfo().get("InvoiceExpiry"));
                    if (ebsResponse.getBillInfo().get("InvoiceStatus").equals("1")){
                        addRow("Invoice Status", "PENDING");
                    } else if (ebsResponse.getBillInfo().get("InvoiceStatus").equals("2")){
                        addRow("Invoice Status", "PAID");
                    } else {
                        addRow("Invoice Status", "CANCELED");
                    }
                    addRow("Payer Name", ebsResponse.getBillInfo().get("PayerName"));
                    addRow("Reference Id", ebsResponse.getBillInfo().get("ReferenceId"));
                    addRow("Service Name", ebsResponse.getBillInfo().get("ServiceName"));
                    addRow("Total Amount", ebsResponse.getBillInfo().get("TotalAmount"));
                    addRow("Unit Name", ebsResponse.getBillInfo().get("UnitName"));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;
                case "e15Payment":
                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
                    addRow("Invoice No", paymentInfo.get("INVOICENUMBER"));
                    addRow("Phone No", paymentInfo.get("PHONENUMBER"));
                    addRow("Payer Name", ebsResponse.getBillInfo().get("PayerName"));
                    addRow("Reference Id", ebsResponse.getBillInfo().get("ReferenceId"));
                    addRow("Service Name", ebsResponse.getBillInfo().get("ServiceName"));
                    addRow("Total Amount", ebsResponse.getBillInfo().get("TotalAmount"));
                    addRow("Unit Name", ebsResponse.getBillInfo().get("UnitName"));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    break;

                case "billers":
//                    paymentInfo = Splitter.on("/").withKeyValueSeparator("=").split(ebsResponse.getPaymentInfo());
//                    addRow("Amount", String.valueOf(ebsResponse.getTranAmount()));
                    addRow("Currency", ebsResponse.getTranCurrency());
                    addRow("Fees", String.valueOf(ebsResponse.getIssuerTranFee()));
                    addRow("Card Number", ebsResponse.getPAN());
                    addRow("Date", ebsResponse.getTranDateTime());
                    addRow("From Account", ebsResponse.getFromAccount());
                    break;

            }
        } else {
            image.setImageResource(R.drawable.ic_fail);
        }
    }

    public void addRow(String key, String value){
        resultTable.setColumnShrinkable(1,true);
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        TextView textKey = new TextView(this);
        textKey.setText(key);
        textKey.setGravity(Gravity.LEFT);
        textKey.setPadding(10, 10, 10, 10);
        textKey.setTextSize(16.0f);
        textKey.setTypeface(Typeface.DEFAULT_BOLD);
        TextView textValue = new TextView(this);
        textValue.setText(value);
        textValue.setGravity(Gravity.RIGHT);
        textValue.setPadding(10, 10, 10, 10);
        textValue.setTextSize(16.0f);
        textValue.setSingleLine(false);
        row.addView(textKey, 0);
        row.addView(textValue, 1);
        resultTable.addView(row);

    }

    public static double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }

}
