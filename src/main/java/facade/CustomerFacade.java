package facade;

import dao.CustomersDBDAO;

public class CustomerFacade extends ClientFacade {

    private CustomersDBDAO customersDBDAO = new CustomersDBDAO();

    @Override
    public boolean login(String email, String password) {
        return customersDBDAO.isCustomerExists(email, password);
    }
}
