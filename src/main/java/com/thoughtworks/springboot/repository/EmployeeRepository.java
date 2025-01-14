package com.thoughtworks.springboot.repository;

import com.thoughtworks.springboot.exception.EmployeeNotFoundException;
import com.thoughtworks.springboot.exception.EmployeeValidationException;
import com.thoughtworks.springboot.model.Employee;
import com.thoughtworks.springboot.utility.EmployeeValidator;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EmployeeRepository {
    private static final List<Employee> employees = new ArrayList<>();
    private static final Long STARTING_ID_MINUS_ONE = 0L;

    static {
        employees.add(new Employee(1L, "Ilnear", 42, "Male", 10000));
        employees.add(new Employee(2L, "Ilfar", 42, "Female", 20000));
        employees.add(new Employee(3L, "Ilclose", 42, "Male", 3000));
        employees.add(new Employee(4L, "Ilalmostthere", 42, "Female", 5000));
        employees.add(new Employee(5L, "Ilfaraway", 42, "Male", 14500));
    }

    private Long generateNextId() {
        return employees.stream()
                .mapToLong(Employee::getId)
                .max()
                .orElse(STARTING_ID_MINUS_ONE) + 1;
    }

    public List<Employee> listAll() {
        return employees;
    }

    public Employee findById(Long id) {
        return employees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    public List<Employee> findByGender(String gender) {
        return employees.stream()
                .filter(employee -> employee.getGender().equalsIgnoreCase(gender))
                .collect(Collectors.toList());
    }

    public Employee addEmployee(Employee employee) {
        try {
            Long id = generateNextId();
            EmployeeValidator.validateEmployee(employee);

            Employee newEmployee = new Employee(id,
                    employee.getName(),
                    employee.getAge(),
                    employee.getGender(),
                    employee.getSalary());

            employees.add(newEmployee);
            //TODO Handle Business Logic and Validation in service, reference day8-Spring Boot.pdf page 8
            return newEmployee;
        } catch (EmployeeValidationException ex) {
            throw new EmployeeValidationException(ex.getMessage());
        }
    }

    public Employee updateEmployee(Employee updatedEmployee) throws EmployeeValidationException {
        EmployeeValidator.validateEmployee(updatedEmployee);

        Employee employeeToUpdate = employees.stream()
                .filter(employee -> employee.getId().equals(updatedEmployee.getId()))
                .findFirst()
                .orElse(null);
        //TODO Handle Business Logic and Validation in service, reference day8-Spring Boot.pdf page 8
        if (employeeToUpdate != null) {
            employeeToUpdate.setAge(updatedEmployee.getAge());
            employeeToUpdate.setSalary(updatedEmployee.getSalary());
            employeeToUpdate.setIsActive(updatedEmployee.getIsActive());
            return employeeToUpdate;
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + updatedEmployee.getId());
        }
    }

    public List<Employee> listByPage(Long pageNumber, Long pageSize) {
        return employees.stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    public void cleanUpEmployeeData() {
        employees.clear();
    }

    public Employee insert(Employee employee) {
        Long id = generateNextId();
        Employee newEmployee = new Employee(id, employee.getName(), employee.getAge(), employee.getGender(), employee.getSalary());
        employees.add(newEmployee);
        return newEmployee;
    }
}
