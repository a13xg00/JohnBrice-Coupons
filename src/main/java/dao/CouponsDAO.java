package dao;

import entities.Coupon;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.util.ArrayList;

public interface CouponsDAO {
    Coupon addCoupon(Coupon coupon) throws ExistException;
    Coupon updateCoupon(Coupon coupon) throws NotExistException;
    Coupon deleteCoupon(long couponId) throws NotExistException;
    ArrayList<Coupon> getAllCoupons();
    Coupon getCouponById(long couponId) throws  NotExistException;
    void addCouponPurchase(long customerId, long couponId);
    void deleteCouponPurchase(long customerId, long couponId);
}
