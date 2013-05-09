function doOnLoad() {
    var Environment = function (data) {
        var self = this;
        self.name = ko.observable(data.name);
        self.properties = ko.observable(data.properties);
        self.subEnvironments = ko.observableArray(ko.utils.arrayMap(data.subEnvironmentViews, function (s) {
            return new Environment(s);
        }));
        self.applications = ko.observableArray(ko.utils.arrayMap(data.applications, function (a) {
            return new Application(a);
        }));
        self.properties = ko.observableArray(data.properties, function (propertyObj) {
            return new Property(propertyObj.key, propertyObj.value, propertyObj.inherited, propertyObj.derivedValue)
        });
        self.addProp = function(formElement) {
            addProp(formElement, data);
        };
        self.addSubEnv = function(formElement) {
            var values = $(formElement).serializeArray();
            var name = values[0].value;
            if (typeof name !== undefined && name.length > 0) {
                $.ajax({
                    url: data.link + "/newSubContainer/" + encodeURIComponent(name),
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
                    url: data.link + "/newApplication/" + encodeURIComponent(name),
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
                url: data.link,
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

    var Application = function (data) {
        var self = this;
        self.name = ko.observable(data.name);
        self.properties = ko.observableArray(data.properties, function (propertyObj) {
            return new Property(propertyObj.key, propertyObj.value, propertyObj.inherited, propertyObj.derivedValue)
        });
        self.addProp = function(formElement) {
            addProp(formElement, data);
        }
    };

    var Property = function (key, value, inherited, derivedValue) {
        var self = this;
        self.key = ko.observable(key);
        self.value = ko.observable(value);
        self.inherited = ko.observable(inherited);
        self.derivedValue = ko.observable(derivedValue);
    };

    // Activates knockout.js
    function update(){
        $.getJSON("/environment", function (env) {
            var environment = new Environment(env);
            ko.applyBindings(environment);
            console.log(ko.toJSON(environment))
        });
    }
    update();
}