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

import static org.mockito.Mockito.*;


import java.security.Provider;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    ExamenRepositoryImplOtro repository;
    @Mock
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;
    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        //APLICACIÓN DE INYECCIÓN DE DEPENDENCIAS aunque se lo puede hace mucho mejor
        //con @ExtendWith(MockitoExtension.class)
        //MockitoAnnotations.openMocks(this);
        //************************************************************
        //repository= mock(ExamenRepositoryImplOtro.class);//= new ExamenRepositoryImpl();
        //preguntaRepository=mock(PreguntaRepository.class);
        //service=new ExamenServiceImpl(repository,preguntaRepository);
    }

    //NO SE PUEDE HACER UN MOCK DE CUALQUIER MÉTODO SOLO DE AQUELLOS QUE SON PÚBLICOS
    //O DEFAULT,,, NO DE UN PRIVADO O ESTÁTICO O FINAL
    @Test
    void findExamenPorNombre() {
        //uso de mockito
        //when... cuando se llame el método finAll thenReturn.... devolver
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        //uso de mockito
        //when... cuando se llame el método finAll thenReturn.... devolver
        when(repository.findAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
        assertFalse(examen.isPresent());
    }

    //**********************************
    //DESARROLLO IMPULSADO AL COMPORTAMIENTO BDD
    @Test
    void testPreguntaExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("integrales"));
    }

    //**********************************
    //DESARROLLO IMPULSADO AL COMPORTAMIENTO BDD
    @Test
    void testPreguntaExamenVerify() {
        //GIVEN -> PRECONDICIONES EN NUESTRO ENTORNO DE PRUEBA
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        //*****************************************************
        //WHEN -> ejecutando un método real a probar
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        //****************************************************
        //THEN -> validaciones
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("integrales"));
        //verificar si se invocaron los metodos emulandos
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    //**********************************
    //DESARROLLO IMPULSADO AL COMPORTAMIENTO BDD
    @Test
    void testNoExisteExamenVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia2");
        assertNull(examen);
        //verificar si se invocaron los metodos emulandos
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    //DESARROLLO IMPULSADO AL COMPORTAMIENTO BDD
    @Test
    void testGuardarExamen() {
        //***************************************************
        //GIVEN -> PRECONDICIONES EN NUESTRO ENTORNO DE PRUEBA
        //creamos un examen con preguntas para que iteractúe
        // con guardar Varias y pase el verify
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        //si se consulta a repository el método guardar retorna datos de examen
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            //esto se implementa para asignar el id incremental que viene null desde
            //datos
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });
        //*****************************************************
        //WHEN -> ejecutando un método real a probar
        Examen examen = service.guardar(newExamen);
        //****************************************************
        //THEN -> validaciones
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    //VIDEO 42 TERMINADO
    //************************************************************+
    //MANEJO DE EXCEPCIONES Y ERRORES
    @Test
    void testManejoException() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalFormatException.class);
        assertThrows(IllegalFormatException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matemáticas");
        });
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testManejoExceptionIdNull() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalFormatException.class);
        assertThrows(IllegalFormatException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matemáticas");
        });
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }
    //********************************************************************
    //ARGUMENT MATCHERS -> VALIDACIONES PERSONALIZADAS DE LOS ARGUMENTOS


    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg ->
                arg != null && arg.equals(5l)));
        //usamos una expresion lamda para ser mucho más específico
        // en el comprobador de argumentos
    }

    //argument matchers personalizado pero con una clase
    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);//ponemos conids negativos para
        //que falle el test
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgumentMatchers()));
        //se puede hacer con expresiones lamda pero no se podría mostra el msj personalizado
        //como se ve con una clase personalizada
    }
    //clase inner personalizada en nuestra clase test
    public static class MiArgumentMatchers implements ArgumentMatcher<Long> {
        private Long argument;
        @Override
        public boolean matches(Long argument) {
            this.argument=argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error que "+
                    "imprime mockito en caso de que falle el test "+
                    argument+" debe ser entero positivo";
        }
    }
    //CAPTURAR LOS ARGUMENTOS QUE SE PASAN EN LOS MÉTODOS MOCK Y PODER
    //PROBARLOS ArgumentCapture

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        //busca el examen por nombre con preguntas devuelve el examen
        service.findExamenPorNombreConPreguntas("Matemáticas");

        //ArgumentCaptor<Long> captor=ArgumentCaptor.forClass(long.class);//captura el Id
        //LA LÍNEA ANTERIOR SE LA PUEDE HACER CONA NOTACIONES AL PRINCIPIO
        //DE LA CLASE CON LA ANOTACIÓN capture de mockito
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());//capturamos el argumento
        //testeamos el argumento capturado
        assertEquals(5L,captor.getValue());
    }
    //********************************************************
    //USANDO MÉTODO DoTrow que es para los métodos void
    @Test
    void testDoTrow() {
        Examen examen=Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        //el when lo utilizamos cuando siempre retorna un objeto
        //pero cuando se trata de un void que no devuelve nada no se puede
        //para ello utilizamos do al principio es como dar la vuelta la
        //pregunta
        doThrow(IllegalFormatException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalFormatException.class, ()->{
            service.guardar(examen);
        });
    }
    //**********************************************************************
    //USANDO EL MÉTODO DoAnsWer

    @Test
    void testDoAnsWer() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
         doAnswer(invocation -> {
             Long id=invocation.getArgument(0);
             return id == 5L? Datos.PREGUNTAS:Collections.emptyList();
         }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

         Examen examen=service.findExamenPorNombreConPreguntas("Matemáticas");

         assertEquals(5,examen.getPreguntas().size());
         assertTrue(examen.getPreguntas().contains("geometría"));
         assertEquals(5L,examen.getId());
         assertEquals("Matemáticas",examen.getNombre());
    }
    @Test
    void testDoAnswerGuardarExamen() {
        //***************************************************
        //GIVEN -> PRECONDICIONES EN NUESTRO ENTORNO DE PRUEBA
        //creamos un examen con preguntas para que iteractúe
        // con guardar Varias y pase el verify
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        //si se consulta a repository el método guardar retorna datos de examen
        doAnswer(new Answer<Examen>() {
            //esto se implementa para asignar el id incremental que viene null desde
            //datos
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));
        //*****************************************************
        //WHEN -> ejecutando un método real a probar
        Examen examen = service.guardar(newExamen);
        //****************************************************
        //THEN -> validaciones
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }
    // METODO DoCallRealMethod PARA LA LLAMADA REAL DE UN MÉTODO MOCK
    // HIBRIDO
    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        //comentamos la siguiente línea para hacer una semi simulacion y eso
        //lo hacemos con DoCallRealMethod()
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen=service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5L,examen.getId());
        assertEquals("Matemáticas",examen.getNombre());
    }
    //VIDEO 50 FINALIZADO*******+++++++++++++++++++++++++++++++

    @Test
    void testSpy() {
        //DIFERENCIAS ENTRE UN SPY Y UN MOCK
        //UN mock EN 100% SIMULADO todos sus metodos tenemos que mockear simular,
        //EL spy NO VAMOS A SIMULAR LO QUE DESEAMOS PERO LO DEMÁS REAL
        //SPY requiere que se cree a partir de una clase concreta y no de una clase abstracta
        // o interfaz
        //ExamenRepository examenRepository=mock(ExamenRepository.class);//creando un mock
        ExamenRepository examenRepository=spy(ExamenRepositoryImplOtro.class);
        PreguntaRepository preguntaRepository=spy(PreguntaRepositoryImpl.class);
        ExamenService examenService=new ExamenServiceImpl(examenRepository,preguntaRepository);

        List<String> preguntas=Arrays.asList("aritmética");
        //la línea siguiente llama al método original, pero termina utilizando una simulación
        //poe eso nos conviene utilizar doReturn para cuando usamos SPY
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen=examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5l,examen.getId());
        assertEquals("Matemáticas",examen.getNombre());
        assertEquals(1,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testOrderDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5l);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6l);
    }
    //ORDEN EN EL QUE SE EJECUTAN LOS MOCKS
    @Test
    void testOrderDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(repository, preguntaRepository);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5l);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6l);
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        verify(preguntaRepository).findPreguntasPorExamenId(5l);
        //al menos, a lo sumo, como máximo etc....
        verify(preguntaRepository,times(1)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atLeast(1)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atMost(10)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5l);
    }
    @Test
    void testNumeroDeInvocaciones2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPreguntas("Matemáticas");
        //verify(preguntaRepository).findPreguntasPorExamenId(5l);
        //al menos, a lo sumo, como máximo etc....
        verify(preguntaRepository,times(2)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atLeast(1)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5l);
        verify(preguntaRepository,atMost(2)).findPreguntasPorExamenId(5l);
       // verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5l);
    }

    @Test
    void tesNumeroInvocaciones3() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.findExamenPorNombreConPreguntas("Matemáticas");
        verify(preguntaRepository,never()).findPreguntasPorExamenId(5l);
        verifyNoInteractions(preguntaRepository);

        verify(repository).findAll();
        verify(repository,times(1)).findAll();
        verify(repository,atLeast(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository,atMost(10)).findAll();
        verify(repository,atMostOnce()).findAll();

    }
}