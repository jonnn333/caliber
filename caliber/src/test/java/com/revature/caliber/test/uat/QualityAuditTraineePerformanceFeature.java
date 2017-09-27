package com.revature.caliber.test.uat;

import org.openqa.selenium.WebDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class QualityAuditTraineePerformanceFeature {


	public QualityAuditPage qaPage;
	
	@cucumber.api.java.Before
	public void setup(){
		ChromeDriverSetup setup = new ChromeDriverSetup();
		qaPage = new QualityAuditPage(setup.getDriver());

	}
	
	@Given("^I am on the Quality Audit Page$")
	public void iAmOnTheQualityAuditPage(){
		qaPage.goToPage();
	    qaPage.verifyPage();
	}

	@Given("^I have selected the current year for year$")
	public void iHaveSelectedCurrentYear(){
		qaPage.clickYearDropdown("2017");
	    qaPage.verifyYear("2017");
	}
	
	@Given("^I have selected the current Batch$")
	public void iHaveSelectedTheCurrentBatch() {
		qaPage.clickBatch("Patrick Walsh - 2/14/17");
	    qaPage.verifyBatch("Patrick Walsh - 2/14/17");
	}

	@Given("^I am on the most current week$")
	public void iAmOnTheMostCurrentWeek() {
		qaPage.clickWeeksForBatch(7);
		qaPage.verifyWeekForBatch("week7");
	}

	@Given("^have entered \"([^\"]*)\" in Trainees note area$")
	public void haveEnteredInTraineesNoteArea(String arg1) {
	    qaPage.setNoteOnTraineeTextArea(arg1);
	}

	@Given("^I click on the individual feedback button to the desried state$")
	public void iClickOnTheIndividualFeedbackButtonToTheDesriedState() {
	    qaPage.clickIndividualFeedbackButton();
	}

	@When("^I click the save button at the bottom of the page$")
	public void iClickTheSaveButtonAtTheBottomOfThePage() {
	    qaPage.clickSaveButton();
	}

	@Then("^the performance notes will be saved$")
	public void thePerformanceNotesWillBeSaved() throws InterruptedException {
	    qaPage.goToPage();
	    qaPage.clickWeeksForBatch(7);
	    Thread.sleep(3000);
	    qaPage.verifyTraineeNotes();
	}
}
