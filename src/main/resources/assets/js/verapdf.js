var errorOnLoadType = false;
$(document).on('change', '.btn-file :file', function () {
  let input = $(this)
  let numFiles = input.get(0).files ? input.get(0).files.length : 1
  let label = input.val().replace(/\\/g, '/').replace(/.*\//, '')
  let rusha = new Rusha()
  let file = input.get(0).files[0]
  let reader = new FileReader()
  reader.onload = function (e) {
    var selectedFile = $('#fileInput')[0].files[0];
    var allowedTypes = ['application/pdf'];

    if (!allowedTypes.includes(selectedFile.type)) {
      $('#filename').val('Invalid file type. Please upload a PDF file.');
      $('#sha1Hex').val('');
      $('#filename').addClass("error-form-data");
      errorOnLoadType = true;
      $('.nextBtn').hide();
    }else{
      if($("#filename").hasClass( "error-form-data" )){
        $('#filename').removeClass("error-form-data");
      }
      let rawData = reader.result
      let digest = rusha.digest(rawData)
      input.trigger('fileselect', [numFiles, label, digest])

      let logInfo = numFiles > 1 ? numFiles + ' files selected' : label
      if (input.length) {
        $('#filename').val(logInfo)
      } else {
        if (logInfo) alert(logInfo)
      }
      $('#sha1Hex').val(digest)
      errorOnLoadType = false;
      $('.nextBtn').show();
    }
  }
  $('a[href="#validate"]').attr("disabled","disabled");
  $('#configure-validator-header').text(`Configure Validator for ${$('#fileInput')[0].files[0].name}`);
  reader.readAsBinaryString(file)
})

$(document).ready(function () {
  pdfaValidator.getDetails(function () {
    let footer = $('<p>').text(pdfaValidator.details.description + ' v' + pdfaValidator.details.version)
    $('#footer').append(footer)
  })
  pdfaValidator.getRelease(function () {
    let footer = $('<p>').text(pdfaValidator.release.id + ' v' + pdfaValidator.release.version + ' ' + new Date(pdfaValidator.release.buildDate).toLocaleString())
    $('#footer').append(footer)
  })
  $('.btn-file :file').on('fileselect', function (event, numFiles, label, digest) {
    let input = $(this).parents('.input-group').find(':text')
    let log = numFiles > 1 ? numFiles + ' files selected' : label

    if (input.length) {
      input.val(log)
      if (!errorOnLoadType) $('.nextBtn').show();
    } else {
      if (log) alert(log)
    }
    $('#sha1Hex').val(digest)
  })
})

$(document).ready(function () {
  if($('#fileInput')[0].files.length === 0 ){
    $('.nextBtn').hide();
  }
  let navListItems = $('div.setup-panel div a')
  let allWells = $('.setup-content')
  var allPreviousBtn = $('.previousBtn')
  var allNextBtn = $('.nextBtn')

  allWells.hide()

  navListItems.click(function (e) {
    e.preventDefault()
    var $target = $($(this).attr('href'))
    var $item = $(this)

    if (!$item.hasClass('disabled')) {
      navListItems.removeClass('btn-primary').addClass('btn-default')
      $item.addClass('btn-primary')
      allWells.hide()
      $target.show()
      $target.find('input:eq(0)').focus()
    }
  })

  allNextBtn.click(function () {
    var curStep = $(this).closest('.setup-content')
    var curStepBtn = curStep.attr('id')
    var nextStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().next().children('a')
    var curInputs = curStep.find("input[type='text'],input[type='url']")
    var isValid = true
    $('.form-group').removeClass('has-error')
    for (var i = 0; i < curInputs.length; i++) {
      if (!curInputs[i].validity.valid) {
        isValid = false
        $(curInputs[i]).closest('.form-group').addClass('has-error')
      }
    }

    if (isValid) {
      nextStepWizard.removeAttr('disabled')
      nextStepWizard[0].click();
    }
    if (curStepBtn === 'configure') {
      callVeraPdfService()
    }
  })

  allPreviousBtn.click(function () {
    var curStep = $(this).closest('.setup-content')
    var curStepBtn = curStep.attr('id')
    var nextStepWizard = $('div.setup-panel div a[href="#' + curStepBtn + '"]').parent().prev().children('a')
    var curInputs = curStep.find("input[type='text'],input[type='url']")
    var isValid = true
    $('.form-group').removeClass('has-error')
    for (var i = 0; i < curInputs.length; i++) {
      if (!curInputs[i].validity.valid) {
        isValid = false
        $(curInputs[i]).closest('.form-group').addClass('has-error')
      }
    }

    if (isValid) {
      nextStepWizard.removeAttr('disabled')
      nextStepWizard[0].click();
    }
  })

  $('#download-results-btn').hide();

  $('div.setup-panel div a.btn-primary').trigger('click')
})

// Default profile flavour to use for validation
var flavour = 'auto'

function changeFlavour (newFlavour) {
  flavour = newFlavour
}

var outputFormat = 'html'

function changeOutputFormat (newFormat) {
  outputFormat = newFormat;
}

function callVeraPdfService () {
  var formData = new FormData($('form')[0])

  $('#results').empty()
  var spinHtml = $('#spinner-template').html()
  $('#results').html(spinHtml)
  pdfaValidator.validate(formData, flavour, function () {
    $.when(renderResult()).then(addFileConfigurationToResult()).then((showDownloadBtn()));
  }, outputFormat)
}

function addFileConfigurationToResult () {
  $("#result-details").text(`Validation results for ${$('#fileInput')[0].files[0].name} are shown below.`);
}

function showDownloadBtn () {
  $('#download-results-btn').show();
}

function renderResult () {
  $('#results').empty()
  if (outputFormat === 'html') {
    $('#results').html(pdfaValidator.result)
  } else {
    var preBlock = $('<pre>').text(pdfaValidator.result)
    $('#results').append(preBlock)
  }
}

function downloadResult () {
  var texts = pdfaValidator.result;
  var hidden_a = document.createElement('a');
  switch(outputFormat) {
    case 'html':  hidden_a.setAttribute('href', 'data:text/html;charset=utf-8,'+ encodeURIComponent(texts));
      break;
    case 'xml': hidden_a.setAttribute('href', 'data:application/xml;charset=utf-8,'+ encodeURIComponent(texts));
      break;
    case 'json': hidden_a.setAttribute('href', 'data:application/json;charset=utf-8,'+ encodeURIComponent(texts));
      break;
    default: hidden_a.setAttribute('href', 'data:text/plain;charset=utf-8,'+ encodeURIComponent(texts));
      break;
  }

  hidden_a.setAttribute('download', "validation_results");
  document.body.appendChild(hidden_a);
  hidden_a.click();
}

$(window).on('load', function(){
  $('#filename').val('');
  $('#sha1Hex').val('');
  flavour = 'auto';
  outputFormat = 'html';
  $("#flavour option:selected").prop("selected", false);
  $("#outputFormat option:selected").prop("selected", false);
});