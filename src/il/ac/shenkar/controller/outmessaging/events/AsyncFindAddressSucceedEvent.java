package il.ac.shenkar.controller.outmessaging.events;

import java.util.ArrayList;

public class AsyncFindAddressSucceedEvent {
	public ArrayList<String> address;

	public AsyncFindAddressSucceedEvent(ArrayList<String> address) {
		super();
		this.address = address;
	}
}
