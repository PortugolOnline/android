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
    if (window.javascriptInterface) {
        window.javascriptInterface.aoModificarCodigoFonte(codigoFonte, desfazerPossivel, refazerPossivel);
    } else {
        // Depuração usando o navegador
        console.log("aoModificarCodigoFonte(codigoFonte, desfazerPossivel: " + desfazerPossivel + ", refazerPossivel: " + refazerPossivel + ")");
    }
}

function aoMovimentarCursor() {
    // https://stackoverflow.com/a/38182500/1657502
    var posicaoDoCursor = editor.getCursorPosition();
    if (window.javascriptInterface) {
        window.javascriptInterface.aoMovimentarCursor(posicaoDoCursor.row + 1, posicaoDoCursor.column + 1);
    } else {
        // Depuração usando o navegador
        console.log("aoMovimentarCursor(" + (posicaoDoCursor.row + 1) + ", " + (posicaoDoCursor.column + 1) + ")");
    }
}

editor.on('input', aoModificarCodigoFonte);

editor.session.selection.on('changeCursor', aoMovimentarCursor);

var intervaloDestacarLinhaComErro;

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
    editor.gotoLine(linha - 1, coluna - 1, true);
}

function refazer() {
    editor.redo();
}

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}
