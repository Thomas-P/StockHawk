package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;


public class StockInfoRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor dataCursor = null;
            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat dollarFormat;
            private DecimalFormat percentageFormat;


            @Override
            public void onCreate() {
                dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");
            }

            @Override
            public void onDataSetChanged() {
                if (dataCursor != null) {
                    dataCursor.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                dataCursor = getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (dataCursor != null) {
                    dataCursor.close();
                    dataCursor = null;
                }
            }

            @Override
            public int getCount() {
                return dataCursor == null ? 0 : dataCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        dataCursor == null || !dataCursor.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);
               /*



                final Intent fillInIntent = new Intent();
                String locationSetting =
                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting,
                        dateInMillis);
                fillInIntent.setData(weatherUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);*/


                views.setTextViewText(R.id.symbol, dataCursor.getString(Contract.Quote.POSITION_SYMBOL));
                views.setTextViewText(R.id.price, dollarFormat.format(dataCursor.getFloat(Contract.Quote.POSITION_PRICE)));

                float rawAbsoluteChange = dataCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                float percentageChange = dataCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                if (rawAbsoluteChange > 0) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                if (PrefUtils.getDisplayMode(getApplicationContext())
                        .equals(getApplicationContext().getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(R.id.change, change);
                } else {
                    views.setTextViewText(R.id.change, percentage);
                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (dataCursor.moveToPosition(position))
                    return dataCursor.getLong(Contract.Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
