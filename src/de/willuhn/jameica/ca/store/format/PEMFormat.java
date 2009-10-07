/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/PEMFormat.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/07 11:47:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
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

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Implementierung des PEM-Format (Base64).
 */
public class PEMFormat implements Format
{
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readCertificate(java.io.InputStream)
   */
  public X509Certificate readCertificate(InputStream is) throws Exception
  {
    PEMReader reader = new PEMReader(new InputStreamReader(is));
    return (X509Certificate) reader.readObject();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readPrivateKey(java.io.InputStream, char[])
   */
  public PrivateKey readPrivateKey(InputStream is, final char[] password) throws Exception
  {
    PEMReader reader = new PEMReader(new InputStreamReader(is),new PasswordFinder()
    {
      /**
       * @see org.bouncycastle.openssl.PasswordFinder#getPassword()
       */
      public char[] getPassword()
      {
        try
        {
          return password;
        }
        catch (Exception e)
        {
          throw new RuntimeException(e);
        }
      }
    });
    KeyPair pair = (KeyPair) reader.readObject();
    return pair.getPrivate();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#writeCertificate(java.security.cert.X509Certificate, java.io.OutputStream)
   */
  public void writeCertificate(X509Certificate cert, OutputStream os) throws Exception
  {
    PEMWriter writer = new PEMWriter(new OutputStreamWriter(os));
    writer.writeObject(cert);
    writer.flush();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#writePrivateKey(java.security.PrivateKey, java.io.OutputStream)
   */
  public void writePrivateKey(PrivateKey key, OutputStream os) throws Exception
  {
    PEMWriter writer = new PEMWriter(new OutputStreamWriter(os));
    writer.writeObject(key);
    writer.flush();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#getName()
   */
  public String getName()
  {
    return "PEM-Format (Base64)";
  }

}


/**********************************************************************
 * $Log: PEMFormat.java,v $
 * Revision 1.3  2009/10/07 11:47:59  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/06 16:36:00  willuhn
 * @N Extensions
 * @N PEM-Writer
 *
 * Revision 1.1  2009/10/06 00:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
