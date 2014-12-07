(function(angular){
  'use strict';

  var appCause = angular.module('Cause', ['CauseServer', 'ui.bootstrap']);

  appCause.controller("CauseController", function($scope, ServerParams, ServerFuncs, $filter, $log, $modal) {
    $log.log("CALLED");
    $scope.cause = ServerParams.cause;
    $scope.isFollower = ServerParams.isFollower;
    $scope.isLogged = ServerParams.isLogged;
    $scope.sms = false;
    $scope.email = false;

    $scope.fetchCause = function() {
      ServerFuncs.fetchCause(ServerParams.cause);
    };

    $scope.contribute = function(quantity, resource) {
      ServerFuncs.contribute({quantity: quantity, resource: resource});
    };

    $scope.follow = function(sms, email) {
      ServerFuncs.follow({sms: sms, email: email});
    };

    $scope.unfollow = function() {
      ServerFuncs.unfollow();
    };

    $scope.$on('after-fetch-cause', function (event, data) {
      $scope.$apply(function () {
        $scope.cause = data;
      });
    });

    $scope.$on('after-contribute', function (event, data) {
      $log.log(data);
      $scope.cause = data.cause;
    });

    $scope.$on('after-follow', function (event, data) {
      $scope.$apply(function () {
        $scope.isFollower = data.isFollower;
        $scope.cause = data.cause;
      });
    });

    $scope.$on('after-unfollow', function (event, data) {
      $scope.$apply(function () {
        $scope.isFollower = data.isFollower;
        $scope.cause = data.cause;
      });
    });

    $scope.openFollowModal = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'followModal.html',
        controller: 'FollowModalInstanceCtrl',
        size: size,
        resolve: {
          data: function() {
            return {sms: $scope.sms, email: $scope.email};
          }
        }
      });

      modalInstance.result.then(function (data) {
        $log.log(data);
        $scope.follow(data.sms, data.email);
      }, function () {
        $log.info('Modal dismissed at: ' + new Date());
      });
    };
  });

  appCause.controller('FollowModalInstanceCtrl', function ($scope, $modalInstance, $log) {

    $scope.sms = false;
    $scope.email = false;

    $scope.ok = function () {
      $modalInstance.close({sms: $scope.sms, email: $scope.email});
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });
})(window.angular);
