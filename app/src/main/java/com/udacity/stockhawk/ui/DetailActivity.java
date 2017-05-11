package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.utils.LoadHistory;

import java.util.ArrayList;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

import static com.udacity.stockhawk.ui.MainActivity.DETAIL_INTENT_KEY;

public class DetailActivity extends AppCompatActivity implements LoadHistory.Callback {

    private LineChart chart;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        chart = (LineChart) findViewById(R.id.chart);
        Intent intent = getIntent();
        if (intent == null || intent.hasExtra(DETAIL_INTENT_KEY)) {
            Toast.makeText(this, R.string.error_no_id_given, Toast.LENGTH_LONG).show();
            finish();
        } else {
            symbol = intent.getStringExtra(DETAIL_INTENT_KEY);
            setTitle(symbol);

            new LoadHistory(this).execute(symbol);
        }
    }


    @Override
    public void onLoadFinished(List<HistoricalQuote> historicalQuotes) {
        int i = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        if (historicalQuotes != null) {
            for (HistoricalQuote hq : historicalQuotes) {
                float open = hq.getOpen().floatValue();
                float close = hq.getClose().floatValue();
                float high = hq.getHigh().floatValue();
                float low = hq.getLow().floatValue();
                entries.add(new Entry(i++, close));
                labels.add(hq.getDate().toString());
            }
        }
        LineDataSet dataSet = new LineDataSet(entries, symbol);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        LineData data = new LineData(dataSet);

        chart.setData(data);
    }
}
