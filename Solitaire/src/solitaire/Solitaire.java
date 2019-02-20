package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		
		if(deckRear == null){
			return;
		}
		CardNode ptr = deckRear;
		for (int i =0; i < 28 ; i++){
			if (ptr.cardValue ==27){
				int tmp = ptr.cardValue;
				ptr.cardValue = ptr.next.cardValue;
				ptr.next.cardValue = tmp;
				return;
			}
			
			ptr=ptr.next;
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		CardNode ptr = deckRear;
		
		if (deckRear == null){
			return;
		}
		
		for (int i =0; i < 28 ; i++){
			if (ptr.cardValue == 28){
				int tmp = ptr.cardValue;
				ptr.cardValue = ptr.next.cardValue;
				ptr.next.cardValue = tmp;
				ptr=ptr.next;
				tmp = ptr.cardValue;
				ptr.cardValue = ptr.next.cardValue;
				ptr.next.cardValue = tmp;
				return;
			}
			ptr=ptr.next;
		}
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode prev = deckRear;
		CardNode lastNode = deckRear;
		CardNode ptr = deckRear.next;
		CardNode firstNode = deckRear.next;
		CardNode sec = deckRear.next.next;
		
		
		
		if(deckRear.cardValue == 27 || deckRear.cardValue ==28){
			while(ptr.cardValue != 27 && ptr.cardValue != 28){
				ptr = ptr.next;
				prev = prev.next;
			}
			
			deckRear = prev;
			
		}else if(deckRear.next.cardValue == 27 || deckRear.next.cardValue == 28){
			while(sec.cardValue != 27 && sec.cardValue != 28){
				sec = sec.next;
				}

			deckRear = sec;
					}
		else{
		while (ptr.cardValue != 27 && ptr.cardValue != 28) {
			prev = ptr;
			ptr = ptr.next;
		}
		CardNode tmp = prev;
		CardNode JA = ptr;
		ptr = ptr.next;

		while (ptr.cardValue != 27 && ptr.cardValue != 28) {
			ptr = ptr.next;
		}
		
		CardNode nptr = ptr.next;
		
		lastNode.next = JA;
		ptr.next = firstNode;
	    this.deckRear = tmp;
		
		deckRear.next = nptr;
	    }


		
	}
	 
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {		
		int number = deckRear.cardValue;
		if(number == 28 || number == 27){
			number = 27;
			return;
		}	
		
		CardNode prev = deckRear.next;
		CardNode firstNode = deckRear.next;
		int count = 0;
		
		while(prev.next != deckRear){
			prev = prev.next;
		}
		CardNode lastNode = deckRear;
			
		while(count != number){
			lastNode = lastNode.next;
			count++;
		}
		
		CardNode nlastNode = lastNode.next;
		
		prev.next = firstNode;
		lastNode.next = deckRear;
		deckRear.next = nlastNode;

	
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		jokerA();
		jokerB();
		tripleCut();
		countCut();
		
		int firstcardvalue = deckRear.next.cardValue;
		int key =0;
		
		if (firstcardvalue == 28){
			firstcardvalue = 27;
		}
		
		CardNode ptr = deckRear;
		
		for (int i = 0; i < firstcardvalue; i++){
			ptr = ptr.next;
		}
		
		key = ptr.next.cardValue;
		
		while (key == 27 || key == 28){
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			
			firstcardvalue = deckRear.next.cardValue;
			ptr = deckRear;
			
			if (firstcardvalue == 28){
				firstcardvalue = 27;
			}
			
			for (int i = 0; i < firstcardvalue; i++){
				ptr = ptr.next;
			}
			
			key = ptr.next.cardValue;
		}
		
	    return key;	
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		
		String lettermessage = "";
		message = message.toUpperCase();
		
		for (int i=0; i<message.length(); i++){
			if (Character.isLetter(message.charAt(i))){
				
				lettermessage += message.charAt(i);
			}
		}
		
		String encryptmessage = "";
		for (int i =0; i< lettermessage.length(); i++){
			char ch = lettermessage.charAt(i);
			int chposition = (int)(ch - 'A' +1);
			int key = getKey();
			int encryptnum = chposition+key;
			if (encryptnum > 26){
				encryptnum -= 26;
			}
			
			encryptmessage += (char)(encryptnum-1+'A');
		}
		
	    return encryptmessage;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		
		String lettermessage = "";
		message = message.toUpperCase();
	    
		for (int i=0; i<message.length(); i++){
			if (Character.isLetter(message.charAt(i))){
				lettermessage += message.charAt(i);
			}
		}
		
		String decryptmessage = "";
		for (int i =0; i<lettermessage.length(); i++){
			char ch = lettermessage.charAt(i);
			int chposition = (int)(ch - 'A' +1);
			int key = getKey();
			int decryptnum = chposition-key;
			
			if (decryptnum <= 0){
				decryptnum +=26;
			}
			
			decryptmessage += (char)(decryptnum-1+'A');
		}
		return decryptmessage;
	}
}
