package com.example.bboyi.todolist.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.bboyi.todolist.R;
import com.example.bboyi.todolist.Utils;
import com.example.bboyi.todolist.fragment.CurrentTaskFragment;
import com.example.bboyi.todolist.model.Item;
import com.example.bboyi.todolist.model.ModelTask;

import java.util.ArrayList;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentTasksAdapter extends TaskAdapter {



    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;

    public CurrentTasksAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case TYPE_TASK:
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_task, viewGroup, false);
                TextView title = (TextView) v.findViewById(R.id.tvTaskTitle);
                TextView date = (TextView) v.findViewById(R.id.tvTaskDate);
                CircleImageView priority = (CircleImageView) v.findViewById(R.id.cvTaskPriority);

                return new TaskViewHolder(v, title, date, priority);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Item item = items.get(position);

        if (item.isTask()) {
            viewHolder.itemView.setEnabled(true);
            final ModelTask task = (ModelTask) item;
            final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;

            final View itemView = taskViewHolder.itemView;
            final Resources resources = itemView.getResources();

            taskViewHolder.title.setText(task.getTitle());
            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(null);
            }

            itemView.setVisibility(View.VISIBLE);

            itemView.setBackgroundColor(resources.getColor(R.color.gray_50));

            taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_default_material_light));
            taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_default_material_light));
            taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                        }
                    }, 1000);

                    return true;
                }
            });

            taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    task.setStatus(ModelTask.STATUS_DONE);
                    getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(), ModelTask.STATUS_DONE);

                    itemView.setBackgroundColor(resources.getColor(R.color.gray_200));

                    taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_disabled_material_light));
                    taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_disabled_material_light));
                    taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                    ObjectAnimator flipIn = ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", -180f, 0f);

                    flipIn.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (task.getStatus() == ModelTask.STATUS_DONE) {


                                ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,
                                        "translationX", 0f, itemView.getWidth());

                                ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,
                                        "translationX", itemView.getWidth(), 0f);


                                translationX.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        itemView.setVisibility(View.GONE);
                                        getTaskFragment().moveTask(task);
                                        removeItem(taskViewHolder.getLayoutPosition());
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });

                                AnimatorSet translationSet = new AnimatorSet();
                                translationSet.play(translationX).before(translationXBack);
                                translationSet.start();
                            }

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    flipIn.start();


                }
            });
        }

    }



    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }


}