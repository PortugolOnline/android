/////////////////////////
// Palavras reservadas //
/////////////////////////

var portugolPalavrasReservadas = [
    // 3.1 Estrutura sequencial em algoritmos
    'algoritmo',
    'declare',
    'fim_algoritmo',
    // 3.1.3 Comando de entrada em algoritmos
    'leia',
    // 3.1.4 Comando de saída em algoritmos
    'escreva',
    // 4.1.1 Estrutura condicional simples
    'se',
    'entao',
    'inicio',
    'fim',
    // 4.1.2 Estrutura condicional composta
    'senao',
    // 5.1.1 Estrutura de repetição para número definido de repetições (estrutura para)
    'para',
    'ate',
    'faca',
    'passo',
    // 5.1.2 Estrutura de repetição para número indefinido de repetições e teste no início (estrutura enquanto)
    'enquanto',
    // 5.1.3 Estrutura de repetição para número indefinido de repetições e teste no final (estrutura repita)
    'repita',
    // 8.1 Sub-rotinas (programação modularizada)
    'sub-rotina',
    'retorne',
    'fim_sub_rotina'
];

var portugolTipos = [
    // 3.1.1 Declaração de variáveis em algoritmos
    'numerico',
    'literal',
    'logico'
];

// 10.2 Declaração de registros em algoritmos
// registro

// Sub-rotinas pré-definidas
var portugolSubrotinasPreDefinidas = [
    'arredonda',
    'cosseno',
    'parte_inteira',
    'potencia',
    'raiz_enesima',
    'raiz_quadrada',
    'resto',
    'seno',
    'limpar_tela',
    'obtenha_ano',
    'obtenha_data',
    'obtenha_dia',
    'obtenha_hora',
    'obtenha_horario',
    'obtenha_mes',
    'obtenha_minuto',
    'obtenha_segundo'
];

/////////////
// Valores //
/////////////

// 1.6.2 Lógico
var portugolValoresLogicos = [
    'verdadeiro',
    'falso'
];

define("ace/mode/portugol_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var portugolHighlightRules = function() {
    this.$rules = { start:

         // Palavras reservadas
       [ { caseInsensitive: true,
           token: 'keyword.control.portugol',
           regex: new RegExp(portugolPalavrasReservadas.join('|'), 'i') },

        /* { caseInsensitive: true,
           token:
            [ 'variable.portugol', 'text',
              'storage.type.prototype.portugol',
              'entity.name.function.prototype.portugol' ],
           regex: '\\b(function|procedure)(\\s+)(\\w+)(\\.\\w+)?(?=(?:\\(.*?\\))?;\\s*(?:attribute|forward|external))' },
         { caseInsensitive: true,
           token:
            [ 'variable.portugol', "text",
              'storage.type.function.portugol',
              'entity.name.function.portugol' ],
           regex: '\\b(function|procedure)(\\s+)(\\w+)(\\.\\w+)?' },*/

         // Tipos
         { token: 'storage.type.portugol',
           regex: new RegExp(portugolTipos.join('|'), 'i') },

         // Registro
         { token: 'support.type.portugol',
           regex: '\\b(?:(registro))\\b' },

         // Sub-rotinas pré-definidas
         { token: 'support.function.portugol',
           regex: new RegExp(portugolSubrotinasPreDefinidas.join('|'), 'i') },

         // Valores - Lógico
         { token: 'constant.language.portugol',
           regex: new RegExp(portugolValoresLogicos.join('|'), 'i') },

         // Valores - Numérico
         { token: 'constant.numeric.portugol',
           regex: '\\b[0-9]+\\.?[0-9]*' },

        /* { token: 'punctuation.definition.comment.portugol',
           regex: '--.*$',
           push_:
            [ { token: 'comment.line.double-dash.portugol.one',
                regex: '$',
                next: 'pop' },
              { defaultToken: 'comment.line.double-dash.portugol.one' } ] },*/

         // Comentários
         { token: 'punctuation.definition.comment.portugol',
           regex: '//.*$',
           push_:
            [ { token: 'comment.line.double-slash.portugol.two',
                regex: '$',
                next: 'pop' },
              { defaultToken: 'comment.line.double-slash.portugol.two' } ] },

        /* { token: 'punctuation.definition.comment.portugol',
           regex: '\\(\\*',
           push:
            [ { token: 'punctuation.definition.comment.portugol',
                regex: '\\*\\)',
                next: 'pop' },
              { defaultToken: 'comment.block.portugol.one' } ] },
         { token: 'punctuation.definition.comment.portugol',
           regex: '\\{',
           push:
            [ { token: 'punctuation.definition.comment.portugol',
                regex: '\\}',
                next: 'pop' },
              { defaultToken: 'comment.block.portugol.two' } ] },*/

         // Valores - Literal ou caractere
         { token: 'punctuation.definition.string.begin.portugol',
           regex: '"',
           push:
            [ /*{ token: 'constant.character.escape.portugol', regex: '\\\\.' },*/
              { token: 'punctuation.definition.string.end.portugol',
                regex: '"',
                next: 'pop' },
              { defaultToken: 'string.quoted.double.portugol' } ]
            },

        /* { token: 'punctuation.definition.string.begin.portugol',
           regex: '\'',
           push:
            [ { token: 'constant.character.escape.apostrophe.portugol',
                regex: '\'\'' },
              { token: 'punctuation.definition.string.end.portugol',
                regex: '\'',
                next: 'pop' },
              { defaultToken: 'string.quoted.single.portugol' } ] },*/

         // Operadores
          { token: 'keyword.operator',
           regex: '\\b(e|nao|ou)\\b|[+\\-*/=<>]' } ] };
         // TODO Separadores de símbolos

    this.normalizeRules();
};

oop.inherits(portugolHighlightRules, TextHighlightRules);

exports.portugolHighlightRules = portugolHighlightRules;
});

define("ace/mode/folding/coffee",["require","exports","module","ace/lib/oop","ace/mode/folding/fold_mode","ace/range"], function(require, exports, module) {
"use strict";

var oop = require("../../lib/oop");
var BaseFoldMode = require("./fold_mode").FoldMode;
var Range = require("../../range").Range;

var FoldMode = exports.FoldMode = function() {};
oop.inherits(FoldMode, BaseFoldMode);

(function() {

    this.getFoldWidgetRange = function(session, foldStyle, row) {
        var range = this.indentationBlock(session, row);
        if (range)
            return range;

        var re = /\S/;
        var line = session.getLine(row);
        var startLevel = line.search(re);
        if (startLevel == -1 || line[startLevel] != "#")
            return;

        var startColumn = line.length;
        var maxRow = session.getLength();
        var startRow = row;
        var endRow = row;

        while (++row < maxRow) {
            line = session.getLine(row);
            var level = line.search(re);

            if (level == -1)
                continue;

            if (line[level] != "#")
                break;

            endRow = row;
        }

        if (endRow > startRow) {
            var endColumn = session.getLine(endRow).length;
            return new Range(startRow, startColumn, endRow, endColumn);
        }
    };
    this.getFoldWidget = function(session, foldStyle, row) {
        var line = session.getLine(row);
        var indent = line.search(/\S/);
        var next = session.getLine(row + 1);
        var prev = session.getLine(row - 1);
        var prevIndent = prev.search(/\S/);
        var nextIndent = next.search(/\S/);

        if (indent == -1) {
            session.foldWidgets[row - 1] = prevIndent!= -1 && prevIndent < nextIndent ? "start" : "";
            return "";
        }
        if (prevIndent == -1) {
            if (indent == nextIndent && line[indent] == "#" && next[indent] == "#") {
                session.foldWidgets[row - 1] = "";
                session.foldWidgets[row + 1] = "";
                return "start";
            }
        } else if (prevIndent == indent && line[indent] == "#" && prev[indent] == "#") {
            if (session.getLine(row - 2).search(/\S/) == -1) {
                session.foldWidgets[row - 1] = "start";
                session.foldWidgets[row + 1] = "";
                return "";
            }
        }

        if (prevIndent!= -1 && prevIndent < indent)
            session.foldWidgets[row - 1] = "start";
        else
            session.foldWidgets[row - 1] = "";

        if (indent < nextIndent)
            return "start";
        else
            return "";
    };

}).call(FoldMode.prototype);

});

define("ace/mode/portugol",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/portugol_highlight_rules","ace/mode/folding/coffee"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var portugolHighlightRules = require("./portugol_highlight_rules").portugolHighlightRules;
var FoldMode = require("./folding/coffee").FoldMode;

var PortugolCompletions = function() {
    // Compleção de código
    var configuracaoDaComplecao = [].concat(
        portugolPalavrasReservadas.map(function(palavra) {
            return {name: palavra, value: palavra, score: 1, meta: "palavra reservada"}
        }),
        portugolTipos.map(function(tipo) {
            return {name: tipo, value: tipo, score: 1, meta: "tipo"}
        }),
        [{name: "registro", value: "registro", score: 1, meta: "tipo"}],
        portugolSubrotinasPreDefinidas.map(function(subrotina) {
            return {name: subrotina, value: subrotina, score: 1, meta: "sub-rotina"}
        }),
        portugolValoresLogicos.map(function(valor) {
            return {name: valor, value: valor, score: 1, meta: "valor logico"}
        })
    );

    this.getCompletions = function(state, session, pos, prefix, callback) {
        if (prefix.length < 2) {
            callback(null, []);
        } else {
            callback(null, configuracaoDaComplecao);
        }
    };
};

var Mode = function() {
    this.HighlightRules = portugolHighlightRules;
    this.foldingRules = new FoldMode();
    this.$behaviour = this.$defaultBehaviour;
    this.completer = new PortugolCompletions();
};
oop.inherits(Mode, TextMode);

(function() {

    this.lineCommentStart = /*["--", */"//"/*]*/;
    /*this.blockComment = [
        {start: "(*", end: "*)"},
        {start: "{", end: "}"}
    ];*/

    this.$id = "ace/mode/portugol";
}).call(Mode.prototype);

exports.Mode = Mode;
});
                (function() {
                    window.require(["ace/mode/portugol"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
