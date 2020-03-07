package dao;

import connections.ConnectionPool;
import entities.Company;
import entities.Coupon;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.sql.*;
import java.util.ArrayList;

public class CompaniesDBDAO implements CompaniesDAO {

    private Connection connection;
    private  CouponsDBDAO coupons = new CouponsDBDAO();

    public CompaniesDBDAO() {
        ConnectionPool pool = ConnectionPool.getInstance();
        connection = pool.getConnection();
    }

    private Company buildCompany(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(1);
        String name = resultSet.getString(2);
        String email = resultSet.getString(3);
        String password = resultSet.getString(4);
        return new Company(id,name,email,password, getCompanyCoupons(id));
    }

    public ArrayList<Coupon> getCompanyCoupons(long companyId){
        ArrayList<Coupon> companyCoupons = new ArrayList<>();
        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,companyId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                companyCoupons.add(coupons.buildCoupon(resultSet));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return companyCoupons;
    }

    private PreparedStatement buildCompanyPSTMT(PreparedStatement pstmt, Company company) throws SQLException {
        pstmt.setString(1, company.getName());
        pstmt.setString(2, company.getEmail());
        pstmt.setString(3, company.getPassword());
        return pstmt;
    }

    public Company getCompany(String email, String password){
        Company company = null;
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ? AND PASSWORD = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1,email);
            pstmt.setString(2,password);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                company = buildCompany(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return company;
    }
    @Override
    public boolean isCompanyExists(String email, String password) {
        return getCompany(email, password) != null;
    }

    @Override
    public Company addCompany(Company company) throws ExistException {
        String sql = "INSERT INTO COMPANIES(NAME, EMAIL, PASSWORD) VALUES (?, ?, ?)";
        if (!isCompanyExists(company.getEmail(), company.getPassword())){
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                buildCompanyPSTMT(pstmt, company);
                pstmt.executeUpdate();
                ResultSet resultSet = pstmt.getGeneratedKeys();
                if (resultSet.next()) {
                    company.setId(resultSet
                            .getLong(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new ExistException("Company: " + company.getId() + " already exist!" );
        }
        return company;
    }

    @Override
    public Company updateCompany(Company company) throws NotExistException {
        String sql = "UPDATE COMPANIES SET NAME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?";
        if (getCompanyById(company.getId()) != null){
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                buildCompanyPSTMT(pstmt, company);
                pstmt.setLong(4, company.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return company;
    }

    @Override
    public Company deleteCompany(long companyId) throws NotExistException {
        Company company = getCompanyById(companyId);
        String sql = "DELETE FROM COMPANIES WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1,companyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return company;
    }

    @Override
    public ArrayList<Company> getAllCompanies() {
        ArrayList<Company> companyList = new ArrayList<>();
        String sql = "SELECT * FROM COMPANIES";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                companyList.add(buildCompany(resultSet));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return companyList;
    }

    @Override
    public Company getCompanyById(long companyId) throws NotExistException {
        Company company = null;
        String sql = "SELECT * FROM COMPANIES WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,companyId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                company = buildCompany(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if (company == null){
            throw new NotExistException("Company with id: " +  companyId + " not found");
        }
        return company;
    }

}
