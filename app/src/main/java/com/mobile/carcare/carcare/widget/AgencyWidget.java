package com.mobile.carcare.carcare.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.model.Agency;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class AgencyWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.agency_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_lv, intent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateFromActivity(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, int itemClicked, ArrayList<Agency> agencies) {


        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.updated_widget_layout);

            Log.e("Widget_Agency2",agencies.get(itemClicked).getName());
            views.setTextViewText(R.id.tv_item_agency_name_widget, agencies.get(itemClicked).getName());
            views.setTextViewText(R.id.tv_item_agency_city_widget, agencies.get(itemClicked).getCityName());
            views.setTextViewText(R.id.tv_item_agency_province_widget, agencies.get(itemClicked).getProvinceName());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}