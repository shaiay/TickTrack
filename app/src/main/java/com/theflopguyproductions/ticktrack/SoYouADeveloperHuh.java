package com.theflopguyproductions.ticktrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theflopguyproductions.ticktrack.ui.counter.CounterFragment;
import com.theflopguyproductions.ticktrack.ui.settings.SettingsActivity;
import com.theflopguyproductions.ticktrack.ui.stopwatch.StopwatchFragment;
import com.theflopguyproductions.ticktrack.ui.timer.TimerFragment;
import com.theflopguyproductions.ticktrack.utils.TickTrackThemeSetter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class SoYouADeveloperHuh extends AppCompatActivity {

    private Toolbar mainToolbar;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_you_a_developer_huh);


        navView = findViewById(R.id.nav_view);
        TickTrackThemeSetter.mainActivityTheme(navView, this);

        mainToolbar = findViewById(R.id.mainActivityToolbar);
        overflowMenuSetup();

        navView.setItemIconTintList(null);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_counter:
                openFragment(new CounterFragment());
                return true;

            case R.id.navigation_stopwatch:
                openFragment(new StopwatchFragment());
                return true;

            case R.id.navigation_timer:
                openFragment(new TimerFragment());
                return true;
        }
        return false;
    };

    public void openFragment(Fragment fragment) {
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
        manager.popBackStack();
    }

    public void overflowMenuSetup(){
        mainToolbar.inflateMenu(R.menu.overflow_menu);

        mainToolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.screensaverMenuItem:
                    Toast.makeText(getApplicationContext(),"Screensaver",Toast.LENGTH_SHORT).show();
                    return false;

                case R.id.settingsMenuItem:
                    startActivity(new Intent(this, SettingsActivity.class));
                    return false;

                case R.id.feedbackMenuItem:
                    Toast.makeText(getApplicationContext(),"Feedback",Toast.LENGTH_SHORT).show();
                    return false;

                case R.id.aboutMenuItem:
                    Toast.makeText(getApplicationContext(),"About",Toast.LENGTH_SHORT).show();
                    return true;

                default:
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TickTrackThemeSetter.mainActivityTheme(navView, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}