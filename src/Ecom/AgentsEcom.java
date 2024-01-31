package Ecom;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentsEcom extends Agent{
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		System.out.println("Démarrage de l'agentEcom: " + this.getAID().getName());
		
		
        try {
            // Création de la description de l'agent Acheteur
            DFAgentDescription agentDescription = new DFAgentDescription();
            agentDescription.setName(getAID());
            
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Vente-Voiture");
            sd.setName("commerce-en-ligne");
            agentDescription.addServices(sd);
            
            // Enregistrement de la description de l'agent dans DF (Directory Facilitator)
            DFService.register(this, agentDescription);
            System.out.println(getLocalName() + " Enregistrement dans l'annuaire DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
        
     // Ajout d'un comportement cyclique pour attendre les demandes du client
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Attendre un message de type CFP (Call For Proposals) de l'agent client
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage cfpMessage = myAgent.receive(mt);
                System.out.println("cfpMessage  =  "+cfpMessage);

                // Attendre un message de type PROPOSE
                MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage proposeMessage = myAgent.receive(mt2);
                System.out.println("proposeMessage  =  "+myAgent.receive(mt2));

                if (cfpMessage != null) {
                    // Traitement du message CFP
                    System.out.println("Reçu une proposition de l'agent Ecom : " + cfpMessage.getContent());
                    String product = extractProductFromCFP(cfpMessage);

                    // Exemple : Génération d'une proposition
                    int price = generatePrice(product);
                    int deliveryTime = generateDeliveryTime(product);
                    int availableQuantity = generateAvailableQuantity(product);

                    // Envoi de la proposition à l'agent client
                    ACLMessage proposeMessage2 = new ACLMessage(ACLMessage.PROPOSE);
                    proposeMessage2.setContent("Prix: " + price + ", Délai: " + deliveryTime + ", Quantité disponible: " + availableQuantity);
                    System.out.println("Prix: " + price + ", Délai: " + deliveryTime + ", Quantité disponible: " + availableQuantity);
                    System.out.println("Envoi du message PROPOSE à l'agent client avec le contenu : " + proposeMessage2.getContent());
                    proposeMessage2.addReceiver(cfpMessage.getSender());
                    myAgent.send(proposeMessage2);
                } else if (proposeMessage != null) {
                    // Traitement du message PROPOSE
                    String content = proposeMessage.getContent();
                    System.out.println("Réception du message PROPOSE avec le contenu : " + content);

                    // Vérification du contenu du message
                    if (content.equals("Votre proposition a été acceptée")) {
                        // Extraction des informations du message
                        int price = extractPriceFromMessage(content);

                        // Mise à jour du stock
                        int updatedQuantity = updateAvailableQuantity(price);

                        // Envoi du message CONFIRM à l'agent client
                        ACLMessage confirmMessage = new ACLMessage(ACLMessage.CONFIRM);
                        confirmMessage.setContent("Votre proposition a été acceptée. Nouvelle quantité disponible : " + updatedQuantity);
                        confirmMessage.addReceiver(proposeMessage.getSender());
                        myAgent.send(confirmMessage);
                        System.out.println("Réponse CONFIRM envoyée à l'agent client : " + proposeMessage.getSender().getName());
                    } else {
                        // Le contenu du message n'est pas "Accept_Proposal", traiter en conséquence
                        System.out.println("Contenu du message PROPOSE non valide. Ignoré.");
                    }
                } else {
                    // Si aucun des deux types de messages n'est reçu, bloquer en attendant
                    block();
                    System.out.println("Attente d'un message CFP ou PROPOSE...");
                }
            }
        });

	}
	// Méthode pour extraire le prix du contenu du message PROPOSE
	private int extractPriceFromMessage(String messageContent) {
	    // Implémentez la logique pour extraire le prix du contenu du message PROPOSE
	    // Vous pouvez utiliser des expressions régulières, des analyseurs JSON, etc.
	    return 0; // Mettez la logique appropriée ici
	}

	// Méthode pour mettre à jour le nombre de produits disponibles en fonction du prix
	private int updateAvailableQuantity(int price) {
	    // Implémentez la logique pour mettre à jour la quantité disponible en fonction du prix
	    // Par exemple, décrémentez la quantité de 1 à chaque achat
	    return 100; // Mettez la logique appropriée ici
	}
	
	// Ajoutez ici d'autres méthodes nécessaires pour générer les propositions

    private String extractProductFromCFP(ACLMessage cfpMessage) {
        // Implémentez la logique pour extraire le produit du message CFP
        return cfpMessage.getContent();
    }

    private int generatePrice(String product) {
        // Implémentez la logique pour générer le prix en fonction du produit
         return 50;
    }

    private int generateDeliveryTime(String product) {
        // Implémentez la logique pour générer le délai de livraison en fonction du produit
        return 2;
    }

    private int generateAvailableQuantity(String product) {
        // Implémentez la logique pour générer la quantité disponible en fonction du produit
        return 100;
    }
	
}
