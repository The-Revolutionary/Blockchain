package blockchain;

import java.util.Date;
import java.util.ArrayList;

public class Block {

		public String hash;
		public String previousHash;
		private long timeStamp; //as number of milliseconds since 1/1/1970.
		private int nonce;
		public String merkleRoot;
		
		//our data will be a simple message.
		public ArrayList<Transaction> transactions=new ArrayList<Transaction>();
		
		//Block Constructor.
		public Block(String previousHash ) {
			
			this.previousHash = previousHash;
			this.timeStamp = new Date().getTime();
			this.hash = calculateHash(); 
		}
		
		
		//Calculate new hash based on blocks contents
		public String calculateHash() {
			String calculatedhash = StringUtil.applySha256(	previousHash +
													Long.toString(timeStamp) +
													Integer.toString(nonce) +
													merkleRoot);
			return calculatedhash;
			}

		//The mineBlock() method takes in an int called difficulty, this is the number of 0’s they
		//must solve for. Low difficulty like 1 or 2 can be solved nearly instantly on most computers,
		//i’d suggest something around 4–6 for testing.
		
		//Increases nonce value until hash target is reached.
		public void mineBlock(int difficulty) {
			merkleRoot=StringUtil.getMerkleRoot(transactions);
			String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0"
			while(!hash.substring( 0, difficulty).equals(target)) {
				nonce ++;
				hash = calculateHash();
			}
			System.out.println("Block Mined!!! : " + hash);
		}
		
		
		//Add transactions to this block
		public boolean addTransaction(Transaction transaction) {
			//process transaction and check if valid, unless block is genesis block then ignore.
			if(transaction == null) return false;		
			if((!"0".equals(previousHash))) {
				if((transaction.processTransaction() != true)) {
					System.out.println("Transaction failed to process. Discarded.");
					return false;
				}
			}

			transactions.add(transaction);
			System.out.println("Transaction Successfully added to Block");
			return true;
		}
}
