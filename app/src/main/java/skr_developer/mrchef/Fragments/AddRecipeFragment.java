package skr_developer.mrchef.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skr_developer.mrchef.Adapter.AddIngredientsAdapter;
import skr_developer.mrchef.Adapter.AddStepsAdapter;
import skr_developer.mrchef.HelperClasses.HelperClass;
import skr_developer.mrchef.HelperClasses.RecyclerViewItemTouchHelper;
import skr_developer.mrchef.Model.Ingredient;
import skr_developer.mrchef.Model.Recipe;
import skr_developer.mrchef.Model.Step;
import skr_developer.mrchef.R;

import static android.app.Activity.RESULT_OK;

public class AddRecipeFragment extends Fragment implements RecyclerViewItemTouchHelper.RecyclerItemTouchHelperListener,
        AddStepsAdapter.OnStepAddVideoButtonClick {

    private static final String TAG = AddRecipeFragment.class.getSimpleName();

    private static final int REQUEST_ID_PICK_IMAGE = 100;
    private static final int REQUEST_ID_PICK_VIDEO = 200;
    private static final String INGREDIENTS_LIST_BUNDLE_KEY = "ingredients";
    private static final String STEPS_LIST_BUNDLE_KEY = "steps_list";
    private static final String RECIPE_NAME_BUNDLE_KEY = "recipe_name";
    private static final String IMAGE_URI_BUNDLE_KEY = "image_uri";
    private static final String BUNDLE_KEY = "imageuribundle";

    private Context context;
    private String userUID;
    private Uri imageUri;
    private String recipeImageURL;
    private int videoPosition = -1;

    private AddIngredientsAdapter addIngredientsAdapter;
    private AddStepsAdapter addStepsAdapter;

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    @BindView(R.id.recyclerview_ingredients)
    RecyclerView recyclerView_ingredients;
    @BindView(R.id.recyclerview_steps)
    RecyclerView recyclerview_steps;
    @BindView(R.id.iv_recipe_pic)
    ImageView iv_recipe_pic;
    @BindView(R.id.et_recipe_name)
    EditText et_recipe_name;
    @BindView(R.id.pb_recipe_pic)
    ProgressBar pb_recipe_pic;
    @BindView(R.id.cl_content)
    ConstraintLayout cl_content;
    @BindView(R.id.cardview_recipe_successfully)
    CardView cardview_recipe_successfully;

    public AddRecipeFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);
        ButterKnife.bind(this,view);
        context = view.getContext();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("recipes");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("recipes");
        pb_recipe_pic.setVisibility(View.GONE);

        showContent(true);
        setIngredientAdapter();
        setStepAdapter();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(INGREDIENTS_LIST_BUNDLE_KEY, (ArrayList<? extends Parcelable>) addIngredientsAdapter.getIngredientList());
        outState.putParcelableArrayList(STEPS_LIST_BUNDLE_KEY, (ArrayList<? extends Parcelable>) addStepsAdapter.getstepList());
        Bundle bundle = new Bundle();
        bundle.putParcelable(IMAGE_URI_BUNDLE_KEY,imageUri);
        outState.putBundle(BUNDLE_KEY,bundle);
        outState.putString(RECIPE_NAME_BUNDLE_KEY,et_recipe_name.getText().toString());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            addIngredientsAdapter.setIngredientList(savedInstanceState.<Ingredient>getParcelableArrayList(INGREDIENTS_LIST_BUNDLE_KEY));
            addStepsAdapter.setStepList(savedInstanceState.<Step>getParcelableArrayList(STEPS_LIST_BUNDLE_KEY));
            et_recipe_name.setText(savedInstanceState.getString(RECIPE_NAME_BUNDLE_KEY));
            Bundle bundle = savedInstanceState.getBundle(BUNDLE_KEY);
            imageUri = bundle.getParcelable(IMAGE_URI_BUNDLE_KEY);
            iv_recipe_pic.setImageURI(imageUri);
        }
    }

    @OnClick(R.id.ib_add_ingredients)
    public void addIngredientOnClick(View view){
        addIngredientsAdapter.addIngredient();
        addIngredientsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.ib_add_steps)
    public void addStepsOnClick(View view){
        addStepsAdapter.addStep();
        addStepsAdapter.notifyDataSetChanged();
        addStepsAdapter.notifyItemInserted(0);
    }

    @OnClick(R.id.iv_recipe_pic)
    public void addRecipePic(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , REQUEST_ID_PICK_IMAGE);
    }

    @OnClick(R.id.btn_add_recipe)
    public void addRecipe(View view){

        if (!isEditTextRecipeNameValid() && recipeImageURL == null){
            new HelperClass().showSimpleSnakbar(view.getRootView().findViewById(R.id.bottom_navigation_view),"Please enter the recipe name and select a picture!");
            return;
        }
        if (!isEditTextRecipeNameValid()){
            new HelperClass().showSimpleSnakbar(view,"Please enter the recipe name!");
            return;
        }
        if (recipeImageURL == null){
            new HelperClass().showSimpleSnakbar(view,"Please upload a picture!");
            return;
        }

        int id = (int)System.currentTimeMillis();

        Recipe recipe = new Recipe(id,et_recipe_name.getText().toString(),
                addIngredientsAdapter.getIngredientList(),
                addStepsAdapter.getstepList(),
                addStepsAdapter.getstepList().size(),recipeImageURL);
        mDatabaseRef.push().setValue(recipe);

        addIngredientsAdapter.clearAllRecord();
        addStepsAdapter.clearAllRecord();
        showContent(false);
    }


    @OnClick(R.id.btn_add_recipe_again)
    public void addRecipeAgain(View view){
        showContent(true);
        et_recipe_name.setText(null);
        addIngredientsAdapter.addIngredient();
        addStepsAdapter.addStep();
        iv_recipe_pic.setBackground(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == REQUEST_ID_PICK_IMAGE && resultCode == RESULT_OK ){

            pb_recipe_pic.setVisibility(View.VISIBLE);

            imageUri = data.getData();
            iv_recipe_pic.setImageURI(imageUri);

            mStorageRef.child(imageUri.getLastPathSegment()+"_"+ System.currentTimeMillis()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(context, "Upload Done" , Toast.LENGTH_LONG).show();

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            recipeImageURL = String.valueOf(uri);
                            pb_recipe_pic.setVisibility(View.GONE);
                            Log.d(TAG + " recipeImageURL", recipeImageURL);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG + "url", e.toString());
                        }
                    });
                }
            });
        }else if (requestCode == REQUEST_ID_PICK_VIDEO && resultCode == RESULT_OK){

            Uri videoUri = data.getData();
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("recipe_steps");
            if (et_recipe_name.getText().toString().isEmpty()){
                Toast.makeText(context, "Please enter the Recipe Name first" , Toast.LENGTH_LONG).show();
                return;
            }

            reference.child(userUID).child(et_recipe_name.getText().toString() +"_"+ System.currentTimeMillis())
                    .putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "Upload Done" , Toast.LENGTH_LONG).show();

                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String videoURL = String.valueOf(uri);
                                    Toast.makeText(context, "Upload " + videoURL , Toast.LENGTH_LONG).show();
                                    addStepsAdapter.addStepVideo(videoPosition,videoURL);
                                    Log.d(TAG + " videoURL", videoURL);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG + "url", e.toString());
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void onSwipedIngredient(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        addIngredientsAdapter.removeItem(position);
    }

    @Override
    public void onSwipedStep(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        addStepsAdapter.removeItem(position);
    }

    @Override
    public void onAddVideoButtonClick(int position) {
        videoPosition = position;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent , REQUEST_ID_PICK_VIDEO);
    }

    /*
        Helper Functions
     */
    private void setIngredientAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView_ingredients.setLayoutManager(layoutManager);
        recyclerView_ingredients.setHasFixedSize(true);
        addIngredientsAdapter = new AddIngredientsAdapter();
        recyclerView_ingredients.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView_ingredients.setAdapter(addIngredientsAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerViewItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView_ingredients);
    }

    private void setStepAdapter(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerview_steps.setLayoutManager(layoutManager);
        recyclerview_steps.setHasFixedSize(true);
        addStepsAdapter = new AddStepsAdapter(this);
        recyclerview_steps.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerview_steps.setAdapter(addStepsAdapter);

        ItemTouchHelper.SimpleCallback simpleCallbackStep = new RecyclerViewItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallbackStep).attachToRecyclerView(recyclerview_steps);
    }

    private boolean isEditTextRecipeNameValid(){
        if (et_recipe_name.getText().toString().isEmpty()){
            et_recipe_name.setError("Must not be Empty");
            return false;
        }else {
            return true;
        }
    }

    private void showContent(boolean show){
        if (show){
            cl_content.setVisibility(View.VISIBLE);
            cardview_recipe_successfully.setVisibility(View.GONE);
        }else {
            cl_content.setVisibility(View.GONE);
            cardview_recipe_successfully.setVisibility(View.VISIBLE);
        }
    }

}
