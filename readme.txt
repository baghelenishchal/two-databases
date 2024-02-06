Configure Multiple Data Sources:
Update your application properties (application.properties or application.yml) to include configurations for both databases.
Define two DataSource beans in your configuration class, each pointing to a different database.
Configure two EntityManagerFactory beans, each associated with a different DataSource.
java

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource dataSourceSecondary() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("primaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.yourpackage.model.primary")
                .persistenceUnit("primary")
                .build();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecondary(EntityManagerFactoryBuilder builder,
                                                                               @Qualifier("secondaryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.yourpackage.model.secondary")
                .persistenceUnit("secondary")
                .build();
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager transactionManagerSecondary(
            @Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
Invoke Stored Procedure:
Create two JPA repositories for each database, extending the JpaRepository interface.
Inject these repositories into your service or scheduler where you want to invoke the stored procedure.
java

@Repository
public interface PrimaryDatabaseRepository extends JpaRepository<YourEntity, Long> {

    @Procedure(name = "your_stored_procedure_name")
    void invokeStoredProcedure();
}

@Repository
public interface SecondaryDatabaseRepository extends JpaRepository<YourEntity, Long> {

    @Procedure(name = "your_stored_procedure_name")
    void invokeStoredProcedure();
}
Scheduler or Service:
In your scheduler or service class, inject both repositories and invoke the stored procedure using the appropriate repository and database connection.
java

@Service
public class YourSchedulerService {

    @Autowired
    private PrimaryDatabaseRepository primaryRepository;

    @Autowired
    private SecondaryDatabaseRepository secondaryRepository;

    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    public void scheduleTask() {
        // Invoke stored procedure for the first database connection
        primaryRepository.invokeStoredProcedure();

        // Invoke stored procedure for the second database connection
        secondaryRepository.invokeStoredProcedure();
    }
}
By following these steps, you can accommodate the new database connection and invoke the stored procedure without making many changes to your existing flow. This approach uses Spring Boot's support for multiple data sources and JPA repositories.
