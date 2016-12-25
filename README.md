Encryption
===============

## Why
* An utility Framework for Encryption.
* Simple configuration file to configure/ boot strap the system.

    
## How
### Maven Dependency
```xml
    <dependency>
        <groupId>com.uimirror.framework</groupId>
        <artifactId>encryption</artifactId>
        <version>1.0.0</version>
    </dependency>
```

### Gradle Dependency

`com.uimirror.framework:encryption:1.0.0`


### Features

- Supports json or yml based configuration
- Key validation
- Multiple key versions
- Multiple key families for a database
- Supports all algorithms in javax.crypto
- Configurable JCE provider (default being BouncyCastle)


#### Sample code:

A Sample Json config to the application will look like:

```json
{
  "keys": [
    {
      "key_alias": "common.default",
      "fixed_salt": "1gru7ulzhqfct3ua",
      "current_version": "001",
      "keys": [
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES/CBC/PKCS5Padding",
          "version": "001",
          "fixed_salt": "1gru7ulzhqfct3ua"
        },
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES",
          "version": "002",
          "fixed_salt": "1gru7ulzhqfct3ua"
        }
      ]
    },
    {
      "key_alias": "userlookup.default",
      "fixed_salt": "1gru7ulzhqfct3ua",
      "initial_vector_length": 16,
      "current_version": "001",
      "keys": [
        {
          "key": "1boWH6iy49uGutMj9tQMtg==",
          "cipher_name": "AES",
          "version": "001",
          "fixed_salt": "1gru7ulzhqfct3ua"
        },
        {
          "key": "1boWH6iy49uGutMj9tQMtg==",
          "cipher_name": "AES",
          "version": "002",
          "fixed_salt": "1gru7ulzhqfct3ua"
        }
      ]
    },
    {
      "key_alias": "static.default",
      "fixed_salt": "1gru7ulzhqfct3ua",
      "initial_vector_length": 16,
      "current_version": "001",
      "keys": [
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES",
          "version": "001",
          "fixed_salt": "1gru7ulzhqfct3ua"
        },
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES",
          "version": "002",
          "fixed_salt": "1gru7ulzhqfct3ua"
        }
      ]
    },
    {
      "key_alias": "user.default.shard1",
      "fixed_salt": "1gru7ulzhqfct3ua",
      "initial_vector_length": 16,
      "current_version": "001",
      "keys": [
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES",
          "version": "001",
          "fixed_salt": "1gru7ulzhqfct3ua"
        },
        {
          "key": "Ty66chaeN1BVwSnvGzb6lg==",
          "cipher_name": "AES",
          "version": "002",
          "fixed_salt": "1gru7ulzhqfct3ua"
        }
      ]
    }
  ]
}
```

```java
//Create an object of DefaultLocalEncryptionServiceImpl and use that when needed.

public class LocalEncryptionServiceTest {

    private static LocalEncryptionService localEncryptionService;

    @BeforeClass
    public static void setUp() throws Exception {
        localEncryptionService = new DefaultLocalEncryptionServiceImpl();
        localEncryptionService.setConfigLoc("classpath:/sample_key_info.json");
        localEncryptionService.init();
    }

    @Test
    public void testEncryption() throws Exception {
        String clearValue = "HelloWorld";
        byte[] encryptByteArray = localEncryptionService.encryptByteArray("common.key_family_1.shard1", clearValue.getBytes());
        byte[] decryptByteArray = localEncryptionService.decryptByteArray("common.key_family_1.shard1", encryptByteArray);
        assertThat(new String(decryptByteArray, "UTF-8")).isEqualTo(clearValue);
    }

}

//When Resource entity requires client information, please extend ClientMeta which will make sure default things are getting inject such as ip, user agent etc

 

```

#### Developer Guide
* Make Sure you are on Maven 3.1 +
* Clone the repo
* Navigate to main directory and run `mvn clean install`
 
Navigate to code coverage folder to see current coverage status

##### Simple Steps for a complete release process
* ```git clone```
* ```mvn versions:set```
* ```mvn deploy```
* ```mvn scm:tag```

* `mvn source:jar` For Generating Source
* `mvn javadoc:jar` For generating Java doc

*** Please note before any push or merge request you run the test cases at least once.
