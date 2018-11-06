package com.mobile.carcare.carcare.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobile.carcare.carcare.R;
import com.mobile.carcare.carcare.activity.LoginActivity;
import com.mobile.carcare.carcare.interfaces.AgencyListener;
import com.mobile.carcare.carcare.model.Agency;
import com.mobile.carcare.carcare.utils.Font;
import com.mobile.carcare.carcare.utils.MyPreferences;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AgenciesAdapter extends RecyclerView.Adapter<AgenciesAdapter.AgencyViewHolder> {

    private List<Agency> agencies;
    private Context context;
    private AgencyListener agencyListener;

    public AgenciesAdapter(Context context, List<Agency> agencies, AgencyListener agencyListener){
        this.context =context;
        this.agencies =agencies;
        this.agencyListener =agencyListener;
    }

    @NonNull
    @Override
    public AgencyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view =LayoutInflater.from(context).inflate(R.layout.rv_agency_item, viewGroup, false);
        return new AgencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgencyViewHolder agencyViewHolder, int i) {
        if (agencies.size()>0) {
            agencyViewHolder.tvAgencyName.setText(agencies.get(i).getName());
            agencyViewHolder.tvAgencyProvince.setText(agencies.get(i).getProvinceName());
            agencyViewHolder.tvAgencyCity.setText(agencies.get(i).getCityName());
            if (!agencies.get(i).getAvatar().equals("default")) {
                Picasso.get().load(agencies.get(i).getAvatar())
                        .placeholder(R.drawable.placeholder).into(agencyViewHolder.imgAvatar);
            }
            if (!agencies.get(i).getHeader().equals("default")) {
                Picasso.get().load(agencies.get(i).getHeader())
                        .placeholder(R.drawable.placeholder).into(agencyViewHolder.imgHeader);
            }
            if (agencies.get(i).isInFavorites()){
                agencyViewHolder.agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_enabled));
            }
        }
    }

    @Override
    public int getItemCount() {
        return agencies.size();
    }

    public class AgencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgHeader, imgAvatar, agencyFavorite;
        TextView tvAgencyName, tvAgencyProvince, tvAgencyCity;

        public AgencyViewHolder(@NonNull View itemView) {
            super(itemView);
            Font.apply(context, itemView);
            imgHeader =itemView.findViewById(R.id.img_header_agency_widget);
            imgAvatar =itemView.findViewById(R.id.img_avatar_agency_widget);
            agencyFavorite =itemView.findViewById(R.id.img_item_agency_favorite);
            tvAgencyName =itemView.findViewById(R.id.tv_item_agency_name_widget);
            tvAgencyProvince =itemView.findViewById(R.id.tv_item_agency_province_widget);
            tvAgencyCity =itemView.findViewById(R.id.tv_item_agency_city_widget);
            itemView.setOnClickListener(this);
            agencyFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Agency agency =agencies.get(getAdapterPosition());
            switch (v.getId()){
                case R.id.img_item_agency_favorite:
                    String userType;

                    if (FirebaseAuth.getInstance().getCurrentUser()!=null){
                        String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                        MyPreferences myPreferences =new MyPreferences(context);
                        if (myPreferences.getUserType() ==1){
                            userType ="users";
                        }
                        else {
                            userType ="agencies";
                        }
                        if (agency.isInFavorites()){
                            agencyFavorite.setClickable(false);
                            agency.setInFavorites(false);
                            agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_disabled));
                            removeFromFavorite(agency,uid, agencyFavorite,userType);
                        }
                        else {
                            agencyFavorite.setClickable(false);
                            agency.setInFavorites(true);
                            agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_enabled));
                            addToFavorite(agency,uid,agencyFavorite,userType);
                        }
                    }
                    else {
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }

                    break;

                    default:
                        //open profile
                        agencyListener.onAgencyClick(agency, getAdapterPosition());
                        break;
            }
        }

        void addToFavorite(Agency agency, String uid, final ImageView agencyFavorite, String userType){
            String favoriteId ="Fav_"+UUID.randomUUID().toString();
            DatabaseReference databaseReference =FirebaseDatabase.getInstance().
                    getReference().child(userType).child(uid).child("favorites").child(favoriteId);
            HashMap<String, String> favMap =new HashMap<>();
            favMap.put("agency_key",agency.getKey());
            databaseReference.setValue(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, R.string.added, Toast.LENGTH_SHORT).show();
                    }
                    agencyFavorite.setClickable(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_disabled));
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    agencyFavorite.setClickable(true);
                }
            });
        }

//        void removeFromFavorite(final Agency agency, String uid, final ImageView agencyFavorite, String userType){
//
//            final DatabaseReference databaseReference =FirebaseDatabase.getInstance().
//                    getReference().child(userType).child(uid).child("favorites");
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                        if (agency.getKey().equals(snapshot.child("agency_key").getValue(String.class))){
//                            databaseReference.child(snapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_disabled));
//                                    Log.e("REmoved", "removed");
//                                    Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
//                                    agencyFavorite.setClickable(true);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    agencyFavorite.setClickable(true);
//                                }
//                            });
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    agencyFavorite.setClickable(true);
//                }
//            });
//        }

        void removeFromFavorite(final Agency agency, String uid, final ImageView agencyFavorite, String userType){

            final DatabaseReference databaseReference =FirebaseDatabase.getInstance().
                    getReference();
            Query servicesQuery = databaseReference.child(userType).child(uid).child("favorites").orderByChild("agency_key").equalTo(agency.getKey());

            servicesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        agencyFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_fav_disabled));
                        Log.e("REmoved", "removed");
                        Toast.makeText(context, R.string.removed, Toast.LENGTH_SHORT).show();
                        agencyFavorite.setClickable(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("TAG", "onCancelled", databaseError.toException());
                }
            });
        }
    }
}