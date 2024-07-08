# Crypto Recommendation Service

This project provides a REST API for cryptocurrency recommendations, calculating various metrics based on cryptocurrency prices, and rate-limiting access to the endpoints.

## Tech Stack

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **H2 Database**: In-memory database used to store the cryptocurrency data.
- **Hazelcast**: Distributed in-memory data grid for caching. Used to store rate limiting data and jpa query results.
- **Bucket4j**: Java rate-limiting library integrated with Hazelcast.
- **Spring Integration**: For integration flows and file processing.
- **OpenCSV**
- **Lombok**
- **ModelMapper**
- **Maven**
- **Docker**

### Running with Docker
1. **Build the Docker Image:**
   ```sh
   docker build -t crypto-recommendation-service .
   ```

2. **Run the Docker Container:**
   ```sh
   docker run -d -p 8080:8080 --name crypto-recommendation-service crypto-recommendation-service
   ```

## Endpoints

### 1. Get Metrics for a Specific Cryptocurrency
- **URL:** `/api/v1/crypto/recommendations/metrics/{cryptoId}`
- **Method:** `GET`
- **Description:** Returns metrics for the specified cryptocurrency.
- **Path Variables:**
    - `cryptoId`: The ID of the cryptocurrency (e.g., `BTC`, `ETH`).
- **Response:**
  ```json
  {
    "cryptocurrency": "BTC",
    "oldestTimestamp": 1641009600000,
    "newestTimestamp": 1643659200000,
    "minPrice": 30000.00,
    "maxPrice": 60000.00
  }
  ```
  
- **Sample Requests:** <br/><br/>
  Status Code: `200 OK`  
  [Get Metrics for a Supported Cryptocurrency](http://localhost:8080/api/v1/crypto/recommendations/metrics/BTC) <br/><br/>
  Status Code: `400 Bad Request`  
  [Get Metrics for an Unsupported Cryptocurrency](http://localhost:8080/api/v1/crypto/recommendations/metrics/ADA) 

### 2. Get Metrics Sorted by Descending Normalized Range
- **URL:** `/api/v1/crypto/recommendations/metrics/normalizedRange/desc`
- **Method:** `GET`
- **Description:** Returns a list of all cryptocurrencies sorted by their normalized range in descending order.
  - **Response:**
    ```json
    [
        {
            "cryptocurrency": "ETH",
            "oldestTimestamp": 1641024000000,
            "newestTimestamp": 1643659200000,
            "minPrice": 2336.52,
            "maxPrice": 3828.11,
            "normalizedRange": 0.64
        },
        {
            "cryptocurrency": "XRP",
            "oldestTimestamp": 1640995200000,
            "newestTimestamp": 1643590800000,
            "minPrice": 0.56,
            "maxPrice": 0.85,
            "normalizedRange": 0.52
        }
    ]
    ```

- **Sample Requests:** <br/><br/>
  Status Code: `200 OK`  
  [Get Metrics Sorted by Descending Normalized Range](http://localhost:8080/api/v1/crypto/recommendations/metrics/normalizedRange/desc) <br/><br/>

### 3. Get Cryptocurrency with Highest Normalized Range for a Specific Date
- **URL:** `/api/v1/crypto/recommendations/metrics/highestNormalizedRange/date/{date}` 
- **Method:** `GET`
- **Description:** Returns the cryptocurrency with the highest normalized range for the specified date.
- **Path Variables:**
   - `date`: The date in `yyyy-MM-dd` format.
- **Response:**
   ```json
   {
      "cryptocurrency": "DOGE",
      "minPrice": 0.13,
      "maxPrice": 0.14,
      "normalizedRange": 0.08
    }
  ```

- **Sample Requests:** <br/><br/>
  Status Code: `200 OK`  
  [Get Cryptocurrency with Highest Normalized Range for a Specific Date - Date found](http://localhost:8080/api/v1/crypto/recommendations/metrics/highestNormalizedRange/date/2022-01-23) <br/><br/>
  Status Code: `404 Not Found`  
  [Get Cryptocurrency with Highest Normalized Range for a Specific Date - Date does not have a normalized range](http://localhost:8080/api/v1/crypto/recommendations/metrics/highestNormalizedRange/date/2024-01-23) <br/><br/>
  Status Code: `400 Bad Request`  
  [Get Cryptocurrency with Highest Normalized Range for a Specific Date - Wrong Date format](http://localhost:8080/api/v1/crypto/recommendations/metrics/highestNormalizedRange/date/twothousand24) <br/><br/>

## Rate Limiter

### Overview
The rate limiter is implemented to control the number of requests a client can make to the API within a specified time window. This helps to prevent abuse and ensures fair usage of the API resources.

### Configuration
The rate limiter configuration is specified in the `application.properties` file. The key parameters include:
- **maxTokens:** The maximum number of requests a client can make within the specified time period.
- **refilledTokens:** The number of tokens refilled at each interval.
- **refillPeriod:** The duration in minutes for the token bucket to refill.

### How It Works
- **IP Based Limiting:** Requests are rate-limited based on the client's IP address.
- **Rate Limit:** Each client can make a defined number of requests per minute.
- **Refill Policy:** The token bucket refills at a defined rate every minute.

### Response for Rate Limit Exceeded
If a client exceeds the request limit, they will receive a `429 Too Many Requests` response.

### Testing the Rate Limiter

You can use the following commands to test the rate limiter functionality with a recursive approach:

#### Using `curl` in Unix-based Systems

```sh
for i in {1..11}; do 
  curl -i -X GET "http://localhost:8080/api/v1/crypto/recommendations/metrics/BTC"
  echo ""
done
```

#### Using `curl` in PowerShell

```sh
  1..11 | ForEach-Object { curl "http://localhost:8080/api/v1/crypto/recommendations/metrics/BTC"; Start-Sleep -Seconds 1 }
```

### Things to consider - Answers

- **Initially the cryptos are only five, but what if we want to include more? Will the
  recommendation service be able to scale?** <br/><br/>

  Sure. If a new CSV file is detected in the specified path in the application.properties, it is going to be parsed.
  If the entries are of the correct format and the cryptocurrencies are supported, the values will be persisted 
  to the database, the metrics will be updated, and the relevant metrics will be evicted from the in-memory cache.
  <br/><br/>

- **New cryptos pop up every day, so we might need to safeguard recommendations service endpoints from not currently 
  supported cryptos** <br/><br/>

  The endpoint where the user may input a cryptocurrency to retrieve its metrics is protected by a validation. If the 
  client requests metrics for an unsupported cryptocurrency, a `400 Bad Request` error is going to be returned.
  <br/><br/>

- **For some cryptos it might be safe to invest, by just checking only one month's time frame. However, for some of them
  it might be more accurate to check six months or even a year. Will the recommendation service be able to handle this?**
  <br/><br/>

  Yes, the recommendation service will be able to handle this. Older entries may be persisted in the database by adding
  the respective data through one or more csv files to the directory of the Spring Integration listener.



