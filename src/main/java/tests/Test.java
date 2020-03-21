package tests;

import connections.ConnectionPool;
import entities.Category;
import entities.Company;
import entities.Coupon;
import entities.Customer;
import exeptions.ExistException;
import exeptions.NotExistException;
import exeptions.PermissionException;
import exeptions.PurchaseException;
import facade.AdminFacade;
import facade.CompanyFacade;
import facade.CustomerFacade;
import job.CouponExpirationDailyJob;
import loginManager.ClientType;
import loginManager.LoginManager;

import java.time.LocalDate;
import java.util.ArrayList;

public class Test {

    public static void testAll(){

        // 1. Starting daily job thread
        CouponExpirationDailyJob couponExpirationDailyJob = new CouponExpirationDailyJob();
        Thread couponExpirationDailyJobThread = new Thread(couponExpirationDailyJob);
        couponExpirationDailyJobThread.start();
        //
        LoginManager loginManager = LoginManager.getInstance();

        // 2. Admin Tests
        AdminFacade admin = (AdminFacade) loginManager.login(
                "admin@admin","admin", ClientType.ADMINISTRATOR);

        Company testCompany = new Company("test", "test@mail", "test", new ArrayList<Coupon>());
        try {
            admin.addCompany(testCompany);
        } catch (ExistException e) {
            System.out.println(e.getMessage());
        }

        Customer testCustomer = new Customer("customerFN","customerLN","customer@mail","password", new ArrayList<Coupon>());
        try {
            admin.addCustomer(testCustomer);
        } catch (ExistException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println(admin.getOneCompany(testCompany.getId()));
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println(admin.getOneCustomer(testCustomer.getId()));
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        testCompany.setPassword("password");
        try {
            admin.updateCompany(testCompany);
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        } catch (PermissionException e) {
            System.out.println(e.getMessage());
        }

        testCustomer.setFirstName("changedFN");
        try {
            admin.updateCustomer(testCustomer);
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(admin.getAllCompanies());
        System.out.println(admin.getAllCustomers());


        // 3. Company Tests
        CompanyFacade company = (CompanyFacade) loginManager.login(
                "test@mail","password", ClientType.COMPANY);

        Coupon testCoupon = new Coupon(testCompany.getId(), Category.FOOD, "test", "this is a test coupon", LocalDate.now(), LocalDate.now().plusMonths(1), 1, 100, "https://imgur.com/gallery/MCakEhm");

        try {
            company.addCoupon(testCoupon);
        } catch (ExistException e) {
            System.out.println(e.getMessage());
        }

        testCoupon.setImage("https://imgur.com/gallery/zwjtdNX");
        try {
            company.updateCoupon(testCoupon);
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(company.getCompanyCoupons());
        System.out.println(company.getCompanyCoupons(200));
        System.out.println(company.getCompanyCoupons(Category.FOOD));
        System.out.println(company.getCompany());


        // 4. Customer Tests
        CustomerFacade customer = (CustomerFacade) loginManager.login(
                "customer@mail", "password", ClientType.CUSTOMER);

        try {
            customer.purchaseCoupon(testCoupon);
        } catch (NotExistException e) {
            e.printStackTrace();
        } catch (PurchaseException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(customer.getCustomerCoupons());
        System.out.println(customer.getCustomerCoupons(200));
        System.out.println(customer.getCustomerCoupons(Category.FOOD));
        System.out.println(customer.getCustomerDetails());

        // Delete All //

        try {
            company.deleteCoupon(testCoupon.getId());
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        try {
            admin.deleteCompany(testCompany.getId());
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        try {
            admin.deleteCustomer(testCustomer.getId());
        } catch (NotExistException e) {
            System.out.println(e.getMessage());
        }

        // 4. Stop Daily Job
        couponExpirationDailyJob.stop();
        couponExpirationDailyJobThread.interrupt();

        // 5. Closing connections
        ConnectionPool pool = ConnectionPool.getInstance();
        pool.closeAllConnections();

    }
}
