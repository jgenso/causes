(function(angular){
  'use strict';

  var appContributors = angular.module('Contributors', ['ContributorsServer']);

  appContributors.controller("ContributorsController", function($scope, ServerFuncs, $filter, $log) {
    $log.log("CALLED");
    $scope.page = 0;
    $scope.count = 0;
    $scope.items = [];
    $scope.fetchPage = function() {
      ServerFuncs.fetchPage(page);
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
