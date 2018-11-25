package skr_developer.mrchef.HelperClasses;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import skr_developer.mrchef.Adapter.AddIngredientsAdapter;
import skr_developer.mrchef.Adapter.AddStepsAdapter;
import skr_developer.mrchef.R;
/*
    Helper class to support ItemTouchHelper.SimpleCallback on Recyclerview Item.
 */
public class RecyclerViewItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs,RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView = null;
            if (viewHolder instanceof AddIngredientsAdapter.IngredientViewHolder) {
                 foregroundView = ((AddIngredientsAdapter.IngredientViewHolder) viewHolder).itemView;
            }else if (viewHolder instanceof AddStepsAdapter.StepViewHolder){
                 foregroundView = ((AddStepsAdapter.StepViewHolder) viewHolder).itemView;
            }
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = null;
        if (viewHolder instanceof AddIngredientsAdapter.IngredientViewHolder){
            foregroundView = ((AddIngredientsAdapter.IngredientViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }else if (viewHolder instanceof AddStepsAdapter.StepViewHolder){
            foregroundView = ((AddStepsAdapter.StepViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }
        getDefaultUIUtil().onDrawOver(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View foregroundView = null;
        if (viewHolder instanceof AddIngredientsAdapter.IngredientViewHolder){
            foregroundView = ((AddIngredientsAdapter.IngredientViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }else if (viewHolder instanceof AddStepsAdapter.StepViewHolder){
            foregroundView = ((AddStepsAdapter.StepViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View foregroundView = null;
        if (viewHolder instanceof AddIngredientsAdapter.IngredientViewHolder){
            foregroundView = ((AddIngredientsAdapter.IngredientViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }else if (viewHolder instanceof AddStepsAdapter.StepViewHolder){
            foregroundView = ((AddStepsAdapter.StepViewHolder) viewHolder).itemView.findViewById(R.id.foreground);
        }
        getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        if (viewHolder instanceof AddIngredientsAdapter.IngredientViewHolder) {
            listener.onSwipedIngredient(viewHolder, direction, viewHolder.getAdapterPosition());
        }else if (viewHolder instanceof AddStepsAdapter.StepViewHolder){
            listener.onSwipedStep(viewHolder,direction,viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwipedIngredient(RecyclerView.ViewHolder viewHolder, int direction, int position);
        void onSwipedStep(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
