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
	private ArrayList<String> canciones=new ArrayList<String>();

	public App(String prefix){
		App.prefix = prefix;
		this.pid = Configuration.lookupPid(prefix.substring(prefix.lastIndexOf('.') + 1));
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		this.dfsLayer = ((DFS) CommonState.getNode().getProtocol(tid));
		this.dfsLayer.setMyApp(this);
	}
	public void receive(Object event){		//RECIBIMOS DEL DFS
		Message m = (Message) event;
		String cancion = m.body.toString();
		if(canciones.contains(cancion)==false){
			canciones.add(cancion);
			System.out.println(canciones.size());
			System.out.println("Guardo canción");
			try{//recibe la canción y la guarda como archivo
			FileOutputStream fileOuputStream = new FileOutputStream("reproducida.mp3");
			byte[] b = cancion.getBytes();
			fileOuputStream.write(b);
			fileOuputStream.close();
			}catch(Exception e){
			System.out.println("Problema al reproducir");
			}
		}
	}

	@Override 	
	public void processEvent(Node myNode, int pid, Object event){  // LLEGA DESDE Vista.java
		Message m = (Message) event;
		Node nodo=myNode;
		//Recive mensaje desde la interfaz gráfica e indica el nodo fuente
		m.src=((MSPastryProtocol) myNode.getProtocol(pid-2)).nodeId;
		System.out.println("Consulta capa App en Nodo: "+dfsLayer.routeLayer.nodeId);
		EDSimulator.add(0, m, nodo, tid);//lo envia al DFS del nodo
	}

	public Object clone() {
		App clon = new App(App.prefix);

		return clon;
	}


    


} // End of class
