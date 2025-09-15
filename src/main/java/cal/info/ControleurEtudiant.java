package cal.info;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ControleurEtudiant implements HttpHandler {

    List<Etudiant> lesEtudiants = new ArrayList<>();

    @Override
    public void handle(HttpExchange echange) throws IOException {
        String response = "Bienvenue au contrôle des étudiants !";
        
        try {
            // Récupération de la méthode HTTP
            String methode = echange.getRequestMethod();
            
            // Récupération du chemin de la requête
            String chemin = echange.getRequestURI().getPath();
            
            switch (methode) {
                case "GET":
                    if (chemin.equals("/etudiants")) {
                        response = afficherEtudiants();
                    }
                    break;
                case "POST":
                    // Pour l'instant, on ajoute un étudiant de test
                    ajouterEtudiant(new Etudiant("E001", "Dupont", "Jean", 
                        "jean.dupont@email.com", "Informatique"));
                    response = "Étudiant ajouté avec succès !";
                    break;
                case "PUT":
                    // Pour l'instant, on modifie le premier étudiant
                    if (!lesEtudiants.isEmpty()) {
                        Etudiant etudiant = lesEtudiants.get(0);
                        etudiant.setEmail("nouveau.email@email.com");
                        modifierEtudiant(etudiant);
                        response = "Étudiant modifié avec succès !";
                    } else {
                        response = "Aucun étudiant à modifier !";
                    }
                    break;
                case "DELETE":
                    if (!lesEtudiants.isEmpty()) {
                        String matricule = lesEtudiants.get(0).getMatricule();
                        supprimer(matricule);
                        response = "Étudiant supprimé avec succès !";
                    } else {
                        response = "Aucun étudiant à supprimer !";
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

    // Méthodes de gestion des étudiants
    public String afficherEtudiants() {
        if (lesEtudiants.isEmpty()) {
            return "Aucun étudiant disponible.";
        }
        return lesEtudiants.stream()
                .map(Etudiant::toString)
                .collect(Collectors.joining("\n"));
    }

    public void ajouterEtudiant(Etudiant etudiant) {
        lesEtudiants.add(etudiant);
    }

    public void modifierEtudiant(Etudiant etudiant) {
        for (int i = 0; i < lesEtudiants.size(); i++) {
            if (lesEtudiants.get(i).getMatricule().equals(etudiant.getMatricule())) {
                lesEtudiants.set(i, etudiant);
                break;
            }
        }
    }

    public void supprimer(String matricule) {
        lesEtudiants.removeIf(e -> e.getMatricule().equals(matricule));
    }
}
