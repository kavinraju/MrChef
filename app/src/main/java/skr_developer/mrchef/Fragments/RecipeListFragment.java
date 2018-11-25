package skr_developer.mrchef.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import skr_developer.mrchef.Adapter.RecipeAdapter;
import skr_developer.mrchef.Adapter.RecipeCardAdapter;
import skr_developer.mrchef.HelperClasses.HelperClass;
import skr_developer.mrchef.Model.Recipe;
import skr_developer.mrchef.R;
import skr_developer.mrchef.RecipeActivity;
import skr_developer.mrchef.Retrofit.ApiClient;
import skr_developer.mrchef.Retrofit.ApiService;

public class RecipeListFragment extends Fragment implements  RecipeCardAdapter.OnClickRecipeCardListener,
                                                                        RecipeAdapter.OnClickRecipeCardListener {

    private static final String TAG = RecipeListFragment.class.getSimpleName();

    //Keys used for savedInstanceState
    public static String SHARED_ELEMENT_TRANSITION_EXTRA = "sharedElementTransition";
    public static String RECIPE_LIST_BUNDLE_KEY = "recipeList";
    public static String RECIPE_LIST_2_BUNDLE_KEY = "recipeList_2";

    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeCardAdapter recipeCardAdapter;
    private List<Recipe> recipeList_2= new ArrayList<>();
    private RecipeAdapter recipeAdapter;
    private HelperClass helperClass = new HelperClass();

    private Context context;
    private DatabaseReference mDatabaseRef;

    @BindView(R.id.recyclerview_home)
    RecyclerView recyclerView;
    @BindView(R.id.recyclerview_2_home)
    RecyclerView recyclerView_2;
    @BindView(R.id.linear_layout_no_recipies)
    LinearLayout linearLayoutNoRecipies;
    @BindView(R.id.cardview_no_network)
    CardView cardview_no_network;
    @BindView(R.id.pb_other_recpies)
    ProgressBar pb_other_recpies;
    @BindView(R.id.btn_no_wifi)
    Button btn_no_wifi;

    //UI Testing
    @VisibleForTesting
    private boolean isInBackgroundProcess;
    @VisibleForTesting
    private UITestListener uiTestListener;

    @VisibleForTesting
    public interface UITestListener{
        public void onBackgroundTaskComplete();
        public void onBackgroundTaskDismissed();
    }

    public RecipeListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list_home,container,false);
        ButterKnife.bind(this,view);
        context = view.getContext();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("recipes");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        linearLayoutNoRecipies.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            pb_other_recpies.setVisibility(View.VISIBLE);
            setNetworkFailedUI(!helperClass.isNetworkAvailable(context));

            //Gets Retrofit instance using getClient method and creates and instance for ApiService.
            isInBackgroundProcess = true;
            notifyListener(uiTestListener);

            apiCallForRecipe(context);
            callForRecipeFromFirebase();

        }else {

            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST_BUNDLE_KEY);
            recipeList_2 = savedInstanceState.getParcelableArrayList(RECIPE_LIST_2_BUNDLE_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recipeList != null){
            setRecipeAdapter(recipeList);
        }
        if (recipeList_2 != null){
            setRecipeAdapter2(recipeList_2);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(RECIPE_LIST_BUNDLE_KEY, (ArrayList<? extends Parcelable>) recipeList);
        outState.putParcelableArrayList(RECIPE_LIST_2_BUNDLE_KEY, (ArrayList<? extends Parcelable>) recipeList_2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClickRecipeCard(View view, int clickedMoviePosition) {

        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("recipe", recipeList.get(clickedMoviePosition));
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_POSITION, clickedMoviePosition);
        intent.putExtra(RecipeActivity.EXTRA_IS_FROM_WIDGET, false);
        intent.putExtra(SHARED_ELEMENT_TRANSITION_EXTRA, ViewCompat.getTransitionName(view));
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                Objects.requireNonNull(getActivity()),
                view,
                ViewCompat.getTransitionName(view));

        startActivity(intent, optionsCompat.toBundle());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClickRecipe(View view, int clickedMoviePosition) {

        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("recipe", recipeList_2.get(clickedMoviePosition));
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_POSITION, -1);
        intent.putExtra(RecipeActivity.EXTRA_IS_FROM_WIDGET, false);
        intent.putExtra(SHARED_ELEMENT_TRANSITION_EXTRA, ViewCompat.getTransitionName(view));
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                Objects.requireNonNull(getActivity()),
                view,
                ViewCompat.getTransitionName(view));

        startActivity(intent, optionsCompat.toBundle());
    }

    @OnClick(R.id.btn_no_wifi)
    public void onClickNetworkRetry(View view){
        if (!helperClass.isNetworkAvailable(context)){
            helperClass.showSimpleSnakbar(linearLayoutNoRecipies.getRootView().findViewById(R.id.bottom_navigation_view),"Check Your Internet Settings");
            return;
        }
        setNetworkFailedUI(false);
        apiCallForRecipe(context);
        callForRecipeFromFirebase();
    }

    /*
       Helper Functions
    */

    private void callForRecipeFromFirebase() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                List<Recipe> list = new ArrayList<>();
                for (DataSnapshot snapshot:snapshots){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    list.add(recipe);
                }
                Log.d(TAG, String.valueOf(list.size()));
                recipeList_2 = list;
                setRecipeAdapter2(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void apiCallForRecipe(Context context) {
        apiService = ApiClient.getClient(context).create(ApiService.class);
        disposable.add(
                apiService.fetchAllRecipes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Recipe>>() {
                            @Override
                            public void onSuccess(List<Recipe> recipes) {
                                Log.d("Size", String.valueOf(recipes.size()));
                                recipeList = recipes;

                                isInBackgroundProcess = false;
                                setRecipeAdapter(recipes);
                                notifyListener(uiTestListener);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("error", "onError: " + e.getMessage());
                            }
                        })
        );
    }


    public void setRecipeAdapter(List<Recipe> recipeList) {
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (recipeList.size() > 0) {
            GridLayoutManager layoutManager = new GridLayoutManager(context, 1, 0, false);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recipeCardAdapter = new RecipeCardAdapter(this, recipeList);
            recyclerView.setAdapter(recipeCardAdapter);
        }
    }
    public void setRecipeAdapter2(List<Recipe> recipeList) {
        //recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (recipeList_2.size() > 0) {
            GridLayoutManager layoutManager = getGridLayoutManager();

            recyclerView_2.setLayoutManager(layoutManager);
            recyclerView_2.setHasFixedSize(true);
            recipeAdapter = new RecipeAdapter(this, recipeList);
            recyclerView_2.setAdapter(recipeAdapter);
            pb_other_recpies.setVisibility(View.GONE);
        }
    }

    private GridLayoutManager getGridLayoutManager(){
        GridLayoutManager layoutManager;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){

            // if PORTRAIT mode set spanCount as
            layoutManager = new GridLayoutManager(context, 3, 1, false);
        }
        else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            // if PORTRAIT mode set spanCount as
            layoutManager = new GridLayoutManager(context, 4, 1, false);
        }else {
            layoutManager = new GridLayoutManager(context, 3, 1, false);
        }

        return layoutManager;
    }

    // Helper Method to set UI on Network Error
    private void setNetworkFailedUI(boolean noConnection){
        if (noConnection){
            cardview_no_network.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            recyclerView_2.setVisibility(View.GONE);
        }else {
            cardview_no_network.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView_2.setVisibility(View.VISIBLE);
        }
    }

    @VisibleForTesting
    public boolean isInBackgroundProcess() {
        return isInBackgroundProcess;
    }

    @VisibleForTesting
    public void setUITestListener(UITestListener uiTestListener){
        this.uiTestListener = uiTestListener;
    }

    @VisibleForTesting
    private void notifyListener(UITestListener listener) {
        if (listener == null){
            return;
        }
        if (isInBackgroundProcess){
            listener.onBackgroundTaskComplete();
        }
        else {
            listener.onBackgroundTaskDismissed();
        }
    }
}

