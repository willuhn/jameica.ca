/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/service/StoreService.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/07 12:24:04 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.service;

import de.willuhn.datasource.Service;
import de.willuhn.jameica.ca.store.Store;

/**
 * Service, der den Zugriff auf den Keystore bereitstellt.
 */
public interface StoreService extends Service
{
  /**
   * Liefert den Store mit den Schluesseln.
   * @return der Store mit den Schluesseln.
   */
  public Store getStore();
}


/**********************************************************************
 * $Log: StoreService.java,v $
 * Revision 1.2  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
