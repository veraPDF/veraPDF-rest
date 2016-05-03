/**
* @namespace JS Encapsulation of information required to render PDF/A validtion results.
*/
var pdfaValidator = {
  // validation result
  result : null,
  validate : function (formData, flavour, callback) {
    $.ajax({
      url:   '/api/validate/' + flavour + '/',
      type:  'POST',
      data:  formData,
      dataType: 'json',
      contentType: false,
      processData: false,
      success : function (data, textStatus, jqXHR) {
        pdfaValidator.result = JSON.parse(jqXHR.responseText);
        callback();
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
  },
  /**
  * @function jsonResult
  *
  * Returns the validation result as JSON
  */
  jsonResult : function () {
    return JSON.stringify(pdfaValidator.result, null, "  ");
  },
  /**
  * @function xmlResult
  *
  * Returns the validation result as XML
  */
  xmlResult : function () {
    // Empty the current results
    return pdfaValidator.obj2xml(pdfaValidator.result, "\t");
  },
  obj2xml : function (o, tab) {
     var toXml = function(v, name, ind) {
        var xml = "";
        if (v instanceof Array) {
           for (var i=0, n=v.length; i<n; i++)
              xml += ind + toXml(v[i], name, ind+"\t") + "\n";
        }
        else if (typeof(v) == "object") {
           var hasChild = false;
           xml += ind + "<" + name;
           for (var m in v) {
              if (m.charAt(0) == "@")
                 xml += " " + m.substr(1) + "=\"" + v[m].toString() + "\"";
              else
                 hasChild = true;
           }
           xml += hasChild ? ">\n" : "/>\n";
           if (hasChild) {
              for (var m in v) {
                 if (m == "#text")
                    xml += v[m];
                 else if (m == "#cdata")
                    xml += "<![CDATA[" + v[m] + "]]>";
                 else if (m.charAt(0) != "@")
                    xml += toXml(v[m], m, ind+"\t");
              }
              xml += (xml.charAt(xml.length-1)=="\n"?ind:"") + "</" + name + ">\n";
           }
        }
        else {
           xml += ind + "<" + name + ">" + v.toString() +  "</" + name + ">\n";
        }
        return xml;
     }, xml="";
     for (var m in o)
        xml += toXml(o[m], m, "");
     return tab ? xml.replace(/\t/g, tab) : xml.replace(/\t|\n/g, "");
  },
  /**
  * @function formatDate
  *
  * Just a date formatter, outputs an ISO style short date String.
  *
  * TODO really belongs in a more general JS file / module
  *
  * @param date
  *            the JavaScript date object to format
  */
  formatDate : function (date) {
    return date.getFullYear() + "-" + pdfaValidator.pad(date.getMonth() + 1, 2) +
    "-" + pdfaValidator.pad(date.getDate(), 2);
  },
  /**
  * @function pad
  *
  * Pads a string with leading characters to a requested width, see
  * http://stackoverflow.com/questions/10073699/pad-a-number-with-leading-zeros-in-javascript
  *
  * TODO really belongs in a more general JS file / module
  *
  * @param str
  *            the string to pad
  * @param width
  *            the minimum string width to pad to
  * @param chr
  *            the character to pad with, defaults to zero
  */
  pad : function (str, width, chr) {
    chr = chr || '0';
    str = str + '';
    if (str.length >= width) {
      return str;
    }
    var retVal = [];
    retVal.length = width - str.length + 1;
    return retVal.join(chr) + str;
  }
};
