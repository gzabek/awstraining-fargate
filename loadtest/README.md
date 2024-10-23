## Backend load tests

# Structure

    .
    ├── ...
    ├── src          
    │   ├──test      
    │       ├──resources    # Resources used to configure gatling load tests
    │       │    ├──scripts # script perform the test
    │       └── scala      # Contains encrypted ansible vaults and path variables
    ├── Dockerfile          # docker file to create docker image
    └──  pom.xml             # maven configuration with gatling plugin
    
   
# Start load tests locally

**Prerequisites**

1. Configure load test in `buildprofiles/NONE-CI-config.properties`
    
**Start load test**

1. Go to `loadtest` directory
2. Run: `mvn test -P NONE-CI`  
    
# Start load tests on aws

**Prerequisites**

1. Check if the load test infrastructure is created - if not, run terraform scripts

```
./w2.sh [PROFILE] [REGION] common/services/ecs-loadtests apply
```

2. Build load test image (in ```loadtest```) directory
```
mvn clean install
```

3. Dockerize load test app and push to ECR

```
aws ecr get-login-password --region eu-central-1 --profile [PROFILE] | docker login --username AWS --password-stdin 063814393886.dkr.ecr.eu-central-1.amazonaws.com
```

```
docker build -t backend-loadtest .
```

```
docker tag backend-loadtest:latest 063814393886.dkr.ecr.eu-central-1.amazonaws.com/backend-loadtest:latest
```

```
docker push 063814393886.dkr.ecr.eu-central-1.amazonaws.com/backend-loadtest:latest
```

4. Force new service deployment in backend-loadtest cluster

If needed you can adjust task definition to set up specific load test parameters.

 
   