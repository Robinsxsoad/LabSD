package peersim.pastry;

import java.io.*;
import java.util.List;
import java.util.*;

public class Chunkeador{

    // Función que corta el archivo
    // public static void cortarCancion(File f) throws IOException {
 //        int numeroParte = 1;
 //        int tamParticion = 128000;// 128KB // Tamaño de las particiones
 //        byte[] buffer = new byte[tamParticion];

 //        try (BufferedInputStream bis = new BufferedInputStream(
 //                new FileInputStream(f))) {//try-with-resources to ensure closing stream
 //            String nombre = f.getName();

 //            int temp = 0;
 //            while ((temp = bis.read(buffer)) > 0) {
 //                File parte = new File(f.getParent(), nombre + "."
 //                        + String.format("%01d", numeroParte++));
 //                try (FileOutputStream out = new FileOutputStream(parte)) {
 //                    out.write(buffer, 0, temp);
 //                }
 //            }
 //        }
 //    }
    
    // Función que une la canción
// public static void mergeFiles(List<File> files, File into)
//         throws IOException {
//     try (BufferedOutputStream mergingStream = new BufferedOutputStream(
//             new FileOutputStream(into))) {
//         for (File f : files) {
//             Files.copy(f.toPath(), mergingStream);
//         }
//     }
// }

    // Función para buscar las partes de la canción
// public static List<File> listOfFilesToMerge(File oneOfFiles) {
//     String tmpName = oneOfFiles.getName();//{name}.{number}
//     File dir;
//     String name;
//     String destFileName = tmpName.substring(0, tmpName.lastIndexOf('.'));//remove .{number}
//     File[] files = oneOfFiles.getParentFile().listFiles(
//             (dir, name) -> name.matches(destFileName + "[.]\\d+"));
//     Arrays.sort(files);//ensuring order 001, 002, ..., 010, ...
//     return Arrays.asList(files);
// }
    
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
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
            }
            return partesCancion;
        }

        // Asumo que la entrada llega ordenada
        public static Object[] unirCancion(ArrayList<String> partes, int numPartes){
            Object[] cancion;
            cancion = partes.toArray();
            
            
            return cancion;
        }

        public static void main(String [] args) {
            ArrayList<String> partes = cortarCancion("song.mp3");
            
            Object[] cancion;
            cancion = unirCancion(partes, 103);
            
        }
    

} // End of class
