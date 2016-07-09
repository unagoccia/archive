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
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiaGVsbG93b3JsZC5qcyIsInNvdXJjZVJvb3QiOiIiLCJzb3VyY2VzIjpbIi4uLy4uLy4uL29zbS9zcmMvdHMvaGVsbG93b3JsZC50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQSxJQUFVLFVBQVUsQ0FRbkI7QUFSRCxXQUFVLFVBQVUsRUFBQyxDQUFDO0lBRXJCO1FBQUE7UUFJQSxDQUFDO1FBSEEsb0JBQUssR0FBTDtZQUNDLE9BQU8sQ0FBQyxHQUFHLENBQUMsYUFBYSxDQUFDLENBQUM7UUFDNUIsQ0FBQztRQUNGLFdBQUM7SUFBRCxDQUFDLEFBSkQsSUFJQztJQUpZLGVBQUksT0FJaEIsQ0FBQTtBQUVGLENBQUMsRUFSUyxVQUFVLEtBQVYsVUFBVSxRQVFuQjtBQUVELElBQUksSUFBSSxHQUFHLElBQUksVUFBVSxDQUFDLElBQUksRUFBRSxDQUFDO0FBQ2pDLElBQUksQ0FBQyxLQUFLLEVBQUUsQ0FBQyIsInNvdXJjZXNDb250ZW50IjpbIm5hbWVzcGFjZSBoZWxsb3dvcmxkIHtcblxuXHRleHBvcnQgY2xhc3MgRGVtbyB7XG5cdFx0Z3JlZXQoKTogdm9pZCB7XG5cdFx0XHRjb25zb2xlLmxvZyhcImhlbGxvIHdvcmxkXCIpO1xuXHRcdH1cblx0fVxuXG59XG5cbnZhciBkZW1vID0gbmV3IGhlbGxvd29ybGQuRGVtbygpO1xuZGVtby5ncmVldCgpO1xuIl19