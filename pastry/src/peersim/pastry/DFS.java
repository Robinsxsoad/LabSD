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
<<<<<<< HEAD
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
=======

	// Función que corta el archivo
	public static void cortarCancion(File f) throws IOException {
        int numeroParte = 1;
        int tamParticion = 1024 * 1024;// 1MB // Tamaño de las particiones
        byte[] buffer = new byte[tamParticion];

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f))) {//try-with-resources to ensure closing stream
            String nombre = f.getName();

            int temp = 0;
            while ((temp = bis.read(buffer)) > 0) {
                File parte = new File(f.getParent(), nombre + "."
                        + String.format("%03d", partCounter++));
                try (FileOutputStream out = new FileOutputStream(parte)) {
                    out.write(buffer, 0, temp);
                }
            }
        }
    }
    
    public static void unirCancion(List<File> partes, File cancion)
        throws IOException {
    try (BufferedOutputStream uniones = new BufferedOutputStream(
            new FileOutputStream(cancion))) {
        for (File f : partes) {
            partes.copy(f.toPath(), uniones);
        }
    }
}
    

//    public static void main(String[] args) throws IOException {
//        splitFile(new File("D:\\destination\\myFile.mp3"));
//    }

>>>>>>> 2ec368b03e222aade0cc00797490278077c8d24a
} // End of class
