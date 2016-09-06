// angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));

(function(angular) {
  'use strict';
  angular.module('app', ['ngComponentRouter', 'new', 'reports', 'help'])

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
        url: '/currentowner'
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
  .controller('newCtrl', ['newService', function NewCtrl($newService) {
    var ctrl = this;

    ctrl.alert = 0;
    ctrl.state = 1; // 未预览

    var editor = editormd("editor", {
        height: 500,
        watch : false,
        saveHTMLToTextarea: true,
        path : "../bower_components/editor.md/lib/", // Autoload modules mode, codemirror, marked... dependents libs path
        toolbarIcons : function() {
            return [
                "undo", "redo", "|",
                "bold", "del", "italic", "quote", "uppercase", "lowercase", "|",
                "h1", "h2", "h3", "h4", "h5", "h6", "|",
                "list-ul", "list-ol", "hr", "|",
                "rmarkdown",
                "rpreveiw"
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

                //var cursor    = cm.getCursor();     //获取当前光标对象，同cursor参数
                //var selection = cm.getSelection();  //获取当前选中的文本，同selection参数

                // 替换选中文本，如果没有选中文本，则直接插入
                cm.replaceSelection("```{r}" + selection + "\r\n\r\n```");

                // 如果当前没有选中的文本，将光标移到要输入的位置
                if(selection === "") {
                    cm.setCursor(cursor.line + 1, cursor.ch);
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
      var reportName = (function () {
        var arr = editor.getMarkdown().match(/title:"([\S\s*]+)"/);

        return arr != null ? arr[1]: '';
      })();
      var reportContent = editor.getMarkdown();

      ctrl.alert = 0;
      ctrl.success = 0;

      if (!reportName) {
        ctrl.alert = 1;
        ctrl.rule = '报告名称不能为空!';
        ctrl.errMsg = '请在title的双引号内输入您的报告名';
        return;
      }

      $curBtn.button('loading');
      $newService.add(reportName, reportContent).then(function (response){
        $curBtn.button('reset');
        ctrl.success = 1;
        ctrl.successMsg = '保存成功！';
      }, function (e) {
        ctrl.alert = 1;
        ctrl.rule = e.status;
        ctrl.errMsg = e.statusText;
        $curBtn.button('reset');
      });
    }
  }])
  .service('newService', ['$http', function NewService($http) {
    this.add = function (reportName, reportContent){
      return $http({
        method: 'post',
        url: '/report/add',
        data: {
          reportName: reportName,
          reportContent: reportContent
        }
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
