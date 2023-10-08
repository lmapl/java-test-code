package gcp;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.util.concurrent.ListenableFuture;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.cloud.spring.core.DefaultCredentialsProvider;
import com.google.cloud.spring.core.DefaultGcpProjectIdProvider;
import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.DefaultPublisherFactory;
import com.google.cloud.spring.pubsub.support.DefaultSubscriberFactory;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.StreamingPullRequest;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.Topic;

public class Gcp {
  public static class Test {
    public static void main(String[] args){
      StreamingPullRequest request;
      ProjectSubscriptionName subscriptionName =
          ProjectSubscriptionName.of("pc-api-7728122231740597231-932", "gcptopictestSub1");

      MessageReceiver receiver =
          (PubsubMessage message, AckReplyConsumer consumer) -> {
            // Handle incoming message, then ack the received message.
            System.out.println("Id: " + message.getMessageId());
            System.out.println("Data: " + message.getData().toStringUtf8());
            // Print message attributes.
            message
                .getAttributesMap()
                .forEach((key, value) -> System.out.println(key + " = " + value));
            consumer.ack();
          };

      Subscriber subscriber = null;
      try {
        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        // Start the subscriber.
        subscriber.startAsync().awaitRunning();
        System.out.printf("Listening for messages on %s:\n", subscriptionName.toString());
        // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
        subscriber.awaitTerminated(300, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
        // Shut down the subscriber after 30s. Stop receiving messages.
        subscriber.stopAsync();
      }


    }
  }

  private void fo(){
    PubSubAdmin pubSubAdmin = new PubSubAdmin(new DefaultGcpProjectIdProvider(),new CredentialsProviderXXX());
    //创建Subscription
    Subscription a = pubSubAdmin.createSubscription("gcptopictestSub1","gcptopictest");
    a = null;
    //创建topic
    //Topic topic  = pubSubAdmin.createTopic("gcptopictest");
    // topic = null;
    //System.setProperties("http.proxy"， "");
  }
}
