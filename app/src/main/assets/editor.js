var editor = ace.edit('editor');
editor.setTheme('ace/theme/textmate');
editor.session.setMode('ace/mode/portugol');

function aoEditarCodigo() {
    atualizarDesfazerRefazer();
}

editor.on('input', aoEditarCodigo);

function atualizarDesfazerRefazer() {
    window.javascriptInterface.atualizarDesfazerRefazer(editor.session.getUndoManager().hasUndo(), editor.session.getUndoManager().hasRedo());
}

/* Funções para uso externo */

function desfazer() {
    editor.undo();
    atualizarDesfazerRefazer();
}

function limparHistoricoDesfazerRefazer() {
    editor.session.getUndoManager().reset();
    atualizarDesfazerRefazer();
}

function refazer() {
    editor.redo();
    atualizarDesfazerRefazer();
}

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}
