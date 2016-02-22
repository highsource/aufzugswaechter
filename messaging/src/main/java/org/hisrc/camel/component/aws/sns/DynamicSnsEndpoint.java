package org.hisrc.camel.component.aws.sns;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.aws.sns.SnsConfiguration;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SetTopicAttributesRequest;

/**
 * Defines the <a href="http://camel.apache.org/aws.html">AWS SNS Endpoint</a>.  
 */
@UriEndpoint(scheme = "aws-sns-dynamic", title = "AWS Simple Notification System", syntax = "aws-sns-dynamic:defaultTopicName", producerOnly = true, label = "cloud,mobile,messaging")
public class DynamicSnsEndpoint extends DefaultEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicSnsEndpoint.class);

    private AmazonSNS snsClient;

    @UriParam
    private SnsConfiguration configuration;

    @Deprecated
    public DynamicSnsEndpoint(String uri, CamelContext context, SnsConfiguration configuration) {
        super(uri, context);
        this.configuration = configuration;
    }
    public DynamicSnsEndpoint(String uri, Component component, SnsConfiguration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("You cannot receive messages from this endpoint");
    }

    public Producer createProducer() throws Exception {
        return new DynamicSnsProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }

    @Override
    public void doStart() throws Exception {
        super.doStart();
        snsClient = configuration.getAmazonSNSClient() != null
            ? configuration.getAmazonSNSClient() : createSNSClient();
        
        // Override the setting Endpoint from url
        if (ObjectHelper.isNotEmpty(configuration.getAmazonSNSEndpoint())) {
            LOG.trace("Updating the SNS region with : {} " + configuration.getAmazonSNSEndpoint());
            snsClient.setEndpoint(configuration.getAmazonSNSEndpoint());
        }
    }

    public SnsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(SnsConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public void setSNSClient(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }
    
    public AmazonSNS getSNSClient() {
        return snsClient;
    }

    /**
     * Provide the possibility to override this method for an mock implementation
     *
     * @return AmazonSNSClient
     */
    AmazonSNS createSNSClient() {
        AmazonSNS client = null;
        AWSCredentials credentials = new BasicAWSCredentials(configuration.getAccessKey(), configuration.getSecretKey());
        if (ObjectHelper.isNotEmpty(configuration.getProxyHost()) && ObjectHelper.isNotEmpty(configuration.getProxyPort())) {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setProxyHost(configuration.getProxyHost());
            clientConfiguration.setProxyPort(configuration.getProxyPort());
            client = new AmazonSNSClient(credentials, clientConfiguration);
        } else {
            client = new AmazonSNSClient(credentials);
        }
        return client;
    }
}