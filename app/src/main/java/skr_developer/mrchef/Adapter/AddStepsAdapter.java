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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skr_developer.mrchef.Model.Step;
import skr_developer.mrchef.R;

public class AddStepsAdapter extends RecyclerView.Adapter<AddStepsAdapter.StepViewHolder> {

    private Context context;
    private List<Step> stepList;
    private OnStepAddVideoButtonClick mOnStepAddVideoButtonClick;

    public AddStepsAdapter(OnStepAddVideoButtonClick mOnStepAddVideoButtonClick) {
        this.mOnStepAddVideoButtonClick = mOnStepAddVideoButtonClick;
        stepList = new ArrayList<>();
        addStep();
    }

    public interface OnStepAddVideoButtonClick{
        public void onAddVideoButtonClick(int position);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutID = R.layout.add_recipe_step_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID,parent,false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StepViewHolder holder, final int position) {

        holder.et_step_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (position < stepList.size()) {
                    stepList.get(position).setShortDescription(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.et_step_desp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (position < stepList.size()) {
                    stepList.get(position).setDescription(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.et_video_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (position < stepList.size()) {
                    stepList.get(position).setVideoURL(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (!stepList.get(position).getVideoURL().equals("")){
            holder.et_video_url.setText(stepList.get(position).getVideoURL());
            holder.pb_add_video.setVisibility(View.GONE);
            holder.btn_add_video.setVisibility(View.VISIBLE);
        }else {
            holder.pb_add_video.setVisibility(View.GONE);
            holder.btn_add_video.setVisibility(View.VISIBLE);
        }

        holder.btn_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnStepAddVideoButtonClick.onAddVideoButtonClick(holder.getAdapterPosition());
                holder.pb_add_video.setVisibility(View.VISIBLE);
                holder.btn_add_video.setVisibility(View.INVISIBLE);
            }
        });

        fillUpFields(holder,position);
    }
    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.et_step_title)
        EditText et_step_title;
        @BindView(R.id.et_step_desp)
        EditText et_step_desp;
        @BindView(R.id.et_video_url)
        EditText et_video_url;
        @BindView(R.id.btn_add_video)
        Button btn_add_video;
        @BindView(R.id.pb_add_video)
        ProgressBar pb_add_video;
        @BindView(R.id.background)
        ConstraintLayout background;
        @BindView(R.id.foreground)
        CardView foreground;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            pb_add_video.setVisibility(View.GONE);
        }

    }

    private void fillUpFields(StepViewHolder holder,int position) {

        if (!stepList.get(position).getShortDescription().equals("")){
            holder.et_step_title.setText(stepList.get(position).getShortDescription());
        }
        if (!stepList.get(position).getDescription().equals("")){
            holder.et_step_desp.setText(stepList.get(position).getDescription());
        }
        if (!stepList.get(position).getVideoURL().equals("")){
            holder.et_video_url.setText(stepList.get(position).getVideoURL());
        }
    }

    public void addStep(){
        if (stepList == null){
            stepList = new ArrayList<>();
            stepList.add(new Step(0, "", "", "", ""));
        }else {
            stepList.add(new Step(0, "", "", "", ""));
        }
    }

    public void removeItem(int position){
        stepList.remove(position);
        notifyItemRemoved(position);

    }
    public void clearAllRecord() {
        stepList.clear();
        notifyDataSetChanged();
    }
    public void addStepVideo(int position, String videoURL){
        stepList.get(position).setVideoURL(videoURL);
        notifyItemChanged(position);
    }

    public List<Step> getstepList() {
        return stepList;
    }
    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }
}
