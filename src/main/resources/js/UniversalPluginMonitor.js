function getSmth(){
    jQuery.get( "https://jira-test.dellin.ru/rest/plugins/1.0/", function( data ) {
        var arr = data.plugins;
        var name = "ru.dellin.jira.JIRABUSConnector";
        var found = false;

        arr.forEach(function(item, i, arr) {
            if(item.key === name){
                found = true;
                if(item.enabled){
                    console.log("Plugin " + name + " Version: " + item.version + " enabled.");
                } else {
                    console.log("Plugin " + name + " Version: " + item.version + " disabled.");
                }
            }
        });

        if(!found){
            console.log("Plugin " + name + " not installed.");
        }
    });
}
function getPlugins(){

    jQuery.get( "https://jira-test.dellin.ru/rest/plugins/1.0/", function( resp ) {
        return resp.plugins;
    });
}

function getLink(){
    return AJS.RestfulTable.CustomReadView.extend({
        render:function(self){
            if (self !== undefined){
                this.el.innerHTML = "<a href=\"" + self.value + "\" >link</a>";
                }
            return this.el;
        }
    })
}
jQuery(document).ready(function(){
    var rt = new AJS.RestfulTable({
        allowCreate: false,
        allowEdit: false,
        allowReorder: false,
        allowDelete: false,
        reverseOrder: true,
        el: jQuery(".restful"),
        resources: {
            all: AJS.contextPath()+"/rest/upmonitor/latest/monitor/getAll",
            self: AJS.contextPath()+"/rest/upmonitor/latest/monitor"
        },
        columns: [
            {
                id: "component",
                header: "component",
            },
            {
                id: "version",
                header: "version",
            },
            {
                id: "creationDate",
                header: "creationDate",
            },
            {
                id: "dependency",
                header: "dependency",
            },
            {
                id: "description",
                header: "description",
            },
            {
                 id: "link",
                 header: "link",
                 readView: getLink(),
             }
        ]
    });
})