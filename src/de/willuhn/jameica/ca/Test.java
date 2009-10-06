/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/Attic/Test.java,v $
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

package de.willuhn.jameica.ca;

import java.io.File;

import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.EntryFactory.FORMAT;
import de.willuhn.jameica.ca.store.template.WebserverTemplate;


/**
 * Tester.
 */
public class Test
{
  public static void main(String[] args) throws Exception
  {
    Callback cb = new Callback()
    {
      /**
       * @see de.willuhn.jameica.ca.store.Callback#getPassword(java.lang.Object)
       */
      public char[] getPassword(Object context) throws Exception
      {
        return "".toCharArray();
      }
    };
    
    EntryFactory ef = new EntryFactory(cb);
    
    WebserverTemplate template = new WebserverTemplate();
    template.setHostname("www.willuhn.de");
    Entry e = ef.create(template,null);
    ef.write(e,new File("/tmp/install/server.crt"),new File("/tmp/install/server.key"),FORMAT.PEM);
  }
}


/**********************************************************************
 * $Log: Test.java,v $
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
