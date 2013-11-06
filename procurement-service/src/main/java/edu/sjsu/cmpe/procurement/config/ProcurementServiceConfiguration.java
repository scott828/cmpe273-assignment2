package edu.sjsu.cmpe.procurement.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class ProcurementServiceConfiguration extends Configuration {
	    @NotEmpty
	    @JsonProperty
	    private String stompQueueName;

	    @NotEmpty
	    @JsonProperty
	    private String stompTopicPrefix;
	    
	    @NotEmpty
	    @JsonProperty
	    private String apolloUser;

	    @NotEmpty
	    @JsonProperty
	    private String apolloPassword;
	    
	    @NotEmpty
	    @JsonProperty
	    private String apolloHost;

	    @NotNull
	    @JsonProperty
	    private int apolloPort;
	    
	    @Valid
	    @NotNull
	    @JsonProperty
	    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

	    public JerseyClientConfiguration getJerseyClientConfiguration() {
	        return httpClient;
	    }

	    public String getApolloUser() {
			return apolloUser;
		}

		public void setApolloUser(String apolloUser) {
			this.apolloUser = apolloUser;
		}

		public String getApolloPassword() {
			return apolloPassword;
		}

		public void setApolloPassword(String apolloPassword) {
			this.apolloPassword = apolloPassword;
		}

		public String getApolloHost() {
			return apolloHost;
		}

		public void setApolloHost(String apolloHost) {
			this.apolloHost = apolloHost;
		}

		public int getApolloPort() {
			return apolloPort;
		}

		public void setApolloPort(int apolloPort) {
			this.apolloPort = apolloPort;
		}

		/**
	     * @return the stompQueueName
	     */
	    public String getStompQueueName() {
		return stompQueueName;
	    }

	    /**
	     * @param stompQueueName
	     *            the stompQueueName to set
	     */
	    public void setStompQueueName(String stompQueueName) {
		this.stompQueueName = stompQueueName;
	    }

    public String getStompTopicPrefix() {
	return stompTopicPrefix;
    }

    public void setStompTopicPrefix(String stompTopicPrefix) {
	this.stompTopicPrefix = stompTopicPrefix;
    }

}
