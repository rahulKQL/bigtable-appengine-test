SpringBoot Sample App To test Bigtable on GAE (Java 8)
============================

This is extension of Google's getting started samples for **Springboot on appengine 8**:
[https://github.com/rahulKQL/getting-started-java/tree/master/appengine-standard-java8/springboot-appengine-standard](https://github.com/rahulKQL/getting-started-java/tree/master/appengine-standard-java8/springboot-appengine-standard)


#### Test Bigtable connection:

    http://localhost:8080/table-present/{tableName}
    http://localhost:8080/table-present/{tableName}/{rowKey}

eg;

    http://localhost:8080/table-present/AppEngineTestTable
    http://localhost:8080/table-present/AppEngineTestTable/rowkey-89


## Setup

* Download and initialize the [Cloud SDK](https://cloud.google.com/sdk/)

    `gcloud init`

* Create an App Engine app within the current Google Cloud Project

    `gcloud app create`

## Maven
### Running locally

`mvn appengine:run`

To use vist: http://localhost:8080/

### Deploying

`mvn appengine:deploy`

To use vist:  https://YOUR-PROJECT-ID.appspot.com
