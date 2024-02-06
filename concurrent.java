@Service
public class YourSchedulerService {

    @Autowired
    private PrimaryDatabaseRepository primaryRepository;

    @Autowired
    private SecondaryDatabaseRepository secondaryRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    public void scheduleTask() {
        // Use ExecutorService to invoke stored procedures concurrently
        executorService.submit(() -> primaryRepository.invokeStoredProcedure());
        executorService.submit(() -> secondaryRepository.invokeStoredProcedure());
    }
}


In this example, two tasks are submitted to the ExecutorService concurrently, allowing the stored procedures to be invoked simultaneously. The ExecutorService is created with a fixed pool size of 2 to handle both tasks concurrently. Make sure to properly manage the lifecycle of the ExecutorService to avoid memory leaks.

Keep in mind that this approach assumes that the stored procedures can be invoked independently, and there are no dependencies between them. If there are dependencies, you might need to reconsider the design or explore other synchronization mechanisms.
