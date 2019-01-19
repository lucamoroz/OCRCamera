package unipd.se18.ocrcamera;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Class that manages all the Fragment changes
 * @author Leonardo Pratesi
 */
public class FragmentManagerDesign extends AppCompatActivity {

    /**
     * The bottom layout with all the buttons
     */
    private BottomNavigationView mMainNav;

    /**
     * Object handling Fragments transitions
     */
    private ViewPager mMainPager;

    /**
     *  Result activity Fragment
     */
    private Fragment resultActivityFragment;

    /**
     * Camera Fragment
     */
    private Fragment camera2Fragment;

    /**
     * Forum Fragment //TODO implements this
     */
    private Fragment forumFragment;


    /**
     * variable indicating the menu item selected
     */
    MenuItem prevMenuItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_manager_layout);

        mMainPager = (ViewPager) findViewById(R.id.main_viewpager);
        mMainNav = (BottomNavigationView)findViewById(R.id.main_nav);

        //listener for menu selection
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.result :
                        mMainNav.setItemBackgroundResource(R.color.design_default_color_primary_dark);
                        mMainPager.setCurrentItem(0);
                        return true;
                    case R.id.camera : //this now opens the camera
                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        mMainPager.setCurrentItem(1);
                        return true;
                    case R.id.forum :
                        mMainNav.setItemBackgroundResource(R.color.common_google_signin_btn_text_dark);
                        mMainPager.setCurrentItem(2);
                        return true;
                    default:
                        return false;

                }
            }
        });

        //listener for swipe gestures
        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    mMainNav.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                mMainNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mMainNav.getMenu().getItem(position);

            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(mMainPager);

    }

    /**
     * method to setup the view Pager
     * @param viewPager
     * @author Leonardo Pratesi
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        resultActivityFragment = new ResultActivityFragment();
        forumFragment = new Fragment();
        camera2Fragment = new Camera2Fragment();
        viewPagerAdapter.addFragment(resultActivityFragment);
        viewPagerAdapter.addFragment(camera2Fragment);
        viewPagerAdapter.addFragment(forumFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }


    /** NOT USEFULL ANYMORE MAYBE USEFULL LATER
     * Method to set the fragment to be viewed
     * @param fragment the frangment that needs to be inflated
     * @author Leonardo Pratesi
     */
    public void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_viewpager, fragment);
        fragmentTransaction.commit();
    }
}
