package skr_developer.mrchef.WidgetUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import skr_developer.mrchef.Database.RecipeDatabase;
import skr_developer.mrchef.Database.RecipeEntry;
import skr_developer.mrchef.R;
import skr_developer.mrchef.RecipeActivity;

/**
 *      This class, extending RemoteViewsService, is for displaying the Recipes in a GridView.
 */

public class RecipesGridviewWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("widget2","onGetViewFactory");
        return  new RecipesGridviewRemoteViewFactory(this.getApplicationContext());
    }
}

class RecipesGridviewRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<RecipeEntry> recipeEntries;
    private RecipeDatabase recipeDatabase;

    RecipesGridviewRemoteViewFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        //Get the instance of Database
        recipeDatabase = RecipeDatabase.getInstance(context);

        //Get all the Recipies
        recipeEntries = recipeDatabase.recipesDao().loadAllRecipesForWidgets();
        Log.d("widget2",String.valueOf(recipeEntries.size()));
    }

    @Override
    public void onDestroy() {
        recipeDatabase = null;
    }

    @Override
    public int getCount() {
        if (recipeEntries.size() == 0) return 0;
        Log.d("widget2 - getCount",String.valueOf(recipeEntries.size()));
        return recipeEntries.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_gridview_item);
        if (recipeEntries.size() > 0) {
            views.setImageViewBitmap(R.id.iv_widget_recipes, BitmapFactory.decodeByteArray(
                    recipeEntries.get(position).getRecipeImage(),
                    0,
                    recipeEntries.get(position).getRecipeImage().length));
            views.setTextViewText(R.id.tv_widget_recipes_name, recipeEntries.get(position).getName());

            //Set up fill intent
            Bundle bundle = new Bundle();
            bundle.putInt(RecipeActivity.EXTRA_RECIPE_ID, recipeEntries.get(position).getRecipeId());
            bundle.putBoolean(RecipeActivity.EXTRA_IS_FROM_WIDGET, true);

            Intent fillIntent = new Intent();
            fillIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.iv_widget_recipes,fillIntent);
            views.setOnClickFillInIntent(R.id.tv_widget_recipes_name,fillIntent);

        }else {
            views.setImageViewResource(R.id.iv_widget_recipes, R.drawable.bk);
            views.setTextViewText(R.id.tv_widget_recipes_name, "No Recipies");
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}