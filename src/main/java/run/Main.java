package run;


import dao.CompaniesDBDAO;
import dao.CouponsDBDAO;
import entities.Category;
import entities.Coupon;
import exeptions.ExistException;
import exeptions.NotExistException;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        CompaniesDBDAO companiesDAO = new CompaniesDBDAO();

        CouponsDBDAO couponsDAO = new CouponsDBDAO();

//        System.out.println(companiesDAO.getAllCompanies());


//        Coupon coupon = new Coupon(1,Category.FOOD,"bsgngb","bgmasbgm",
//                LocalDate.of(2020,2,3),
//                LocalDate.of(2021,2,3),
//                2,244,"fafaf");
//        try {
//            couponsDAO.addCoupon(coupon);
//        } catch (ExistException e) {
//            e.printStackTrace();
//        }



    }
}
