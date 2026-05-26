import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class RunLocal {

    private static final Duration POSTGRES_READY_TIMEOUT = Duration.ofMinutes(2);
    private static final Duration BACKEND_READY_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration WEBSITE_READY_TIMEOUT = Duration.ofMinutes(3);
    private static final String DEFAULT_POSTGRES_IMAGE = "postgres:17-alpine";

    public static void main(String[] args) throws Exception {
        Config config = Config.parse(args);
        new Launcher(config).run();
    }

    private record Config(
            boolean withWebsite,
            boolean keepPostgres,
            String containerRuntimeOverride,
            int preferredDbPort,
            int preferredAppPort,
            int preferredWebPort,
            String postgresContainerName,
            String postgresImage,
            String databaseName,
            String databaseUser,
            String databasePassword,
            String bootstrapAdminUsername,
            String bootstrapAdminPassword
    ) {
        private static Config parse(String[] args) {
            boolean withWebsite = false;
            boolean keepPostgres = false;
            String containerRuntimeOverride = getenvOrBlank("DF_CONTAINER_RUNTIME");
            int preferredDbPort = parseIntEnv("DF_DB_PORT", 55433);
            int preferredAppPort = parseIntEnv("DF_APP_PORT", 8080);
            int preferredWebPort = parseIntEnv("DF_WEB_PORT", 3000);
            String postgresContainerName = getenvOrDefault("DF_POSTGRES_CONTAINER_NAME", "df-run-local-postgres");
            String postgresImage = getenvOrDefault("DF_POSTGRES_IMAGE", DEFAULT_POSTGRES_IMAGE);
            String databaseName = getenvOrDefault("DF_DB_NAME", "education_framework");
            String databaseUser = getenvOrDefault("DF_DB_USER", "education_framework");
            String databasePassword = getenvOrDefault("DF_DB_PASSWORD", "education_framework");
            String bootstrapAdminUsername = getenvOrDefault("DF_BOOTSTRAP_ADMIN_USERNAME", "bootstrap-admin");
            String bootstrapAdminPassword = getenvOrDefault("DF_BOOTSTRAP_ADMIN_PASSWORD", "BootstrapPassword!123");

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--help", "-h" -> {
                        printUsageAndExit();
                        return null;
                    }
                    case "--with-website" -> withWebsite = true;
                    case "--keep-postgres" -> keepPostgres = true;
                    case "--container-runtime" -> containerRuntimeOverride = requireNextValue(args, ++i, arg);
                    case "--db-port" -> preferredDbPort = parsePositiveInt(requireNextValue(args, ++i, arg), arg);
                    case "--app-port" -> preferredAppPort = parsePositiveInt(requireNextValue(args, ++i, arg), arg);
                    case "--web-port" -> preferredWebPort = parsePositiveInt(requireNextValue(args, ++i, arg), arg);
                    case "--postgres-container-name" -> postgresContainerName = requireNextValue(args, ++i, arg);
                    case "--postgres-image" -> postgresImage = requireNextValue(args, ++i, arg);
                    case "--db-name" -> databaseName = requireNextValue(args, ++i, arg);
                    case "--db-user" -> databaseUser = requireNextValue(args, ++i, arg);
                    case "--db-password" -> databasePassword = requireNextValue(args, ++i, arg);
                    case "--bootstrap-admin-username" -> bootstrapAdminUsername = requireNextValue(args, ++i, arg);
                    case "--bootstrap-admin-password" -> bootstrapAdminPassword = requireNextValue(args, ++i, arg);
                    default -> throw new IllegalArgumentException("Unknown argument: " + arg + "\nRun `java RunLocal.java --help` for usage.");
                }
            }

            return new Config(
                    withWebsite,
                    keepPostgres,
                    blankToNull(containerRuntimeOverride),
                    preferredDbPort,
                    preferredAppPort,
                    preferredWebPort,
                    postgresContainerName,
                    postgresImage,
                    databaseName,
                    databaseUser,
                    databasePassword,
                    bootstrapAdminUsername,
                    bootstrapAdminPassword
            );
        }

        private static String requireNextValue(String[] args, int index, String flagName) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for " + flagName + '.');
            }
            return args[index];
        }

        private static int parsePositiveInt(String value, String flagName) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed <= 0 || parsed > 65535) {
                    throw new IllegalArgumentException("Value for " + flagName + " must be between 1 and 65535.");
                }
                return parsed;
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Value for " + flagName + " must be a valid integer.", exception);
            }
        }

        private static int parseIntEnv(String envName, int defaultValue) {
            String raw = System.getenv(envName);
            if (raw == null || raw.isBlank()) {
                return defaultValue;
            }
            return parsePositiveInt(raw.trim(), envName);
        }

        private static String getenvOrDefault(String envName, String defaultValue) {
            String raw = System.getenv(envName);
            return raw == null || raw.isBlank() ? defaultValue : raw.trim();
        }

        private static String getenvOrBlank(String envName) {
            String raw = System.getenv(envName);
            return raw == null ? "" : raw.trim();
        }

        private static String blankToNull(String value) {
            return value == null || value.isBlank() ? null : value;
        }

        private static void printUsageAndExit() {
            String usage = """
                    Usage: java RunLocal.java [options]

                    Starts the local Education System Framework stack from one cross-platform file.

                    Options:
                      --with-website             Also try to start frontend/website.
                      --keep-postgres            Leave the PostgreSQL container running on exit.
                      --container-runtime NAME   Force docker or podman.
                      --db-port PORT             Preferred local PostgreSQL host port (default: 55433).
                      --app-port PORT            Preferred backend port (default: 8080).
                      --web-port PORT            Preferred website port (default: 3000).
                      --postgres-container-name  Override the local PostgreSQL container name.
                      --postgres-image IMAGE     Override the PostgreSQL image (default: postgres:17-alpine).
                      --db-name NAME             Override local database name.
                      --db-user USER             Override local database user.
                      --db-password PASSWORD     Override local database password.
                      --bootstrap-admin-username USER
                      --bootstrap-admin-password PASSWORD
                      --help, -h                 Show this help.

                    Environment-variable overrides are also supported for the defaults above through:
                      DF_CONTAINER_RUNTIME, DF_DB_PORT, DF_APP_PORT, DF_WEB_PORT,
                      DF_POSTGRES_CONTAINER_NAME, DF_POSTGRES_IMAGE,
                      DF_DB_NAME, DF_DB_USER, DF_DB_PASSWORD,
                      DF_BOOTSTRAP_ADMIN_USERNAME, DF_BOOTSTRAP_ADMIN_PASSWORD.
                    """;
            System.out.println(usage);
            System.exit(0);
        }
    }

    private static final class Launcher {
        private final Config config;
        private final Path repoRoot = Path.of("").toAbsolutePath().normalize();
        private final HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        private final boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");

        private Process backendProcess;
        private Process websiteProcess;
        private String containerRuntime;
        private int dbPort;
        private int appPort;
        private int webPort;
        private boolean postgresContainerCreated;

        private Launcher(Config config) {
            this.config = config;
        }

        private void run() throws Exception {
            validateRepoRoot();
            containerRuntime = resolveContainerRuntime();
            dbPort = chooseAvailablePort(config.preferredDbPort());
            appPort = chooseAvailablePort(config.preferredAppPort());
            webPort = chooseAvailablePort(config.preferredWebPort());

            installShutdownHook();
            printPlan();

            startPostgresContainer();
            waitForPostgresReadiness();
            startBackend();
            waitForHttpReady("backend health", URI.create("http://127.0.0.1:" + appPort + "/platform/status"), BACKEND_READY_TIMEOUT);
            waitForHttpReady("OpenAPI", URI.create("http://127.0.0.1:" + appPort + "/api-docs"), BACKEND_READY_TIMEOUT);

            boolean websiteStarted = false;
            if (config.withWebsite()) {
                websiteStarted = tryStartWebsite();
            }

            printSuccessBanner(websiteStarted);

            int backendExitCode = backendProcess.waitFor();
            throw new IllegalStateException("Backend process exited with code " + backendExitCode + ".");
        }

        private void validateRepoRoot() {
            requireFile(repoRoot.resolve("mvnw"), "Expected to run from the repository root containing `mvnw`.");
            requireFile(repoRoot.resolve("backend/platform-core/pom.xml"), "Expected `backend/platform-core/pom.xml` under the current working directory.");
            requireFile(repoRoot.resolve("frontend/website/package.json"), "Expected `frontend/website/package.json` under the current working directory.");
        }

        private void requireFile(Path path, String message) {
            if (!Files.exists(path)) {
                throw new IllegalStateException(message + " Missing path: " + path);
            }
        }

        private String resolveContainerRuntime() throws Exception {
            if (config.containerRuntimeOverride() != null) {
                verifyToolAvailable(config.containerRuntimeOverride());
                return config.containerRuntimeOverride();
            }
            for (String candidate : List.of("docker", "podman")) {
                if (isToolAvailable(candidate)) {
                    return candidate;
                }
            }
            throw new IllegalStateException("Neither docker nor podman is available on PATH. Install one of them or rerun with --container-runtime.");
        }

        private void verifyToolAvailable(String tool) throws Exception {
            if (!isToolAvailable(tool)) {
                throw new IllegalStateException("Requested container runtime `" + tool + "` is not available on PATH.");
            }
        }

        private boolean isToolAvailable(String tool) throws Exception {
            List<String> command = new ArrayList<>();
            command.add(toolExecutable(tool));
            command.add("--version");
            try {
                CommandResult result = runCommand(command, repoRoot, Map.of(), false, true);
                return result.exitCode() == 0;
            } catch (IOException exception) {
                return false;
            }
        }

        private String toolExecutable(String tool) {
            if (!windows) {
                return tool;
            }
            return switch (tool) {
                case "npm" -> "npm.cmd";
                default -> tool;
            };
        }

        private int chooseAvailablePort(int preferredPort) {
            int current = preferredPort;
            for (int attempt = 0; attempt < 50; attempt++) {
                if (isPortAvailable(current)) {
                    return current;
                }
                current++;
            }
            throw new IllegalStateException("Could not find a free port starting from " + preferredPort + '.');
        }

        private boolean isPortAvailable(int port) {
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.setReuseAddress(false);
                serverSocket.bind(new InetSocketAddress("127.0.0.1", port));
                return true;
            } catch (IOException exception) {
                return false;
            }
        }

        private void printPlan() {
            System.out.println("== Education System Framework local launcher ==");
            System.out.println("Repository root: " + repoRoot);
            System.out.println("Container runtime: " + containerRuntime);
            System.out.println("Database port: " + dbPort + (dbPort == config.preferredDbPort() ? "" : " (adjusted from preferred " + config.preferredDbPort() + ")"));
            System.out.println("Backend port: " + appPort + (appPort == config.preferredAppPort() ? "" : " (adjusted from preferred " + config.preferredAppPort() + ")"));
            System.out.println("Website port: " + webPort + (webPort == config.preferredWebPort() ? "" : " (adjusted from preferred " + config.preferredWebPort() + ")"));
            System.out.println("Website startup: " + (config.withWebsite() ? "enabled" : "disabled (use --with-website to enable)"));
            System.out.println();
        }

        private void startPostgresContainer() throws Exception {
            runCommand(List.of(containerRuntime, "rm", "-f", config.postgresContainerName()), repoRoot, Map.of(), false, true);

            CommandResult result = runCommand(
                    List.of(
                            containerRuntime,
                            "run",
                            "-d",
                            "--name", config.postgresContainerName(),
                            "-e", "POSTGRES_DB=" + config.databaseName(),
                            "-e", "POSTGRES_USER=" + config.databaseUser(),
                            "-e", "POSTGRES_PASSWORD=" + config.databasePassword(),
                            "-p", dbPort + ":5432",
                            config.postgresImage()
                    ),
                    repoRoot,
                    Map.of(),
                    false,
                    false
            );
            postgresContainerCreated = true;
            System.out.println("Started PostgreSQL container `" + config.postgresContainerName() + "`: " + oneLine(result.output()) + System.lineSeparator());
        }

        private void waitForPostgresReadiness() throws Exception {
            Instant deadline = Instant.now().plus(POSTGRES_READY_TIMEOUT);
            while (Instant.now().isBefore(deadline)) {
                CommandResult result = runCommand(
                        List.of(containerRuntime, "exec", config.postgresContainerName(), "pg_isready", "-U", config.databaseUser(), "-d", config.databaseName()),
                        repoRoot,
                        Map.of(),
                        false,
                        true
                );
                if (result.exitCode() == 0) {
                    System.out.println("PostgreSQL is ready." + System.lineSeparator());
                    return;
                }
                Thread.sleep(2_000);
            }
            throw new IllegalStateException("PostgreSQL container did not become ready within " + POSTGRES_READY_TIMEOUT.toSeconds() + " seconds.");
        }

        private void startBackend() throws Exception {
            Map<String, String> env = new LinkedHashMap<>();
            env.put("EDU_DB_URL", "jdbc:postgresql://localhost:" + dbPort + "/" + config.databaseName());
            env.put("EDU_DB_USERNAME", config.databaseUser());
            env.put("EDU_DB_PASSWORD", config.databasePassword());
            env.put("EDU_AUTH_JWT_SECRET", "df-local-jwt-secret-0123456789abcdef");
            env.put("EDU_AUTH_MFA_SECRET_ENCRYPTION_KEY", "df-local-mfa-secret-0123456789abcdef");
            env.put("EDU_AUTH_BOOTSTRAP_ADMIN_USERNAME", config.bootstrapAdminUsername());
            env.put("EDU_AUTH_BOOTSTRAP_ADMIN_PASSWORD", config.bootstrapAdminPassword());

            List<String> command = new ArrayList<>();
            if (windows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add(repoRoot.resolve("mvnw.cmd").toString());
            } else {
                command.add("sh");
                command.add(repoRoot.resolve("mvnw").toString());
            }
            command.add("-f");
            command.add("backend/platform-core/pom.xml");
            command.add("-Dspring-boot.run.arguments=--server.port=" + appPort);
            command.add("spring-boot:run");

            System.out.println("Starting backend..." + System.lineSeparator());
            backendProcess = startLongRunningProcess(command, repoRoot, env);
        }

        private boolean tryStartWebsite() throws Exception {
            if (!isToolAvailable("node") || !isToolAvailable("npm")) {
                System.out.println("Website startup requested, but node/npm are not available. Continuing with backend only." + System.lineSeparator());
                return false;
            }

            Path websiteDir = repoRoot.resolve("frontend/website");
            if (!Files.isDirectory(websiteDir)) {
                System.out.println("Website directory `frontend/website` is missing. Continuing with backend only." + System.lineSeparator());
                return false;
            }

            if (!Files.isDirectory(websiteDir.resolve("node_modules"))) {
                System.out.println("`frontend/website/node_modules` is missing. Running `npm install` first..." + System.lineSeparator());
                CommandResult installResult = runCommand(websiteCommand("install"), websiteDir, Map.of(), true, true);
                if (installResult.exitCode() != 0) {
                    System.out.println("Website dependency installation failed. Continuing with backend only." + System.lineSeparator());
                    return false;
                }
            }

            Map<String, String> env = Map.of("EDUCATION_API_BASE_URL", "http://127.0.0.1:" + appPort);
            System.out.println("Starting website..." + System.lineSeparator());
            websiteProcess = startLongRunningProcess(websiteCommand("run", "dev", "--", "--port", String.valueOf(webPort)), websiteDir, env);

            try {
                waitForHttpReady("website", URI.create("http://127.0.0.1:" + webPort + "/"), WEBSITE_READY_TIMEOUT);
                return true;
            } catch (Exception exception) {
                System.out.println("Website did not become ready in time. Backend remains available. Details: " + exception.getMessage() + System.lineSeparator());
                destroyProcess(websiteProcess, "website");
                websiteProcess = null;
                return false;
            }
        }

        private List<String> websiteCommand(String... args) {
            List<String> command = new ArrayList<>();
            if (windows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add("npm.cmd");
            } else {
                command.add("npm");
            }
            command.addAll(List.of(args));
            return command;
        }

        private void waitForHttpReady(String label, URI uri, Duration timeout) throws Exception {
            Instant deadline = Instant.now().plus(timeout);
            while (Instant.now().isBefore(deadline)) {
                if (backendProcess != null && !backendProcess.isAlive() && uri.getPort() == appPort) {
                    throw new IllegalStateException("Backend process exited before `" + label + "` became ready.");
                }
                if (websiteProcess != null && !websiteProcess.isAlive() && uri.getPort() == webPort) {
                    throw new IllegalStateException("Website process exited before `" + label + "` became ready.");
                }

                try {
                    HttpRequest request = HttpRequest.newBuilder(uri)
                            .timeout(Duration.ofSeconds(5))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                    if (response.statusCode() >= 200 && response.statusCode() < 500) {
                        System.out.println(label + " is ready at " + uri + System.lineSeparator());
                        return;
                    }
                } catch (IOException | InterruptedException ignored) {
                    // retry
                }
                Thread.sleep(2_000);
            }
            throw new IllegalStateException("Timed out waiting for " + label + " at " + uri + '.');
        }

        private void printSuccessBanner(boolean websiteStarted) {
            System.out.println("== Startup complete ==");
            System.out.println("Backend health: http://127.0.0.1:" + appPort + "/platform/status");
            System.out.println("Backend OpenAPI: http://127.0.0.1:" + appPort + "/api-docs");
            System.out.println("Backend Swagger UI: http://127.0.0.1:" + appPort + "/swagger-ui");
            System.out.println("Bootstrap-admin login endpoint: http://127.0.0.1:" + appPort + "/api/v1/identity/auth/login");
            if (websiteStarted) {
                System.out.println("Website: http://127.0.0.1:" + webPort + '/');
            } else if (config.withWebsite()) {
                System.out.println("Website: not started (see launcher output above).");
            } else {
                System.out.println("Website: disabled. Re-run with `java RunLocal.java --with-website` to try starting it.");
            }
            System.out.println();
            System.out.println("Press Ctrl+C to stop the launcher. If `--keep-postgres` is not set, the launcher will also remove the PostgreSQL container on exit.");
            System.out.println();
        }

        private void installShutdownHook() {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                destroyProcess(websiteProcess, "website");
                destroyProcess(backendProcess, "backend");
                if (postgresContainerCreated && !config.keepPostgres()) {
                    try {
                        runCommand(List.of(containerRuntime == null ? "docker" : containerRuntime, "rm", "-f", config.postgresContainerName()), repoRoot, Map.of(), false, true);
                    } catch (Exception ignored) {
                        // best effort cleanup
                    }
                }
            }, "run-local-shutdown"));
        }

        private Process startLongRunningProcess(List<String> command, Path workingDirectory, Map<String, String> environment) throws IOException {
            ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .directory(workingDirectory.toFile())
                    .inheritIO();
            processBuilder.environment().putAll(environment);
            return processBuilder.start();
        }

        private CommandResult runCommand(
                List<String> command,
                Path workingDirectory,
                Map<String, String> environment,
                boolean inheritIo,
                boolean allowFailure
        ) throws Exception {
            ProcessBuilder processBuilder = new ProcessBuilder(command)
                    .directory(workingDirectory.toFile());
            processBuilder.environment().putAll(environment);

            if (inheritIo) {
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                if (!allowFailure && exitCode != 0) {
                    throw new IllegalStateException("Command failed with exit code " + exitCode + ": " + command);
                }
                return new CommandResult(exitCode, "");
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            byte[] outputBytes = process.getInputStream().readAllBytes();
            int exitCode = process.waitFor();
            String output = new String(outputBytes, StandardCharsets.UTF_8).trim();
            if (!allowFailure && exitCode != 0) {
                throw new IllegalStateException("Command failed with exit code " + exitCode + ": " + command + System.lineSeparator() + output);
            }
            return new CommandResult(exitCode, output);
        }

        private String oneLine(String value) {
            if (value == null) {
                return "";
            }
            return value.replace(System.lineSeparator(), " ").trim();
        }

        private void destroyProcess(Process process, String label) {
            if (process == null || !process.isAlive()) {
                return;
            }
            process.destroy();
            try {
                if (!process.waitFor(10, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
            System.out.println("Stopped " + label + " process.");
        }
    }

    private record CommandResult(int exitCode, String output) {
    }
}


