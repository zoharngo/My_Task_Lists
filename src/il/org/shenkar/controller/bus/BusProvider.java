package il.org.shenkar.controller.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;


public final class BusProvider extends Bus {
	private static BusProvider busProvider = new BusProvider();

	private BusProvider() {
		super(ThreadEnforcer.ANY);
	}

	public static BusProvider getBusProvider() {
		return busProvider;
	}

	private final Handler mainThread = new Handler(Looper.getMainLooper());

	@Override
	public void post(final Object event) {

		if (Looper.myLooper() == Looper.getMainLooper()) {
			super.post(event);
		} else {
			mainThread.post(new Runnable() {
				public void run() {
					post(event);
				}
			});
		}

		
	}
}
