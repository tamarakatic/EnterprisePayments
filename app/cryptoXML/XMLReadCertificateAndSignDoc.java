package cryptoXML;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

public class XMLReadCertificateAndSignDoc {
			
	public Certificate readCertificateFromFile(String filename) {
		Certificate cert = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = cf.generateCertificate(new FileInputStream(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return cert;
	}
	 public Certificate readCertificate(String keyStoreFile, 
			 							String keyStorePass, 
			 							String alias) {
			try {			
				KeyStore ks = KeyStore.getInstance("JKS", "SUN");
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
				ks.load(in, keyStorePass.toCharArray());
				
				if(ks.isKeyEntry(alias)) {
					Certificate cert = ks.getCertificate(alias);
					return cert;
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
			}
			return null;
		}
		
		public Document signDocument(Document doc, 
									 PrivateKey privateKey, 
									 Certificate cert) {
	        try {
				Element rootElement = doc.getDocumentElement();
				XMLSignature xmlSignature = new XMLSignature(doc, 
															 null, 
															 XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
				Transforms transforms = new Transforms(doc);
				
				transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
				transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
				   
				xmlSignature.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);			   
				xmlSignature.addKeyInfo(cert.getPublicKey());
				xmlSignature.addKeyInfo((X509Certificate) cert);

				rootElement.appendChild(xmlSignature.getElement());
				xmlSignature.sign(privateKey);
				
				return doc;
			} catch (TransformationException e) {
				e.printStackTrace();
			} catch (XMLSignatureException e) {
				e.printStackTrace();
			} catch (DOMException e) {
				e.printStackTrace();
			} catch (XMLSecurityException e) {
				e.printStackTrace();
			}
	        return null;
		}

}
