package com.example.testcarbonnotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAdapter extends ArrayAdapter<Item> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Item> FileList;
    private MyNotesActivity activity;
    public ItemAdapter(Context context, int resource, ArrayList<Item> files, MyNotesActivity x) {
        super(context, resource,files);
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        FileList=files;
        activity=x;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Item item=FileList.get(position);
        viewHolder.nameView.setText(item.getName());
        viewHolder.textView.setText(item.getText());
        viewHolder.delete.setImageResource(R.drawable.ic_delete_black_24dp);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.delete(item.getName());
            }
        });
        viewHolder.edit.setImageResource(R.drawable.ic_edit_black_24dp);
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.edit(item.getName());
            }
        });
        return  convertView;

    }

    private class ViewHolder {
        final TextView nameView,textView;
        final ImageView delete,edit;
        ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.name);
            textView = (TextView) view.findViewById(R.id.noteText);
            delete= view.findViewById(R.id.delete);
            edit=view.findViewById(R.id.edit);
        }
    }
}
