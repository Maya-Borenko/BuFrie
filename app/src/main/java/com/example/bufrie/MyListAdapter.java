package com.example.bufrie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lain;
    ArrayList<Add> adds;

    MyListAdapter(Context context, ArrayList<Add> adds){
        this.ctx = context;
        this.adds = adds;
        lain = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return adds.size();
    }

    @Override
    public Object getItem(int position) {
        return adds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = lain.inflate(R.layout.fragment_ad, parent, false);
        }

        Add a = getAdd(position);
        ((TextView) view.findViewById(R.id.name)).setText(a.title);
        ((TextView) view.findViewById(R.id.price)).setText(a.price+"");
        ((TextView) view.findViewById(R.id.seller)).setText(a.seller);
        if (a.image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(a.image, 0, a.image.length);
            if (((ImageView) view.findViewById(R.id.ani)) != null) {((ImageView) view.findViewById(R.id.ani)).setImageBitmap(bitmap);}
        }
        else{((ImageView) view.findViewById(R.id.ani)).setImageResource(R.drawable.icon);}
        ((TextView) view.findViewById(R.id.adId)).setText(a.id+"");
        return view;
    }

    private Add getAdd(int position) {
        return (Add) getItem(position);
    }
    ArrayList<Add> getBox(){
        ArrayList<Add> box = new ArrayList<Add>();
        for (Add a: adds){
            if (a.like){
                box.add(a);
            }
        }
        return box;
    }
}
