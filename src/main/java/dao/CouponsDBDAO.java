package dao;

import connections.ConnectionPool;
import entities.Category;
import entities.Coupon;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CouponsDBDAO implements CouponsDAO {

    private Connection connection;

    public CouponsDBDAO() {
        ConnectionPool pool = ConnectionPool.getInstance();
        connection = pool.getConnection();
    }

    public Coupon buildCoupon(ResultSet resultSet) throws SQLException {
        long couponId = resultSet.getLong(1);
        long companyId = resultSet.getLong(2);
        int categoryId = resultSet.getInt(3);
        String title = resultSet.getString(4);
        String description = resultSet.getString(5);
        LocalDate startDate = resultSet.getDate(6).toLocalDate();
        LocalDate endDate = resultSet.getDate(7).toLocalDate();
        int amount = resultSet.getInt(8);
        double price = resultSet.getDouble(9);
        String image = resultSet.getString(10);

        Category category = getCategoryById(categoryId);

        return new Coupon(couponId,companyId,category,title,description,startDate,endDate,amount,price,image);
    }
// Change getCouponById sql query to use JOIN, and remove it
    private Category getCategoryById(int categoryId){
        String name = null;
        String sql = "SELECT NAME FROM CATEGORIES WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,categoryId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                name = resultSet.getString(1);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return Category.valueOf(name);
    }

    private PreparedStatement buildCouponPSTMT(PreparedStatement pstmt, Coupon coupon) throws SQLException {

        pstmt.setLong(1, coupon.getCompanyId());
        pstmt.setLong(2, coupon.getCategory().getId());;
        pstmt.setString(3, coupon.getTitle());
        pstmt.setString(4, coupon.getDescription());
        pstmt.setDate(5, Date.valueOf(coupon.getStartDate()));
        pstmt.setDate(6, Date.valueOf(coupon.getEndDate()));
        pstmt.setInt(7, coupon.getAmount());
        pstmt.setDouble(8, coupon.getPrice());
        pstmt.setString(9, coupon.getImage());

        return pstmt;

    }

    @Override
    public Coupon addCoupon(Coupon coupon) throws ExistException {

        String sql = "INSERT INTO COUPONS" +
                "(COMPANY_ID, CATEGORY_ID, TITLE, DESCRIPTION, START_DATE, END_DATE, AMOUNT, PRICE, IMAGE)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            buildCouponPSTMT(pstmt,coupon);
            pstmt.executeUpdate();
            ResultSet resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()) {
                coupon.setId(resultSet
                        .getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coupon;
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) throws NotExistException {
        String sql = "UPDATE COUPONS SET " +
                "COMPANY_ID = ?, CATEGORY_ID = ? , TITLE = ?, DESCRIPTION = ?, START_DATE = ?, END_DATE = ?, AMOUNT = ?, PRICE = ?, IMAGE = ?" +
                "WHERE ID = ?";
        if (getCouponById(coupon.getId()) != null){
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                buildCouponPSTMT(pstmt, coupon);
                pstmt.setLong(10, coupon.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return coupon;
    }

    @Override
    public Coupon deleteCoupon(long couponId) throws NotExistException {
        Coupon coupon = getCouponById(couponId);
        String sql = "DELETE FROM COUPONS WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1,couponId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coupon;
    }

    @Override
    public ArrayList<Coupon> getAllCoupons() {
        ArrayList<Coupon> couponList = new ArrayList<>();
        String sql = "SELECT * FROM COUPONS";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                couponList.add(buildCoupon(resultSet));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return couponList;
    }

    @Override
    public Coupon getCouponById(long couponId) throws NotExistException {
        Coupon coupon = null;
        String sql = "SELECT * FROM COUPONS WHERE ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,couponId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                coupon = buildCoupon(resultSet);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if (coupon == null){
            throw new NotExistException("Coupon with id: " +  couponId + " not found");
        }
        return coupon;
    }

    @Override
    public void addCouponPurchase(long customerId, long couponId) {
        String sql = "INSERT INTO CUSTOMERS_VS_COUPONS (CUSTOMER_ID, COUPON_ID) VALUES (?, ?)";
        executeCouponPurchaseOperation(customerId, couponId, sql);
    }

    @Override
    public void deleteCouponPurchase(long customerId, long couponId) {
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?";
        executeCouponPurchaseOperation(customerId, couponId, sql);
    }

    public void deletePurchaseByCouponId(long couponId) {
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE COUPON_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql))  {
            pstmt.setLong(1,couponId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeCouponPurchaseOperation(long customerId, long couponId, String sql){
        try (PreparedStatement pstmt = connection.prepareStatement(sql))  {
            pstmt.setLong(1,customerId);
            pstmt.setLong(2,couponId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
