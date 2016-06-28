package peersim.pastry;



import java.math.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;


public class DFS implements Cloneable, EDProtocol {

    public static interface Listener {

        public void receive(Message m);
    }

    private App listener;

    public void setListener(App l) {
    	listener = l;
    }



	private static final String PAR_TRANSPORT = "transport";

	protected MSPastryProtocol routeLayer;
	private static String prefix;
	private int pid;
	private int tid;
	//Array list de destinos de mi cancion
	private ArrayList<BigInteger> dests = new ArrayList<BigInteger>();
	// ArrayList para mantener las particiones con la info
	private ArrayList<Bloque> particiones = new ArrayList<Bloque>();
	private ArrayList<String> bloques = new ArrayList<String>();

	public DFS(String prefix){
		DFS.prefix = prefix;
		this.pid = Configuration.lookupPid(prefix.substring(prefix.lastIndexOf('.') + 1));
		this.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		this.routeLayer = ((MSPastryProtocol) CommonState.getNode().getProtocol(tid));
		this.routeLayer.setMyApp(this);
	}
	public void setMyApp(App l) {
        listener = l;
    }
	public void receive(Object event){		//RECIBIMOS DEL DHT
		Message m = (Message) event;
		// System.out.println("Soy DFS y nombre es "+m.body.toString());
		// ArrayList<String> bloques = new ArrayList<String>();
		// ArrayList<Bloque> particiones = new ArrayList<Bloque>();
		bloques=Chunkeador.cortarCancion((String)m.body);
		// Bloque bloqueActual = new Bloque(); // temporal
		for(int i=0;i<bloques.size();i++) {
			Bloque bloqueActual = new Bloque(); // temporal para guardar los datos
 			// Message q = Message.makeQuery(bloques.get(i));
 			bloqueActual.setNombreCancion(m.body.toString());
 			bloqueActual.setSecuenciaBloque(i+1);
 			bloqueActual.setParticion(bloques.get(i));
 			particiones.add(bloqueActual); // Agrego al listado el objeto particion actual
			// try{
			// 	q.dest = HashSHA.applyHash(bloques.get(i));
			// 	dests.add(q.dest);
			// }catch (UnsupportedEncodingException e) {
			// 	e.printStackTrace();
			// }
			// System.out.println(bloqueActual.getNombreCancion());
			// System.out.println(bloqueActual.getSecuenciaBloque());
			// System.out.println(bloqueActual.getParticion());
			// this.sendtoDHT(q); //RUTEO A DHT
		}// Fin de for para enviar las particiones

		for(int j=0;j<particiones.size();j++){ // Recorro todos los objetos particion
			Message q = Message.makeQuery(particiones.get(j)); // El mensaje a enviar es el objeto
			try{
				q.dest = HashSHA.applyHash(particiones.get(j).getParticion()); // Hash con los datos para saber quién los tendrá
				dests.add(q.dest); // Agrego a los destinatarios del mensaje
			}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			this.sendtoDHT(q); // Enviar todos los objetos partición a DHT
		}
	}
	public void sendtoDHT(Message m){
		routeLayer.sendDHTLookup(m.dest, m);
	}
	@Override 	
	public void processEvent(Node myNode, int pid, Object event){  // LLEGA DESDE APP.java
		Message m = (Message) event;
		String hash = m.body.toString();
		System.out.println("en DFS"+((MSPastryProtocol) myNode.getProtocol(pid-1)).nodeId);
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
