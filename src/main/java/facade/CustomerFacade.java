package facade;

import dao.CustomersDBDAO;
import entities.Customer;

public class CustomerFacade extends ClientFacade {

    private Customer customer;
    private CustomersDBDAO customersDBDAO = new CustomersDBDAO();

    @Override
    public boolean login(String email, String password) {
        Customer customer = customersDBDAO.getCustomer(email, password);
        if (customer == null) { return false; }
        this.customer = customer;
        return true;
    }


}
