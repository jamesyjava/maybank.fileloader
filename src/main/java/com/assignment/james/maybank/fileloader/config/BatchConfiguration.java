package com.assignment.james.maybank.fileloader.config;

import com.assignment.james.maybank.fileloader.entity.Transaction;
import com.assignment.james.maybank.fileloader.notification.JobCompletionNotificationListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Transaction> reader() {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(new ClassPathResource("dataSource.txt"))
                .linesToSkip(1)
                .delimited()
                .delimiter("|")
                .names(new String[]{"account_number", "trx_amount", "description", "trx_date", "trx_time", "customer_id"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Transaction>() {{
                    setTargetType(Transaction.class);
                }})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO trx_data (account_number, trx_amount, description, trx_date, trx_time, customer_id) " +
                        "VALUES (:accountNumber, :trxAmount, :description, :trxDate, :trxTime, :customerId)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importDataJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importDataJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Transaction> writer) {
        return stepBuilderFactory.get("step1")
                .<Transaction, Transaction> chunk(5)
                .reader(reader())
                .writer(writer)
                .build();
    }
}