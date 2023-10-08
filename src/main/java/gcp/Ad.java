package gcp;




import com.google.auth.oauth2.ServiceAccountCredentials;

public class Ad {
  //ServiceAccountCredentials
 /* ServiceAccountCredentials.newBuilder()
      .setProjectId(googlePlayConfig.getProjectId())
      .setPrivateKeyId(googlePlayConfig.getPrivateKeyId())
      .setPrivateKeyString(googlePlayConfig.getPrivateKeyString())
      .setClientEmail(googlePlayConfig.getClientEmail())
      .setClientId(googlePlayConfig.getClientId())
      .build();*/
  org.springframework.cloud.gcp.pubsub.core.PubSubTemplate pubSubTemplate;
  com.google.cloud.spring.pubsub.core.PubSubTemplate pubSubTemplate2;
}
