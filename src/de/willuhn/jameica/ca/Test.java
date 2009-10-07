/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/Attic/Test.java,v $
 * $Revision: 1.6 $
 * $Date: 2009/10/07 12:24:04 $
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
import java.util.List;

import de.willuhn.jameica.ca.store.Callback;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.ca.store.EntryFactory;
import de.willuhn.jameica.ca.store.Store;
import de.willuhn.jameica.ca.store.EntryFactory.FORMAT;
import de.willuhn.jameica.ca.store.template.Attribute;
import de.willuhn.jameica.ca.store.template.CodeSignTemplate;


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
        return "test".toCharArray();
      }
    };
    
    Store store = new Store(new File("/tmp/install/store.keystore"),cb);
    
    EntryFactory ef = new EntryFactory(cb);

    Entry ca = ef.read(new File("/tmp/install/ca.crt"),new File("/tmp/install/ca.key"),FORMAT.PEM);
    store.store(ca);
    
    Entry cal = ef.read(new File("/tmp/install/willuhn.ca.licensing.crt"),new File("/tmp/install/willuhn.ca.licensing.key"),FORMAT.PEM);
    store.store(cal);

    
    CodeSignTemplate template = new CodeSignTemplate();
    template.setIssuer(cal);

    List<Attribute> attributes = template.getAttributes();
    attributes.add(new Attribute(Attribute.CN,"willuhn.codesigning"));
    attributes.add(new Attribute(Attribute.O,"willuhn software & services"));
    attributes.add(new Attribute(Attribute.OU,"Development"));
    attributes.add(new Attribute(Attribute.C,"DE"));
    attributes.add(new Attribute(Attribute.L,"Leipzig"));
    attributes.add(new Attribute(Attribute.ST,"Saxony"));
    Entry e = ef.create(template,null);
    store.store(e);
  }
}


/**********************************************************************
 * $Log: Test.java,v $
 * Revision 1.6  2009/10/07 12:24:04  willuhn
 * @N Erster GUI-Code
 *
 * Revision 1.5  2009/10/07 11:39:27  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2009/10/06 16:47:58  willuhn
 * @N Aussteller fehlte
 *
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
