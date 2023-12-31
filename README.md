# repos-browser-simplified

Simplified version of [repos-browser](https://github.com/AlmostRenoir/repos-browser), a service to browse GitHub repositories.

## Technologies

- Application is written with **Java 17**, **Maven** and **Spring Boot 3**. 
- The solution is based on a reactive approach with **WebFlux**.
- **WireMock** and **WebTestClient** are used for integration tests.

## Running the application locally

The easiest way is to execute the `main` method in the `almostrenoir.reposbrowser.ReposbrowserApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html):

```shell
mvn spring-boot:run
```

## API

#### **GET** */git-repos/by-user/{username}/no-forks*

**Response:**

*application/json*

```json
[{
  "name": "string",
  "owner": "string",
  "branches": [
    {
      "name": "string",
      "lastCommitSHA": "string"
    }
  ]
}]
```

**Statuses:**

- 200 - OK
- 400 - Invalid username
- 404 - User with given username not exist
- 406 - Not acceptable content type

**Notes:**

This route skips pagination, so only the first pages of repositories and branches are returned.
Version which returns all records is available [here](https://github.com/AlmostRenoir/repos-browser).

## License

[MIT](https://choosealicense.com/licenses/mit/)