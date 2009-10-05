/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/Attic/Test.java,v $
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

package de.willuhn.jameica.ca;

import java.io.File;

import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.logging.Logger;


/**
 * Tester.
 */
public class Test
{
  public static void main(String[] args) throws Exception
  {
    Store store = new Store(new File("/tmp/install/test.keystore"),new Callback()
    {
      public char[] getStorePassword() throws Exception
      {
        return "test".toCharArray();
      }
      public char[] getPassword(Entry entry) throws Exception
      {
        return getStorePassword();
      }
    });

    store.store(EntryFactory.fromPublicKey(new File("/tmp/install/ca.crt")));
    Logger.flush();
  }
}


/**********************************************************************
 * $Log: Test.java,v $
 * Revision 1.1  2009/10/05 16:02:38  willuhn
 * @N Neues Jameica-Plugin: "jameica.ca" - ein Certifcate-Authority-Tool zum Erstellen und Verwalten von SSL-Zertifikaten
 *
 **********************************************************************/
