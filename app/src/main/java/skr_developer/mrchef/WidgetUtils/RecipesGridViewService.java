package skr_developer.mrchef.WidgetUtils;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import skr_developer.mrchef.Database.RecipeDatabase;
import skr_developer.mrchef.Database.RecipeEntry;
import skr_developer.mrchef.R;


public class RecipesGridViewService extends IntentService {

    public static final String ACTION_VIEW_RECIPES = "view_recipes";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *  super("RecipesGridViewService") Used to name the worker thread, important only for debugging.
     */
    public RecipesGridViewService() {
        super("RecipesGridViewService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        if (intent.getAction().equals(ACTION_VIEW_RECIPES)){
            queryRecipes(getApplicationContext());
        }
    }

    private void queryRecipes(Context context) {

        RecipeDatabase recipeDatabase = RecipeDatabase.getInstance(context);
        List<RecipeEntry> recipeEntries = recipeDatabase.recipesDao().loadAllRecipesForWidgets();
        Log.d("widget2-Service","queryRecipes - " + String.valueOf(recipeEntries.size()));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipesWidget.class));

        //Notify the collection, here ListView, that Data has changed.
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_recipes);

        //Call updateRecipeAppWidgets() so as to update the widgets
        RecipesWidget.updateRecipesGridViewWidgets(context,appWidgetManager,recipeEntries,appWidgetIds);
    }

    public static void startQueryingRecipes(Context context){
        Intent intent = new Intent(context,RecipesGridViewService.class);
        intent.setAction(ACTION_VIEW_RECIPES);
        context.startService(intent);
    }
}
