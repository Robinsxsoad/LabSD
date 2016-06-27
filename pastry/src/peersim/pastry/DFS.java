package peersim.pastry;



import java.math.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;


public class DFS implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_SONG = "song";

	protected MSPastryProtocol routeLayer;
	private static String prefix;
	private String song;
	private int pid;
	private int tid;
	//Array list de destinos de mi cancion
	private ArrayList<BigInteger> dests = new ArrayList<BigInteger>();

	public DFS(String prefix){
		DFS.prefix = prefix;
		this.pid = Configuration.lookupPid(prefix.substring(prefix.lastIndexOf('.') + 1));
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		this.routeLayer = ((MSPastryProtocol) CommonState.getNode().getProtocol(tid));
		this.routeLayer.setMyApp(this);
	}
	public void receive(Object event){		//RECIBIMOS DEL DHT
		Message m = (Message) event;
		ArrayList<String> bloques = new ArrayList<String>();
		bloques=Chunkeador.cortarCancion((String)m.body);
		for(int i=0;i<bloques.size();i++) {
 			Message q = Message.makeQuery(bloques.get(i));
			try{
				q.dest = HashSHA.applyHash(bloques.get(i));
				dests.add(q.dest);
			}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
				this.sendtoDHT(q); //RUTEO A DHT
		}
	}
	public void sendtoDHT(Message m){
		routeLayer.sendDHTLookup(m.dest, m);
	}
	@Override 	
	public void processEvent(Node myNode, int pid, Object event){  // LLEGA DESDE CONSULTA.java
		Message m = (Message) event;
		String hash = m.body.toString();
		try{
			m.dest = HashSHA.applyHash(hash);
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		this.sendtoDHT(m); //RUTEO A DHT
	}

	public Object clone() {
		DFS clon = new DFS(DFS.prefix);

		return clon;
	}


    


} // End of class
