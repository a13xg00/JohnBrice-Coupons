package facade;

import dao.CouponsDBDAO;
import dao.CustomersDBDAO;
import entities.Category;
import entities.Coupon;
import entities.Customer;
import exeptions.NotExistException;
import exeptions.PurchaseException;

import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerFacade extends ClientFacade {

    private Customer customer;
    private CustomersDBDAO customersDBDAO = new CustomersDBDAO();
    private CouponsDBDAO couponsDBDAO =new CouponsDBDAO();

    @Override
    public boolean login(String email, String password) {
        Customer customer = customersDBDAO.getCustomer(email, password);
        if (customer == null) { return false; }
        this.customer = customer;
        return true;
    }

    private boolean isValidPurchase(Coupon coupon) {
        for (Coupon customerCoupon: customer.getCoupons()) {
            if (coupon.getId() == customerCoupon.getId()){ return false; }
        }
        if (coupon.getAmount() == 0) { return false; }
        if (coupon.getEndDate().isBefore(LocalDate.now())) { return false; }

        return true;
    }
    public void purchaseCoupon(Coupon coupon) throws NotExistException, PurchaseException {
        if (isValidPurchase(coupon)){
            couponsDBDAO.addCouponPurchase(customer.getId(),coupon.getId());
            coupon.setAmount(coupon.getAmount() - 1 );
            couponsDBDAO.updateCoupon(coupon);
            customer.getCoupons().add(coupon);
        } else {
            throw new PurchaseException("Invalid Purchase");
        }
    }

    public ArrayList<Coupon> getCustomerCoupons(){
        return customer.getCoupons();
    }

    public ArrayList<Coupon> getCustomerCoupons(Category category) {
        ArrayList<Coupon> coupons = new ArrayList<>();
        for (Coupon coupon: getCustomerCoupons()
        ) {
            if (coupon.getCategory().equals(category)) { coupons.add(coupon); }
        }
        return coupons;
    }

    public ArrayList<Coupon> getCustomerCoupons(double maxPrice){
        ArrayList<Coupon> coupons = new ArrayList<>();
        for (Coupon coupon: getCustomerCoupons()
        ) {
            if (coupon.getPrice() < maxPrice ) { coupons.add(coupon); }
        }
        return coupons;
    }

    public Customer getCustomerDetails(){
        return customer;
    }
}
