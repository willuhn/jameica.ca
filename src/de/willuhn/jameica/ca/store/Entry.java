/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Entry.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 16:38:59 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Ein einzelner Eintrag aus dem StoreService.
 * In der Regel handelt es sich hier um ein Schluesselpaar.
 */
public class Entry
{
  private Store store          = null;

  private String alias         = null;
  private X509Certificate cert = null;
  private PrivateKey key       = null;
  
  /**
   * ct.
   * Oeffentlicher Konstruktor.
   */
  public Entry()
  {
    
  }
  
  /**
   * ct.
   * Interner Konstruktor.
   * @param store der Store, aus dem der Entry stammt.
   */
  Entry(Store store)
  {
    this.store = store;
  }
  
  /**
   * Liefert das Zertifikat.
   * @return das Zertifikat.
   * @throws Exception 
   */
  public X509Certificate getCertificate() throws Exception
  {
    return this.cert;
  }
  
  /**
   * Speichert das Zertifikat.
   * @param cert das Zertifikat.
   */
  public void setCertificate(X509Certificate cert)
  {
    this.cert = cert;
  }
  
  /**
   * Liefert den Private-Key - falls vorhanden.
   * @return der Private-Key.
   * @throws Exception
   */
  public PrivateKey getPrivateKey() throws Exception
  {
    if (this.key == null && this.store != null)
      this.store.unlock(this);
    return this.key;
  }
  
  /**
   * Speichert den Private-Key.
   * @param key der Private-Key.
   */
  public void setPrivateKey(PrivateKey key)
  {
    this.key = key;
  }

  /**
   * Liefert den Alias des Eintrages.
   * @return der Alias.
   */
  public String getAlias()
  {
    return this.alias;
  }
  
  /**
   * Speichert den Alias-Namen.
   * @param alias Alias-Name.
   */
  public void setAlias(String alias)
  {
    this.alias = alias;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return this.alias;
  }
  
  
}


/**********************************************************************
 * $Log: Entry.java,v $
 * Revision 1.2  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
