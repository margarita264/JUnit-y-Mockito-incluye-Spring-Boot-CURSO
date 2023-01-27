package org.mzerpa.ejemplos.repositories;

import org.mzerpa.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar (Examen examen);
    List<Examen>findAll();
}
