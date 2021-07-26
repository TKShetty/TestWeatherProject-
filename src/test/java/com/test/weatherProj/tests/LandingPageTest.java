package com.test.weatherProj.tests;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.test.weatherProj.api.StatusCode;
import com.test.weatherProj.applicationApi.WeatherAPI;
import com.test.weatherProj.pages.HomePage;
import com.test.weatherProj.pages.WeatherForecastPage;
import com.test.weatherProj.utils.DataLoader;
import com.test.weatherProj.utils.UtilityClass;

import io.restassured.response.Response;

public class LandingPageTest extends BaseTest {

	public WebDriver driver;
	SoftAssert softAssert = new SoftAssert();

	@BeforeMethod
	public void initialize() throws IOException {
		driver = getDriver();
	}

	@Test
	public void verifyTemperature() {
		String cityName = DataLoader.getInstance().getPropertyValue("CityName");
		float uiTemp;
		float apiTemp;
		HomePage homePage = new HomePage(driver);
		try {
			WeatherForecastPage wp = homePage.searchCity(cityName);
			uiTemp = Float.parseFloat(wp.getCurrentTemperature());
			Response response = WeatherAPI.getCityWeatherAPIResponse(cityName);
			assertStatusCode(response.statusCode(), StatusCode.CODE_200);
			apiTemp = Float.parseFloat(WeatherAPI.getCityWeatherTemp(response));
			compareTemperature(uiTemp,apiTemp);
			if(UtilityClass.getVariance(new float[] {uiTemp,apiTemp})>1) {
			 throw new Exception();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			softAssert.assertAll();
		}
	}

	private void compareTemperature(Float uiTemp, Float apiTemp) {
		softAssert.assertEquals(uiTemp, apiTemp, "API and UI Temperature are not equal.");
	}
}
