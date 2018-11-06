package com.mobile.carcare.carcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.interfaces.ServiceListener;
import com.mobile.carcare.carcare.model.Service;
import com.mobile.carcare.carcare.utils.Font;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> services;
    private Context context;
    private ServiceListener serviceListener;


    public ServiceAdapter(Context context, List<Service> services, ServiceListener listener) {
        this.services = services;
        this.context = context;
        this.serviceListener =listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context =viewGroup.getContext();
        View v =LayoutInflater.from(context).inflate(R.layout.rv_service_item,viewGroup,false);
        return new ServiceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder serviceViewHolder, int i) {
        serviceViewHolder.tvServiceTitle.setText(services.get(i).getServiceTitle());
        serviceViewHolder.tvServiceDescription.setText(services.get(i).getServiceDescription());
        serviceViewHolder.tvServicePrice.setText(services.get(i).getServicePrice());
        if (services.get(i).getServiceImg()!=null){
            Picasso.get().load(services.get(i).getServiceImg()).placeholder(R.drawable.placeholder).into(serviceViewHolder.imgHeaderService);
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        ImageView imgHeaderService;
        TextView tvServiceTitle,tvServicePrice, tvServiceDescription;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            Font.apply(context, itemView);
            imgHeaderService =itemView.findViewById(R.id.img_header_service);
            tvServiceTitle =itemView.findViewById(R.id.tv_item_service_title);
            tvServiceDescription =itemView.findViewById(R.id.tv_item_service_description);
            tvServicePrice =itemView.findViewById(R.id.tv_item_service_price);
        }

        @Override
        public boolean onLongClick(View v) {
            serviceListener.onServiceClick(services.get(getAdapterPosition()));
            return true;
        }
    }
}
