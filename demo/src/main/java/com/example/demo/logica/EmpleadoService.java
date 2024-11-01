package com.example.demo.logica;

import com.example.demo.bd.EmpleadoORM;
import com.example.demo.bd.EmpleadoJPA;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoJPA repository;

    public boolean addEmpleado(EmpleadoORM empleado){
        if (validateEmpleado(empleado)){
            empleado.setId(getMaxId(findAllEmpleados())+1);
            repository.save(empleado);

            invokeLambdaApi(empleado.getNombre(), empleado.getApellido(), empleado.getEmail(), empleado.getTelefono(), "mateogonzalezcano04@gmail.com");
            return true;
        }
            return false;
    }

    private void invokeLambdaApi(String nombre, String apellido, String email, String telefono, String recipient) {
        try {
            URL url = new URL("https://gi6tx90uk0.execute-api.us-east-2.amazonaws.com/default/Welcome");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crea el payload para enviar la información en JSON
            String jsonInputString = String.format(
                    "{\"nombre\": \"%s\", \"apellido\": \"%s\", \"recipient\": \"%s\", \"email\": \"%s\", \"telefono\": \"%s\"}",
                    nombre, apellido, recipient, email, telefono);

            // Envía el payload
            System.out.println(connection.getOutputStream());
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            // Lee la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Respuesta de Lambda API: " + responseCode);
            } else {
                System.err.println("Error en la respuesta de Lambda API: " + responseCode);
            }

        } catch (Exception e) {
            System.err.println("Error al invocar la API de Lambda: " + e.getMessage());
        }
    }

    public List<EmpleadoORM> findAllEmpleados(){
        return repository.findAll();
    }

    public EmpleadoORM getEmpleadoById(Integer EmpleadoId){
        return repository.findById(EmpleadoId).get();
    }

    public EmpleadoORM getEmpleadoByEmail(String EmpleadoEmail){
        return repository.findByEmail(EmpleadoEmail);
    }

    public EmpleadoORM updateEmpleado(EmpleadoORM empleadoRequest){
        EmpleadoORM empleadoActual = repository.findById(empleadoRequest.getId()).get();
        empleadoActual.setNombre(empleadoRequest.getNombre());
        empleadoActual.setApellido(empleadoRequest.getApellido());
        empleadoActual.setEmail(empleadoRequest.getEmail());
        empleadoActual.setTelefono(empleadoRequest.getTelefono());
        empleadoActual.setHabilidades(empleadoRequest.getHabilidades());
        empleadoActual.setFormacionAcademica(empleadoRequest.getFormacionAcademica());
        empleadoActual.setHistorialLaboral(empleadoRequest.getHistorialLaboral());
        return repository.save(empleadoActual);
    }

    public String deleteEmpleado(Integer empleadoId){
        repository.deleteById(empleadoId);
        return "Empleado "+ empleadoId +" eliminado exitosamente";
    }

    public Integer getMaxId(List<EmpleadoORM> listaEmpleados){
        int maxId = 0;
        for (EmpleadoORM empleado : listaEmpleados){
                if (empleado.getId() > maxId){
                    maxId = empleado.getId();
                }
        }
        if (listaEmpleados.isEmpty()){
            return 0;
        }
        return maxId;
    }

    private Boolean validateEmpleado(EmpleadoORM empleado){
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<EmpleadoORM>> violations = validator.validate(empleado);
        log.info(String.valueOf(violations.isEmpty()));
        return violations.isEmpty();
    }

}
