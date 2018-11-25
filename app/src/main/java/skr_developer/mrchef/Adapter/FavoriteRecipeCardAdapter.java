package skr_developer.mrchef.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.Model.Recipe;
import skr_developer.mrchef.R;

public class FavoriteRecipeCardAdapter extends RecyclerView.Adapter<FavoriteRecipeCardAdapter.RecipeCardViewHolder>{

    private List<Recipe> recipeList;
    private List<byte[]> recipeImages;

    private Context context;
    private OnClickRecipeCardListener onClickRecipeCardListener;

    public FavoriteRecipeCardAdapter(OnClickRecipeCardListener onClickRecipeCardListener, List<Recipe> recipeList, List<byte[]> recipeImages){
        this.onClickRecipeCardListener = onClickRecipeCardListener;
        this.recipeList = recipeList;
        this.recipeImages = recipeImages;
    }

    public interface OnClickRecipeCardListener{
        public void onClickRecipeCard(View view, int clickedMoviePosition);
    }

    @NonNull
    @Override
    public RecipeCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutID = R.layout.fav_recipe_card_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,viewGroup,false);
        return new RecipeCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeCardViewHolder recipeCardViewHolder, int position) {

        if (position< recipeList.size()){
            recipeCardViewHolder.iv_recipeCard.setImageBitmap(BitmapFactory.decodeByteArray(recipeImages.get(position),0,recipeImages.get(position).length));
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
            onClickRecipeCardListener.onClickRecipeCard(view, getAdapterPosition());
        }
    }
}
