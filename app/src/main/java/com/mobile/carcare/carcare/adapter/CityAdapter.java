package com.mobile.carcare.carcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.model.City;
import com.mobile.carcare.carcare.utils.Font;

import java.util.List;

public class CityAdapter extends BaseAdapter {
    private List<City> cities;
    private Context context;
    private LayoutInflater inflater;

    public CityAdapter(Context context, List<City> cities) {
        this.cities = cities;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.spinner_item, null);
        TextView tvSpinnerTitle = v.findViewById(R.id.tv_spinner_item);
        tvSpinnerTitle.setText(cities.get(position).getCityName());
        Font.apply(context, v);
        return v;
    }
}
