package ru.bia.jira.plugin;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;

/**
 * Created by Kmatveev on 25.11.2015.
 */
public class Constants {
    public static final String HEADER_AUTH_NAME = "Authorization";
    public static final String HEADER_AUTH_VALUE = "Basic S01hdHZlZXY6N2FmR2hkZ0tUcVlZ";
    public static final String PROJECT_KEY = "JIRAPLUGIN";
    public static final String BASE_URL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
    public static final String UPM_URL = BASE_URL + "/rest/plugins/1.0/";
}
