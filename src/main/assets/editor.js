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
        console.log("aoMovimentarCursor(linha: " + (posicaoDoCursor.row + 1) + ", coluna: " + (posicaoDoCursor.column + 1) + ")");
    }
}

function aoNaoEncontrar() {
    if (window.javascriptInterface) {
        window.javascriptInterface.aoNaoEncontrar(configuracoesDaPesquisa.localizar);
    } else {
        console.log("aoNaoEncontrar(localizar: " + configuracoesDaPesquisa.localizar + ")");
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
    if (window.javascriptInterface) {
        window.javascriptInterface.compartilharTextoSelecionado(textoSelecionado);
    } else {
        console.log("compartilharTextoSelecionado(texto: " + textoSelecionado + ")");
    }
}

function copiar() {
    var textoSelecionado = editor.getSelectedText();
    if (textoSelecionado) {
        if (window.javascriptInterface) {
            window.javascriptInterface.copiar(textoSelecionado);
        } else {
            console.log("copiar(texto: " + textoSelecionado + ")");
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
    editor.selection.removeListener('changeCursor', aoMovimentarCursor);
    posicionarCursor(linha + 1, coluna + 1);
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

function localizarAnterior() {
    localizar(false);
}

function localizarProximo() {
    localizar(true);
}

function posicionarCursor(linha, coluna) {
    editor.gotoLine(linha - 1, coluna - 1, true);
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
