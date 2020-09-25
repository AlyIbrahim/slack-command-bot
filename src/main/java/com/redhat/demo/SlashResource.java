package com.redhat.demo;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;


@Path("/slash")
public class SlashResource {

    private PigLatin pigLatin;
    private static final Logger LOG = Logger.getLogger(SlashResource.class);

    @Inject
    @Channel("slack")
    Emitter<PigLatin> slackEmitter;

    public SlashResource() {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String translate(String input, @PathParam String command, @PathParam String text) throws UnsupportedEncodingException {
        LOG.info(input);

        HashMap<String, String> paramsMap = new HashMap<String, String>();

        String[] params = input.split("&");
        for (String param : params){
            LOG.info(param.split("=")[0]);
            LOG.info(param.split("=")[1]);
            paramsMap.put( URLDecoder.decode(param.split("=")[0], StandardCharsets.UTF_8.name()) , URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8.name()) );
        }
        switch (paramsMap.get("command")) {
            case "/piglatin":
                return piglatinHandler(paramsMap.get("text"));
            default:
                return "Unknown Command";
        }

    }

    private String piglatinHandler(String inputtext){
        pigLatin = new PigLatin(inputtext);
        pigLatin.translateToPigLatin();
        BigInteger fact=BigInteger.valueOf(1); 
        LOG.info(pigLatin.inputText + " translated to " + pigLatin.outputText + " (" + fact + ")");
        slackEmitter.send(pigLatin);
        return pigLatin.outputText;
    }

}