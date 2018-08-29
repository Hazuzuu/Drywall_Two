package sapphyx.gsd.com.drywall.util;

import android.os.HandlerThread;
import android.os.Looper;

public class AppModel {

    static final HandlerThread sWorkerThread = new HandlerThread("app-loader");
    static {
        sWorkerThread.start();
    }

    public static Looper getWorkerLooper() {
        return sWorkerThread.getLooper();
    }
}
