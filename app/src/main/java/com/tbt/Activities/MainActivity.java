package com.tbt.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbt.Fragments.FeedbackFragment;
import com.tbt.Fragments.HomeFragment;
import com.tbt.Fragments.PenPointFragment;
import com.tbt.Fragments.SubmitYourStoryFragment;
import com.tbt.ListItems.NavListItem;
import com.tbt.R;
import com.tbt.Services.MyFirebaseIIDService;
import com.tbt.Services.MyFirebaseMessagingService;
import com.tbt.Tools.SPManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.tbt.Constants.Config.log;

public class MainActivity extends AppCompatActivity {
    Toolbar actionBar;
    DrawerLayout drawerLayout;
    ListView navListView;
    ArrayList<NavListItem> navList;
    String uname;
    String uemail;
    String photoURL;
    FragmentManager fragmentManager;
    int activeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        uname = getIntent().getExtras().getString("name");
        uemail = getIntent().getExtras().getString("email");
        photoURL = getIntent().getExtras().getString("profile_pic_url");

        initializeElements();
        populateNavListView();
        initializeListeners();
    }

    private void initializeListeners() {
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeFragment == 1) {
                    if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        drawerLayout.closeDrawers();
                    } else {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                } else {
                    selectItem(1);
                }
            }
        });

        navListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem(i);
            }
        });
        selectItem(1);
    }

    private void populateNavListView() {
        navList = new ArrayList<>();
        navList.add(new NavListItem("Home", R.drawable.ic_home));
        navList.add(new NavListItem("Trending Now", R.drawable.ic_grade));
        navList.add(new NavListItem("Pen Point", R.drawable.ic_pen));
        navList.add(new NavListItem("Submit your Story", R.drawable.ic_file_upload));
        navList.add(new NavListItem("Feedback", R.drawable.ic_feedback));
        navListView.setAdapter(new NavListAdapter(MainActivity.this, R.layout.nav_list_item));
    }

    private void initializeElements() {
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(actionBar);

        actionBar.setTitleTextColor(Color.WHITE);
        actionBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navListView = (ListView) findViewById(R.id.nav_list_menu);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("In onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (activeFragment == 1) moveTaskToBack(true);
        else selectItem(1);
    }

    public class NavListAdapter extends ArrayAdapter<NavListItem> {
        Context context;
        int resource;

        public NavListAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            this.context = context;
            this.resource = resource;
        }

        @Override
        public int getCount() {
            return navList.size() + 1;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (position == 0) view = inflater.inflate(R.layout.profile_layout, parent, false);
            else view = inflater.inflate(R.layout.nav_list_item, parent, false);

            if (position > 0) {
                NavListItem item = navList.get(position - 1);
                ImageView img = (ImageView) view.findViewById(R.id.nav_list_item_img);
                TextView name = (TextView) view.findViewById(R.id.nav_list_item_name);
                img.setImageDrawable(getResources().getDrawable(item.getImgRes()));
                name.setText(item.getName());
            } else {
                TextView name = (TextView) view.findViewById(R.id.profile_name);
                TextView email = (TextView) view.findViewById(R.id.profile_email);
                ImageView profilePic = (ImageView) view.findViewById(R.id.profile_img);
                name.setText(uname);
                email.setText(uemail);
                setProfilePicture(profilePic, photoURL);
            }
            return view;
        }
    }

    public void setProfilePicture(final ImageView iv, final String url) {

        new Thread() {
            @Override
            public void run() {

                try {
                    Bitmap dp = null;
                    URL picURL = new URL(url);
                    dp = BitmapFactory.decodeStream(picURL.openConnection().getInputStream());
                    final Bitmap finalDp = dp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(finalDp);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_item:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SPManager spManager = new SPManager(MainActivity.this);
        spManager.edit();
        spManager.addEntity("isLoggedIn", false);
        spManager.removeEntity("logJSON");
        spManager.commit();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MainActivity.this, MyFirebaseIIDService.class));
        startService(new Intent(MainActivity.this, MyFirebaseMessagingService.class));

        publishInstanceId();
    }

    private void publishInstanceId() {

    }

    void selectItem(int position) {
        if (position != 1) {
            actionBar.setNavigationIcon(R.drawable.ic_back);
        } else {
            actionBar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        activeFragment = position;
        String title = "The BBD Times";
        switch (position) {
            case 0:
                // TODO Profile Framgment
                startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                break;
            case 1:
                title = "The BBD Times";
                transaction.replace(R.id.main_frame_layout, new HomeFragment());
                break;
            case 2:
                // TODO Trending Now Framgment
                break;
            case 3:
                title = "Pen Point";
                transaction.replace(R.id.main_frame_layout, new PenPointFragment());
                break;
            case 4:
                title = "Submit your Story";
                transaction.replace(R.id.main_frame_layout, new SubmitYourStoryFragment());
                break;
            case 5:
                title = "Feedback";
                transaction.replace(R.id.main_frame_layout, new FeedbackFragment());
                break;
        }
        transaction.commit();
        actionBar.setTitle(title);
        drawerLayout.closeDrawers();
    }
}
