(function(angular){
  'use strict';

  var appContributors = angular.module('Cause', ['CauseServer', 'ui.bootstrap']);

  appContributors.controller("CauseController", function($scope, ServerParams, ServerFuncs, $filter, $log) {
    $log.log("CALLED");
    $scope.cause = ServerParams.cause;
    $scope.isFollower = ServerParams.isFollower;

    $scope.fetchCause = function() {
      ServerFuncs.fetchCause(ServerParams.cause);
    };

    $scope.contribute = function(quantity, resource) {
      ServerFuncs.contribute({quantity: quantity, resource: resource});
    };

    $scope.follow = function(sms, email) {
      ServerFuncs.follow({sms: sms, email: email});
    };

    $scope.$on('after-fetch-cause', function (event, data) {
      $scope.$apply(function () {
        $scope.cause = data;
      });
    });

    $scope.$on('after-contribute', function (event, data) {
      $log.log(data);
    });

    $scope.$on('after-follow', function (event, data) {
      $scope.$apply(function () {
        $scope.isFollower = data.isFollower;
      });
    });
  });
})
