package br.com.vinyanalista.portugol.android.util;

import android.util.Log;

import br.com.vinyanalista.portugol.android.BuildConfig;

public class S {
    private static final String APP_TAG = "PortugolOnline ";

    // https://stackoverflow.com/a/41590629/1657502
    // https://stackoverflow.com/a/2690339/1657502
    public static void l(Object context, Object object) {
        if (BuildConfig.DEBUG) {
            Log.d(APP_TAG + context.getClass().getSimpleName(), object.toString());
        }
    }
}
