package com.udacity.stockhawk.utils;

import android.os.AsyncTask;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class CheckStock extends AsyncTask<String, Void, String> {

    private Callback cb;

    public CheckStock(Callback cb) {
        super();
        this.cb = cb;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Stock stock = YahooFinance.get(params[0]);
            return stock.getName() != null ? stock.getSymbol() : null;

        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String name) {
        super.onPostExecute(name);
        if (this.cb != null) {
            this.cb.onCheck(name);
        }
    }

    public interface Callback {
        void onCheck(String stockSymbol);
    }
}
