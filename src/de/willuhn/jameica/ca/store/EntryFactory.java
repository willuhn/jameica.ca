/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/EntryFactory.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/10/06 16:47:58 $
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
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.ca.store.format.PEMFormat;
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
   * @return der erzeugte Entry.
   * @throws Exception
   */
  public Entry read(File cert, File privateKey, FORMAT format) throws Exception
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
        e.setPrivateKey(f.readPrivateKey(is, this.callback));
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
  public void write(Entry entry, File cert, File privateKey, FORMAT format) throws Exception
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
  public Entry create(Template template, ProgressMonitor monitor) throws Exception
  {
    if (monitor != null) monitor.setStatus(ProgressMonitor.STATUS_RUNNING);

    try
    {
      Entry entry = new Entry();

      ////////////////////////////////////////////////////////////////////////////
      // Schluesselpaar erstellen
      if (monitor != null) monitor.setStatusText("creating key pair");
      Logger.info("creating key pair");
      
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
      Logger.info("creating certificate");

      X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
      generator.setPublicKey(keypair.getPublic());
      generator.setSignatureAlgorithm(template.getSignatureAlgorithm());

      if (monitor != null) monitor.addPercentComplete(20);

      // Gueltigkeit
      generator.setNotAfter(template.getValidTo());
      generator.setNotBefore(template.getValidFrom());

      // Subject
      Hashtable<DERObjectIdentifier, String> props = new Hashtable();
      Vector<DERObjectIdentifier> order = new Vector<DERObjectIdentifier>();
      List<Attribute> attributes = template.getAttributes();
      for (Attribute a:attributes)
      {
        DERObjectIdentifier oid = new DERObjectIdentifier(a.getOid());
        props.put(oid,a.getValue());
        order.add(oid);
      }
      generator.setSubjectDN(new X509Name(order,props));

      // Seriennummer
      byte[] serno = new byte[8];
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      random.setSeed((long) (new Date().getTime()));
      random.nextBytes(serno);
      generator.setSerialNumber((new BigInteger(serno)).abs());

      if (monitor != null) monitor.addPercentComplete(20);

      List<Extension> extensions = template.getExtensions();
      for (Extension e:extensions)
      {
        generator.addExtension(e.getOid(),e.isCritical(),e.getValue());
      }
      
      if (monitor != null) monitor.addPercentComplete(20);

      // Aussteller
      PrivateKey key = null;
      
      Entry issuer = template.getIssuer();
      if (issuer == null)
      {
        if (monitor != null) monitor.setStatusText("creating self signed certificate");
        generator.setIssuerDN(new X509Name(order,props));
        key = entry.getPrivateKey();
      }
      else
      {
        if (monitor != null) monitor.setStatusText("creating ca signed certificate");
        generator.setIssuerDN(issuer.getCertificate().getSubjectX500Principal());
        key = issuer.getPrivateKey();
      }
      entry.setCertificate(generator.generate(key));
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
 * Revision 1.4  2009/10/06 16:47:58  willuhn
 * @N Aussteller fehlte
 *
 * Revision 1.3  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.2  2009/10/06 00:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
