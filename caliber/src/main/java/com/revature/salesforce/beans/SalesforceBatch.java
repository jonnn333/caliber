package com.revature.salesforce.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Salesforce Data Transfer Object
 * (current as of 7/3/2017) 
 * @author Patrick Walsh
 *
 */
public class SalesforceBatch {

	@JsonProperty("attributes")
	private Attributes attributes;
	
	@JsonProperty("Id")
	private String id;
	
	@JsonProperty("Name")
	private String name;
	
	@JsonProperty("Batch_Start_Date__c")
	private Date batchStartDate;
	
	@JsonProperty("Batch_End_Date__c")
	private Date batchEndDate;
	
	@JsonProperty("Batch_Trainer__r")
	private BatchTrainer trainer;
	
	@JsonProperty("Co_Trainer__r")
	private BatchTrainer cotrainer;
	
	@JsonProperty("Skill_Type__c")
	private String skillType;
	
	@JsonProperty("Type__c")
	private String type;
	
	public SalesforceBatch() {
		super();
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBatchStartDate() {
		return batchStartDate;
	}

	public void setBatchStartDate(Date batchStartDate) {
		this.batchStartDate = batchStartDate;
	}

	public Date getBatchEndDate() {
		return batchEndDate;
	}

	public void setBatchEndDate(Date batchEndDate) {
		this.batchEndDate = batchEndDate;
	}

	public BatchTrainer getTrainer() {
		return trainer;
	}

	public void setTrainer(BatchTrainer trainer) {
		this.trainer = trainer;
	}

	public BatchTrainer getCotrainer() {
		return cotrainer;
	}

	public void setCotrainer(BatchTrainer cotrainer) {
		this.cotrainer = cotrainer;
	}

	public String getSkillType() {
		return skillType;
	}

	public void setSkillType(String skillType) {
		this.skillType = skillType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((batchEndDate == null) ? 0 : batchEndDate.hashCode());
		result = prime * result + ((batchStartDate == null) ? 0 : batchStartDate.hashCode());
		result = prime * result + ((cotrainer == null) ? 0 : cotrainer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((skillType == null) ? 0 : skillType.hashCode());
		result = prime * result + ((trainer == null) ? 0 : trainer.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SalesforceBatch other = (SalesforceBatch) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (batchEndDate == null) {
			if (other.batchEndDate != null)
				return false;
		} else if (!batchEndDate.equals(other.batchEndDate))
			return false;
		if (batchStartDate == null) {
			if (other.batchStartDate != null)
				return false;
		} else if (!batchStartDate.equals(other.batchStartDate))
			return false;
		if (cotrainer == null) {
			if (other.cotrainer != null)
				return false;
		} else if (!cotrainer.equals(other.cotrainer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (skillType == null) {
			if (other.skillType != null)
				return false;
		} else if (!skillType.equals(other.skillType))
			return false;
		if (trainer == null) {
			if (other.trainer != null)
				return false;
		} else if (!trainer.equals(other.trainer))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SalesforceBatch [attributes=" + attributes + ", id=" + id + ", name=" + name + ", batchStartDate="
				+ batchStartDate + ", batchEndDate=" + batchEndDate + ", trainer=" + trainer + ", cotrainer="
				+ cotrainer + ", skillType=" + skillType + ", type=" + type + "]";
	}
	
}
