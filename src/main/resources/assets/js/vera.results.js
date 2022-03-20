/**
* @namespace JS Encapsulation of information required to render PDF/A validtion results.
*/
var pdfaValidator = {
  // validation result
  result: null,
  status: null,
  details: null,
  getDetails: function (callback, contentType = 'json') {
    $.ajax({
      url: '/api/validate/details/',
      type: 'GET',
      success: function (data, textStatus, jqXHR) {
        console.log(jqXHR)
        console.log(data)
        pdfaValidator.details = data
        callback()
      }
    })
  },
  validate: function (formData, flavour, callback, contentType = 'json') {
    $.ajax({
      url: '/api/validate/' + flavour + '/',
      type: 'POST',
      data: formData,
      dataType: contentType,
      contentType: false,
      processData: false,
      success: function (data, textStatus, jqXHR) {
        pdfaValidator.result = jqXHR.responseText
        callback()
      },
      error: function (jqXHR, textStatus, errorThrown) {
        $('#results').empty()
        var template = $('#error-template').html()
        Mustache.parse(template)   // optional, speeds up future uses
        var rendered = Mustache.render(template, jqXHR)
        $('#results').html(rendered)
        console.log('Validation Error: ' + textStatus + errorThrown)
        console.log(jqXHR)
      }
    })
  }
}
