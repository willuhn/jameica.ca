/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.format;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung des PEM-Format (Base64).
 */
public class PEMFormat implements Format
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#read(java.io.File, java.io.File, de.willuhn.jameica.ca.store.Callback)
   */
  @Override
  public Entry read(File cert, File key, Callback callback) throws Exception
  {
    Entry e = new Entry();
    
    if (cert != null)
    {
      PEMParser reader = null;
      
      try
      {
        reader = new PEMParser(new InputStreamReader(new BufferedInputStream(new FileInputStream(cert))));
        X509CertificateHolder holder = (X509CertificateHolder) reader.readObject();
        
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate c = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(holder.getEncoded()));
        e.setCertificate(c);
      }
      finally
      {
        IOUtil.close(reader);
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
    PEMParser reader = null;
    
    try
    {
      PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password);
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
      
      reader = new PEMParser(new InputStreamReader(is));
      Object object = reader.readObject();
      KeyPair pair = null;
      if (object instanceof PEMEncryptedKeyPair)
      {
        pair = converter.getKeyPair(((PEMEncryptedKeyPair) object).decryptKeyPair(decProv));
        return pair.getPrivate();
      }
      else if (object instanceof PrivateKeyInfo)
      {
        return converter.getPrivateKey((PrivateKeyInfo) object);
      }
      
      throw new ApplicationException(i18n.tr("Schlüsselformat unbekannt: {0}",object.getClass().getName()));
    }
    finally
    {
      IOUtil.close(reader);
    }
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
      JcaPEMWriter writer = null;
      File file = new File(dir,name + ".crt");
      if (!file.exists() || callback.overwrite(file))
      {
        try
        {
          writer = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)));
          writer.writeObject(cert);
          writer.flush();
        }
        finally
        {
          IOUtil.close(writer);
        }
      }
    }
    
    if (key != null)
    {
      JcaPEMWriter writer = null;
      File file = new File(dir,name + ".key");
      if (!file.exists() || callback.overwrite(file))
      {
        try
        {
          writer = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)));
          writer.writeObject(key);
          writer.flush();
        }
        finally
        {
          IOUtil.close(writer);
        }
      }
    }
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#getName()
   */
  public String getName()
  {
    return "PEM-Format, Base64 (Dateiendung meist *.pem oder *.crt)";
  }
}
