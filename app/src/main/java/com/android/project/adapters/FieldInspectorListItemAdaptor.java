package com.android.project.adapters;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.project.R;
import com.android.project.model.FieldInspector;
import com.android.project.model.Labourer;
import com.android.project.utility.Constants;
import com.bumptech.glide.Glide;

import java.util.List;

public class FieldInspectorListItemAdaptor extends ArrayAdapter<FieldInspector> {

    private Activity context;
    List<FieldInspector> fieldInspectorList;
    LayoutInflater inflater;

    public FieldInspectorListItemAdaptor(Activity context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder
    {
        TextView nameTV;
        TextView mobileTV;

        ImageView iconIV;
    }

    public View getView(int position, View view, ViewGroup parent) {

        final FieldInspectorListItemAdaptor.ViewHolder holder;
        if (view == null) {
            holder = new FieldInspectorListItemAdaptor.ViewHolder();
            view = inflater.inflate(R.layout.fieldinpector_list, null);
            holder.nameTV = view.findViewById(R.id.name);
            holder.mobileTV = view.findViewById(R.id.mobile);

            holder.iconIV = view.findViewById(R.id.profile);
            view.setTag(holder);
        } else
        {
            holder = (FieldInspectorListItemAdaptor.ViewHolder) view.getTag();
        }
        holder.nameTV.setText(fieldInspectorList.get(position).getName());
        holder.mobileTV.setText(fieldInspectorList.get(position).getMobile());

        String imagePath = fieldInspectorList.get(position).getImage();
        Glide.with(context)
                .load(imagePath)
                .into(holder.iconIV);


        return view;
    }

    public List<FieldInspector> getFieldInspectorList() {
        return fieldInspectorList;
    }

    public void setFieldInspectorList(List<FieldInspector> fieldInspectorList)
    {
        this.fieldInspectorList = fieldInspectorList;
    }

    @Override
    public int getCount() {
        return fieldInspectorList.size();
    }

    @Override
    public FieldInspector getItem(int position) {
        return fieldInspectorList.get(position);
    }

}