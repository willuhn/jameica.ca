/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.format;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;
import de.willuhn.logging.Logger;

/**
 * Implementierung des DER-Format (binaer).
 */
public class DERFormat implements Format
{
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#read(java.io.File, java.io.File, de.willuhn.jameica.ca.store.Callback)
   */
  @Override
  public Entry read(File cert, File key, Callback callback) throws Exception
  {
    Entry e = new Entry();
    
    if (cert != null)
    {
      InputStream is = null;
      
      try
      {
        is = new BufferedInputStream(new FileInputStream(cert));
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        e.setCertificate((X509Certificate) factory.generateCertificate(is));
      }
      finally
      {
        IOUtil.close(is);
      }
    }
    
    if (key != null)
    {
      InputStream is = null;
      PrivateKey k = null;

      // Wir versuchen es erstmal mit einem leeren Passwort.
      // Das wird bei Webserver-Zertifikaten haeufig so gemacht.
      // Wenn das fehlschlaegt, koennen wir den User allemal noch fragen
      try
      {
        is = new BufferedInputStream(new FileInputStream(key));
        Logger.info("trying to read " + key + " with empty password");
        k = this.readPrivateKey(is,new char[0]);
        
        // Wir machen noch einen Test zur sicherheit
        if (k.getEncoded().length == 0)
          throw new Exception();
      }
      catch (Exception ex)
      {
        Logger.info(key + " seems to have a password, asking user");
        k = this.readPrivateKey(is,callback.getPassword(key));
      }
      finally
      {
        IOUtil.close(is);
      }
      
      e.setPrivateKey(k);
    }
    
    return e;
  }
  
  /**
   * Versucht, den Private-Key aus dem Stream zu lesen.
   * @param is der Stream.
   * @param password das Passwort.
   * @return der Private-Key.
   * @throws Exception
   */
  private PrivateKey readPrivateKey(InputStream is, final char[] password) throws Exception
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    int count = 0;
    byte[] buf = new byte[1024];
    while ((count = is.read(buf)) != -1)
      bos.write(buf,0,count);
    
    KeyFactory kf = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec(bos.toByteArray());
    return kf.generatePrivate(keysp);
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#write(de.willuhn.jameica.ca.store.Entry, java.io.File, de.willuhn.jameica.ca.store.Callback)
   */
  @Override
  public void write(Entry e, File dir, Callback callback) throws Exception
  {
    // Wir ermitteln die Dateinamen anhand des Common-Names.
    PrivateKey key       = e.getPrivateKey();
    X509Certificate cert = e.getCertificate();
    String name          = new Certificate(cert).getSubject().getAttribute(Principal.COMMON_NAME);
    
    if (cert != null)
    {
      OutputStream os = null;
      File file = new File(dir,name + ".crt");
      if (!file.exists() || callback.overwrite(file))
      {
        try
        {
          os = new BufferedOutputStream(new FileOutputStream(file));
          os.write(cert.getEncoded());
        }
        finally
        {
          IOUtil.close(os);
        }
      }
    }
    
    if (key != null)
    {
      OutputStream os = null;
      File file = new File(dir,name + ".key");
      if (!file.exists() || callback.overwrite(file))
      {
        try
        {
          os = new BufferedOutputStream(new FileOutputStream(new File(dir,name + ".key")));
          os.write(key.getEncoded());
        }
        finally
        {
          IOUtil.close(os);
        }
      }
    }
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#getName()
   */
  public String getName()
  {
    return "DER-Format, binär (Dateiendung meist *.cer oder *.der)";
  }
}
