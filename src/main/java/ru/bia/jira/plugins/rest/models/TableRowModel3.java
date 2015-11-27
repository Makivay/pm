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
        String link = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(10004L)));
        this.component = mutableIssue.getComponentObjects().iterator().next().getName();
        this.version = ModelUtilities.getVersion(link);
        //TODO: SimpleDateFormat created = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        this.creationDate = String.valueOf(mutableIssue.getCreated());
        this.dependency = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(12703L)));
        this.description = mutableIssue.getDescription();
        if (link != null) {
            if (!link.equals("null")) {
                this.link = link;
            } else {
                this.link = "#";
            }
        } else {
            this.link = "#";
        }
    }

    public boolean younger(TableRowModel3 tableRowModel) {
        final int length = this.creationDate.length();
        final String self = this.creationDate;
        final String quest = tableRowModel.getCreationDate();

        char cSelf;
        char cQuest;
        for (int i = 0; i < length; i++) {
            cSelf = self.charAt(i);
            cQuest = quest.charAt(i);
            if (cSelf > cQuest) {
                return true;
            } else if (cSelf < cQuest) {
                return false;
            }
        }
        return false;
    }

    public String getComponent() {
        return component;
    }

    public String getVersion() {
        return version;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getDependency() {
        return dependency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }
}