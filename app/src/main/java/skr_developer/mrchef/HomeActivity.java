package skr_developer.mrchef;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.Fragments.AboutBottomSheetFragment;
import skr_developer.mrchef.Fragments.AddRecipeFragment;
import skr_developer.mrchef.Fragments.FavoriteRecipeFragment;
import skr_developer.mrchef.Fragments.RecipeListFragment;
import skr_developer.mrchef.Fragments.SettingsFragment;
import skr_developer.mrchef.HelperClasses.BottomNavigationBehavior;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {

    //Keys for Fragments
    private static String RECIPE_LIST_FRAG = "recipe_home_frag";
    private static String FAV_RECIPE_LIST_FRAG = "fav_recipe_frag";
    private static String ADD_RECIPE_FRAG = "add_recipe_frag";
    private static String SETTINGS_FRAG = "add_recipe_frag";

    //Keys for SavedInstanceState
    private static String BUNDLE_KEY_RECIPE_HOME_FRAG= "recipe_home_frag_bundle";
    private static String BUNDLE_KEY_RECIPE_FAV_FRAG = "recipe_fav_frag_bundle";
    private static String BUNDLE_KEY_ADD_RECIPE_FRAG = "add_recipe_frag_bundle";
    private static String BUNDLE_KEY_SETTINGS_FRAG = "settings_frag_bundle";
    private static String BUNDLE_KEY_IS_FAV = "isFav";
    private static String BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV = "currently_selected_bottom_navigation_view";

    private static boolean isFav = false;
    private static boolean isBackDoubledPressed = false;
    private static String RECIPE_HOME = "recipehome";
    private static String FAVORITE_RECIPE = "fav_recipe";
    private static String ADD_RECIPE = "add_recipe";
    private static String SETTINGS = "settings";
    private String currentlySelected = RECIPE_HOME;

    private FragmentManager fragmentManager;
    private RecipeListFragment recipeListFragment;
    private FavoriteRecipeFragment favoriteRecipeFragment;
    private AddRecipeFragment addRecipeFragment;
    private SettingsFragment settingsFragment;

   /* private AdRequest mAdRequest;
    private InterstitialAd mInterstitialAd;*/

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_toolbar_title)
    TextView tv_toolbar_title;
    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        updateViews();

        if (savedInstanceState == null) {

            recipeListFragment = new RecipeListFragment();
            favoriteRecipeFragment = new FavoriteRecipeFragment();
            addRecipeFragment = new AddRecipeFragment();
            settingsFragment = new SettingsFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                    .add(R.id.container_home_recipe, addRecipeFragment, ADD_RECIPE_FRAG)
                    .add(R.id.container_home_recipe, settingsFragment, SETTINGS_FRAG)
                    .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                    .commit();

        }else {

            isFav = savedInstanceState.getBoolean(BUNDLE_KEY_IS_FAV);
            currentlySelected = savedInstanceState.getString(BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV);

            if (currentlySelected.equals(RECIPE_HOME)){

               recipeListFragment = (RecipeListFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_RECIPE_HOME_FRAG);
               if (recipeListFragment == null) {
                   recipeListFragment = new RecipeListFragment();
               }

               fragmentManager.beginTransaction()
                       .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                       .commit();
           }else if (currentlySelected.equals(FAVORITE_RECIPE)){

               favoriteRecipeFragment = (FavoriteRecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_RECIPE_FAV_FRAG);
               if (favoriteRecipeFragment == null) {
                   favoriteRecipeFragment = new FavoriteRecipeFragment();
               }

               fragmentManager.beginTransaction()
                       .replace(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                       .commit();
           }else if (currentlySelected.equals(ADD_RECIPE)){
               addRecipeFragment = (AddRecipeFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_ADD_RECIPE_FRAG);
               if (addRecipeFragment == null) {
                   addRecipeFragment = new AddRecipeFragment();
               }

               fragmentManager.beginTransaction()
                       .replace(R.id.container_home_recipe, addRecipeFragment, ADD_RECIPE_FRAG)
                       .commit();
           }else if (currentlySelected.equals(SETTINGS)){
                settingsFragment = (SettingsFragment) getSupportFragmentManager().getFragment(savedInstanceState, BUNDLE_KEY_SETTINGS_FRAG);
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, settingsFragment, SETTINGS_FRAG)
                        .commit();
           }
        }

       /* MobileAds.initialize(this, getResources().getString(R.string.applicationID));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
               exitFromApplication();
            }
        });
        mInterstitialAd.loadAd(mAdRequest);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BUNDLE_KEY_IS_FAV,isFav);
        outState.putString(BUNDLE_KEY_CURRENTLY_SELECTED_BOTTOM_NAV, currentlySelected);
        if (currentlySelected.equals(RECIPE_HOME)){
            if (recipeListFragment != null && getSupportFragmentManager().findFragmentByTag(RECIPE_LIST_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_HOME_FRAG, recipeListFragment);
            }
        }else if (currentlySelected.equals(FAVORITE_RECIPE)){
            if (favoriteRecipeFragment != null && getSupportFragmentManager().findFragmentByTag(FAV_RECIPE_LIST_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_RECIPE_FAV_FRAG, favoriteRecipeFragment);
            }
        }else if (currentlySelected.equals(ADD_RECIPE)){
            if (addRecipeFragment != null && getSupportFragmentManager().findFragmentByTag(ADD_RECIPE_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_ADD_RECIPE_FRAG, addRecipeFragment);
            }
        }else if (currentlySelected.equals(SETTINGS)){
            if (settingsFragment != null && getSupportFragmentManager().findFragmentByTag(SETTINGS_FRAG).isAdded()) {
                getSupportFragmentManager().putFragment(outState, BUNDLE_KEY_SETTINGS_FRAG, settingsFragment);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.bottom_nav_recipes:
                currentlySelected = RECIPE_HOME;
                if (fragmentManager != null){
                    isFav = false;
                    recipeListFragment = new RecipeListFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container_home_recipe, recipeListFragment, RECIPE_LIST_FRAG)
                            .commit();
                    updateViews();
                }
                return true;

            case R.id.bottom_nav_favorite_recipes:
                currentlySelected = FAVORITE_RECIPE;
                isFav = true;
                favoriteRecipeFragment = new FavoriteRecipeFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, favoriteRecipeFragment, FAV_RECIPE_LIST_FRAG)
                        .commit();
                updateViews();
                return true;

            case R.id.bottom_nav_add_recipe:
                currentlySelected = ADD_RECIPE;
                isFav = false;
                addRecipeFragment = new AddRecipeFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, addRecipeFragment, ADD_RECIPE_FRAG)
                        .commit();
                updateViews();
                return true;

            case R.id.bottom_nav_settings:
                currentlySelected = SETTINGS;
                isFav = false;
                settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_home_recipe, settingsFragment, SETTINGS_FRAG)
                        .commit();
                updateViews();
                return true;
        }
        return false;
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_aboutUs) {
            AboutBottomSheetFragment aboutBottomSheetFragment = new AboutBottomSheetFragment();
            aboutBottomSheetFragment.setAboutBottomSheetFragment(aboutBottomSheetFragment);
            aboutBottomSheetFragment.show(getSupportFragmentManager(),aboutBottomSheetFragment.getTag());
            return true;
        }
        else if( id == R.id.action_send_feedback){
            String data = "mailto:skr.appdeveloper@gmail.com"; //+ "&subject=" + Uri.encode("Feedback - MrChef");
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse(data));
            startActivity(intent);
            return true;
        }else if (id == R.id.action_sign_out){
            FirebaseAuth.getInstance().signOut();
            finish();
            overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
/*

    @OnClick(R.id.btn_sign_out)
    public void onClickCloseAbout(View view){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
*/

    @Override
    public void onBackPressed() {
        if (isBackDoubledPressed){
           /* if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
            }else {
                super.onBackPressed();
                exitFromApplication();
            }*/
            super.onBackPressed();
            exitFromApplication();
        }
        isBackDoubledPressed = true;
        Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackDoubledPressed = false;
            }
        },2000);
    }

    /*
        Helper Functions
     */
    private void updateViews() {

        if (currentlySelected.equals(RECIPE_HOME)){
            tv_toolbar_title.setText(R.string.recipes);
        }else if (currentlySelected.equals(FAVORITE_RECIPE)){
            tv_toolbar_title.setText(R.string.favorite_recipes);
        }else if (currentlySelected.equals(ADD_RECIPE)){
            tv_toolbar_title.setText(R.string.add_recipe);
        }else if (currentlySelected.equals(SETTINGS)){
            tv_toolbar_title.setText(R.string.settings);
        }
    }

    private void exitFromApplication(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
