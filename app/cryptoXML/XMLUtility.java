package cryptoXML;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class XMLUtility {
	
    public XMLUtility() {
        Security.addProvider(new BouncyCastleProvider());
        org.apache.xml.security.Init.init();
    }
	
	public Document encrypt(Document doc, 
							SecretKey key, 
							Certificate certificate) {
		try {
		    XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.TRIPLEDES);
		    xmlCipher.init(XMLCipher.ENCRYPT_MODE, key);
		    
			XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_v1dot5);
		    keyCipher.init(XMLCipher.WRAP_MODE, certificate.getPublicKey());
		    EncryptedKey encryptedKey = keyCipher.encryptKey(doc, key);
		    
		    EncryptedData encryptedData = xmlCipher.getEncryptedData();
		    KeyInfo keyInfo = new KeyInfo(doc);
		    keyInfo.addKeyName("Sifrovan tajni kljuc");
		    keyInfo.add(encryptedKey);
	        encryptedData.setKeyInfo(keyInfo);
			
			NodeList odseci = doc.getElementsByTagName("invoices");
			Element odsek = (Element) odseci.item(0);
			
			xmlCipher.doFinal(doc, odsek, true);
			
			return doc;
		} catch (XMLEncryptionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document decrypt(Document doc, 
							PrivateKey privateKey) {
		try {
			XMLCipher xmlCipher = XMLCipher.getInstance();
			xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
			xmlCipher.setKEK(privateKey);
			
			NodeList encDataList = doc.getElementsByTagNameNS(
								   "http://www.w3.org/2001/04/xmlenc#", 
								   "EncryptedData");
			Element encData = (Element) encDataList.item(0);
			
			xmlCipher.doFinal(doc, encData); 
			return doc;
		} catch (XMLEncryptionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static PrivateKey loadPrivateKey(String key64) 
				throws GeneralSecurityException, IOException {
	    byte[] clear = new BASE64Decoder().decodeBuffer(key64);
	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
	    KeyFactory fact = KeyFactory.getInstance("DSA");
	    PrivateKey priv = fact.generatePrivate(keySpec);
	    Arrays.fill(clear, (byte) 0);
	    return priv;
	}
	
	public byte[] documentToByte(Document document)
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    org.apache.xml.security.utils.XMLUtils.outputDOM(document, baos, true);
	    return baos.toByteArray();
	}
	
	public boolean verifySignature(Document doc) {
		try {
			NodeList signatures = doc.getElementsByTagNameNS(
								  "http://www.w3.org/2000/09/xmldsig#", 
								  "Signature");
			Element signedElement = (Element) signatures.item(0);
			XMLSignature signature = new XMLSignature(signedElement, null);
			KeyInfo keyInfo = signature.getKeyInfo();
			if(keyInfo != null) {
				keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			    keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
	
			    if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) { 
			        Certificate cert = keyInfo.itemX509Data(0)
			        						   .itemCertificate(0)
			        						   .getX509Certificate();
			        if(cert != null) 
			        	return signature.checkSignatureValue((X509Certificate) cert);
			    }
			}
		} catch (XMLSecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public SecretKey generateDataEncryptionKey() {
        try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
			return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return null;
    }
	
	public PrivateKey readPrivateKey(String keyStoreFile, 
									 String keyStorePass, 
									 String alias, 
									 String pass) {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
			keyStore.load(in, keyStorePass.toCharArray());
			
			if(keyStore.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
				return pk;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
}
