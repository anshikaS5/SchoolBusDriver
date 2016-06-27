package com.vmoksha.schoolbusdriver.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.vmoksha.schoolbusdriver.R;
import com.vmoksha.schoolbusdriver.activity.ChangePassword;
import com.vmoksha.schoolbusdriver.activity.LoginActivity;
import com.vmoksha.schoolbusdriver.model.ModelClass;
import com.vmoksha.schoolbusdriver.util.CircleImageView;

/**
 * Created by AbakashaP on 21-01-2016.
 */
public class DrawerFragment extends Fragment {
    private Context con;

    public static DrawerLayout mDrawerLayout;
    /*private View containerView;*/
    public static View containerView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String DriverImage;
   protected ImageLoader imgLoader;
    private DisplayImageOptions options;
    ProgressBar progressBar;
    CircleImageView profile_img;

    public DrawerFragment() {
    }

    private TextView menuPassword, menuLogout, tvDriverName;
    private LinearLayout mchangepwddrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        options = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)

                .build();
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);




        sharedPreferences = getActivity().getSharedPreferences(ModelClass.DriverLoginPreference, Context.MODE_PRIVATE);
        menuPassword = (TextView) layout.findViewById(R.id.menu_ChangePassword);
        menuLogout = (TextView) layout.findViewById(R.id.menu_logout);
        DriverImage=sharedPreferences.getString(ModelClass.DriveProfileUrl, "");
        String FirstName=sharedPreferences.getString(ModelClass.FirstName,"");
        String LastName=sharedPreferences.getString(ModelClass.LastName,"");
        mchangepwddrawer= (LinearLayout) layout.findViewById(R.id.changepwd_drawer);
        tvDriverName = (TextView) layout.findViewById(R.id.tvDriverName);
         profile_img= (CircleImageView) layout.findViewById(R.id.profile_img);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress);
        tvDriverName.setText(FirstName+" "+LastName);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        if (DriverImage.contentEquals("null")||DriverImage.contentEquals("")) {
            profile_img.setImageResource(R.drawable.images);

        } else {
//
            ImageLoader.getInstance().displayImage(DriverImage,profile_img, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    progressBar.setVisibility(View.VISIBLE);
                    profile_img.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    super.onLoadingCancelled(imageUri, view);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    progressBar.setVisibility(View.GONE);
                    profile_img.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
//                    holder.progressBar.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }




        mchangepwddrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(containerView);
                Intent intent;
                intent = new Intent(getActivity(),ChangePassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity(intent);


            }
        });
        menuLogout.setOnClickListener(LogoutFromProfile);



        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

    }

    /*
	 * Set up Navigation drawer with all actions that will be performed on
	 * opening and closing of drawer.
	 */


    public static void closeDrawer() {
        mDrawerLayout.closeDrawer(containerView);
    }

    /*
     * On LogOut button in nav drawer.On Click Clear SharedPreferences.
     */
    View.OnClickListener LogoutFromProfile = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            closeDrawer();
            editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            getActivity().finish();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    };
}
