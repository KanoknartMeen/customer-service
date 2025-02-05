package com.example.customerservice.controller;

import com.example.customerservice.model.request.CustomerRequest;
import com.example.customerservice.model.response.CustomerResponse;
import com.example.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    CustomerResponse customerResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerResponse = new CustomerResponse(1L, "firstname", "lastname", "0854444444", "email@mail.com");
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(customerResponse));
    }

    @Test
    void getAllCustomersReturnsListOfCustomers() {
        List<CustomerResponse> customers = List.of(customerResponse);
        when(customerService.getAllCustomers()).thenReturn(customers);

        ResponseEntity<List<CustomerResponse>> response = customerController.getAllCustomers();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(customers, response.getBody());
    }

    @Test
    void getCustomerByIdReturnsCustomer() {
        ResponseEntity<CustomerResponse> response = customerController.getCustomerById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(customerResponse, response.getBody());
    }

    @Test
    void getCustomerByIdReturnsNotFound() {
        when(customerService.getCustomerById(1L)).thenReturn(Optional.empty());

        ResponseEntity<CustomerResponse> response = customerController.getCustomerById(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createCustomerReturnsOk() {
        CustomerRequest customerRequest = new CustomerRequest(1L, "firstname", "lastname", "0854444444", "email@mail.com");
        when(customerService.createCustomer(customerRequest)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(customerRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(customerResponse, response.getBody());
    }

    @Test
    void createCustomerReturnsBadRequestOnException() {
        CustomerRequest customerRequest = new CustomerRequest(1L, "firstname", "lastname", "0854444444", "email@mail.com");
        doThrow(new RuntimeException()).when(customerService).createCustomer(customerRequest);

        ResponseEntity<CustomerResponse> response = customerController.createCustomer(customerRequest);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateCustomerReturnsOk() {
        CustomerRequest customerRequest = new CustomerRequest(1L, "firstname", "lastname", "0854444444", "email@mail.com");
        when(customerService.updateCustomer(1L, customerRequest)).thenReturn(customerResponse);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(1L, customerRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(customerResponse, response.getBody());
    }

    @Test
    void updateCustomerReturnsBadRequestOnException() {
        CustomerRequest customerRequest = new CustomerRequest(1L, "firstname", "lastname", "0854444444", "email@mail.com");
        doThrow(new RuntimeException()).when(customerService).updateCustomer(1L, customerRequest);

        ResponseEntity<CustomerResponse> response = customerController.updateCustomer(1L, customerRequest);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void deleteCustomerByIdReturnsOk() {
        ResponseEntity<Boolean> response = customerController.deleteById(1L);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteCustomerByIdReturnsBadRequestOnException() {
        doThrow(new RuntimeException()).when(customerService).deleteCustomerById(1L);

        ResponseEntity<Boolean> response = customerController.deleteById(1L);

        assertEquals(400, response.getStatusCode().value());
    }

}
