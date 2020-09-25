package com.redhat.demo;


import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;


import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.inject.Inject;

@WebServlet("/slack/events")
public class SlackApp extends SlackAppServlet {

  private static final Logger LOG = Logger.getLogger(SlackApp.class);

  @Inject
  @Channel("slack")
  Emitter<PigLatin> slackEmitter;

  private static final long serialVersionUID = 1L;

  public SlackApp() throws IOException { 
      super(initSlackApp()); 
}

  public SlackApp(App app) { super(app); }

  private static App initSlackApp() throws IOException {
    App app = new App();
    app.command("/piglatin",
    (req, ctx) -> {
      LOG.info(req.getRequestBodyAsString());
      String text = req.getPayload().getText();
      LOG.info(text);
      PigLatin pigLatin = new PigLatin(text);
      pigLatin.translateToPigLatin();

    //   PigLatinKafka pigKafka = new PigLatinKafka(pigLatin);
    //   pigKafka.send();
    
      return ctx.ack("What's up?");
    });
    return app;
  }
}