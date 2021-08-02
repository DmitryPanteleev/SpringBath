package ru.homework.dpanteleev.springbath.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.lang.NonNull;
import ru.homework.dpanteleev.springbath.model.Beer;
import ru.homework.dpanteleev.springbath.service.AddBeerService;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;


@Configuration
public class JobConfig {
    private static final int CHUNK_SIZE = 5;
    private final Logger logger = LoggerFactory.getLogger("Batch");

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    MongoTemplate mongoTemplate;

    @StepScope
    @Bean
    public MongoItemReader<Beer> reader() {
        return new MongoItemReaderBuilder<Beer>()
                .name("reader")
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(Beer.class)
                .sorts(new HashMap())
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Beer, Beer> processor(AddBeerService addBeerService) {
        return addBeerService::addBeer;
    }

    @StepScope
    @Bean
    public JpaItemWriter<Beer> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Beer>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job importUserJob(Step step, Step cleanUpStep) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(@NonNull JobExecution jobExecution) {
                        logger.info("Начало job");
                    }

                    @Override
                    public void afterJob(@NonNull JobExecution jobExecution) {
                        logger.info("Конец job");
                    }
                })
                .build();
    }

    @Bean
    public Step step(MongoItemReader<Beer> reader, JpaItemWriter<Beer> writer,
                     ItemProcessor<Beer, Beer> processor) {
        return stepBuilderFactory.get("step1")
                .<Beer, Beer>chunk(CHUNK_SIZE)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(new ItemReadListener<>() {
                    public void beforeRead() {
                        logger.info("Начало чтения");
                    }

                    public void afterRead(@NonNull Beer o) {
                        logger.info("Конец чтения");
                    }

                    public void onReadError(@NonNull Exception e) {
                        logger.info("Ошибка чтения");
                    }
                })
                .listener(new ItemWriteListener<>() {
                    public void beforeWrite(@NonNull List list) {
                        logger.info("Начало записи");
                    }

                    public void afterWrite(@NonNull List list) {
                        logger.info("Конец записи");
                    }

                    public void onWriteError(@NonNull Exception e, @NonNull List list) {
                        logger.info("Ошибка записи");
                    }
                })
                .listener(new ItemProcessListener<>() {
                    public void beforeProcess(Beer o) {
                        logger.info("Начало обработки");
                    }

                    public void afterProcess(@NonNull Beer o, Beer o2) {
                        logger.info("Конец обработки");
                    }

                    public void onProcessError(@NonNull Beer o, @NonNull Exception e) {
                        logger.info("Ошибка обработки");
                    }
                })
                .listener(new ChunkListener() {
                    public void beforeChunk(@NonNull ChunkContext chunkContext) {
                        logger.info("Начало пачки");
                    }

                    public void afterChunk(@NonNull ChunkContext chunkContext) {
                        logger.info("Конец пачки");
                    }

                    public void afterChunkError(@NonNull ChunkContext chunkContext) {
                        logger.info("Ошибка пачки");
                    }
                })
//                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

}
