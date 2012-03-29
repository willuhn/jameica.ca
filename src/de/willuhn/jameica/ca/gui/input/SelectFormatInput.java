/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/input/SelectFormatInput.java,v $
 * $Revision: 1.5 $
 * $Date: 2012/03/29 21:14:17 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.input;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.store.format.Format;
import de.willuhn.jameica.gui.input.SelectInput;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ClassFinder;
import de.willuhn.util.I18N;

/**
 * Vorkonfigurierte Selectbox mit der Auswahl des Schluesselformats.
 */
public class SelectFormatInput extends SelectInput
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * ct.
   */
  public SelectFormatInput()
  {
    super(init(), null);
    this.setAttribute("name");
    this.setName(i18n.tr("Schlüsselformat"));
    this.setPleaseChoose(i18n.tr("Bitte wählen..."));
  }
  
  /**
   * Initialisiert die Liste der moeglichen Schluesselformate.
   * @return Liste der moeglichen Schluesselformate.
   */
  private static List<Format> init()
  {
    List<Format> list = new ArrayList<Format>();
    try
    {
      ClassFinder finder = Application.getPluginLoader().getManifest(Plugin.class).getClassLoader().getClassFinder();
      Class<Format>[] classes = finder.findImplementors(Format.class);
      for (Class<Format> c:classes)
      {
        try
        {
          list.add(c.newInstance());
        }
        catch (Exception e)
        {
          Logger.error("unable to load key format " + c + ", skipping",e);
        }
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to load key format list",e);
    }
    return list;
  }
}


/**********************************************************************
 * $Log: SelectFormatInput.java,v $
 * Revision 1.5  2012/03/29 21:14:17  willuhn
 * @C Kompatibilitaet zu Jameica 2.2 leider doch nicht moeglich
 *
 * Revision 1.4  2012/03/29 20:57:15  willuhn
 * @C Kompatibilitaet zu Jameica 2.2 wieder hergestellt
 *
 * Revision 1.3  2012/03/28 22:28:11  willuhn
 * @N Einfuehrung eines neuen Interfaces "Plugin", welches von "AbstractPlugin" implementiert wird. Es dient dazu, kuenftig auch Jameica-Plugins zu unterstuetzen, die selbst gar keinen eigenen Java-Code mitbringen sondern nur ein Manifest ("plugin.xml") und z.Bsp. Jars oder JS-Dateien. Plugin-Autoren muessen lediglich darauf achten, dass die Jameica-Funktionen, die bisher ein Object vom Typ "AbstractPlugin" zuruecklieferten, jetzt eines vom Typ "Plugin" liefern.
 * @C "getClassloader()" verschoben von "plugin.getRessources().getClassloader()" zu "manifest.getClassloader()" - der Zugriffsweg ist kuerzer. Die alte Variante existiert weiterhin, ist jedoch als deprecated markiert.
 *
 * Revision 1.2  2009/10/15 23:14:18  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/07 16:38:59  willuhn
 * @N GUI-Code zum Anzeigen und Importieren von Schluesseln
 *
 **********************************************************************/
