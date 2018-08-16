package com.valleyforge.cdi.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.ApiEndPoints;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.ui.fragments.ActivePendingFragment;
import com.valleyforge.cdi.ui.fragments.DashboardFragment;
import com.valleyforge.cdi.ui.fragments.MyProfileFragment;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView headerName,headerEmail,headerPhone;
    de.hdodenhof.circleimageview.CircleImageView personImage;
    private RetrofitInterface.UserProfileDetailsClient UserProfileDetailAdapter;
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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.username);
        headerEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.user_email);
        headerPhone = (TextView)navigationView.getHeaderView(0).findViewById(R.id.user_phone);

        personImage = (de.hdodenhof.circleimageview.CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.person_image);
        setUpRestAdapter();

        setHeaderData();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        setUserLoggedIn();
        setFragment();

    }

    private void setUpRestAdapter() {
        UserProfileDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProfileDetailsClient.class, BASE_URL, this);

    }

    private void setProfilePicURL(String profilepicUrlComplete) {
        Glide.with(this).load(profilepicUrlComplete).asBitmap().centerCrop().dontAnimate().dontTransform().listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                //imageProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                //imageProgressBar.setVisibility(View.GONE);
                return false;
            }
        })
                .into(new BitmapImageViewTarget(personImage) {
                    @Override
                    protected void setResource(Bitmap bitmap) {
                        Bitmap output;

                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        } else {
                            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                        }

                        Canvas canvas = new Canvas(output);

                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                        float r = 0;

                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            r = bitmap.getHeight() / 2;
                        } else {
                            r = bitmap.getWidth() / 2;
                        }

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawCircle(r, r, r, paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(bitmap, rect, rect, paint);
                        Log.e("abhi", "setResource: -----------output"+output );
                        personImage.setImageBitmap(output);
                       // imageProgressBar.setVisibility(View.GONE);


                    }
                });
    }

    private void setProfileDetails() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<LoginResponse> call = UserProfileDetailAdapter.userProfileDetailData(PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                            for (int i=0; i<response.body().getData().size(); i++) {
                                Log.e("abhi", "onResponse:.......................image url " + response.body().getData().get(i).getProfileImageUrl());
                                setProfilePicURL(response.body().getData().get(i).getProfileImageUrl());
                            }

                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
        }
    }

    private void setHeaderData() {
        headerName.setText(PrefUtils.getUserName(NavigationActivity.this));
        headerEmail.setText(PrefUtils.getEmail(NavigationActivity.this));
        headerPhone.setText(PrefUtils.getUserPhone(NavigationActivity.this));
        /*if (PrefUtils.getUserImage(NavigationActivity.this) !=null) {
            //imageUri = PrefUtils.getUserImage(NavigationalActivity.this);*/
            setProfileDetails();
       // }

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
