FROM ubuntu
RUN apt-get update
RUN echo "deb http://www.apache.org/dist/cassandra/debian 39x main" | tee -a /etc/apt/sources.list.d/cassandra.sources.list
RUN apt-get install -y curl
RUN curl https://www.apache.org/dist/cassandra/KEYS | apt-key add -
RUN apt-get update
RUN apt-get install -y gradle
RUN apt-get install -y git
RUN apt-get install -y openjdk-8-jdk
RUN apt-get install -y cassandra
RUN git clone https://github.com/paulkass/server_test_with_gradle.git
WORKDIR server_test_with_gradle

RUN gradle build -x test
EXPOSE 8000
# CMD cassandra -R && sleep 20 && cqlsh -u cassandra -p cassandra -f schema_definition.txt && gradle run

# Make sure that a cassandra DB is running on 127.0.0.1:9042 and has the DB specified in schema_definition.txt
CMD gradle run
