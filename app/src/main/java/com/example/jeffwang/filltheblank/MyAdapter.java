package com.example.jeffwang.filltheblank;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

class MyAdapter<T> extends ArrayAdapter<T> {

    private ArrayList<T> mData;

    MyAdapter(ArrayList<T> data, Context context) {
        super(context, android.R.layout.simple_list_item_1, data);
        mData = data;
    }

    boolean contains(T object) {
        return mData.contains(object);
    }
}
