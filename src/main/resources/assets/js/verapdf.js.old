$(document).ready(function(){

    var curFile = null;

    $(document).on("click", 'input#validate', function(e) {
        console.log('validatePdf called');

        var path = "http://localhost:8080/api/validate/" + $("#inputProfileId")[0].value + '/';
        var formData = new FormData();
        formData.append('file', curFile);

        $.ajax({
            type: 'POST',
            url: path,
            data: formData,
            cache : false,
            contentType : false,
            processData : false,
            accepts: {
                text: "application/json"
            },
            success: function (response) {
                $("article#result").removeClass('hidden');
                $("#result-content").html("");
                var respStr = JSON.stringify(response, undefined, 4);

                $('<pre/>', {text: respStr}).appendTo("#result-content");

            }
        });


    });

    $(document).on("click", 'input#getprofile', function(e) {
        var path = "/api/profiles/" + $("#inputProfileId")[0].value + '/';

        $.ajax({
            type: 'GET',
            url: path,
            cache : false,
            contentType : false,
            processData : false,
            accepts: {
                text: "application/json"
            },
            success: function (response) {
                $("article#result").removeClass('hidden');
                $("#result-content").html("");
                var respStr = JSON.stringify(response, undefined, 4);

                $('<pre/>', {text: respStr}).appendTo("#result-content");

            }
        });
    });

    $(document).on('change', '#inputPdfToValidate', function (e) {
        curFile = this.files[0];
    });

});