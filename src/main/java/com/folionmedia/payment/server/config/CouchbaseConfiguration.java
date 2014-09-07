package com.folionmedia.payment.server.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.util.StringUtils;

@Configuration
@EnableCouchbaseRepositories(basePackages="com.folionmedia.payment.server.dao")
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
	
	@Value("${couchbase.servers}")
	private String servers;
	
	@Value("${couchbase.bucketName}")
	private String bucketName;
	
	@Value("${couchbase.bucketPassword}")
	private String bucketPassword;
	
	
    @Override
    protected List<String> bootstrapHosts() {
    	String[] serverArray = StringUtils.tokenizeToStringArray(servers, ",");
        return Arrays.asList(serverArray);
    }

    @Override
    protected String getBucketName() {
        return bucketName;
    }

    @Override
    protected String getBucketPassword() {
        return bucketPassword;
    }
}