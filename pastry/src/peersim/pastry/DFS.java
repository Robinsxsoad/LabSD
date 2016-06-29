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
	private ArrayList<Catalogo> catalogo = new ArrayList<Catalogo>();
	

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
		Message m = (Message) event; // m.body está el nombre de la canción al venir del DHT primera parte
		ArrayList<String> bloques = new ArrayList<String>();
		switch (m.messageType) {
        case Message.MSG_LOOKUP:
			bloques=Chunkeador.cortarCancion((String)m.body);
			for(int i=0;i<bloques.size();i++) {
	 			Catalogo entrada = new Catalogo();
	 			entrada.setNombreCancion(m.body.toString());
	 			try{
	 				entrada.setEncargado(HashSHA.applyHash(bloques.get(i)));
	 			}catch(UnsupportedEncodingException e){
	 				e.printStackTrace();
	 			}
	 			catalogo.add(entrada);

	 			Message q = Message.makeQuery(bloques.get(i)); // El mensaje a enviar es el trozo de canción
				q.body = bloques.get(i);
				try{
					q.dest = HashSHA.applyHash(bloques.get(i)); // Hash con los datos para saber quién los tendrá
					dests.add(q.dest); // Agrego a los destinatarios del mensaje
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				this.sendtoDHT(q); // Enviar todos los objetos partición a DHT

			} // FIN FOR

			for (int i=0; i<catalogo.size(); i++ ) {
				System.out.println(catalogo.get(i).getNombreCancion());
				System.out.println(catalogo.get(i).getEncargado());
			}

            break;

        //Caso para buscar una canción almacenada
        case Message.MSG_SEARCH:
        	//UNIR PIEZAS Y ENVIAR
            break;
        } // Fin de switch

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
