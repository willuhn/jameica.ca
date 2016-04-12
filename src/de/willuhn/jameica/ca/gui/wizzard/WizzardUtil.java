/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.wizzard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;

/**
 * Hilfsklasse mit statischen Methoden fuer die Wizzards.
 */
public class WizzardUtil
{
  /**
   * Liefert eine Liste der moeglichen Assistenten zur Erstellung neuer Zertifikate.
   * @return Liste der moeglichen Asstistenten.
   */
  public final static List<CertificateWizzard> getWizzards()
  {
    List<CertificateWizzard> list = new ArrayList<CertificateWizzard>();
      
    try
    {
      ClassFinder finder = Application.getPluginLoader().getManifest(Plugin.class).getClassLoader().getClassFinder();
      Class<CertificateWizzard>[] classes = finder.findImplementors(CertificateWizzard.class);
      for (Class<CertificateWizzard> c:classes)
      {
        try
        {
          list.add(c.newInstance());
        }
        catch (Exception e)
        {
          Logger.error("error while loading wizzard " + c.getName() + ", skipping",e);
        }
      }
      
      // Jetzt sortieren wir die Liste noch
      Collections.sort(list);
    }
    catch (ClassNotFoundException ce)
    {
      Logger.error("no implementors found for interface " + CertificateWizzard.class.getName(),ce);
    }
    return list;
  }
}
