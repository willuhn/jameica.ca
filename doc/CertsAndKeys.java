/*
 * Copyright (c) 2006, Axel Nennker - http://axel.nennker.de/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The names of the contributors may NOT be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package org.xmldap.util;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.xmldap.asn1.*;
import org.xmldap.exceptions.TokenIssuanceException;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

/**
 * 
 * @author Axel Nennker
 */
public class CertsAndKeys {

	public final static DERObjectIdentifier netscapeCertType = new DERObjectIdentifier(
			"2.16.840.1.113730.1.1");

	/** Creates a new instance of XmldapCertsAndKeys */
	private CertsAndKeys() {
	}

	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException,
			NoSuchProviderException {
		Provider provider = new BouncyCastleProvider();
		return generateKeyPair(provider);
	}

	public static KeyPair generateKeyPair(Provider provider)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", provider);
		keyGen.initialize(2048, new SecureRandom());
		return keyGen.generateKeyPair();
	}

	public static X509Certificate infocard2Certificate(KeyPair kp)
			throws TokenIssuanceException {
		X509Certificate cert = null;
		return cert;
	}

	public static X509Certificate generateCaCertificate(
			Provider provider, String friendlyName,
			KeyPair kp, X509Name issuer) throws InvalidKeyException,
			SecurityException, SignatureException, IOException, IllegalStateException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		return generateCaCertificate(provider, friendlyName, kp, issuer, issuer);
	}

	public static X509Certificate generateCaCertificate(
			Provider provider, String friendlyName,
			KeyPair kp) throws InvalidKeyException, SecurityException,
			SignatureException, IOException,
			IllegalStateException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		String issuerStr = "CN=firefox, OU=infocard selector, O=xmldap, L=San Francisco, ST=California, C=US";
		X509Name issuer = new X509Name(issuerStr);
		
		return generateCaCertificate(provider, friendlyName, kp, issuer, issuer);
	}

	static private X509V3CertificateGenerator addClientExtensions(
			X509V3CertificateGenerator gen) throws UnsupportedEncodingException {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		gen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment
						| KeyUsage.dataEncipherment | KeyUsage.keyCertSign));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));

		return gen;
	}

	static private X509V3CertificateGenerator addLogotype(
			X509V3CertificateGenerator gen) {
		String mediaType = "image/jpg";
		AlgorithmIdentifier algId = new AlgorithmIdentifier("1.3.14.3.2.26");
		byte[] digest = { (byte) 0x96, (byte) 0xda, (byte) 0x5a, (byte) 0xf6,
				(byte) 0x0f, (byte) 0x50, (byte) 0xf1, (byte) 0x84,
				(byte) 0x84, (byte) 0x3a, (byte) 0x3f, (byte) 0x2c,
				(byte) 0x2d, (byte) 0x9a, (byte) 0x5b, (byte) 0xf3,
				(byte) 0x8e, (byte) 0xa1, (byte) 0xd0, (byte) 0xd4 };
		DigestInfo digestInfo = new DigestInfo(algId, digest);
		DigestInfo[] logotypeHash = { digestInfo };
		String[] logotypeURI = { "http://static.flickr.com/10/buddyicons/18119196@N00.jpg?1115549486" };
		LogotypeDetails imageDetails = new LogotypeDetails(mediaType,
				logotypeHash, logotypeURI);
		// LogotypeImageInfo imageInfo = null;
		// LogotypeImage image = new LogotypeImage(imageDetails, imageInfo);
		// LogotypeImage[] images = { image };
		LogotypeDetails[] images = { imageDetails };
		LogotypeAudio[] audio = null;
		LogotypeData direct = new LogotypeData(images, audio);
		LogotypeInfo[] communityLogos = null;
		LogotypeInfo issuerLogo = new LogotypeInfo(direct);
		LogotypeInfo subjectLogo = null;
		OtherLogotypeInfo[] otherLogos = null;
		Logotype logotype = new Logotype(communityLogos, issuerLogo,
				subjectLogo, otherLogos);
		DERObject obj = logotype.toASN1Object();
		byte[] logotypeBytes = obj.getDEREncoded();
		gen.addExtension(Logotype.id_pe_logotype, false, logotypeBytes);
		return gen;
	}

	static private X509V3CertificateGenerator addCertificationPracticeStatementPointer(
			X509V3CertificateGenerator gen, String certificatePracticeStatement) {
		//		2.16.840.1.113733.1.7.23.3 / 6
		//		Certification Practice Statement pointer
		DERObjectIdentifier policyQualifierCpsOidStr = new DERObjectIdentifier(
				"1.3.6.1.5.5.7.2.1");
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(policyQualifierCpsOidStr);
		v.add(new DERIA5String(certificatePracticeStatement));
		gen.addExtension(new DERObjectIdentifier("2.16.840.1.113733.1.7.23.3"),
				false, new DERSequence(v));
		return gen;
	}

	static private X509V3CertificateGenerator addOCSP(
			X509V3CertificateGenerator gen, DERIA5String ocsp) {
		//		id-pkix OBJECT IDENTIFIER ::=
		//		  {iso(1) identified-organization(3) dod(6) internet(1) security(5)
		//		   mechanisms(5) pkix(7)}
		//		id-ad OBJECT IDENTIFIER ::= { id-pkix 48 }
		//		id-ad-ocsp OBJECT IDENTIFIER ::= { id-ad 1 }
		gen.addExtension(new DERObjectIdentifier("1.3.6.1.5.5.7.48.1"), false,
				ocsp);
		return gen;
	}

	static private X509V3CertificateGenerator addCaExtensions(
			X509V3CertificateGenerator gen, PublicKey pubKey)
			throws IOException {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(true));
		gen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment
						| KeyUsage.dataEncipherment | KeyUsage.keyCertSign
						| KeyUsage.cRLSign));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth));
		// gen.addExtension(X509Extensions.SubjectAlternativeName, false,
		// new GeneralNames(new GeneralName(GeneralName.rfc822Name,
		// "test@test.test")));

		// netscape-cert-type "2.16.840.1.113730.1.1"
		// * bit-0 SSL client			- 128
		// * bit-1 SSL server			- 64
		// * bit-2 S/MIME				- 32
		// * bit-3 Object Signing		- 16
		// * bit-4 Reserved 			- 8
		// * bit-5 SSL CA				- 4
		// * bit-6 S/MIME CA			- 2
		// * bit-7 Object Signing CA	- 1
		gen.addExtension(netscapeCertType, false, new DERBitString(
				new byte[] { 4 }));

		addSubjectKeyIdentifier(gen, pubKey);
		addAuthorityKeyIdentifier(gen, pubKey);
		return gen;
	}

	/**
	 * @param gen
	 * @param pubKey
	 * @throws IOException
	 */
	private static void addAuthorityKeyIdentifier(X509V3CertificateGenerator gen, PublicKey pubKey) throws IOException {
		{
			SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo(
					(ASN1Sequence) new ASN1InputStream(
							new ByteArrayInputStream(pubKey.getEncoded()))
							.readObject());
			AuthorityKeyIdentifier aki = new AuthorityKeyIdentifier(apki);

			gen.addExtension(X509Extensions.AuthorityKeyIdentifier.getId(),
					false, aki);
		}
	}

	/**
	 * @param gen
	 * @param pubKey
	 * @throws IOException
	 */
	private static void addSubjectKeyIdentifier(X509V3CertificateGenerator gen, PublicKey pubKey) throws IOException {
		{
			SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo(
					(ASN1Sequence) new ASN1InputStream(
							new ByteArrayInputStream(pubKey.getEncoded()))
							.readObject());
			SubjectKeyIdentifier ski = new SubjectKeyIdentifier(spki);
			gen.addExtension(X509Extensions.SubjectKeyIdentifier.getId(),
					false, ski);
		}
	}

	static private X509V3CertificateGenerator addSSLServerExtensions(
			X509V3CertificateGenerator gen) {
		gen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		gen.addExtension(X509Extensions.KeyUsage, false, new KeyUsage(
				KeyUsage.keyEncipherment | KeyUsage.digitalSignature));
		Vector<DERObjectIdentifier> extendedKeyUsageV = new Vector<DERObjectIdentifier>();
		extendedKeyUsageV.add(KeyPurposeId.id_kp_serverAuth);
		extendedKeyUsageV.add(KeyPurposeId.id_kp_clientAuth);
		// Netscape Server Gated Crypto
		// extendedKeyUsageV.add(new DERObjectIdentifier("2.16.840.1.113730.4.1"));
		// Microsoft Server Gated Crypto
//		extendedKeyUsageV
//				.add(new DERObjectIdentifier("1.3.6.1.4.1.311.10.3.3"));
		gen.addExtension(X509Extensions.ExtendedKeyUsage, false,
				new ExtendedKeyUsage(extendedKeyUsageV));
		// gen.addExtension(X509Extensions.SubjectAlternativeName, false,
		// new GeneralNames(new GeneralName(GeneralName.rfc822Name,
		// "test@test.test")));
//		gen.addExtension(netscapeCertType, false, new DERBitString(
//				new byte[] { 64 }));

		return gen;
	}

	/**
	 * generates an X509 certificate which is used to sign the xmlTokens in the
	 * firefox infocard selector
	 * 
	 * @param provider
	 * @param certificatePublicKey
	 * @param caPrivateKey
	 * @param issuer
	 * @param subject
	 * @param gender
	 * @param dateOfBirth
	 * @param streetAddress
	 * @param telephoneNumber
	 * @return
	 * @throws TokenIssuanceException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws CertificateEncodingException
	 * @throws IllegalStateException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public static X509Certificate generateClientCertificate(
			String provider,
			RSAPublicKey certificatePublicKey,
			RSAPrivateKey caPrivateKey,
			X509Name issuer, X509Name subject, String gender,
			Date dateOfBirth, String streetAddress, String telephoneNumber)
			throws TokenIssuanceException, UnsupportedEncodingException,
			InvalidKeyException, SecurityException, SignatureException,
			CertificateEncodingException, IllegalStateException,
			NoSuchProviderException, NoSuchAlgorithmException {

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(issuer);
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.HOUR, -2); 
		gen.setNotBefore(rightNow.getTime());
		rightNow.add(Calendar.YEAR, 5);
		gen.setNotAfter(rightNow.getTime());
		gen.setSubjectDN(subject);
		gen.setPublicKey(certificatePublicKey);
		gen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addClientExtensions(gen);
		SubjectDirectoryAttributes sda = new SubjectDirectoryAttributes(gender,
				dateOfBirth, streetAddress, telephoneNumber);
		if (sda.size() > 0) {
			gen.addExtension(X509Extensions.SubjectDirectoryAttributes, false,
					sda);
		}

		cert = gen.generate(caPrivateKey, provider);
		return cert;
	}

	/**
	 * generates an X509 certificate which is used to sign the xmlTokens in the
	 * firefox infocard selector
	 * 
	 * @param kp
	 * @param issuer
	 * @param subject
	 * @return
	 * @throws SignatureException 
	 * @throws SecurityException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 * @throws NoSuchProviderException 
	 * @throws IllegalStateException 
	 * @throws CertificateException 
	 * @throws TokenIssuanceException
	 */
	public static X509Certificate generateCaCertificate(
			Provider provider, String friendlyName,
			KeyPair kp, X509Name issuer, X509Name subject)
			throws InvalidKeyException, SecurityException, SignatureException,
			IOException,
			IllegalStateException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(issuer);
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.HOUR, -2);
		gen.setNotBefore(rightNow.getTime());
		rightNow.add(Calendar.YEAR, 5);
		gen.setNotAfter(rightNow.getTime());
		gen.setSubjectDN(subject);
		gen.setPublicKey(kp.getPublic());
		gen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addCaExtensions(gen, kp.getPublic());
		gen = addLogotype(gen);
		// gen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
		// new SubjectKeyIdentifierStructure(kp.getPublic()));
		cert = gen.generate(kp.getPrivate(), provider.getName());
		
		cert.checkValidity();
		cert.verify(kp.getPublic(), provider.getName());
		
        PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)cert;
        bagAttr.setBagAttribute(
            PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
            new DERBMPString(friendlyName));

		return cert;
	}

	public static PKCS10CertificationRequest generateCertificateRequest(
			X509Certificate cert,
			PrivateKey signingKey) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, IOException {
		ASN1EncodableVector attributes = new ASN1EncodableVector();
		
		Set<String> nonCriticalExtensionOIDs = cert.getNonCriticalExtensionOIDs();
		for (String nceoid : nonCriticalExtensionOIDs) {
			byte[] derBytes = cert.getExtensionValue(nceoid);
			ByteArrayInputStream bis = new ByteArrayInputStream(derBytes);
			ASN1InputStream dis = new ASN1InputStream(bis);
			DERObject derObject = dis.readObject();
			DERSet value = new DERSet(derObject);
			Attribute attr = new Attribute(new DERObjectIdentifier(nceoid), value);
			attributes.add(attr);
		}
		PKCS10CertificationRequest certificationRequest = new PKCS10CertificationRequest(
				"SHA1WithRSAEncryption", cert.getSubjectX500Principal(), cert.getPublicKey(), new DERSet(attributes), signingKey);
		return certificationRequest;
	}
	
	/**
	 * @param provider
	 * @param friendlyName
	 * @param caKeyPair
	 * @param caCert
	 * @param kp
	 * @param issuer
	 * @param subject
	 * @return
	 * @throws InvalidKeyException
	 * @throws SecurityException
	 * @throws SignatureException
	 * @throws IllegalStateException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 * @throws IOException 
	 */
	public static X509Certificate generateSSLServerCertificate(
			Provider provider, String friendlyName,
			KeyPair caKeyPair, X509Certificate caCert, KeyPair kp,
			X509Name issuer, X509Name subject) throws InvalidKeyException,
			SecurityException, SignatureException, IllegalStateException,
			NoSuchAlgorithmException, NoSuchProviderException, CertificateException, IOException {

		X509Certificate cert = null;

		X509V3CertificateGenerator gen = new X509V3CertificateGenerator();
		gen.setIssuerDN(PrincipalUtil.getSubjectX509Principal(caCert));
//		gen.setIssuerDN(issuer);
		
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.HOUR, -2);
		Date notBefore = calender.getTime();
		gen.setNotBefore(notBefore);

		// set notAfter to one minute before the caCert expires
		// some browsers are said to complain if the cert lives longer than the caCert
		// EV certificates must not live longer than 27 month
		calender.add(Calendar.MONTH, 27);
		Date max = calender.getTime();
		Date notAfter = caCert.getNotAfter();
		if (notAfter.after(max)) {
			notAfter = max;
		}
		calender.setTime(notAfter);
		calender.add(Calendar.MINUTE, -1);
		gen.setNotAfter(calender.getTime());
		
		gen.setSubjectDN(subject);
		gen.setPublicKey(kp.getPublic());
		gen.setSignatureAlgorithm("SHA1WithRSAEncryption");
		gen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		gen = addSSLServerExtensions(gen);
		gen = addLogotype(gen);
		gen = addCertificationPracticeStatementPointer(gen,
				"http://xmldap.org/cps");
		gen = addOCSP(gen, new DERIA5String("http://ocsp.xmldap.org"));
		if (caKeyPair != null) {
			addAuthorityKeyIdentifier(gen, caKeyPair.getPublic());
		}
		addSubjectKeyIdentifier(gen, kp.getPublic());

		if (caKeyPair != null) {
			cert = gen.generate(caKeyPair.getPrivate(), provider.getName());
		} else {
			cert = gen.generate(kp.getPrivate(), provider.getName());
		}
		
		cert.checkValidity();
		if (caKeyPair != null) {
			cert.verify(caKeyPair.getPublic(), provider.getName());
		} else {
			cert.verify(kp.getPublic(), provider.getName());
		}

		PKCS12BagAttributeCarrier   bagAttr = (PKCS12BagAttributeCarrier)cert;
        bagAttr.setBagAttribute(
            PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
            new DERBMPString(friendlyName));
		return cert;
	}

	public static KeyPair bytesToKeyPair(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (KeyPair) ois.readObject();
	}

	public static byte[] keyPairToBytes(KeyPair kp) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(kp);
		oos.close();
		return bos.toByteArray();
	}

	public static X509Certificate der2cert(String der)
			throws CertificateException {
		byte[] certBytes = Base64.decode(der);
		ByteArrayInputStream is = new ByteArrayInputStream(certBytes);
		BufferedInputStream bis = new BufferedInputStream(is);
		CertificateFactory cf = null;
		X509Certificate cert = null;
		cf = CertificateFactory.getInstance("X.509");
		cert = (X509Certificate) cf.generateCertificate(bis);
		return cert;
	}

	// private void storeInfoCardAsCertificate(String nickname, Document
	// infocard)
	// throws TokenIssuanceException { // temporary hack to store infocards
	// // as certificates
	// try {
	// X509Certificate cardAsCert = infocard2Certificate(infocard);
	// // store in firefox.jks
	// storeCardCertKeystore(nickname, cardAsCert, false);
	// // store in <ppi>.pem
	// storeCardCertPem(nickname, cardAsCert);
	// // store in <ppi>.p12
	// // storeCardCertP12(token.getPrivatePersonalIdentifier(),
	// // cardAsCert);
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (CertificateEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	// private X509Name claims2X509Name(Element data)
	// throws TokenIssuanceException {
	//
	// Vector oids = new Vector();
	// Vector values = new Vector();
	//
	// String value = getDataValue(data, "givenname");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.GIVENNAME);
	// values.add(value);
	// }
	// value = getDataValue(data, "surname");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.SURNAME);
	// values.add(value);
	// }
	// value = getDataValue(data, "emailaddress");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.E);
	// values.add(value);
	// }
	// // value = getDataValue(data, "streetladdress");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" streetladdress=");
	// // sb.append(value);
	// // }
	// value = getDataValue(data, "locality");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.L);
	// values.add(value);
	// }
	// value = getDataValue(data, "stateorprovince");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.ST);
	// values.add(value);
	// }
	// // value = getDataValue(data, "postalcode");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append("postalcode=");
	// // sb.append(value);
	// // }
	// value = getDataValue(data, "country");
	// if ((value != null) && !value.equals("")) {
	// oids.add(X509Name.C);
	// values.add(value);
	// }
	// // value = getDataValue(data, "primaryphone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" primaryphone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "otherphone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" otherphone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "mobilephone");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" mobilephone=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "dateofbirth");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" dateofbirth=");
	// // sb.append(value);
	// // }
	// // value = getDataValue(data, "gender");
	// // if ((value != null) && !value.equals("")) {
	// // sb.append(" gender=");
	// // sb.append(value);
	// // }
	//
	// return new X509Name(oids, values);
	// }

	// public X509Certificate infocard2Certificate(Document infocard, KeyPair
	// kp)
	// throws UnsupportedEncodingException, ParseException {
	// X509Certificate cert = null;
	// // KeyPair kp = new KeyPair(signingCert.getPublicKey(), signingKey);
	// X509Name issuer = new X509Name(
	// "CN=firefox, OU=infocard selector, O=xmldap, L=San Francisco,
	// ST=California, C=US");
	// Nodes dataNodes = infocard.query("/infocard/carddata/selfasserted");
	// Element data = (Element) dataNodes.get(0);
	// X509Name subject = claims2X509Name(data);
	//
	// DateFormat df = DateFormat.getDateInstance();
	// Date dateOfBirth = df.parse(getDataValue(data, "dateofbirth"));
	// cert = CertsAndKeys.generateClientCertificate(kp, issuer, subject,
	// getDataValue(data, "gender"), dateOfBirth, getDataValue(data,
	//						"streetladdress"), getDataValue(data, "primaryphone"));
	//		return cert;
	//	}

	static public void printCert(X509Certificate cert) throws CertificateParsingException
	{
		HashMap<String, String> oidNames = new HashMap<String,String>();
		oidNames.put("1.3.6.1.5.5.7.1.12", "Logotype");
		oidNames.put("1.3.6.1.5.5.7.3.1", "id_kp_serverAuth");
		oidNames.put("1.3.6.1.5.5.7.3.2", "id_kp_clientAuth");
		oidNames.put("1.3.6.1.5.5.7.48.1", "OCSP");
		oidNames.put("2.5.29.14", "Subject Key Identifier");
		oidNames.put("2.5.29.15", "id-ce-keyUsage");
		oidNames.put("2.5.29.35", "Authority Key Identifier");
		oidNames.put("2.5.29.37", "Extended key usage");
		oidNames.put("2.16.840.1.113733.1.7.23.3", "Class 3 CP");
//		oidNames.put("", "");

		String certType = cert.getType();
		System.out.println("certType: " + certType);
		int version = cert.getVersion();
		System.out.println("version: " + version);
		System.out.println("BasicConstraints: " + cert.getBasicConstraints());
		List<String> extendedKeyUsageSet = cert.getExtendedKeyUsage();
		for (String eku : extendedKeyUsageSet) {
			System.out.println("extendedKeyUsage: " + oidNames.get(eku));
		}
		boolean[] keyUsage = cert.getKeyUsage();
		System.out.print("keyUsage: ");
		for (boolean bool : keyUsage) {
			System.out.println("keyUsage: " + bool);
		}
		X500Principal principal = cert.getIssuerX500Principal();
		System.out.println("issuer: " + principal.getName());
		X500Principal subjectPrincipal = cert.getSubjectX500Principal();
		System.out.println("subject: " + subjectPrincipal.getName());
		Set<String> nonCriticalExtensionOIDs = cert.getNonCriticalExtensionOIDs();
		for (String nceo : nonCriticalExtensionOIDs) {
			System.out.println("nonCriticalExtensionOIDs: " + oidNames.get(nceo));
		}
	}
}
