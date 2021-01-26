package ru.testtask.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import ru.testtask.model.BackgroundJobStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundJobDTO {
    private BackgroundJobStatus jobStatus;
    private int progress;
}
