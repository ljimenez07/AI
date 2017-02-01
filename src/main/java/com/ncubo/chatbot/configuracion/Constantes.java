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
	
	public static String ENTIDAD_SYS_NUMBER = "sys-number";
	
	
	//Base de datos Regresion
	
	public static String DB_HOST = "172.16.60.2";
	public static String DB_NAME = "dmuni";
	public static String DB_USER = "root";
	public static String DB_PASSWORD = "123456";
	
}
