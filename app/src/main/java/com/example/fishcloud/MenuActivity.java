package com.example.fishcloud;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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
import com.example.fishcloud.ui.login.LoginViewModel;
import com.example.fishcloud.ui.map.MapsFragmentDirections;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private NavController navController;
    private Integer[] iconIds;
    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout mDrawerLayout;
    private LoginViewModel loginViewModel;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginViewModel.setGoogleSignInClient(mGoogleSignInClient);

        iconIds = new Integer[5];
        iconIds[0] = R.drawable.camera_icon;
        iconIds[1] = R.drawable.history_list_icon;
        iconIds[2] = R.drawable.maps_icon;
        iconIds[3] = R.drawable.settings_icon;
        iconIds[4] = R.drawable.logout_icon;

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter(getApplicationContext(), this, iconIds);
        recyclerView.setAdapter(mAdapter);

        setSupportActionBar(toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.camera_nav, R.id.maps_nav, R.id.list_nav)
                .setDrawerLayout(mDrawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.login_nav) {
                    toolbar.setVisibility(View.GONE);

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        NavDirections action;

        switch (position) {
            case 0:
                action = CameraFragmentDirections.actionGlobalCameraNav();
                navController.navigate(action);
                mDrawerLayout.closeDrawers();

                break;
            case 1:
                action = ListFragmentDirections.actionGlobalListNav();
                navController.navigate(action);
                mDrawerLayout.closeDrawers();

                break;
            case 2:
                action = MapsFragmentDirections.actionGlobalMapsNav();
                navController.navigate(action);
                mDrawerLayout.closeDrawers();

                break;
            case 3:
                break;
            case 4:
                mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                navController.navigate(R.id.login_nav);
                                loginViewModel.revokeAuth();
                                mDrawerLayout.closeDrawers();
                            }
                        });

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
