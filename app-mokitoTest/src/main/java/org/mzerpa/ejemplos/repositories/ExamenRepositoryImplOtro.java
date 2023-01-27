package org.mzerpa.ejemplos.repositories;

import org.mzerpa.ejemplos.Datos;
import org.mzerpa.ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImplOtro implements ExamenRepository {
    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImplOtro.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImplOtro.findAll");
        try {
            System.out.println("holaaa");
            TimeUnit.SECONDS.sleep(5);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }
}
