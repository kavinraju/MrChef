package skr_developer.mrchef.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.HelperClasses.HelperClass;
import skr_developer.mrchef.R;

public class PhotoPagerFragment extends Fragment {

    private static String URL_BUNDLE_KEY = "url";
    private String url;
    private String login_img_content;

    @BindView(R.id.iv_login_img)
    ImageView iv_login_img;
    @BindView(R.id.tv_login_img_content)
    TextView tv_login_img_content;

    public PhotoPagerFragment() {
    }

    public static PhotoPagerFragment newInstance(String login_img_content, String url) {
        PhotoPagerFragment fragment = new PhotoPagerFragment();
        fragment.setLogin_img_content(login_img_content);
        fragment.setUrl(url);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_pager, container, false);
        ButterKnife.bind(this,rootView);

        //iv_login_img.setBackgroundResource(imageID);
        if (savedInstanceState != null){
            url = savedInstanceState.getString(URL_BUNDLE_KEY);
        }
        new HelperClass().loadImage(rootView.getContext(), url ,iv_login_img);
        tv_login_img_content.setText(login_img_content);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URL_BUNDLE_KEY, url);
    }

    /*
       Helper Functions
    */

    public String getLogin_img_content() {
        return login_img_content;
    }

    public void setLogin_img_content(String login_img_content) {
        this.login_img_content = login_img_content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
