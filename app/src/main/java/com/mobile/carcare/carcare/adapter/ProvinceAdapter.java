package com.mobile.carcare.carcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.model.Province;
import com.mobile.carcare.carcare.utils.Font;

import java.util.List;

public class ProvinceAdapter extends BaseAdapter {
    private List<Province> provinces;
    private Context context;
    private LayoutInflater inflater;

    public ProvinceAdapter(Context context, List<Province> provinces) {
        this.provinces = provinces;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return provinces.size();
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
        tvSpinnerTitle.setText(provinces.get(position).getProvinceName());
        Font.apply(context, v);
        return v;
    }
}
