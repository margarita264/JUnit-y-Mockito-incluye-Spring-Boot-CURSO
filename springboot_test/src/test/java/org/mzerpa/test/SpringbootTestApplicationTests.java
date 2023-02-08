package org.mzerpa.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mzerpa.test.Datos.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mzerpa.test.exceptions.DineroInsuficienteException;
import org.mzerpa.test.models.Banco;
import org.mzerpa.test.models.Cuenta;
import org.mzerpa.test.repositories.BancoRepository;
import org.mzerpa.test.repositories.CuentaRepository;
import org.mzerpa.test.services.CuentaService;
import org.mzerpa.test.services.CuentaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

@SpringBootTest
class SpringbootTestApplicationTests {
	@MockBean//anotaciones con springBoot
	//@Mock
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
	@Autowired
	CuentaService service;
	//@InjectMocks
	//CuentaServiceImpl service;

	@BeforeEach
	void setUp() {
		//cuentaRepository= mock(CuentaRepository.class);
		//bancoRepository= mock(BancoRepository.class);

		//service=new CuentaServiceImpl(cuentaRepository,bancoRepository);
	}
	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.finById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino= service.revisarSaldo(2L);
		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		service.transferir(1L,2L,new BigDecimal("100"),1L);
		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino= service.revisarSaldo(2L);

		assertEquals("900",saldoOrigen.toPlainString());
		assertEquals("2100",saldoDestino.toPlainString());

		int total=service.revisarTotalTransferencias(1L);
		assertEquals(1,total);

		//verificamos cuantas veces se ejecutan los mock
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(3)).findById(2L);
		verify(cuentaRepository,times(2)).update(any(Cuenta.class));

		verify(bancoRepository,times(2)).finById(1L);
		verify(bancoRepository).update(any(Banco.class));

		verify(cuentaRepository,times(6)).findById(anyLong());
		verify(cuentaRepository,never()).findAll();
	}
	//EXCEPCIONES*************************************************
	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(crearCuenta002());
		when(bancoRepository.finById(1L)).thenReturn(crearBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino= service.revisarSaldo(2L);
		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class,()->{
			service.transferir(1L,2L,new BigDecimal("1200"),1L);
		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino= service.revisarSaldo(2L);

		assertEquals("1000",saldoOrigen.toPlainString());
		assertEquals("2000",saldoDestino.toPlainString());

		int total=service.revisarTotalTransferencias(1L);
		assertEquals(0,total);

		//verificamos cuantas veces se ejecutan los mock
		verify(cuentaRepository,times(3)).findById(1L);
		verify(cuentaRepository,times(2)).findById(2L);
		verify(cuentaRepository,never()).update(any(Cuenta.class));

		verify(bancoRepository,times(1)).finById(1L);
		verify(bancoRepository,never()).update(any(Banco.class));

		verify(cuentaRepository,times(5)).findById(anyLong());
		verify(cuentaRepository,never()).findAll();
	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(crearCuenta001());

		Cuenta cuenta1=service.findById(1L);
		Cuenta cuenta2=service.findById(1L);

		assertSame(cuenta1,cuenta2);
		assertTrue(cuenta1==cuenta2);
		assertEquals("Andres",cuenta1.getPersona());
		assertEquals("Andres",cuenta2.getPersona());

		verify(cuentaRepository,times(2)).findById(1L);
	}
	//QUE ES LA INYECCIÓN DE DEPENDENCIA??
	//es suministrar a un objeto una referencia de otro(s) objeto(s)
	//que necesite según la relación
	//resuelve el problema de reutilización y modularidad entre componentes

	//COMO FUNCIONA
	//el contenedor se encarga de gestionar las instancias y dependencias de
	//componentes mediante la relación e inyección de objetos

	//En contra-oposición de la creación explica (operador new) de objetos
	//esto permite un bajo acoplamiento entre objetos
	//Tiene que plasmarse mediante la anotación @Autowired

	//***************************************************************
	//VIDEO 66 FINALIZADO
}
