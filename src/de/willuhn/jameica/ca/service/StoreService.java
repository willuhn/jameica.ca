/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/service/StoreService.java,v $
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

package de.willuhn.jameica.ca.service;

import java.rmi.RemoteException;
import java.util.List;

import de.willuhn.datasource.Service;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.util.ApplicationException;

/**
 * Service, der den Zugriff auf den Keystore bereitstellt.
 */
public interface StoreService extends Service
{
  /**
   * Liefert eine Liste aller Elemente des Keystore.
   * @return Liste aller Elemente des Keystore.
   * @throws RemoteException
   */
  public List<Entry> getEntries() throws RemoteException;
  
  /**
   * Speichert ein Element im Keystore.
   * @param entry das zu speichernde Element.
   * Existiert im Keystore bereits ein Entry mit diesem
   * Alias-Namen wird er automatisch ueberschrieben. Andernfalls wird
   * ein neuer Eintrag angelegt.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public void store(Entry entry) throws RemoteException, ApplicationException;
}


/**********************************************************************
 * $Log: StoreService.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
