package skr_developer.mrchef.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.Model.Ingredient;
import skr_developer.mrchef.R;

public class AddIngredientsAdapter extends RecyclerView.Adapter<AddIngredientsAdapter.IngredientViewHolder> {

    private String[] unit = { "TSP" , "CUP", "MG" , "Lt" };

    private Context context;
    private List<Ingredient> ingredientList;
    private IngredientViewHolder ingredientViewHolder;

    public AddIngredientsAdapter() {
        ingredientList = new ArrayList<>();
        addIngredient();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutID = R.layout.add_recipe_ingredient_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,parent,false);
        ingredientViewHolder = new IngredientViewHolder(view);
        return ingredientViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientViewHolder holder, final int position) {

        holder.et_ingredient.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (position < ingredientList.size()) {
                    ingredientList.get(position).setIngredient(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.et_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && position < ingredientList.size()) {
                    ingredientList.get(position).setQuantity(Float.valueOf(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posi, long id) {
                ingredientList.get(position).setMeasure(String.valueOf(holder.spinner.getItemAtPosition(posi)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fillUpFields(holder,position);
    }

    private void fillUpFields(IngredientViewHolder holder, int position) {
        if (!ingredientList.get(position).getIngredient().equals("")){
            holder.et_ingredient.setText(ingredientList.get(position).getIngredient());
        }
        if (!ingredientList.get(position).getMeasure().equals("")){
            int spinnerPositon = 0;
            switch (ingredientList.get(position).getMeasure()){
                case "TSP":
                    spinnerPositon = 0;
                    break;
                case "CUP":
                    spinnerPositon = 1;
                    break;
                case "MG":
                    spinnerPositon = 2;
                    break;
                case "Lt":
                    spinnerPositon = 3;
                    break;
            }
            holder.spinner.setSelection(spinnerPositon,true);
        }
        if (!(ingredientList.get(position).getQuantity() == -1)){
            holder.et_qty.setText(String.valueOf(ingredientList.get(position).getQuantity()));
        }
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.et_ingredient)
        EditText et_ingredient;
        @BindView(R.id.et_qty)
        EditText et_qty;
        @BindView(R.id.spinner_unit)
        Spinner spinner;
        @BindView(R.id.background)
        ConstraintLayout background;
        @BindView(R.id.foreground)
        CardView foreground;

        IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            setSpinnerAdapter(unit,spinner);
        }

    }

    private void setSpinnerAdapter(String[] list, Spinner spinner){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_list , list);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void addIngredient(){
        ingredientList.add(new Ingredient(-1,"",""));
    }

    public void removeItem(int position){
        ingredientList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public void clearAllRecord() {
        ingredientList.clear();
        notifyDataSetChanged();
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }
    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }
}
