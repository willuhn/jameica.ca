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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;

/**
 * Implementierung des PKCS12-Format.
 */
public class PKCS12Format implements Format
{
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#read(java.io.File, java.io.File, de.willuhn.jameica.ca.store.Callback)
   */
  @Override
  public Entry read(File cert, File key, Callback callback) throws Exception
  {
    Entry e = new Entry();
    
    // Bei PKCS12 gibt es nur eine Datei. Uns ist egal, welche von beiden es ist.
    
    File f = cert != null ? cert : key;
    
    if (f == null)
      return e;

    InputStream is = null;
    
    try
    {
      is = new BufferedInputStream(new FileInputStream(f));
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(is,callback.getPassword(f));
      
      // Wir iterieren ueber die Liste der Alias und importieren einfach das erste gefundene
      Enumeration list = ks.aliases();
      while (list.hasMoreElements())
      {
          String alias = (String) list.nextElement();
          java.security.KeyStore.Entry ke = ks.getEntry(alias,new PasswordProtection(callback.getPassword(alias)));
          if (!(ke instanceof PrivateKeyEntry))
            continue;
          
          PrivateKeyEntry pke = (PrivateKeyEntry) ke;
          e.setCertificate((X509Certificate) pke.getCertificate());
          e.setPrivateKey(pke.getPrivateKey());
      }
    }
    finally
    {
      IOUtil.close(is);
    }
    
    return e;
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#write(de.willuhn.jameica.ca.store.Entry, java.io.File, de.willuhn.jameica.ca.store.Callback)
   */
  @Override
  public void write(Entry e, File dir, Callback callback) throws Exception
  {
    // Wir ermitteln die Dateinamen und Alias anhand des Common-Names.
    PrivateKey key       = e.getPrivateKey();
    X509Certificate cert = e.getCertificate();
    String name          = new Certificate(cert).getSubject().getAttribute(Principal.COMMON_NAME);
    
    File file = new File(dir,name + ".p12");
    if (!file.exists() || callback.overwrite(file))
    {
      KeyStore ks = KeyStore.getInstance("PKCS12");
      ks.load(null);
      ks.setKeyEntry(name, key, callback.getPassword(name), new java.security.cert.Certificate[]{cert});
      
      OutputStream os = null;
      try
      {
        os = new BufferedOutputStream(new FileOutputStream(file));
        ks.store(os, callback.getPassword(file));
      }
      finally
      {
        IOUtil.close(os);
      }
    }
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#getName()
   */
  public String getName()
  {
    return "PKCS12-Format (z.Bsp. S/MIME-Zertifikat, Dateiendung meist *.p12)";
  }
}
