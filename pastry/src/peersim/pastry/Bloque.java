package peersim.pastry;



import java.math.*;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;
import java.util.*;


public class Bloque{

	private String nombreCancion;
	private int secuenciaBloque;
	private String particion;

	public void Bloque(){
		nombreCancion="";
		secuenciaBloque=0;
		particion="";
	}

	public void Bloque(String nombre, int secuencia, String particion){
		this.nombreCancion=nombre;
		this.secuenciaBloque=secuencia;
		this.particion=particion;
	}

	public String getNombreCancion(){
        return nombreCancion;
    }
	
	public void setNombreCancion(String nombreCancion){
    	this.nombreCancion = nombreCancion;
	}

	public int getSecuenciaBloque(){
        return secuenciaBloque;
    }
	
	public void setSecuenciaBloque(int secuenciaBloque){
    	this.secuenciaBloque = secuenciaBloque;
	}

	public String getParticion(){
        return particion;
    }
	
	public void setParticion(String particion){
    	this.particion = particion;
	}

} // End of class
