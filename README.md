[![Download](https://api.bintray.com/packages/kaiyan910/Pirate/pirate/images/download.svg?version=1.0.1)](https://bintray.com/kaiyan910/Pirate/pirate/1.0.1/link)

# Pirate

Pirate is a multi-module ```Activity``` collecting library.

The annotation processor will generate a class below:

``` java
public final class PirateTreasureMap implements TreasureMap {

  private static final Map<String, Treasure> ISLANDS;

  static {
    ISLANDS = new java.util.HashMap<>();
    ISLANDS.put("/main", new Treasure("/main", MainActivity.class, false));
    ISLANDS.putAll(SecretPirateMap.ISLANDS);
  }

  @Override
  @Nullable
  public Treasure sail(String key) {
    return ISLANDS.get(key);
  }

  @Override
  public Map<String, Treasure> islands() {
    return ISLANDS;
  }
}
```

## How to use

1. add dependencies
```groovy

// currently not available on JCenter yet please add
repositories {
    ...
    maven {
        url "https://dl.bintray.com/kaiyan910/Pirate"
    }
}

// Java
dependencies {
  ...
  implementation 'com.crookk.pirate:pirate:{latest-version}'
  annotationProcessor 'com.crookk.pirate:pirate-compiler:{latest-version}'
}

// Kotlin
dependencies {
  ...
  implementation 'com.crookk.pirate:pirate:{latest-version}'
  kapt 'com.crookk.pirate:pirate-compiler:{latest-version}'
}
```

2. config your sub-module gradle file by adding, you can ignore this step if you only have one module:
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

|   	| description                                 	| default 	| example 	|
|------	|---------------------------------------------	|---------	|---------	|
| key  	| the name/id/key of the activity            	| -       	| "/main" 	|
| auth 	| showing if the activity is protected or not 	| false   	| false   	|

```kotlin
@PirateIsland(key = "/main", auth = false)
class MainActivity : AppCompatActivity() {
    ...
}
```

4. mark your ```App``` class with ```@Pirate```, you should also pass in the all ```piratePackage``` to ```value```. You can ignore this step if you only have one module.
```kotlin
@Pirate(value = ["com.crookk.pirate2"])
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // let the pirates study the generated map
        Pirates.study(PirateTreasureMap())
    }
}
```

5. Let ```Pirates``` study the ```PirateTreasureMap```
```koltin
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // let the pirates study the generated map
        Pirates.study(PirateTreasureMap())
    }
}
```

6. call ```Pirates``` anywhere to ```sails()``` to the treasure islands.
```kotlin
Pirates.sails("/main")
```