package Persistencia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// La copia anterior permite recuperar una escritura interrumpida.
class ArchivoLista<T> {
    private final File archivo;
    private final File respaldo;

    ArchivoLista(String nombre) {
        archivo = new File("datos/" + nombre);
        respaldo = new File(archivo + ".anterior");
    }

    private boolean existe() { return archivo.exists() || respaldo.exists(); }

    List<T> leer() {
        if (!existe()) return new ArrayList<>();
        File origen = archivo.exists() ? archivo : respaldo;
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(origen))) {
            return (List<T>) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new ErrorArchivo("No se pudo leer " + origen, e);
        }
    }

    void escribir(List<T> datos) {
        try {
            Files.createDirectories(Paths.get("datos"));
        } catch (IOException e) {
            throw new ErrorArchivo("No se pudo crear la carpeta datos.", e);
        }
        File temporal = new File(archivo + ".nuevo");
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(temporal))) {
            salida.writeObject(datos);
        } catch (IOException e) {
            throw new ErrorArchivo("No se pudo escribir " + temporal, e);
        }
        try {
            if (archivo.exists()) {
                if (respaldo.exists()) Files.delete(Paths.get(respaldo.toString()));
                Files.move(Paths.get(archivo.toString()), Paths.get(respaldo.toString()));
            }
            Files.move(Paths.get(temporal.toString()), Paths.get(archivo.toString()));
        } catch (IOException e) {
            throw new ErrorArchivo("No se pudo completar la escritura de " + archivo, e);
        }
    }
}
