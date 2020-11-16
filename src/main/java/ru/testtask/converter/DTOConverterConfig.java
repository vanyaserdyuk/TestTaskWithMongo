package ru.testtask.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.model.Project;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class DTOConverterConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        mapper.createTypeMap(Project.class, ProjectDTO.class).addMapping(Project::getAttributes, ProjectDTO::setAttrs);
        return mapper;
    }
}
