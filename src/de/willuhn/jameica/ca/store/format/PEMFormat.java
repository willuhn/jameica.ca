/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.format;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import de.willuhn.io.IOUtil;
import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Implementierung des PEM-Format (Base64).
 */
public class PEMFormat implements Format
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readCertificate(java.io.InputStream)
   */
  public X509Certificate readCertificate(InputStream is) throws Exception
  {
    PEMParser reader = null;
    
    try
    {
      reader = new PEMParser(new InputStreamReader(is));
      return (X509Certificate) reader.readObject();
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readPrivateKey(java.io.InputStream, char[])
   */
  public PrivateKey readPrivateKey(InputStream is, final char[] password) throws Exception
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
   * @see de.willuhn.jameica.ca.store.format.Format#writeCertificate(java.security.cert.X509Certificate, java.io.OutputStream)
   */
  public void writeCertificate(X509Certificate cert, OutputStream os) throws Exception
  {
    JcaPEMWriter writer = null;
    try
    {
      writer = new JcaPEMWriter(new OutputStreamWriter(os));
      writer.writeObject(cert);
      writer.flush();
    }
    finally
    {
      IOUtil.close(writer);
    }
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#writePrivateKey(java.security.PrivateKey, java.io.OutputStream)
   */
  public void writePrivateKey(PrivateKey key, OutputStream os) throws Exception
  {
    JcaPEMWriter writer = null;
    try
    {
      writer = new JcaPEMWriter(new OutputStreamWriter(os));
      writer.writeObject(key);
      writer.flush();
    }
    finally
    {
      IOUtil.close(writer);
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
