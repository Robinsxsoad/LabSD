package peersim.pastry;

import peersim.core.*;
import peersim.config.Configuration;
import peersim.edsim.EDSimulator;
import java.math.*;
import java.util.*;

/**
 * This "test" control generates random traffic between random nodes (source and destination).
 * It was created for test and statistical analysis purpose
 * <p>Title: MSPASTRY</p>
 *
 * <p>Description: MsPastry implementation for PeerSim</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: The Pastry Group</p>
 *
 * @author Elisa Bisoffi, Manuel Cortella
 * @version 1.0
 */


//______________________________________________________________________________________________
public class TrafficGenerator implements Control {

    ArrayList<String> list = new ArrayList<String>();
    //______________________________________________________________________________________________
    /**
     * MSPastry Protocol to act
     */
    private final static String PAR_PROT = "protocol";


    /**
     * MSPastry Protocol ID to act
     */
    private final int pid;


    //______________________________________________________________________________________________
    public TrafficGenerator(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);

    }

    //______________________________________________________________________________________________
    /**
     * generates a random lookup message, by selecting randomly the destination.
     * @return Message
     */
    private Message generateLookupMessage() {
            
            //Aca se generan los mensajes a nivel de applicacion
            list.add("Mensaje 1");
            list.add("Mensaje 2");
            list.add("Mensaje 3");
            list.add("Mensaje 4");
            list.add("Mensaje 5");
            list.add("Mensaje 6");
            list.add("Mensaje 7");
            list.add("Mensaje 8");
            list.add("Mensaje 9");
            list.add("Mensaje 10");

            Random randomizer = new Random();

            String random = list.get(randomizer.nextInt(list.size()));

            Message m = Message.makeLookUp(random);
            m.timestamp = CommonState.getTime();

            if (CommonState.r.nextInt(100) < 100)
                m.dest = new BigInteger(MSPastryCommonConfig.BITS, CommonState.r);
             else
                m.dest = ((MSPastryProtocol) (Network.get(CommonState.r.nextInt(
                        Network.size())).getProtocol(pid))).nodeId;

            String s = String.format("\n[ Insertar ]\t[ Cuerpo Mensaje = %s | Id Mensaje = %d]",m.body,m.id);
            System.err.println(s);

            return m;

        }

    //______________________________________________________________________________________________
    /**
     * every call of this control generates and send a random lookup message
     * @return boolean
     */
    public boolean execute() {

        Node start;

        do{
            start = Network.get(CommonState.r.nextInt(Network.size()));
        } while (( start==null)||(!start.isUp())) ;

        EDSimulator.add(0, generateLookupMessage(), start, pid);
        return false;
    }

    //______________________________________________________________________________________________


} // End of class
//______________________________________________________________________________________________
