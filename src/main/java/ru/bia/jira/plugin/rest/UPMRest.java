package ru.bia.jira.plugin.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import ru.bia.jira.plugin.Constants;
import ru.bia.jira.plugin.ao.NoticeEntity;
import ru.bia.jira.plugin.rest.models.TableRowModel4;

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

    String baseUrl;
    OkHttpClient client;

    ProjectManager projectManager;
    IssueManager issueManager;
    CustomFieldManager customFieldManager;
    ActiveObjects activeObjects;

    public UPMRest(ActiveObjects activeObjects) {
        this.issueManager = ComponentAccessor.getIssueManager();
        this.projectManager = ComponentAccessor.getProjectManager();
        this.customFieldManager = ComponentAccessor.getCustomFieldManager();
        this.activeObjects = activeObjects;
        this.baseUrl = ComponentAccessor.getWebResourceUrlProvider().getBaseUrl();
        client = new OkHttpClient();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/test")
    public Response getSomething() {
        JSONArray answer = new JSONArray();
        try {
            String resp = run(Constants.UPM_URL);
            answer = new JSONObject(resp).getJSONArray("plugins");
        } catch (Exception e){
        }
        return Response.ok(answer.toString()).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/test2")
    public Response getSomething2() {
        JSONArray answer = new JSONArray();
        Project project;
        Long aLong;
        Collection<Long> issues;
        try {
            project = projectManager.getProjectByCurrentKey(Constants.PROJECT_KEY);
            if (project != null) {
                aLong = project.getId();
                issues = issueManager.getIssueIdsForProject(aLong);
                JSONObject curModel;
                boolean find = false;
                JSONArray pluginsInfo = getPluginsInfo();
                for (Long issue : issues) {
                    curModel = new TableRowModel4(issueManager.getIssueObject(issue), pluginsInfo).toJSON();
                    for (int i = 0; i < answer.length(); i++) {
                        if (answer.getJSONObject(i).get("component").equals(curModel.get("component"))) {
                            find = true;
                            if (compareData(curModel.get("creationDate").toString(), answer.get(i).toString())) {
                                answer.put(curModel);
                            }
                        }
                    }
                    if (!find) {
                        answer.put(curModel);
                    }
                    find = false;
                }
            }
        } catch (Exception e) {
        }
        return Response.ok(answer.toString()).build();
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
                JSONArray pluginsInfo = getPluginsInfo();
                for (Long issue : issues) {
                    curModel = new TableRowModel4(issueManager.getIssueObject(issue), pluginsInfo);
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

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getAll2")
    public Response getAll2(){
        Collection<Plugin> plugins = ComponentAccessor.getPluginAccessor().getPlugins();
        for (Plugin plugin : plugins) {

        }

        return Response.ok().build();
    }

    @POST
    @AnonymousAllowed
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response noticeListManagement(final String request) throws Exception{



        JSONObject jsonObject = new JSONObject(request);

        String component = jsonObject.getString("component");
        boolean notice  = jsonObject.getBoolean("notice");

        NoticeEntity[] noticeEntities = activeObjects.find(NoticeEntity.class);
        boolean found = false;
        for (NoticeEntity entity : noticeEntities) {
            if(component.equals(entity.getComponent())){
                if(!notice){
                    activeObjects.delete(entity);
                }
                found = true;
                break;
            }
        }
        if(!found){
            if (notice){
                NoticeEntity newNoticeEntity = activeObjects.create(NoticeEntity.class);
                newNoticeEntity.setComponent(component);
                newNoticeEntity.save();
            }
        }
        String ver = ComponentAccessor.getPluginAccessor().getPlugin(component).getPluginInformation().getVersion();
        JSONObject answer = new JSONObject();
        answer.put("component", component);
        answer.put("notice", notice);
        answer.put("ver", ver);

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