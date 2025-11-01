# Building PhotonJockey

## Prerequisites

- **Java 21** (JDK 21 or higher)
- **Gradle** (wrapper included)

## Required Dependencies

### Custom Hue API Fork

This project requires a custom fork of the Yet Another Hue API library with PhotonJockey-specific enhancements.

**Dependency:** `io.github.zeroone3010:yetanotherhueapi:2.8.0-lb`

Since this custom fork is not available in Maven Central, you need to build and install it locally:

```bash
# Clone the fork repository
git clone https://github.com/Kakifrucht/yetanotherhueapi
cd yetanotherhueapi

# Build and install to local Maven repository
mvn install

# Return to PhotonJockey project
cd ../PhotonJockey
```

### Alternative: Using Public Version (Limited Functionality)

If the fork is not available, you can temporarily use the public version by modifying `build.gradle`:

```gradle
// Change this line:
implementation 'io.github.zeroone3010:yetanotherhueapi:2.8.0-lb'

// To this:
implementation 'io.github.zeroone3010:yetanotherhueapi:2.7.0'
```

**Note:** Using the public version may cause compilation errors or missing functionality, as the code is designed for the custom fork.

## Building the Project

Once the dependency is installed:

```bash
# Compile the code
./gradlew compileJava

# Run tests
./gradlew test

# Check code style with Checkstyle
./gradlew check

# Build complete application
./gradlew build

# Create distribution packages
./gradlew shadowJar              # Uber JAR
./gradlew bundleReleaseZip       # ZIP distribution
./gradlew jpackage               # Platform-specific installer
```

## Running the Application

```bash
# Run directly with Gradle
./gradlew runShadow

# Or run the compiled JAR
java -jar build/libs/PhotonJockey-<version>.jar
```

## Troubleshooting

### "Could not find io.github.zeroone3010:yetanotherhueapi:2.8.0-lb"

This means the custom fork is not in your local Maven repository. Follow the installation steps above.

### "Invalid source release: 21"

You need Java 21 or higher. Check your Java version:
```bash
java -version
```

If you have multiple Java versions, set JAVA_HOME:
```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

## Development

- **Code Style:** Google Java Style Guide
- **Editor Config:** `.editorconfig` provided
- **Style Checks:** Run `./gradlew checkstyleMain` before committing
- **Conventions:** See `docs/CODING_CONVENTIONS.md`
