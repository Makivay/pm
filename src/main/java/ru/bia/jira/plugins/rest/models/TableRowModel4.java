package ru.bia.jira.plugins.rest.models;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import ru.bia.jira.plugins.ao.NoticeEntity;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Kmatveev on 23.11.2015.
 */
public class TableRowModel4 extends ModelUtilities {

    @XmlElement
    String condition;
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
    @XmlElement
    boolean notice;

    public TableRowModel4(MutableIssue mutableIssue, JSONArray pluginsInfo, NoticeEntity[] noticeInfo) {
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        String link = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(10004L)));
        this.component = mutableIssue.getComponentObjects().iterator().next().getName();
        this.condition = getCondition(pluginsInfo, this.component);
        this.version = ModelUtilities.getVersion(link);
        //TODO: SimpleDateFormat created = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        this.creationDate = String.valueOf(mutableIssue.getCreated());
        this.dependency = String.valueOf(mutableIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(12703L)));
        this.description = mutableIssue.getDescription();
        this.notice = getNotice(noticeInfo, this.component);
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

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        try {
            object.put("condition", new JSONObject(this.condition));
            object.put("component", this.component);
            object.put("version", this.version);
            object.put("creationDate", this.creationDate);
            object.put("dependency", this.dependency);
            object.put("description", this.description);
            object.put("link", this.link);
            object.put("notice", this.notice);
        } catch (Exception e){

        }
        return object;
    }

    public boolean younger(TableRowModel4 tableRowModel) {
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

    public String getCondition() {
        return condition;
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

    public String getLink() {
        return link;
    }
}
