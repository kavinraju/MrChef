package skr_developer.mrchef.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import skr_developer.mrchef.Database.IngredientsEntry;
import skr_developer.mrchef.Database.RecipeDatabase;
import skr_developer.mrchef.Database.RecipeEntry;
import skr_developer.mrchef.Database.StepsEntry;

public class FavoriteRecipeFragmentViewModel extends AndroidViewModel {

    private LiveData<List<RecipeEntry>> recipesLiveData;
    private LiveData<List<IngredientsEntry>> ingridientsLiveData;
    private LiveData<List<StepsEntry>> stepsLiveData;

    public FavoriteRecipeFragmentViewModel(@NonNull Application application) {
        super(application);

        Log.d("Querying from Database","FavoriteRecipeViewModel");

        RecipeDatabase recipeDatabase = RecipeDatabase.getInstance(this.getApplication());
        recipesLiveData = recipeDatabase.recipesDao().loadAllRecipes();
        ingridientsLiveData = recipeDatabase.recipesDao().loadIngredients();
        stepsLiveData = recipeDatabase.recipesDao().loadSteps();
    }

    public LiveData<List<RecipeEntry>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public LiveData<List<IngredientsEntry>> getIngridientsLiveData() {
        return ingridientsLiveData;
    }

    public LiveData<List<StepsEntry>> getStepsLiveData() {
        return stepsLiveData;
    }
}
