var helloworld;
(function (helloworld) {
    var Demo = (function () {
        function Demo() {
        }
        Demo.prototype.greet = function () {
            console.log("hello world");
        };
        return Demo;
    }());
    helloworld.Demo = Demo;
})(helloworld || (helloworld = {}));
var demo = new helloworld.Demo();
demo.greet();
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaGVsbG93b3JsZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uL3NyYy90cy9oZWxsb3dvcmxkLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBLElBQVUsVUFBVSxDQVFuQjtBQVJELFdBQVUsVUFBVSxFQUFDLENBQUM7SUFFckI7UUFBQTtRQUlBLENBQUM7UUFIQSxvQkFBSyxHQUFMO1lBQ0MsT0FBTyxDQUFDLEdBQUcsQ0FBQyxhQUFhLENBQUMsQ0FBQztRQUM1QixDQUFDO1FBQ0YsV0FBQztJQUFELENBQUMsQUFKRCxJQUlDO0lBSlksZUFBSSxPQUloQixDQUFBO0FBRUYsQ0FBQyxFQVJTLFVBQVUsS0FBVixVQUFVLFFBUW5CO0FBRUQsSUFBSSxJQUFJLEdBQUcsSUFBSSxVQUFVLENBQUMsSUFBSSxFQUFFLENBQUM7QUFDakMsSUFBSSxDQUFDLEtBQUssRUFBRSxDQUFDIiwic291cmNlc0NvbnRlbnQiOlsibmFtZXNwYWNlIGhlbGxvd29ybGQge1xuXG5cdGV4cG9ydCBjbGFzcyBEZW1vIHtcblx0XHRncmVldCgpOiB2b2lkIHtcblx0XHRcdGNvbnNvbGUubG9nKFwiaGVsbG8gd29ybGRcIik7XG5cdFx0fVxuXHR9XG5cbn1cblxudmFyIGRlbW8gPSBuZXcgaGVsbG93b3JsZC5EZW1vKCk7XG5kZW1vLmdyZWV0KCk7XG4iXX0=