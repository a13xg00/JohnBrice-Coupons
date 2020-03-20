package run;

import facade.AdminFacade;
import facade.ClientFacade;
import facade.CompanyFacade;
import job.CouponExpirationDailyJob;
import loginManager.ClientType;
import loginManager.LoginManager;

public class Main {
    public static void main(String[] args) throws InterruptedException {

//        LoginManager loginManager = LoginManager.getInstance();
//        AdminFacade facade = (AdminFacade) loginManager.login("admin@admin","admin", ClientType.ADMINISTRATOR);
//        CompanyFacade companyFacade = (CompanyFacade) loginManager.login("some@mail","password", ClientType.COMPANY);

        CouponExpirationDailyJob couponExpirationDailyJob = new CouponExpirationDailyJob();
        Thread couponExpirationDailyJobThread = new Thread(couponExpirationDailyJob);

        couponExpirationDailyJobThread.start();

    }
}
