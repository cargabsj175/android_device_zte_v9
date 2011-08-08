package com.cyanogenmod.V9Parts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class V9PartsStartup extends BroadcastReceiver
{
   private boolean hasdata = false;
   private int xoffset, yoffset, xscale, yscale;

   private void loadCalData() {
      try {
         FileInputStream fis = new FileInputStream("/data/system/pointercal");
         BufferedReader buf = new BufferedReader(new InputStreamReader(fis));
	 String readString = new String();
	 if ((readString = buf.readLine()) != null) {
            // Split string and parse values
            StringTokenizer st = new StringTokenizer(readString, " ");
	    xscale = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    int temp = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    xoffset = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    temp = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    yscale = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    yoffset = Integer.parseInt(st.nextToken());
	    if(!st.hasMoreTokens()) return;
	    temp = Integer.parseInt(st.nextToken().trim());
            hasdata = true;
         }
         fis.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void writeValue(String parameter, int value) {
      try {
          FileOutputStream fos = new FileOutputStream(new File("/sys/module/msm_ts/parameters/tscal_" + parameter));
          fos.write(String.valueOf(value).getBytes());
          fos.flush();
          fos.getFD().sync();
          fos.close();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onReceive(final Context context, final Intent bootintent) {
      // Touchscreen calibration
      loadCalData();
      if(hasdata) {
         writeValue("xoffset",xoffset);
         writeValue("yoffset",yoffset);
         writeValue("xscale",xscale);
         writeValue("yscale",yscale);
      }
      // Gesture emulation
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
      writeValue("gesture_pressure", Integer.parseInt(prefs.getString("gesture_pressure", "1200")));
      writeValue("gesture_blindspot", Integer.parseInt(prefs.getString("gesture_blindspot", "100")));
   }
}
