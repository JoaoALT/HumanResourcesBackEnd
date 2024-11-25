package com.example.demo.logica;

import com.example.demo.bd.EmpleadoORM;
import com.example.demo.bd.EmpleadoJPA;
import com.example.demo.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoJPA repository;

    public boolean addEmpleado(EmpleadoORM empleado){
        if (validateEmpleado(empleado)){
            empleado.setId(getMaxId(findAllEmpleados())+1);
            repository.save(empleado);
            return true;
        }
            return false;
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

    public EmpleadoORM updateEmpleado(EmpleadoORM empleadoRequest) throws IOException, TimeoutException {

        EmpleadoORM empleadoActual = repository.findById(empleadoRequest.getId()).get();

        alertaRabbitmq(empleadoActual, empleadoRequest);

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


    private void alertaRabbitmq(EmpleadoORM empleadoActual, EmpleadoORM empleadoRequest) throws IOException, TimeoutException {

        ArrayList<String> espaciosActualizados = new ArrayList<>();

        if(!Objects.equals(empleadoActual.getNombre(), empleadoRequest.getNombre())){
            espaciosActualizados.add(empleadoRequest.getNombre());
        }

        if(!empleadoActual.getApellido().equals(empleadoRequest.getApellido())){
            espaciosActualizados.add(empleadoRequest.getApellido());
        }

        if(!Objects.equals(empleadoActual.getEmail(), empleadoRequest.getEmail())){
            espaciosActualizados.add(empleadoRequest.getEmail());
        }

        if(!Objects.equals(empleadoActual.getTelefono(), empleadoRequest.getTelefono())){
            espaciosActualizados.add(empleadoRequest.getTelefono());
        }

        if(!Objects.equals(empleadoActual.getHabilidades(), empleadoRequest.getHabilidades())){
            espaciosActualizados.add(empleadoRequest.getHabilidades().toString());
        }

        if(!Objects.equals(empleadoActual.getFormacionAcademica(), empleadoRequest.getFormacionAcademica())){
            espaciosActualizados.add(empleadoRequest.getFormacionAcademica().toString());
        }

        if(!Objects.equals(empleadoActual.getHistorialLaboral(), empleadoRequest.getHistorialLaboral())){
            espaciosActualizados.add(empleadoRequest.getHistorialLaboral().toString());
        }


        Producer.sendMessage(
                "Empleado actualizado"+espaciosActualizados+

                "Empleado anterior: "+empleadoActual.getId().toString() +" "+ empleadoActual.getNombre() +" "+ empleadoActual.getApellido() +" "+ empleadoActual.getEmail() +" "+ empleadoActual.getTelefono() +" "+ empleadoActual.getHabilidades() +" "+ empleadoActual.getFormacionAcademica() +" "+ empleadoActual.getHistorialLaboral()
        +" "
        );
        invokeLambdaApi("evento");
    }
    private void invokeLambdaApi(String evento) {
        try {
            URL url = new URL("https://gi6tx90uk0.execute-api.us-east-2.amazonaws.com/default/Welcome");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crear un mensaje simple para enviar solo el evento
            String jsonInputString = String.format("{\"evento\": \"%s\"}", evento);

            // Enviar el mensaje
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leer la respuesta
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

}
