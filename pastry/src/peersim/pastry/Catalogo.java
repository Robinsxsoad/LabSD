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
	private BigInteger encargado;

	public void Bloque(){
		nombreCancion="";
	}

	public void Bloque(String nombreCancion, BigInteger encargado){
		this.nombreCancion=nombreCancion;
		this.encargado=encargado;
	}

	public String getNombreCancion(){
        return nombreCancion;
    }
	
	public void setNombreCancion(String nombreCancion){
    	this.nombreCancion = nombreCancion;
	}

	public BigInteger getEncargado(){
		return encargado;
	}

	public void setEncargado(BigInteger encargado){
    	this.encargado = encargado;
	}


} // End of class
