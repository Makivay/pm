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
                this.el.innerHTML = "<a href=\"" + self.value + "\" class=\"aui-button\" > Download </a>";
                }
            return this.el;
        }
    })
}

function getComponent(){
    return AJS.RestfulTable.CustomReadView.extend({
        render:function(self){
            var plugins;
            jQuery.ajax({
                url: AJS.contextPath()+"/rest/plugins/1.0/",
                success: function( resp ) {
                    plugins = resp.plugins;
                },
                async: false
            });
            var name = self.value;
            var found = false;
            var element = this.el;
            plugins.forEach(function(item, i) {
                if(item.key === name){
                    found = true;
                    if(item.enabled){
                        element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-success\">" + name + "</span>";
                    } else {
                        element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-current\">" + name + "</span>";
                    }
                }
            });
            if(!found){
                element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-error\">" + name + "</span>";
            }
            return element;
        }
    })
}

function getVersion(){
    return AJS.RestfulTable.CustomReadView.extend({
        render:function(self){
            var element = this.el;
            if( self.value.enabled === "enabled" ){
                element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-success\"> Enabled " + self.value.version + "</span>";
            } else if( self.value.enabled === "disabled" ) {
                element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-current\"> Disabled " + self.value.version + "</span>";
            } else {
                element.innerHTML = "<span class=\"aui-lozenge aui-lozenge-error\"> Not installed </span>";
            }
            return element;
        }
    })
}

function addRestfulTable(){
    new AJS.RestfulTable({
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
                id: "condition",
                header: "Status",
                readView: getVersion(),
            },
            {
                id: "component",
                header: "Component",
            },
            {
                id: "version",
                header: "Current version"
            },
            {
                id: "creationDate",
                header: "Creation date"
            },
            {
                id: "dependency",
                header: "Dependency"
            },
            {
                id: "description",
                header: "Description"
            },
            {
                 id: "link",
                 header: "",
                 readView: getLink()
             }
        ]
    });
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
                id: "condition",
                header: "Condition",
                readView: getVersion(),
            },
            {
                id: "component",
                header: "Component",
//                readView: getComponent()
            },
            {
                id: "version",
                header: "This version"
            },
            {
                id: "creationDate",
                header: "CreationDate"
            },
            {
                id: "dependency",
                header: "Dependency"
            },
            {
                id: "description",
                header: "Description"
            },
            {
                 id: "link",
                 header: "Link",
                 readView: getLink()
             }
        ]
    });
})