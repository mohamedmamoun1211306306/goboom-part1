import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Card {
    private String suit;
    private String rank;
    private Player playedBy;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public Player getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(Player playedBy) {
        this.playedBy = playedBy;
    }

    @Override
    public String toString() {
        return suit + rank;
    }
}

class Player {
    private String name;
    private List<Card> hand;
    private Card playedCard;
    private int tricksWon;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.tricksWon = 0;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public void setPlayedCard(Card playedCard) {
        this.playedCard = playedCard;
    }

    public int getTricksWon() {
        return tricksWon;
    }

    public void setTricksWon(int tricksWon) {
        this.tricksWon = tricksWon;
    }

    public Card playCard(String leadSuit, List<Card> center, List<Card> deck) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("(" + name + ") Choose a card from your hand: ");
        String cardString = scanner.nextLine().trim().toUpperCase();
        Card card = null;

        for (Card c : hand) {
            if (c.toString().equalsIgnoreCase(cardString)) {
                if (leadSuit == null || c.getSuit().equalsIgnoreCase(leadSuit)) {
                    card = c;
                    break;
                }
            }
        }

        if (card == null) {
            System.out.println("Invalid card. Please choose a valid card from your hand.");
            return playCard(leadSuit, center, deck);
        }

        hand.remove(card); // Remove the played card from the player's hand
        return card;
    }
}

class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"C", "D", "H", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return cards;
    }
}

class Game {
    private List<Player> players;
    private List<Card> center;
    private Deck deck;
    private Player leadPlayer;

    public Game() {
        players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
        players.add(new Player("Player 4"));
        center = new ArrayList<>();
        deck = new Deck();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getCenter() {
        return center;
    }

    private Player getNextLeadPlayer(Player currentPlayer) {
        int currentPlayerIndex = players.indexOf(currentPlayer);
        return players.get((currentPlayerIndex + 1) % 4);
    }

    private Player playTrick(Card leadCard, Player leadPlayer) {
        center.add(leadCard);
        System.out.println("\n---------------");
        System.out.println("Trick #" + ((center.size() - 1) / 4 + 1));
        System.out.println("---------------");

        for (Player player : players) {
            System.out.println(player.getName() + "'s Hand: " + player.getHand());
        }

        System.out.println("\nLead Card: " + leadCard);
        System.out.println("\nCards in Deck: " + deck.getCards());
        System.out.println("\nCards in Center: " + center);

        Player currentPlayer = leadPlayer;
        String leadSuit = leadCard.getSuit();
        Card highestCard = leadCard;

        for (int i = 0; i < 3; i++) {
            currentPlayer = getNextLeadPlayer(currentPlayer);
            Card playedCard;
            if (currentPlayer == leadPlayer) {
                // Determine valid cards based on lead player's suit preference
                List<String> validSuits = new ArrayList<>();
                if (leadSuit.equals("A") || leadSuit.equals("5") || leadSuit.equals("9") || leadSuit.equals("K")) {
                    validSuits.add("C");
                }
                if (leadSuit.equals("2") || leadSuit.equals("6") || leadSuit.equals("10")) {
                    validSuits.add("D");
                }
                if (leadSuit.equals("3") || leadSuit.equals("7") || leadSuit.equals("J")) {
                    validSuits.add("H");
                }
                if (leadSuit.equals("4") || leadSuit.equals("8") || leadSuit.equals("Q")) {
                    validSuits.add("S");
                }

                // Player chooses a card from valid suits
                playedCard = currentPlayer.playCard(validSuits.isEmpty() ? null : validSuits.get(0), center, deck.getCards());
            } else {
                // Other players play normally
                playedCard = currentPlayer.playCard(leadSuit, center, deck.getCards());
            }

            center.add(playedCard);
            playedCard.setPlayedBy(currentPlayer);
            System.out.println(currentPlayer.getName() + " played: " + playedCard);

            if (playedCard.getSuit().equalsIgnoreCase(leadSuit) && playedCard.getRank().compareTo(highestCard.getRank()) > 0) {
                highestCard = playedCard;
            }
        }

        Player trickWinner = highestCard.getPlayedBy();
        trickWinner.setTricksWon(trickWinner.getTricksWon() + 1);
        System.out.println("\nTrick #" + ((center.size() - 1) / 4) + " won by " + trickWinner.getName() + "!");
        center.clear();
        return trickWinner;
    }

    public void play() {
        deck.shuffle();

        // Deal 7 cards to each player
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                Card card = deck.getCards().remove(0);
                player.getHand().add(card);
            }
        }

        // Select a leading card and put it in the center
        Card leadingCard = deck.getCards().remove(0);
        center.add(leadingCard);

        // Determine lead player based on leading card
        if (leadingCard.getSuit().equals("Q")) {
            leadPlayer = players.get(3);
        } else if (leadingCard.getSuit().equals("8")) {
            leadPlayer = players.get(2);
        } else if (leadingCard.getSuit().equals("7") || leadingCard.getSuit().equals("J")) {
            leadPlayer = players.get(1);
        } else {
            leadPlayer = players.get(0);
        }

        System.out.println("Initial Hands:");
        for (Player player : players) {
            System.out.println(player.getName() + "'s Hand: " + player.getHand());
        }

        System.out.println("\nLead Card: " + leadingCard);
        System.out.println("\nCards in Deck: " + deck.getCards());
        System.out.println("\nCards in Center: " + center);

        for (int i = 0; i < 13; i++) {
            Card leadCard = leadPlayer.playCard(null, center, deck.getCards());
            leadCard.setPlayedBy(leadPlayer);
            System.out.println("\nLead Card: " + leadCard);

            Player trickWinner = playTrick(leadCard, leadPlayer);
            System.out.println("\nTrick #" + ((i % 4) + 1) + " won by " + trickWinner.getName() + "!");

            leadPlayer = trickWinner; // Set the next lead player as the trick winner
        }

        System.out.println("\n--- Game Over ---");
        System.out.println("Tricks won:");

        for (Player player : players) {
            System.out.println(player.getName() + ": " + player.getTricksWon());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.play();}
}
