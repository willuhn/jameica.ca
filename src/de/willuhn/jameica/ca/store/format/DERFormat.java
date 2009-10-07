/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/DERFormat.java,v $
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Implementierung des DER-Format (binaer).
 */
public class DERFormat implements Format
{
  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readCertificate(java.io.InputStream)
   */
  public X509Certificate readCertificate(InputStream is) throws Exception
  {
    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    return (X509Certificate) factory.generateCertificate(is);
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#readPrivateKey(java.io.InputStream, char[])
   */
  public PrivateKey readPrivateKey(InputStream is, final char[] password) throws Exception
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
   * @see de.willuhn.jameica.ca.store.format.Format#writeCertificate(java.security.cert.X509Certificate, java.io.OutputStream)
   */
  public void writeCertificate(X509Certificate cert, OutputStream os) throws Exception
  {
    os.write(cert.getEncoded());
    os.flush();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#writePrivateKey(java.security.PrivateKey, java.io.OutputStream)
   */
  public void writePrivateKey(PrivateKey key, OutputStream os) throws Exception
  {
    os.write(key.getEncoded());
    os.flush();
  }

  /**
   * @see de.willuhn.jameica.ca.store.format.Format#getName()
   */
  public String getName()
  {
    return "DER-Format (binär)";
  }
}


/**********************************************************************
 * $Log: DERFormat.java,v $
 * Revision 1.3  2009/10/07 11:47:59  willuhn
 * *** empty log message ***
 *
 **********************************************************************/