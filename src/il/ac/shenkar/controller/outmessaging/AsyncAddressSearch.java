package il.ac.shenkar.controller.outmessaging;

import il.ac.shenkar.controller.bus.BusProvider;
import il.ac.shenkar.controller.outmessaging.events.AsyncFindAddressFaildEvent;
import il.ac.shenkar.controller.outmessaging.events.AsyncFindAddressSucceedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncAddressSearch extends AsyncTask<String, Void, Void> {
	public static final String TAG = "il.ac.shenkar.controller.outmessaging.AsyncAddressSearch";
	Context context;
	BusProvider bus = BusProvider.getBusProvider();

	public AsyncAddressSearch(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocationName(params[0], 5);
		} catch (IOException e) {
			Log.e(TAG, "IOException while searching address");
			bus.post(new AsyncFindAddressFaildEvent(e.toString()));
			e.printStackTrace();
		}
		if (addresses != null) {
			ArrayList<String> addressTextArr = new ArrayList<String>();
			for (int i = 0; i < addresses.size(); ++i) {
				Address address = addresses.get(i);

				String line = String.format(
						"%s,%s,%s",
						address.getCountryName() != null ? address
								.getCountryName() : "",
						address.getLocality() != null ? address.getLocality()
								: "",
						address.getAddressLine(0) != null ? address
								.getAddressLine(0) : "no result!.");
				addressTextArr.add(line);

			}
			bus.post(new AsyncFindAddressSucceedEvent(addressTextArr));
		}
		return null;
	}
}