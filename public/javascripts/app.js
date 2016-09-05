angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));

(function(angular) {
  'use strict';
  angular.module('app', ['ngComponentRouter', 'new', 'reports', 'help'])

  // .config(function($locationProvider) {
  //   $locationProvider.html5Mode(true);
  // })

  .value('$routerRootComponent', 'app')

  .component('app', {
    templateUrl: '../template/app.html',
    $routeConfig: [
      {path: '/', name: 'Index', component: 'index'},
      {path: '/new', name: 'New', component: 'new'},
      {path: '/reports', name: 'Reports', component: 'reports' },
      {path: '/help', name: 'Help', component: 'help' }
    ]
  })
  .controller('appCtrl', ['$http', '$location', function AppCtrl($http, $location) {
    var ctrl = this;

    this.isActive = function (path) {
      return $location.$$path == path;
    };

    $http({
        method: 'get',
        // url: '/currentowner'
        url: '/test.json'
    }).then(function (responses) {
      if (responses.data.data.msg == '获取成功'){
        ctrl.owner_realName = responses.data.owner_realName;
      }
    });
  }])
  .component('index', {
      templateUrl: '../template/index.html'
  });

  angular.module('new', [])
  .component('new', {
    templateUrl: '../template/new.html'
  })
  .controller('newCtrl', ['$http', function NewCtrl($http) {
    var ctrl = this;
  }]);

  angular.module('reports', [])
  .component('reports', {
    templateUrl: '../template/reports.html'
  })
  .controller('reportsCtrl', ['$http', function ReportsCtrl($http) {
    var ctrl = this;

    ctrl.list = [];

    $http({
        method: 'get',
        url: '/report/list'
    }).then(function (responses) {
      if (responses.data.data.length > 0){
        ctrl.list = responses.data.data;
      }
    });

  }]);

  angular.module('help', [])
  .component('help', {
      templateUrl: '../template/help.html'
  });

})(window.angular);
