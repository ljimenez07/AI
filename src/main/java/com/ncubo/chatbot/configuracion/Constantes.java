package com.ncubo.chatbot.configuracion;

public class Constantes {
	
	public enum ModoDeLaVariable{ REAL, PRUEBA }
	public enum TiposDeVariables{ CONTEXTO, USUARIO, NEGOCIO, ENUM }
	
	// Watson IBM f63e42df-1405-4ea7-8bff-7556a2586828
	public static String WATSON_CONVERSATION_ID = "8af4d07d-d2b5-4a32-804c-6a0e2749ffed";
	public static String WATSON_CONVERSATION_USER = "bb9bd2e7-d63f-490c-b5d6-32f026a4c646";
	public static String WATSON_CONVERSATION_PASS = "Tvm3ZJzX2vab";
	public static double WATSON_CONVERSATION_CONFIDENCE = 0.55;
	
	public static String WATSON_USER_TEXT_SPEECH = "8f1ec844-f8ad-4303-9293-3da7192c5b59";
	public static String WATSON_PASS_TEXT_SPEECH = "LHVIAi4Kfweb";
	//public static String WATSON_VOICE_TEXT_SPEECH = "es-US_SofiaVoice";
	public static String WATSON_VOICE_TEXT_SPEECH = "en-US_MichaelVoice";
	
	public static String WATSON_USER_SPEECH_TEXT = "ccb556df-6132-4b9d-a05c-8d83f2527e26";
	public static String WATSON_PASS_SPEECH_TEXT = "BC4TrqdBckdp";
	public static String WATSON_MODEL_SPEECH_TEXT = "es-ES_NarrowbandModel";
	
	// Server
	public static String TOMCAT_ROOT = "/LogicaDeChateadores";
	public static String IP_SERVER = "http://138.94.58.158:7870/LogicaDeChateadores/";
	//public static String IP_SERVER = "http://10.102.101.252:8080/LogicaDeChateadores/";
	
	public static String PATH_TO_SAVE = "/opt/tomcat/webapps/LogicaDeChateadores/";
	public static String FOLDER_TO_SAVE = "audios/";
	
	// "\"queBusca\": \"true\"\n" +
	public static String CONTEXT = "{\n" +
            "\"conversation_id\": \"8f10552e-b11f-451d-9b5f-68c6648ee81a\",\n" +
            "\"system\": {\n" +
            "  \"dialog_stack\": [\n" +
            "\t{\n" +
            "\t  \"dialog_node\": \"root\"\n" +
            "\t}\n" +
            "  ],\n" +
            "  \"dialog_turn_counter\": 1.0,\n" +
            "  \"dialog_request_counter\": 1.0\n" +
            "},\n" +
            "\"finished\": \"false\",\n" +
            "\"tema\": \"10\"\n" +
            "}";
	
	public static String PATH_ARCHIVO_DE_CONFIGURACION_RS = "src/main/resources/conversaciones.xml";
	public static String PATH_ARCHIVO_DE_CONFIGURACION_BA = "src/main/resources/conversacionesBA.xml";
	public static String PATH_ARCHIVO_DE_CONFIGURACION = "src/main/resources/conversaciones.xml";
	
	// Agente
	public static String WORKSPACE_GENERAL = "general";
	public static String ANYTHING_ELSE = "anyThingElse";
	public static String CAMBIAR_INTENCION = "cambiarIntencion";
	public static String NODO_ACTIVADO = "nodo";
	public static String TERMINO_EL_TEMA = "terminoTema";
	public static String ORACIONES_AFIRMATIVAS = "oracionesAfirmativas";
	public static String CAMBIAR_A_GENERAL = "cambiarAGeneral";
	public static String ID_TEMA = "idTema";
	public static int MAXIMO_DE_INTENTOS_OPCIONALES = 4; // Sino se aborda el tema
	
	// Tipos de intenciones
	
	public static String TIPO_INTENCION_FUERA_DE_CONTEXTO = "fueraDeContexto";
	public static String TIPO_INTENCION_NO_ENTIENDO = "noEntendi";
	public static String TIPO_INTENCION_DESPEDIDA = "despedida";
	public static String TIPO_INTENCION_SALUDAR = "saludo";
	public static String TIPO_INTENCION_DESPISTADOR = "despistador";
	public static String TIPO_INTENCION_REPETIR_ULTIMA_FRASE = "repetirUltima";
	public static String TIPO_INTENCION_ERROR_CON_WATSON = "errorConWatson";
	public static String TIPO_INTENCION_AGRADECIMIENTO = "agradecimiento";
	public static String TIPO_INTENCION_QUE_PUEDEN_PREGUNTAR = "quePuedenPreguntar";
	public static String TIPO_INTENCION_PREGUNTAR_POR_OTRA_CONSULTA = "preguntarPorOtraConsulta";
	public static String TIPO_INTENCION_RECORDAR_TEMA = "recordatorTemasPendientes";
	
	// Intenciones
	public static String INTENCION_FUERA_DE_CONTEXTO = "";
	public static String INTENCION_NO_ENTIENDO = "";
	public static String INTENCION_DESPEDIDA = "";
	public static String INTENCION_SALUDAR = "";
	public static String INTENCION_DESPISTADOR = "";
	public static String INTENCION_REPETIR_ULTIMA_FRASE = "";
	public static String INTENCION_ERROR_CON_WATSON = "";
	public static String INTENCION_AGRADECIMIENTO = "";
	public static String INTENCION_QUE_PUEDEN_PREGUNTAR = "";
	public static String INTENCION_PREGUNTAR_POR_OTRA_CONSULTA = "";
	
	public static String[] FRASES_INTENCION_SALUDAR = new String[]{};
	public static String[] FRASES_INTENCION_DESPEDIDA = new String[]{};
	public static String[] FRASES_INTENCION_FUERA_DE_CONTEXTO  = new String[]{};
	public static String[] FRASES_INTENCION_DESPISTADOR = new String[]{};
	public static String[] FRASES_INTENCION_REPETIR = new String[]{};
	public static String[] FRASES_INTENCION_NO_ENTIENDO = new String[]{};
	public static String[] FRASES_INTENCION_ERROR_CON_WATSON = new String[]{};
	public static String[] FRASES_INTENCION_AGRADECIMIENTO = new String[]{};
	public static String[] FRASES_INTENCION_QUE_PUEDEN_PREGUNTAR = new String[]{};
	public static String[] FRASES_INTENCION_PREGUNTAR_POR_OTRA_CONSULTA = new String[]{};
	public static String[] FRASES_INTENCION_RECORDAR_TEMAS = new String[]{};
	
	// Frase
	public static String CONDICION_POR_DEFECTO = "enCualquierMomento";
	public static String TIPO_FRASE_GERERAL = "frase";
	public static String TIPO_FRASE_IMPERTINENTE = "impertinente";
	public static String TIPO_FRASE_ME_RINDO = "meRindo";
	public static String TIPO_FRASE_CONJUNCION = "conjuncion";
	
	// Variables
	public static String VARIABLE_TIPO_CONTEXTO = "Contexto";
	public static String VARIABLE_TIPO_USUARIO = "Usuario";
	public static String VARIABLE_TIPO_NEGOCIO = "Negocio";
	public static String VARIABLE_TIPO_ENUM = "Enum";
	
	// Vinetas
	public static String TIPO_VINETA_ILUSTRATIVA = "Ilustrativa";
	public static String TIPO_VINETA_SELECTIVA = "Selectiva";
	
	// Reflexivas
	public static String PATH_VARIABLES = "com.bibliotecas";
	public static String NOMBRE_VARIABLE = "nombreDeLaVariable";
	public static String TIPO_VARIABLE = "tipoDeLaVariable";
	public static String VARIABLE = "Variable";
	public static String CLASE_PARAMETROS = "ParametrosDeLasVariables";
	public static String INSTANCEA_PARAMETROS = "parametros";
	
	public static String ENTIDAD_SYS = "sys-";
	
	
	//Base de datos Regresion
	public static String DB_HOST = "172.16.60.2";
	public static String DB_NAME = "conversaciones";
	public static String DB_USER = "root";
	public static String DB_PASSWORD = "123456";
	
	// Email
	public static String EMAIL_USER = "server.arts@gmail.com";
	public static String EMAIL_PASS = "sruxxdbpfzwhkxrn";
	public static String EMAIL_PROJECT_NAME = "LogicaDeConversaciones";
	public static String EMAIL_IMAGE_LOGO = "http://devs.ncubo.com:7870/logo.png";
	public static String EMAIL_IMAGE_FOOTER = "http://devs.ncubo.com:7870/footerimg.png";
	public static String EMAIL_COLOR_BASE = "#48a1b4";
	public static String EMAIL_BASE_CONVERSACION = "<li class=\"#QUIEN#\">\n" +
            "\t<div class=\"msg\">\n" +
            "\t\t<p>#TEXT#</p>\n" +
            "\t\t<time>#HORA#</time>\n" +
            "\t</div>\n" +
            "</li>";
	public static String EMAIL_BASE = "<!DOCTYPE html>\n" +
            "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
            "<head>\n" +
            "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "\t<title>Mi cantón</title>\n" +
            "\t<style>\n" +
            "\n" +
            "\thtml, body, div, span, applet, object, iframe,\n" +
            "\th1, h2, h3, h4, h5, h6, p, blockquote, pre,\n" +
            "\ta, abbr, acronym, address, big, cite, code,\n" +
            "\tdel, dfn, em, img, ins, kbd, q, s, samp,\n" +
            "\tsmall, strike, strong, sub, sup, tt, var,\n" +
            "\tb, u, i, center,\n" +
            "\tdl, dt, dd, ol, ul, li,\n" +
            "\tfieldset, form, label, legend,\n" +
            "\tcaption, tbody, tfoot, thead, tr, th, td,\n" +
            "\tarticle, aside, canvas, details, embed,\n" +
            "\tfigure, figcaption, footer, header, hgroup,\n" +
            "\tmenu, nav, output, ruby, section, summary,\n" +
            "\ttime, mark, audio, video {\n" +
            "\t\tmargin: 0;\n" +
            "\t\tpadding: 0;\n" +
            "\t\tborder: 0;\n" +
            "\t\tfont-size: 100%;\n" +
            "\t\tfont: inherit;\n" +
            "\t\tvertical-align: baseline;\n" +
            "\t\tfont-family: 'NeoSan';\n" +
            "\t\tletter-spacing: 1px;\n" +
            "\t\tline-height: 1.2;\n" +
            "\t}\n" +
            "\t\n" +
            "\tbody {\n" +
            "\t\tline-height: 1;\n" +
            "\t}\n" +
            "\tol, ul {\n" +
            "\t\tlist-style: none;\n" +
            "\t}\n" +
            "\t\n" +
            "\tstrong {\n" +
            "\t\tfont-weight: bold;\n" +
            "\t}\n" +
            "\n" +
            "\t/* Agente Cognitivo */\n" +
            "\n" +
            "\t.hide\n" +
            "\t{\n" +
            "\t\tdisplay:none;\n" +
            "\t}\n" +
            "\t.show\n" +
            "\t{\n" +
            "\t\tdisplay:block;\n" +
            "\t}\n" +
            "\t.btn \n" +
            "\t{\n" +
            "\t\tbackground-color: #E92531;\n" +
            "\t\tcolor: white;\n" +
            "\t}\n" +
            "\t.btn:hover\n" +
            "\t{\n" +
            "\t\tbackground-color: #E92531;\n" +
            "\t\tcolor: white;\n" +
            "\t}\n" +
            "\t.btn:active\n" +
            "\t{\n" +
            "\t\tbackground-color: #E92531;\n" +
            "\t\tcolor: white;\n" +
            "\t}\n" +
            "\t.panel-heading {\n" +
            "\t\tpadding-top: 0;\n" +
            "\t\tz-index: 1;\n" +
            "\t\twidth: 100%;\n" +
            "\t}\n" +
            "\t.panel.panel-default\n" +
            "\t{\n" +
            "\t\tbox-shadow: none;\n" +
            "\t}\n" +
            "\t.panel-default {\n" +
            "\t\tborder: none;\n" +
            "\t\tmargin-bottom: 0;\n" +
            "\t}\n" +
            "\t.row.title {\n" +
            "\t\tmargin-top: 10px;\n" +
            "\t}\n" +
            "\t.panel-heading\n" +
            "\t{\n" +
            "\t\tposition:fixed;\n" +
            "\t\tfont-family: \"Arial Bold\";\n" +
            "\t\tfont-size: 14pt;\n" +
            "\t}\n" +
            "\t.title\n" +
            "\t{\n" +
            "\t\ttext-align: center;\n" +
            "\t\tfont-family: \"Arial Bold\";\n" +
            "\t\tfont-size: 16pt;\n" +
            "\t}\n" +
            "\t.clickable:hover\n" +
            "\t{\n" +
            "\t\tcursor:pointer;\n" +
            "\t}\n" +
            "\t.panel-heading .bck-red {\n" +
            "\t\theight: 34px;\n" +
            "\t}\n" +
            "\t.bck-red\n" +
            "\t{\n" +
            "\t\tbackground-color: #COLOR#;\n" +
            "\t}\n" +
            "\t.bck-gray\n" +
            "\t{\n" +
            "\t\tbackground-color: gray;\n" +
            "\t}\n" +
            "\t.color-white\n" +
            "\t{\n" +
            "\t\tcolor: white;\n" +
            "\t}\n" +
            "\t.contenedor-centrado\n" +
            "\t{\n" +
            "\t\tdisplay: flex;\n" +
            "\t\talign-items: center;\n" +
            "\t\theight: 100%;\n" +
            "\t}\n" +
            "\t.contenedor-centrado div\n" +
            "\t{\n" +
            "\t\tmargin: 0 auto;\n" +
            "\t}\n" +
            "\t.logo-header {\n" +
            "\t\tmax-width: 400px;\n" +
            "\t\twidth: 100%;\n" +
            "\t\t\n" +
            "\t}\n" +
            "\t.logo-footer {\n" +
            "\t\tmax-width: 100px;\n" +
            "\t\twidth: 100%;\n" +
            "\t}\n" +
            "\t.row.title {\n" +
            "\t\tfont-size: 13pt;\n" +
            "\t\tfont-weight: bold;\n" +
            "\t\tcolor: #88898C;\n" +
            "\t}\n" +
            "\n" +
            "\t/*Chat*/\n" +
            "\n" +
            "\t.header-menu-icon {\n" +
            "\t\twidth: 38px;\n" +
            "\t\theight: 28px;\n" +
            "\t\tbackground: url(../img/iconos-40x28.png) 0 0;\n" +
            "\t\tdisplay: block;\n" +
            "\t}\n" +
            "\n" +
            "\t.chat {\n" +
            "\t\tlist-style: none;\n" +
            "\t\tbackground: none;\n" +
            "\t\tmargin: 0;\n" +
            "\t}\n" +
            "\t.chat li {\n" +
            "\t\tpadding: 0.5rem;\n" +
            "\t\toverflow: hidden;\n" +
            "\t\tdisplay: flex;\n" +
            "\t\tpadding-bottom: 10px;\n" +
            "\t}\n" +
            "\t.chat .avatar {\n" +
            "\t\twidth: 40px;\n" +
            "\t\theight: 40px;\n" +
            "\t\tposition: relative;\n" +
            "\t\tdisplay: block;\n" +
            "\t\tz-index: 2;\n" +
            "\t\tborder-radius: 100%;\n" +
            "\t\t-webkit-border-radius: 100%;\n" +
            "\t\t-moz-border-radius: 100%;\n" +
            "\t\t-ms-border-radius: 100%;\n" +
            "\t\tbackground-color: rgba(255,255,255,0.9);\n" +
            "\t}\n" +
            "\t.chat .avatar img {\n" +
            "\t\twidth: 40px;\n" +
            "\t\theight: 40px;\n" +
            "\t\tborder-radius: 100%;\n" +
            "\t\t-webkit-border-radius: 100%;\n" +
            "\t\t-moz-border-radius: 100%;\n" +
            "\t\t-ms-border-radius: 100%;\n" +
            "\t\tbackground-color: rgba(255,255,255,0.9);\n" +
            "\t\t-webkit-touch-callout: none;\n" +
            "\t\t-webkit-user-select: none;\n" +
            "\t\t-moz-user-select: none;\n" +
            "\t\t-ms-user-select: none;\n" +
            "\t}\n" +
            "\t.chat .day {\n" +
            "\t\tposition: relative;\n" +
            "\t\tdisplay: block;\n" +
            "\t\ttext-align: center;\n" +
            "\t\tcolor: #c0c0c0;\n" +
            "\t\theight: 20px;\n" +
            "\t\ttext-shadow: 7px 0px 0px #e5e5e5, 6px 0px 0px #e5e5e5, 5px 0px 0px #e5e5e5, 4px 0px 0px #e5e5e5, 3px 0px 0px #e5e5e5, 2px 0px 0px #e5e5e5, 1px 0px 0px #e5e5e5, 1px 0px 0px #e5e5e5, 0px 0px 0px #e5e5e5, -1px 0px 0px #e5e5e5, -2px 0px 0px #e5e5e5, -3px 0px 0px #e5e5e5, -4px 0px 0px #e5e5e5, -5px 0px 0px #e5e5e5, -6px 0px 0px #e5e5e5, -7px 0px 0px #e5e5e5;\n" +
            "\t\tbox-shadow: inset 20px 0px 0px #e5e5e5, inset -20px 0px 0px #e5e5e5, inset 0px -2px 0px #d7d7d7;\n" +
            "\t\tline-height: 38px;\n" +
            "\t\tmargin-top: 5px;\n" +
            "\t\tmargin-bottom: 20px;\n" +
            "\t\tcursor: default;\n" +
            "\t\t-webkit-touch-callout: none;\n" +
            "\t\t-webkit-user-select: none;\n" +
            "\t\t-moz-user-select: none;\n" +
            "\t\t-ms-user-select: none;\n" +
            "\t}\n" +
            "\t.other .msg {\n" +
            "\t\torder: 1;\n" +
            "\t\tborder-top-left-radius: 0px;\n" +
            "\t\tbox-shadow: -1px 2px 0px #D4D4D4;\n" +
            "\t\tbackground-color: #f5f5f5;\n" +
            "\t\tcolor: black;\n" +
            "\t\tfont-family: \"Arial regular\";\n" +
            "\t\tfont-size: 14pt;\n" +
            "\t}\n" +
            "\t.other .after {\n" +
            "\t\torder: 0;\n" +
            "\t\tposition: relative;\n" +
            "\t\ttop: 26px;\n" +
            "\t\tright: 0px;\n" +
            "\t\tleft: 20px;\n" +
            "\t\twidth: 0px;\n" +
            "\t\theight: 0px;\n" +
            "\t\tborder: 5px solid #D4D4D5;\n" +
            "\t\tborder-right-color: transparent;\n" +
            "\t\tborder-bottom-color: transparent;\n" +
            "\t}\n" +
            "\t.self .after {\n" +
            "\t\torder: 2;\n" +
            "\t\tposition: relative;\n" +
            "\t\ttop: 8px;\n" +
            "\t\tright: 20px;\n" +
            "\t\twidth: 0px;\n" +
            "\t\theight: 0px;\n" +
            "\t\tborder: 5px solid #COLOR#;\n" +
            "\t\tborder-left-color: transparent;\n" +
            "\t\tborder-bottom-color: transparent;\n" +
            "\t}\n" +
            "\t.self {\n" +
            "\t\tjustify-content: flex-end;\n" +
            "\t\talign-items: flex-end;\n" +
            "\t}\n" +
            "\t.self .msg {\n" +
            "\t\torder: 1;\n" +
            "\t\tborder-bottom-right-radius: 0px;\n" +
            "\t\tbox-shadow: 1px 2px 0px #D4D4D4;\n" +
            "\t\tbackground-color: #COLOR#;\n" +
            "\t\tcolor: white;\n" +
            "\t}\n" +
            "\t.self .avatar {\n" +
            "\t\torder: 2;\n" +
            "\t}\n" +
            "\t.self .avatar:after {\n" +
            "\t\tcontent: \"\";\n" +
            "\t\tposition: relative;\n" +
            "\t\tdisplay: inline-block;\n" +
            "\t\tbottom: 19px;\n" +
            "\t\tright: 0px;\n" +
            "\t\twidth: 0px;\n" +
            "\t\theight: 0px;\n" +
            "\t\tborder: 5px solid #fff;\n" +
            "\t\tborder-right-color: transparent;\n" +
            "\t\tborder-top-color: transparent;\n" +
            "\t\tbox-shadow: 0px 2px 0px #D4D4D4;\n" +
            "\t}\n" +
            "\t.msg {\n" +
            "\t\tbackground: white;\n" +
            "\t\tmin-width: 50px;\n" +
            "\t\tpadding: 10px;\n" +
            "\t\tborder-radius: 2px;\n" +
            "\t\tbox-shadow: 0px 2px 0px rgba(0, 0, 0, 0.07);\n" +
            "\t}\n" +
            "\t.msg p {\n" +
            "\t\tfont-size: 0.8rem;\n" +
            "\t\tmargin: 0 0 0.2rem 0;\n" +
            "\t}\n" +
            "\t.msg img {\n" +
            "\t\tposition: relative;\n" +
            "\t\tdisplay: block;\n" +
            "\t\twidth: 450px;\n" +
            "\t\tborder-radius: 5px;\n" +
            "\t\tbox-shadow: 0px 0px 3px #eee;\n" +
            "\t\ttransition: all .4s cubic-bezier(0.565, -0.260, 0.255, 1.410);\n" +
            "\t\tcursor: default;\n" +
            "\t\t-webkit-touch-callout: none;\n" +
            "\t\t-webkit-user-select: none;\n" +
            "\t\t-moz-user-select: none;\n" +
            "\t\t-ms-user-select: none;\n" +
            "\t}\n" +
            "\t.msg time {\n" +
            "\t\tfont-size: 0.7rem;\n" +
            "\t\tcolor: #ccc;\n" +
            "\t\tmargin-top: 3px;\n" +
            "\t\tfloat: right;\n" +
            "\t\tcursor: default;\n" +
            "\t\t-webkit-touch-callout: none;\n" +
            "\t\t-webkit-user-select: none;\n" +
            "\t\t-moz-user-select: none;\n" +
            "\t\t-ms-user-select: none;\n" +
            "\t}\n" +
            "\t.msg time:before{\n" +
            "\t/*\tcontent:\"\\f017\";*/\n" +
            "\t\tcolor: #ddd;\n" +
            "\t\tfont-family: FontAwesome;\n" +
            "\t\tdisplay: inline-block;\n" +
            "\t\tmargin-right: 4px;\n" +
            "\t}\n" +
            "\t.contenedor-campo-de-entrada\n" +
            "\t{\n" +
            "\t\tposition: fixed;\n" +
            "\t\twidth: 100%;\n" +
            "\t\tleft: 0;\n" +
            "\t\tbottom: 0;\n" +
            "\t\tbackground-color: white;\n" +
            "\t\tpadding: 15px\n" +
            "\t}\n" +
            "\t.imagen-del-chat\n" +
            "\t{\n" +
            "\t\tmax-width: 50px;\n" +
            "\t\tmax-height: 50px;\n" +
            "\t}\n" +
            "\t.footer{\n" +
            "\t\ttext-align: center;\n" +
            "\t}\n" +
            "\t</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\t\n" +
            "\t<div id=\"chat\" class=\"show\">\n" +
            "\t\t<div class=\"panel panel-default\">\n" +
            "\t\t\t<div class=\"panel-heading\">\n" +
            "\t\t\t\t<div class=\"row\">\n" +
            "\t\t\t\t\t<div class=\"col-xs-6 col-xs-offset-1 contenedor-centrado\">\n" +
            "\t\t\t\t\t\t<div><img src=\"#LOGO#\" class=\"logo-header\"></div>\n" +
            "\t\t\t\t\t</div>\n" +
            "\t\t\t\t</div>\n" +
            "\t\t\t\t\n" +
            "\t\t\t</div>\n" +
            "\t\t\t\n" +
            "\t\t\t<div class=\"panel-body\">\n" +
            "\t\t\t\t<div id=\"chat-id-chat\">\n" +
            "\t\t\t\t\t<div style=\"height: 57px\"></div>\n" +
            "\t\t\t\t\t<ol id=\"conversation\" class=\"chat\">\n" +
            "\t\t\t\t\t\t#CONVERSACION#\n" +
            "\t\t\t\t\t</ol>\n" +
            "\t\t\t\t</div>\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>\n" +
            "\t</div>\n" +
            "\n" +
            "\t<div class=\"footer\">Creado por <img src=\"#FOOTER#\" class=\"logo-footer\"></div>\n" +
            "\t\n" +
            "</body>\n" +
            "</html>";

	
}
