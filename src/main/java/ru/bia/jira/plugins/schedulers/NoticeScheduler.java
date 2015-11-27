package ru.bia.jira.plugins.schedulers;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.scheduling.PluginJob;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bia.jira.plugins.Constants;
import ru.bia.jira.plugins.ao.NoticeEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Kmatveev on 26.11.2015.
 */
public class NoticeScheduler implements PluginJob {

    Logger log;

    UserManager userManager;
    GroupManager groupManager;
    PluginAccessor pluginAccessor;

    public NoticeScheduler() {
        log = LoggerFactory.getLogger(this.getClass());
        this.userManager = ComponentAccessor.getUserManager();
        this.groupManager = ComponentAccessor.getGroupManager();
        this.pluginAccessor = ComponentAccessor.getPluginAccessor();
    }

    @Override
    public void execute(Map<String, Object> map) {

        Collection<String> userNames = groupManager.getUserNamesInGroup(Constants.GROUP_JIRA_ADMINISTRATORS);
        StringBuilder receivers = new StringBuilder();
        ActiveObjects activeObjects = (ActiveObjects) map.get("activeObjects");

        String tmpEmail;
        for (String userName : userNames) {
            tmpEmail = userManager.getUserByName(userName).getEmailAddress();
            receivers.append(tmpEmail);
            receivers.append(", ");
        }

        StringBuilder emailContent = new StringBuilder();
        Collection<String> missedPlugins = getMissedPlugins(activeObjects);

        if (!missedPlugins.isEmpty()) {
            emailContent.append("Plugins which don't been enabled: \n");
            for (String missedPlugin : missedPlugins) {
                emailContent.append(missedPlugin);
                emailContent.append('\n');
            }
            log.debug(emailContent.toString());
            SendMail(receivers.toString(), "[JIRA Plugin Monitor] Warning", emailContent.toString());
        } else {
            log.debug("All plugins enabled!");
        }

//        log.debug("Admin emails: <" + receivers.toString() + ">");
//        log.debug("Ao: <" + emailContent.toString() + ">");

        SendMail(receivers.toString(), "Notice", "Something wrong! =D");
    }

    private Collection<String> getMissedPlugins(ActiveObjects activeObjects) {
        Collection<String> answer = new LinkedList<String>();

        NoticeEntity[] noticeEntities = activeObjects.find(NoticeEntity.class);
        Collection<String> pluginKeys = Collections2.transform(pluginAccessor.getPlugins(), new Function<Plugin, String>() {
            @Override
            public String apply(@Nullable Plugin input) {
                return input.getKey();
            }
        });

        String tmpKey;
        boolean found;
        for (NoticeEntity entity : noticeEntities) {
            tmpKey = entity.getComponent();
            found = false;
            for (String key : pluginKeys) {
                if (tmpKey.equals(key)) {
                    if (pluginAccessor.getPlugin(key).getPluginState().equals(PluginState.ENABLED)) {
                        found = true;
                    }
                }
            }
            if (!found) {
                answer.add(tmpKey);
            }
        }
        return answer;
    }

    private void SendMail(String receiver, String title, String content) {
        try {
            SMTPMailServer mailServer = ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer();
            if (mailServer != null) {
                Email email = new Email(receiver, null, null);
                email.setFrom(mailServer.getDefaultFrom());
                email.setSubject(title);
                email.setMimeType("text/plain");
                email.setBody(content);
                SingleMailQueueItem item = new SingleMailQueueItem(email);
                ComponentAccessor.getMailQueue().addItem(item);
            }
        } catch (Exception e) {
            log.error("NoticeScheduler can't send mail: ");
            log.error(e.toString());
        }
    }
}
