package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import group9.tcss450.uw.edu.chatappgroup9.utils.ThemeUtil;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnFragmentInteractionListener,
        ConnectionFragment.OnFragmentInteractionListener,
        LandingFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener{

    public static int mTheme = ThemeUtil.THEME_MEDITERRANEAN_BLUES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To change theme just put your theme id.
        int theme = ThemeUtil.getThemeId(mTheme);
        setTheme(theme);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            if(findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LandingFragment()).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        if (id == R.id.action_mediterranean_blues_theme) {
            changeTheme(ThemeUtil.THEME_MEDITERRANEAN_BLUES);
        }

        if (id == R.id.action_shimmering_blues_theme) {
            changeTheme(ThemeUtil.THEME_SHIMMERING_BLUES);
        }

        if (id == R.id.action_turquoise_red_theme) {
            changeTheme(ThemeUtil.THEME_TURQUOISE_RED);
        }

        if (id == R.id.action_orange_sunset_theme) {
            changeTheme(ThemeUtil.THEME_ORANGE_SUNSET);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeTheme(int theme) {
        // Handles theme changes to activity
        mTheme = theme;
        setTheme(mTheme);

        NavigationActivity.this.recreate();

        int duration = Toast.LENGTH_SHORT;
        Context context = this.getBaseContext();
        Toast toast = Toast.makeText(context, "Changed Theme", duration);
        toast.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_landing) {
            loadFragment(new LandingFragment());
        } else if (id == R.id.nav_chat) {
            loadFragment(new ChatFragment());
        } else if (id == R.id.nav_connection) {
            loadFragment(new ConnectionFragment());
        } else if (id == R.id.nav_search) {
            loadFragment(new SearchFragment());
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag)
                .addToBackStack(null);

        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSearchAttempt(String searchInfo) {

    }

    @Override
    public void onSendRequestClicked() {

    }
}
