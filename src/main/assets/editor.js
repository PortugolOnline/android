const TAMANHO_DA_FONTE_AJUSTE = 2;
const TAMANHO_DA_FONTE_INICIAL = 12;

var tamanhoDaFonte = TAMANHO_DA_FONTE_INICIAL;

var langTools = ace.require("ace/ext/language_tools");
var editor = ace.edit('editor');
editor.setTheme('ace/theme/textmate');
editor.getSession().setMode('ace/mode/portugol');
editor.setOptions({
    enableBasicAutocompletion: true,
    //enableSnippets: true,
    enableLiveAutocompletion: true
});

editor.setFontSize(tamanhoDaFonte);

function aoModificarCodigoFonte() {
    var codigoFonte = editor.getValue();
    var desfazerPossivel = editor.session.getUndoManager().hasUndo();
    var refazerPossivel = editor.session.getUndoManager().hasRedo();
    window.javascriptInterface.aoModificarCodigoFonte(codigoFonte, desfazerPossivel, refazerPossivel);
}

editor.on('input', aoModificarCodigoFonte);

function removerDestaqueLinhaComErro() {
    document.getElementsByClassName('ace_active-line')[0].classList.remove('linha_com_erro');
    editor.selection.removeListener('changeCursor', removerDestaqueLinhaComErro);
}

/* Funções para uso externo */

function aumentarFonte() {
    tamanhoDaFonte += TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
}

function desfazer() {
    editor.undo();
}

var intervaloDestacarLinhaComErro;

function destacarLinhaComErro(linha, coluna) {
    posicionarCursor(linha, coluna);
    intervaloDestacarLinhaComErro = setInterval(function() {
        document.getElementsByClassName('ace_active-line')[0].classList.add('linha_com_erro');
        editor.selection.on('changeCursor', removerDestaqueLinhaComErro);
        clearInterval(intervaloDestacarLinhaComErro);
    }, 50);
}

function diminuirFonte() {
    tamanhoDaFonte -= TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
}

function limparHistoricoDesfazerRefazer() {
    editor.session.getUndoManager().reset();
}

function posicionarCursor(linha, coluna) {
    editor.gotoLine(linha, coluna - 1, true);
}

function refazer() {
    editor.redo();
}

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}
