(function(angular){
  'use strict';

  var appContributors = angular.module('Contributors', ['ContributorsServer', 'ui.bootstrap']);

  appContributors.controller("ContributorsController", function($scope, ServerFuncs, $filter, $log) {
    $log.log("CALLED");
    $scope.page = 0;
    $scope.count = 0;
    $scope.itemsPerPage = 10;
    $scope.items = [];

    $scope.fetchPage = function() {
      ServerFuncs.fetchPage(page);
    };

    $scope.pageChanged = function() {
      $log.log('Page changed to: ' + $scope.currentPage);
      $scope.fetchPage();
    };

    $scope.$on('after-fetch-page', function (event, data) {
      $scope.$apply(function () {
        $scope.items = data.items;
        $scope.count = data.count;
      });
    });
  });
  $scope.fetchPage();
})
