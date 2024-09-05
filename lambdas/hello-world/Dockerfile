FROM public.ecr.aws/lambda/provided:al2023

WORKDIR /

# Setup
RUN dnf update -y && dnf install -y \
    unzip \
    zip \
    tar \
    gcc \
    gcc-c++ \
    libstdc++-static \
    libtool \
    zlib-devel

# Install GraalVM
RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-22.0.2/graalvm-community-jdk-22.0.2_linux-x64_bin.tar.gz -o graalvm.tar.gz
RUN tar -xzf graalvm.tar.gz
ENV PATH="/graalvm-community-openjdk-22.0.2+9.1/bin:${PATH}"
RUN rm graalvm.tar.gz

# Copy project files into container
COPY ./run-build.sh /
COPY ./build/libs/fat.jar /

RUN chmod +x /run-build.sh

ENTRYPOINT ["/run-build.sh"]
