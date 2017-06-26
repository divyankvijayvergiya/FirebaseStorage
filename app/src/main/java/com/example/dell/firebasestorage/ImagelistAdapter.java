package com.example.dell.firebasestorage;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Dell on 07-05-2017.
 */

public class ImagelistAdapter extends ArrayAdapter<ImageUpload> {
    private Activity context;
    private int resource;
    private List<ImageUpload> listimage;


    public ImagelistAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<ImageUpload> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        listimage=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View v=inflater.inflate(resource,null);
        TextView tvname= (TextView) v.findViewById(R.id.tvimage);
        ImageView img= (ImageView) v.findViewById(R.id.imageview2);
        tvname.setText(listimage.get(position).getName());
        Glide.with(context).load(listimage.get(position).getUrl()).into(img);
        return v;
    }
}
