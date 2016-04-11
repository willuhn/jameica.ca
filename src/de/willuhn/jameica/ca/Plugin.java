/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Basis-Klasse des Plugins.
 */
public class Plugin extends AbstractPlugin
{
  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  @Override
  public void init() throws ApplicationException
  {
    Provider prov = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    
    // Version checken.
    if (prov.getVersion() < 1.54d)
    {
      I18N i18n = getResources().getI18N();
      throw new ApplicationException(i18n.tr("BouncyCastle-Version zu alt. Bitte aktualisieren Sie Jameica"));
    }
    
    super.init();
  }
}
