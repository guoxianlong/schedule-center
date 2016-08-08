'use strict';

var adminLteApp = angular.module("adminLteApp", ['ngRoute']);
adminLteApp.config(function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/dashboard.html',
            controller: 'DashboardCtrl'
        })
        .when('/jdAuth', {
            templateUrl: 'views/jdauth.html',
            controller: 'JdCtrl'
        })
        .when('/jdStore', {
            templateUrl: 'views/jdStore.html',
            controller: 'JdStoreCtrl'
        })
        .when('/jdOrder', {
            templateUrl: 'views/jdOrder.html',
            controller: 'JdOrderCtrl'
        })
        .when('/jdParty', {
            templateUrl: 'views/jdParty.html',
            controller: 'JdPartyCtrl'
        })
        .when('/configure', {
            templateUrl: 'views/configure.html',
            controller: 'ConfigureCtrl'
        })
        .otherwise({
            redirectTo: '/'
        });
});

adminLteApp.controller('MenuCtrl', function ($scope, $http) {
    $http({url:"/mark/menu/policy.php",method:"GET"}).success(function(data) {
        console.log(data);
        $scope.menus = data;
    });
});

adminLteApp.controller('DashboardCtrl', function ($scope, $http) {
    $scope.name = "zhangsan";
    console.log("DashboardCtrl");
});

adminLteApp.controller('JdCtrl', function ($scope, $http) {

    console.log("JdCtrl");
});

adminLteApp.controller('JdStoreCtrl', function ($scope, $http) {
    $http({url:"/mark/jd/getOnlineShop.php",method:"GET"}).success(function(data) {
        console.log(data);
    });
});

adminLteApp.controller('JdOrderCtrl', function ($scope, $http) {
    $http({url:"/mark/jd/getHistoryOrder.php",method:"GET"}).success(function(data) {
        console.log(data);
    });
});

adminLteApp.controller('JdPartyCtrl', function ($scope, $http) {
    console.log("JdPartyCtrl");
});

adminLteApp.controller('ConfigureCtrl', function ($scope, $http) {
    console.log("ConfigureCtrl");
});
