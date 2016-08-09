$(function () {
    $('.weui_tab').on('click', '.weui_tabbar_item', function () {
        $('.weui_tab .weui_tab_bd').removeClass('show enter').addClass('hide leave');
        $('.weui_tab .weui_tab_bd').eq($(this).index()).removeClass('hide leave').addClass('show enter');
        $(this).addClass('weui_bar_item_on').siblings('.weui_bar_item_on').removeClass('weui_bar_item_on');
    });


    var editor = editormd("editor", {
        height: 640,
        watch : false,
        path : "../public/bower_components/editor.md/lib/", // Autoload modules mode, codemirror, marked... dependents libs path
        toolbarIcons : function() {
            return editormd.toolbarModes['simple']; // full, simple, mini
        }
    });
});
