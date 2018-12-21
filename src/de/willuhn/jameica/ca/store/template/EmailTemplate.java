/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.ca.store.template;

import java.io.IOException;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import de.willuhn.jameica.ca.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Vorkonfiguriertes Template fuer ein Mail-Zertifikat.
 */
public class EmailTemplate extends Template
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * Erstellt ein neues Template fuer ein Mail-Zertifikat.
   * @throws ApplicationException
   */
  public EmailTemplate() throws ApplicationException
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

      // Client-Authentifizierung und Email-Protection.
      ASN1EncodableVector purposes = new ASN1EncodableVector();
      purposes.add(KeyPurposeId.id_kp_emailProtection);
      purposes.add(KeyPurposeId.id_kp_clientAuth);
      extensions.add(new Extension(org.bouncycastle.asn1.x509.Extension.extendedKeyUsage.getId(),
                                   false,
                                   new DERSequence(purposes).getEncoded()));
    }
    catch (IOException e)
    {
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Template: {0}",e.getMessage()), e);
    }
  }
  
  /**
   * Ueberschrieben um SubjectAltName und Email noch hinzuzufuegen.
   * @see de.willuhn.jameica.ca.store.template.Template#prepare()
   */
  @Override
  public void prepare()
  {
    super.prepare();
    for (Attribute a:this.getAttributes())
    {
      String oid = a.getOid();
      String value = a.getValue();
      
      if (value == null || value.length() == 0 || oid == null || oid.length() == 0)
        continue;
      
      if (oid.equals(Attribute.CN))
      {
        // Mail-Adresse automatisch hinzufuegen
        SubjectAltName an = new SubjectAltName();
        an.setType(SubjectAltNameType.MAIL);
        an.setValue(value);
        this.getAltNames().add(an);

        // Ausserdem noch das Attribut fuer die Mail-Adresse
        this.getAttributes().add(new Attribute(Attribute.EmailAddress,value));
        
        break;
      }
    }
  }
  
  /**
   * @see de.willuhn.jameica.ca.store.template.Template#getName()
   */
  public String getName()
  {
    return "S/MIME-Zertifikat (E-Mail)";
  }
}
