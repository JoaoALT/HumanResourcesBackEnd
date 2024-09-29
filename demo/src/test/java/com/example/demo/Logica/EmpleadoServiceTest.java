package com.example.demo.Logica;

import com.example.demo.bd.EmpleadoJPA;
import com.example.demo.bd.EmpleadoORM;
import com.example.demo.logica.EmpleadoService;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServiceTest.class);
    private Validator validator;

    @Mock
    EmpleadoJPA empleadoJPA;

    @InjectMocks
    EmpleadoService empleadoService;

    @Test
    void Given_unvalidEmpleadoORM_When_validandoEmpleado_Then_AssertViolations(){
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setId(1);
        empleadoORM.setNombre("");
        empleadoORM.setApellido("");
        empleadoORM.setEmail("");
        empleadoORM.setTelefono("");
        empleadoORM.setHabilidades(new ArrayList<>());
        empleadoORM.setFormacionAcademica(new ArrayList<>());
        empleadoORM.setHistorialLaboral(new ArrayList<>());

        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<EmpleadoORM>> violations = validator.validate(empleadoORM);

        for (ConstraintViolation<EmpleadoORM> violation : violations) {
            log.info(violation.getMessage());
        }


        assertEquals(7,violations.size());
    }

    @Test
    void When_guardarEmpleado_Then_returnTrue(){
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setId(1);
        empleadoORM.setNombre("mario");
        empleadoORM.setApellido("castaneda");
        empleadoORM.setEmail("hai@gmail.com");
        empleadoORM.setTelefono("1010101010");
        empleadoORM.setHabilidades(new ArrayList<>());
        empleadoORM.setFormacionAcademica(new ArrayList<>());
        empleadoORM.setHistorialLaboral(new ArrayList<>());

        boolean resultado = empleadoService.addEmpleado(empleadoORM);
        Assertions.assertTrue(resultado);
        verify(empleadoJPA).save(Mockito.any());
    }

    @Test
    void GivenEmpleadoIncorrecto_WhenGuardarEmpleado_ThenReturnFalse(){
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setId(1);
        empleadoORM.setNombre("");
        empleadoORM.setApellido("castaneda");
        empleadoORM.setEmail("hai@gmail.com");
        empleadoORM.setTelefono("1010101010");
        empleadoORM.setHabilidades(new ArrayList<>());
        empleadoORM.setFormacionAcademica(new ArrayList<>());
        empleadoORM.setHistorialLaboral(new ArrayList<>());

        boolean result = empleadoService.addEmpleado(empleadoORM);
        Assertions.assertFalse(result);
    }


    @Test
    void GivenEmpleadoValido_WhenGuardarEmpleadoLanzaException_ThenCatchException() {
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setNombre("Mario");
        empleadoORM.setApellido("Castaneda");
        empleadoORM.setEmail("mario@gmail.com");
        empleadoORM.setTelefono("1010101010");
        empleadoORM.setHabilidades(new ArrayList<>());
        empleadoORM.setFormacionAcademica(new ArrayList<>());
        empleadoORM.setHistorialLaboral(new ArrayList<>());

        when(empleadoJPA.save(any(EmpleadoORM.class))).thenThrow(new RuntimeException("Database save failed"));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            empleadoService.addEmpleado(empleadoORM);
        });
        Assertions.assertEquals("Database save failed", exception.getMessage());
    }


    @Test
    void When_findAllEmpleados_Then_returnTrue(){

        ArrayList<EmpleadoORM> empleadosSimulados = new ArrayList<>();
        empleadosSimulados.add(new EmpleadoORM());
        empleadosSimulados.add(new EmpleadoORM());
        empleadosSimulados.add(new EmpleadoORM());

        when(empleadoJPA.findAll()).thenReturn(empleadosSimulados);

        List<EmpleadoORM> empleadoORMList = empleadoService.findAllEmpleados();

        assertEquals(3, empleadoORMList.size());
        Assertions.assertFalse(empleadoORMList.isEmpty());
        verify(empleadoJPA).findAll();
    }

    @Test
    void GiveExisteEmpleadoId_WhenGetEmpleadoById_Then_returEmpleado() {
            EmpleadoORM empleadoORM = new EmpleadoORM();
            empleadoORM.setId(0);
            when(empleadoJPA.findById(0)).thenReturn(Optional.of(empleadoORM));

            EmpleadoORM result = empleadoService.getEmpleadoById(0);

            assertEquals(empleadoORM, result);
            verify(empleadoJPA).findById(0);
    }

    @Test
    void GiveExisteEmpleadoEmail_WhenGetEmpleadoByEmail_Then_returEmpleado() {
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setEmail("hola@gmail.com");

        String email = "hola@gmail.com";
        when(empleadoJPA.findByEmail(email)).thenReturn(empleadoORM);

        EmpleadoORM result = empleadoService.getEmpleadoByEmail(email);

        assertEquals(empleadoORM, result);
        verify(empleadoJPA).findByEmail(email);
    }

    @Test
    void GivenEmpleadoId_WhenDeleteEmpleadoById_ThenVerifyDeletion() {
        int empleadoId = 0;

        empleadoService.deleteEmpleado(empleadoId);

        verify(empleadoJPA, times(1)).deleteById(empleadoId);
    }

    @Test
    void GivenEmpleadoId_WhenAddingEmpleado_ThenVerifyMaxId(){
        List<EmpleadoORM> empleadosEmulados = new ArrayList<>();
        EmpleadoORM empleadoORMA = new EmpleadoORM();
        empleadoORMA.setId(1);
        empleadoORMA.setNombre("mario");
        empleadoORMA.setApellido("castaneda");
        empleadoORMA.setEmail("hai@gmail.com");
        empleadoORMA.setTelefono("1010101010");
        empleadoORMA.setHabilidades(new ArrayList<>());
        empleadoORMA.setFormacionAcademica(new ArrayList<>());
        empleadoORMA.setHistorialLaboral(new ArrayList<>());

        empleadosEmulados.add(empleadoORMA);

        when(empleadoJPA.findAll()).thenReturn(empleadosEmulados);

        EmpleadoORM newEmpleado = new EmpleadoORM();
        newEmpleado.setNombre("Pepito");
        newEmpleado.setApellido("Perez");
        newEmpleado.setEmail("hai@gmail.com");
        newEmpleado.setTelefono("1010101010");
        newEmpleado.setHabilidades(new ArrayList<>());
        newEmpleado.setFormacionAcademica(new ArrayList<>());
        newEmpleado.setHistorialLaboral(new ArrayList<>());

        empleadoService.addEmpleado(newEmpleado);

        Assertions.assertEquals(2, newEmpleado.getId());
        verify(empleadoJPA).save(newEmpleado);
    }




    @Test
    void Given_ModifiedEmpleado_When_UpdatingEmpleado_Then_ReturnUpdatedEmpleado(){
        EmpleadoORM empleadoExistente = new EmpleadoORM();
        empleadoExistente.setId(1);
        empleadoExistente.setNombre("Pablo");

        EmpleadoORM updatedEmpleado = new EmpleadoORM();
        updatedEmpleado.setId(1);
        updatedEmpleado.setNombre("Petruccio");

        when(empleadoJPA.findById(1)).thenReturn(Optional.of(empleadoExistente));
        when(empleadoJPA.save(empleadoExistente)).thenReturn(updatedEmpleado);

        EmpleadoORM result = empleadoService.updateEmpleado(updatedEmpleado);

        assertEquals("Petruccio", result.getNombre());
        verify(empleadoJPA).findById(1);
        verify(empleadoJPA).save(empleadoExistente);
    }




}
