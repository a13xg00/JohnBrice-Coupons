package dao;

import entities.Customer;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.util.ArrayList;

public interface CustomersDAO {
    boolean isCustomerExists(String email, String password);
    Customer addCustomer(Customer customer) throws ExistException;
    Customer updateCustomer(Customer customer) throws NotExistException;
    Customer deleteCustomer(long customerId) throws NotExistException;
    ArrayList<Customer> getAllCustomers();
    Customer getCustomerById(long customerId) throws NotExistException;
}
