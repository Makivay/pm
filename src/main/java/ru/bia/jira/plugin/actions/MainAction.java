package ru.bia.jira.plugin.actions;

import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Created by Kmatveev on 16.11.2015.
 */
public class MainAction extends JiraWebActionSupport {
    @Override
    protected String doExecute() throws Exception {
        return INPUT;
    }
}
