package com.fenixcommunity.centralspace.utilities.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.internal.util.Strings;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.PropertyType;

public class ModelMapperBuilder {
    private final ModelMapper modelMapper = new ModelMapper();

    public ModelMapperBuilder withUsingLombokBuilderForBothSide() {
        withUsingLombokBuilderForSource();
        withUsingLombokBuilderForDestination();
        return this;
    }

    public ModelMapperBuilder withUsingLombokBuilderForSource() {
        modelMapper.getConfiguration()
                .setSourceNamingConvention((propertyName, propertyType) -> PropertyType.METHOD.equals(propertyType))
                .setSourceNameTransformer((name, nameableType) -> Strings.decapitalize(name));
        return this;
    }

    public ModelMapperBuilder withUsingLombokBuilderForDestination() {
        modelMapper.getConfiguration()
                .setDestinationNamingConvention((propertyName, propertyType) -> PropertyType.METHOD.equals(propertyType))
                .setDestinationNameTransformer((name, nameableType) -> Strings.decapitalize(name));
        return this;
    }

    public ModelMapperBuilder withMatchingStrategy(final MatchingStrategy matchingStrategy) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(matchingStrategy);
        return this;
    }

    public ModelMapperBuilder withNameConvention(final NameTokenizer sourceNameConvention, final NameTokenizer destinationNameConvention) {
        modelMapper.getConfiguration()
                .setSourceNameTokenizer(sourceNameConvention)
                .setDestinationNameTokenizer(destinationNameConvention);
        return this;
    }

    // Determines which methods and fields are eligible for matching based on accessibility
    public ModelMapperBuilder withMethodAccessLevelToMapping(final AccessLevel accessLevelToMapping) {
        modelMapper.getConfiguration()
                .setMethodAccessLevel(accessLevelToMapping);
        return this;
    }

    public ModelMapper build() {
        return modelMapper;
    }
}
