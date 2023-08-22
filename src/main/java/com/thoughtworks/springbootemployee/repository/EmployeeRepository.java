package com.thoughtworks.springbootemployee.repository;

import com.thoughtworks.springbootemployee.exception.EmployeeNotFoundException;
import com.thoughtworks.springbootemployee.exception.EmployeeValidationException;
import com.thoughtworks.springbootemployee.model.Employee;
import com.thoughtworks.springbootemployee.utility.EmployeeValidator;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                .orElseThrow(EmployeeNotFoundException::new);
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

            return newEmployee;
        } catch (EmployeeValidationException ex) {
            throw new EmployeeValidationException(ex.getMessage());
        }
    }

    public Employee updateEmployee(Employee employeeToBeUpdated) {
        Employee employeeToUpdate = employees.stream()
                .filter(employee -> employee.getId().equals(employeeToBeUpdated.getId()))
                .findFirst()
                .orElse(null);

        if (employeeToUpdate != null) {
            employeeToUpdate.setName(employeeToBeUpdated.getName());
            employeeToUpdate.setGender(employeeToBeUpdated.getGender());
            employeeToUpdate.setAge(employeeToBeUpdated.getAge());
            return employeeToUpdate;
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + employeeToBeUpdated.getId());
        }
    }

    public void deleteEmployee(Long id) {
        Optional<Employee> employeeToDelete = employees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst();

        if (employeeToDelete.isPresent()) {
            employees.remove(employeeToDelete.get());
        } else {
            throw new EmployeeNotFoundException("Employee not found with ID: " + id);
        }
    }

    public List<Employee> listByPage(Long pageNumber, Long pageSize) {
        return employees.stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }
}
