package com.example.saveit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import alarmmanager.NotificationService;
import helpclass.Money;
import helpclass.MoneyArrayAdapter;

import static com.example.saveit.Saveit.CHANNEL_1_ID;
import static com.example.saveit.Saveit.CHANNEL_2_ID;

/**
 * MainActivity
 * @author Zilin.Song
 */
public class MainActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private static final int MONEY_ACTIVITY_ADD_REQUEST_CODE = 901;
    private static final int MONEY_ACTIVITY_EDIT_REQUEST_CODE = 902;

    private static boolean ifDaily = false;
    private static boolean ifWeekly = false;
    private static boolean ifMonthly = false;


    Calendar c = null;


    private List<Money> moneyList = new ArrayList<>();

    private ListView MoneyListView;
    private TextView YourBalanceView;
    private FloatingActionButton addMoneyButton;
    private FloatingActionButton StaticButton;
    private FloatingActionButton GraphButton;

    private ArrayAdapter<Money> MoneyListViewAdapter;
    /**
     * The Balance.
     */
    public Double balance = 0.0;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        notificationManager = NotificationManagerCompat.from(this);
        loadData();
        setContentView(R.layout.activity_main);
        assignObjects();
        initAddMoneyButton();
        initStatisticalButton();
        initListView();
        initGraphButton();
        initCalender();
        Weekly();
        Monthly();
        super.onCreate(savedInstanceState);
    }


    /**
     * Every time Resume the Activity , this Moneylist will be
     * Sort by date.And the Balance ,weekly cost will be update.
     */
    @Override
    protected void onResume() {
        try {
            updateBalance();
            weeklycost();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sortMoneyList();
        super.onResume();
    }

    /**
     * When the Activity stop , this data will be save and the NotificationService
     * will be start ,when the Daily Reminder is open.
     */

    @Override
    protected void onStop() {
        saveData();
        if (ifDaily) {
            Intent intent1 = new Intent(this, NotificationService.class);
            startService(intent1);
        }
        super.onStop();
    }

    private void initCalender() {
        if (c == null) {
            c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 8);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
        }
    }

    private void sortMoneyList() {
        moneyList.sort(Comparator.comparing(Money::getDatesort).reversed());
        MoneyListViewAdapter.notifyDataSetChanged();
    }


    /**
     * assigns the views defined in the layout to class objects
     */
    private void assignObjects() {
        YourBalanceView = findViewById(R.id.YourBalance);
        MoneyListView = findViewById(R.id.MoneyListview);
        addMoneyButton = findViewById(R.id.addMoneyButton);
        StaticButton = findViewById(R.id.StatisticalButton);
        GraphButton = findViewById(R.id.GraphButton);
    }

    /**
     * init floating action button to add Moneys
     */
    private void initAddMoneyButton() {
        addMoneyButton.setOnClickListener((View v) -> {
            final Intent MoneyActivityAddIntent = new Intent(this, MoneyActivity.class);
            startActivityForResult(MoneyActivityAddIntent, MONEY_ACTIVITY_ADD_REQUEST_CODE);
        });
    }
    /**
     * init floating action button to go to Statistical Activity
     */
    private void initStatisticalButton() {
        StaticButton.setOnClickListener((View v) -> {
            final Intent StatisticalActivityAddIntent = new Intent(this, StatisticalActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("List", (Serializable) moneyList);
            StatisticalActivityAddIntent.putExtras(bundle);
            startActivity(StatisticalActivityAddIntent);
        });
    }
    /**
     * init floating action button to go to Graph Activity
     */
    private void initGraphButton() {
        GraphButton.setOnClickListener((View v) -> {
            final Intent StatisticalActivityAddIntent = new Intent(this, GraphActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("List", (Serializable) moneyList);
            StatisticalActivityAddIntent.putExtras(bundle);
            startActivity(StatisticalActivityAddIntent);
        });
    }

    private void initListView() {
        MoneyListViewAdapter = new MoneyArrayAdapter(this, R.layout.money_list_item, moneyList);
        MoneyListView.setAdapter(MoneyListViewAdapter);

        //init item click
        MoneyListView.setOnItemClickListener((parent, view, position, id) -> {
            final Intent MoneyActivityEditIntent = new Intent(this, MoneyActivity.class);
            final Money currentMoney = MoneyListViewAdapter.getItem(position);

            MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_VALUE_KEY, currentMoney.getValue());
            MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_DATE_KEY, currentMoney.getDate());
            MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_INSTRUCTIONS_KEY, currentMoney.getInstructions());
            MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_TYPE_KEY, currentMoney.getType());
            if (currentMoney.getPicturePath() != null)
                MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_PATH_KEY, currentMoney.getPicturePath());
            MoneyActivityEditIntent.putExtra(MoneyActivity.MONEY_ITEM_POSITION_KEY, position);
            startActivityForResult(MoneyActivityEditIntent, MONEY_ACTIVITY_EDIT_REQUEST_CODE);
        });

        //init item long click
        MoneyListView.setOnItemLongClickListener((parent, view, position, id) -> {
            final Money currentMoney = MoneyListViewAdapter.getItem(position);
            MoneyListViewAdapter.remove(currentMoney);
            try {
                updateBalance();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MONEY_ACTIVITY_ADD_REQUEST_CODE:
                    MoneyListViewAdapter.add(getMoneyFromIntent(data));
                case MONEY_ACTIVITY_EDIT_REQUEST_CODE:
                    updateMoney(data);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private Money getMoneyFromIntent(final Intent data) {
        final String value = data.getStringExtra(MoneyActivity.MONEY_VALUE_KEY);
        final String date = data.getStringExtra(MoneyActivity.MONEY_DATE_KEY);
        final String type = data.getStringExtra(MoneyActivity.MONEY_TYPE_KEY);
        final String instructions = data.getStringExtra(MoneyActivity.MONEY_INSTRUCTIONS_KEY);
        final String path = data.getStringExtra(MoneyActivity.MONEY_PATH_KEY);
        return new Money(value, date, instructions, type, path);
    }

    private void updateMoney(final Intent data) {
        final int itemPosition = data.getIntExtra(MoneyActivity.MONEY_ITEM_POSITION_KEY, -1);
        if (itemPosition != -1) {
            final Money currentMoney = MoneyListViewAdapter.getItem(itemPosition);
            final Money returnedMoney = getMoneyFromIntent(data);
            currentMoney.setValue(returnedMoney.getValue());
            currentMoney.setDate(returnedMoney.getDate());
            currentMoney.setInstructions(returnedMoney.getInstructions());
            currentMoney.setType(returnedMoney.getType());
            currentMoney.setPicturePath(returnedMoney.getPicturePath());

            MoneyListViewAdapter.notifyDataSetChanged();

        }
    }

    private void updateBalance() throws ParseException {
        balance = 0.0;
        for (Money money : moneyList) {
            if (money.getDatedate().after(new Date())) {
                continue;
            }
            balance += money.getDoubleValue();
        }
        balance = (double) Math.round(balance * 100) / 100;
        YourBalanceView.setText(balance.toString());
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(moneyList);
        editor.putString("task list", json);
        editor.putBoolean("ifDaily", ifDaily);
        editor.putBoolean("ifWeekly", ifWeekly);
        editor.putBoolean("ifMonthly", ifMonthly);
        editor.putInt("day", c.get(Calendar.HOUR_OF_DAY));
        editor.putInt("minute", c.get(Calendar.MINUTE));


        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Money>>() {
        }.getType();
        moneyList = gson.fromJson(json, type);
        ifDaily = sharedPreferences.getBoolean("ifDaily", false);
        ifWeekly = sharedPreferences.getBoolean("ifWeekly", false);
        ifMonthly = sharedPreferences.getBoolean("ifMonthly", false);
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("day", 1));
        c.set(Calendar.MINUTE, sharedPreferences.getInt("minute", 1));

        if (moneyList == null) {
            moneyList = new ArrayList<>();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Daily:
                if (ifDaily == true) {
                    Toast.makeText(this, "Daily Notification is close", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Daily Notification is open", Toast.LENGTH_SHORT).show();
                }
                ifDaily = !ifDaily;
                return true;
            case R.id.Week:
                if (ifWeekly == true)
                    Toast.makeText(this, "Weekly Notification is close", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Weekly Notification is open", Toast.LENGTH_SHORT).show();
                ifWeekly = !ifWeekly;
                return true;
            case R.id.Month:
                if (ifMonthly == true)
                    Toast.makeText(this, "Monthly Notification is close", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Monthly Notification is open", Toast.LENGTH_SHORT).show();
                ifMonthly = !ifMonthly;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void Monthly() {
        if (ifMonthly) {
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_monthly)
                        .setContentTitle("Save it!")
                        .setContentText("Time to check your last month cost!")
                        .build();
                notificationManager.notify(1, notification);

            }
        }
    }

    private void Weekly() {
        if (ifWeekly) {
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_monthly)
                        .setContentTitle("Save it!")
                        .setContentText("Time to check your last week cost!")
                        .build();
                notificationManager.notify(1, notification);
            }
        }
    }

    private void weeklycost() throws ParseException {
        int Cost = 0,Income =0;
        for (Money money : moneyList
        ) {
            Date currentDate = new Date();
            if (isThisWeek(money.getDatedate()) && (!money.getDatedate().after(currentDate))) {
                if (money.getType().equals("Cost")) {
                    Cost += money.getDoubleValue();
                } else if (money.getType().equals("Income")) {
                    Income += money.getDoubleValue();
                }
            }
        }
        if (-Cost >300){
            Notification notification = new NotificationCompat.Builder(this,  CHANNEL_2_ID)
                    .setSmallIcon(R.drawable.ic_monthly)
                    .setContentTitle("Save it!")
                    .setContentText("You Cost "+Cost+" this Week, please care about it !")
                    .build();
            notificationManager.notify(2, notification);
        }
    }

    /**
     * Is this to check if the  time  in this week .
     *
     * @param time the Date
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
}