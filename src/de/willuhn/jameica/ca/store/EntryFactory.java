/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.ca.store.template.Attribute;
import de.willuhn.jameica.ca.store.template.Extension;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.logging.Logger;
import de.willuhn.util.ProgressMonitor;

/**
 * Factory zum Erzeugen, Lesen und Schreiben von Schluesseln.
 */
public class EntryFactory
{
  private Callback callback = null;
  
  static
  {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
    {
      Provider p = new BouncyCastleProvider();
      Logger.info("applying security provider " + p.getInfo());
      Security.addProvider(p);
    }

  }
  
  /**
   * ct.
   * @param callback der Callback fuer Passwort-Rueckfragen.
   */
  public EntryFactory(Callback callback)
  {
    this.callback = callback;
  }
  
  /**
   * Liest ein Schluesselpaar ein. 
   * @param cert Dateiname des Zertifikates.
   * @param privateKey Dateiname des Private-Keys. Optional.
   * @param format das Format des Keys.
   * @return der erzeugte Entry.
   * @throws Exception
   */
  public Entry read(File cert, File privateKey, Format format) throws Exception
  {
    InputStream is = null;

    try
    {
      Entry e = new Entry();

      // Public Key
      is = new BufferedInputStream(new FileInputStream(cert));
      e.setCertificate(format.readCertificate(is));
      is.close();
      is = null;

      // Private Key
      if (privateKey != null)
      {
        is = new BufferedInputStream(new FileInputStream(privateKey));
        PrivateKey key = null;
        
        // Wir versuchen es erstmal mit einem leeren Passwort.
        // Das wird bei Webserver-Zertifikaten haeufig so gemacht.
        // Wenn das fehlschlaegt, koennen wir den User allemal noch fragen
        try
        {
          Logger.info("trying to read " + privateKey + " with empty password");
          key = format.readPrivateKey(is,new char[0]);
          
          // Wir machen noch einen Test zur sicherheit
          if (key.getEncoded().length == 0)
            throw new Exception();
        }
        catch (Exception ex)
        {
          Logger.info(privateKey + " seems to have a password, asking user");
          key = format.readPrivateKey(is,this.callback.getPassword(privateKey));
        }
        
        e.setPrivateKey(key);
        is.close();
        is = null;
      }

      return e;
    }
    finally
    {
      if (is != null)
        is.close();
    }
  }
  
  /**
   * Schreibt einen Schluessel in die angegebenen Dateien.
   * @param entry der zu exportierende Schluessel.
   * @param cert Dateiname fuer das Zertifikate.
   * @param privateKey Dateiname fuer den Private-Key. Optional.
   * @param format das Dateiformat.
   * @throws Exception
   */
  public void write(Entry entry, File cert, File privateKey, Format format) throws Exception
  {
    OutputStream os = null;

    try
    {
      os = new BufferedOutputStream(new FileOutputStream(cert));
      format.writeCertificate(entry.getCertificate(),os);
      os.close();
      os = null;

      if (privateKey != null)
      {
        PrivateKey key = entry.getPrivateKey();
        if (key != null)
        {
          os = new BufferedOutputStream(new FileOutputStream(privateKey));
          format.writePrivateKey(key,os);
          os.close();
          os = null;
        }
      }
    }
    finally
    {
      if (os != null)
        os.close();
    }
  }
  
  /**
   * Erstellt einen neuen Schluessel basierend auf dem angegebenen Template.
   * @param template das Template.
   * @param monitor optionaler Fortschritts-Monitor.
   * @return der erstellte Schluessel.
   * @throws Exception
   */
  public Entry create(Template template, ProgressMonitor monitor) throws Exception
  {
    if (monitor != null) monitor.setStatus(ProgressMonitor.STATUS_RUNNING);

    try
    {
      Entry entry = new Entry();
      Entry issuer = template.getIssuer();


      if (monitor != null)
      {
        monitor.addPercentComplete(5);
        monitor.setStatusText("creating key pair");
      }
      Logger.info("creating key pair");
      

      ////////////////////////////////////////////////////////////////////////////
      // Schluesselpaar erstellen
      KeyPairGenerator kp = KeyPairGenerator.getInstance("RSA",BouncyCastleProvider.PROVIDER_NAME);
      kp.initialize(template.getKeySize());
      KeyPair keypair = kp.generateKeyPair();
      entry.setPrivateKey(keypair.getPrivate());
      //
      ////////////////////////////////////////////////////////////////////////////

      Logger.info("creating certificate");
      if (monitor != null)
      {
        monitor.setStatusText("creating certificate");
        monitor.addPercentComplete(20);
      }

      //////////////////////////////////////////////////////////////////////////
      // Gueltigkeit
      final Date validFrom = template.getValidFrom();
      final Date validTo   = template.getValidTo();
      Logger.info("  validity: " + validFrom + " - " + validTo);
      if (monitor != null) monitor.addPercentComplete(10);
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Seriennummer
      byte[] serno = new byte[8];
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed(System.nanoTime());
      random.nextBytes(serno);
      final BigInteger serial = new BigInteger(serno).abs();
      Logger.info("  serial number: " + serial);
      if (monitor != null) monitor.addPercentComplete(10);
      //////////////////////////////////////////////////////////////////////////

      // Subject
      X500Name subjectName = null;

      // Attribute
      final List<Attribute> attributes = template.getAttributes();
      X500NameBuilder nameBuilder = new X500NameBuilder();
      for (Attribute a:attributes)
      {
        String value = a.getValue();
        if (value == null || value.length() == 0)
          continue;
        
        nameBuilder.addRDN(new ASN1ObjectIdentifier(a.getOid()),value);
        subjectName = nameBuilder.build();
      }


      // Aussteller
      PrivateKey key = null;
      X500Name issuerName = null;
      
      if (issuer != null)
      {
        Logger.info("  issuer: " + issuer.getCertificate().getSubjectDN().getName());
        if (monitor != null) monitor.setStatusText("creating ca signed certificate");
        key = issuer.getPrivateKey();
        issuerName = X500Name.getInstance(issuer.getCertificate().getSubjectX500Principal().getEncoded());
      }
      else
      {
        Logger.info("  issuer: <selfsigned>");
        if (monitor != null) monitor.setStatusText("creating self signed certificate");
        key = entry.getPrivateKey();
        issuerName = subjectName;
      }


      // Generator initialisieren
      X509v3CertificateBuilder generator = new JcaX509v3CertificateBuilder(issuerName,serial,validFrom,validTo,subjectName,keypair.getPublic());

      Logger.info("  algorithm: " + template.getSignatureAlgorithm());
      if (monitor != null) monitor.addPercentComplete(20);

      //////////////////////////////////////////////////////////////////////////
      // Extensions

      // Template-Extensions
      List<Extension> extensions = template.getExtensions();
      for (Extension e:extensions)
      {
        generator.addExtension(new org.bouncycastle.asn1.x509.Extension(new ASN1ObjectIdentifier(e.getOid()),e.isCritical(),e.getValue()));
      }

      // Subject Key-Identifier als Extension hinzufuegen
      SubjectKeyIdentifier ski = new BcX509ExtensionUtils().createSubjectKeyIdentifier(this.readKeyInfo(keypair.getPublic()));
      generator.addExtension(org.bouncycastle.asn1.x509.Extension.subjectKeyIdentifier,false, ski);

      // CA Key-Identifier als Extension hinzufuegen
      if (issuer != null)
      {
        AuthorityKeyIdentifier aki = new BcX509ExtensionUtils().createAuthorityKeyIdentifier(this.readKeyInfo(issuer.getCertificate().getPublicKey()));
        generator.addExtension(org.bouncycastle.asn1.x509.Extension.authorityKeyIdentifier,false, aki);
      }
      //////////////////////////////////////////////////////////////////////////

      // Zertifikat erstellen
      ContentSigner signer = new JcaContentSignerBuilder(template.getSignatureAlgorithm()).build(key);
      X509CertificateHolder holder = generator.build(signer);
      entry.setCertificate(new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getCertificate(holder));
      ////////////////////////////////////////////////////////////////////////////

      Logger.info("certificate created");
      if (monitor != null)
      {
        monitor.setPercentComplete(100);
        monitor.setStatus(ProgressMonitor.STATUS_DONE);
        monitor.setStatusText("certificate created successfully");
      }
      
      
      return entry;
    }
    catch (Exception e)
    {
      if (monitor != null)
      {
        monitor.setPercentComplete(100);
        monitor.setStatus(ProgressMonitor.STATUS_ERROR);
        monitor.setStatusText("error while creating certificate: " + e.getMessage());
      }
      throw e;
    }
  }
  
  /**
   * erzeugt ein SubjectPublicKeyInfo aus dem Public-Key.
   * @param key der Public-Key.
   * @return das SubjectPublicKeyInfo.
   * @throws
   */
  private SubjectPublicKeyInfo readKeyInfo(PublicKey key) throws IOException
  {
    ASN1InputStream is = null;
    try
    {
      is = new ASN1InputStream(new ByteArrayInputStream(key.getEncoded()));
      return SubjectPublicKeyInfo.getInstance(is.readObject());
    }
    finally
    {
      IOUtil.close(is);
    }
  }
}
