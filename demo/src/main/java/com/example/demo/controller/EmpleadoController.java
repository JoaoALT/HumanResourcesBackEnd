package com.example.demo.controller;

import com.example.demo.bd.EmpleadoORM;
import com.example.demo.logica.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {


    @Autowired
    private EmpleadoService service;

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean crearEmpleado(@RequestBody EmpleadoORM empleado){
        service.addEmpleado(empleado);
        return true;
    }

    @CrossOrigin
    @GetMapping
    public List<EmpleadoORM> getAllEmpleados(){
        return service.findAllEmpleados();
    }

    @CrossOrigin
    @GetMapping("/{empleadoId}")
    public EmpleadoORM getEmpleadoById(@PathVariable Integer empleadoId){
        return service.getEmpleadoById(empleadoId);
    }

    @CrossOrigin
    @GetMapping("/email/{empleadoEmail}")
    public EmpleadoORM getEmpleadoByEmail(@PathVariable String empleadoEmail){
        return service.getEmpleadoByEmail(empleadoEmail);
    }

    @CrossOrigin
    @PutMapping("/{empleadoId}")
    public EmpleadoORM updateEmpleado(@RequestBody EmpleadoORM empleado) throws IOException, TimeoutException {
        return service.updateEmpleado(empleado);
    }

    @CrossOrigin
    @DeleteMapping("/{empleadoId}")
    public String deleteEmpleado(@PathVariable Integer empleadoId){
        return service.deleteEmpleado(empleadoId);
    }

}

