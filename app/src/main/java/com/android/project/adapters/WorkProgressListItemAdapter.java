package com.android.project.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.project.R;
import com.android.project.model.WorkProgress;

import java.util.List;


public class WorkProgressListItemAdapter extends ArrayAdapter<WorkProgress> {
    private Activity context;
    List<WorkProgress> workProgressList;
    LayoutInflater inflater;

    public WorkProgressListItemAdapter(Activity context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder
    {
        TextView dateTV;
        TextView descriptionTV;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.workprogresslist_item, null);

            holder.dateTV = view.findViewById(R.id.date);
            holder.descriptionTV = view.findViewById(R.id.description);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }

        holder.dateTV.setText("Date: "+ workProgressList.get(position).getDate());
        holder.descriptionTV.setText("Description: "+ workProgressList.get(position).getDescription());

        return view;
    }

    public List<WorkProgress> getWorkProgressList() {
        return this.workProgressList;
    }

    public void setWorkProgressList(List<WorkProgress> workProgressList)
    {
        this.workProgressList = workProgressList;
    }

    @Override
    public int getCount() {
        return workProgressList.size();
    }

    @Override
    public WorkProgress getItem(int position) {
        return workProgressList.get(position);
    }

}

