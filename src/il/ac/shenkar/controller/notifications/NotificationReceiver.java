package il.ac.shenkar.controller.notifications;

import il.ac.shenkar.controller.AddNewTaskActivity;
import il.ac.shenkar.controller.MainTaskListActivity;
import il.ac.shenkar.controller.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

public class NotificationReceiver extends BroadcastReceiver {

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

		Intent intentOpenTaskList = new Intent(context,
				MainTaskListActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intentOpenTaskList, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Builder notificationBuilder = new Notification.Builder(context);

		notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		notificationBuilder.setContentTitle("Task Notifier");
		notificationBuilder.setContentText("Don't forget to do : "
				+ intent.getStringExtra(AddNewTaskActivity.TASK_EXTRA));
		notificationBuilder.setWhen(System.currentTimeMillis());
		notificationBuilder.setContentIntent(pendingIntent);
		notificationBuilder.setLights(Notification.DEFAULT_LIGHTS, 10000, 1000);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setTicker("TODO LIST CALL");
		Uri sound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notificationBuilder.setSound(sound);
		Notification notification = notificationBuilder.build();

		notificationManager.notify(0, notification);

	}
}
