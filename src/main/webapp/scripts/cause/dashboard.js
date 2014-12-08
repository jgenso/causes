(function(angular){
  'use strict';

  var appCause = angular.module('CauseDashboard', ['CauseDashboardServer', 'ui.bootstrap']);

  appCause.controller("CauseDashboardController", function($scope, ServerParams, ServerFuncs, $filter, $log, $modal) {
    $log.log("CALLED");
    $scope.cause = ServerParams.cause;
    $scope.pendingLimit = 10;

    $scope.findResourceForCommittedResource = function(resourceId) {
      return _.find($scope.cause.resources, function(item) { return item._id === resourceId;});
    };

    $scope.approve = function(req) {
      ServerFuncs.approve({resource: req._id});
    };

    $scope.cancel = function(req) {
      ServerFuncs.cancel({resource: req._id});
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

    $scope.filterPending = function(item) {
        return item.status === 'WaitingExecuted' || item.status === 'Committed';
    };

    $scope.showMorePendingApprovals = function($event) {
      $scope.pendingLimit = $scope.pendingLimit + 10;
      $event.preventDefault();
    };

    $scope.pendingTotal = function() {
      return _.size(_.filter($scope.cause.committedResources, function(item) { return item.status === 'Executed'; }));
    };

    $scope.$on('after-fetch-cause', function (event, data) {
      $scope.$apply(function() {
        $scope.cause = data;
      });
    });

    $scope.$on('after-approve', function (event, data) {
      $scope.$apply(function() {
        var item = _.find($scope.cause.committedResources, function(cr) { return cr._id === data._id;})
        item.status = data.status;
      });
    });

    $scope.$on('after-cancel', function (event, data) {
      $scope.$apply(function() {
        $scope.cause.committedResources = _.filter($scope.cause.committedResources, function(cr) { return cr._id === data._id;});
      });
    });
  });
})(window.angular);
