package com.example.saveit;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import helpclass.Money;
import helpclass.PeriodArrayAdapter;

/**
 * The StatisticalActivity can show the cost ,income and Balance
 * in different period.
 * @author Zilin.Song
 *
 */
public class StatisticalActivity extends AppCompatActivity {
    /**
     * The Period spinner.
     */
    Spinner PeriodSpinner ;
    /**
     * The Balance text view.
     */
    TextView BalanceTextView;
    /**
     * The Cost text view.
     */
    TextView CostTextView;
    /**
     * The Income text view.
     */
    TextView IncomeTextView;
    /**
     * The Money list.
     */
    List<Money> moneyList = new ArrayList<>();

    /**
     * The Income.
     */
    int Income, /**
     * The Cost.
     */
    Cost, /**
     * The Balance.
     */
    Balance;
    /**
     * The Period.
     */
    int Period = 0 ; // 0:Last 7 Days   1:Last 30 Days   2:This Week   3: This Month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);
        assignObjects();
        initPeriodSpinner();
        initContent();
        SpinnerListener();
    }
    private void assignObjects() {
        PeriodSpinner = findViewById(R.id.periodspinner);
        BalanceTextView = findViewById(R.id.BalanceTextView);
        CostTextView = findViewById(R.id.CostTextView);
        IncomeTextView = findViewById(R.id.IncomeTextView);
    }
    private void initPeriodSpinner() {
        ArrayAdapter<String> mAdapter;
        String[] mStringArray;
        mStringArray = getResources().getStringArray(R.array.Period);
        mAdapter = new PeriodArrayAdapter(StatisticalActivity.this, mStringArray);
        PeriodSpinner.setAdapter(mAdapter);
    }
    private void initContent(){
        Bundle bundle = this.getIntent().getExtras();
        moneyList = (List<Money>) bundle.get("List");
    }
    private void SpinnerListener(){
        PeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Period = i;
                try {
                    getStatic();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                BalanceTextView.setText(String.valueOf(Income+Cost));
                IncomeTextView.setText(String.valueOf(Income));
                CostTextView.setText(String.valueOf(-Cost));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void getStatic() throws ParseException {
        Cost = 0;
        Income = 0;
        Balance = 0;
        if (Period == 0){ // Last 7 days
            for (Money money: moneyList
                 ) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, - 7);
                Date sevenDays = c.getTime();
                Date currentDate=new Date();
                if (!money.getDatedate().before(sevenDays) && (!money.getDatedate().after(currentDate))){
                    if (money.getType().equals("Cost")){
                        Cost += money.getDoubleValue();
                    }else if (money.getType().equals("Income")){
                        Income += money.getDoubleValue();
                    }
                }
            }
        }else if (Period == 1){ // Last 30 days
            for (Money money: moneyList
            ) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, - 30);
                Date sevenDays = c.getTime();
                Date currentDate=new Date();
                if (!money.getDatedate().before(sevenDays) && (!money.getDatedate().after(currentDate))){
                    if (money.getType().equals("Cost")){
                        Cost += money.getDoubleValue();
                    }else if (money.getType().equals("Income")){
                        Income += money.getDoubleValue();
                    }
                }
            }
        }else if (Period == 2){// This week
            for (Money money: moneyList
            ) {
                Date currentDate=new Date();
                if (isThisWeek(money.getDatedate()) && (!money.getDatedate().after(currentDate))){
                    if (money.getType().equals("Cost")){
                        Cost += money.getDoubleValue();
                    }else if (money.getType().equals("Income")){
                        Income += money.getDoubleValue();
                    }
                }
            }
        }else if (Period == 3){//This Month
            for (Money money: moneyList
            ) {
                Date currentDate=new Date();
                if (isThisMonth(money.getDatedate()) && (!money.getDatedate().after(currentDate))){
                    if (money.getType().equals("Cost")){
                        Cost += money.getDoubleValue();
                    }else if (money.getType().equals("Income")){
                        Income += money.getDoubleValue();
                    }
                }
            }
        }
    }

    /**
     * Check if the Date in this Week.
     *
     * @param time the time
     * @return the boolean
     */
    public static boolean isThisWeek(Date time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(time);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    /**
     * Check if the Date in this Month
     *
     * @param date the date
     * @return the boolean
     */
    public static boolean isThisMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

}