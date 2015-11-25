package ru.bia.jira.plugin.actions;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Created by Kmatveev on 16.11.2015.
 */
public class MainAction extends JiraWebActionSupport {

    boolean checkLogin;
    private String ERROR_TITLE = "Default error title";
    private String ERROR_MESSAGE = "Default error message";

    public MainAction(){
        checkLogin = checkLogin();
        if(!checkLogin) {
            ERROR_TITLE = "Authorization error.";
            ERROR_MESSAGE = "User not authorizated!";
        }
    }

    /**
     * "default" command
     */
    @Override
    public String doDefault() throws Exception {
        if(checkLogin){
            return INPUT;
        } else {
            return ERROR;
        }
    }

    /**
     * Subclasses may override this method to provide validation of input data. The execute() method should call
     * validate() in the beginning of its code (which will delegate to this method), so as to check input data before
     * doing the actual processing.
     * <p>
     * If any application errors arise these should be registered by calling addErrorMessage or addError .
     * <p>
     * If the validation is dependent on whether a command has been issued, and what that command is, then the
     * isCommand() method should be used.
     */


    @Override
    protected void doValidation() {
        if(!checkLogin){
            return;
        }
        super.doValidation();
    }

    @Override
    protected String doExecute() throws Exception {
        if(checkLogin()){
            return INPUT;
        } else {
            return ERROR;
        }
    }

    private boolean checkLogin() {
        try{
            ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getUser();
            return (user!=null&&user.isActive());
        }
        catch (NullPointerException e){

            return false;
        }
    }

    public String getERROR_TITLE(){
        return ERROR_TITLE;
    }

    public String getERROR_MESSAGE(){
        return ERROR_MESSAGE;
    }
}
