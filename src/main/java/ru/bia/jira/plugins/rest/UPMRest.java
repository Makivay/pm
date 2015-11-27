package ru.bia.jira.plugins.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bia.jira.plugins.Constants;
import ru.bia.jira.plugins.ao.NoticeEntity;
import ru.bia.jira.plugins.rest.models.TableRowModel4;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


@Path("/monitor")
@AnonymousAllowed
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class UPMRest {

    OkHttpClient client;
    Logger log;

    ProjectManager projectManager;
    IssueManager issueManager;
    CustomFieldManager customFieldManager;
    ActiveObjects activeObjects;
    PluginScheduler pluginScheduler;

    public UPMRest(ActiveObjects activeObjects,PluginScheduler pluginScheduler) {
        this.issueManager = ComponentAccessor.getIssueManager();
        this.projectManager = ComponentAccessor.getProjectManager();
        this.customFieldManager = ComponentAccessor.getCustomFieldManager();
        this.pluginScheduler = pluginScheduler;
        this.activeObjects = activeObjects;
        client = new OkHttpClient();

        log = LoggerFactory.getLogger(this.getClass());
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/test")
    public Response getSomething() throws JSONException {
        JSONArray answer = new JSONArray();
        NoticeEntity[] entities = activeObjects.find(NoticeEntity.class);
        for (NoticeEntity entity : entities) {
            answer.put(new JSONObject().put("component", entity.getComponent()));
        }
        return Response.ok(answer.toString()).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/test2")
    public Response getSomething2() throws JSONException {

        return Response.ok(getPluginsInfo2().toString()).build();
    }



    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getAll")
    public Response getAll() {

        List<TableRowModel4> answer = new LinkedList<TableRowModel4>();
        Project project;
        Long aLong;
        Collection<Long> issues;
        try {
            project = projectManager.getProjectByCurrentKey(Constants.PROJECT_KEY);
            if (project != null) {
                aLong = project.getId();
                issues = issueManager.getIssueIdsForProject(aLong);
                TableRowModel4 curModel;
                boolean find = false;
                JSONArray pluginsInfo = getPluginsInfo2();
                NoticeEntity[] noticeInfo = getNoticeInfo();
                for (Long issue : issues) {
                    curModel = new TableRowModel4(issueManager.getIssueObject(issue), pluginsInfo, noticeInfo);
                    for (TableRowModel4 row : answer) {
                        if (row.getComponent().equals(curModel.getComponent())) {
                            find = true;
                            if (curModel.younger(row)) {
                                Collections.replaceAll(answer, row, curModel);
                            }
                        }
                    }
                    if (!find) {
                        answer.add(curModel);
                    }
                    find = false;
                }
            }
        } catch (Exception e) {
        }
        JSONArray jsonArray = new JSONArray();
        for (TableRowModel4 tableRowModel4 : answer) {
            jsonArray.put(tableRowModel4.toJSON());
        }
        return Response.ok(jsonArray.toString()).build();
    }


    @POST
    @AnonymousAllowed
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response noticeListManagement(final String request) throws Exception{
        JSONObject jsonObject = new JSONObject(request);
        StringBuilder debug = new StringBuilder();

        String component = jsonObject.getString("component");
        boolean notice  = jsonObject.getBoolean("notice");

        NoticeEntity[] noticeEntities = activeObjects.find(NoticeEntity.class);
        debug.append(noticeEntities.toString());
        boolean found = false;
        for (NoticeEntity entity : noticeEntities) {
            if(component.equals(entity.getComponent())){
                if(!notice){
                    activeObjects.delete(entity);
                    debug.append("|notice was deleted|");
//                    Scheduler.unscheduleJob(Constants.SHEDULE_JOB_NAME);
//                    log.debug("Stop Sheduling!");
                }
                found = true;
                debug.append("found:");
                debug.append(found);
                break;
            }
        }
        if(!found){
            if (notice){
                NoticeEntity newNoticeEntity = activeObjects.create(NoticeEntity.class);
                newNoticeEntity.setComponent(component);
                newNoticeEntity.save();
                debug.append("|notice was saved|");
//                pluginScheduler.scheduleJob(Constants.SHEDULE_JOB_NAME, NoticeScheduler.class, Collections.EMPTY_MAP , new Date(), Constants.SHEDULE_PERIOD);
//                log.debug("Start Sheduling!");
            }
        }
        Plugin plugin = ComponentAccessor.getPluginAccessor().getPlugin(component);
        String ver = null;
        if(plugin != null){
            ver = plugin.getPluginInformation().getVersion();
        }

        JSONObject answer = new JSONObject();
        answer.put("component", component);
        answer.put("notice", notice);
        answer.put("ver", ver);
        answer.put("debug", debug.toString());

        return Response.ok(answer.toString()).build();
    }

    public boolean compareData(String data1, String data2) {
        char cSelf;
        char cQuest;
        for (int i = 0; i < data1.length(); i++) {
            cSelf = data1.charAt(i);
            cQuest = data2.charAt(i);
            if (cSelf > cQuest) {
                return true;
            } else if (cSelf < cQuest) {
                return false;
            }
        }
        return false;
    }

    private JSONArray getPluginsInfo() {

        JSONArray array;
        try {
            String resp = run(Constants.UPM_URL);
            array = new JSONObject(resp).getJSONArray("plugins");
        } catch (Exception e) {
            array = null;
        }
        return array;
    }

    private JSONArray getPluginsInfo2() throws JSONException {
        JSONArray answer = new JSONArray();

        Collection<Plugin> plugins = ComponentAccessor.getPluginAccessor().getPlugins();
        JSONObject tmp;
        for (Plugin plugin : plugins) {
            tmp = new JSONObject();
            try {
                tmp.put("enabled", plugin.getPluginState().equals(PluginState.ENABLED));
                tmp.put("version", plugin.getPluginInformation().getVersion());
                tmp.put("key", plugin.getKey());
            } catch (JSONException e){
                tmp = null;
            }
            answer.put(tmp);
        }
        return answer;
    }

    private NoticeEntity[] getNoticeInfo(){
        return activeObjects.find(NoticeEntity.class);
    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader(Constants.HEADER_AUTH_NAME, Constants.HEADER_AUTH_VALUE)
                .build();

        com.squareup.okhttp.Response response;
        response = client.newCall(request).execute();
        return response.body().string();
    }



}