package job;

import dao.CouponsDBDAO;
import entities.Coupon;
import exeptions.NotExistException;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class CouponExpirationDailyJob implements Runnable {

    private CouponsDBDAO couponsDBDAO;
    private boolean quit = false;


    public CouponExpirationDailyJob() {
        this.couponsDBDAO = new CouponsDBDAO();
    }

    @Override
    public void run() {
        while (!quit) {
            try {
                for (Coupon coupon: couponsDBDAO.getAllCoupons()) {
                    if (coupon.getEndDate().isBefore(LocalDate.now())){
                        try {
                            couponsDBDAO.deleteCoupon(coupon.getId());
                            couponsDBDAO.deletePurchaseByCouponId(coupon.getId());
                        } catch (NotExistException e) {
                            e.printStackTrace();
                        }
                    }
                }
                TimeUnit.DAYS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void stop(){
        quit = true;
    }

}
