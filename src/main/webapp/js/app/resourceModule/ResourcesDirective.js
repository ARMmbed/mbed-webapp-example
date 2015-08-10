angular.module('App.directives', []);
angular.module('App.directives').directive('ngSuccess', function () {
    return function (scope, element, attrs) {
        var button = angular.element(element);
        $(button).popover({
            template: '<div class="popover success_popover" role="tooltip" style="width: 500px;">' +
            '<div class="arrow success_popover-arrow">' +
            '</div>' +
            '<h3 class="popover-title success_popover-title">' +
            '</h3>' +
            '<div class="popover-content success_popover-content">' +
            '<div class="data-content text-center">' +
            '</div>' +
            '</div>' +
            '</div>',
            content: "",
            html: true
        });
    };

});
angular.module('App').directive('ngError', function () {
    return function (scope, element, attrs) {
        var button = angular.element(element);
        $(button).popover({
            template: '<div class="popover error_popover" role="tooltip" style="width: 500px;">' +
            '<div class="arrow error_popover-arrow">' +
            '</div>' +
            '<h3 class="popover-title error_popover-title">' +
            '</h3>' +
            '<div class="popover-content error_popover-content">' +
            '<div class="data-content">' +
            '</div>' +
            '</div>' +
            '</div>',
            content: ""
        });
    };
});
angular.module('App').directive('ngAction', function () {
    return function (scope, element, attrs) {
        var editable_clicked = angular.element(element);
        $(editable_clicked).editable({
            title: 'ENTER YOUR COMMAND',
            value: '',
            pk: 1,
            type: 'textarea',
            emptytext: 'action',
            emptyclass: '',
            rows: 2,
            tpl: '<textarea id="commandValue"></textarea>'
        });
    };
});

angular.module('App').directive('ngConfirmClick', [
    function () {
        return {
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure?";
                var clickAction = attr.confirmedClick;
                element.bind('click', function (event) {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }])