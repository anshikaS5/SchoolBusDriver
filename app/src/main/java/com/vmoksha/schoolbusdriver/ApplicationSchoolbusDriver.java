package com.vmoksha.schoolbusdriver;

import android.app.Application;

import com.vmoksha.schoolbusdriver.util.FontsOverride;

/**
 * Created by anshikas on 20-01-2016.
 */
public class ApplicationSchoolbusDriver extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
       // setCustomFonts();
    }

    /**
     * Replaces application default font with custom font.
     */
   /* private void setCustomFonts() {
        FontsOverride.setDefaultFont(this, "MONOSPACE", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "Lato-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF","Lato-Bold.ttf");
    }*/
}
