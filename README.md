# r2k

r2k = RSS to Kindle

*Work in progress...*

## Docker instructions

### Building and pushing the image to Docker Hub

```
docker image rm bodlulu/r2k:latest
DOCKER_USERNAME=<your docker hub login> DOCKER_PASSWORD=<your docker hub password> ./gradlew dockerPushImage
```

### Running the image

```
docker pull bodlulu/r2k
docker run -v /Users/bod/gitrepo/r2k/etc:/foo bodlulu/r2k -u xxx@yyy.com -p xxxxxx -k zzz@kindle.com -f xxx@yyy.com /foo/file.opml
```
