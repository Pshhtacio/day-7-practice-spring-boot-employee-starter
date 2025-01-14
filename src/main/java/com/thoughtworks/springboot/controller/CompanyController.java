package com.thoughtworks.springboot.controller;

import com.thoughtworks.springboot.exception.CompanyNotFoundException;
import com.thoughtworks.springboot.exception.CompanyValidationException;
import com.thoughtworks.springboot.model.Company;
import com.thoughtworks.springboot.repository.CompanyRepository;
import com.thoughtworks.springboot.exception.EmployeeValidationException;
import com.thoughtworks.springboot.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO Use CompanyService instead of CompanyRepository
@RequestMapping(path = "companies")
@RestController
public class CompanyController {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public ResponseEntity<List<Company>> listAllCompanies() {
        List<Company> companies = companyRepository.listAll();
        return ResponseEntity.ok(companies);
    }

    @GetMapping(path = "/{id}")
    public Company findCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id);
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public List<Company> findCompaniesByPage(@RequestParam Long pageNumber, Long pageSize) {
        return companyRepository.listByPage(pageNumber, pageSize);
    }

    @GetMapping("/{companyId}/employees")
    public ResponseEntity<List<Employee>> getEmployeesByCompanyId(@PathVariable Long companyId) {
        try {
            List<Employee> employees = companyRepository.getEmployeesByCompanyId(companyId);
            return ResponseEntity.ok(employees);
        } catch (CompanyNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Object> addCompany(@RequestBody Company company) {
        try {
            //TODO Handle the failed adding in the CompanyService
            Company addedCompany = companyRepository.addCompany(company);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCompany);
            //TODO Wrong exception being caught
        } catch (EmployeeValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCompanyById(@PathVariable Long id, @RequestBody Company updatedCompany) {
        try {
            //TODO Handle validation handling in Company Service
            Company updated = companyRepository.updateCompanyById(id, updatedCompany);
            if (updated != null) {
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (CompanyValidationException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (CompanyNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompanyById(@PathVariable Long id) {
        try {
            //TODO Handle validation handling in Company Service
            companyRepository.deleteCompanyById(id);
            return ResponseEntity.noContent().build();
        } catch (CompanyNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
