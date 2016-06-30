package peersim.pastry;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.edsim.EDSimulator;
import java.math.*;
import java.util.*;


public class Consulta implements Control{

    private final static String PAR_PROT = "protocol";
    private final int pid;
    private String prefix;
    Node consulta;

    public Consulta(String prefix) {
        this.prefix = prefix;
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    private Message consultaMensaje(){
        System.out.println("Ingrese una opción");
        System.out.println("1. Ingresar canción");
        System.out.println("2. Busca canción");
		Scanner sc = new Scanner(System.in);
        int opcion = sc.nextInt();
        String nombreCancion;
        Message m = new Message();
        switch(opcion){
            case 1:{
                sc.nextLine();
                System.out.println("Ingrese el nombre de la canción para agregar:");
                nombreCancion = sc.nextLine();
                m = Message.makeLookUp(nombreCancion);
                m.timestamp = CommonState.getTime();
                if (CommonState.r.nextInt(100) < 100){
                    m.dest = new BigInteger(MSPastryCommonConfig.BITS, CommonState.r);
                }
                else{
                    m.dest = ((MSPastryProtocol) (Network.get(CommonState.r.nextInt(
                         Network.size())).getProtocol(pid))).nodeId;
                }
                Node consulta = Network.get(CommonState.r.nextInt(Network.size()));
                System.out.println(m.body);
                EDSimulator.add(0, m, consulta, 5);
                // Código de botón insertar
                break;
            }
            case 2:{
                sc.nextLine();
                System.out.println("Ingrese el nombre de la canción: para buscar");
                nombreCancion = sc.nextLine();
                m = Message.makeSearch(nombreCancion);
                m.timestamp = CommonState.getTime();
                if (CommonState.r.nextInt(100) < 100){
                m.dest = new BigInteger(MSPastryCommonConfig.BITS, CommonState.r);
                }
                else{
                    m.dest = ((MSPastryProtocol) (Network.get(CommonState.r.nextInt(
                         Network.size())).getProtocol(pid))).nodeId;
                }
                Node consulta = Network.get(CommonState.r.nextInt(Network.size()));
                EDSimulator.add(0, m, consulta, 5);
                break;
            }
        }
        return m;
    }


    public boolean execute(){
    	
    	consulta = Network.get(CommonState.r.nextInt(Network.size()));
    	EDSimulator.add(0, consultaMensaje(), consulta, pid);
    	return false;
    } 

}
