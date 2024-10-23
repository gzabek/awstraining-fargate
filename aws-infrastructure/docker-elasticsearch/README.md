# Push image to ECR
Run below commands to push our custom Elasticsearch image to ECR.
```
aws ecr get-login-password --region eu-central-1 --profile backend-test | docker login --username AWS --password-stdin 063814393886.dkr.ecr.eu-central-1.amazonaws.com
```

```
docker build -t elasticsearch-custom .
```

```
docker tag elasticsearch-custom 063814393886.dkr.ecr.eu-central-1.amazonaws.com/monitoring:elasticsearch
```

```
docker push 063814393886.dkr.ecr.eu-central-1.amazonaws.com/monitoring:elasticsearch
```