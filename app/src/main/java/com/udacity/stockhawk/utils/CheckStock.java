package com.udacity.stockhawk.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class CheckStock extends AsyncTaskLoader<String> {

    private String result = null;
    private String param;

    public CheckStock(Context c, String stock) {
        super(c);
        this.param = stock;
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged() || result == null) {
            forceLoad();
        } else {
            deliverResult(result);
        }

    }

    @Override
    protected void onReset() {
        super.onReset();
        result = null;
    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }


    @Override
    public String loadInBackground() {
        try {
            Stock stock = YahooFinance.get(param);
            return stock.getName() != null ? stock.getSymbol() : null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
