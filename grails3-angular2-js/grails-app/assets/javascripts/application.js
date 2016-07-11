// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better
// to create separate JavaScript files as needed.
//
//= require jquery-2.2.0.min
//= require bootstrap
//= require ./angular2/dependencies/shim.min.js
//= require ./angular2/dependencies/zone.min.js
//= require ./angular2/dependencies/Reflect.js
//= require ./angular2/dependencies/Rx.umd.min.js
//= require ./angular2/core.umd.min.js
//= require ./angular2/common.umd.min.js
//= require ./angular2/compiler.umd.min.js
//= require ./angular2/platform-browser.umd.min.js
//= require ./angular2/platform-browser-dynamic.umd.min.js
//= require ./app/app.component.js
//= require ./app/main.js
//= require_self

if (typeof jQuery !== 'undefined') {
    (function($) {
        $('#spinner').ajaxStart(function() {
            $(this).fadeIn();
        }).ajaxStop(function() {
            $(this).fadeOut();
        });
    })(jQuery);
}
