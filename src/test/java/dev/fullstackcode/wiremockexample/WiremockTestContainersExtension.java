package dev.fullstackcode.wiremockexample;

import nl.thecheerfuldev.testcontainers.wiremock.WireMockContainer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;


@Testcontainers
public class WiremockTestContainersExtension  implements BeforeAllCallback, AfterAllCallback {
    static final WireMockContainer WIRE_MOCK_CONTAINER = new WireMockContainer("wiremock/wiremock:2.35.0-alpine")
            .withCopyFileToContainer(MountableFile.forClasspathResource("extensions"), "/var/wiremock/extensions")
            .withStubMappingForClasspathResource("stubs")//// loads all *.json files in resources/stubs/
            .withCommand("--verbose")
            .withCommand("--gloabl-response-templating")
            .withCommand("--extensions dev.fullstackcode.wiremock.transformer.ResponseTemplateTransformerExtension,com.ninecookies.wiremock.extensions.JsonBodyTransformer");

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        WIRE_MOCK_CONTAINER.stop();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        WIRE_MOCK_CONTAINER.start();
    }

}
