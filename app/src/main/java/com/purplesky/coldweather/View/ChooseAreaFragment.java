package com.purplesky.coldweather.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.purplesky.coldweather.R;
import com.purplesky.coldweather.db.City;
import com.purplesky.coldweather.db.County;
import com.purplesky.coldweather.db.Province;
import com.purplesky.coldweather.util.CityUtility;

public class ChooseAreaFragment extends Fragment {

    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;

    private TextView title;
    private Button back;
    private RecyclerView cityList;
    private int level;

    private Province chooseProvince;
    private City chooseCity;
    private County chooseCounty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        title=view.findViewById(R.id.choose_area_title);
        back=view.findViewById(R.id.choose_area_back);
        cityList=view.findViewById(R.id.choose_area_list);
        cityList.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        cityList.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        back.setOnClickListener(v->{
            switch (level){
                case LEVEL_CITY:
                    RefreshProvinces();
                    break;
                case LEVEL_COUNTY:
                    RefreshCities(chooseProvince);
                    break;
            }
        });
        RefreshProvinces();
    }

    private void RefreshProvinces(){
        level=LEVEL_PROVINCE;
        back.setVisibility(View.GONE);
        title.setText("中国");
        new Thread(()->{
            CityAdapter adapter=new CityAdapter(CityUtility.QueryProvinces(),CityAdapter.TYPE_PROVINCE,(province -> {
                chooseProvince =(Province)province;
                RefreshCities(chooseProvince);
            }));
            if(level==LEVEL_PROVINCE)
                getActivity().runOnUiThread(()-> cityList.setAdapter(adapter));
        }).start();
    }

    private void RefreshCities(Province province){
        level=LEVEL_CITY;
        back.setVisibility(View.VISIBLE);
        title.setText(province.getName());

        new Thread(()->{
            CityAdapter adapter=new CityAdapter(CityUtility.QueryCities(province),CityAdapter.TYPE_CITY,(city -> {
                chooseCity =(City)city;
                RefreshCounties(chooseCity);
            }));
            if(level==LEVEL_CITY)
                getActivity().runOnUiThread(()-> cityList.setAdapter(adapter));
        }).start();
    }

    private void RefreshCounties(City city){
        level=LEVEL_COUNTY;
        back.setVisibility(View.VISIBLE);
        title.setText(city.getName());
        new Thread(()->{
            CityAdapter adapter=new CityAdapter(CityUtility.QueryCounties(city),CityAdapter.TYPE_COUNTY,(county -> {
                chooseCounty=(County)county;
                Toast.makeText(getContext(),"你点击了"+((County)county).getName(),Toast.LENGTH_SHORT).show();
            }));
            if(level==LEVEL_COUNTY)
                getActivity().runOnUiThread(()-> cityList.setAdapter(adapter));
        }).start();
    }
}
