package il.ac.shenkar.controller.outmessaging.events;

public class ASyncNewUserFailedEvent {
	public String ex;

	public ASyncNewUserFailedEvent(String ex) {
		super();
		this.ex = ex;
	}
}
