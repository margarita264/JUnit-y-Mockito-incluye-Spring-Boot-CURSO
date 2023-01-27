package org.mzerpa.ejemplos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.mzerpa.ejemplos.Datos;
import org.mzerpa.ejemplos.models.Examen;
import org.mzerpa.ejemplos.repositories.ExamenRepository;
import org.mzerpa.ejemplos.repositories.ExamenRepositoryImplOtro;
import org.mzerpa.ejemplos.repositories.PreguntaRepository;
import org.mzerpa.ejemplos.repositories.PreguntaRepositoryImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @Spy
    ExamenRepositoryImplOtro examenRepository;
    @Spy
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    @Test
    void testSpy() {
        //DIFERENCIAS ENTRE UN SPY Y UN MOCK
        //UN mock EN 100% SIMULADO todos sus metodos tenemos que mockear simular,
        //EL spy NO VAMOS A SIMULAR LO QUE DESEAMOS PERO LO DEMÁS REAL
        //SPY requiere que se cree a partir de una clase concreta y no de una clase abstracta
        // o interfaz

        List<String> preguntas=Arrays.asList("aritmética");
        //la línea siguiente llama al método original, pero termina utilizando una simulación
        //poe eso nos conviene utilizar doReturn para cuando usamos SPY
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen=service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5l,examen.getId());
        assertEquals("Matemáticas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
}