package com.revature.caliber.data;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.caliber.beans.Batch;
import com.revature.caliber.beans.Trainee;
import com.revature.caliber.exceptions.ServiceNotAvailableException;
import com.revature.salesforce.beans.SalesforceBatchResponse;
import com.revature.salesforce.beans.SalesforceTraineeResponse;

@Repository
public class SalesforceDAO {

	private static final Logger log = Logger.getLogger(SalesforceDAO.class);
	
	@Value("#{systemEnvironment['SALESFORCE_INSTANCE_URL']}")
	private String salesforceInstanceUrl;
	@Value("/services/data/v39.0/query/")
	private String salesforceApiUrl;
	
	//////////// SOQL - Salesforce Object Query Language //////////////
	
	/**
	 * Will change as of version 2.0 Salesforce API in August/September 2017 timeframe
	 * Used to populate the dropdown list of importable batches.
		select id, name, batch_start_date__c, batch_end_date__c,
			batch_trainer__r.name, Co_Trainer__r.name, Skill_Type__c,
			Type__c from training__c where batch_start_date__c >= THIS_YEAR
	 */
	@Value("select id, name, batch_start_date__c, batch_end_date__c, batch_trainer__r.name, Co_Trainer__r.name, Skill_Type__c, Type__c from training__c where batch_start_date__c >= THIS_YEAR")
	private String relevantBatches;
	
	/**
	 * Will change as of version 2.0 Salesforce API in August/September 2017 timeframe
	 * Once user selects a batch to import, use this to load all the Trainee details.
	 	select id, name, training_status__c, phone, email, MobilePhone,
			Training_Batch__c , Training_Batch__r.name, 
			Training_Batch__r.batch_start_date__c, 
			Training_Batch__r.batch_end_date__c, 
			Training_Batch__r.batch_trainer__r.name, 
			rnm__Recruiter__r.name, account.name, 
			Training_Batch__r.Co_Trainer__r.name, 
			eintern_current_project_completion_pct__c ,
			Training_Batch__r.Skill_Type__c, 
			Training_Batch__r.Type__c from Contact 
			where training_batch__c = 'a0Yi000000F0b7I'
			
			// 'a0Yi000000F0b7I' is the resourceId
		ResourceId *MUST* be surrounded in single quotes to function properly
	 */
	@Value("select id, name, training_status__c, phone, email, MobilePhone, Training_Batch__c , Training_Batch__r.name, Training_Batch__r.batch_start_date__c, Training_Batch__r.batch_end_date__c, Training_Batch__r.batch_trainer__r.name, rnm__Recruiter__r.name, account.name, Training_Batch__r.Co_Trainer__r.name, eintern_current_project_completion_pct__c , Training_Batch__r.Skill_Type__c, Training_Batch__r.Type__c from Contact where training_batch__c = ")
	private String batchDetails;
	
	@Value("select id, name from Training__c")
	private String allBatches;
	
	// TODO test sample Batch query
	public void getAllBatches() {
		try {
			HttpResponse queryResponse = getFromSalesforce(batchDetails + "'a0Yi000000F0b7I'");
			// convert to your salesforce beans
			SalesforceTraineeResponse queryResults = new ObjectMapper().readValue(queryResponse.getEntity().getContent(), SalesforceTraineeResponse.class);
			log.info(queryResults);
			log.info(queryResults.getRecords()[0].getEmail());
			
			// example 2 using q=relevantBatches 
/*			SalesforceBatchResponse queryResults2 = new ObjectMapper().readValue(queryResponse.getEntity().getContent(), SalesforceBatchResponse.class);
			log.info(queryResults2);
			log.info(queryResults2.getRecords()[0].getTrainer().getName());*/	
		} catch (IOException e) {
			log.error("Unable to fetch Salesforce data: cause " + e.getClass() + " " + e.getMessage());
		}
	}
	
	/**
	 * TODO implement
	 * Get the batches in the current year and future years.
	 * Access data using the Salesforce REST API
	 * @return
	 */
	public List<Batch> getAllRelevantBatches(){
		try {
			SalesforceBatchResponse response = new ObjectMapper().readValue(getFromSalesforce(relevantBatches).getEntity().getContent(), SalesforceBatchResponse.class);
			// convert to Caliber beans
			return null; // TODO return something of value
		} catch (IOException e) {
			log.error("Unable to fetch Salesforce data: cause " + e.getClass() + " " + e.getMessage());
			throw new ServiceNotAvailableException();
		}
	}
	
	/**
	 * TODO implement
	 * Get all the trainees for a single batch.
	 * Access data using the Salesforce REST API
	 * @return
	 */
	public List<Trainee> getBatchDetails(String resourceId){
		String query = batchDetails + "' " + resourceId + " + '";
		try {
			SalesforceTraineeResponse response = new ObjectMapper().readValue(getFromSalesforce(query).getEntity().getContent(), SalesforceTraineeResponse.class);
			// convert to Caliber bean
			return null; // TODO return something of value
		} catch (IOException e) {
			log.error("Unable to fetch Salesforce data: cause " + e.getClass() + " " + e.getMessage());
			throw new ServiceNotAvailableException();
		}
	}
	
	private HttpResponse getFromSalesforce(String soql){
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = new URIBuilder(salesforceInstanceUrl).setScheme("https").setHost(salesforceInstanceUrl)
					.setPath(salesforceApiUrl).setParameter("q", soql).build().toString();
			HttpGet getRequest = new HttpGet(url);
			getRequest.setHeader("Authorization", "Bearer " + getAccessToken());
			HttpResponse queryResponse = httpClient.execute(getRequest);
			return queryResponse;
		} catch (IOException | URISyntaxException e) {
			log.error("Unable to fetch Salesforce data: cause " + e.getClass() + " " + e.getMessage());
			throw new ServiceNotAvailableException();
		}
	}

	private String getAccessToken() {
		return "00D0n0000000Q1l!AQQAQF8kUz6QVhBC8_zSVi4k8mjZeKbwe3fUJzgAKcFWLyGBMEWdsaeRJOcS90VaNTwYHdyhJ27F4kJlSZhL4pYlqk6XNk4J";
		//return ((SalesforceUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSalesforceToken().getAccessToken();
	}
}
