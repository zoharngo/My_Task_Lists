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


public class AsyncAddressSearch extends AsyncTask<String, Void, Void> {

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
			bus.post(new AsyncFindAddressFaildEvent(e.toString()));
			e.printStackTrace();
		}
		if (addresses != null) {
			ArrayList<String> addressTextArr = new ArrayList<String>();
			for (int i = 0; i < addresses.size(); ++i) {
				Address address = addresses.get(i);

				String line = String.format(
						Locale.ENGLISH,
						"%s,%s,%s:%f,%f",
						address.getCountryName() != null ? address
								.getCountryName() : "",
						address.getLocality() != null ? address.getLocality()
								: "",
						address.getAddressLine(0) != null ? address
								.getAddressLine(0) : "no result!.", address
								.getLatitude(), address.getLongitude());
				addressTextArr.add(line);

			}
			bus.post(new AsyncFindAddressSucceedEvent(addressTextArr));
		}
		return null;
	}
}
