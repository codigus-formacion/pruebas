package es.codeurjc.test.cucumber;

import org.junit.runner.RunWith;

import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
  plugin = {"pretty"}, 
  features = { "classpath:es/codeurjc/test/cucumber" },
  glue = {"es.codeurjc.test.cucumber" })
public class CalculatorTest {}