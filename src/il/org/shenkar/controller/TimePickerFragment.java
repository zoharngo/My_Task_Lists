package il.org.shenkar.controller;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
				(OnTimeSetListener) getActivity(), hour, minute,
				DateFormat.is24HourFormat(getActivity()));

		return timePickerDialog;
	}

}
