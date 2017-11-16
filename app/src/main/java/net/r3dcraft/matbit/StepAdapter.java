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
 */

class StepAdapter extends BaseAdapter {

    private Context context;
    private boolean attachOnClick = false;
    private ArrayList<Step> data = new ArrayList<Step>();
    private static LayoutInflater inflater = null;

    public StepAdapter(Context context, ArrayList<Step> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public StepAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public StepAdapter(Context context, boolean attachOnClick) {
        this.context = context;
        this.attachOnClick = attachOnClick;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.step_item, null);

        final int index = position;
        final TextView txt_nr = (TextView) view.findViewById(R.id.step_item_txt_nr);
        final TextView txt_text = (TextView) view.findViewById(R.id.step_item_txt_text);

        txt_nr.setText(Integer.toString(index + 1));
        txt_text.setText(data.get(index).getString());

        if (attachOnClick) {
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Vil du slette steget?")
                            .setPositiveButton("Slett", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    data.remove(index);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }
            });
        } else {
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

    public ArrayList<Step> getStepList() {
        return data;
    }

    public void setStepList(ArrayList<Step> stepList) {
        this.data = stepList;
        notifyDataSetChanged();
    }

    public void addStep(Step step) {
        data.add(step);
        notifyDataSetChanged();
    }
}