package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.math.BigDecimal;


import java.math.RoundingMode;

class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private SharedPreferences myPreferences;

    private EditText etTotalBill;
    private EditText etNoOfPeople;
    private RadioGroup rbTipPercent;
    private TextView tvTipAmount;
    private TextView tvTotalWithTip;
    private TextView tvTotalPerPerson;
    private TextView tvOverage;

    private double tipPercentage = 0;
    private double totalWithTip;
    private double tipAmount;
    private double totalPerPerson;
    private double totalAgain;
    private double overage;
    private int buttonID = -1;
    private String valStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTotalBill = findViewById(R.id.numberBillTotal);
        etNoOfPeople = findViewById(R.id.noOfPeopleNumber);
        rbTipPercent = findViewById(R.id.tipPercentage);
        tvTipAmount = findViewById(R.id.numberTipAmount);
        tvTotalWithTip = findViewById(R.id.numberTotalWithTip);
        tvTotalPerPerson = findViewById(R.id.numberTotalPerPerson);
        tvOverage = findViewById(R.id.numberOverage);

        Log.d(TAG, "created: ");

        myPreferences = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);

        String pTotalBill = myPreferences.getString("pTotalBill", "");
        String pTotalWithTip = myPreferences.getString("pTotalWithTip", "");
        String pTipAmount = myPreferences.getString("pTipAmount", "");
        String pNoPeople = myPreferences.getString("pNoPeople", "");
        String pTotalPerPerson = myPreferences.getString("pTotalPerPerson", "");
        String pOverage = myPreferences.getString("pOverage", "");

        etTotalBill.setText(pTotalBill);
        etNoOfPeople.setText(pNoPeople);
        tvTipAmount.setText(pTipAmount);
        tvTotalWithTip.setText(pTotalWithTip);
        tvTotalPerPerson.setText(pTotalPerPerson);
        tvOverage.setText(pOverage);
    }

    public void radioClicked(View v) {


        if (buttonID != rbTipPercent.getCheckedRadioButtonId() || !valStr.equals(etTotalBill.getText().toString())){
            valStr = etTotalBill.getText().toString();
            buttonID = rbTipPercent.getCheckedRadioButtonId();
            tvTotalPerPerson.setText("");
            tvOverage.setText("");
        }

        String totalBill = etTotalBill.getText().toString();
        double dTotalBill = Double.parseDouble(totalBill);


        RadioButton rb = null;
        if (v.getId() == R.id.tipPercent1) {
            rb = findViewById(R.id.tipPercent1);
            tipPercentage = 0.12;
        } else if (v.getId() == R.id.tipPercent2) {
            rb = findViewById(R.id.tipPercent2);
            tipPercentage = 0.15;
        } else if (v.getId() == R.id.tipPercent3) {
            rb = findViewById(R.id.tipPercent3);
            tipPercentage = 0.18;
        } else if (v.getId() == R.id.tipPercent4) {
            rb = findViewById(R.id.tipPercent4);
            tipPercentage = 0.20;
        } else {
            return;
        }

        if(valStr.isEmpty()){
            rb.setChecked(false);
            tvTotalWithTip.setText("");
            tvTipAmount.setText("");
        }
        else if( !valStr.isEmpty() && !rb.isChecked()){
            tvTotalWithTip.setText("");
            tvTipAmount.setText("");
        }
        else {
            tipAmount = dTotalBill * tipPercentage;
            BigDecimal ta = new BigDecimal(tipAmount).setScale(2, RoundingMode.HALF_UP);
            double roundedTipAmount = ta.doubleValue();
            tvTipAmount.setText(String.valueOf(roundedTipAmount));

            totalWithTip = dTotalBill + tipAmount;
            BigDecimal twt = new BigDecimal(tipAmount).setScale(2, RoundingMode.HALF_UP);
            double roundedTotalWithTip = twt.doubleValue();
            tvTotalWithTip.setText(String.valueOf(roundedTotalWithTip));

            Log.d(TAG, "radioClicked: " + totalBill + " * " + String.valueOf(tipPercentage) +
                    " = " + String.valueOf(roundedTipAmount));
            Log.d(TAG, "radioClicked: " + totalBill + " + " + String.valueOf(roundedTipAmount) +
                    " = " + String.valueOf(roundedTotalWithTip));
        }
    }

    public void goClicked(View v) {

        String sNoOfPeople = etNoOfPeople.getText().toString();
        int iNoOfPeople = Integer.parseInt(sNoOfPeople);

        if(sNoOfPeople.isEmpty() || tvTotalWithTip.getText().toString().isEmpty()) {
            tvTotalPerPerson.setText("");
            tvOverage.setText("");
        }

        else if (!sNoOfPeople.isEmpty()){
            if(iNoOfPeople == 0){
                etNoOfPeople.setText("");
            }
            else {
                totalPerPerson = totalWithTip / iNoOfPeople;
                BigDecimal tpp = new BigDecimal(totalPerPerson).setScale(2, RoundingMode.HALF_UP);
                double roundedTotalPerPerson = tpp.doubleValue();
                tvTotalPerPerson.setText(String.valueOf(roundedTotalPerPerson));
                Log.d(TAG, "goClicked: " + String.valueOf(totalWithTip) + " / " + String.valueOf(iNoOfPeople));

                totalAgain = roundedTotalPerPerson * iNoOfPeople;
                BigDecimal ta = new BigDecimal(totalAgain).setScale(2, RoundingMode.HALF_UP);
                double roundedTotalAgain = ta.doubleValue();

                overage = roundedTotalAgain - totalWithTip;
                BigDecimal rOverage = new BigDecimal(overage).setScale(2, RoundingMode.HALF_UP);
                double roundedOverage = rOverage.doubleValue();
                tvOverage.setText(String.valueOf(roundedOverage));
            }
        }
        copy();
    }

    public void clearClicked(View v) {
        etTotalBill.setText("");
        tvTipAmount.setText("");
        tvTotalWithTip.setText("");
        etNoOfPeople.setText("");
        tvTotalPerPerson.setText("");
        tvOverage.setText("");
        RadioButton rb = findViewById(rbTipPercent.getCheckedRadioButtonId());
        rb.setChecked(false);
        copy();
    }

    public void copy(){
        SharedPreferences.Editor preferencesEditor = myPreferences.edit();
        preferencesEditor.putString("pTotalBill", etTotalBill.getText().toString());
        preferencesEditor.putString("pTipAmount",tvTipAmount.getText().toString());
        preferencesEditor.putString("pTotalWithTip", tvTotalWithTip.getText().toString());
        preferencesEditor.putString("pNoOfPeople", etNoOfPeople.getText().toString());
        preferencesEditor.putString("pTotalPerPerson", tvTotalPerPerson.getText().toString());
        preferencesEditor.putString("pOverage", tvOverage.getText().toString());
        preferencesEditor.apply();
    }
}