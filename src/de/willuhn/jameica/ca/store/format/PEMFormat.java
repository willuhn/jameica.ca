/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/PEMFormat.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/06 00:27:37 $
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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

/**
 * Implementierung des PEM-Format.
 */
public class PEMFormat implements Format
{
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readCertificate(java.io.InputStream)
   */
  public X509Certificate readCertificate(InputStream is) throws Exception
  {
    // Mal auf PEMReader umstellen
    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    return (X509Certificate) factory.generateCertificate(is);
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readPrivateKey(java.io.InputStream)
   */
  public PrivateKey readPrivateKey(InputStream is) throws Exception
  {
    // TODO: Sollte irgendwo an zentraler Stelle NUR EINMAL gemacht werden
    java.security.Security.addProvider(new BouncyCastleProvider());
    
    PEMReader reader = new PEMReader(new InputStreamReader(is),new PasswordFinder()
    {
      // TODO Passwort
      public char[] getPassword()
      {
        return new char[0];
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
    // TODO: Das ist DER-Format und nicht PEM
    os.write(cert.getEncoded());
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#writePrivateKey(java.security.PrivateKey, java.io.OutputStream)
   */
  public void writePrivateKey(PrivateKey key, OutputStream os) throws Exception
  {
    // TODO: Das ist DER-Format und nicht PEM
    os.write(key.getEncoded());
  }

}


/**********************************************************************
 * $Log: PEMFormat.java,v $
 * Revision 1.1  2009/10/06 00:27:37  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
