/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Template fuer das Zertifikat eines Webservers.
 */
public class WebserverTemplate extends Template
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * Erstellt ein neues Template fuer ein Webserver-Zertifikat.
   * @throws ApplicationException
   */
  public WebserverTemplate() throws ApplicationException
  {
    try
    {
      List<Extension> extensions = this.getExtensions();

      extensions.add(new Extension(org.bouncycastle.asn1.x509.Extension.basicConstraints.getId(),
                                   true,
                                   new BasicConstraints(false).getEncoded()));

      // Key-Usage
      extensions.add(new Extension(org.bouncycastle.asn1.x509.Extension.keyUsage.getId(),
                                   true,
                                   new KeyUsage(KeyUsage.digitalSignature |
                                                KeyUsage.keyEncipherment |
                                                KeyUsage.nonRepudiation |
                                                KeyUsage.dataEncipherment).getEncoded()));

      // Server-Zertifikat
      ASN1EncodableVector purposes = new ASN1EncodableVector();
      purposes.add(KeyPurposeId.id_kp_serverAuth);
      extensions.add(new Extension(org.bouncycastle.asn1.x509.Extension.extendedKeyUsage.getId(),
                                   false,
                                   new DERSequence(purposes).getEncoded()));

      // Netscape-Extension
      extensions.add(new Extension(MiscObjectIdentifiers.netscapeCertType.getId(),
                                   false,
                                   new NetscapeCertType(NetscapeCertType.sslServer).getEncoded()));
    }
    catch (IOException e)
    {
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Template: {0}",e.getMessage()), e);
    }
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.template.Template#getExtensions()
   */
  @Override
  public List<Extension> getExtensions()
  {
    List<Extension> list = new ArrayList<Extension>(super.getExtensions());
    
    try
    {
      // subjectAltName noch hinzufuegen
      for (Attribute a:this.getAttributes())
      {
        String oid = a.getOid();
        String value = a.getValue();
        
        if (value == null || value.length() == 0 || oid == null || oid.length() == 0)
          continue;
        
        if (oid.equals(Attribute.CN))
        {
          GeneralNames subjectAltName = new GeneralNames(new GeneralName(GeneralName.dNSName,value));
          list.add(new Extension(org.bouncycastle.asn1.x509.Extension.subjectAlternativeName.getId(),false,subjectAltName.getEncoded()));
        }
      }
    }
    catch (Exception e)
    {
      Logger.error("unable to add subjectAltName",e);
    }

    return list;
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.template.Template#getName()
   */
  public String getName()
  {
    return "Webserver-Zertifikat";
  }
}
