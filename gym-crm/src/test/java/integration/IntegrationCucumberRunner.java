package integration;

import io.cucumber.core.options.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/integration")
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value = "integration"
)
//@ConfigurationParameter(
//        key = Constants.FILTER_TAGS_PROPERTY_NAME,
//        value =  "@jms-delete"
//)
@ConfigurationParameter(
    key = Constants.PLUGIN_PROPERTY_NAME,
    value = "pretty, html:target/cucumber-reports/integration-report.html"
)
public class IntegrationCucumberRunner {
}