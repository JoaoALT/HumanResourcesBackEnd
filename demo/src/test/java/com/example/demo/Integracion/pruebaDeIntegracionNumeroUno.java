package com.example.demo.Integracion;


import com.example.demo.bd.EmpleadoORM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "h2")
public class pruebaDeIntegracionNumeroUno{

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void PrimeraIntegracion() {
        EmpleadoORM empleadoORM = new EmpleadoORM();
        empleadoORM.setId(1);
        empleadoORM.setNombre("mario");
        empleadoORM.setApellido("castaneda");
        empleadoORM.setEmail("hai@gmail.com");
        empleadoORM.setTelefono("1010101010");
        empleadoORM.setHabilidades(new ArrayList<>());
        empleadoORM.setFormacionAcademica(new ArrayList<>());
        empleadoORM.setHistorialLaboral(new ArrayList<>());


        ResponseEntity<Boolean> respuestaInsercion = testRestTemplate.postForEntity("/empleados", empleadoORM, Boolean.class);
        Assertions.assertEquals(true , respuestaInsercion.getBody());

        ResponseEntity<List> resultado = testRestTemplate.getForEntity("/empleados", List.class);
        Assertions.assertFalse(Objects.requireNonNull(resultado.getBody()).isEmpty());
        Assertions.assertTrue(resultado.getStatusCode().is2xxSuccessful());

        ResponseEntity<EmpleadoORM> resultadoId = testRestTemplate.getForEntity("/empleados/0", EmpleadoORM.class);
        Assertions.assertNotEquals(null, resultadoId.getBody().getId());
        Assertions.assertTrue(resultadoId.getStatusCode().is2xxSuccessful());

        /* TODO
        ResponseEntity<EmpleadoORM> resultadoEmail = testRestTemplate.getForEntity("/empleados/email/Macaco@gmail.com", EmpleadoORM.class);
        Assertions.assertNotEquals(null, resultadoEmail.getBody().getEmail());
        Assertions.assertTrue(resultadoEmail.getStatusCode().is2xxSuccessful());
        */

        EmpleadoORM empleadoORM2 = new EmpleadoORM();
            empleadoORM2.setId(1);
            empleadoORM2.setNombre("Geronimo");
            empleadoORM2.setApellido("Perez");
            empleadoORM2.setEmail("hai@gmail.com");
            empleadoORM2.setTelefono("1010101010");
            empleadoORM2.setHabilidades(new ArrayList<>());
            empleadoORM2.setFormacionAcademica(new ArrayList<>());
            empleadoORM2.setHistorialLaboral(new ArrayList<>());

        testRestTemplate.put("/empleados/1", empleadoORM2);
        EmpleadoORM respuestaUpdateInjection = testRestTemplate.getForEntity("/empleados/1", EmpleadoORM.class).getBody();
        Assertions.assertEquals(empleadoORM2.getNombre(), Objects.requireNonNull(respuestaUpdateInjection).getNombre());



        testRestTemplate.delete("/empleados/1");
        ResponseEntity<EmpleadoORM> respuestaDeleteEmbeeded = testRestTemplate.getForEntity("/empleados/1", EmpleadoORM.class);
        Assertions.assertFalse(respuestaDeleteEmbeeded.getStatusCode().is2xxSuccessful());
    }

}