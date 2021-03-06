package com.fenixcommunity.centralspace.utilities.resourcehelper;

import static com.fenixcommunity.centralspace.utilities.common.Var.SLASH;
import static com.fenixcommunity.centralspace.utilities.validator.ValidatorType.NOT_NULL;
import static com.google.common.base.Charsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import com.fenixcommunity.centralspace.utilities.configuration.properties.ResourceProperties;
import com.fenixcommunity.centralspace.utilities.validator.Validator;
import com.fenixcommunity.centralspace.utilities.validator.ValidatorFactory;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;


@Component
//todo @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) add to other
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class InternalResourceLoader {

    private final String resourcePath;
    private final ResourceLoader resourceLoader;
    private final ResourceProperties resourceProperties;
    private final Validator validator;

    InternalResourceLoader
            (final ResourceLoader resourceLoader, final ResourceProperties resourceProperties, final ValidatorFactory validatorFactory,
             @Value("${path.resource}") String resourcePath) {
        this.resourceLoader = resourceLoader;
        this.resourceProperties = resourceProperties;
        this.validator = validatorFactory.getInstance(NOT_NULL);
        this.resourcePath = resourcePath;
    }

    public Resource loadResourceFile(final InternalResource resource) {
        validator.validateWithException(resource);

        final String fileParentPath = resolveResourcePrefix(resource);
        final StringBuilder fullPath = new StringBuilder("classpath:static/")
                .append(fileParentPath)
                .append(SLASH)
                .append(resource.getFullName());
        return resourceLoader.getResource(fullPath.toString());
    }

    public Resource loadResourceByPath(final String filePath) {
        return resourceLoader.getResource("classpath:" + filePath);
    }

    public String loadResourceAsStringByPath(final String filePath) {
        final Resource resource = loadResourceByPath(filePath);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return null;
        }
    }

    public ResourceProperties getResourceProperties() {
        return resourceProperties;
    }

    public String getAbsoluteImagePath() {
        return resourcePath + resourceProperties.getImageUrl();
    }

    private String resolveResourcePrefix(final InternalResource resource) {
        final var fileFormat = resource.getFileFormat();
        return fileFormat.getSubtype();
    }
//todo File Upload Progress -> baeldung
}
