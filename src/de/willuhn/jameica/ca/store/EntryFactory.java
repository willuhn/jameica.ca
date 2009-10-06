/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/EntryFactory.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/06 00:27:37 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.X509V3CertificateGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.willuhn.jameica.ca.store.format.PEMFormat;
import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.ca.store.template.Attributes;
import de.willuhn.jameica.ca.store.template.Template;
import de.willuhn.util.ProgressMonitor;

/**
 * Hilfsklasse mit statischen Funktionen zum Erzeugen, Lesen und Schreiben von Schluesseln.
 */
public class EntryFactory
{
  /**
   * Schluesselformat.
   */
  public static enum FORMAT
  {
    PEM
  }
  
  private final static Map<FORMAT,Format> formatMap = new HashMap<FORMAT,Format>();
  
  static
  {
    formatMap.put(FORMAT.PEM,new PEMFormat());
  }
  
  /**
   * Liest ein Schluesselpaar ein. 
   * @param cert Dateiname des Zertifikates.
   * @param privateKey Dateiname des Private-Keys. Optional.
   * @return der erzeugte Entry.
   * @throws Exception
   */
  public static Entry read(File cert, File privateKey, FORMAT format) throws Exception
  {
    Format f = formatMap.get(format);
    if (f == null)
      throw new Exception("format " + format + " unknown");

    InputStream is = null;

    try
    {
      Entry e = new Entry();

      // Public Key
      is = new BufferedInputStream(new FileInputStream(cert));
      e.setCertificate(f.readCertificate(is));
      is.close();
      is = null;

      // Private Key
      if (privateKey != null)
      {
        is = new BufferedInputStream(new FileInputStream(privateKey));
        e.setPrivateKey(f.readPrivateKey(is));
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
   * Exportiert einen Schluessel in die angegebene Datei.
   * @param entry der zu exportierende Schluessel.
   * @param cert Dateiname fuer das Zertifikate.
   * @param privateKey Dateiname fuer den Private-Key. Optional.
   * @param format das Dateiformat.
   * @throws Exception
   */
  public static void export(Entry entry, File cert, File privateKey, FORMAT format) throws Exception
  {
    Format f = formatMap.get(format);
    if (f == null)
      throw new Exception("format " + format + " unknown");

    OutputStream os = null;
    try
    {
      os = new BufferedOutputStream(new FileOutputStream(cert));
      f.writeCertificate(entry.getCertificate(),os);
      os.close();
      os = null;

      if (privateKey != null)
      {
        PrivateKey key = entry.getPrivateKey();
        if (key != null)
        {
          os = new BufferedOutputStream(new FileOutputStream(privateKey));
          f.writePrivateKey(key,os);
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
  public static Entry create(Template template, ProgressMonitor monitor) throws Exception
  {
    if (monitor != null) monitor.setStatus(ProgressMonitor.STATUS_RUNNING);

    try
    {
      Entry entry = new Entry();

      ////////////////////////////////////////////////////////////////////////////
      // Schluesselpaar erstellen
      if (monitor != null) monitor.setStatusText("creating key pair");

      KeyPairGenerator kp = KeyPairGenerator.getInstance("RSA",BouncyCastleProvider.PROVIDER_NAME);
      kp.initialize(template.getKeySize());
      KeyPair keypair = kp.generateKeyPair();
      entry.setPrivateKey(keypair.getPrivate());

      if (monitor != null) monitor.addPercentComplete(20);
      //
      ////////////////////////////////////////////////////////////////////////////


      ////////////////////////////////////////////////////////////////////////////
      // Zertifikat erstellen
      if (monitor != null) monitor.setStatusText("creating subject certificate");

      X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
      generator.setPublicKey(keypair.getPublic());
      generator.setSignatureAlgorithm(template.getSignatureAlgorithm());

      if (monitor != null) monitor.addPercentComplete(20);

      // Gueltigkeit
      generator.setNotAfter(template.getValidTo());
      generator.setNotBefore(template.getValidFrom());

      // Subject
      Hashtable<DERObjectIdentifier, String> props = new Hashtable();
      Attributes as = template.getAttributes();
      if (as.CN != null)           props.put(X509Name.CN,           as.CN);
      if (as.GIVENNAME != null)    props.put(X509Name.GIVENNAME,    as.GIVENNAME);
      if (as.O != null)            props.put(X509Name.O,            as.O);
      if (as.OU != null)           props.put(X509Name.OU,           as.OU);
      if (as.SURNAME != null)      props.put(X509Name.SURNAME,      as.SURNAME);
      if (as.EmailAddress != null) props.put(X509Name.EmailAddress, as.EmailAddress);
      generator.setSubjectDN(new X509Name(props));

      // Seriennummer
      byte[] serno = new byte[8];
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed((long) (new Date().getTime()));
      random.nextBytes(serno);
      generator.setSerialNumber((new BigInteger(serno)).abs());

      if (monitor != null) monitor.addPercentComplete(20);

      generator.addExtension(MiscObjectIdentifiers.netscapeCertType,false,
                             new NetscapeCertType(NetscapeCertType.objectSigning | NetscapeCertType.smime));
      // TODO Verwendungszweck
      generator.addExtension(X509Extensions.KeyUsage, true,
          new KeyUsage(KeyUsage.digitalSignature |
                       KeyUsage.keyAgreement | 
                       KeyUsage.keyEncipherment | 
                       KeyUsage.nonRepudiation |
                       KeyUsage.dataEncipherment |
                       KeyUsage.keyCertSign |
                       KeyUsage.cRLSign
                      )
      );
      
      if (monitor != null) monitor.addPercentComplete(20);

      // Aussteller
      PrivateKey key = null;
      
      Entry issuer = template.getIssuer();
      if (issuer == null)
      {
        if (monitor != null) monitor.setStatusText("creating self signed certificate");
        generator.setIssuerDN(new X509Name(props));
        key = entry.getPrivateKey();
      }
      else
      {
        if (monitor != null) monitor.setStatusText("creating ca signed certificate");
        key = issuer.getPrivateKey();
      }
      entry.setCertificate(generator.generateX509Certificate(key));
      ////////////////////////////////////////////////////////////////////////////

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
}


/**********************************************************************
 * $Log: EntryFactory.java,v $
 * Revision 1.2  2009/10/06 00:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
