package Ecom;

import jade.core.AID;import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AgentsClient extends Agent {
    private JTextField productField;
    private JTextField quantityField;
    private JTextField deliveryTimeField;

    @Override
    protected void setup() {
        System.out.println("Démarrage de l'agentClient: " + this.getAID().getName());

        // Création de la description de l'agent Acheteur
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Vente-Voiture"); // Remplacez "client-service-type" par le type approprié
        sd.setName("client");
        agentDescription.addServices(sd);

        // Enregistrement de la description de l'agent dans DF (Directory Facilitator)
        try {
            DFService.register(this, agentDescription);
            System.out.println(getLocalName() + " Enregistrement dans l'annuaire DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Création de l'interface graphique
        SwingUtilities.invokeLater(() -> createGUI());
        
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Attendre un message de type PROPOSE
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage proposeMessage = receive(mt);

                if (proposeMessage != null) {
                    // Traitement du message PROPOSE
                    String content = proposeMessage.getContent();
                    System.out.println("Réception du message PROPOSE avec le contenu : " + content);

                    // Ajoutez ici le code pour extraire et traiter les informations du message
                    // Vous pouvez utiliser des expressions régulières, des analyseurs JSON, etc.

                    // Exemple : récupération du prix à partir du contenu du message
                    int price = extractPriceFromMessage(content);

                    // Récupération de l'AID de l'expéditeur
                    AID senderAID = proposeMessage.getSender();

                    // Création du message ACCEPT ou REFUSE
                    ACLMessage replyMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL); // Utilisez REFUSE_PROPOSAL pour REFUSE
                    replyMessage.setContent("Votre proposition a été " + (price > 100 ? "acceptée" : "refusée")); // Exemple conditionnel
                    System.out.println("Votre proposition a été " + (price > 100 ? "acceptée" : "refusée"));
                    replyMessage.addReceiver(senderAID);

                    // Envoi du message ACCEPT ou REFUSE
                    send(replyMessage);
                    System.out.println("Réponse envoyée à l'agent : " + senderAID.getName());
                } else {
                    block(); // Se bloquer en attendant un message PROPOSE
                }
            }
        });

    }
 // Méthode pour extraire le prix du contenu du message
    private int extractPriceFromMessage(String messageContent) {
        // Implémentez la logique pour extraire le prix du contenu du message
        // Vous pouvez utiliser des expressions régulières, des analyseurs JSON, etc.
        return 440; // Mettez la logique appropriée ici
    }
    
    private void createGUI() {
        JFrame frame = new JFrame("Agent Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2));

        JLabel productLabel = new JLabel("Produit:");
        productField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantité:");
        quantityField = new JTextField();
        JLabel deliveryTimeLabel = new JLabel("Délai de remise:");
        deliveryTimeField = new JTextField();
        JButton submitButton = new JButton("Soumettre");

        // Ajout des composants à la fenêtre
        frame.add(productLabel);
        frame.add(productField);
        frame.add(quantityLabel);
        frame.add(quantityField);
        frame.add(deliveryTimeLabel);
        frame.add(deliveryTimeField);
        frame.add(submitButton);

        // Ajout d'un écouteur d'événement au bouton Soumettre
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupération des données saisies
                String product = productField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                int deliveryTime = Integer.parseInt(deliveryTimeField.getText());

             // Création d'un message CFP
                ACLMessage cfpMessage = new ACLMessage(ACLMessage.CFP);
                cfpMessage.setContent("Demande de proposition");
                // Vous pouvez ajouter des informations supplémentaires au contenu du message

                // Ajout des destinataires (ici, l'agent Ecom)
                AID ecomAID = new AID("AgentEcom", AID.ISLOCALNAME); // Assurez-vous que le nom de l'agent est correct
                cfpMessage.addReceiver(ecomAID);

                // Envoi du message CFP
                send(cfpMessage);
                System.out.println("Le message envoyer avec succer !...");

                // Exemple : Affichage des données
                System.out.println("Produit: " + product + ", Quantité: " + quantity + ", Délai: " + deliveryTime);
            }
        });


        // Affichage de la fenêtre
        frame.pack();
        frame.setVisible(true);
    }
}
