package blockchain;
// In crypto-currencies, coin ownership is transfered on the Blockchain as transactions, 
//participants have an address which funds can be sent to and from. 
//In their basic form wallets can just store these addresses, most wallets however, 
//are also software able to make new transactions on the Blockchain.

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

	public PrivateKey privateKey;		// Used to sign transaction. Only owner of private key can spend the coins.
	public PublicKey publicKey;			// Acts as our address. Shared to receive payment.
										// Used to verify integrity of transaction.
	public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	
	
	public Wallet(){
		generateKeyPair();
	}


//We also send our public key along with the transaction and it can be used to verify that
//our signature is valid and data has not been tampered with.

// To generate Elliptic Curve KepPair
public void generateKeyPair() {
	try {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
		// Initialize the key generator and generate a KeyPair
		
		keyGen.initialize(ecSpec, random); //256 bytes provides an acceptable security level
		KeyPair keyPair = keyGen.generateKeyPair();
		
		// Set the public and private keys from the keyPair
		privateKey = keyPair.getPrivate();
		publicKey = keyPair.getPublic();
		
	}
	catch(Exception e) {
		throw new RuntimeException(e);
		}
	}


public float getBalance() {
	float total = 0;	
    for (Map.Entry<String, TransactionOutput> item: Bchain.UTXOs.entrySet()){
    	TransactionOutput UTXO = item.getValue();
        if(UTXO.isMine(publicKey)) { //if output belongs to me ( if coins belong to me )
        	UTXOs.put(UTXO.id,UTXO); //add it to our list of unspent transactions.
        	total += UTXO.value ; 
        }
    }  
	return total;
}

public Transaction sendFunds(PublicKey _recipient,float value ) {
	if(getBalance() < value) {
		System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
		return null;
	}
	ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	
	float total = 0;
	for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
		TransactionOutput UTXO = item.getValue();
		total += UTXO.value;
		inputs.add(new TransactionInput(UTXO.id));
		if(total > value) break;
	}
	
	Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
	newTransaction.generateSignature(privateKey);
	
	for(TransactionInput input: inputs){
		UTXOs.remove(input.transactionOutputId);
	}
	
	return newTransaction;
}

}