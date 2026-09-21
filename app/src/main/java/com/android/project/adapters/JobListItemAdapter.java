package com.android.project.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.project.R;
import com.android.project.model.Job;
//import com.android.project.model.Job;

import java.util.List;


public class JobListItemAdapter extends ArrayAdapter<Job> {
    private Activity context;
    List<Job> jobList;
    LayoutInflater inflater;

    public JobListItemAdapter(Activity context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder
    {
        TextView nameTV;
        TextView durationTV;
        TextView fundsTV;
        TextView descriptionTV;
        TextView numberOfLabourersTV;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.joblist_item, null);

            holder.nameTV = view.findViewById(R.id.title);
            holder.durationTV = view.findViewById(R.id.duration);
            holder.fundsTV = view.findViewById(R.id.funds);
            holder.descriptionTV = view.findViewById(R.id.description);
            holder.numberOfLabourersTV = view.findViewById(R.id.numberOfLabourers);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }

        holder.nameTV.setText("Title: "+jobList.get(position).getTitle());
        holder.descriptionTV.setText("Description: "+jobList.get(position).getDescription());
        holder.durationTV.setText("Duration: " +jobList.get(position).getDurationInDays());
        holder.fundsTV.setText("Funds: ₹" +jobList.get(position).getFundsAllotted());
        holder.numberOfLabourersTV.setText("Required Labourers: " +jobList.get(position).getNumberOfLabourers());

        return view;
    }

    public List<Job> getJobList() {
        return this.jobList;
    }

    public void setJobList(List<Job> jobList)
    {
        this.jobList = jobList;
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Job getItem(int position) {
        return jobList.get(position);
    }

}

