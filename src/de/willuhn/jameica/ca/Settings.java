/**********************************************************************
 *
 * Copyright (c) 2017 Olaf Willuhn
 * GNU GPLv2
 *
 **********************************************************************/

package de.willuhn.jameica.ca;

import de.willuhn.jameica.system.Application;

/**
 * Einstellungen fuer das Plugin.
 */
public class Settings
{
  private final static de.willuhn.jameica.system.Settings settings = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
  
  /**
   * Liefert true, wenn nur tatsaechliche CA-Zertifikate als solche benutzt werden duerfen.
   * @return true, wenn nur tatsaechliche CA-Zertifikate als solche benutzt werden duerfen.
   */
  public static boolean isCheckCA()
  {
    return settings.getBoolean("checkca",true);
  }

}


