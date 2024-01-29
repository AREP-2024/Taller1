package edu.escuelaing.arep.ASE.app;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpServerTest {
    private HttpConnection httpConnection = new HttpConnection("http://www.omdbapi.com/","926dbc03");

    @Test
    public void deberiaSerConcurrente(){
        
        List.of(1,100).parallelStream().forEach((num)->{
            for(int i=0;i<num;i++){
                Thread hilo = new Thread(() -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        var prueba1= HttpServer.mostrarPelicula(httpConnection, "Barbie");
                        JsonNode jsonNode = objectMapper.readTree(prueba1);

                        assertNotNull(prueba1);
                        assertNotEquals(prueba1, "");
                        assertEquals("Barbie", jsonNode.get("Title").asText());
                       
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        fail();
                    }

                });
                hilo.start();

            }           

        });
    }
    
}
