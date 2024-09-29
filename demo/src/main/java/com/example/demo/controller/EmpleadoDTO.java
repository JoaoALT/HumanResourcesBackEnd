package com.example.demo.controller;


import java.util.ArrayList;

public record EmpleadoDTO(Integer id, String nombre, String apellido, String email, String telefono, ArrayList<String> habilidades, ArrayList<String> formacionAcademica, ArrayList<String> historialLaboral) {
}
