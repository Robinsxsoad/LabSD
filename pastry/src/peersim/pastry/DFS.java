package peersim.pastry;



import java.math.*;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.util.Comparator;
import java.util.ArrayList;


public class DFS implements Cloneable, EDProtocol {

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

} // End of class
