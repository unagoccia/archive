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

  $scope.autoModeChange = function() {
    if($scope.isAuto) {
      $scope.autoMode = "OFF";
      $scope.isAuto = false;
    } else {
      $scope.autoMode = "ON";
      $scope.isAuto = true;
    }
  };

  $scope.spin = function() {
    $http.get("casino/spin").success( function( data ) {
        $scope.result = data;
    });
  };
});

casinoApp.controller("BetTableController", function($scope) {
  $scope.bets = [{
    num:      "0",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "1",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "2",
    status:   "BET",
    spinNum:  "126",
    stakes:   "$83.2"
  }, {
    num:      "3",
    status:   "BET",
    spinNum:  "47",
    stakes:   "$5.8"
  }, {
    num:      "4",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "5",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "6",
    status:   "BET",
    spinNum:  "89",
    stakes:   "$24.7"
  }, {
    num:      "7",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "8",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "9",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "10",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "11",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "12",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "13",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "14",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "15",
    status:   "BET",
    spinNum:  "7",
    stakes:   "$0.7"
  }, {
    num:      "16",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "17",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "18",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "19",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "20",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "21",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "22",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "23",
    status:   "BET",
    spinNum:  "10",
    stakes:   "$1.0"
  }, {
    num:      "24",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "25",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "26",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "27",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "28",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "29",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "30",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "31",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "32",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "33",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "34",
    status:   "BET",
    spinNum:  "1",
    stakes:   "$0.1"
  }, {
    num:      "35",
    status:   "-",
    spinNum:  "-",
    stakes:   "-"
  }, {
    num:      "36",
    status:   "BET",
    spinNum:  "52",
    stakes:   "$7.1"
  }, ];
});

casinoApp.controller("SpinResultTableController", function($scope) {
  $scope.spinResults = [{
    id: 5,
    num: 23,
    recentHit: 4,
  }, {
    id: 4,
    num: 4,
    recentHit: 45,
  }, {
    id: 3,
    num: 31,
    recentHit: 256,
  }, {
    id: 2,
    num: 10,
    recentHit: 86,
  }, {
    id: 1,
    num: 23,
    recentHit: 12,
  }, ];
});

casinoApp.controller("ProfitTableController", function($scope) {
  $scope.profits = [{
    id: 5,
    date: "2015/09/10",
    num: 23,
    recentHit: 4,
    profit: "$3.2",
  }, {
    id: 4,
    date: "2015/09/10",
    num: 32,
    recentHit: 45,
    profit: "$1.6",
  }, {
    id: 3,
    date: "2015/09/09",
    num: 27,
    recentHit: 42,
    profit: "$2.2",
  }, {
    id: 2,
    date: "2015/09/09",
    num: 18,
    recentHit: 86,
    profit: "$2.7",
  }, {
    id: 1,
    date: "2015/09/09",
    num: 7,
    recentHit: 11,
    profit: "$3.5",
  }, ];
});

casinoApp.controller("MonthlyProfitTableController", function($scope) {
  $scope.monthlyProfit = [{
    month: "2015/09",
    mProfit: "$2,564.2",
  }, {
    month: "2015/08",
    mProfit: "$11,524.7",
  }, {
    month: "2015/07",
    mProfit: "$11,640.1",
  }, {
    month: "2015/06",
    mProfit: "$11,920.6",
  }, {
    month: "2015/05",
    mProfit: "$23,400.2",
  }, ];
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
