package com.vmoksha.schoolbusdriver.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.activity.PickupPoint;
import com.vmoksha.schoolbusdriver.model.PickupPointModel;
import com.vmoksha.schoolbusdriver.util.CircleImageView;

import java.util.ArrayList;

/**
 * Created by anshikas on 28-01-2016.
 */
public class BoardedChildrenAdapter  {
   /* Context context;
    ArrayList<PickupPointModel> pickuplist;
    public BoardedChildrenAdapter(Context context, ArrayList<PickupPointModel> pickuplist) {
        this.context = context;
        this.pickuplist = pickuplist;
    }
        public int getCount() {
            return pickuplist.size();
        }

        @Override
        public Object getItem(int position) {
            return pickuplist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.boarded_students_list_items, null);
            holder = new Holder();
            holder.mStudentname = (TextView) convertView.findViewById(R.id.childname);
           *//* holder.mRollno=(TextView) convertView.findViewById(R.id.childroll);*//*
            holder.mStudentClass = (TextView) convertView.findViewById(R.id.childclass);
            holder.mStudentRollno = (TextView) convertView.findViewById(R.id.childroll);
            holder.pickup_image = (CircleImageView) convertView.findViewById(R.id.rounded_childpic);

            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();


        holder.mStudentname.setText(pickuplist.get(position).getFirstName());
       *//* holder.mRollno.setText(pickuplist.get(position).getRegistrationId());*//*
        holder.mStudentClass.setText(pickuplist.get(position).getStudentClass());
        holder.mStudentRollno.setText(pickuplist.get(position).getRegistrationId());
        holder.pickup_image.setImageResource(pickuplist.get(position).getStudentpic());

        return convertView;
    }

    public class Holder {
        CircleImageView pickup_image;
        *//*TextView childname, childclass, childroll, select;
        ImageView childrenselect;*//*
        TextView mStudentname,mStudentClass,mStudentRollno;

    }*/

}
