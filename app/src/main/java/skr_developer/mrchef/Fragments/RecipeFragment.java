package skr_developer.mrchef.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.Adapter.IngredientsAdapter;
import skr_developer.mrchef.Adapter.StepsAdapter;
import skr_developer.mrchef.Model.Recipe;
import skr_developer.mrchef.R;
import skr_developer.mrchef.RecipeStepActivity;

public class RecipeFragment extends Fragment implements StepsAdapter.OnStepClickListener {

    //Keys used for savedInstanceState
    private static String BUNDLE_KEY_RECIPE= "recipe_";
    private static String BUNDLE_KEY_RECIPE_POSITION = "recipe_position_";
    private static String BUNDLE_KEY_TWO_PANE = "two_pane_";

    private Recipe recipe;
    private int recipePosition;
    private boolean twoPane;

    @BindView(R.id.recyclerview_ingredients)
    RecyclerView recyclerViewIngredients;
    @BindView(R.id.recyclerview_steps)
    RecyclerView recyclerViewSteps;

    public RecipeFragment() {
    }

    private OnRecipeSetpClickListener onRecipeSetpClickListener;

    public interface OnRecipeSetpClickListener {
        void onRecipeStepSelected(int position);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onRecipeSetpClickListener = (OnRecipeSetpClickListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeSetpClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe, container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null){
            setUpIngredientsAdapter();
            setUpStepsAdapter();
        }else {
            recipe = savedInstanceState.getParcelable(BUNDLE_KEY_RECIPE);
            recipePosition = savedInstanceState.getInt(BUNDLE_KEY_RECIPE_POSITION);
            twoPane = savedInstanceState.getBoolean(BUNDLE_KEY_TWO_PANE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_KEY_RECIPE, recipe);
        outState.putInt(BUNDLE_KEY_RECIPE_POSITION, recipePosition);
        outState.putBoolean(BUNDLE_KEY_TWO_PANE, twoPane);

    }

    @Override
    public void onStepClicked(int position) {
        if (twoPane){
            onRecipeSetpClickListener.onRecipeStepSelected(position);
        }else {
            Intent intent = new Intent(getActivity(),RecipeStepActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("steps", (ArrayList<? extends Parcelable>) recipe.getSteps());
            bundle.putInt("position",position);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_from_right , R.anim.slide_to_left);
        }

    }

        /*
           Helper Functions
        */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setTwoPane(boolean twoPane) {
        this.twoPane = twoPane;
    }

    public void setRecipePosition(int recipePosition) {
        this.recipePosition = recipePosition;
    }

    private void setUpIngredientsAdapter(){
        Log.d("ccc","setUpIngredientsAdapter");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),1,false);
        recyclerViewIngredients.setLayoutManager(layoutManager);
        recyclerViewIngredients.setHasFixedSize(true);
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(recipe.getIngredients());
        recyclerViewIngredients.setAdapter(ingredientsAdapter);
    }

    private void setUpStepsAdapter(){
        Log.d("ccc","setUpStepsAdapter");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),1,false);
        recyclerViewSteps.setLayoutManager(layoutManager);
        recyclerViewSteps.setHasFixedSize(true);
        StepsAdapter stepsAdapter = new StepsAdapter(recipe.getSteps(),this);
        recyclerViewSteps.setAdapter(stepsAdapter);
    }
}