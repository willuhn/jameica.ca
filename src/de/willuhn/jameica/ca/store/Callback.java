/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/store/Callback.java,v $
 * $Revision: 1.1 $
 * $Date: 2009/10/05 16:02:38 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store;

/**
 * Callback-Interface fuer die Passwort-Abfragen.
 */
public interface Callback
{
  /**
   * Liefert das Passwort des Keystores.
   * @return das Passwort.
   * @throws Exception
   */
  public char[] getStorePassword() throws Exception;
  
  /**
   * Liefert das Passwort fuer den Private-Key des Entry.
   * @param entry der Entry.
   * @return das Passwort.
   * @throws Exception
   */
  public char[] getPassword(Entry entry) throws Exception;
}


/**********************************************************************
 * $Log: Callback.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
