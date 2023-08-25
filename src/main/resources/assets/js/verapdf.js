$(document).on('change', '.btn-file :file', function () {
  let input = $(this)
  let numFiles = input.get(0).files ? input.get(0).files.length : 1
  let label = input.val().replace(/\\/g, '/').replace(/.*\//, '')
  let rusha = new Rusha()
  let file = input.get(0).files[0]
  let reader = new FileReader()
  reader.onload = function (e) {
    let rawData = reader.result
    let digest = rusha.digest(rawData)
    input.trigger('fileselect', [numFiles, label, digest])
  }
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
    } else {
      if (log) alert(log)
    }
    $('#sha1Hex').val(digest)
  })
})

$(document).ready(function () {

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
      nextStepWizard.removeAttr('disabled').trigger('click')
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

    if (isValid) nextStepWizard.removeAttr('disabled').trigger('click')
  })

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
    renderResult()
  }, outputFormat)
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

$(window).on('load', function(){
  $('#filename').val('');
  $('#sha1Hex').val('');
  flavour = 'auto';
  outputFormat = 'html';
  $("#flavour option:selected").prop("selected", false);
  $("#outputFormat option:selected").prop("selected", false);
});