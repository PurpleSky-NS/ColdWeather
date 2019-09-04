package com.purplesky.coldweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.purplesky.coldweather.db.City;
import com.purplesky.coldweather.db.Province;

public class ChooseAreaFragment extends Fragment {

    private TextView title;
    private Button back;
    private RecyclerView cityList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        title=view.findViewById(R.id.choose_area_title);
        back=view.findViewById(R.id.choose_area_back);
        cityList=view.findViewById(R.id.choose_area_list);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void RefreshProvices(){

    }

    private void RefreshCities(Province province){

    }

    private void RefreshCounties(City city){

    }
}
