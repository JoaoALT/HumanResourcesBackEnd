package com.example.demo.logica;

import com.example.demo.bd.EmpleadoORM;
import com.example.demo.bd.EmpleadoJPA;
import com.example.demo.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.*;
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

    public EmpleadoORM updateEmpleado(EmpleadoORM empleadoRequest){
        EmpleadoORM empleadoActual = repository.findById(empleadoRequest.getId()).get();
        empleadoActual.setNombre(empleadoRequest.getNombre());
        empleadoActual.setApellido(empleadoRequest.getApellido());
        empleadoActual.setEmail(empleadoRequest.getEmail());
        empleadoActual.setTelefono(empleadoRequest.getTelefono());
        empleadoActual.setHabilidades(empleadoRequest.getHabilidades());
        empleadoActual.setFormacionAcademica(empleadoRequest.getFormacionAcademica());
        empleadoActual.setHistorialLaboral(empleadoRequest.getHistorialLaboral());

        Producer.sendMessage(empleadoRequest.getId().toString() + empleadoRequest.getNombre() + empleadoRequest.getApellido() + empleadoRequest.getEmail() + empleadoRequest.getTelefono() + empleadoRequest.getHabilidades() + empleadoRequest.getFormacionAcademica() + empleadoRequest.getHistorialLaboral()
        );

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
