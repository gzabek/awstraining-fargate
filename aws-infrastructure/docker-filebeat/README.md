# Push image to ECR
Run below commands to push our custom Filebeat image to ECR.
```
aws ecr get-login-password --region eu-central-1 --profile backend-test | docker login --username AWS --password-stdin 063814393886.dkr.ecr.eu-central-1.amazonaws.com
```

```
docker build -t filebeat-custom .
```

```
docker tag filebeat-custom 063814393886.dkr.ecr.eu-central-1.amazonaws.com/monitoring:filebeat
```

```
docker push 063814393886.dkr.ecr.eu-central-1.amazonaws.com/monitoring:filebeat
```