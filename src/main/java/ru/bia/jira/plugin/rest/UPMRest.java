package ru.bia.jira.plugin.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import ru.bia.jira.plugin.rest.models.TableRowModel3;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.LinkedList;


@Path("/monitor")
@AnonymousAllowed
@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
public class UPMRest {

    ProjectManager projectManager;
    IssueManager issueManager;
    CustomFieldManager customFieldManager;

    public UPMRest() {
        this.issueManager = ComponentAccessor.getIssueManager();
        this.projectManager = ComponentAccessor.getProjectManager();
        this.customFieldManager = ComponentAccessor.getCustomFieldManager();
    }

//    public UPMRest(ProjectManager projectManager, IssueManager issueManager) {
//        this.issueManager = issueManager;
//        this.projectManager = projectManager;
//    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/test")
    public Response getSomething() {
        return Response.ok("Ok!").build();
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/getAll")
    public Response getAll() {
        Collection<TableRowModel3> answer = new LinkedList<TableRowModel3>();
        final String name = "JIRAPLUGIN";
        Project project;
        Long aLong;
        Collection<Long> issues;
        try {
            project = projectManager.getProjectByCurrentKey(name);
            if (project != null) {
                aLong = project.getId();
                issues = issueManager.getIssueIdsForProject(aLong);
                for (Long issue : issues) {
                    answer.add(new TableRowModel3(issueManager.getIssueObject(issue)));
                }
            } else{
            }
        } catch (Exception e) {
        }
        return Response.ok(answer).build();
    }

}