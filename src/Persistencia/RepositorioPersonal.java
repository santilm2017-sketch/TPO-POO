package Persistencia;

import Dominio.PersonalAdministrativo;
import java.util.ArrayList;
import java.util.List;

public class RepositorioPersonal implements Repositorio<PersonalAdministrativo> {
    private final ArchivoLista<PersonalAdministrativo> archivo = new ArchivoLista<>("personal.dat");
    private List<PersonalAdministrativo> datos;

    public RepositorioPersonal() {
        datos = archivo.leer();
    }

    public PersonalAdministrativo buscarPorId(String legajo) {
        if (legajo == null) return null;
        for (PersonalAdministrativo persona : datos) {
            if (persona.getLegajo().equalsIgnoreCase(legajo.trim())) return persona;
        }
        return null;
    }

    public List<PersonalAdministrativo> obtenerTodos() {
        return new ArrayList<>(datos);
    }

    public void guardar(PersonalAdministrativo persona) {
        if (persona == null) throw new IllegalArgumentException("Personal obligatorio.");
        if (buscarPorId(persona.getLegajo()) != null) {
            throw new IllegalArgumentException("Ya existe personal con ese legajo.");
        }
        for (PersonalAdministrativo existente : datos) {
            if (existente.getDocumento().equalsIgnoreCase(persona.getDocumento())) {
                throw new IllegalArgumentException("Ya existe personal con ese documento.");
            }
        }
        datos.add(persona);
        archivo.escribir(datos);
    }

    public void actualizar(PersonalAdministrativo persona) {
        if (persona == null) throw new IllegalArgumentException("Personal obligatorio.");
        PersonalAdministrativo anterior = buscarPorId(persona.getLegajo());
        if (anterior == null) throw new IllegalArgumentException("Personal inexistente.");
        for (PersonalAdministrativo existente : datos) {
            if (existente != anterior && existente.getDocumento().equalsIgnoreCase(persona.getDocumento())) {
                throw new IllegalArgumentException("Ya existe personal con ese documento.");
            }
        }
        datos.set(datos.indexOf(anterior), persona);
        archivo.escribir(datos);
    }
}
