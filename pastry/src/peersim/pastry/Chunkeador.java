package peersim.pastry;

import java.io.*;
import java.util.List;
import java.util.*;

public class Chunkeador{
    
        public static ArrayList<String> cortarCancion(String nombre){
            String fileName = nombre;
            ArrayList<String> partesCancion = new ArrayList<String>();
        try {
            // Use this for reading the data.
            byte[] buffer = new byte[128000];
            String partes;
            FileInputStream inputStream = 
                new FileInputStream(fileName);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            int i = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                // System.out.println(new String(buffer));
                // partes = Arrays.toString(buffer);
                // partes = buffer.toString();
                partes = new String(buffer);
                partesCancion.add(partes);
                i=i+1;
                // System.out.println("Esta es la partición: "+i);
                total += nRead;
            }   

            // Always close files.
            inputStream.close();        
            // System.out.println("Read " + total + " bytes");
            return partesCancion;
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "No se encuentra el archivo para la canción '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error en la lectura del archivo '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
            }
            return partesCancion;
        }

        // Asumo que la entrada llega ordenada
        public static String unirCancion(ArrayList<String> partes){
            Object[] cancion;
            cancion = partes.toArray();
            String cancionUnida= Arrays.toString(cancion);
            
            return cancionUnida;
        }


    

} // End of class
