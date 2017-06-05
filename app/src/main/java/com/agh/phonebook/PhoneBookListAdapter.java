package com.agh.phonebook;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Damian on 2017-06-04.
 */

public class PhoneBookListAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> contactsList;
    private LayoutInflater mInflater;

    protected PhoneBookListAdapter(@NonNull Context context, @LayoutRes int resource,
                                @NonNull ArrayList<Contact> objects) {
        super(context, resource, objects);
        contactsList = objects;
        mInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView text;
        ImageView avatar;
        ProgressBar progress;
        int position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.contact_row, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.contact_name);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.progress = (ProgressBar) convertView.findViewById(R.id.avatar_progress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(contactsList.get(position).getContactName());

        holder.position = position;

        new AsyncTask<ViewHolder, Void, Bitmap>() {
            private ViewHolder v;

            @Override
            protected Bitmap doInBackground(ViewHolder... params) {
                v = params[0];

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                if (v.position == position) {
                    // If this item hasn't been recycled already, hide the
                    // progress and set and show the image
                    v.progress.setVisibility(View.GONE);
                    v.avatar.setVisibility(View.VISIBLE);
                    v.avatar.setImageResource(R.drawable.ic_account);
                }
            }
        }.execute(holder);

        return convertView;
    }
}
