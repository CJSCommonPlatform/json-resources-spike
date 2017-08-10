package uk.gov.justice.api;

import java.lang.Override;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.justice.services.adapter.messaging.JmsLoggerMetadataInterceptor;
import uk.gov.justice.services.adapter.messaging.JmsProcessor;
import uk.gov.justice.services.adapter.messaging.JsonSchemaValidationInterceptor;
import uk.gov.justice.services.core.annotation.Adapter;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.messaging.logging.LoggerUtils;

@Adapter("EVENT_PROCESSOR")
@MessageDriven(
    activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "stagingtfl.event"),
        @ActivationConfigProperty(propertyName = "shareSubscriptions", propertyValue = "true"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "CPPNAME in('stagingtfl.events.charged-cases-csv-files-upload-accepted')"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "stagingtfl.event.processor"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "stagingtfl.event.processor.stagingtfl.event")
    }
)
@Interceptors({
    JmsLoggerMetadataInterceptor.class,
    JsonSchemaValidationInterceptor.class
})
public class StagingtflEventProcessorStagingtflEventJmsListener implements MessageListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(StagingtflEventProcessorStagingtflEventJmsListener.class);

  @Inject
  InterceptorChainProcessor interceptorChainProcessor;

  @Inject
  JmsProcessor jmsProcessor;

  @Override
  public void onMessage(Message message) {
    LoggerUtils.trace(LOGGER, () -> "Received JMS message");
    jmsProcessor.process(interceptorChainProcessor::process, message);
  }
}
