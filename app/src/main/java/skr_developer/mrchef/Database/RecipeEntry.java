package skr_developer.mrchef.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int recipeId;
    private String name;
    private int servings;
    private String image;
    private byte[] recipeImage;

    @Ignore
    public RecipeEntry(int recipeId, String name, int servings, String image,byte[] recipeImage) {
        this.recipeId = recipeId;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.recipeImage = recipeImage;
    }

    public RecipeEntry(int id, int recipeId, String name, int servings, String image,byte[] recipeImage) {
        this.id = id;
        this.recipeId = recipeId;
        this.name = name;
        this.servings = servings;
        this.image = image;
        this.recipeImage = recipeImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(byte[] recipeImage) {
        this.recipeImage = recipeImage;
    }

}
