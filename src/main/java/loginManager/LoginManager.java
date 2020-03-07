package loginManager;

import facade.AdminFacade;
import facade.ClientFacade;
import facade.CompanyFacade;
import facade.CustomerFacade;

public class LoginManager {

    private static LoginManager instance = null;

    private LoginManager() {}

    public static LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }

    public ClientFacade login(String email, String password, ClientType clientType){
        switch (clientType){
            case COMPANY:
                CompanyFacade companyFacade = new CompanyFacade();
                return companyFacade.login(email,password) ? companyFacade : null;
            case CUSTOMER:
                CustomerFacade customerFacade = new CustomerFacade();
                return customerFacade.login(email,password) ? customerFacade : null;
            case ADMINISTRATOR:
                AdminFacade adminFacade = new AdminFacade(couponsDBDAO);
                return adminFacade.login(email,password) ? adminFacade : null;
        }
        return null;
    }


}

