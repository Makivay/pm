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
public class TableRowModel2 {
    @XmlElement
    private String component;
    @XmlElement
    private String dependency;
    @XmlElement
    private String link;

    public TableRowModel2(String component, String dependency, String link) {
        this.component = component;
        this.dependency = dependency;
        this.link = link;
    }

    public TableRowModel2(MutableIssue issueObject) {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        this.component = issueObject.getComponentObjects().iterator().next().getName();
        this.dependency = String.valueOf(issueObject.getCustomFieldValue(customFieldManager.getCustomFieldObject(12703L)));
        this.link = "<a href="+String.valueOf(issueObject.getCustomFieldValue(customFieldManager.getCustomFieldObject(10004L)))+" >link</a>";
    }
}
