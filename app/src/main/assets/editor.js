var editor = ace.edit("editor");
editor.setTheme("ace/theme/textmate");
editor.session.setMode("ace/mode/portugol");

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}