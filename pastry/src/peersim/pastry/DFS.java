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

		switch (m.messageType) {
        case Message.MSG_LOOKUP:
        	System.out.println("Soy DHT y nombre es "+m.body.toString());
			ArrayList<String> bloques = new ArrayList<String>();
			ArrayList<Bloque> particiones = new ArrayList<Bloque>();
			bloques=Chunkeador.cortarCancion((String)m.body);
			Bloque bloqueActual = new Bloque(); // temporal
			for(int i=0;i<bloques.size();i++) {
	 			Message q = Message.makeQuery(bloques.get(i));
	 			bloqueActual.setNombreCancion(m.body.toString());
	 			bloqueActual.setSecuenciaBloque(i+1);
	 			bloqueActual.setParticion(bloques.get(i));
	 			particiones.add(bloqueActual);
	 			// particiones.get(i).setNombreCancion(m.body.toString());
	 			// particiones.get(i).setSecuenciaBloque(i);
	 			// particiones.get(i).setParticion(bloques.get(i));
				try{
					q.dest = HashSHA.applyHash(bloques.get(i));
					dests.add(q.dest);
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.out.println(bloqueActual.getNombreCancion());
				System.out.println(bloqueActual.getSecuenciaBloque());
				// System.out.println(bloqueActual.getParticion());
				this.sendtoDHT(q); //RUTEO A DHT
			}// Fin de for para enviar las particiones
            break;

        //Caso para buscar una canción almacenada
        case Message.MSG_SEARCH:
        	//UNIR PIEZAS Y ENVIAR
            break;
        }


	}
	public void sendtoDHT(Message m){
		routeLayer.sendDHTLookup(m.dest, m);
	}
	@Override 	
	public void processEvent(Node myNode, int pid, Object event){
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
