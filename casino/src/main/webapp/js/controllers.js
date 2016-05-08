var casinoApp = angular.module('casinoApp', []);

casinoApp.filter('offset', function() {
  return function(input, start) {
    start = parseInt(start);
    return input.slice(start);
  };
});

casinoApp.config(function($httpProvider) {
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;application/json;charset=utf-8';
});

angular.element(document).ready(function() {
  var gameCtrlScope = angular.element(gameCtrl).scope();
  var betTableCtrlScope = angular.element(betTable).scope();
  var spinResultHistoryTableCtrlScope = angular.element(spinResultHistoryTable).scope();
  var profitTableCtrlScope = angular.element(profitTable).scope();
  var monthlyProfitTableCtrlScope = angular.element(monthlyProfitTable).scope();

  gameCtrlScope.isNotBeBetNewly();
  gameCtrlScope.getAutoMode();
  betTableCtrlScope.getBetListAndUpdate();
  spinResultHistoryTableCtrlScope.getSpinResultListAndUpdate();
  profitTableCtrlScope.getPeofitListAndUpdate();
  monthlyProfitTableCtrlScope.getMonthlyPeofitListAndUpdate();
});

casinoApp.controller("GameController", function($scope, $http) {
  $scope.autoMode = "OFF";
  $scope.isAuto = false;
  $scope.spinResultNum = "－";

  $scope.checkAutoUpdate = function() {
    var betTableCtrlScope = angular.element(betTable).scope();
    var spinResultHistoryTableCtrlScope = angular.element(spinResultHistoryTable).scope();
    var profitTableCtrlScope = angular.element(profitTable).scope();
    var monthlyProfitTableCtrlScope = angular.element(monthlyProfitTable).scope();
    if($scope.isAuto) {
      // 各テーブルの自動更新開始
      betTableCtrlScope.intervalUpdateRun();
      spinResultHistoryTableCtrlScope.intervalUpdateRun();
      profitTableCtrlScope.intervalUpdateRun();
      monthlyProfitTableCtrlScope.intervalUpdateRun();
    } else {
      // 各テーブルの自動更新停止
      betTableCtrlScope.intervalUpdateStop();
      betTableCtrlScope.getBetListAndUpdate();
      spinResultHistoryTableCtrlScope.intervalUpdateStop();
      spinResultHistoryTableCtrlScope.getSpinResultListAndUpdate();
      profitTableCtrlScope.intervalUpdateStop();
      profitTableCtrlScope.getPeofitListAndUpdate();
      monthlyProfitTableCtrlScope.intervalUpdateStop();
      monthlyProfitTableCtrlScope.getMonthlyPeofitListAndUpdate();
    }
  };

  $scope.getAutoMode = function() {
    $http.get("casino/getAutoMode").success( function( data ) {
      $scope.isAuto = data.result;
      if($scope.isAuto) {
        $scope.autoMode = "ON";
      } else {
        $scope.autoMode = "OFF";
      }
      $scope.checkAutoUpdate();
    });
  };

  $scope.autoModeChange = function() {
    $http.get("casino/autoModeChange").success( function( data ) {
      if($scope.isAuto) {
        $scope.autoMode = "OFF";
      } else {
        $scope.autoMode = "ON";
      }
      $scope.isAuto = data.result;
      if($scope.isAuto) {
        $scope.auto();
      }
      $scope.checkAutoUpdate();
    });
  };

  $scope.auto = function() {
    $http.get("casino/auto");
  };

  $scope.spin = function() {
    $http.get("casino/spin").success( function( data ) {
      // Update Spin Number
      $scope.spinResultNum = data.spinResultNum;

      // Update Bet Table
      var betTableCtrlScope = angular.element(betTable).scope();
      betTableCtrlScope.update(data.betList);

      // Update Spin Result Table
      var spinResultHistoryTableCtrlScope = angular.element(spinResultHistoryTable).scope();
      spinResultHistoryTableCtrlScope.update(data.spinResultList);

    });
  };

  $scope.reset = function() {
    $http.get("casino/reset").success( function( data ) {
      var betTableCtrlScope = angular.element(betTable).scope();
      betTableCtrlScope.update(data);
    });
  };

  $scope.isNotBeBetNewly = function() {
    $http.get("casino/isNotBeBetNewly").success( function( data ) {
      $scope.notBeBetNewly = data.result;
    });
  };

  $scope.notBeBetNewlyChange = function() {
    $http.get("casino/notBeBetNewlyChange").success( function( data ) {
      $scope.notBeBetNewly = data.result;
    });
  };
});

casinoApp.controller("BetTableController", function($scope, $http, $interval) {
  var time;
  $scope.bets = [];
  for(var i=0; i<=36; i++) {
      $scope.bets[i] = {
        num:            i,
        status:         "-",
        currentSpinNum: "0",
        nextStakes:     "-"
      };
  }

  $scope.update = function(data) {
    $scope.bets = data;
  };

  $scope.getBetListAndUpdate = function() {
    $http.get("casino/getBetList").success( function( data ) {
      $scope.update(data);
    });
  };

  $scope.intervalUpdateRun = function(){
    time = $interval(function () {
      $scope.getBetListAndUpdate();
    }, 10000);
  };

  $scope.intervalUpdateStop = function() {
    if (angular.isDefined(time)) {
      $interval.cancel(time);
      time = undefined;
    }
  };

  $scope.$on('$destroy', function() {
    $scope.intervalUpdateStop();
  });

});

casinoApp.controller("SpinResultTableController", function($scope, $http, $interval) {
  var time;
  $scope.spinResultList = [];

  $scope.update = function(data) {
    $scope.spinResultList = data;
  };

  $scope.getSpinResultListAndUpdate = function() {
    $http.get("casino/getSpinResultList").success( function( data ) {
      $scope.update(data);
    });
  };

  $scope.intervalUpdateRun = function(){
    time = $interval(function () {
      $scope.getSpinResultListAndUpdate();
    }, 10000);
  };

  $scope.intervalUpdateStop = function() {
    if (angular.isDefined(time)) {
      $interval.cancel(time);
      time = undefined;
    }
  };

  $scope.$on('$destroy', function() {
    $scope.intervalUpdateStop();
  });

});

casinoApp.controller("ProfitTableController", function($scope, $http, $interval) {
  var time;
  $scope.profitList = [];
  $scope.profitOrders = [
    {value:'id', text:'#'},
    {value:'lastUpdated', text:'Date'},
    {value:'profit', text:'Profit'},
    {value:'betNumber', text:'Number'},
    {value:'recentHit', text:'RecentHit'}
  ];
  $scope.orderProfit = $scope.profitOrders[0];

  $scope.changeOrderProfit = function() {
    $scope.getPeofitListAndUpdate();
  }

  $scope.update = function(data) {
    $scope.profitList = data;
  };

  $scope.getPeofitListAndUpdate = function() {
    $http.post("casino/getProfitList",$.param({order: $scope.orderProfit.value})).success( function( data ) {
      $scope.update(data);
    });
  };

  $scope.intervalUpdateRun = function(){
    time = $interval(function () {
      $scope.getPeofitListAndUpdate();
    }, 10000);
  };

  $scope.intervalUpdateStop = function() {
    if (angular.isDefined(time)) {
      $interval.cancel(time);
      time = undefined;
    }
  };

  $scope.$on('$destroy', function() {
    $scope.intervalUpdateStop();
  });

});

casinoApp.controller("MonthlyProfitTableController", function($scope, $http, $interval) {
  var time;
  $scope.monthlyProfitList = [];

  $scope.update = function(data) {
    $scope.monthlyProfitList = data;
  };

  $scope.getMonthlyPeofitListAndUpdate = function() {
    $http.get("casino/getMonthlyProfitList").success( function( data ) {
      $scope.update(data);
    });
  };

  $scope.intervalUpdateRun = function(){
    time = $interval(function () {
      $scope.getMonthlyPeofitListAndUpdate();
    }, 10000);
  };

  $scope.intervalUpdateStop = function() {
    if (angular.isDefined(time)) {
      $interval.cancel(time);
      time = undefined;
    }
  };

  $scope.$on('$destroy', function() {
    $scope.intervalUpdateStop();
  });

});

// テンプレート
// casinoApp.controller("TemplateController", function($scope) {
//   $scope.friends = [{
//     id: 1,
//     name: "相田",
//     age: 20,
//     address: "東京都品川区"
//   }, {
//     id: 2,
//     name: "伊藤",
//     age: 55,
//     address: "神奈川県横浜市"
//   }, {
//     id: 3,
//     name: "上野",
//     age: 20,
//     address: "埼玉県川越市"
//   }, ];
// });
