var casinoApp = angular.module('casinoApp', []);

casinoApp.controller("GameController", function($scope) {
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
  }

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

casinoApp.controller("PagingController", function($scope) {
  $scope.itemsPerPage = 3;
  $scope.currentPage = 1;
  $scope.friends = [{
    id: 1,
    name: "相田",
    age: 20,
    address: "東京都品川区"
  }, {
    id: 2,
    name: "伊藤",
    age: 55,
    address: "神奈川県横浜市"
  }, {
    id: 3,
    name: "上野",
    age: 20,
    address: "埼玉県川越市"
  }, ];
  $scope.range = function() {
    $scope.maxPage = Math.ceil($scope.friends.length / $scope.itemsPerPage);
    var ret = [];
    for (var i = 1; i <= $scope.maxPage; i++) {
      ret.push(i);
    }
    return ret;
  };
  $scope.setPage = function(n) {
    $scope.currentPage = n;
  };
  $scope.prevPage = function() {
    if ($scope.currentPage > 1) {
      $scope.currentPage--;
    }
  };
  $scope.nextPage = function() {
    if ($scope.currentPage < maxPage) {
      $scope.currentPage++;
    }
  };
  $scope.prevPageDisabled = function() {
    return $scope.currentPage === 1 ? "disabled" : "";
  };

  $scope.nextPageDisabled = function() {
    return $scope.currentPage === $scope.maxPage ? "disabled" : "";
  };
});
casinoApp.filter('offset', function() {
  return function(input, start) {
    start = parseInt(start);
    return input.slice(start);
  };
});
