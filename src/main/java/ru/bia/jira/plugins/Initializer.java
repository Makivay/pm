package ru.bia.jira.plugins;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.bia.jira.plugins.schedulers.NoticeScheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kmatveev on 26.11.2015.
 */
public class Initializer implements InitializingBean, DisposableBean {

    Logger log;
    PluginScheduler pluginScheduler;
    ActiveObjects activeObjects;

    public Initializer(PluginScheduler pluginScheduler, ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
        this.pluginScheduler = pluginScheduler;
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void destroy() throws Exception {
        pluginScheduler.unscheduleJob(Constants.SCHEDULE_JOB_NAME);
        log.debug("Stop sheduling. =(");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("activeObjects", activeObjects);

        Date startTime = new Date();
        startTime.setTime(startTime.getTime() + 5000L);

        pluginScheduler.scheduleJob(Constants.SCHEDULE_JOB_NAME, NoticeScheduler.class, params, startTime, Constants.SCHEDULE_PERIOD);
        log.debug("Start sheduling! =D");

    }
}
