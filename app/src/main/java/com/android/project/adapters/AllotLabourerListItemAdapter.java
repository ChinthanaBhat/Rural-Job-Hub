package com.android.project.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.project.model.Labourer;
import com.android.project.R;
import com.android.project.utility.Constants;
import com.bumptech.glide.Glide;


import java.util.List;

public class AllotLabourerListItemAdapter extends ArrayAdapter<Labourer> {
    private Activity context;
    List<Labourer> labourerList;
    LayoutInflater inflater;

    public AllotLabourerListItemAdapter(Activity context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder
    {
        TextView nameTV;
        ImageView iconIV;
//        CheckBox presentCB;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.allotlabourerlist_item, null);
            holder.nameTV = view.findViewById(R.id.name);
            holder.iconIV = view.findViewById(R.id.profile);
            view.setTag(holder);
        } else
        {
            holder = (ViewHolder) view.getTag();
        }
        holder.nameTV.setText(labourerList.get(position).getName());
        String imagePath = labourerList.get(position).getProfilePath();
        Glide.with(context)
                .load(imagePath)
                .into(holder.iconIV);
//        holder.presentCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    selectedStrings.add(tv.getText().toString());
//                }else{
//                    selectedStrings.remove(tv.getText().toString());
//                }
//
//            }
//        });


        return view;
    }

    public List<Labourer> getLabourerList() {
        return labourerList;
    }

    public void setLabourerList(List<Labourer> labourerList)
    {
        this.labourerList = labourerList;
    }

    @Override
    public int getCount() {
        return labourerList.size();
    }

    @Override
    public Labourer getItem(int position) {
        return labourerList.get(position);
    }

}


