package com.purplesky.coldweather.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.purplesky.coldweather.R;
import com.purplesky.coldweather.db.City;
import com.purplesky.coldweather.db.County;
import com.purplesky.coldweather.db.Province;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

    public interface OnCityChoose{
        void onChoose(Object city);
    }

    public static final int TYPE_PROVINCE=0;
    public static final int TYPE_CITY=1;
    public static final int TYPE_COUNTY=2;

    private OnCityChoose onCityChoose;
    private List<?> list;
    private int type;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view=itemView;
            textView=itemView.findViewById(R.id.city_text);
        }
    }

    public CityAdapter(List<?> list, int type, OnCityChoose onCityChoose){
        this.list=list;
        this.type=type;
        this.onCityChoose=onCityChoose;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text=null;
        switch (type){
            case TYPE_PROVINCE:
                text=((List<Province>)(list)).get(position).getName();
                break;
            case TYPE_CITY:
                text=((List<City>)(list)).get(position).getName();
                break;
            case TYPE_COUNTY:
                text=((List<County>)(list)).get(position).getName();
                break;
        }
        holder.view.setOnClickListener(v->onCityChoose.onChoose(list.get(position)));
        holder.textView.setText(text);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
