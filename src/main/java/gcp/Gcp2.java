package gcp;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.util.concurrent.ListenableFuture;

import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.DefaultPublisherFactory;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

public class Gcp2 {
  public static class Test {
    public static void main(String[] args){
      DefaultPublisherFactory defaultPublisherFactory = new DefaultPublisherFactory(new DefaultGcpProjectIdProvider());
      DefaultSubscriberFactory defaultSubscriberFactory = new DefaultSubscriberFactory(new
          DefaultGcpProjectIdProvider());
      PubSubTemplate template = new PubSubTemplate(defaultPublisherFactory,defaultSubscriberFactory);

      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().putAttributes("aaaK","account-change-notify")
          .setData(ByteString.copyFrom("account-change-notify33".getBytes())).build();
      //projects/pc-api-7728122231740597231-932/topics/
      ListenableFuture<String> listenableFuture =  template.publish("account-change-notify",pubsubMessage);
      CompletableFuture<String> s =  listenableFuture.completable();
      try {
        String b =  s.get();
        b=null;
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
      }
  }
}
