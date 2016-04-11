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

import de.willuhn.util.ApplicationException;

/**
 * Vorkonfiguriertes Template fuer Code-Signierung.
 */
public class CodeSignTemplate extends Template
{
  /**
   * Erstellt ein neues Template fuer Code-Signierung.
   * @throws ApplicationException
   */
  public CodeSignTemplate() throws ApplicationException
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
                                   new KeyUsage(KeyUsage.digitalSignature).getEncoded()));

      // Code-Signing
      ASN1EncodableVector purposes = new ASN1EncodableVector();
      purposes.add(KeyPurposeId.id_kp_codeSigning);
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
   * @see de.willuhn.jameica.ca.store.template.Template#getName()
   */
  public String getName()
  {
    return "Code-Signierung";
  }
}
