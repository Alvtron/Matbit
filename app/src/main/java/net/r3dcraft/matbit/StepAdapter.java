package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 08.11.2017.
 *
 * This Step adapter extends the BaseAdapter and keeps track of steps and loads the correct views
 * with each step.
 */
class StepAdapter extends BaseAdapter {

    private Context context;
    private boolean attachOnClick = false;
    private ArrayList<Step> steps = new ArrayList<Step>();
    private static LayoutInflater inflater = null;

    /**
     * Constructor for step adapter.
     * @param context Activity where step adapter is initialized
     * @param steps steps
     * @param attachOnClick attach onclick method to steps
     */
    public StepAdapter(Context context, ArrayList<Step> steps, boolean attachOnClick) {
        this.context = context;
        this.steps = steps;
        this.attachOnClick = attachOnClick;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Constructor for step adapter.
     * @param context Activity where step adapter is initialized
     * @param attachOnClick attach onclick method to steps
     */
    public StepAdapter(Context context, boolean attachOnClick) {
        this.context = context;
        this.attachOnClick = attachOnClick;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @return number of steps in this adapter
     */
    @Override
    public int getCount() {
        return steps.size();
    }

    /**
     *
     * @param position step position
     * @return step at position
     */
    @Override
    public Object getItem(int position) {
        return steps.get(position);
    }

    /**
     *
     * @param position step position
     * @return step position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view of step at position
     * @param position step position
     * @param convertView convert view
     * @param parent parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.step_item, null);

        final int index = position;
        final TextView txt_nr = view.findViewById(R.id.step_item_txt_nr);
        final TextView txt_text = view.findViewById(R.id.step_item_txt_text);

        txt_nr.setText(Integer.toString(index + 1));
        txt_text.setText(steps.get(index).getString());

        if (attachOnClick) {
            // Decide to delete step or not. For step list in create recipe acitivty.
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.string_do_you_want_to_delete_this_step)
                            .setPositiveButton(R.string.string_delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    steps.remove(index);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            });
        } else {
            // Toggle fade out step
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (txt_nr.getAlpha() < 1.0 && txt_text.getAlpha() < 1.0) {
                        txt_nr.setAlpha(1.0f);
                        txt_text.setAlpha(1.0f);
                    } else {
                        txt_nr.setAlpha(0.25f);
                        txt_text.setAlpha(0.25f);
                    }
                }
            });
        }
        return view;
    }

    /**
     *
     * @return get step list from step adapter
     */
    public ArrayList<Step> getStepList() {
        return steps;
    }

    /**
     * Add steps to step adapter.
     * @param step step object to add
     */
    public void addStep(Step step) {
        steps.add(step);
        notifyDataSetChanged();
    }
}