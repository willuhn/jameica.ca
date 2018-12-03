/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package de.willuhn.jameica.ca.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.ca.gui.action.EntryView;
import de.willuhn.jameica.ca.gui.model.EntryListModel;
import de.willuhn.jameica.ca.gui.model.ListItem;
import de.willuhn.jameica.gui.calendar.AbstractAppointment;
import de.willuhn.jameica.gui.calendar.Appointment;
import de.willuhn.jameica.gui.calendar.AppointmentProvider;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Appointment-Provider, der die Ablaufdaten der Zertifikate als Termine exportiert.
 */
public class CertificateExpiryAppointmentProvider implements AppointmentProvider
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
  
  /**
   * @see de.willuhn.jameica.gui.calendar.AppointmentProvider#getName()
   */
  @Override
  public String getName()
  {
    return i18n.tr("Ablauftermine der Zertifikate");
  }

  /**
   * @see de.willuhn.jameica.gui.calendar.AppointmentProvider#getAppointments(java.util.Date, java.util.Date)
   */
  @Override
  public List<Appointment> getAppointments(Date from, Date to)
  {
    
    List<Appointment> result = new ArrayList<Appointment>();
    
    try
    {
      List<ListItem> items = new EntryListModel().getItems();
      for (ListItem item:items)
      {
        Date expiry = item.getValidTo();
        if (expiry == null)
          continue;
        
        if (from != null && expiry.before(from))
          continue;
        
        if (to != null && expiry.after(to))
          continue;

        result.add(new ExpiryAppointment(item));
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to load certificates",e);
    }
    
    return result;
  }
  
  /**
   * Kapselt die Daten des Termins.
   */
  private class ExpiryAppointment extends AbstractAppointment
  {
    private ListItem item = null;
    
    /**
     * ct.
     * @param i das Item.
     */
    private ExpiryAppointment(ListItem i)
    {
      this.item = i;
    }

    /**
     * @see de.willuhn.jameica.gui.calendar.Appointment#getDate()
     */
    @Override
    public Date getDate()
    {
      try
      {
        return this.item.getValidTo();
      }
      catch (Exception e)
      {
        Logger.error("unable to determine expiry date",e);
      }
      return null;
    }

    /**
     * @see de.willuhn.jameica.gui.calendar.Appointment#getName()
     */
    @Override
    public String getName()
    {
      try
      {
        return i18n.tr("Zertifikat läuft ab: {0}",this.item.getSubject());
      }
      catch (Exception e)
      {
        Logger.error("unable to determine certificate subject",e);
      }
      
      return null;
    }
    
    /**
     * @see de.willuhn.jameica.gui.calendar.AbstractAppointment#getDescription()
     */
    @Override
    public String getDescription()
    {
      try
      {
        String issuer = StringUtils.trimToNull(this.item.getIssuer());
        if (issuer != null)
        {
          return i18n.tr("Ausgestellt für: {0}\n" +
                         "Ausgestellt von: {1}\n" + 
                         "Ablaufdatum: {2}",this.item.getSubject(),issuer,DateUtil.DEFAULT_FORMAT.format(this.item.getValidTo()));
        }
        else
        {
          return i18n.tr("Ausgestellt für: {0}\n" +
                         "Ablaufdatum: {1}",this.item.getSubject(),DateUtil.DEFAULT_FORMAT.format(this.item.getValidTo()));
        }
      }
      catch (Exception e)
      {
        Logger.error("unable to determine certificate subject",e);
      }
      
      return null;
    }
    
    /**
     * @see de.willuhn.jameica.gui.calendar.AbstractAppointment#execute()
     */
    @Override
    public void execute() throws ApplicationException
    {
      new EntryView().handleAction(this.item);
    }
    
    /**
     * @see de.willuhn.jameica.gui.calendar.AbstractAppointment#hasAlarm()
     */
    @Override
    public boolean hasAlarm()
    {
      return true;
    }
  }

}
