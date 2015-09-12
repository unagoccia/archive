$(function() {

  // ウィンドウリサイズ時
  $(window).resize(function() {
    srNumResize();
  });

  $(document).ready(function () {
    srNumResize();

    // DatePicker(Spin Profit)
    $(".input-group.date").datepicker({
      format: "yyyy/mm/dd",
      todayBtn: "linked",
      orientation: "bottom left",
      autoclose: true
    });
  });

  function srNumResize() {
    var html_w = $("html").width();

    var srImg_w = $("#spinResultImg").width();
    var srImg_h = $("#spinResultImg").height();
    var srNum_fontSize = srImg_w - 40;

    var stNum_w = srImg_w;
    var srNum_left = html_w/2 - stNum_w/2;
    var srNum_top = -srImg_h / 7 + 35;

    $("#spinResultNum").css("font-size", srNum_fontSize);
    $("#spinResultNum").css("width", stNum_w);
    $("#spinResultNum").css("top", srNum_top);
    $("#spinResultNum").css("left", srNum_left);
  }
});
