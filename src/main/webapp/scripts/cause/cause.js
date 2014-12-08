(function(angular){
  'use strict';

  var appCause = angular.module('Cause', ['CauseServer', 'ui.bootstrap']);

  appCause.controller("CauseController", function($scope, ServerParams, ServerFuncs, $filter, $log, $modal, $window) {
    $log.log("CALLED");
    $scope.cause = ServerParams.cause;
    $scope.isFollower = ServerParams.isFollower;
    $scope.isLogged = ServerParams.isLogged;
    $scope.isOrganizer = ServerParams.isOrganizer;
    $scope.sms = false;
    $scope.email = false;

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

    $scope.calcTotalPercentageCommitted = function() {
      return _.reduce(
          $scope.cause.committedResources, function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / _.reduce($scope.cause.resources, function(base, r) { return base + r.quantity;}, 0);
    };

    $scope.calcTotalPercentageDelivered = function() {
     return _.reduce(
        _.filter(
          $scope.cause.committedResources, function(cr){ return  cr.status === 'Executed'; }
        ), function(base, cr){ return base  + cr.quantity}, 0
      ) * 100.0 / _.reduce($scope.cause.resources, function(base, r) { return base + r.quantity}, 0);
    };

    $scope.showDetails = function(resource, $event) {
      if (resource.showDetails) {
          resource.showDetails = false;
      } else {
          resource.showDetails = true;
      }
      $event.preventDefault();
    };

    $scope.fetchCause = function() {
      ServerFuncs.fetchCause(ServerParams.cause);
    };

    $scope.contribute = function(quantity, resource) {
      ServerFuncs.contribute({quantity: parseInt(quantity), resource: resource._id});
    };

    $scope.follow = function(sms, email) {
      ServerFuncs.follow({sms: sms, email: email});
    };

    $scope.unfollow = function() {
      ServerFuncs.unfollow();
    };

    $scope.addComment = function(text) {
      ServerFuncs.addComment({text: text});
    };

    $scope.addNews = function(title, description) {
      ServerFuncs.addNews({title: title, description: description});
    };

    $scope.$on('after-fetch-cause', function (event, data) {
      $scope.$apply(function () {
        $scope.cause = data.cause;
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

    $scope.$on('after-add-comment', function (event, data) {
      $log.log(data);
      $scope.reloadPage();
    });

    $scope.reloadPage = function() {
       $window.location.reload();
    }

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

    $scope.openContributeModal = function (resource) {
      var modalInstance = $modal.open({
        templateUrl: 'contributeModal.html',
        controller: 'ContributeModalInstanceCtrl',
        resolve: {
          data: function() {
            return {quantity: resource.quantity, resource: resource};
          }
        }
      });

      modalInstance.result.then(function (data) {
        $scope.contribute(data.quantity, data.resource);
      }, function () {
        $log.info('Modal dismissed at: ' + new Date());
      });
    };

    $scope.openCommentModal = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'commentModal.html',
        controller: 'CommentModalInstanceCtrl',
        size: size,
        resolve: {
          data: function() {
            return {text: $scope.text};
          }
        }
      });

      modalInstance.result.then(function (data) {
        $log.log(data);
        $scope.addComment(data.text);
      }, function () {
        $log.info('Modal dismissed at: ' + new Date());
      });
    };

    $scope.openNewsModal = function (size) {
      var modalInstance = $modal.open({
        templateUrl: 'newsModal.html',
        controller: 'NewsModalInstanceCtrl',
        size: size,
        resolve: {
          data: function() {
            return {title: $scope.title, description: $scope.description};
          }
        }
      });

      modalInstance.result.then(function (data) {
        $log.log(data);
        $scope.addNews(data.title, data.description);
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

  appCause.controller('ContributeModalInstanceCtrl', function ($scope, $modalInstance, $log, data) {

    $scope.quantity = data.quantity;
    $scope.resource = data.resource;

    $scope.ok = function () {
      $modalInstance.close({quantity: $scope.quantity, resource: $scope.resource});
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });

  appCause.controller('CommentModalInstanceCtrl', function ($scope, $modalInstance, $log) {

    $scope.text = '';

    $scope.ok = function () {
      $modalInstance.close({text: $scope.text});
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });

  appCause.controller('NewsModalInstanceCtrl', function ($scope, $modalInstance, $log) {

    $scope.title = '';
    $scope.description = '';

    $scope.ok = function () {
      $modalInstance.close({title: $scope.title, description: $scope.description});
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });

})(window.angular);
