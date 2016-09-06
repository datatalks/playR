angular.element(document.getElementsByTagName('head')).append(angular.element('<base href="' + window.location.pathname + '" />'));

(function(angular) {
  'use strict';
  angular.module('app', ['ngComponentRouter', 'new', 'reports', 'help'])

  // .config(function($locationProvider) {
  //   $locationProvider.html5Mode(true);
  // })

  .value('$routerRootComponent', 'app')

  .run(function($http) {
    $http.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
  })

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
  .controller('newCtrl', ['$http', function NewCtrl($http) {
    var ctrl = this;

    this.state = 1; // 未预览

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
            rmarkdown : "rmarkdown",  // 如果没有图标，则可以这样直接插入内容，可以是字符串或HTML标签
            rpreveiw: "fa-eye-slash"
        },
        toolbarIconTexts : {
            rmarkdown : "R",  // 如果没有图标，则可以这样直接插入内容，可以是字符串或HTML标签
            rpreveiw: "预览"
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
            },
            rpreveiw: function () {
              var $preview = $(this.preview);
              var $codeMirror = $(this.codeMirror);

              console.log(this.activeIcon[0].className);
              if (this.activeIcon[0].className == 'fa fa-eye-slash'){
                $preview.show();
                $codeMirror.css("border-right", "1px solid #ddd").width(this.editor.width() / 2);
                $preview.find('.editormd-preview-container').html(editor.getHTML());

                this.activeIcon.removeClass('fa-eye-slash').addClass('fa-eye');
              } else {
                console.log($codeMirror);
                this.activeIcon.removeClass('fa-eye').addClass('fa-eye-slash');
                $preview.hide();

                $codeMirror.css("border-right", "none").width(this.editor.width());
              }
            }
        },
        lang : {
            toolbar : {
                rmarkdown : "Rmd",
                rmarkdown : "Rpreview"
            }
        }
    });

    //预览
    this.preview = function () {
      var $preview = editor.preview;
      var $codeMirror = editor.codeMirror[0];

      console.log(typeof editor.preview)

      if (this.state) {
        $preview.show();

        $codeMirror.css("border-right", "1px solid #ddd").width(editor.width() / 2);
        $('.editormd-preview .editormd-preview-container').html(editor.getHTML());

        this.state = 0;
      } else {
        $preview.hide();
        $codeMirror.css("border-right", "none").width(this.editor.width());

        this.state = 1;
      }

    }

    // 保存
    this.save = function () {
      $('.editormd-preview').show();
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
