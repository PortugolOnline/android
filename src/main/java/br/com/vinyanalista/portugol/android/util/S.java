package br.com.vinyanalista.portugol.android.util;

import android.content.Context;
import android.util.Log;

import br.com.vinyanalista.portugol.android.BuildConfig;

public class S {
    private static final String APP_TAG = "PortugolOnline ";

    // https://stackoverflow.com/a/41590629/1657502
    public static void l(Context context, Object object) {
        if (BuildConfig.DEBUG) {
            Log.d(APP_TAG + context.getClass().getName().replace(context.getPackageName() + ".", ""), object.toString());
        }
    }
}
