package unipd.se18.ocrcamera;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import unipd.se18.ocrcamera.inci.Ingredient;

/**
 * Class to show the statistics about ingredients
 * @author Leonardo Pratesi
 */
public class StatCalculatorActivity extends AppCompatActivity {

    private HorizontalBarChart mChart;

    /**
     * map that contains the ingredients with the counter
     */
    private HashMap<String, Integer> statmap = new HashMap<>();

    /**
     * see documentation for Android chart view :
     * https://github.com/PhilJay/MPAndroidChart
     * License Apache 2.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatisticManager manager = new StatisticManager(getApplicationContext());
        statmap = manager.loadMap();

        Log.i("HASHFINAL", statmap.toString());
        setContentView(R.layout.activity_stat_calculator);
        mChart = (HorizontalBarChart) findViewById(R.id.chart1);

        //set graph
        setData(20, 50);

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatisticManager manager = new StatisticManager(getApplicationContext());
        statmap = manager.loadMap();

    }

    /**
     * method to populate the chart
     * @param count
     * @param range
     * @author Leonardo Pratesi
     */
    private void setData(int count, int range) {


        ArrayList<BarEntry> yVals = new ArrayList<>(); //values
        ArrayList<String> xVals = new ArrayList<>();   //keys

        float barWidth = 20f;
        float spaceForBar = 1f;
        int i = 0;
        for (Map.Entry<String, Integer> entry : statmap.entrySet()) {
            if (i <= 12){
                float val = (float) (entry.getValue());
                yVals.add(new BarEntry(i * spaceForBar, entry.getValue()));
                xVals.add((String)entry.getKey());

                i++;


            }
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };
        yVals.setValueFormatter(formatter);

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xVals));
        mChart.getDescription().setEnabled(false);
        BarDataSet set1;
        set1 = new BarDataSet(yVals, "");
        set1.setColors(ColorTemplate.VORDIPLOM_COLORS);

        BarData data = new BarData(set1);
        mChart.setData(data);

    }


}

