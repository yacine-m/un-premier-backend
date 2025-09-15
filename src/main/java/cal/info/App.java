package cal.info;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Application principale avec serveur HTTP
 */
public class App 
{
    public static void main(String[] args) throws IOException {
        // Création du serveur HTTP qui écoutera sur le port 8000
        HttpServer serveur = HttpServer.create(new InetSocketAddress(8000), 0);

        // Première route "/accueil" :
        serveur.createContext("/accueil", new HttpHandler() {
                @Override
                public void handle(HttpExchange echange) throws IOException {
                    String response = "Bienvenue sur la page d'accueil !";
                    echange.sendResponseHeaders(200, response.length());
                    OutputStream os = echange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });

        // Lie le chemin /hackathons au ControleurHackathon
        serveur.createContext("/hackathons", new ControleurHackathon());

        // Lie le chemin /etudiants au ControleurEtudiant
        serveur.createContext("/etudiants", new ControleurEtudiant());

        // Démarrer le serveur
        serveur.setExecutor(null); // Créer un exécuteur par défaut
        serveur.start();

        System.out.println("Serveur démarré et en écoute sur le port 8000");
        System.out.println("URLs disponibles :");
        System.out.println("- http://localhost:8000/accueil");
        System.out.println("- http://localhost:8000/hackathons");
        System.out.println("- http://localhost:8000/etudiants");
    }
}
