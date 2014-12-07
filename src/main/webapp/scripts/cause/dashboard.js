(function(angular){
  'use strict';

  var appCause = angular.module('CauseDashboard', ['CauseDashboardServer', 'ui.bootstrap']);

  appCause.controller("CauseDashboardController", function($scope, ServerParams, ServerFuncs, $filter, $log, $modal) {
    $log.log("CALLED");
    $scope.cause = ServerParams.cause;

    $scope.approve = function(req) {
      ServerFuncs.approve({req: req});
    };

    $scope.calcPercentageCommitted = function(resource) {
      return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return cr.resource === resource._id; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / resource.quantity;
    };

    $scope.calcPercentageDelivered = function(resource) {
      return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return cr.resource === resource._id && cr.status === 'Executed'; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / resource.quantity;
    };

    $scope.calcTotalCommitted = function(resource) {
      return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return cr.resource === resource._id; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      );
    };

    $scope.calcTotalDelivered = function(resource) {
      return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return cr.resource === resource._id && cr.status === 'Executed'; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      );
    };

    $scope.calcTotalPercentageCommitted = function() {
      return _.reduce(
          $scope.cause.committedResources, function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / $scope.total();
    };

    $scope.calcTotalPercentageDelivered = function() {
     return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return  cr.status === 'Executed'; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / $scope.total();
    };

    $scope.calcTotalCommitted = function() {
      return _.reduce(
          $scope.cause.committedResources, function(base, cr){ return base  + cr.quantity}, 0
      );
    };

    $scope.calcTotalDelivered = function() {
     return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return  cr.status === 'Executed'; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      );
    };

    $scope.total = function() {
      return _.reduce($scope.cause.resources, function(base, r) { return base + r.quantity}, 0);
    }

    $scope.fetchCause = function() {
      ServerFuncs.fetchCause(ServerParams.cause);
    };

    $scope.$on('after-fetch-cause', function (event, data) {
      $scope.$apply(function () {
        $scope.cause = data;
      });
    });
  });
})(window.angular);
