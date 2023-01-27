package org.mzerpa.ejemplos;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mzerpa.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)//para crear una sola intancia de la clase y usar la misma para los métodos
class CuentaTest {
    Cuenta cuenta;

    private TestInfo testInfo;//se definen para poder usar estas variables todos los métodos
    private TestReporter testReporter;
    @BeforeEach//permite que este método se ejecute antes de cada test
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter){//INYECCIÓN DE DEPENDENCIAS se aplica para todos los métodos
        this.cuenta = new Cuenta("Marga", new BigDecimal("1000.12345"));
        this.testInfo=testInfo;
        this.testReporter=testReporter;
        System.out.println("INICIANDO EL METODO.");
        testReporter.publishEntry("ejecutando: " + testInfo.getDisplayName()+" "+testInfo.getTestMethod().orElse(null).getName()
                +" con las etiquetas "+ testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("fializando el método de prueba");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("inicializando el test");
    }
    @AfterAll
    static void afterAll(){
        System.out.println("finalizando el test");
    }

    //EN LAS CLASES ANIDADAD SE PUEDE UTILIZAR EL FEFOREEACH Y AFTEREACHA

    // SI FALLA UN TEST DE UNA CLASE ANIDADA FALLA TODA LA CLASE ANIDADA}
    // LAS USAREMOS PARA ORGANIZAR LOS TEST Y CATEGORIZAR
    @Nested
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo{
        //ERRORES PERSONALIZADOS en cada assert se puede mandar un mensaje para
        //que sea más descriptivo el error se pone una función lamda para que la
        //instancia del mensaje se cree solo si falla el test y no siempre consumiendo espacio,
        @Tag("cuenta")
        @Test
        @DisplayName("probando el nombre de la cuenta")
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if(testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            //instanciamos la clase a probar
            cuenta = new Cuenta("Marga", new BigDecimal("1000.12313"));
            String esperado = "Marga";
            String real = cuenta.getPersona();
            assertNotNull(real,() ->"La cuenta no puede ser nula");
            assertEquals(esperado, real,() ->"El nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals("Marga"),() ->"nombre cuenta esperada debe ser igual a la real");
        }
        @Tag("cuenta")
        @Test
        @DisplayName("probando saldo que no sea null, mayor que cero, valor esperado")
        void testSaldoCuenta() {
            cuenta = new Cuenta("Marga", new BigDecimal("1000.12313"));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12313, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
        //terminado video 10
        //Guía Completa JUnit y Mockito incluye Spring Boot Test 2023
        @Tag("cuenta")
        @Test
        @DisplayName("testeando referencias que sean iguales con método equals")
        void testReferenciaCuenta() {
            cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
            //assertNotEquals(cuenta2,cuenta);
            assertEquals(cuenta2, cuenta);
        }
    }
    @Nested
    class CuentaOperacionesTest{
        //EL BIGDECIMAL ES INMUTABLE
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }
        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");
            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
            assertEquals("3000", cuenta1.getSaldo().toPlainString());
        }

    }

    //manejo de excepcione***********************
    @Test
    void DineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }



    @Test
    @Disabled //hace que se salte este test
    void testRelacionBancoCuentas() {
        fail(); //fuerza el error y nos aseguramos que el test falla
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());
        assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());

        //verifica si banco tiene una cuenta de tal persona
        assertEquals("Andres", banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Andres"))
                .findFirst().get().getPersona());
        //otra opcion para la verificación anterior
        assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getPersona().equals("Andres"))
                .findFirst().isPresent());
        //otra opcion para las verificaciones anteriores
        assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getPersona().equals("Andres")));
    }

    //USO DE assertAll PARA SEGUIMIENTO
    //se usa para llevar un controll de los assertAll
    // para verificar cual falla en caso de tener muchos
    @Test
    @DisplayName("Probando relaciones entre las cuentas y el banco con asserAll")
    void testRelacionBancoCuentas1() {
        Cuenta cuenta1 = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> {
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
                },
                () -> {//verifica si banco tiene una cuenta de tal persona
                    assertEquals("Andres", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst().get().getPersona());
                },
                () -> {//otra opcion para la verificación anterior
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst().isPresent());
                },
                () -> { //otra opcion para las verificaciones anteriores
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Andres")));
                });
    }
    //EJECUCIÓN DE TEST CONDICIONALES**************************************
    //CONDICIONAR LA EJECUCIÓN DE LOS TEST SEGÚN AGUNA CONFIGURACIÓN O PROPERTIES
    //DE NUESTRA MÁQUINA O VARIABLES DE ENTORNO
    @Nested
    class SistemaOperativoTest{//clases anidadas
        @Test
        @EnabledOnOs({OS.WINDOWS})
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs({OS.WINDOWS})
        void sinNoWindows() {
        }
    }
    @Nested
   class JavaVersionTest{
       @Test
       @EnabledOnJre({JRE.JAVA_8})
       void solojdk8() {
       }
       @Test
       @EnabledOnJre({JRE.OTHER})
       void solojdk17() {
       }
   }
    //se puede crear test para que se ejecuten dependiendo de cualquier
    //propiedad con @EnabledIfSystemProperty(named=...., matches=...)
    @Nested
   class SistemPropertiesTest{
       @Test
       void imprimirSystemProperties() {
           Properties properties=System.getProperties();
           properties.forEach((k,v)->System.out.println(k + ":" + v));
       }
       @Test
       @EnabledIfSystemProperty(named="java.version", matches=".*17.*")
       void testJavaVersion() {
       }
   }
    //VARIABLES DE AMBIENTE
    @Nested
   class VariableAmbienteTest{
        @Test
        void imprimirVariableAmbientes() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v)->System.out.println(k +"="+v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA:HOME", matches=".*jdk-17.0.5")
        void testJavaHome() {
        }
   }

    //VIDEO 22 TERMINADO
    //El método siguiente se desabilita si se ejecuta en prod
    @Test
    @DisplayName("test saldo  CuentasDev")
    void testSaldoCuentaDev() {
        boolean esDev="DEV".equals(System.getProperty("env"));
        assumeTrue(esDev);// si es false se desabilita el método directamente
        cuenta = new Cuenta("Marga", new BigDecimal("1000.12313"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12313, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    //siqueremos solo omitir parte del código y no todo el método
    @Test
    @DisplayName("test saldo  CuentasDev 2")
    void testSaldoCuentaDev2() {
        boolean esDev="DEV".equals(System.getProperty("env"));
        assumingThat(esDev,()->{
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12313, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });// lo que está dentro de la función lamnda se ejecutara
        // depende si es false o no
        //lo que está fuera si o si se ejecuta
    }
    @Tag("param")//etiqueta
    @Nested
    class pruebasRepetitivasParametrizadasTest{
        //PRUEBAS REPETITIVAS
        @DisplayName("probando Debito Cuenta Repetir!")
        @RepeatedTest(value=5, name="{displayName} Repetición numero {currentRepetition} de {totalRepetitions} ") // que el tes se repita 5 veces
        void TTestDebitoCuentaRepeat(RepetitionInfo info) {
            //como es test es repetitivo podemos usar la información de la repeticion
            //actual para poner algun condicional o lo que sea necesario
            if(info.getCurrentRepetition()==3){
                System.out.println("estamos en la repetición "+info.getCurrentRepetition());
            }
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        //EL SIQUIENTE TEST ESTÁ INHABILITADO PORQUE NO ME RECONOCE parameterizer.....

        @ParameterizedTest(name="numero {index} ejecutando con valor {0}-{argumentsWithNames}")
        @ValueSource(strings={"100","200","300","500","700","1000"})//pueden ser enteros o doubles
        void testDebitoCuentaSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
         }


        @ParameterizedTest(name="numero {index} ejecutando con valor {0}-{argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000"})//pueden ser enteros o doubles
        void testDebitoCuentaSource(String index, String monto) {
        System.out.println(index + "->"+ monto);
           cuenta.debito(new BigDecimal(monto));
           assertNotNull(cuenta.getSaldo());
           assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
         }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0}-{argumentsWithNames}")
        @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","300,300,maria,Maria","510,500,Pepa,Pepa","750,700,Lucas,Luca","1000.12345,1000.12345,Cata,Cata"})//pueden ser enteros o doubles
        void testDebitoCuentaSource1(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + "->"+ monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);
            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0}-{argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")//pueden ser enteros o doubles
        void testDebitoCuentaSource1(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
        }

        @ParameterizedTest(name="numero {index} ejecutando con valor {0}-{argumentsWithNames}")
        @MethodSource("montoList")
        //@Disabled //inabilita el test
        void testDebitoCuentaSource3(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);
         }
        static List<String> montoList(){
            return Arrays.asList("100","200","300","500","700","1000");
        }
        //VIDEO 26 TERMINADO*************************************

    }
    @Tag("tiemout")
    @Nested
    class pruebaSTimeout{
        @Test
        @Timeout(1)//despues de 5 segundos falla
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }
        @Test
        @Timeout(value=1000, unit=TimeUnit.MILLISECONDS)//despues de 5 segundos falla
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeOutAssertion() {
            assertTimeout(Duration.ofSeconds(5),()->{
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }
}
