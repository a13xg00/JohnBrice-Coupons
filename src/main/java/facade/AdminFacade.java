package facade;

import dao.CompaniesDBDAO;
import dao.CustomersDBDAO;
import entities.Company;
import entities.Customer;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.util.ArrayList;

public class AdminFacade extends ClientFacade {

    private CompaniesDBDAO companiesDBDAO;
    private CustomersDBDAO customersDBDAO;

    public AdminFacade() {
        this.companiesDBDAO = new CompaniesDBDAO();
        this.customersDBDAO = new CustomersDBDAO();
    }

    @Override
    public boolean login(String email, String password) {
        return email.equals("admin@admin") && password.equals("admin");
    }

    public void addCompany(Company company) throws ExistException {
        companiesDBDAO.addCompany(company);
    }

    public void updateCompany(Company company) throws NotExistException {
        companiesDBDAO.updateCompany(company);
    }

    public void deleteCompany(long companyId) throws NotExistException {
        companiesDBDAO.deleteCompany(companyId);
    }

    public ArrayList<Company> getAllCompanies(){
        return companiesDBDAO.getAllCompanies();
    }

    public Company getOneCompany(long companyId) throws NotExistException {
        return companiesDBDAO.getCompanyById(companyId);
    }

    public void addCustomer(Customer customer) throws ExistException {
        customersDBDAO.addCustomer(customer);
    }

    public void updateCustomer(Customer customer) throws NotExistException {
        customersDBDAO.updateCustomer(customer);
    }

    public void deleteCustomer(long customerId) throws NotExistException {
        customersDBDAO.deleteCustomer(customerId);
    }

    public ArrayList<Customer> getAllCustomer(){
        return customersDBDAO.getAllCustomers();
    }

    public Customer getOneCustomer(long customerId) throws NotExistException {
        return customersDBDAO.getCustomerById(customerId);
    }


}
