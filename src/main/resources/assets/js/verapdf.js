$(document).on('change', '.btn-file :file', function() {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, ''),
        rusha = new Rusha(),
        file = input.get(0).files[0],
        reader = new FileReader();
    reader.onload = function(e) {
        var rawData = reader.result;
        var digest = rusha.digest(rawData);
        input.trigger('fileselect', [numFiles, label, digest]);
    }
    reader.readAsBinaryString(file);
});

$(document).ready( function() {
    $('.btn-file :file').on('fileselect', function(event, numFiles, label, digest) {
        var input = $(this).parents('.input-group').find(':text'),
            log = numFiles > 1 ? numFiles + ' files selected' : label;
        
        if( input.length ) {
            input.val(log);
        } else {
            if( log ) alert(log);
        }
        getRequestDataType();
        $("#digest").val(digest);
        callPreflightService();
    });
});

$(document).on('click', "#type-requested > button", function() {
    var input = $(this);
    // Do nothing if the active view
    if (input.hasClass("btn-success")) return;
    // select the new button
    $("#type-requested > button.btn-success").removeClass("btn-success").addClass("btn-default");
    input.addClass("btn-success");
    // return if no file selected
    if ($("#filename").val()) callPreflightService();

});

function getRequestDataType() {
    return($("#type-requested > button.btn-success").attr("name"));
}

function callPreflightService() {
    var formData = new FormData($('form')[0]);

    $("#results").empty();
    var spinHtml = $("#spinner-template").html();
    $("#results").html(spinHtml);
    $.ajax({
        url:   '/api/validate/1b/',
        type:  'POST',
        data:  formData,
        dataType: getRequestDataType(),
        contentType: false,
        processData: false,
        success : function (data, textStatus, jqXHR) {
           $("#results").empty();
           if (getRequestDataType() != "html") {
                var preBlock = $("<pre>").text(jqXHR.responseText);
                $("#results").append(preBlock);
           } else {
                $("#results").html(jqXHR.responseText);
           }
        },
        error : function (jqXHR, textStatus, errorThrown) {
            $("#results").empty();
            var tempHtml = $("#error-template").html();
            $("#results").html(tempHtml);
            console.log('uploadAttachment error: ' + textStatus + errorThrown);
            console.log(jqXHR);
            console.log('uploadAttachment error: ' + textStatus + errorThrown);

        }
    });
}
