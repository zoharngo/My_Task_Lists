package il.ac.shenkar.controller.outmessaging.events;


public class ASyncGetUserInfoFailedEvent {
	public String ex;

	public ASyncGetUserInfoFailedEvent(String ex) {
		super();
		this.ex = ex;
	}
	
}
