package dao;

import connections.ConnectionPool;
import entities.Coupon;
import entities.Customer;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.sql.*;
import java.util.ArrayList;

public class CustomersDBDAO implements CustomersDAO {

    private Connection connection;
    private  CouponsDBDAO coupons = new CouponsDBDAO();

    public CustomersDBDAO() {
        ConnectionPool pool = ConnectionPool.getInstance();
        connection = pool.getConnection();
    }

    private Customer buildCustomer(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(1);
        String firstName = resultSet.getString(2);
        String lastName = resultSet.getString(3);
        String email = resultSet.getString(4);
        String password = resultSet.getString(5);

        return new Customer(id, firstName, lastName, email, password, getCustomerCoupons(id));
    }

    private ArrayList<Coupon> getCustomerCoupons(long customerId){
        ArrayList<Coupon> customerCoupons = new ArrayList<>();
        String sql = "SELECT COUPON_ID FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,customerId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                long couponId = resultSet.getLong(1);
                customerCoupons.add(coupons.getCouponById(couponId));
            }
        }catch (SQLException | NotExistException e) {
            e.printStackTrace();
        }
        return customerCoupons;
    }

    private PreparedStatement buildCustomerPSTMT(PreparedStatement pstmt, Customer customer) throws SQLException {
        pstmt.setString(1, customer.getFirstName());
        pstmt.setString(2, customer.getLastName());
        pstmt.setString(3, customer.getEmail());
        pstmt.setString(4, customer.getPassword());
        return pstmt;
    }

    @Override
    public boolean isCustomerExists(String email, String password) {
        Customer customer = null;
        String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1,email);
            pstmt.setString(2,password);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                customer = buildCustomer(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if (customer == null){
            return false;
        }

        return true;
    }

    @Override
    public Customer addCustomer(Customer customer) throws ExistException {
        String sql = "INSERT INTO CUSTOMERS(FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (?, ?, ?, ?)";
        if (!isCustomerExists(customer.getEmail(), customer.getPassword())){
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                buildCustomerPSTMT(pstmt, customer);
                pstmt.executeUpdate();
                ResultSet resultSet = pstmt.getGeneratedKeys();

                if (resultSet.next()) {
                    customer.setId(resultSet
                            .getLong(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            throw new ExistException("Customer: " + customer.getEmail() + " already exist!" );
        }
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer) throws NotExistException {
        String sql = "UPDATE CUSTOMERS SET FIRST_NAME = ?, LAST_NAME = ?, EMAIL = ?, PASSWORD = ? WHERE ID = ?";
        if (getCustomerById(customer.getId()) != null){
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                buildCustomerPSTMT(pstmt, customer);
                pstmt.setLong(5, customer.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return customer;
    }

    @Override
    public Customer deleteCustomer(long customerId) throws NotExistException {
        Customer customer = getCustomerById(customerId);
        String sql = "DELETE FROM CUSTOMERS WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1,customerId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public ArrayList<Customer> getAllCustomers() {
        ArrayList<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMERS";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                customerList.add(buildCustomer(resultSet));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }

    @Override
    public Customer getCustomerById(long customerId) throws NotExistException {
        Customer customer = null;
        String sql = "SELECT * FROM CUSTOMERS WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,customerId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                customer = buildCustomer(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if (customer == null){
            throw new NotExistException("Customer with id: " +  customerId + " not found");
        }
        return customer;

    }
}
