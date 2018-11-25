package skr_developer.mrchef.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.mrchef.HelperClasses.HelperClass;
import skr_developer.mrchef.R;

public class SettingsFragment extends Fragment {

    //Keys used for savedInstanceState
    private static String BUNDLE_KEY_USER_NAME = "user_name";
    private static String BUNDLE_KEY_USER_IMAGE_URL = "user_image_url";

    private Context context;
    private FirebaseUser firebaseUser;
    private HelperClass helperClass;

    private String userName;
    private String userImageURL;

    @BindView(R.id.btn_sign_out)
    Button btn_sign_out;
    @BindView(R.id.tv_user_name)
    TextView tv_user_name;
    @BindView(R.id.iv_user)
    ImageView iv_user;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container,false);
        ButterKnife.bind(this,view);
        context = view.getContext();
        helperClass = new HelperClass();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null){
            if (firebaseUser != null) {
                userName = firebaseUser.getDisplayName();
                userImageURL = String.valueOf(firebaseUser.getPhotoUrl());
                setUpUI();
            }
        }else {
            userName = savedInstanceState.getString(BUNDLE_KEY_USER_NAME);
            userImageURL = savedInstanceState.getString(BUNDLE_KEY_USER_IMAGE_URL);
            setUpUI();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_USER_NAME, userName);
        outState.putString(BUNDLE_KEY_USER_IMAGE_URL, userImageURL);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.btn_sign_out)
    public void onClickSignOut(View view){
        FirebaseAuth.getInstance().signOut();
        Objects.requireNonNull(getActivity()).finish();
        getActivity().overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
    }

    private void setUpUI() {
        tv_user_name.setText(userName);
        if (userImageURL != null){
            helperClass.loadImage(context, userImageURL, iv_user);
        }else {
            helperClass.loadImage(context,helperClass.getAvatarURL() ,iv_user);
        }
    }
}
