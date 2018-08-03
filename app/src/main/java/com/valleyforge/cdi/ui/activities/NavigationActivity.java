package com.valleyforge.cdi.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.ui.fragments.ActivePendingFragment;
import com.valleyforge.cdi.ui.fragments.DashboardFragment;
import com.valleyforge.cdi.ui.fragments.MyProfileFragment;
import com.valleyforge.cdi.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    NavigationView navigationView;

    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;


    @BindView(R.id.logout)
    ImageView ivLogout;

    Fragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);
        ivBackIcon.setVisibility(View.GONE);
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutValleyforgeApp();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        setUserLoggedIn();
        setFragment();
    }



    private void setUserLoggedIn() {
        PrefUtils.storeUserLoggedIn(true, this);
    }

    public void setFragment() {
            //PrefUtils.storeUserFrag("Admin",getBaseContext());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            dashboardFragment = new DashboardFragment();
            fragmentTransaction.add(R.id.fragment_container, dashboardFragment, "DASHBOARD").addToBackStack(null);
            fragmentTransaction.commit();

        tvAppTitle.setText("Dashboard");



    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else if (getFragmentManager().getBackStackEntryCount() == 0)
        {

            //MenuItem itemid = navigationView.getMenu().findItem(R.id.nav_active);
            if (getFragmentManager().findFragmentById(R.id.fragment_container) == null) {
                //onNavigationItemSelected(itemid);
                Log.e("abhi123", " inside null" );

                openExitAppDialog();

            }
            Log.e("abhi123", " outside null" );
            tvAppTitle.setText("Dashboard");
            // super.onBackPressed();


        }
        else
        {
            Log.e("abhi", "onBackPressed:else "+getFragmentManager().getBackStackEntryCount() );
            getFragmentManager().popBackStack();

        }
    }


    private void openExitAppDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setCancelable(false);
        ab.setTitle("Exit App?");
        ab.setMessage("Are you sure you want to exit?");
        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //if you want to kill app . from other then your main avtivity.(Launcher)
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

                //if you want to finish just current activity

                NavigationActivity.this.finish();
            }
        });
        ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
            tvAppTitle.setText( item.getTitle());
            ivBackIcon.setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_completed) {
            Intent i = new Intent(NavigationActivity.this, CompletedProjectsActivity.class);
            startActivity(i);


        }  else if (id == R.id.nav_active) {
            /*fragment = new ActivePendingFragment();
            tvAppTitle.setText( item.getTitle());
            ivBackIcon.setVisibility(View.INVISIBLE);*/
            Intent i = new Intent(NavigationActivity.this, ActivePendingActivity.class);
            startActivity(i);
        }

        else if (id == R.id.nav_profile) {
            /*fragment = new MyProfileFragment();
            tvAppTitle.setText( item.getTitle());
            ivBackIcon.setVisibility(View.INVISIBLE);*/
            Intent i = new Intent(NavigationActivity.this, ProfileActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            logoutValleyforgeApp();

        } else if (id == R.id.nav_hq) {

        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }
        return true;
    }

    private void logoutValleyforgeApp() {
        PrefUtils.storeUserLoggedIn(false, NavigationActivity.this);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        navigationView.getMenu().getItem(0).setChecked(true);
//
//    }
}
