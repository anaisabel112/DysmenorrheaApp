package com.example.login;
import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.Scanner;

public class FileUtils {

    // Método para exportar el archivo JSON a la carpeta de "Descargas"
    public static void exportJsonToDownloads(File origen, String nombreArchivo) {
        try {
            // Obtener el directorio de Descargas en almacenamiento externo
            File destino = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    nombreArchivo
            );

            // Verificar si el directorio existe, y si no, crear el directorio
            if (!destino.getParentFile().exists()) {
                destino.getParentFile().mkdirs();
            }

            // Leer el archivo de origen y escribirlo en el destino
            try (FileInputStream fis = new FileInputStream(origen);
                 FileOutputStream fos = new FileOutputStream(destino)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                Log.d("FileUtils", "Archivo exportado a: " + destino.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error exportando archivo JSON", e);
        }
    }


    // Método para leer el contenido de un archivo
    public static String readFile(File file) {
        StringBuilder jsonString = new StringBuilder();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error leyendo el archivo", e);
        }
        return jsonString.toString();
    }

    // Método para escribir contenido en un archivo
    public static void writeFile(File file, String data) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            Log.e("FileUtils", "Error escribiendo en el archivo", e);
        }
    }
}
