package peersim.pastry;



import java.math.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;


public class Catalogo{

	private String nombreCancion;
	private ArrayList<BigInteger> encargados =new ArrayList<BigInteger>();;

	public void Bloque(){
		nombreCancion="";
	}

	public void Bloque(String nombreCancion, ArrayList<BigInteger> encargado){
		this.nombreCancion=nombreCancion;
		this.encargados=encargado;
	}

	public String getNombreCancion(){
        return nombreCancion;
    }
	
	public void setNombreCancion(String nombreCancion){
    	this.nombreCancion = nombreCancion;
	}

	public ArrayList<BigInteger> getEncargados(){
		return encargados;
	}


} // End of class
