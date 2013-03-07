package il.org.shenkar.controller;

import il.org.shenkar.controller.R;
import il.org.shenkar.controller.application.ApplicationRoot;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogErrorMessage extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Bundle bundle = getArguments();
		String title = bundle
				.getString(ApplicationRoot.EXTRA_TITLE_ERROR_DIALOG);
		String message = bundle
				.getString(ApplicationRoot.EXTRA_MESSAGE_ERROR_DIALOG);
		builder.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.dialog_positive_btn,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getActivity().finish();
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
