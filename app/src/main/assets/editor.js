const TAMANHO_DA_FONTE_AJUSTE = 2;
const TAMANHO_DA_FONTE_INICIAL = 12;

var tamanhoDaFonte = TAMANHO_DA_FONTE_INICIAL;

var editor = ace.edit('editor');
editor.setFontSize(tamanhoDaFonte);
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

function aumentarFonte() {
    tamanhoDaFonte += TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
}

function desfazer() {
    editor.undo();
    atualizarDesfazerRefazer();
}

function diminuirFonte() {
    tamanhoDaFonte -= TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
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
