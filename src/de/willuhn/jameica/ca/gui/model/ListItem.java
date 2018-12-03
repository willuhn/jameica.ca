/**********************************************************************
 * $Source: /cvsroot/jameica/jameica.ca/src/de/willuhn/jameica/ca/gui/model/ListItem.java,v $
 * $Revision: 1.3 $
 * $Date: 2010/06/14 08:32:18 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.gui.model;

import java.rmi.RemoteException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.GenericObject;
import de.willuhn.datasource.GenericObjectNode;
import de.willuhn.datasource.pseudo.PseudoIterator;
import de.willuhn.jameica.ca.store.Entry;
import de.willuhn.jameica.security.Certificate;
import de.willuhn.jameica.security.Principal;

/**
 * Implementiert eine einzelne Zeile.
 */
public class ListItem implements GenericObjectNode, Comparable
{
  private Entry entry = null;
  
  /**
   * ct.
   * @param entry
   */
  public ListItem(Entry entry)
  {
    this.entry = entry;
  }
  
  /**
   * Liefert den Namen, auf den das Zertifikat ausgestellt ist.
   * @return Name, auf den das Zertifikat ausgestellt ist.
   * @throws Exception
   */
  public String getSubject() throws Exception
  {
    X509Certificate x = this.entry.getCertificate();
    Certificate c = new Certificate(x);
    String cn = c.getSubject().getAttribute(Principal.COMMON_NAME);
    return cn == null ? x.getSubjectDN().getName() : cn;
  }

  /**
   * Liefert den Namen der Organisation, auf die das Zertifikat ausgestellt ist.
   * @return Name der Organisation, auf die das Zertifikat ausgestellt ist.
   * @throws Exception
   */
  public String getOrganization() throws Exception
  {
    X509Certificate x = this.entry.getCertificate();
    Certificate c = new Certificate(x);
    return c.getSubject().getAttribute(Principal.ORGANIZATION);
  }

  /**
   * Liefert den Namen des Ausstellers.
   * @return Name des Ausstellers.
   * @throws Exception
   */
  public String getIssuer() throws Exception
  {
    X509Certificate x = this.entry.getCertificate();
    Certificate c = new Certificate(x);
    Principal self = c.getSubject();
    Principal ca = c.getIssuer();
    
    // Keine CA angegeben oder Subject und CA identisch
    if (ca == null || (self != null && ca != null && self.getAttribute(Principal.DISTINGUISHED_NAME).equals(ca.getAttribute(Principal.DISTINGUISHED_NAME))))
      return null;
    
    return ca.getAttribute(Principal.COMMON_NAME);
  }
  
  /**
   * Liefert das Beginn-Datum der Gueltigkeit.
   * @return Beginn-Datum der Gueltigkeit.
   * @throws Exception
   */
  public Date getValidFrom() throws Exception
  {
    return this.entry.getCertificate().getNotBefore();
  }

  /**
   * Liefert das End-Datum der Gueltigkeit.
   * @return End-Datum der Gueltigkeit.
   * @throws Exception
   */
  public Date getValidTo() throws Exception
  {
    return this.entry.getCertificate().getNotAfter();
  }

  /**
   * Liefert true, wenn ein Private-Key vorhanden ist.
   * @return true, wenn ein Private-Key vorhanden ist.
   * @throws Exception
   */
  public boolean havePrivateKey() throws Exception
  {
    return this.entry.getPrivateKey() != null;
  }

  /**
   * Liefert true, wenn es ein CA-Zertifikat ist.
   * @return true, wenn es ein CA-Zertifikat ist.
   * @throws Exception
   */
  public boolean isCA() throws Exception
  {
    return this.entry.isCA();
  }

  /**
   * Liefert das zugehoerige Entry-Objekt.
   * @return das Entry-Objekt.
   */
  public Entry getEntry()
  {
    return this.entry;
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) throws RemoteException
  {
    try
    {
      if ("subject".equals(name))      return this.getSubject();
      if ("organization".equals(name)) return this.getOrganization();
      if ("issuer".equals(name))       return this.getIssuer();
      if ("validFrom".equals(name))    return this.getValidFrom();
      if ("validTo".equals(name))      return this.getValidTo();
      return null;
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to load attribute " + name,e);
    }
  }


  /**
   * @see de.willuhn.datasource.GenericObjectNode#getChildren()
   */
  public GenericIterator getChildren() throws RemoteException
  {
    try
    {
      List<ListItem> list = new ArrayList<ListItem>();
      List<Entry> l = this.entry.getClients();
      for (Entry e:l)
      {
        list.add(new ListItem(e));
      }
      return PseudoIterator.fromArray(list.toArray(new ListItem[list.size()]));
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to get child entries",e);
    }
  }

  /**
   * Liefert das Aussteller-Zertifikat
   * @see de.willuhn.datasource.GenericObjectNode#getParent()
   */
  public GenericObjectNode getParent() throws RemoteException
  {
    try
    {
      Entry issuer = this.entry.getIssuer();
      return issuer == null ? null : new ListItem(issuer);
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to find issuer",e);
    }
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getPath()
   */
  public GenericIterator getPath() throws RemoteException
  {
    throw new RemoteException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#getPossibleParents()
   */
  public GenericIterator getPossibleParents() throws RemoteException
  {
    throw new RemoteException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObjectNode#hasChild(de.willuhn.datasource.GenericObjectNode)
   */
  public boolean hasChild(GenericObjectNode child) throws RemoteException
  {
    GenericIterator children = this.getChildren();
    while (children.hasNext())
    {
      if (children.next().equals(this))
        return true;
    }
    return false;
  }

  /**
   * @see de.willuhn.datasource.GenericObject#equals(de.willuhn.datasource.GenericObject)
   */
  public boolean equals(GenericObject other) throws RemoteException
  {
    if (other == null || !(other instanceof ListItem))
      return false;
    
    ListItem i = (ListItem) other;
    return this.getID().equals(i.getID());
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getAttributeNames()
   */
  public String[] getAttributeNames() throws RemoteException
  {
    throw new RemoteException("not implemented");
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getID()
   */
  public String getID() throws RemoteException
  {
    try
    {
      return this.entry.getAlias() + "." + this.entry.getCertificate().getSerialNumber().toString();
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to determine entry id",e);
    }
  }

  /**
   * @see de.willuhn.datasource.GenericObject#getPrimaryAttribute()
   */
  public String getPrimaryAttribute() throws RemoteException
  {
    return "subject";
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o)
  {
    return this.entry.compareTo(o);
  }
}

/**********************************************************************
 * $Log: ListItem.java,v $
 * Revision 1.3  2010/06/14 08:32:18  willuhn
 * @N Zertifikate alphabetisch sortieren
 *
 * Revision 1.2  2009/10/15 17:04:48  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2009/10/13 00:26:32  willuhn
 * @N Tree-View fuer Zertifikate
 *
 **********************************************************************/