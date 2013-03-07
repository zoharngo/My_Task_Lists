package il.org.shenkar.controller.outmessaging.events;

import il.org.shenkar.model.Task;

public class ASyncTaskSucceedEvent {
	
	public Task task;

	public ASyncTaskSucceedEvent(Task task) {
		super();
		this.task = task;
	}
	
}
