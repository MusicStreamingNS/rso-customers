# RSO: Customers microservice

## Prerequisites

```bash
docker run -d --name pg-customers -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=customer -p 5432:5432 postgres:10.5
```
## Travis status
[![Build Status](https://travis-ci.org/cloud-computing-project/customers.svg?branch=master)](https://travis-ci.org/MusicStreamingNS/rso-customers)