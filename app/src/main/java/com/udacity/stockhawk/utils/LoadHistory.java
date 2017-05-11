package com.udacity.stockhawk.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;


public class LoadHistory extends AsyncTask<String, Void, List<HistoricalQuote>> {

    private Callback cb;

    public LoadHistory(Callback cb) {
        this.cb = cb;
    }

    @Override
    protected List<HistoricalQuote> doInBackground(String... params) {
        Stock stock = null;
        try {
            stock = YahooFinance.get(params[0], true);
            return stock.getHistory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<HistoricalQuote> historicalQuotes) {
        super.onPostExecute(historicalQuotes);
        if (this.cb != null) {
            this.cb.onLoadFinished(historicalQuotes);
        }
    }

    public interface Callback {
        void onLoadFinished(List<HistoricalQuote> historicalQuotes);
    }
}
