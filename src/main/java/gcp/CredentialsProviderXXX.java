package gcp;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.Sets;

public class CredentialsProviderXXX implements CredentialsProvider {
  @Override public Credentials getCredentials() throws IOException {
    //return null;
    /*return ServiceAccountCredentials.newBuilder()
        .setProjectId(googlePlayConfig.getProjectId())
        .setPrivateKeyId(googlePlayConfig.getPrivateKeyId())
        .setPrivateKeyString(googlePlayConfig.getPrivateKeyString())
        .setClientEmail(googlePlayConfig.getClientEmail())
        .setClientId(googlePlayConfig.getClientId())
        .build();*/



    GoogleCredentials gcredentials = ServiceAccountCredentials.newBuilder()
        .setProjectId("pc-api-7728122231740597231-932")
        .setPrivateKeyId("77458a098ff40ea038f76cb66640598ade59c4e8")
        .setPrivateKey(privateKeyFromPkcs8(
            "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDMhOdFDcS6NSFL"
                + "\ndjjeq3p2uIEfQ0pl64jLmIdUNI5WyLeyiNVSz6pe3ltPQ+azKP4ShLjtoOuD5qrP\n1ZlLzAQQ/Ccf3/S69rhPfWAcRjHoLy7rh3OcKxn/aJlD+aynPxBHC1dgiMuPBAN4\nZ6K4WcqThxhM3t/NgB1g6x3sqOpf3Nc/lCR/tPqtMZ91sZYEPCRYqIeoyHFxjjO4\nwL3wrvAoQHByTNfDy7K8txGd86RMjRG9V0idFDFHadh8XxErXT9GsYKNJnKvGY3j\nwG7kaVNisTGeQoj9NCyYEGOSTZPOx4IkrJ4XDxDyl/5GPsiHXTEAm1ptxez4uPTU\nyEt9LnKVAgMBAAECggEAYk5B0h3voWe3ZMvjQxjeySEfo7TXFo10GBTte6q1fn6F\nWfJOw7rhFC/CaSPkyNpb4e/c6g/vFNwz36m2wUnD7JwffEFMo+/CoSbNI1oUVBwV\n7A2UIw1OtkeDYDqrZEq6WsPXUKYAXnKZ7m62NvGmH2qQ4iZBIkjCFlt9a0u4OLIU\n4uCQCsJd4mKPS+fKF71GZokW5Zz2VSpkM0LshSuvw0LNT/13dz/JbX6/YmWYM7ni\nrBT81xepMtkdmybdQA7jS4b1vQHAl5QtFET5Dsf/mwf+f9zpTqO0Or5t6wdTvgWK\nq0hJFJforvZYm10sCJsZ3vBI+NWwcBpG+9uS7SyMMwKBgQDnMDU3igx+Oes+9ogy\nF7Zz/QbIxMpfFNwIqVQnsJkCySnBPtUVvdjd1mYZgukrHUTBNc4wGhbxoQuIcT8h\nQcaFaCRnI52v8UE9pNQOFtK/n4aSP7S3bOlBRtX59UksRWKwExK6F21I0qbNMdbn\nLJPduSlPS/Sgrqw01QvC8Xx3DwKBgQDid/kDwp2gubJGv3VoEoLV1+l1mWdU+IBX\nT/kEK3/SRx1X8YKULenBwVOfnCDFnvDzTjFl8PVWznmETrfMt3/WtATBwohgwaqW\nfyGUjy8AJYSQOwyyoiaYKY2GqHxkNcfxdYvkx5psx1/LTO2VfqG6iQm9RSvICN5i\nC9lDPXPcGwKBgDaVIitfs31lzU3LRPKWuKP/3EcwdxslwKFm4RCR2D5ZIjuWyxZc\nAUS25tolF0nvmhkHzAt9TWd0/1USb7Y5YlaC9P1I4mT0z9NvBAXji5RkZJBI7DDW\nTOf1gnWGXOVP6HHrvuMivtGPsoQFvVhNJQgXPFKVe0colk3O8vzuqNUvAoGAQwju\nLb3CuNueuLBdiWPJVJEnqsawvoccPjwO4rwZht3EAs8bLs90XQTjwuANF1IX0n03\nxAwSIxLVB2Phpfg/IAeDTPNPOQy6ePfCsd28NNfmKGdCN/IlaG8MVGq0/1ioCYIY\n2ax3unE9msj3noOwTa3Ysyyxa/GsGeuV8QBUtjcCgYBQ93Xujh/zfsagWE2irQTs\nT9TqGUrUL9EVcFtIvrzjKcsVdwzJ4nX7xa9ZQaNEnQqQ/XplW3YelRcp/W+J6idc\nK0bNQrx6LSeTVPkwTA1dY47FBNi+AHrh8ZDVjz16ri9fCyEC3cm7LRjNd2zGndBf\nZ1/zjQJSwfqa5zrqJQGpZg==\n-----END PRIVATE KEY-----\n"))
        .setClientEmail("yexin-yinxiang@pc-api-7728122231740597231-932.iam.gserviceaccount.com")
        .setClientId("112777192638136913813").build();
    //gcredentials.createScoped(Sets.newHashSet(AndroidPublisherScopes.ANDROIDPUBLISHER));

    return gcredentials;
  }

  static PrivateKey privateKeyFromPkcs8(String privateKeyPkcs8) throws IOException {
    Reader reader = new StringReader(privateKeyPkcs8);
    PemReader.Section section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY");
    if (section == null) {
      throw new IOException("Invalid PKCS#8 data.");
    } else {
      byte[] bytes = section.getBase64DecodedBytes();
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

      try {
        KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
        return keyFactory.generatePrivate(keySpec);
      } catch (InvalidKeySpecException | NoSuchAlgorithmException var7) {
        throw new IOException("Unexpected exception reading PKCS#8 data", var7);
      }
    }
  }
}
