package peersim.pastry;



import java.math.*;
import java.io.UnsupportedEncodingException;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.util.Comparator;
import java.util.ArrayList;


public class DFS implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_SONG = "song";

	protected MSPastryProtocol routeLayer;
	private static String prefix;
	private String song;
	private int pid;
	private int tid;

	public DFS(String prefix){
		DFS.prefix = prefix;
		this.pid = Configuration.lookupPid(prefix.substring(prefix.lastIndexOf('.') + 1));
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	//	this.song = Configuration.getSting(prefix + "." + PAR_SONG);
		this.routeLayer = ((MSPastryProtocol) CommonState.getNode().getProtocol(tid));
		this.routeLayer.setMyApp(this);
	}
	public void receive(Object event){		//RECIVIMOS DEL DHT
		Message m = (Message) event;
		String mensaje = m.body.toString();
		System.out.println(mensaje);

	}
	public void sendtoDHT(Message m){
		routeLayer.sendDHTLookup(m.dest, m);
	}
	@Override 	
	public void processEvent(Node myNode, int pid, Object event){  // LLEGA DEL GENERADOR DE TRAFICO
		Message m = (Message) event;
		String hash = m.body.toString();
		try{
			m.dest = HashSHA.applyHash(hash);
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.sendtoDHT(m); //RUTEO A DHT
	//	System.out.println(hash);
	}

	public Object clone() {
		DFS clon = new DFS(DFS.prefix);

		return clon;
	}
} // End of class
