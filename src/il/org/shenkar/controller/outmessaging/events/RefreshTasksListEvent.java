package il.org.shenkar.controller.outmessaging.events;

import il.org.shenkar.model.Task;

import java.util.ArrayList;

public class RefreshTasksListEvent {
	public ArrayList<Task> tasks;

	public RefreshTasksListEvent(ArrayList<Task> tasks) {
		super();
		this.tasks = tasks;
	}

}
