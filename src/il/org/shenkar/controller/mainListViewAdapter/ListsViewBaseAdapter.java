package il.org.shenkar.controller.mainListViewAdapter;

import il.org.shenkar.controller.R;
import il.org.shenkar.model.Task;
import il.org.shenkar.model.TasksListModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ListsViewBaseAdapter extends BaseAdapter implements Filterable,
		OnClickListener {


	private LayoutInflater l_Inflater;
	private TasksListModel taskListsModel;
	private ArrayList<Task> doneTasks;
	private ArrayList<Integer> itemPosChecked;
	private Context context;

	public ListsViewBaseAdapter(Context context) {
		this.context = context;
		l_Inflater = LayoutInflater.from(context);
		taskListsModel = TasksListModel.getInstance(context);
		doneTasks = new ArrayList<Task>();
		itemPosChecked = new ArrayList<Integer>();
	}

	public int getCount() {
		// Getting Access to the Model collection
		return taskListsModel.getSize();
	}

	public Task getItem(int position) {
		// Getting Access to the Model collection
		return taskListsModel.getTask(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		TaskViewHolder holder = null;
		if (convertView == null) {

			convertView = this.l_Inflater.inflate(R.layout.item_view, null);
			holder = new TaskViewHolder();
			holder.taskContent = (TextView) convertView.findViewById(R.id.task);
			holder.timeStamp = (TextView) convertView
					.findViewById(R.id.time_stamp);
			holder.location = (TextView) convertView
					.findViewById(R.id.location_where);
			holder.when = (TextView) convertView.findViewById(R.id.todo_when);
			holder.done = (CheckBox) convertView.findViewById(R.id.check_done);
			holder.done.setOnClickListener(this);
			convertView.setTag(holder);

		} else {
			holder = (TaskViewHolder) convertView.getTag();

		}
		if ((itemPosChecked.contains(position))) {
			holder.done.setChecked(true);
		} else {
			holder.done.setChecked(false);
		}

		CharSequence timeStamp = DateFormat.format("dd/MM/yy   h:mm:ss",
				getItem(position).getTaskId());

		long todoWenMilliSec = getItem(position).getDate();
		String place;

		StringBuilder todoWhen = new StringBuilder(
				context.getString(R.string.todo_when)).append(" ");

		if (todoWenMilliSec > 0) {

			todoWhen.append(DateFormat.format("E , dd/MM/yy   h:mm",
					getItem(position).getDate()));
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(todoWenMilliSec);
			if(calendar.get(Calendar.AM_PM) == Calendar.AM){
				todoWhen.append(" AM.");
			}else{
				todoWhen.append(" PM.");
			}
			

		} else {
			todoWhen.append(context.getString(R.string.sometime));
		}
		
		if (TextUtils.isEmpty(getItem(position).getLocation())) {
			place = context.getString(R.string.somewhere);
		} else {
			place = getItem(position).getLocation();
		}
		
		StringBuilder location = new StringBuilder(
				context.getString(R.string.location_where)).append(" ")
				.append(place).append(".");

		holder.location.setText(location.toString());
		holder.when.setText(todoWhen.toString());
		holder.timeStamp.setText(timeStamp.toString());
		holder.taskContent.setText(getItem(position).getDescription());
		holder.done.setTag(position);

		return convertView;
	}

	@Override
	public void onClick(View v) {
		CheckBox checkBox = (CheckBox) v;
		Integer position = (Integer) checkBox.getTag();
		
		if (checkBox.isChecked()) {
			doneTasks.add(getItem(position));
			itemPosChecked.add(position);
		} else {
			doneTasks.remove(getItem(position));
			itemPosChecked.remove(position);
		}

	}

	public ArrayList<Task> getDoneTasks() {
		return doneTasks;
	}

	public void clearDoneTask() {
		itemPosChecked.clear();
		doneTasks.clear();
	}

	static class TaskViewHolder {
		TextView taskContent;
		TextView timeStamp;
		TextView location;
		TextView when;
		CheckBox done;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results != null && results.count >= 0) {
					taskListsModel
							.setSearcResult((ArrayList<Task>) results.values);
				} else {
					taskListsModel.removeSearcResult();
				}
				notifyDataSetInvalidated();

			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults result = new FilterResults();
				if (!(TextUtils.isEmpty(constraint))) {
					constraint = constraint.toString().toLowerCase(
							Locale.ENGLISH);
					ArrayList<Task> foundItems = new ArrayList<Task>();
					ArrayList<Task> fullTaskList = taskListsModel.getTaskList();
					if (!(fullTaskList.isEmpty())) {
						for (int i = 0; i < fullTaskList.size(); i++) {
							if (fullTaskList.get(i).getDescription()
									.toLowerCase(Locale.ENGLISH)
									.contains(constraint)) {
								foundItems.add(fullTaskList.get(i));
							}
						}
					}
					result.count = foundItems.size();
					result.values = foundItems;
				} else {
					result.count = -1;
				}
				return result;
			}
		};
		return filter;
	}

}
