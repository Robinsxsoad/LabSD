import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Chunkeador{

	// Función que corta el archivo
	public static void cortarCancion(File f) throws IOException {
        int numeroParte = 1;
        int tamParticion = 128000;// 128KB // Tamaño de las particiones
        byte[] buffer = new byte[tamParticion];

        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f))) {//try-with-resources to ensure closing stream
            String nombre = f.getName();

            int temp = 0;
            while ((temp = bis.read(buffer)) > 0) {
                File parte = new File(f.getParent(), nombre + "."
                        + String.format("%01d", numeroParte++));
                try (FileOutputStream out = new FileOutputStream(parte)) {
                    out.write(buffer, 0, temp);
                }
            }
        }
    }
    
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
    
        public static List<Byte> cortarCancion(String nombre){
            String fileName = "song.mp3";

        try {
            // Use this for reading the data.
            byte[] buffer = new byte[128000];

            FileInputStream inputStream = 
                new FileInputStream(fileName);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                // System.out.println(new String(buffer));
                System.out.println("Leí la primera partición de tamaño "+buffer.length+"\n");
                total += nRead;
            }   

            // Always close files.
            inputStream.close();        

            System.out.println("Read " + total + " bytes");
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
        }

        public static void main(String [] args) {

        // The name of the file to open.
        String fileName = "song.mp3";

        try {
            // Use this for reading the data.
            byte[] buffer = new byte[128000];

            FileInputStream inputStream = 
                new FileInputStream(fileName);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                // System.out.println(new String(buffer));
                System.out.println("Leí la primera partición de tamaño "+buffer.length+"\n");
                total += nRead;
            }   

            // Always close files.
            inputStream.close();        

            System.out.println("Read " + total + " bytes");
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
        }
    

} // End of class
