package il.ac.shenkar.controller.outmessaging.events;

public class ASyncNewUserSucceedEvent {
	public String result;

	public ASyncNewUserSucceedEvent(String result) {
		super();
		this.result = result;
	}
}
