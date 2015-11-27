package ru.bia.jira.plugins.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;

/**
 * Created by Kmatveev on 25.11.2015.
 */
@Preload
public interface NoticeEntity extends Entity {

    String getComponent();
    void setComponent(String component);

}
