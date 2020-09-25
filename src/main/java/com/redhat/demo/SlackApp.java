package com.redhat.demo;


import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.inject.Inject;
//////////////////////////////////////////////////////////////////////////
//This class is not used in the app, just left for reference//////////////
/////////////////////////////////////////////////////////////////////////

@WebServlet("/slack/events")
public class SlackApp extends HttpServlet  {

private static final Logger LOG = Logger.getLogger(SlackApp.class);

  private static final long serialVersionUID = 1L;


  @Inject
  @Channel("slack")
  Emitter<PigLatin> slackEmitter;

  protected void doPost(HttpServletRequest request, 
  HttpServletResponse response)
	      throws IOException {
          InputStream  is = request.getInputStream();
          StringBuilder sb = new StringBuilder();

          int i;
          char c;
          while((i = is.read())!=-1) {
         
            // converts integer to character
            c = (char)i;
            sb.append(c);
            
            // prints character
            System.out.print(c);
         }
          LOG.info(sb.toString());

          Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                LOG.info("Header Name : " + headerName);
                LOG.info("Header Value : " + request.getHeader(headerName));
            }

          PrintWriter writer = response.getWriter();          
          writer.print("Hola");
          PigLatin pigLatin = new PigLatin(sb.toString());
          pigLatin.translateToPigLatin();
          slackEmitter.send(pigLatin);
          writer.close();


  }



//   public SlackApp() throws IOException { 
//       super(initSlackApp()); 
// }

//   public SlackApp(App app) { super(app); }

//   private static App initSlackApp() throws IOException {
//     App app = new App();
//     app.command("/piglatin",
//     (req, ctx) -> {
//       LOG.info(req.getRequestBodyAsString());
//       String text = req.getPayload().getText();
//       LOG.info(text);
//       PigLatin pigLatin = new PigLatin(text);
//       pigLatin.translateToPigLatin();

//       slackEmitter.send(pigLatin);
//     //   PigLatinKafka pigKafka = new PigLatinKafka(pigLatin);
//     //   pigKafka.send();
    
//       return ctx.ack("What's up?");
//     });
//     return app;
//   }
}
