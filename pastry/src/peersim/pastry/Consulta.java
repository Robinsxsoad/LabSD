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
		Scanner sc = new Scanner(System.in);
		String mensaje;
		System.out.print("Introduzca el nombre de la canción: ");       
        mensaje = sc.nextLine();
    	Message m = Message.makeLookUp(mensaje);
    	m.timestamp = CommonState.getTime();

    	if (CommonState.r.nextInt(100) < 100){
        	m.dest = new BigInteger(MSPastryCommonConfig.BITS, CommonState.r);
        }
        else{
        	m.dest = ((MSPastryProtocol) (Network.get(CommonState.r.nextInt(
                     Network.size())).getProtocol(pid))).nodeId;
        }
        return m;

    }


    public boolean execute(){
    	
    	consulta = Network.get(CommonState.r.nextInt(Network.size()));
        
    	EDSimulator.add(0, consultaMensaje(), consulta, pid);
    	return false;
    } 

}
