package com.ncubo.email;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ncubo.chatbot.configuracion.Constantes;

public class Email {
	
	static Properties mailServerProperties;
	static MimeMessage generateMailMessage;
	
	/**
	 * This is the constructor
	 * @throws SecurityException
	 * @throws IOException
	 */
	public Email(){}
	
	/**
	 * The method send a report by email
	 * @param tittle This is the subject of the email
	 * @param emailTo The string with the email to send the report (separated by ",")
	 * @param body The html body of the email
	 */
	public boolean sendEmail(String tittle, String emailTo, String body){
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Constantes.EMAIL_USER, Constantes.EMAIL_PASS);
			}
		  });

		try {
			String[] emails = emailTo.split(",");
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Constantes.EMAIL_PROJECT_NAME+" <"+Constantes.EMAIL_USER+">"));
			
			for (String email: emails){
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			}
			
			message.setSubject(tittle);
			message.setContent(body, "text/html");
			//message.setText(emailBody);

			Transport transport = session.getTransport("smtp");			
			//Transport.send(message);

			transport.connect("smtp.gmail.com", Constantes.EMAIL_USER, Constantes.EMAIL_PASS);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			
			System.out.println(Constantes.EMAIL_PROJECT_NAME+" has just sent an Email successfully.");
			return true;
		} catch (MessagingException e) {
			System.out.println(Constantes.EMAIL_PROJECT_NAME+" had a problem trying to send the email.");
			//throw new RuntimeException(e);
			return false;
		}
	}
	
	public static void main(String argv[]) throws Exception {
		Email email = new Email();
		
		String test = "<!DOCTYPE html>\n" +
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
	            "\t\tbackground-color: #48a1b4;\n" +
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
	            "\t\tborder: 5px solid #48a1b4;\n" +
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
	            "\t\tbackground-color: #48a1b4;\n" +
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
	            "\t\t\t\t\t\t<div><img src=\"http://devs.ncubo.com:7870/logo.png\" class=\"logo-header\"></div>\n" +
	            "\t\t\t\t\t</div>\n" +
	            "\t\t\t\t</div>\n" +
	            "\t\t\t\t\n" +
	            "\t\t\t</div>\n" +
	            "\t\t\t\n" +
	            "\t\t\t<div class=\"panel-body\">\n" +
	            "\t\t\t\t<div id=\"chat-id-chat\">\n" +
	            "\t\t\t\t\t<div style=\"height: 57px\"></div>\n" +
	            "\t\t\t\t\t<ol id=\"conversation\" class=\"chat\">\n" +
	            "\t\t\t\t\t\t<li class=\"other\">\n" +
	            "\t\t\t\t\t\t\t<div class=\"msg\">\n" +
	            "\t\t\t\t\t\t\t\t<p>¡Hola!</p><time>11:37</time>\n" +
	            "\t\t\t\t\t\t\t</div>\n" +
	            "\t\t\t\t\t\t</li>\n" +
	            "\t\t\t\t\t\t<li class=\"other\">\n" +
	            "\t\t\t\t\t\t\t<div class=\"msg\">\n" +
	            "\t\t\t\t\t\t\t\t<p>¿En qué le puedo ayudar?</p><time>11:37</time>\n" +
	            "\t\t\t\t\t\t\t</div>\n" +
	            "\t\t\t\t\t\t</li>\n" +
	            "\t\t\t\t\t\t\n" +
	            "\t\t\t\t\t\t<li class=\"self\">\n" +
	            "\t\t\t\t\t\t\t<div class=\"msg\">\n" +
	            "\t\t\t\t\t\t\t\t<p>horario de la muni</p><time>11:38</time>\n" +
	            "\t\t\t\t\t\t\t</div>\n" +
	            "\t\t\t\t\t\t</li>\n" +
	            "\t\t\t\t\t\t\n" +
	            "\t\t\t\t\t\t<li class=\"other\">\n" +
	            "\t\t\t\t\t\t\t<div class=\"msg\">\n" +
	            "\t\t\t\t\t\t\t\t<p>Nuestras oficinas están abiertas de Lunes a Viernes de 8 de la mañana a 4 de la tarde. Sin embargo, el servicio de recaudación (Cajas) tiene horario en esos mismos días, de 7 de la mañana a 5 de la tarde. Además, los Sábados, de 7 a 11 de la mañana.</p><time>11:38</time>\n" +
	            "\t\t\t\t\t\t\t</div>\n" +
	            "\t\t\t\t\t\t</li>\n" +
	            "\t\t\t\t\t\t<li class=\"other\">\n" +
	            "\t\t\t\t\t\t\t<div class=\"msg\">\n" +
	            "\t\t\t\t\t\t\t\t<p>¿Hay algo más con lo que le pueda ayudar?</p><time>11:38</time>\n" +
	            "\t\t\t\t\t\t\t</div>\n" +
	            "\t\t\t\t\t\t</li>\n" +
	            "\t\t\t\t\t</ol>\n" +
	            "\t\t\t\t</div>\n" +
	            "\t\t\t</div>\n" +
	            "\t\t</div>\n" +
	            "\t</div>\n" +
	            "\n" +
	            "\t<div class=\"footer\">Creado por <img src=\"http://devs.ncubo.com:7870/footerimg.png\" class=\"logo-footer\"></div>\n" +
	            "\t\n" +
	            "</body>\n" +
	            "</html>";
		
		email.sendEmail("Test", "sgonzales@cecropiasolutions.com", test);
	}
}
