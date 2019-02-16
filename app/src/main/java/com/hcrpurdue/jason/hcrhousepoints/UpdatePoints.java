package com.hcrpurdue.jason.hcrhousepoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class UpdatePoints extends Fragment {

    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_points);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        MenuInflater inflater1 = new MenuInflater(context);
//
//        inflater1.inflate(R.menu.approve_list, menu);
//        super.onCreateOptionsMenu(menu, inflater1);
//    }
}
