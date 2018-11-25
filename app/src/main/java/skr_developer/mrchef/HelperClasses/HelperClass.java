package skr_developer.mrchef.HelperClasses;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
/*
    This class contains all the common functions used across this project and also has the URLs used in this project.
 */
public class HelperClass {

    private String[] recipe_urls = {
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fnutella_pie.jpg?alt=media&token=2e9796f2-38a2-4d01-a231-d521e00bcb21",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fbrownie.png?alt=media&token=0890d31d-1895-408d-a286-1bf03c7838d8",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fyellow_cake.jpg?alt=media&token=fe722830-394d-4456-a356-67dca90426a8",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fcheesecake.jpg?alt=media&token=f63325c6-e56e-4172-ac35-cb12122f94fd"
    };

    private String[] loginScreenImageUrls = {
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fhamburger-and-fries_925x.jpg?alt=media&token=96611990-c90a-486e-a1e8-24ed68b48974",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fkid-helping-in-kitchen.jpg?alt=media&token=58fb9a77-d6a2-4d3d-b7f6-2f3f299c9974",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fstrawberries-and-cream-with-color_925x.jpg?alt=media&token=5c7436d7-1351-40c3-8f85-dbbd5ef2cf07",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fpizza-dough-ready-to-roll_925x.jpg?alt=media&token=2e71d054-5a18-4014-be4f-33d858deaff6",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fthanksgiving-turkey-dinner_925x.jpg?alt=media&token=4203bbed-dfc7-433f-9f8b-442c010390b5",
            "https://firebasestorage.googleapis.com/v0/b/mr-chef-af2f8.appspot.com/o/recipe_app_pics_admin%2Fthanksgiving-turkey-dinner_925x.jpg?alt=media&token=4203bbed-dfc7-433f-9f8b-442c010390b5"
    };

    private String avatarURL = "https://firebasestorage.googleapis.com/v0/b/friendlychat-e9cd8.appspot.com/o/KS_18%2Fmale%20black.png?alt=media&token=4ef6a5d9-250b-43b6-8952-63734b396a3f";

    public void loadImage(Context context, String url, ImageView imageView){
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public int getScreenHeight(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
    public int getScreenWeight(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void showSimpleSnakbar(View view, String content){
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    // Helper Method to check if network is available
    public boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected()|| wifi.isConnected();
    }

    public String getRecipe_url(int position) {
        return recipe_urls[position];
    }

    public String getLoginScreenImageUrls(int position) {
        return loginScreenImageUrls[position];
    }

    public String getAvatarURL() {
        return avatarURL;
    }
}
