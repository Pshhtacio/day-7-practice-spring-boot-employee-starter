package com.thoughtworks.springboot.controller;

import com.thoughtworks.springboot.exception.EmployeeNotFoundException;
import com.thoughtworks.springboot.exception.EmployeeValidationException;
import com.thoughtworks.springboot.model.Employee;
import com.thoughtworks.springboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping(path = "employees")
@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> listAllEmployees() {
        return employeeService.listAllEmployees();
    }

    @GetMapping(path = "/{id}")
    public Employee findEmployeeById(@PathVariable Long id) {
        return employeeService.findEmployeeById(id);
    }

    @GetMapping(params = {"gender"})
    public List<Employee> findEmployeeByGender(@RequestParam String gender) {
        return employeeService.findEmployeeByGender(gender);
    }

    @PostMapping()
    public ResponseEntity<Object> addEmployee(@RequestBody Employee employee) {
        try {
            //TODO Handle validation handling in Employee Service
            Employee addedEmployee = employeeService.create(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedEmployee);
        } catch (EmployeeValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateEmployeeById(@PathVariable Long id, @RequestBody Employee updatedEmployee) {
        updatedEmployee.setId(id);
        try {
            //TODO Handle validation handling in Employee Service
            Employee updated = employeeService.update(updatedEmployee);

            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable Long id) {
        try {
            //TODO Handle validation handling in Employee Service
            employeeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public List<Employee> findEmployeesByPage(@RequestParam Long pageNumber, @RequestParam Long pageSize) {
        List<Employee> employees = employeeService.findEmployeesByPage(pageNumber, pageSize);
        //TODO Handle validation handling in Employee Service
        if (employees.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No employees found for the specified page.");
        }
        return employees;
    }
}