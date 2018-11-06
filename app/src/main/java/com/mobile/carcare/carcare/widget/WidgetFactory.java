package com.mobile.carcare.carcare.widget;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.model.Agency;

import java.util.ArrayList;
import java.util.List;

class WidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Agency> agencies;

    public WidgetFactory(Context applicationContext, Intent intent) {

        mContext = applicationContext;
        agencies =new ArrayList<>();

    }

    @Override
    public void onCreate() {
        getAgencies();
        SystemClock.sleep(1000);
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {


        if (agencies != null) {

            return agencies.size();

        } else {

            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        Agency agency = agencies.get(position);
        Log.e("Widget_Agency", agency.getName());
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_lv_item);
        rv.setTextViewText(R.id.tv_item_agency_name_widget, agency.getName());
        rv.setTextViewText(R.id.tv_item_agency_city_widget, agency.getCityName());
        rv.setTextViewText(R.id.tv_item_agency_province_widget, agency.getProvinceName());
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void getAgencies(){
            DatabaseReference agenciesRef =FirebaseDatabase.getInstance().getReference().child("agencies");
            agenciesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    agencies.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Agency agency = snapshot.getValue(Agency.class);
                        agencies.add(agency);
                        Log.e("AGENCIES_SIZE_Widget", agencies.size() + "");
                    }
                }
                    @Override
                    public void onCancelled (@NonNull DatabaseError databaseError){
                        //Log.e("AGENCIES_Error", databaseError.getMessage()+"");
                    }
            });
    }
}