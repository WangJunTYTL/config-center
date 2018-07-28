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
        $.post("/category/property/value/update",
               {"cid": cid, "pid": pid, "newValue": newValue, "desc": desc}, function (data) {
                if (data.code == 200) {
                    $("#propertyValueBody")
                        .load("/category/property/value/list", {"cId": cid});
                } else {
                    alert(data.message);
                }
            })
    })

});