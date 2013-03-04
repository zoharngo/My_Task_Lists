package il.ac.shenkar.controller;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AddressDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View list_view = inflater.inflate(R.layout.addresss_result, null);
		builder.setView(list_view);
		ListView listAddress = (ListView) list_view
				.findViewById(R.id.list_address);
		Bundle bundle = getArguments();
		ArrayList<String> addresss = bundle
				.getStringArrayList(AddNewTaskActivity.FIND_ADDRESSS);	
 		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, addresss);
		listAddress.setAdapter(adapter);
		listAddress.setOnItemClickListener((OnItemClickListener) getActivity());

		builder.setTitle(R.string.address_title);
		return builder.create();

	}

}
