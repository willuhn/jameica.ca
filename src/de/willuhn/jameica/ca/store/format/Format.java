/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/format/Format.java,v $
 * $Revision: 1.3 $
 * $Date: 2009/10/06 16:36:00 $
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
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import de.willuhn.jameica.ca.store.Callback;

/**
 * Interface fuer Schluesselformate.
 */
public interface Format
{
  /**
   * Schreibt ein Zertifikat in den OutputStream.
   * @param cert das Zertifikat.
   * @param os der OutputStream.
   * @throws Exception
   */
  public void writeCertificate(X509Certificate cert, OutputStream os) throws Exception;

  /**
   * Schreibt einen Private-Key in den OutputStream.
   * @param key der Private-Key.
   * @param os der OutputStream.
   * @throws Exception
   */
  public void writePrivateKey(PrivateKey key, OutputStream os) throws Exception;

  /**
   * Liest ein Zertifikat ein.
   * @param is InputStream mit dem Zertifikat.
   * @return das eingelesene Zertifikat.
   * @throws Exception
   */
  public X509Certificate readCertificate(InputStream is) throws Exception;

  /**
   * Liest einen Private-Key ein.
   * @param is InputStream mit dem Private-Key.
   * @param callback Callback fuer die Passwort-Abfrage.
   * @return der eingelesene Private-Key.
   * @throws Exception
   */
  public PrivateKey readPrivateKey(InputStream is, Callback callback) throws Exception;
}


/**********************************************************************
 * $Log: Format.java,v $
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
