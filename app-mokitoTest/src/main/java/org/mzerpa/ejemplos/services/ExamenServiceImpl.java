package org.mzerpa.ejemplos.services;

import org.mzerpa.ejemplos.models.Examen;
import org.mzerpa.ejemplos.repositories.ExamenRepository;
import org.mzerpa.ejemplos.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository=preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        //el optional contiene una representación del objeto para evitar el nullPoinEcxeption
       return examenRepository.findAll()
               .stream()
               .filter(e -> e.getNombre().contains(nombre))
               .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional=findExamenPorNombre(nombre);
        Examen examen=null;
        if(examenOptional.isPresent()){
            examen=examenOptional.orElseThrow();
            List<String> preguntas=preguntaRepository.findPreguntasPorExamenId(examen.getId());
            preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }
}
