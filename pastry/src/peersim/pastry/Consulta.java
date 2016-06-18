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
    	Message m = Message.makeQuery("Mensaje 1");
    	m.timestamp = CommonState.getTime();

    	if (CommonState.r.nextInt(100) < 100){
        	m.dest = new BigInteger(MSPastryCommonConfig.BITS, CommonState.r);
        }
        else{
        	m.dest = ((MSPastryProtocol) (Network.get(CommonState.r.nextInt(
                     Network.size())).getProtocol(pid))).nodeId;
        }

        //System.out.println("[ Consultar ]\t[ Id = "+consulta.getID()+", Consulta = "+m.body+" ]");
        return m;

    }


    public boolean execute(){
    	
    	consulta = Network.get(CommonState.r.nextInt(Network.size()));
        
    	EDSimulator.add(0, consultaMensaje(), consulta, pid);
    	return false;
    } 

}