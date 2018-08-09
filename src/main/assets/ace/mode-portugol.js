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

///////////////////////
// Realce de sintaxe //
///////////////////////

// https://ace.c9.io/#nav=higlighter
// https://github.com/ajaxorg/ace/wiki/Creating-or-Extending-an-Edit-Mode

define("ace/mode/portugol_highlight_rules", function(require, exports, module) {

var oop = require("ace/lib/oop");
var TextHighlightRules = require("ace/mode/text_highlight_rules").TextHighlightRules;

var PortugolHighlightRules = function() {
    this.$rules = {
        start : [
            // Comentários
            {
                token: 'comment.line.double-slash.portugol',
                regex: '//.*$',
                push_: [
                    {
                        token: 'comment.line.double-slash.portugol',
                        regex: '$',
                        next: 'pop'
                    },
                    {
                        defaultToken: 'comment.line.double-slash.portugol'
                    }
                ]
            },
            // Sub-rotinas pré-definidas
            {
                token: 'support.function.portugol',
                regex: new RegExp(portugolSubrotinasPreDefinidas.join('|'), 'i')
            },
            // Palavras reservadas
            {
                caseInsensitive: true,
                token: 'keyword.control.portugol',
                regex: new RegExp(portugolPalavrasReservadas.join('|'), 'i')
            },
            // Tipos
            {
                token: 'storage.type.portugol',
                regex: new RegExp(portugolTipos.join('|'), 'i')
            },
            // Registro
            {
                token: 'support.type.portugol',
                regex: '\\b(?:(registro))\\b'
            },
            // Valores - Numérico
            {
                token: 'constant.numeric.portugol',
                regex: '\\b[0-9]+\\.?[0-9]*'
            },
            // Valores - Lógico
            {
                token: 'constant.language.portugol',
                regex: new RegExp(portugolValoresLogicos.join('|'), 'i')
            },
            // Valores - Literal ou caractere
            {
                token: 'string.quoted.begin.portugol',
                regex: '"',
                push: [
                    {
                        token: 'string.quoted.end.portugol',
                        regex: '"',
                        next: 'pop'
                    },
                    {
                        defaultToken: 'string.quoted.portugol'
                    }
                ]
            },
            // Operadores
            {
                token: 'keyword.operator',
                regex: '\\b(e|nao|ou)\\b|[+\\-*/=<>]'
            }
            // TODO Identificadores (?)
            // TODO Separadores de símbolos (?)
        ]
    };

    this.normalizeRules();
};

oop.inherits(PortugolHighlightRules, TextHighlightRules);

exports.PortugolHighlightRules = PortugolHighlightRules;
});

define("ace/mode/portugol", function(require, exports, module) {

var oop = require("ace/lib/oop");
var TextMode = require("ace/mode/text").Mode;
var PortugolHighlightRules = require("ace/mode/portugol_highlight_rules").PortugolHighlightRules;

/////////////////////////
// Compleção de código //
/////////////////////////

// https://github.com/ajaxorg/ace/wiki/How-to-enable-Autocomplete-in-the-Ace-editor

var PortugolCompletions = function() {
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

//////////////
// Snippets //
//////////////

// https://github.com/form-o-fill/form-o-fill-chrome-extension/issues/79
// https://cloud9-sdk.readme.io/docs/snippets

var snippetManager = require("ace/snippets").snippetManager;
var snippets = snippetManager.parseSnippetFile("");
snippets.push(
    {
        name: 'algoritmo',
        content: 'algoritmo\n${1}\nfim_algoritmo'
    },
    {
        name: 'declare',
        content: 'declare ${1:variavel} ${2:tipo}'
    },
    {
        name: 'escreva',
        content: 'escreva ${1:expressao}'
    },
    {
        name: 'leia',
        content: 'leia ${1:variavel}'
    },
    {
        name: 'se',
        content: 'se ${1:condicao}\nentao${2:comando1}\nsenao${2:comando2}'
    },
    {
        name: 'para',
        content: 'para ${1:indice} <- ${2:valor_inicial} ate ${3:valor_final} faca passo ${4:1}\n\t${5:comando}'
    },
    {
        name: 'enquanto',
        content: 'enquanto ${1:condicao} faca\n\t${2:comando}'
    },
    {
        name: 'repita',
        content: 'repita\n\t${1:comando}\nate ${2:condicao}'
    },
    {
        name: 'sub-rotina',
        content: 'sub-rotina ${1:identificador} (${2:parametro} ${3:tipo})\n\nfim_sub_rotina ${1}'
    },
    {
        name: 'retorne',
        content: 'retorne ${1:expressao}'
    },
    {
        name: 'registro',
        content: 'registro (${1:campo} ${2:tipo})'
    }
);
snippetManager.register(snippets, "portugol");

var Mode = function() {
    this.HighlightRules = PortugolHighlightRules;
    this.$behaviour = this.$defaultBehaviour;
    this.completer = new PortugolCompletions();
};
oop.inherits(Mode, TextMode);

(function() {
    // Extra logic goes here
}).call(Mode.prototype);

exports.Mode = Mode;
});
