package dao;

import entities.Company;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.util.ArrayList;

public interface CompaniesDAO {
    boolean isCompanyExists(String email, String password);
    Company addCompany(Company company) throws ExistException;
    Company updateCompany(Company company) throws NotExistException;
    Company deleteCompany(long companyId) throws NotExistException;
    ArrayList<Company> getAllCompanies();
    Company getCompanyById(long companyId) throws NotExistException;
}
