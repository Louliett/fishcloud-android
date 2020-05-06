package com.example.fishcloud;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fishcloud.adapters.MyAdapter;
import com.example.fishcloud.interfaces.RecyclerViewClickListener;

import com.example.fishcloud.ui.camera.CameraFragmentDirections;
import com.example.fishcloud.ui.list.ListFragmentDirections;
import com.example.fishcloud.ui.map.MapsFragmentDirections;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MenuActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NavController navController;

    private RecyclerView mRecyclerView;
    private Integer[] myDataset;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);

        myDataset = new Integer[4];
        myDataset[0] = R.drawable.camera_icon;
        myDataset[1] = R.drawable.history_list_icon;
        myDataset[2] = R.drawable.maps_icon;
        myDataset[3] = R.drawable.settings_icon;

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(getApplicationContext(), this, myDataset);
        recyclerView.setAdapter(mAdapter);

//        NavigatorAdapter navigatorAdapter = new NavigatorAdapter(getApplicationContext(), mNavigatorList, new NavigatorListener());
//        mRecyclerView.setAdapter(navigatorAdapter);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.camera_nav, R.id.maps_nav, R.id.list_nav)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        NavDirections action;

        switch (position) {
            case 0:


                action = CameraFragmentDirections.actionGlobalCameraNav();
                navController.navigate(action);

                break;
            case 1:

                action = ListFragmentDirections.actionGlobalListNav();
                navController.navigate(action);

                break;
            case 2:

                action = MapsFragmentDirections.actionGlobalMapsNav();
                navController.navigate(action);

                break;
            case 3:
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }




}
