package com.cs.wasselha.interfaces;

import com.cs.wasselha.model.Customer;

import java.io.IOException;
import java.util.ArrayList;

public interface ICustomerDA {

    ArrayList<Customer> getCustomers() throws IOException;
    Customer getCustomer( int id) throws IOException;
    String saveCustomer(Customer customer) throws IOException;

}
