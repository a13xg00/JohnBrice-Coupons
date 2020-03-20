package facade;

import dao.CompaniesDBDAO;
import dao.CouponsDBDAO;
import entities.Category;
import entities.Company;
import entities.Coupon;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.util.ArrayList;

public class CompanyFacade extends ClientFacade {

    private Company company;
    private CompaniesDBDAO companiesDBDAO;
    private CouponsDBDAO couponsDBDAO;

    public CompanyFacade() {
        this.companiesDBDAO = new CompaniesDBDAO();
        this.couponsDBDAO = new CouponsDBDAO();
    }

    private boolean isCompanyCoupon(Coupon coupon){ return coupon.getCompanyId() == company.getId(); }
    private boolean isTitleNotExist(Coupon coupon){
        for (Coupon companyCoupon: getCompanyCoupons()
             ) {
            if (companyCoupon.getTitle().equals(coupon.getTitle())){ return false; }
        }
        return true;
    }

    @Override
    public boolean login(String email, String password) {
        Company company = companiesDBDAO.getCompany(email,password);
        if (company == null){ return false; }
        this.company = company;
        return true;
    }

    public void addCoupon(Coupon coupon) throws ExistException {
        if (isCompanyCoupon(coupon) && isTitleNotExist(coupon)){ couponsDBDAO.addCoupon(coupon); }
    }

    public void updateCoupon(Coupon coupon) throws NotExistException {
        if (isCompanyCoupon(coupon)){ couponsDBDAO.updateCoupon(coupon); }
    }

    public void deleteCoupon (long couponId) throws NotExistException {
        if (isCompanyCoupon(couponsDBDAO.getCouponById(couponId))){
            couponsDBDAO.deletePurchaseByCouponId(couponId);
            couponsDBDAO.deleteCoupon(couponId);
        }
    }

    public ArrayList<Coupon> getCompanyCoupons(){
        return company.getCoupons();
    }

    public ArrayList<Coupon> getCompanyCoupons(Category category){
        ArrayList<Coupon> coupons = new ArrayList<>();
        for (Coupon coupon: getCompanyCoupons()
             ) {
            if (coupon.getCategory().equals(category)) { coupons.add(coupon); }
        }
        return coupons;
    }

    public ArrayList<Coupon> getCompanyCoupons(double maxPrice){
        ArrayList<Coupon> coupons = new ArrayList<>();
        for (Coupon companyCoupon: getCompanyCoupons()
             ) {
            if (companyCoupon.getPrice() < maxPrice ) { coupons.add(companyCoupon); }
        }
        return coupons;
    }

    public Company getCompany(){
        return company;
    }


}
