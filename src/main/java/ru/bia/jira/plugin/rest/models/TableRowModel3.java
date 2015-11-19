package ru.bia.jira.plugin.rest.models;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.MutableIssue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Kmatveev on 16.11.2015.
 */
@XmlRootElement
public class TableRowModel3 {

    @XmlElement
    String component;
    @XmlElement
    String version;
    @XmlElement
    String creationDate;
    @XmlElement
    String dependency;
    @XmlElement
    String description;
    @XmlElement
    String link;

    public TableRowModel3(MutableIssue mutableIssue) {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        String link = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(10004l)));
        this.component = mutableIssue.getComponentObjects().iterator().next().getName();
        this.version = ModelUtilities.getVersion(link);
        //TODO: SimpleDateFormat created = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        this.creationDate = String.valueOf(mutableIssue.getCreated());
        this.dependency = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(12703l)));
        this.description = mutableIssue.getDescription();
        if(link != null) {
            if (!link.equals("null")){
                this.link = link;
            } else {
                this.link = "#";
            }
        } else {
            this.link = "#";
        }
    }
}