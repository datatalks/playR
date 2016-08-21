$(function () {
    var toast = function (){
        var $toast = $('#toast');
        var $toastCnt = $toast.find('.weui_toast_content');
        var timer = null;
        var instancer = false;

        var show = function (content, cb) {
            instancer = true;

            $toast.hide();
            clearTimeout(timer);

            $toast.show();
            $(toastCnt).text(content);

            timer = setTimeout(function (){
                $toast.hide();
                typeof cb === 'function' && cb();

                instancer = false;
            }, 2000);
        }

        return {
            show: show
        }
    };

    $('.weui_tab').on('click', '.weui_tabbar_item', function () {
        $('.weui_tab .weui_tab_bd').removeClass('show enter').addClass('hide leave');
        $('.weui_tab .weui_tab_bd').eq($(this).index()).removeClass('hide leave').addClass('show enter');
        $(this).addClass('weui_bar_item_on').siblings('.weui_bar_item_on').removeClass('weui_bar_item_on');
    });
    var editor = editormd("editor", {
        height: 300,
        watch : false,
        path : "../public/bower_components/editor.md/lib/", // Autoload modules mode, codemirror, marked... dependents libs path
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

    // 保存&预览
    $('.weui_tab_bd').on('click', '.weui_btn', function () {
        var $this = $(this);
        var curId = this.id;
        var postUrl = '';
        var isPreveiw = false;

        // 预览
        if(curId === 'previewBtn') {
            postUrl = '/previewR';
            isPreveiw = true;
        }

        // 保存
        if(curId === 'saveBtn') {
            postUrl = '/reportR';
        }

        if (postUrl === '' || editor.getMarkdown() === '') {
            return;
        }

        $.post(postUrl, {
            'owner': '',
            'reportR': editor.getMarkdown()
        }, function (response) {
            if (isPreveiw) {
                toast.show(response.message, function () {
                    $toast.hide();
                    location.href = response.data.url;
                });
            } else {
                toast.show(response.message, function () {
                    $toast.hide();
                });
            }
        });
    });
});
