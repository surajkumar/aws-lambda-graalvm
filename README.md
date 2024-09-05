# AWS Lambda using GraalVM
This repository contains a sample Hello,World Lambda function that is built using Java and compiled to a native using GraalVM. 

## How it works
When using native images, it's important to compile using the same operating system as the one where the system will run on.
To achieve this, we use Docker to build our image based on the Amazon Linux 2023 operating system. 

In each Lambda we have the following files:

1. Dockerfile: The image that we use to build our Java application
2. docker-compose: Used to trigger the Docker to build the program and set up the volume to fetch the created binary locally
3. Makefile: Needed by CloudFormation to know how to build our native image
4. run-build.sh: Used by Docker to run the native commands and create an artifact
5. **Important** src/main/resources/META-INF.native-image: This is required by GraalVM to understand our usage of reflection. Without it anything dynamic such as the lambda handler will not be found on the classpath.

These files **must** exist in each Lambda otherwise everything will fail.

When using SAM to build the program, you must use `sam build --build-on-source` this is because SAM uses Docker under the hood
and in multi gradle module situations, SAM will lose context of the entire project. In our example, we have a `:libs` module that contains the main method
used to run the Lambda. Without `--build-on-source` CloudFormation treats the Lambda module as the parent.