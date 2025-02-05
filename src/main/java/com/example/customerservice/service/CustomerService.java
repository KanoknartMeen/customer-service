package com.example.customerservice.service;

import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.model.entity.CustomerEntity;
import com.example.customerservice.model.request.CustomerRequest;
import com.example.customerservice.model.response.CustomerResponse;
import com.example.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<CustomerResponse> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToResponse);
    }

    public CustomerResponse createCustomer(CustomerRequest customer) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstname(customer.getFirstname());
        customerEntity.setLastname(customer.getLastname());
        customerEntity.setPhone(customer.getPhone());
        customerEntity.setEmail(customer.getEmail());
        customerRepository.save(customerEntity);
        return convertToResponse(customerEntity);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest updatedCustomer) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id " + updatedCustomer.getId()));

        customerEntity.setFirstname(updatedCustomer.getFirstname());
        customerEntity.setLastname(updatedCustomer.getLastname());
        customerEntity.setPhone(updatedCustomer.getPhone());
        customerEntity.setEmail(updatedCustomer.getEmail());
        CustomerEntity updatedCustomerEntity = customerRepository.save(customerEntity);
        return convertToResponse(updatedCustomerEntity);
    }

    public boolean deleteCustomerById(Long id) {
               if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
        return true;
    }

    private CustomerResponse convertToResponse(CustomerEntity customer) {
        return new CustomerResponse(customer.getId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getPhone(),
                customer.getEmail());
    }
}