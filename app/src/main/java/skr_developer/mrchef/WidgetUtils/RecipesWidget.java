package skr_developer.mrchef.WidgetUtils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

import skr_developer.mrchef.Database.RecipeEntry;
import skr_developer.mrchef.R;
import skr_developer.mrchef.RecipeActivity;

/**
 * Implementation of App Widget-2 functionality.
 */
public class RecipesWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,List<RecipeEntry> recipeEntries,
                                int appWidgetId) {
        Log.d("widget2 - Recipe","updateAppWidget");
        RemoteViews views;
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        if (width <= 180 ) {
            views = getSmallSizedRemoteView(context,recipeEntries.size());
            Log.d("widget2 - Recipe","width <= 180 ");
        }else {
            views = getRecipesGridViewRemoteView(context);
            Log.d("widget2 - Recipe","width > 180 ");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipesGridViewService.startQueryingRecipes(context);
        Log.d("widget2 - Recipe","onUpdate");
    }

    public static void updateRecipesGridViewWidgets(Context context, AppWidgetManager appWidgetManager,
                                                    List<RecipeEntry> recipeEntries, int[] appWidgetIds) {

        Log.d("widget2 - Recipe","updateRecipesGridViewWidgets1 appWidgetIds - " + String.valueOf(appWidgetIds.length));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, recipeEntries, appWidgetId);
            Log.d("widget2 - Recipe","updateRecipesGridViewWidgets");
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RecipesGridViewService.startQueryingRecipes(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /*
        Helper Functions
     */
    private static RemoteViews getSmallSizedRemoteView(Context context, int size){
        Log.d("widget2 - Recipe","getSmallSizedRemoteView");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.recipes_widget);

        Intent intent = new Intent(context,RecipesGridViewService.class);
        intent.setAction(RecipesGridViewService.ACTION_VIEW_RECIPES);

        PendingIntent pendingIntent = PendingIntent.getService(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setTextViewText(R.id.tv_recipes_widget, "You have " + String.valueOf(size) + " Recipies as your favorite one.");

        remoteViews.setOnClickPendingIntent(R.id.tv_recipes_widget, pendingIntent);
        return remoteViews;
    }

    private static RemoteViews getRecipesGridViewRemoteView(Context context){
        Log.d("widget2 - Recipe","getRecipesGridViewRemoteView");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_recipes_gridview);

        Intent intent = new Intent(context, RecipesGridviewWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.gv_recipes,intent);

        Intent appIntent = new Intent(context,RecipeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.gv_recipes,appPendingIntent);

        remoteViews.setEmptyView(R.id.gv_recipes,R.id.rl_gv_emptyView);
        return remoteViews;
    }
}

