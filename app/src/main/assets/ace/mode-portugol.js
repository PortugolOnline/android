define("ace/mode/portugol_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var portugolHighlightRules = function() {
    this.$rules = { start:

         // Palavras reservadas
       [ { caseInsensitive: true,
           token: 'keyword.control.portugol',
           regex: '\\b(?:(algoritmo|declare|fim_algoritmo|leia|escreva|se|entao|inicio|fim|senao|para|ate|faca|passo|enquanto|repita|sub-rotina|retorne|fim_sub_rotina))\\b' },

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
           regex: '\\b(?:(numerico|literal|logico))\\b' },

         // Registro
         { token: 'support.type.portugol',
           regex: '\\b(?:(registro))\\b' },

         // Sub-rotinas pré-definidas
         { token: 'support.function.portugol',
           regex: '\\b(?:(arredonda|cosseno|parte_inteira|potencia|raiz_enesima|raiz_quadrada|resto|seno|limpar_tela|obtenha_ano|obtenha_data|obtenha_dia|obtenha_hora|obtenha_horario|obtenha_mes|obtenha_minuto|obtenha_segundo))\\b' },

         // Valores - Lógico
         { token: 'constant.language.portugol',
           regex: '\\b(?:(verdadeiro|falso))\\b' },

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

var Mode = function() {
    this.HighlightRules = portugolHighlightRules;
    this.foldingRules = new FoldMode();
    this.$behaviour = this.$defaultBehaviour;
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
