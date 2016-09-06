// angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));

(function(angular) {
  'use strict';
  angular.module('login', [])
  .controller('loginCtrl', ['$http', function LoginCtrl($http) {
    var ctrl = this;

    this.save = function () {
      $http({
          method: 'post',
          url: '/login',
          data: {
            owner_nickName: this.owner_nickName,
            password: this.password
          }
      }).then(function (responses) {
        debugger;
        if (responses.data.data != 'null'){
          location.href = './';
        } else {
          ctrl.errMsg = responses.data.message;
        }
      }, function (e) {
        ctrl.errMsg = e.status + " " + e.statusText;
      });
    }
  }]);
})(window.angular);
