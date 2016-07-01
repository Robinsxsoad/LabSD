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
        case Message.MSG_LOOKUP://se corta la cancion y se envia a los nodos encargados
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
				if(!repetida((String)m.body,catalogos)){
					this.sendtoDHT(q); // Enviar todos los objetos partición a DHT					
				}

			} // FIN FOR
			if(!repetida((String)m.body,catalogos)){
				catalogos.add(entrada);
			}else{
				System.out.println("Esta canción ya ha sido ingresada");
			}
            break;

        //Caso para buscar una canción almacenada
        case Message.MSG_SEARCH:
        	solicitud=m.src;
        	//saber id del que debe responderle
        	for(int i=0;i<catalogos.size();i++) {//se solicitan fragmentos a los nodos
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
        case Message.MSG_RESPUESTA://se procesa la respuesta de los nodos cuando llegan todos los fragmentos
        	if(!bloquesEntrantes.contains((Bloque)m.body)){//evitar duplicados
        		bloquesEntrantes.add((Bloque)m.body);
        	}
        	ArrayList<String> partes = new ArrayList<String>();
        	for(int i=0;i<catalogos.size();i++) {
        		if(catalogos.get(i).getNombreCancion().equals(((Bloque)m.body).getNombreCancion())){//busco nombre
        			if(bloquesEntrantes.size()==catalogos.get(i).getEncargados().size()){//verifico el toral
        				bloquesEntrantes=sort(bloquesEntrantes);
        				for (int j=0;j<bloquesEntrantes.size() ;j++ ) {
        					partes.add(bloquesEntrantes.get(j).getParticion());
        				}
        				String cancion =Chunkeador.unirCancion(partes);
        				Message q =Message.makeFinal(cancion);
        				q.dest=solicitud;
        				this.sendtoDHT(q);
        				bloquesEntrantes.clear();//se limpian los bloques entrantes
        			}
        		}
        	}
        	break;
        case Message.MSG_FINAL://se envia respuesta a capa App con cancion recuperada
        	deliver(m);
        } // Fin de switch

	}
	private ArrayList<Bloque> sort(ArrayList<Bloque> blocks){//ordenamiento de los bloques por secuencia
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
	private boolean repetida(String nombre,ArrayList<Catalogo> catalogos){
		String name =nombre;
		ArrayList<Catalogo> blocks = catalogos;
		for (int i=0;i<blocks.size() ;i++ ) {
			if(name.equals(blocks.get(i).getNombreCancion())){
				return true;
			}
		}
		return false;
	}
	public void sendtoDHT(Message m){
		System.out.println("Mensaje enviado a Pastry nodo :"+routeLayer.nodeId);
		routeLayer.sendDHTLookup(m.dest, m);
	}
	@Override 	
	public void processEvent(Node myNode, int pid, Object event){//procesa eventos que llegan desde la capa de App
		Message m = (Message) event;
		String hash = m.body.toString();
		System.out.println("Mensaje recibido en DFS del Nodo: "+((MSPastryProtocol) myNode.getProtocol(pid-1)).nodeId);
		try{
				m.dest = HashSHA.applyHash(hash);//busca destino
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
