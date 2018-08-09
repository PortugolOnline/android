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
    console.log("aoModificarCodigoFonte() - desfazerPossivel: " + desfazerPossivel + ", refazerPossivel: " + refazerPossivel);
    if (window.javascriptInterface) {
        window.javascriptInterface.aoModificarCodigoFonte(codigoFonte, desfazerPossivel, refazerPossivel);
    }
}

function aoMovimentarCursor() {
    // https://stackoverflow.com/a/38182500/1657502
    var posicaoDoCursor = editor.getCursorPosition();
    console.log("aoMovimentarCursor() - linha: " + (posicaoDoCursor.row + 1) + ", coluna: " + (posicaoDoCursor.column + 1));
    if (window.javascriptInterface) {
        window.javascriptInterface.aoMovimentarCursor(posicaoDoCursor.row + 1, posicaoDoCursor.column + 1);
    }
}

function aoNaoEncontrar() {
    console.log("aoNaoEncontrar() - localizar: " + configuracoesDaPesquisa.localizar);
    if (window.javascriptInterface) {
        window.javascriptInterface.aoNaoEncontrar(configuracoesDaPesquisa.localizar);
    }
}

editor.on('input', aoModificarCodigoFonte);

editor.session.selection.on('changeCursor', aoMovimentarCursor);

var intervaloDestacarLinhaComErro;

function localizar(proximo) {
    var range = editor.find(configuracoesDaPesquisa.localizar, {
        backwards: !proximo,
        caseSensitive: configuracoesDaPesquisa.diferenciarMaiusculas,
        wrap: true
    });
    if (!range) {
        aoNaoEncontrar();
    }
}

function removerDestaqueLinhaComErro() {
    console.log("removerDestaqueLinhaComErro()");
    document.getElementsByClassName('ace_active-line')[0].classList.remove('linha_com_erro');
    editor.selection.removeListener('changeCursor', removerDestaqueLinhaComErro);
    editor.selection.on('changeCursor', aoMovimentarCursor);
    aoMovimentarCursor();
}

var configuracoesDaPesquisa = {};

/* Funções para uso externo */

function aumentarFonte() {
    tamanhoDaFonte += TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
}

function colar(texto) {
    editor.insert(texto);
}

function compartilharTextoSelecionado() {
    var textoSelecionado = editor.getSelectedText();
    console.log("compartilharTextoSelecionado() - texto: " + textoSelecionado);
    if (window.javascriptInterface) {
        window.javascriptInterface.compartilharTextoSelecionado(textoSelecionado);
    }
}

function copiar() {
    var textoSelecionado = editor.getSelectedText();
    if (textoSelecionado) {
        console.log("copiar() - texto: " + textoSelecionado);
        if (window.javascriptInterface) {
            window.javascriptInterface.copiar(textoSelecionado);
        }
        return textoSelecionado;
    }
}

function configurarPesquisa(localizar, diferenciarMaiusculas, substituirPor) {
    configuracoesDaPesquisa.localizar = localizar;
    configuracoesDaPesquisa.diferenciarMaiusculas = diferenciarMaiusculas;
    configuracoesDaPesquisa.substituirPor = substituirPor;
}

function desfazer() {
    editor.undo();
}

function destacarLinhaComErro(linha, coluna) {
    console.log("destacarLinhaComErro() - linha: " + linha + ", coluna: " + coluna);
    editor.selection.removeListener('changeCursor', aoMovimentarCursor);
    posicionarCursor(linha, coluna);
    intervaloDestacarLinhaComErro = setInterval(function() {
        console.log("intervaloDestacarLinhaComErro");
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

function localizarAnterior() {
    localizar(false);
}

function localizarProximo() {
    localizar(true);
}

function posicionarCursor(linha, coluna) {
    editor.gotoLine(linha, coluna - 1, true);
}

function recortar() {
    var textoSelecionado = copiar();
    if (textoSelecionado) {
        colar("");
        return textoSelecionado;
    }
}

function refazer() {
    editor.redo();
}

function selecionarTudo() {
    editor.selectAll();
}

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}

function substituir() {
    if (editor.replace(configuracoesDaPesquisa.substituirPor) < 1) {
        aoNaoEncontrar();
    };
    localizarProximo();
}

function substituirTudo() {
    if (editor.replaceAll(configuracoesDaPesquisa.substituirPor) < 1) {
        aoNaoEncontrar();
    }
}
