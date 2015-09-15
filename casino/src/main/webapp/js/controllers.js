var casinoApp = angular.module('casinoApp', []);

casinoApp.filter('offset', function() {
  return function(input, start) {
    start = parseInt(start);
    return input.slice(start);
  };
});

casinoApp.controller("GameController", function($scope, $http) {
  $scope.autoMode = "OFF";
  $scope.isAuto = false;
  $scope.spinResultNum = "－";

  $scope.autoModeChange = function() {
    $http.get("casino/autoModeChange").success( function( data ) {
      if($scope.isAuto) {
        $scope.autoMode = "OFF";
      } else {
        $scope.autoMode = "ON";
      }
      $scope.isAuto = data.result;

      var betTableCtrlScope = angular.element(betTable).scope();
      var spinResultHistoryTableCtrlScope = angular.element(spinResultHistoryTable).scope();
      var profitTableCtrlScope = angular.element(profitTable).scope();
      var monthlyProfitTableCtrlScope = angular.element(monthlyProfitTable).scope();
      if($scope.isAuto) {
        $scope.auto();

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
  $scope.profits = [];

  $scope.update = function(data) {
    $scope.profits = data;
  };

  $scope.getPeofitListAndUpdate = function() {
    $http.get("casino/getProfitList").success( function( data ) {
      console.log(data);
      // $scope.update(data);
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
  $scope.monthlyProfit = [];

  $scope.update = function(data) {
    $scope.monthlyProfit = data;
  };

  $scope.getMonthlyPeofitListAndUpdate = function() {
    $http.get("casino/getMonthlyProfitList").success( function( data ) {
      console.log(data);
      // $scope.update(data);
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
