package il.ac.shenkar.controller.outmessaging.events;

import il.ac.shenkar.model.Task;

public class ASyncTaskSucceedEvent {
	
	public Task task;

	public ASyncTaskSucceedEvent(Task task) {
		super();
		this.task = task;
	}
	
}
