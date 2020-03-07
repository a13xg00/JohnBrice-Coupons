package facade;

import dao.CompaniesDBDAO;
import dao.CouponsDBDAO;
import dao.CustomersDBDAO;
import entities.Company;
import entities.Coupon;
import entities.Customer;
import exeptions.ExistException;
import exeptions.NotExistException;
import exeptions.PermissionException;

import java.util.ArrayList;

public class AdminFacade extends ClientFacade {

    private CompaniesDBDAO companiesDBDAO;
    private CustomersDBDAO customersDBDAO;
    private CouponsDBDAO couponsDBDAO;

    public AdminFacade() {
        this.couponsDBDAO = new CouponsDBDAO();
        this.companiesDBDAO = new CompaniesDBDAO();
        this.customersDBDAO = new CustomersDBDAO();
    }

    @Override
    public boolean login(String email, String password) {
        return email.equals("admin@admin") && password.equals("admin");
    }

    public void addCompany(Company company) throws ExistException {
        if (isCompanyAddable(company)){
            companiesDBDAO.addCompany(company);
        } else {
            throw new ExistException("Company with this email or password already exist");
        }
    }

    public void updateCompany(Company company) throws NotExistException, PermissionException {
        if (companiesDBDAO.getCompanyById(company.getId()).getName().equals(company.getName())) {
            companiesDBDAO.updateCompany(company);
        } else {
            throw new PermissionException("You have no permissions to change name of the company");
        }
    }

    public void deleteCompany(long companyId) throws NotExistException {
        for (Coupon companyCoupon: companiesDBDAO.getCompanyCoupons(companyId)
             ) {
            couponsDBDAO.deletePurchaseByCouponId(companyCoupon.getId());
            couponsDBDAO.deleteCoupon(companyCoupon.getId());
        }
        companiesDBDAO.deleteCompany(companyId);
    }

    public ArrayList<Company> getAllCompanies(){
        return companiesDBDAO.getAllCompanies();
    }

    public Company getOneCompany(long companyId) throws NotExistException {
        return companiesDBDAO.getCompanyById(companyId);
    }

    public void addCustomer(Customer customer) throws ExistException {
        if (isCustomerAddable(customer)){
            customersDBDAO.addCustomer(customer);
        } else {
            throw new ExistException("Customer with this email already exist");
        }
    }

    public void updateCustomer(Customer customer) throws NotExistException {
        customersDBDAO.updateCustomer(customer);
    }

    public void deleteCustomer(long customerId) throws NotExistException {
        for (Coupon customerCoupon: customersDBDAO.getCustomerCoupons(customerId)
             ) {
            couponsDBDAO.deleteCouponPurchase(customerId, customerCoupon.getCompanyId() );
        }
        customersDBDAO.deleteCustomer(customerId);
    }

    public ArrayList<Customer> getAllCustomers(){
        return customersDBDAO.getAllCustomers();
    }

    public Customer getOneCustomer(long customerId) throws NotExistException {
        return customersDBDAO.getCustomerById(customerId);
    }

    private boolean isCompanyAddable(Company newCompany){
        for (Company company: getAllCompanies()) {
            if (company.getName().equals(newCompany.getName()) || company.getEmail().equals(newCompany.getEmail())){
                return false;
            }
        }
        return true;
    }

    private boolean isCustomerAddable(Customer newCustomer){
        for (Customer customer: getAllCustomers()) {
            if (customer.getEmail().equals(newCustomer.getEmail())){
                return false;
            }
        }
        return true;
    }


}
