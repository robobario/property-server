function doOnLoad() {
    window.computed = 0;
    var host = ""
    var PropertiesApplication = function(data) {
       var self = this;
       self.searchProp = ko.observable("");
       self.searchEnv = ko.observable("");
       self.searchApp = ko.observable("");
       self.search = ko.computed(function(){
           var propRegexp = new RegExp(".*"+escapeRegExp(self.searchProp())+".*","i");
           var envRegexp = new RegExp(".*"+escapeRegExp(self.searchEnv())+".*","i");
           var appRegexp = new RegExp(".*"+escapeRegExp(self.searchApp())+".*","i");
           return {
               matchEnv: function(name){
                   return typeof name !== "undefined" && name.match(envRegexp);
               },
               matchApp: function(name){
                   return typeof name !== "undefined" && name.match(appRegexp);
               },
               matchProp: function(key, value, derivedValue){
                   return typeof name !== "undefined" && key.match(propRegexp) ||value.match(propRegexp) ||derivedValue.match(propRegexp) ;
               }
           };
       });
       self.environment = ko.observable(new Environment(data,self.search, ""));
       self.update = function(env){
            self.environment(new Environment(env,self.search, ""));
       };
    }

    var Environment = function (data, search, breadcrumb) {
        var self = this;
        self.search = search;
        self.name = ko.observable(data.name);
        self.breadCrumbs = ko.computed(function(){return breadcrumb + self.name()}, self);
        self.properties = ko.observable(data.properties);
        self.subEnvironments = ko.observableArray(ko.utils.arrayMap(data.subEnvironmentViews, function (s) {
            return new Environment(s,self.search, self.breadCrumbs() + " | ");
        }));
        self.applications = ko.observableArray(ko.utils.arrayMap(data.applications, function (a) {
            return new Application(a,self.search, self.breadCrumbs() + " | ");
        }));
        self.properties = ko.observableArray(ko.utils.arrayMap(data.properties, function (propertyObj) {
            return new Property(propertyObj,self.search)
        }));
        self.visible = ko.computed(function(){
            return self.search().matchEnv(self.name())
        },self);
        self.addProp = function(formElement) {
            addProp(formElement, data);
        };
        self.addSubEnv = function(formElement) {
            var values = $(formElement).serializeArray();
            var name = values[0].value;
            if (typeof name !== undefined && name.length > 0) {
                $.ajax({
                    url: host + data.link + "/newSubContainer/" + encodeURIComponent(name),
                    type: "PUT",
                    success: update,
                    settings: {
                        accepts: "application/json"
                    }
                })
            }
        };
        self.addApp = function(formElement) {
            var values = $(formElement).serializeArray();
            var name = values[0].value;
            if (typeof name !== undefined && name.length > 0) {
                $.ajax({
                    url: host + data.link + "/newApplication/" + encodeURIComponent(name),
                    type: "PUT",
                    success: update,
                    settings: {
                        accepts: "application/json"
                    }
                })
            }
        };
    };

    function addProp(formElement, data) {
        var values = $(formElement).serializeArray();
        var propKey = values[0].value;
        var propVal = values[1].value;
        if (typeof propKey !== undefined && propKey.length > 0 && typeof propVal !== undefined && propVal.length > 0) {
            $.ajax({
                url: host + data.link,
                type: "POST",
                data: JSON.stringify({"key":propKey,"value":propVal}),
                success: update,
                contentType: "application/json; charset=UTF-8",
                settings: {
                    accepts: "application/json"
                }
            })
        }
    }

    var Application = function (data, search, breadcrumbs) {
        var self = this;
        self.search = search;
        self.name = ko.observable(data.name);
        self.breadCrumbs = ko.computed(function(){return breadcrumbs + self.name()},self);
        self.properties = ko.observableArray(ko.utils.arrayMap(data.properties, function (propertyObj) {
            return new Property(propertyObj, self.search)
        }));
        self.addProp = function(formElement) {
            addProp(formElement, data);
        };
        self.visible = ko.computed(function(){
            return self.search().matchApp(self.name())
        },self);
    };

    var Property = function (prop, search) {
        var self = this;
        self.key = prop.key;
        self.search = search;
        self.value = prop.value;
        self.inherited = prop.inherited;
        self.derivedValue = prop.derivedValue;
        self.link = prop.link;
        self.shouldShowDelete = function(){
            return !self.inherited
        };
        self.visible = ko.computed(function(){
            return self.search().matchProp(self.key, self.value, self.derivedValue)
        },self);
        self.doDelete = function(){
            $.ajax({
                url: host +  self.link,
                type: "DELETE",
                success: update,
                settings: {
                    accepts: "application/json"
                }
            })
        };
    };

    var application = new PropertiesApplication({});
    // Activates knockout.js
    function update(){
        $.getJSON(host + "/environment", function (env) {
            application.update(env);
            window.latest = env;
        });
    }
    ko.applyBindings(application);
    update();

    function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }
}