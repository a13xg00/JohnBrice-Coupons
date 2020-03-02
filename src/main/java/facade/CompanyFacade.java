package facade;

import dao.CompaniesDBDAO;
import entities.Company;

public class CompanyFacade extends ClientFacade {

    private long companyId;
    private CompaniesDBDAO companiesDBDAO;

    public CompanyFacade() {
        this.companiesDBDAO = new CompaniesDBDAO();
    }

    @Override
    public boolean login(String email, String password) {
        Company company = companiesDBDAO.getCompany(email,password);
        if (company == null){
            return false;
        }
        this.companyId = company.getId();
        return true;
    }

    public long getCompanyId() {
        return companyId;
    }
}
