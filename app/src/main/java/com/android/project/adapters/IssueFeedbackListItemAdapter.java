package com.android.project.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.project.R;
import com.android.project.model.IssueAndFeedback;
import com.android.project.model.WorkProgress;

import java.util.List;


public class IssueFeedbackListItemAdapter extends ArrayAdapter<IssueAndFeedback> {
    private Activity context;
    List<IssueAndFeedback> issueAndFeedbackList;
    LayoutInflater inflater;

    public IssueFeedbackListItemAdapter(Activity context, int resourceId)
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
            view = inflater.inflate(R.layout.issue_feedbacklist_item, null);

            holder.dateTV = view.findViewById(R.id.date);
            holder.descriptionTV = view.findViewById(R.id.description);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }

        holder.dateTV.setText("Date: "+ issueAndFeedbackList.get(position).getDate());
        holder.descriptionTV.setText("Description: "+ issueAndFeedbackList.get(position).getDescription());

        return view;
    }

    public List<IssueAndFeedback> getIssueAndFeedbackList() {
        return this.issueAndFeedbackList;
    }

    public void setIssueAndFeedbackList(List<IssueAndFeedback> issueAndFeedbackList)
    {
        this.issueAndFeedbackList = issueAndFeedbackList;
    }

    @Override
    public int getCount() {
        return issueAndFeedbackList.size();
    }

    @Override
    public IssueAndFeedback getItem(int position) {
        return issueAndFeedbackList.get(position);
    }

}

