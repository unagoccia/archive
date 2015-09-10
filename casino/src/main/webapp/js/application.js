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

    $("#auto-btn").click(function(e) {
      if($(this).hasClass("btn-default")) {
        $(this).removeClass("btn-default");
        $(this).addClass("btn-success");
      } else {
        $(this).removeClass("btn-success");
        $(this).addClass("btn-default");
      }
    });

  });

  function srNumResize() {
    var sr_w = $("#spin-result").width();
    var sr_h = $("#spin-result").height();

    var srImg_w = $("#spin-result-img").width();
    var srImg_h = $("#spin-result-img").height();
    var srNum_fontSize = srImg_w + "px";

    var srNum_left = sr_w / 2 - (srImg_w / 4);
    var srNum_top = -srImg_h / 7 + 10;

    $("#spin-result-num").html("5");
    $("#spin-result-num").css("font-size", srNum_fontSize);
    $("#spin-result-num").css("top", srNum_top);
    $("#spin-result-num").css("left", srNum_left);
  }
});
