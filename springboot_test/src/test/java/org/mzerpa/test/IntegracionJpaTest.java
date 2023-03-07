package org.mzerpa.test;

import org.junit.jupiter.api.Test;
import org.mzerpa.test.models.Cuenta;
import org.mzerpa.test.repositories.CuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){
       Optional<Cuenta> cuenta= cuentaRepository.findById(1L);
       assertTrue(cuenta.isPresent());
       assertEquals("Andres", cuenta.orElseThrow().getPersona());
    }
    @Test
    void testFindPersona(){
        Optional<Cuenta> cuenta= cuentaRepository.findByPersona("Andres");
        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }
    @Test
    void testFindPersonaThrowException(){
        Optional<Cuenta> cuenta= cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class,cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());
    }
    @Test
    void testSave(){
        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        //cuentaRepository.save(cuentaPepe);//alt+ctrl+v

        //When
        Cuenta cuenta=cuentaRepository.save(cuentaPepe);//alt+ctrl+v
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        //Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3000",cuenta.getSaldo().toPlainString());
        //assertEquals(3,cuenta.getId());
    }

    @Test
    void testUpdate(){
        //Given
        Cuenta cuentaPepe = new Cuenta(null,"Pepe",new BigDecimal("3000"));
        //cuentaRepository.save(cuentaPepe);//alt+ctrl+v

        //When
        Cuenta cuenta=cuentaRepository.save(cuentaPepe);//alt+ctrl+v
        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").orElseThrow();
        //Cuenta cuenta = cuentaRepository.findById(save.getId()).orElseThrow();

        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        //Then
        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800",cuentaActualizada.getSaldo().toPlainString());
        //assertEquals(3,cuenta.getId());
    }
    @Test
    void testDelete(){
        Cuenta cuenta=cuentaRepository.findById(2L).orElseThrow();
        assertEquals("John", cuenta.getPersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () ->{
            cuentaRepository.findByPersona("John").orElseThrow();
        });
        assertEquals(1, cuentaRepository.findAll().size());
    }
    //VIDEO 74 FINALIZADO
    //MÓDULO 5 FINALIZADO
}
