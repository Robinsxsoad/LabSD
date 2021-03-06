/**
 * 
 */
package peersim.chord;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import java.math.*;

/**
 * @author Andrea
 * 
 */
public class ChordProtocol implements EDProtocol {

	private static final String PAR_TRANSPORT = "transport";

	private Parameters p;

	private int[] lookupMessage; 

	public int index = 0;

	public Node predecessor;

	public Node[] fingerTable;

	public Node[] successorList;

	public BigInteger chordId;

	public int m;

	public int succLSize;

	public String prefix;

	private int next = 0;

	// campo x debug
	private int currentNode = 0;

	public int varSuccList = 0;

	public int stabilizations = 0;

	public int fails = 0;

	/**
	 * 
	 */
	public ChordProtocol(String prefix) {
		this.prefix = prefix;
		lookupMessage = new int[1];
		lookupMessage[0] = 0;
		p = new Parameters();
		p.tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int,
	 *      java.lang.Object)
	 */
	public void processEvent(Node node, int pid, Object event) {
		// processare le richieste a seconda della routing table del nodo
		p.pid = pid;
		currentNode = node.getIndex();
		if (event.getClass() == LookUpMessage.class) {
			LookUpMessage message = (LookUpMessage) event; //mensaje a enviar
			message.increaseHopCounter(); //incremento los saltos
			BigInteger target = message.getTarget(); //obtengo el objetivo
			Transport t = (Transport) node.getProtocol(p.tid); // protocolo
			Node n = message.getSender(); //obtengo quien envia el mensaje
			if (target == ((ChordProtocol) node.getProtocol(pid)).chordId) { // si el nodo actual es el target
				// mandare mess di tipo final // soy el último
				t.send(node, n, new FinalMessage(message.getHopCounter()), pid);
			}
			if (target != ((ChordProtocol) node.getProtocol(pid)).chordId) { // si no soy el target
				// funzione lookup sulla fingertabable //operaciones de busqueda en la fingertable
				Node dest = find_successor(target); //dest es el sucesor de target
				if (dest.isUp() == false) { // si el dest no esta caido
					do {
						varSuccList = 0; 
						stabilize(node); //estabiliza el nodo actual
						stabilizations++;
						fixFingers(); //arregla la tabla
						dest = find_successor(target); //actualiza el dest
					} while (dest.isUp() == false);
				}
				if (dest.getID() == successorList[0].getID() // el mas cercano a target es mi sucesor mas cercano
						&& (target.compareTo(((ChordProtocol) dest
								.getProtocol(p.pid)).chordId) < 0)){//dest esta cerca del final
				//	if(dest.getID()<(Network.size() - 1)
				//	 && (target.compareTo(((ChordProtocol) Network.get(0)
				//	.getProtocol(pid)).chordId)>0)){
						//si el destino es menor al maximo de la red
						// y el target esta despues del inicio del anillo (posicion 0) envia el mensaje
					//	t.send(message.getSender(), dest, message, pid); //falla igual
				//	}else{	
						fails++;
				//	}
				} else {
					t.send(message.getSender(), dest, message, pid);
				}
			}
		}
		if (event.getClass() == FinalMessage.class) {
			FinalMessage message = (FinalMessage) event;
			lookupMessage = new int[index + 1];
			lookupMessage[index] = message.getHopCounter();
			index++;
		}
	}

	public Object clone() {
		ChordProtocol cp = new ChordProtocol(prefix);
		String val = BigInteger.ZERO.toString();
		cp.chordId = new BigInteger(val);
		cp.fingerTable = new Node[m];
		cp.successorList = new Node[succLSize];
		cp.currentNode = 0;
		return cp;
	}

	public int[] getLookupMessage() {
		return lookupMessage;
	}

	public void stabilize(Node myNode) {
		try {
			Node node = ((ChordProtocol) successorList[0].getProtocol(p.pid)).predecessor;
			if (node != null) {//si hay un nodo entremedio
				if (this.chordId == ((ChordProtocol) node.getProtocol(p.pid)).chordId) //si ese nodo es el actual
					return; //retorno
				BigInteger remoteID = ((ChordProtocol) node.getProtocol(p.pid)).chordId; // el predecesor de mi sucesor
				if (idInab(remoteID, this.chordId, ((ChordProtocol) successorList[0]
						.getProtocol(p.pid)).chordId)) // si el predesor es mayor al actual y menor a mi sucesor 
					successorList[0] = node; // el antecesor de mi sucesor es mi nuevo sucesor
				//edn if
				((ChordProtocol) successorList[0].getProtocol(p.pid))
						.notify(myNode); 
			}
			updateSuccessorList();
		} catch (Exception e1) {
			e1.printStackTrace();
			updateSuccessor();
		}
	}

	private void updateSuccessorList() throws Exception {
		try {
			while (successorList[0] == null || successorList[0].isUp() == false) {
				updateSuccessor();
			}
			System.arraycopy(((ChordProtocol) successorList[0]
					.getProtocol(p.pid)).successorList, 0, successorList, 1,
					succLSize - 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void notify(Node node) throws Exception {
		BigInteger nodeId = ((ChordProtocol) node.getProtocol(p.pid)).chordId;
		if ((predecessor == null)
				|| (idInab(nodeId, ((ChordProtocol) predecessor
						.getProtocol(p.pid)).chordId, this.chordId))) {
			predecessor = node;
		}
	}

	private void updateSuccessor() {
		boolean searching = true;
		while (searching) {
			try {
				Node node = successorList[varSuccList];
				varSuccList++;
				successorList[0] = node;
				if (successorList[0] == null
						|| successorList[0].isUp() == false) {
					if (varSuccList >= succLSize - 1) {
						searching = false;
						varSuccList = 0;
					} else
						updateSuccessor();
				}
				updateSuccessorList();
				searching = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean idInab(BigInteger id, BigInteger a, BigInteger b) {
		if ((a.compareTo(id) == -1) && (id.compareTo(b) == -1)) {
			return true;
		}
		return false;
	}

	public Node find_successor(BigInteger id) {
		try {
			if (successorList[0] == null || successorList[0].isUp() == false) {
				updateSuccessor();
			}
			if (idInab(id, this.chordId, ((ChordProtocol) successorList[0]
					.getProtocol(p.pid)).chordId)) {
				return successorList[0];
			} else {
				Node tmp = closest_preceding_node(id);
				return tmp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return successorList[0];
	}

	private Node closest_preceding_node(BigInteger id) {
		for (int i = m; i > 0; i--) {
			try {
				if (fingerTable[i - 1] == null
						|| fingerTable[i - 1].isUp() == false) {
					continue;
				}
				BigInteger fingerId = ((ChordProtocol) (fingerTable[i - 1]
						.getProtocol(p.pid))).chordId;
				if ((idInab(fingerId, this.chordId, id))
						|| (id.compareTo(fingerId) == 0)) {
					return fingerTable[i - 1];
				}
				if (fingerId.compareTo(this.chordId) == -1) {
					// sono nel caso in cui ho fatto un giro della rete
					// circolare
					if (idInab(id, fingerId, this.chordId)) {
						return fingerTable[i - 1];
					}
				}
				if ((id.compareTo(fingerId) == -1)
						&& (id.compareTo(this.chordId) == -1)) {
					if (i == 1)
						return successorList[0];
					BigInteger lowId = ((ChordProtocol) fingerTable[i - 2]
							.getProtocol(p.pid)).chordId;
					if (idInab(id, lowId, fingerId))
						return fingerTable[i - 2];
					else if (fingerId.compareTo(this.chordId) == -1)
						continue;
					else if (fingerId.compareTo(this.chordId) == 1)
						return fingerTable[i - 1];
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (fingerTable[m - 1] == null)
			return successorList[0];
		return successorList[0];
	}

	// debug function
	private void printFingers() {
		for (int i = fingerTable.length - 1; i > 0; i--) {
			if (fingerTable[i] == null) {
				System.out.println("Finger " + i + " is null");
				continue;
			}
			if ((((ChordProtocol) fingerTable[i].getProtocol(p.pid)).chordId)
					.compareTo(this.chordId) == 0)
				break;
			System.out
					.println("Finger["
							+ i
							+ "] = "
							+ fingerTable[i].getIndex()
							+ " chordId "
							+ ((ChordProtocol) fingerTable[i]
									.getProtocol(p.pid)).chordId);
		}
	}

	public void fixFingers() {
		if (next >= m - 1)
			next = 0;
		if (fingerTable[next] != null && fingerTable[next].isUp()) {
			next++;
			return;
		}
		BigInteger base;
		if (next == 0)
			base = BigInteger.ONE;
		else {
			base = BigInteger.valueOf(2);
			for (int exp = 1; exp < next; exp++) {
				base = base.multiply(BigInteger.valueOf(2));
			}
		}
		BigInteger pot = this.chordId.add(base);
		BigInteger idFirst = ((ChordProtocol) Network.get(0).getProtocol(p.pid)).chordId;
		BigInteger idLast = ((ChordProtocol) Network.get(Network.size() - 1)
				.getProtocol(p.pid)).chordId;
		if (pot.compareTo(idLast) == 1) {
			pot = (pot.mod(idLast));
			if (pot.compareTo(this.chordId) != -1) {
				next++;
				return;
			}
			if (pot.compareTo(idFirst) == -1) {
				this.fingerTable[next] = Network.get(Network.size() - 1);
				next++;
				return;
			}
		}
		do {
			fingerTable[next] = ((ChordProtocol) successorList[0]
					.getProtocol(p.pid)).find_successor(pot);
			pot = pot.subtract(BigInteger.ONE);
			((ChordProtocol) successorList[0].getProtocol(p.pid)).fixFingers();
		} while (fingerTable[next] == null || fingerTable[next].isUp() == false);
		next++;
	}

	/**
	 */
	public void emptyLookupMessage() {
		index = 0;
		this.lookupMessage = new int[0];
	}
}
