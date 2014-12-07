(function(angular){
  'use strict';

  var appCause = angular.module('Cause');

  appCause.controller("ContributorsController", function($scope, ServerFuncs, $filter, $log) {
    $log.log("CALLED");
    $scope.page = 1;
    $scope.count = 0;
    $scope.itemsPerPage = 10;
    $scope.items = [];

    $scope.fetchPage = function() {
      ServerFuncs.fetchContributorsPage({page: $scope.page, itemsPerPage: $scope.itemsPerPage});
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
    $scope.fetchPage();
  });
})(window.angular);
