package com.example.customerservice.service;

import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.model.entity.CustomerEntity;
import com.example.customerservice.model.request.CustomerRequest;
import com.example.customerservice.model.response.CustomerResponse;
import com.example.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    CustomerEntity customer1;
    CustomerEntity customer2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        customer1 = new CustomerEntity(1L, "first", "lastname1", "0111111111", "one@mail.com");
        customer2 = new CustomerEntity(2L, "second", "lastname2", "02222222222", "two@mail.com");
    }

    @Test
    void getAllCustomersReturnsListOfCustomerResponses() {
        List<CustomerEntity> customerEntities = List.of(customer1, customer2);
        when(customerRepository.findAll()).thenReturn(customerEntities);

        List<CustomerResponse> customerResponses = customerService.getAllCustomers();

        assertEquals(2, customerResponses.size());
        assertEquals("first", customerResponses.get(0).getFirstname());
        assertEquals("second", customerResponses.get(1).getFirstname());
    }

    @Test
    void getCustomerByIdReturnsCustomerResponse() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

        Optional<CustomerResponse> customerResponse = customerService.getCustomerById(1L);

        assertTrue(customerResponse.isPresent());
        assertEquals("first", customerResponse.get().getFirstname());
    }

    @Test
    void getCustomerByIdReturnsEmptyForNonExistentId() {
        when(customerRepository.findById(10L)).thenReturn(Optional.empty());

        Optional<CustomerResponse> customerResponse = customerService.getCustomerById(10L);

        assertFalse(customerResponse.isPresent());
    }

    @Test
    void createCustomerReturnsCustomerResponse() {
        CustomerRequest customerRequest = new CustomerRequest(0L, "first", "lastname1", "0111111111", "one@mail.com");
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customer1);

        CustomerResponse customerResponse = customerService.createCustomer(customerRequest);

        assertEquals("first", customerResponse.getFirstname());
        assertEquals("lastname1", customerResponse.getLastname());
    }

    @Test
    void updateCustomerReturnsUpdatedCustomerResponse() {
        CustomerRequest updatedCustomerRequest = new CustomerRequest(1L,"first", "lastname1", "0111111111", "one@mail.com");
        CustomerEntity updatedCustomerEntity = new CustomerEntity(1L,"first", "lastnameOne", "0111111111", "one@mail.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
        when(customerRepository.save(customer1)).thenReturn(updatedCustomerEntity);

        CustomerResponse customerResponse = customerService.updateCustomer(1L, updatedCustomerRequest);

        assertEquals("first", customerResponse.getFirstname());
        assertEquals("lastnameOne", customerResponse.getLastname());
    }

    @Test
    void updateCustomerThrowsCustomerNotFoundException() {
        CustomerRequest updatedCustomerRequest = new CustomerRequest(11L, "Three", "Smith", "1234567890", "Three.smith@example.com");
        when(customerRepository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(11L, updatedCustomerRequest);
        });
    }

    @Test
    void deleteCustomerByIdReturnsTrue() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(1L);

        boolean result = customerService.deleteCustomerById(1L);

        assertTrue(result);
    }

    @Test
    void deleteCustomerByIdThrowsCustomerNotFoundException() {
        when(customerRepository.existsById(1L)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomerById(1L);
        });
    }


}