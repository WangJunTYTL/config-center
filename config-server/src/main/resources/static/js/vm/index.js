$(document).ready(function () {
    $("body").delegate(".updatePropertyValue", 'click', function () {
        var key = $(this).html();
        var cid = $(this).attr("cid");
        var pid = $(this).attr("pid");
        var desc = $(this).attr("desc");
        var value = $(this).attr("v");
        $("#propertyKey").val(key);
        $("#keyPropertyId").val(pid);
        $("#categoryId").val(cid);
        $("#keyPropertyNewValue").attr("placeholder", value);
        $("#keyPropertyDesc").attr("placeholder", desc);
        $("#myModal").modal('show');
    });

    $("body").delegate("#keyPropertyValueUpdateMenu", "click", function () {
        var cid = $("#categoryId").val();
        var pid = $("#keyPropertyId").val();
        var newValue = $("#keyPropertyNewValue").val();
        var desc = $("#keyPropertyDesc").val();
        $("#myModal").modal('hide');
        $.post("/config/update-kv",
               {"cid": cid, "pid": pid, "newValue": newValue, "desc": desc}, function (data) {
                if (data.code == 200) {
                    $("#propertyValueBody")
                        .load("/config/get-kv-list", {"cId": cid});
                } else {
                    alert(data.message);
                }
            })
    })

    $("body").delegate("#insertKeyValueMenu", "click", function () {
        $("#insertCategoryId").val($(this).attr("cid"));
        $("#insertKeyValueModal").modal('show');
    })

    $("body").delegate("#insertKeyValueSaveMenu", "click", function () {
        var cid = $("#insertCategoryId").val();
        var key = $("#insertPropertyKey").val();
        var value = $("#insertPropertyValue").val();
        var desc = $("#insertPropertyDesc").val();
        $("#insertKeyValueModal").modal('hide');

        $.post("/config/insert-kv",
               {"cid": cid, "key": key, "value": value, "desc": desc}, function (data) {
                if (data.code == 200) {
                    $("#propertyValueBody")
                        .load("/config/get-kv-list", {"cId": cid});
                } else {
                    alert(data.message);
                }
            });
    })

    $("body").delegate("#deleteKeyValueMenu", "click", function () {
        $("#deleteCategoryId").val($(this).attr("cid"));
        $("#deleteKeyValueModal").modal('show');
    })

    $("body").delegate("#deleteKeySaveMenu", "click", function () {
        var cid = $("#deleteCategoryId").val();
        var key = $("#deletePropertyKey").val();
        $("#deleteKeyValueModal").modal('hide');

        $.post("/config/delete-key",
               {"cid": cid, "key": key}, function (data) {
                if (data.code == 200) {
                    $("#propertyValueBody")
                        .load("/config/get-kv-list", {"cId": cid});
                } else {
                    alert(data.message);
                }
            });
    })

    $("body").delegate("#insertCategoryMenu", "click", function () {
        $("#parentCategoryId").val($(this).attr("cid"));
        $("#insertCategoryModal").modal('show');
    })

    $("body").delegate("#insertCategorySaveMenu", "click", function () {
        var cid = $("#parentCategoryId").val();
        var cname = $("#insertCategoryName").val();
        $("#insertCategoryModal").modal('hide');

        $.post("/config/insert-category",
               {"cid": cid, "cname": cname}, function (data) {
                if (data.code == 200) {
                    alert("子目录添加成功，需要刷新页面查看")
                    window.location.replace("/config/index");
                } else {
                    alert(data.message);
                }
            });
    })

    $("body").delegate("#deleteCategoryMenu", "click", function () {
        $("#deleteCurrentCategoryId").val($(this).attr("cid"));
        $("#deleteCategoryModal").modal('show');
    })

    $("body").delegate("#deleteCategorySaveMenu", "click", function () {
        var cid = $("#deleteCurrentCategoryId").val();
        var cname = $("#deleteCategoryName").val();
        $("#deleteCategoryModal").modal('hide');

        $.post("/config/delete-category",
               {"cid": cid, "cname": cname}, function (data) {
                if (data.code == 200) {
                    alert("目录删除成功，需要刷新页面查看")
                    window.location.replace("/config/index");
                } else {
                    alert(data.message);
                }
            });
    })

});