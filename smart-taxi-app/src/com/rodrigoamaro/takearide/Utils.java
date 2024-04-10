
package com.rodrigoamaro.takearide;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    static final void LogError(Context ctx, String tag, Exception e) {
        String txt = ctx.getClass().toString() + " " + e.toString() + " " + e.getMessage();
        Log.e(tag, txt);
        Log.e("test", "lets get the toast roar!");
        Long l = new Long("1");
        Toast.makeText(ctx, txt, Toast.LENGTH_LONG).show();
    }
}
