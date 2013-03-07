package il.org.shenkar.controller.outmessaging.events;


public class ASyncTaskFailedEvent {
	
	public String e;

	public ASyncTaskFailedEvent(String e) {
		super();
		this.e = e;
	}
}
