package skr_developer.mrchef.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.HelperClasses.HelperClass;
import skr_developer.mrchef.Model.Recipe;
import skr_developer.mrchef.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeCardViewHolder>{

    private Context context;
    private List<Recipe> recipeList;
    private HelperClass helperClass;
    private OnClickRecipeCardListener onClickRecipeCardListener;

    public RecipeAdapter(OnClickRecipeCardListener onClickRecipeCardListener, List<Recipe> recipeList){
        this.onClickRecipeCardListener = onClickRecipeCardListener;
        this.recipeList = recipeList;
    }

    public interface OnClickRecipeCardListener{
        public void onClickRecipe(View view, int clickedMoviePosition);
    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutID = R.layout.recipe_card_item_2;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,viewGroup,false);
        helperClass  = new HelperClass();
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder recipeCardViewHolder, int position) {

        if (position< recipeList.size()){
            helperClass.loadImage(context,recipeList.get(position).getImage(),recipeCardViewHolder.iv_recipeCard);
            recipeCardViewHolder.tv_recipeName.setText(recipeList.get(position).getName());
        }

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_recipecard)
        ImageView iv_recipeCard;
        @BindView(R.id.tv_recipe_name)
        TextView tv_recipeName;

        RecipeCardViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            iv_recipeCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onClickRecipeCardListener.onClickRecipe(view, getAdapterPosition());
        }
    }
}
