package ru.bia.jira.plugins.rest.models;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.MutableIssue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Kmatveev on 16.11.2015.
 */
@XmlRootElement
public class TableRowModel {
    @XmlElement
    private String component;
    @XmlElement
    private String dependency;
    @XmlElement
    private String link;
    @XmlElement
    private String version;

    public TableRowModel(String component, String dependency, String link) {
        this.component = component;
        this.dependency = dependency;
        this.link = link;
    }

    public TableRowModel(MutableIssue mutableIssue) {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        this.component = mutableIssue.getComponentObjects().iterator().next().getName();
        this.dependency = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(12703L)));
        this.link = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(10004L)));
        this.version = ModelUtilities.getVersion(this.link);
    }
}