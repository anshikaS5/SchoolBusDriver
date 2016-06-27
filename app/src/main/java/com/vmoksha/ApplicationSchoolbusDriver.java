package com.vmoksha;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.vmoksha.schoolbusdriver.util.FontsOverride;

/**
 * Created by anshikas on 20-01-2016.
 */
public class ApplicationSchoolbusDriver extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        setCustomFonts();
        initImageLoader(getApplicationContext());
    }

    /**
     * Replaces application default font with custom font.
     */
    private void setCustomFonts() {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "Lato-Bold.ttf");
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
