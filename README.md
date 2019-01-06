# Pirate

Pirate is a multi-module ```Activity``` collecting library.

The annotation processor will generate a class below:

``` java

public final class Pirates implements PirateRoad {

  private static final Map<String, PirateTreasure> ISLANDS;

  static {
    ISLANDS = new java.util.HashMap<>();
    ISLANDS.put("/main", new com.crookk.pirate.PirateTreasure("/main", com.crookk.pirate.MainActivity.class, false));
    ISLANDS.putAll(com.crookk.pirate2.SecretPirates.ISLANDS);
  }

  @Override
  @Nullable
  public PirateTreasure sail(String key) {
    return ISLANDS.get(key);
  }

  @Override
  public Map<String, PirateTreasure> islands() {
    return ISLANDS;
  }
}
```

## How to use

1. add dependencies
```groovy
// Java
dependencies {
  ...
  implementation 'com.crookk.pirate:pirate:1.0.0'
  annotationProcessor 'com.crookk.pirate:pirate-compiler:1.0.0'
}

// Kotlin
dependencies {
  ...
  implementation 'com.crookk.pirate:pirate:1.0.0'
  kapt 'com.crookk.pirate:pirate-compiler:1.0.0'
}
```

2. config your sub-module gradle file by adding:
```groovy

// Java
android {
    ...
    defaultConfig {
        ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = [ piratePackage : '{replaceWithSubModulePackageName}', pirateModule : true ]
            }
        }
    }
    ...
}

// Kotlin
kapt {
    arguments {
        arg("piratePackage", "{replaceWithSubModulePackageName}")
        arg("pirateModule", true)
    }
}
```

3. mark your ```Activity``` classes with ```@PirateIsland```

```kotlin
@PirateIsland(key = "/main", auth = false)
class MainActivity : AppCompatActivity() {
    ...
}
```

4. mark your ```App``` class with ```@Pirate```, you should also pass in the all ```piratePackage``` to ```value```
```kotlin
@Pirate(value = ["com.crookk.pirate2"])
class App : Application() {}
```