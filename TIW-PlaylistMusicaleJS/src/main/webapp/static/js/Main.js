const urlParams = new URLSearchParams(window.location.search);
var contextPath = document.querySelector('#contextPath').textContent;
console.log(contextPath);


function renderErrorMessage(errorMessage) {
  const parser = new DOMParser();
  const doc = parser.parseFromString(errorMessage, 'text/html');

  const message = findTextContent(doc, 'Message');
  const description = findTextContent(doc, 'Description');

  window.alert(message.trim());
}

function findTextContent(doc, label) {
  const labels = doc.querySelectorAll('p b');
  for (let i = 0; i < labels.length; i++) {
    if (labels[i].textContent.trim() === label) {
      return labels[i].nextSibling.textContent.trim();
    }
  }
  return '';
}