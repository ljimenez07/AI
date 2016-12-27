package com.ncubo.chatbot.configuracion;

public class Constantes {
	
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
	//public static String PATH_ARCHIVO_DE_CONFIGURACION_BA = "/home/tester/conversacionesBA.xml";
	
	// Agente
	public static String WORKSPACE_GENERAL = "general";
	public static String ANYTHING_ELSE = "anyThingElse";
	public static String CAMBIAR_INTENCION = "cambiarIntencion";
	public static String NODO_ACTIVADO = "nodo";
	public static String TERMINO_EL_TEMA = "terminoTema";
	public static String ORACIONES_AFIRMATIVAS = "oracionesAfirmativas";
	public static String CAMBIAR_A_GENERAL = "cambiarAGeneral";
	public static String ID_TEMA = "idTema";
	
	// Tipos de frases
	public static String FRASE_SALUDO = "saludo";
	public static String FRASE_DESPEDIDA = "despedida";
	public static String FRASE_FUERA_DE_CONTEXTO = "fueraDeContexto";
	public static String FRASE_ERROR_CON_WATSON = "errorDeComunicacionConWatson";
	
	// Intenciones
	public static String INTENCION_FUERA_DE_CONTEXTO = "out_of_scope";
	public static String INTENCION_NO_ENTIENDO = "noEntendi";
	public static String INTENCION_NO_ENTIENDO_ESPECIFICO = "noEntendiConocerte";
	public static String INTENCION_DESPEDIDA = "despedidas";
	public static String INTENCION_SALUDAR = "saludos";
	public static String INTENCION_DESPISTADOR = "despistar";
	public static String INTENCION_QUE_PUEDEN_PREGUNTAR = "que_pueden_preguntar";
	
}
