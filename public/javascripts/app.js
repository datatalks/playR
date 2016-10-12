// angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));

(function(angular) {
  'use strict';
  angular.module('app', ['ui.bootstrap', 'ui.xg', 'ngComponentRouter', 'new', 'reports', 'help', 'users'])

  // .config(function($locationProvider) {
  //   $locationProvider.html5Mode(true);
  // })

  .value('$routerRootComponent', 'app')

  // .run(function($http) {
  //   $http.defaults.headers.post = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'};
  // })

  .component('app', {
    templateUrl: '../template/app.html',
    $routeConfig: [
      {path: '/', name: 'Index', component: 'index'},
      {path: '/new', name: 'New', component: 'new'},
      {path: '/reports', name: 'Reports', component: 'reports' },
      {path: '/users/...', name: 'Users', component: 'users' },
      {path: '/help', name: 'Help', component: 'help' }
    ]
  })
  .controller('appCtrl', ['$http', '$location', '$rootRouter', function AppCtrl($http, $location, $rootRouter) {
    var ctrl = this;

    ctrl.role = [];

    this.isActive = function (path) {
      if (path == '/') {
        return $location.$$path == path;
      }
      return $location.$$path.indexOf(path) > -1;
    };

    this.foldornot = false;
    this.toggleSidebar = function (){
      this.foldornot = !this.foldornot;
    }

    $http({
        method: 'get',
        url: '/currentowner'
    }).then(function (responses) {
      if (responses.data.data == "null") {
        location.href = "./login.html";
        return;
      }
      if (!!responses.data.data.role){
        ctrl.role = responses.data.data.role;
      }
    }, function () {
      location.href = "./login.html";
    });
  }])
  .component('index', {
      templateUrl: '../template/about.html'
  });

  angular.module('users', [])
  .component('users', {
    template: '<ng-outlet></ng-outlet>',
    // templateUrl: '../template/users.html',
    $routeConfig: [
      {path: '/list', name: 'ListUser', component: 'listUser', useAsDefault: true },
      {path: '/add', name: 'AddUser', component: 'addUser' }
    ]
  })
  .service('userService', ['$http', function UserService($http) {
    this.add = function (item){
      return $http({
        method: 'post',
        url: '/addowner',
        data: {
          owner_nickName: item.owner_nickName,
          owner_realName: item.owner_realName,
          password: item.password,
          mobile: item.mobile,
          email: item.email
        }
      });
    };

    this.getList = function (){
      return $http({
        method: 'get',
        url: '/listowner'
      });
    }
  }])
  .component('listUser', {
    templateUrl: '../template/users.html'
  })
  .controller('usersCtrl', ['userService', function UsersCtrl ($userService){
    var ctrl = this;

    ctrl.list = [];
    ctrl.alert = 0;

    $userService.getList().then(function (response){
      ctrl.list = response.data.data;
    }, function (err) {
      ctrl.alert = 1;
      ctrl.rule = err.status;
      ctrl.errMsg = err.statusText;
    });
  }])
  .component('addUser', {
    templateUrl: '../template/addUser.html',
    bindings: { $router: '<' }
  })
  .controller('addUsersCtrl', ['userService', '$timeout', '$rootRouter', function AddUsersCtrl ($userService, $timeout, $rootRouter){
    var ctrl = this;

    ctrl.user = {};
    ctrl.alert = 0;
    ctrl.success = 0;

    ctrl.save = function (evt) {
      var $curBtn = $(evt.target);

      $curBtn.button('loading');
      $userService.add(ctrl.user).then(function (){
        $curBtn.button('reset');
        ctrl.success = 1;
        ctrl.successMsg = '保存成功！正跳转至列表页...';

        $timeout(function () {
          $rootRouter.navigate(['Users']);
        }, 3000);
      }, function (e) {
        ctrl.alert = 1;
        ctrl.rule = e.status;
        ctrl.errMsg = e.statusText;
        $curBtn.button('reset');
      })
    }
  }]);

  angular.module('new', [])
  .component('new', {
    templateUrl: '../template/new.html'
  })
  .controller('newCtrl', ['newService', '$timeout', '$rootRouter', '$scope', function NewCtrl($newService, $timeout, $rootRouter, $scope) {
    var ctrl = this;

    ctrl.alert = 0;
    ctrl.state = 1; // 未预览

    // datepicker
    ctrl.minDate = new Date();



    var editor = editormd("editor", {
      height: 500,
      watch : false,
      path : "../bower_components/editor.md/lib/", // Autoload modules mode, codemirror, marked... dependents libs path
      placeholder: "",
      toolbarIcons : function() {
          return [
              "undo", "redo", "|",
              "bold", "del", "italic", "quote", "uppercase", "lowercase", "|",
              "h1", "h2", "h3", "h4", "h5", "h6", "|",
              "list-ul", "list-ol", "hr", "|",
              "rmarkdown"
          ]
          return editormd.toolbarModes['simple']; // full, simple, mini
      },
      toolbarIconsClass : {
          rmarkdown : "rmarkdown"  // 如果没有图标，则可以这样直接插入内容，可以是字符串或HTML标签
      },
      toolbarIconTexts : {
          rmarkdown : "R"  // 如果没有图标，则可以这样直接插入内容，可以是字符串或HTML标签
      },
      toolbarHandlers : {
          rmarkdown : function(cm, icon, cursor, selection) {

              // 替换选中文本，如果没有选中文本，则直接插入
              cm.replaceSelection("\r\n```{r}" + selection + "\r\n\r\n```");

              // 如果当前没有选中的文本，将光标移到要输入的位置
              if(selection === "") {
                cm.setCursor(cursor.line + 2, cursor.ch);
              }
          }
      },
      lang : {
          toolbar : {
              rmarkdown : "Rmd"
          }
      }
    });

    //预览
    this.preview = function (evt) {
      var $curBtn = $(evt.target);
      var $preview = $('.editormd-preview');
      var $codeMirror = $('.CodeMirror');
      var reportContent = editor.getMarkdown();

      var $preview = editor.preview;
      var $codeMirror = editor.codeMirror;
      var $previewContainer = editor.previewContainer;

      ctrl.success = 0;
      ctrl.alert = 0;

      $curBtn.button('loading');
      $newService.preview(reportContent).then(function (response){
        $curBtn.button('reset');

        ctrl.state = 0;
        ctrl.success = 1;
        ctrl.successMsg = '预览成功！';

        $preview.show();
        $codeMirror.css("border-right", "1px solid #ddd").width(editor.editor.width() / 2);

        $previewContainer.html(response.data.data);
      }, function (e) {
        ctrl.state = 1;
        ctrl.alert = 1;
        ctrl.rule = e.status;
        ctrl.errMsg = e.statusText;

        $curBtn.button('reset');
      });
    }

    // 关闭预览
    this.closePreview = function () {
      var $preview = editor.preview;
      var $codeMirror = editor.codeMirror;
      var $previewContainer = editor.previewContainer;

      ctrl.success = 0;
      ctrl.alert = 0;
      ctrl.state = 1;

      $preview.hide();
      $codeMirror.css("border-right", "none").width(editor.editor.width())

      $previewContainer.empty();
    }

    // 保存
    this.save = function (evt) {
      var $curBtn = $(evt.target);
      var reportName = ctrl.reportName;
      var reportContent = editor.getMarkdown();

      ctrl.alert = 0;
      ctrl.success = 0;

      if (!reportName) {
        ctrl.alert = 1;
        ctrl.rule = '报告名称不能为空!';
        ctrl.errMsg = '请输入您的报告名';
        return;
      }

      if (!ctrl.execute_type) {
        ctrl.alert = 1;
        ctrl.rule = '报告执行类型不能为空!';
        ctrl.errMsg = '请选择您的报告执行类型';
        return;
      }

      if (!reportContent) {
        ctrl.alert = 1;
        ctrl.rule = '报告内容不能为空!';
        ctrl.errMsg = '请输入您的报告内容';
        return;
      }

      $curBtn.button('loading');
      $newService.add(reportName, reportContent, ctrl.execute_type, ctrl).then(function (response){
        $curBtn.button('reset');
        ctrl.success = 1;
        ctrl.successMsg = '保存成功！正跳转至列表页...';

        $timeout(function () {
          $rootRouter.navigate(['Reports']);
        }, 3000);
      }, function (e) {
        ctrl.alert = 1;
        ctrl.rule = e.status;
        ctrl.errMsg = e.statusText;
        $curBtn.button('reset');
      });
    }
  }])
  .service('newService', ['$http', function NewService($http) {
    this.add = function (reportName, reportContent, execute_type, opts){
      var postdata = {
        reportName: reportName,
        reportContent: reportContent,
        execute_type : execute_type
      };
      if (execute_type == "once") {
        postdata.once_scheduled_execute_time = opts.once_scheduled_execute_time;
      } else {
        postdata.circle_scheduled_start_time = opts.circle_scheduled_start_time;
        postdata.circle_scheduled_finish_time = opts.circle_scheduled_finish_time;
        postdata.circle_scheduled_interval_minutes = opts.circle_scheduled_interval_minutes;
      }

      return $http({
        method: 'post',
        url: '/report/add',
        data: postdata
      });
    };

    this.preview = function (reportContent){
      return $http({
        method: 'post',
        url: '/preview',
        data: {
          reportContent: reportContent
        }
      });
    }
  }]);

  angular.module('reports', ['ui.xg.pager'])
  .component('reports', {
    templateUrl: '../template/reports.html'
  })
  .controller('reportsCtrl', ['reportsService', function ReportsCtrl($reportsService) {
    var ctrl = this;

    ctrl.list = [];

    ctrl.pages = {
      pageSize: 20,
      pageNo: 1
    }

    var getList = function () {
      $reportsService.getList(ctrl.pages.pageNo, ctrl.pages.pageSize).then(function (responses) {
        if (responses.data.data != "null") {
          ctrl.list = responses.data.data;

          ctrl.pages.pageSize = responses.data.page.pageSize;
          ctrl.pages.pageNo = responses.data.page.currentPageNo || 1;
          ctrl.pages.totalCount = responses.data.page.totalCount;
        }
      })
    };
    getList();

    ctrl.pageChanged = function () {
        ctrl.pages.pageNo = ctrl.pages.pageNo;
        getList();
    }

  }])
  .service('reportsService', ['$http', function ($http){
    this.getList = function (pageNo, pageSize){
      return $http({
          method: 'get',
          url: '/report/list2',
          params: {
            pageNo: pageNo,
            pageSize: pageSize
          }
      });
    }
  }]);

  angular.module('help', [])
  .component('help', {
      templateUrl: '../template/help.html'
  });

})(window.angular);
