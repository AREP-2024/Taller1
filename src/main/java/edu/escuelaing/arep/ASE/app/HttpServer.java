package edu.escuelaing.arep.ASE.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    private static final String GET_URL="https://www.omdbapi.com/";
    private static final String GET_KEY="926dbc03";
    public static void main(String[] args) throws IOException {
        HttpConnection httpConnection = new HttpConnection(GET_URL,GET_KEY);
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while(running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {                
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;

            String esLaPrimera=null;
    
            while ((inputLine = in.readLine()) != null) {
                if (esLaPrimera==null) {
                    esLaPrimera = inputLine;                    
                }                                
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            if(esLaPrimera==null){
                continue;
            }
            

            String recurso = esLaPrimera.split(" ")[1];
            if (recurso.startsWith("/Peliculas")) {
                outputLine = mostrarPelicula(httpConnection,recurso.split("/")[2]);
                
            }else if (recurso.equals("/")){                
                outputLine = mostrarPagina();
            }
            else{
                outputLine = "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "My Web Site\n"
                + "</body>\n"
                + "</html>\n";
            }
          
            out.println(outputLine);
            
            out.close();
            in.close();
            clientSocket.close();

        }

        serverSocket.close();
    }

    public static String mostrarPagina( ) throws IOException{

        StringBuilder pagina = new StringBuilder();

        String outputLine;

        pagina.append("HTTP/1.1 200 OK\r\n");
        pagina.append("Content-Type: text/html\r\n");
        pagina.append("\r\n");
        File file = new File("src/main/java/edu/escuelaing/arep/ASE/app/pagina.html");
        BufferedReader reader = new BufferedReader(new FileReader(file));  
        while((outputLine = reader.readLine()) != null){
            pagina.append(outputLine);
            pagina.append("\n");
        }

        reader.close();
        return pagina.toString();
    

    }

    public static String mostrarPelicula(HttpConnection httpConnection, String nameMovie) throws IOException{
        StringBuilder pagina = new StringBuilder();

        String outputLine = httpConnection.infoMovieWithCache(nameMovie);

        pagina.append("HTTP/1.1 200 OK\r\n");
        pagina.append("Content-Type: text/json\r\n");
        pagina.append("\r\n");
        pagina.append(outputLine);

        return pagina.toString();

    }
    
}
