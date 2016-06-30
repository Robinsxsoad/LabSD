package peersim.pastry;



import java.math.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;



public class App implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";

	protected DFS dfsLayer;
	private static String prefix;
	private int pid;
	private int tid;
	BigInteger consulta;

	public App(String prefix){
		App.prefix = prefix;
		this.pid = Configuration.lookupPid(prefix.substring(prefix.lastIndexOf('.') + 1));
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		this.dfsLayer = ((DFS) CommonState.getNode().getProtocol(tid));
		this.dfsLayer.setMyApp(this);
	}
	public void receive(Object event){		//RECIBIMOS DEL DFS
		Message m = (Message) event;
		System.out.println("POP UP DE CANCIÓN RECIBIDA en nodo:"+dfsLayer.routeLayer.nodeId );
		System.out.println("Reproduzco canción");
	}

	@Override 	
	public void processEvent(Node myNode, int pid, Object event){  // LLEGA DESDE Vista.java
		Message m = (Message) event;
		Node nodo=myNode;
		m.src=((MSPastryProtocol) myNode.getProtocol(pid-2)).nodeId;
		System.out.println(((MSPastryProtocol) myNode.getProtocol(pid-2)).nodeId);
		EDSimulator.add(0, m, nodo, tid);
	}

	public Object clone() {
		App clon = new App(App.prefix);

		return clon;
	}


    


} // End of class
