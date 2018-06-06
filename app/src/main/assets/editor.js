const TAMANHO_DA_FONTE_AJUSTE = 2;
const TAMANHO_DA_FONTE_INICIAL = 12;

var tamanhoDaFonte = TAMANHO_DA_FONTE_INICIAL;

var editor = ace.edit('editor');
editor.setFontSize(tamanhoDaFonte);
editor.setTheme('ace/theme/textmate');
editor.session.setMode('ace/mode/portugol');

function aoEditarCodigo() {
    atualizarCodigoFonte();
    atualizarDesfazerRefazer();
}

editor.on('input', aoEditarCodigo);

function atualizarCodigoFonte() {
    window.javascriptInterface.atualizarCodigoFonte(editor.getValue());
}

function atualizarDesfazerRefazer() {
    window.javascriptInterface.atualizarDesfazerRefazer(editor.session.getUndoManager().hasUndo(), editor.session.getUndoManager().hasRedo());
}

function removerDestaqueLinhaComErro() {
    document.getElementsByClassName('ace_active-line')[0].classList.remove('linha_com_erro');
    editor.selection.removeListener('changeCursor', removerDestaqueLinhaComErro);
}

// https://stackoverflow.com/a/39914235/1657502
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
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

async function destacarLinhaComErro(linha, coluna) {
    posicionarCursor(linha, coluna);
    await sleep(10);
    document.getElementsByClassName('ace_active-line')[0].classList.add('linha_com_erro');
    editor.selection.on('changeCursor', removerDestaqueLinhaComErro);
}

function diminuirFonte() {
    tamanhoDaFonte -= TAMANHO_DA_FONTE_AJUSTE;
    editor.setFontSize(tamanhoDaFonte);
}

function limparHistoricoDesfazerRefazer() {
    editor.session.getUndoManager().reset();
    atualizarDesfazerRefazer();
}

function posicionarCursor(linha, coluna) {
    editor.gotoLine(linha, coluna - 1, true);
}

function refazer() {
    editor.redo();
    atualizarDesfazerRefazer();
}

function setCodigoFonte(codigoFonte) {
    editor.setValue(codigoFonte, -1);
}
