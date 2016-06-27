package com.vmoksha.schoolbusdriver.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.activity.DashBoardActivityNew;
import com.vmoksha.schoolbusdriver.model.PickupPointModel;
import com.vmoksha.schoolbusdriver.util.CircleImageView;

import java.util.ArrayList;

/**
 * Created by shwethap on 19-01-2016.
 */
public class PickupPointAdapter extends BaseAdapter {

    Context context;
    ArrayList<PickupPointModel> pickuplist;
    boolean isSelected = false;
//    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private DisplayImageOptions options;


    public PickupPointAdapter(Context context, ArrayList<PickupPointModel> pickuplist) {
        this.context = context;
        this.pickuplist = pickuplist;
        options = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)

                .build();

                /*.showImageOnLoading(R.drawable.childpic)
                .showImageForEmptyUri(R.drawable.childpic)
                .showImageOnFail(R.drawable.childpic)*/
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final View listview = convertView;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.pickup_point_listview, null);
            holder = new Holder();
            holder.mStudentname = (TextView) convertView.findViewById(R.id.childname);
           /* holder.mRollno=(TextView) convertView.findViewById(R.id.childroll);*/
            holder.mStudentClass = (TextView) convertView.findViewById(R.id.childclass);
            holder.mStudentRollno = (TextView) convertView.findViewById(R.id.childroll);
            holder.student_pic = (CircleImageView) convertView.findViewById(R.id.rounded_childpic);
            holder.mNotBoardingtext = (TextView) convertView.findViewById(R.id.text);
            holder.childrenselect = (ImageView) convertView.findViewById(R.id.childrenselect_imageView);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();

        holder.childrenselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelected == true) {

                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    if(listview!=null)
                        listview.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.mStudentname.setTextColor(Color.parseColor("#333333"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#333333"));*/
                    holder.mStudentClass.setTextColor(Color.parseColor("#333333"));
                    holder.mStudentRollno.setTextColor(Color.parseColor("#333333"));
                    isSelected = false;
                } else {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);

                    // view.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    if(listview!=null)
                        listview.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    holder.mStudentname.setTextColor(Color.parseColor("#666666"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#666666"));*/
                    holder.mStudentClass.setTextColor(Color.parseColor("#666666"));
                    holder.mStudentRollno.setTextColor(Color.parseColor("#666666"));
                    isSelected = true;
                }
            }
        });


        holder.mStudentname.setText(pickuplist.get(position).getFirstName() + " " + pickuplist.get(position).getLastName());
        holder.mStudentClass.setText(pickuplist.get(position).getStudentClass());
        holder.mStudentRollno.setText(pickuplist.get(position).getRegistrationId());
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        if (pickuplist.get(position).getStudentpic().contentEquals("null")) {
            holder.student_pic.setImageResource(R.drawable.images);

        } else {
            ImageLoader.getInstance().displayImage(pickuplist.get(position).getStudentpic(), holder.student_pic, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.student_pic.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    super.onLoadingCancelled(imageUri, view);
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.student_pic.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    holder.progressBar.setVisibility(View.GONE);

                }
            });
        }
        holder.childrenselect.setImageResource(pickuplist.get(position).getStudentIconSelected());


        if (DashBoardActivityNew.buttonClicked) {
            holder.mNotBoardingtext.setVisibility(View.GONE);
            if (pickuplist.get(position).getIsBoarding() == true) {

                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.mStudentname.setTextColor(Color.parseColor("#333333"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#333333"));*/
                holder.mStudentClass.setTextColor(Color.parseColor("#333333"));
                holder.mStudentRollno.setTextColor(Color.parseColor("#333333"));

                holder.childrenselect.setVisibility(View.VISIBLE);
                //holder.select.setVisibility(View.GONE);

                holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                isSelected = true;
                //sets notify_next to unselected image for 2nd


            } else {
                //holder.mNotBoardingtext.setVisibility(View.GONE);
                //holder.select.setVisibility(View.GONE);

                convertView.setBackgroundColor(Color.parseColor("#f0f0f0"));
                holder.mStudentname.setTextColor(Color.parseColor("#666666"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#666666"));*/
                holder.mStudentClass.setTextColor(Color.parseColor("#666666"));
                holder.mStudentRollno.setTextColor(Color.parseColor("#666666"));
                //holder.childrenselect.setVisibility(View.GONE);

                holder.childrenselect.setVisibility(View.VISIBLE);
                holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                isSelected = false;
            }
        } else {
            //if pickbutton button is false
            if (pickuplist.get(position).getIsBoarding() == true) {

                holder.mNotBoardingtext.setVisibility(View.GONE);

                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                holder.mStudentname.setTextColor(Color.parseColor("#333333"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#333333"));*/
                holder.mStudentClass.setTextColor(Color.parseColor("#333333"));
                holder.mStudentRollno.setTextColor(Color.parseColor("#333333"));


            } else {
                holder.mNotBoardingtext.setVisibility(View.VISIBLE);

                convertView.setBackgroundColor(Color.parseColor("#f0f0f0"));
                holder.mStudentname.setTextColor(Color.parseColor("#666666"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#666666"));*/
                holder.mStudentClass.setTextColor(Color.parseColor("#666666"));
                holder.mStudentRollno.setTextColor(Color.parseColor("#666666"));

                holder.childrenselect.setVisibility(View.GONE);


            }


        }


     /*   if (PickupPoint.buttonClicked) {
||||||| .r117
        if (PickupPoint.buttonClicked) {
=======
        if (DashBoardActivityNew.buttonClicked) {
>>>>>>> .r122
            if (!(pickuplist.get(position).getIsBoarding())) {
                if (isSelected) {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    isSelected = false;
                } else {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                    isSelected = true;
                }
                //isSelected=true;
                holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                isSelected=false;

            } else {

                if (isSelected) {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    isSelected = false;
                } else {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                    isSelected = true;
                }
                //  holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
            }
        }
*/





       //On click of backmap button show the notify next actvity directly

       if(DashBoardActivityNew.clickMapButton){



                holder.mNotBoardingtext.setVisibility(View.GONE);
                if (pickuplist.get(position).getIsBoarding() == true) {

                    convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.mStudentname.setTextColor(Color.parseColor("#333333"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#333333"));*/
                    holder.mStudentClass.setTextColor(Color.parseColor("#333333"));
                    holder.mStudentRollno.setTextColor(Color.parseColor("#333333"));

                    holder.childrenselect.setVisibility(View.VISIBLE);
                    //holder.select.setVisibility(View.GONE);

                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    isSelected = true;
                    //sets notify_next to unselected image for 2nd


                } else {
                    //holder.mNotBoardingtext.setVisibility(View.GONE);
                    //holder.select.setVisibility(View.GONE);

                    convertView.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    holder.mStudentname.setTextColor(Color.parseColor("#666666"));
          /*  holder.mRollno.setTextColor(Color.parseColor("#666666"));*/
                    holder.mStudentClass.setTextColor(Color.parseColor("#666666"));
                    holder.mStudentRollno.setTextColor(Color.parseColor("#666666"));
                    //holder.childrenselect.setVisibility(View.GONE);

                    holder.childrenselect.setVisibility(View.VISIBLE);
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                    isSelected = false;
                }
            }

        if (DashBoardActivityNew.clickMapButton) {
            if (!(pickuplist.get(position).getIsBoarding())) {
                if (isSelected) {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    isSelected = false;
                } else {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                    isSelected = true;
                }


            } else {

                if (isSelected) {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_selected);
                    isSelected = false;
                } else {
                    holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
                    isSelected = true;
                }
                //  holder.childrenselect.setImageResource(R.drawable.children_icon_unselected);
            }
        }

        return convertView;
    }

    public class Holder {
        CircleImageView student_pic;
        TextView mStudentname, mNotBoardingtext, mStudentClass, mStudentRollno;
        ImageView childrenselect;
        ProgressBar progressBar;
    }


}
