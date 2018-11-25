package skr_developer.mrchef;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.mrchef.Fragments.PhotoPagerFragment;
import skr_developer.mrchef.HelperClasses.HelperClass;

public class LoginActivity extends AppCompatActivity {

    private static String CURRENT_PAGE_BUNDLE_KEY ="current_key";
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private int currentPage = 0;
    private int totalNumOfPages = 5;
    private Timer timer;
    private final long DELAY = 800;
    private final long PERIOD = 2500;


    private GoogleSignInClient mSignInClient;
    private FirebaseAuth mFirebaseAuth;
    private HelperClass helperClass;

    @BindView(R.id.btn_login)
    SignInButton btnLogin;
    @BindView(R.id.container_viewPager)
    ViewPager viewPager;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (savedInstanceState != null){
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_BUNDLE_KEY);
        }
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this,signInOptions);

        mFirebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setSize(SignInButton.SIZE_STANDARD);
        helperClass = new HelperClass();

        viewPager.setLayoutParams(getLayoutParams());
        viewPager.setAdapter(new PhotosPagerAdapter(getSupportFragmentManager()));
        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());

        tabLayout.setupWithViewPager(viewPager);
        addTimerForViewPager();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE_BUNDLE_KEY,currentPage);
    }

    @OnClick(R.id.btn_login)
    public void onClickSignIn(View view){
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public class PhotosPagerAdapter extends FragmentStatePagerAdapter {

        PhotosPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    currentPage = 0;
                    return PhotoPagerFragment.newInstance("Get your food done using Mr. Chef",
                            helperClass.getLoginScreenImageUrls(currentPage));
                case 1:
                    currentPage = 1;
                    return PhotoPagerFragment.newInstance("Make your favorite recipe and show it to the world",
                            helperClass.getLoginScreenImageUrls(currentPage));
                case 2:
                    currentPage = 2;
                    return PhotoPagerFragment.newInstance("Be it yummy",
                            helperClass.getLoginScreenImageUrls(currentPage));
                case 3:
                    currentPage = 3;
                    return PhotoPagerFragment.newInstance("Make your favorite pizza",
                            helperClass.getLoginScreenImageUrls(currentPage));
                case 4:
                    currentPage = 4;
                    return PhotoPagerFragment.newInstance("Your favorite Turkey",
                            helperClass.getLoginScreenImageUrls(currentPage));
                default:
                    currentPage = 0;
                    return PhotoPagerFragment.newInstance("Make your favorite within no time.",
                            helperClass.getLoginScreenImageUrls(currentPage));
            }
        }

        @Override
        public int getCount() {
            return totalNumOfPages;
        }

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else {
                view.setAlpha(0);
            }
        }
    }

    /*
        Helper Functions
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            overridePendingTransition(R.anim.slide_from_right , R.anim.slide_to_left);
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.btn_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private ViewGroup.LayoutParams getLayoutParams() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            layoutParams.width = viewPager.getWidth();
            layoutParams.height = (int) (helperClass.getScreenHeight(this) * .8);
        }else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutParams.width = (int) (helperClass.getScreenWeight(this) * .7);
            layoutParams.height = viewPager.getHeight();
        }
        return layoutParams;
    }

    private void addTimerForViewPager() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == totalNumOfPages){
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++,true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },DELAY,PERIOD);
    }

}
