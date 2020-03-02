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
                if(companyFacade.login(email,password)){
                    return companyFacade;
                }
            case CUSTOMER:
                CustomerFacade customerFacade = new CustomerFacade();
                if(customerFacade.login(email,password)){
                    return customerFacade;
                }
            case ADMINISTRATOR:
                AdminFacade adminFacade = new AdminFacade();
                if(adminFacade.login(email,password)){
                    return adminFacade;
                }
        }
        return null;
    }


}

