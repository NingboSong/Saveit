package com.example.saveit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import helpclass.Money;
import helpclass.PeriodArrayAdapter;

/**
 * This Activity can show Chart
 * @author Zilin.Song
 */
public class GraphActivity extends AppCompatActivity {
    /**
     * The Chart.
     */
    LineChart chart;
    /**
     * The Yearormonth Spinner.
     */
    Spinner YearorMonth;
    /**
     * The Money list.
     */
    List<Money> moneyList = new ArrayList<>();
    /**
     * The Period.
     */
    int Period = 0; // 0:This year  1:This Month

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_graph);

        assignObjects();
        initPeriodSpinner();
        initContent();
        InitLineChart();
        SpinnerListener();


        super.onCreate(savedInstanceState);
    }

    private void assignObjects() {
        chart = findViewById(R.id.linechart);
        YearorMonth = findViewById(R.id.YearMonthSpinner);
    }
    private void InitLineChart () {
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(15f);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
    }


    private void initPeriodSpinner() {
        ArrayAdapter<String> mAdapter;
        String[] mStringArray;
        mStringArray = getResources().getStringArray(R.array.YearorMonth);
        mAdapter = new PeriodArrayAdapter(GraphActivity.this, mStringArray);
        YearorMonth.setAdapter(mAdapter);
    }

    private void initContent() {
        Bundle bundle = this.getIntent().getExtras();
        moneyList = (List<Money>) bundle.get("List");
    }

    private void SpinnerListener() {
        YearorMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            /**
             * <p>Callback method to be invoked when an item in this view has been
             * selected. This callback is invoked only when the newly selected
             * position is different from the previously selected position or if
             * there was no selected item.</p>
             * <p>
             * Implementers can call getItemAtPosition(position) if they need to access the
             * data associated with the selected item.
             *
             * @param parent   The AdapterView where the selection happened
             * @param view     The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id       The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Period = position;
                if (Period == 0){
                    YearsChart();
                }else {
                    try {
                        MonthChart();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            /**
             * Callback method to be invoked when the selection disappears from this
             * view. The selection can disappear for instance when touch is activated
             * or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
    }

    private void YearsChart() {

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(12);


        ArrayList<Entry> Cost = new ArrayList<>();
        ArrayList<Entry> Income = new ArrayList<>();

        double[][] result = Year();
        for (int i = 0; i < 12; i++) {
            Cost.add(new Entry(i+1, (float) result[0][i]));
            Income.add(new Entry(i+1, (float) result[1][i]));
        }


        LineDataSet set1 = new LineDataSet(Cost, "Cost");
        LineDataSet set2 = new LineDataSet(Income, "Income");
        set1.setFillAlpha(110);
        set1.setLineWidth(3f);
        set1.setValueTextSize(15f);
        set1.setColor(Color.GREEN);
        set2.setFillAlpha(110);
        set2.setLineWidth(3f);
        set2.setValueTextSize(15f);
        set2.setColor(Color.RED);




        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);



        LineData data = new LineData(dataSets);

        chart.setData(data);
        chart.notifyDataSetChanged();
    }
    private void MonthChart() throws ParseException {
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMaximum(31);



        ArrayList<Entry> Cost = new ArrayList<>();
        ArrayList<Entry> Income = new ArrayList<>();

        double[][] result = Month();
        for (int i = 0; i < 31; i++) {
            Cost.add(new Entry(i+1, (float) result[0][i]));
            Income.add(new Entry(i+1, (float) result[1][i]));
        }


        LineDataSet set1 = new LineDataSet(Cost, "Cost");
        LineDataSet set2 = new LineDataSet(Income, "Income");
        set1.setFillAlpha(110);
        set1.setLineWidth(3f);
        set1.setValueTextSize(15f);
        set1.setColor(Color.GREEN);
        set2.setFillAlpha(110);
        set2.setLineWidth(3f);
        set2.setValueTextSize(15f);
        set2.setColor(Color.RED);




        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);



        LineData data = new LineData(dataSets);

        chart.setData(data);
        chart.notifyDataSetChanged();

    }

    /**
     * Get this Year's data
     * @return Year
     */

    private double[][] Year() {
        double[][] Year = new double[2][12];//Year[0] :cost      Year[1] :Income
        for (Money i : moneyList) {
            if (!i.ifthisyear())
                continue;
            else {
                int m = i.getMonth();
                if (i.getType().equals("Cost")) {
                    Year[0][m-1] -= i.getDoubleValue();
                } else {
                    Year[1][m-1] += i.getDoubleValue();
                }
            }
        }
        return Year;
    }
    /**
     * Get this Month's data
     * @return Month
     */
    private double[][] Month() throws ParseException {
        double[][] Month = new double[2][31];//Month[0] :cost      Month[1] :Income
        for (Money i : moneyList) {
            if (!i.ifThisMonth())
                continue;
            else {
                int m = i.getDay();
                if (i.getType().equals("Cost")) {
                    Month[0][m-1] -= i.getDoubleValue();
                } else {
                    Month[1][m-1] += i.getDoubleValue();
                }
            }
        }
        return Month;
    }


}