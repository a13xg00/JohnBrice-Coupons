package run;

import facade.AdminFacade;
import facade.ClientFacade;
import facade.CompanyFacade;
import loginManager.ClientType;
import loginManager.LoginManager;

public class Main {
    public static void main(String[] args) {

        LoginManager loginManager = LoginManager.getInstance();


//        AdminFacade facade = (AdminFacade) loginManager.login("admin@admin","admin", ClientType.ADMINISTRATOR);
//
//        System.out.println(facade.getAllCompanies());

        CompanyFacade companyFacade = (CompanyFacade) loginManager.login("some@mail","password", ClientType.COMPANY);

        if (companyFacade != null){
            System.out.println(companyFacade.getCompanyId());
        }


    }
}
