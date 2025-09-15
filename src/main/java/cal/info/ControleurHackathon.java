package cal.info;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControleurHackathon implements HttpHandler {

    List<Hackathon> lesHackathons = new ArrayList<>();

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String response = "Bienvenue au contrôle des hackathons !";
        
        try {
            // Récupération de la méthode HTTP
            String methode = echange.getRequestMethod();
            
            // Récupération du chemin de la requête
            String chemin = echange.getRequestURI().getPath();
            
            switch (methode) {
                case "GET":
                    if (chemin.equals("/hackathons")) {
                        response = listeHackathons();
                    } else if (chemin.startsWith("/hackathons/recherche/")) {
                        String motCle = chemin.substring("/hackathons/recherche/".length());
                        response = rechercheHackathons(motCle);
                    }
                    break;
                case "POST":
                    // Pour l'instant, on ajoute un hackathon de test
                    ajouterHackathon(new Hackathon("Hackathon Test", "Description test", 
                        java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2), 
                        "Lieu test", 50));
                    response = "Hackathon ajouté avec succès !";
                    break;
                case "PUT":
                    // Pour l'instant, on modifie le premier hackathon
                    if (!lesHackathons.isEmpty()) {
                        Hackathon hackathon = lesHackathons.get(0);
                        hackathon.setDescription("Description modifiée");
                        modifierHackathon(hackathon);
                        response = "Hackathon modifié avec succès !";
                    } else {
                        response = "Aucun hackathon à modifier !";
                    }
                    break;
                case "DELETE":
                    if (!lesHackathons.isEmpty()) {
                        String nomHackathon = lesHackathons.get(0).getNom();
                        supprimerHackathon(nomHackathon);
                        response = "Hackathon supprimé avec succès !";
                    } else {
                        response = "Aucun hackathon à supprimer !";
                    }
                    break;
                default:
                    response = "Méthode HTTP non supportée : " + methode;
            }
        } catch (Exception e) {
            response = "Erreur lors du traitement de la requête : " + e.getMessage();
            e.printStackTrace();
        }

        echange.sendResponseHeaders(200, response.length());
        OutputStream os = echange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Méthodes de gestion des hackathons
    public String listeHackathons() {
        if (lesHackathons.isEmpty()) {
            return "Aucun hackathon disponible.";
        }
        return lesHackathons.stream()
                .map(Hackathon::toString)
                .collect(Collectors.joining("\n"));
    }

    public String rechercheHackathons(String motHackathon) {
        List<Hackathon> resultats = lesHackathons.stream()
                .filter(h -> h.getNom().toLowerCase().contains(motHackathon.toLowerCase()) ||
                           h.getDescription().toLowerCase().contains(motHackathon.toLowerCase()))
                .collect(Collectors.toList());
        
        if (resultats.isEmpty()) {
            return "Aucun hackathon trouvé pour : " + motHackathon;
        }
        
        return resultats.stream()
                .map(Hackathon::toString)
                .collect(Collectors.joining("\n"));
    }

    public void ajouterHackathon(Hackathon hackathon) {
        lesHackathons.add(hackathon);
    }

    public void modifierHackathon(Hackathon hackathon) {
        for (int i = 0; i < lesHackathons.size(); i++) {
            if (lesHackathons.get(i).getNom().equals(hackathon.getNom())) {
                lesHackathons.set(i, hackathon);
                break;
            }
        }
    }

    public void supprimerHackathon(String nomHackathon) {
        lesHackathons.removeIf(h -> h.getNom().equals(nomHackathon));
    }
}
