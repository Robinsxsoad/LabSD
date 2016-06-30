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
	private boolean enviadoFinal =false;
	//Array list de destinos de mi cancion
	private ArrayList<Catalogo> catalogos = new ArrayList<Catalogo>();
	private ArrayList<Bloque> bloquesEntrantes = new ArrayList<Bloque>();
	private BigInteger solicitud;
	

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

    private void deliver(Message m) {
        //statistiche utili all'observer
        MSPastryObserver.hopStore.add(m.nrHops-1);
        long timeInterval = (CommonState.getTime())-(m.timestamp);
        MSPastryObserver.timeStore.add(timeInterval);

        if (listener != null) {//se envia archivo recuperado a la app
           listener.receive(m);
        }

    }


	public void receive(Object event){		//RECIBIMOS DEL DHT
		Message m = (Message) event; // m.body está el nombre de la canción al venir del DHT primera parte
		ArrayList<String> bloques = new ArrayList<String>();
		ArrayList<Bloque> bloquesEnviar = new ArrayList<Bloque>();
		switch (m.messageType) {
        case Message.MSG_LOOKUP:
			bloques=Chunkeador.cortarCancion((String)m.body);
			Catalogo entrada = new Catalogo();
			entrada.setNombreCancion(m.body.toString());
			for(int i=0;i<bloques.size();i++) {
	 			try{
	 				entrada.getEncargados().add(HashSHA.applyHash(bloques.get(i)));
	 			}catch(UnsupportedEncodingException e){
	 				e.printStackTrace();
	 			}


	 			Bloque bloqueActual = new Bloque(); // temporal para guardar los datos
	 			bloqueActual.setNombreCancion(m.body.toString());
	 			bloqueActual.setSecuenciaBloque(i);
	 			bloqueActual.setParticion(bloques.get(i));

	 			Message q = Message.makeQuery(bloqueActual); // El mensaje a enviar es el trozo de canción
				try{
					q.dest = HashSHA.applyHash(bloques.get(i)); // Hash con los datos para saber quién los tendrá
				}catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				this.sendtoDHT(q); // Enviar todos los objetos partición a DHT

			} // FIN FOR
			catalogos.add(entrada);
            break;

        //Caso para buscar una canción almacenada
        case Message.MSG_SEARCH:
        	solicitud=m.src;
        	//saber id del que debe responderle
        	for(int i=0;i<catalogos.size();i++) {
        		if(catalogos.get(i).getNombreCancion().equals((String)m.body)){
        			for(int j=0;j<catalogos.get(i).getEncargados().size();j++){
        				Message request=Message.makeRequest((String)m.body);
        				request.src=routeLayer.nodeId;
        				request.dest= catalogos.get(i).getEncargados().get(j);
        				System.out.println("Encargado "+j+": "+request.dest);
        				this.sendtoDHT(request);
        			}
        		}
        	}

            break;
        case Message.MSG_RESPUESTA:
        	if(!bloquesEntrantes.contains((Bloque)m.body)){
        		bloquesEntrantes.add((Bloque)m.body);
        	}
        	ArrayList<String> partes = new ArrayList<String>();
        	for(int i=0;i<catalogos.size();i++) {
        		if(catalogos.get(i).getNombreCancion().equals(((Bloque)m.body).getNombreCancion())){
        			if(bloquesEntrantes.size()==catalogos.get(i).getEncargados().size()){
        				bloquesEntrantes=sort(bloquesEntrantes);
        				for (int j=0;j<bloquesEntrantes.size() ;j++ ) {
        					partes.add(bloquesEntrantes.get(j).getParticion());
        				}
        				String cancion =Chunkeador.unirCancion(partes);
        				Message q =Message.makeFinal(cancion);
        				q.dest=solicitud;
        				this.sendtoDHT(q);
        				bloquesEntrantes.clear();
        			}
        		}
        	}
        	break;
        case Message.MSG_FINAL:
        	deliver(m);
        } // Fin de switch

	}
	private ArrayList<Bloque> sort(ArrayList<Bloque> blocks){
		ArrayList<Bloque> sorted=blocks;
		for (int i=1; i<bloquesEntrantes.size(); i++){
		 	for(int j=0 ; j<bloquesEntrantes.size() - 1; j++){
			 	if (bloquesEntrantes.get(j).getSecuenciaBloque() > bloquesEntrantes.get(j+1).getSecuenciaBloque()){
				 	Bloque temp = bloquesEntrantes.get(j);
				 	bloquesEntrantes.set(j, bloquesEntrantes.get(j+1));
				 	bloquesEntrantes.set(j+1,temp);
				}
		 	}
		}
		return sorted;
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
